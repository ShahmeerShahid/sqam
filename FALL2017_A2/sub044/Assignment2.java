import java.sql.*;

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
  Assignment2()
  {
    try {
        Class.forName("org.postgresql.Driver");
    } catch(ClassNotFoundException e) {
        e.printStackTrace();
        return;
    }
  }

  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password)
  {
    try {
        connection = DriverManager.getConnection(URL, username, password);
        sql = connection.createStatement();
        sql.executeUpdate("SET SEARCH_PATH TO a2;");
        sql.close();
    } catch(SQLException e) {
        e.printStackTrace();
        return false;
    }
    if (connection == null) {
        return false;
    } 
    return true;
  }

  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB()
  {
    try {
        connection.close();
    } catch (SQLException e) {
        return false;
    }
    return true;
  }

  public boolean insertStock(int id, int wid, double qty)
  {
    try {
        ps = connection.prepareStatement("INSERT INTO stock VALUES(?, ?, ?)");
        ps.setInt(1, id);
        ps.setInt(2, wid);
        ps.setDouble(3, qty);
        ps.executeUpdate();
        ps.close();
    } catch(SQLException e){
        return false;
    }
    return true;
  }

  public boolean updateStock(int id, int wid, double qty)
  {
    try {
        ps = connection.prepareStatement("UPDATE stock SET quantity = quantity + ? WHERE (pid, wid) = (?, ?)");
        ps.setDouble(1, qty);
        ps.setInt(2, id);
        ps.setDouble(3, wid);
        if(ps.executeUpdate() <= 0) return false;
        ps.close();
    } catch (SQLException e) {
        return false;
    }
    return true;
  }

  public int insertOrder(int cid, int pid, int wid, double qty, double price)
  {
    try {
        // Step 1: Perform the operation
        sql = connection.createStatement();
        sql.executeQuery("select nextval (pg_get_serial_sequence('orders','oid'));");
        String query = "INSERT INTO orders VALUES ((select currval (pg_get_serial_sequence('orders','oid')))," +
                        "?, ?, CURRENT_DATE, ?, ?, ?, 'O');";
        ps = connection.prepareStatement(query);
        ps.setInt(1, cid);
        ps.setInt(2, pid);
        ps.setInt(3, wid);
        ps.setDouble(4, qty);
        ps.setDouble(5, price);
        ps.executeUpdate();

        // Step 2: Get the results back using the result table
        rs = sql.executeQuery("select currval (pg_get_serial_sequence('orders','oid'))");
	rs.next();
        int result = rs.getInt("currval");
        ps.close();
        sql.close();
        rs.close();
        return result;
    } catch (SQLException e) {
        return -1;
    }
  }

  public boolean cancelOrder(int oid)
  {
    try {
        // step 1: test if a order with id oid exists
        ps = connection.prepareStatement("SELECT * FROM orders WHERE oid = ?");
        ps.setInt(1, oid);
        rs = ps.executeQuery();
        if(!rs.next()) return false; // no such entry exists
        rs.close();
        ps.close();

        ps = connection.prepareStatement("DELETE FROM orders WHERE oid = ?;");
        ps.setInt(1, oid);
        ps.executeUpdate();
        ps.close();
    } catch (SQLException e) {
        return false;
    }
    return true;
  }

  public boolean shipOrder(int oid)
  {
    try {
        connection.setAutoCommit(false);

        String query = "SELECT oid, stock.pid as pid, stock.wid as wid, stock.quantity as stockqty, orders.quantity as orderqty " +
                        "FROM stock JOIN orders ON stock.pid = orders.pid AND stock.wid = orders.shipwid " +
                        "WHERE orders.oid = ?;";
        ps = connection.prepareStatement(query);
        ps.setInt(1, oid);
        rs = ps.executeQuery();
        if(!rs.next()) return false;
        if(rs.getInt("stockqty") < rs.getInt("orderqty")) {
            ps.close();
            rs.close();
            return false;
        }
        updateStock(rs.getInt("pid"), rs.getInt("wid"), rs.getInt("stockqty") - rs.getInt("orderqty"));
        ps.close();
        rs.close();

        query = "UPDATE orders SET status = 'S' WHERE oid = ? ";
        ps = connection.prepareStatement(query);
        ps.setInt(1, oid);
        ps.executeUpdate();
        ps.close();
        rs.close();

        connection.commit();
        connection.setAutoCommit(true);
    } catch (SQLException e) {
        return false;
    }
    return true;
  }

  public String listStock(int pid)
  {
    try {
        // step 1: get the result table
        String query = "SELECT wid, quantity FROM stock WHERE pid = ? AND quantity > 0";
        ps = connection.prepareStatement(query);
        ps.setInt(1, pid);
        rs = ps.executeQuery();

        // step 2: generate the string
        String ret = "";
        while(rs.next())
            ret += Integer.toString(rs.getInt("wid")) + ":" + Integer.toString(rs.getInt("quantity")) + "#";
        if(ret.length() > 0 && ret.charAt(ret.length() - 1) == '#')
            ret = ret.substring(0, ret.length() - 1);

        // step 3: clean-up and return 
        rs.close();
        ps.close();
        return ret;
    } catch (SQLException e) {
        return "";
    }
  }

  public String listReferrals(int cid) {
    try {
        // step 1: get the result table
        String query = "SELECT cid, cname FROM customer WHERE cid IN (SELECT custref FROM referral WHERE custid = ?);";
        ps = connection.prepareStatement(query);
        ps.setInt(1, cid);
        rs = ps.executeQuery();

        // step 2: generate the string
        String ret = "";
        while(rs.next())
            ret += Integer.toString(rs.getInt("cid")) + ":" + rs.getString("cname") + "#";
        if(ret.length() > 0 && ret.charAt(ret.length() - 1) == '#')
            ret = ret.substring(0, ret.length() - 1);

        // step 3: clean-up and return
        rs.close();
        ps.close();
        return ret;
    } catch (SQLException e) {
        return "";
    }
  }

  public boolean updateDB()
  {
    try {
        connection.setAutoCommit(false);
        String query = "SELECT pid, SUM(price * quantity) AS sales FROM orders WHERE status='S' GROUP BY pid;";
        ps = connection.prepareStatement(query);
        rs = ps.executeQuery();
        sql = connection.createStatement();
        sql.executeUpdate("CREATE TABLE bestsellers (" +
                            "pid INTEGER, " +
                            "sales NUMERIC(10,2));");
        
        ps = connection.prepareStatement("INSERT INTO bestsellers VALUES (?, ?)");
        while(rs.next()) {
            if(rs.getInt("sales") > 10000) {
                ps.setInt(1, rs.getInt("pid"));
                ps.setInt(2, rs.getInt("sales"));
                ps.executeUpdate();
            }
        }

        sql.close();
        rs.close();
        ps.close();
        connection.commit();
        connection.setAutoCommit(true);
    } catch (SQLException e) {
        return false;
    }
    return true;
  }

}

