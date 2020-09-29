import java.sql.*;
import java.util.StringJoiner;

public class Assignment2 {

    // A connection to the database
    Connection connection;

    // Statement to run queries
    Statement sql;

    // Prepared Statement
    PreparedStatement ps;

    // Resultset for the query
    ResultSet rs;

    //CONSTRUCTOR
    public Assignment2() {
        // dynamically load driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Error: unable to load driver class!");
            System.exit(1);
        }
    }

    // Safely close a java.sql closeable (e.g. connection, statement, ...)
    // Return false if .close() failed or could not be called.
    private boolean close(AutoCloseable ac) {
        if (ac == null) {
            return false;
        }

        try {
            ac.close();
        } catch (SQLException e) {
            return false;
        } catch (Exception e) { // should never occur
            throw new RuntimeException(e);
        }

        return true;
    }

    // Close all opened resources and unset them (except for connection!)
    private void closeResources() {
        close(rs);
        close(ps);
        close(sql);

        // To speed up multiple closeResources() calls
        rs = null;
        ps = null;
        sql = null;
    }

    //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
    public boolean connectDB(String URL, String username, String password) {
        try {
            // close connection before opening
            disconnectDB();
            connection = DriverManager.getConnection(URL, username, password);

            // set search path
            sql = connection.createStatement();
            sql.executeUpdate("SET SEARCH_PATH TO A2");
            closeResources();

        } catch (SQLException e) {
            return false;

        } finally {
            closeResources();
        }

        return true;
    }

    //Closes the connection. Returns true if closure was sucessful
    public boolean disconnectDB() {
        boolean closed = close(connection);
        if (closed) {
            connection = null;
        }
        return closed;
    }

    public boolean insertStock(int id, int wid, double qty) {
        // check constraints that aren't part of the db
        if (qty < 0) {
            return false;
        }

        try {
            sql = connection.createStatement();
            sql.executeUpdate(String.format("" +
                            "INSERT INTO stock   \n" +
                            "VALUES (%d, %d, %f) ",
                    id, wid, qty));
            closeResources();

        } catch (SQLException e) {
            return false;

        } finally {
            closeResources();
        }

        return true;
    }

    public boolean updateStock(int id, int wid, double qty) {
        try {
            sql = connection.createStatement();
            sql.executeUpdate(String.format("" +
                            "UPDATE stock                    \n" +
                            "SET quantity = quantity + %1$f  \n" +
                            "WHERE pid = %2$d                \n" +
                            "      AND wid = %3$d            \n" +
                            "      AND quantity + %1$f >= 0  ",
                    qty, id, wid));

            // don't need to close before calling this b/c of finally{}
            return sql.getUpdateCount() > 0;

        } catch (SQLException e) {
            return false;

        } finally {
            closeResources();
        }
    }

    public int insertOrder(int cid, int pid, int wid, double qty, double price) {
        // check constraints that aren't part of the db
        if (qty <= 0) {
            return -1;
        }

        try {
            // insert order
            sql = connection.createStatement();
            sql.executeUpdate(String.format("" +
                            "INSERT INTO orders(cid, pid, odate, shipwid, quantity, price, status)  \n" +
                            "VALUES (%d, %d, now(), %d, %f, %f, 'O')                                ",
                    cid, pid, wid, qty, price));
            closeResources();

            // get latest order id
            sql = connection.createStatement();
            rs = sql.executeQuery("SELECT currval(pg_get_serial_sequence('orders','oid'))");

            if (!rs.next()) {
                return -1;
            }

            return rs.getInt(1);

        } catch (SQLException e) {
            return -1;

        } finally {
            closeResources();
        }
    }

    public boolean cancelOrder(int oid) {
        try {
            sql = connection.createStatement();
            sql.executeUpdate(String.format("" +
                            "DELETE FROM orders      \n" +
                            "WHERE oid = %d          \n" +
                            "      AND status = 'O'  ", oid));

            // 1 update => order successfully cancelled
            return sql.getUpdateCount() > 0;

        } catch (SQLException e) {
            return false;

        } finally {
            closeResources();
        }
    }

    public boolean shipOrder(int odi) {
        boolean success = true;

        try {
            // Keep this function atomic
            connection.setAutoCommit(false);

            // If this order can be made, set it to shipped
            sql = connection.createStatement();
            sql.executeUpdate(String.format("" +
                    "UPDATE orders o                     \n" +
                    "SET status = 'S'                    \n" +
                    "FROM stock s                        \n" +
                    "WHERE o.oid = %d                    \n" +
                    "      AND o.pid = s.pid             \n" +
                    "      AND o.shipwid = s.wid         \n" +
                    "      AND o.quantity <= s.quantity  \n" +
                    "      AND o.status = 'O'            ", odi));

            // If no updates, then the order cannot be made, so break out of try/catch
            if (sql.getUpdateCount() == 0) {
                throw new SQLException();
            }

            closeResources();

            // Update stock quantity. Now we know that order can be made, (quantity is small enough, and status is O),
            //   so we don't need to check constraints again.
            sql = connection.createStatement();
            sql.executeUpdate(String.format("" +
                    "UPDATE stock s                          \n" +
                    "SET quantity = s.quantity - o.quantity  \n" +
                    "FROM orders o                           \n" +
                    "WHERE o.oid = %d                        \n" +
                    "      AND o.pid = s.pid                 \n" +
                    "      AND o.shipwid = s.wid             ", odi));

            // Same check as before
            if (sql.getUpdateCount() == 0) {
                throw new SQLException();
            }

            closeResources();

            // Now we can commit!
            connection.commit();

        } catch (SQLException e) {
            success = false;

        } finally {
            closeResources();

            try {
                // roll back anything not committed in case exception kicked us out of try/catch.
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                // should not happen, and since update statement already succeeded, we won't return false
            }
        }


        return success;
    }

    public String listStock(int pid) {
        StringJoiner sj = new StringJoiner("#");

        try {
            sql = connection.createStatement();
            rs = sql.executeQuery(String.format("" +
                    "SELECT                        \n" +
                    "  wid,                        \n" +
                    "  quantity                    \n" +
                    "FROM stock s                  \n" +
                    "WHERE quantity > 0            \n" +
                    "      AND pid = %d            \n" +
                    "ORDER BY quantity DESC, pid;  ", pid));

            while (rs.next()) {
                sj.add(String.format("%d:%.2f", rs.getInt("wid"), rs.getDouble("quantity")));
            }

            closeResources();

        } catch (SQLException e) {
            return "";

        } finally {
            closeResources();
        }

        return sj.toString();
    }

    public String listReferrals(int cid) {
        StringJoiner sj = new StringJoiner("#");

        try {
            sql = connection.createStatement();
            rs = sql.executeQuery(String.format("" +
                    "SELECT                       \n" +
                    "  c.cid,                     \n" +
                    "  c.cname                    \n" +
                    "FROM referral r, customer c  \n" +
                    "WHERE r.custid = %d          \n" +
                    "      AND r.custref = c.cid  \n" +
                    "ORDER BY cname ASC, cid      ", cid));

            while (rs.next()) {
                sj.add(String.format("%d:%s", rs.getInt("cid"), rs.getString("cname")));
            }

            closeResources();

        } catch (SQLException e) {
            return "";

        } finally {
            closeResources();
        }

        return sj.toString();
    }

    public boolean updateDB() {
        try {
            // Drop table if it already exists
            sql = connection.createStatement();
            sql.executeUpdate("DROP TABLE IF EXISTS bestsellers");
            closeResources();

            // Create it
            sql = connection.createStatement();
            sql.executeUpdate("" +
                    "CREATE TABLE bestsellers(                                            \n" +
                    "    pid   INTEGER       REFERENCES product(pid) ON DELETE RESTRICT,  \n" +
                    "    sales NUMERIC(10,2) NOT NULL,                                    \n" +
                    "    CHECK (sales >= 0)                                               \n" +
                    ")                                                                    ");
            closeResources();

            // Insert all the values
            sql = connection.createStatement();
            sql.executeUpdate("" +
                    "INSERT INTO bestsellers(                \n" +
                    "    SELECT pid, SUM(price*quantity)     \n" +
                    "    FROM orders                         \n" +
                    "    WHERE status = 'S'                  \n" +
                    "    GROUP BY pid                        \n" +
                    "    HAVING SUM(price*quantity) > 10000  \n" +
                    ")                                       ");
            closeResources();

        } catch (SQLException e) {
            return false;

        } finally {
            closeResources();
        }

        return true;
    }


}

