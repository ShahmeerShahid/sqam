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

	// CONSTRUCTOR
	Assignment2() {
	}

	// Using the input parameters, establish a connection to be used for this
	// session.
	// Returns true if connection is sucessful
	public boolean connectDB(String URL, String username, String password) throws ClassNotFoundException {

		try {

			// Load JDBC driver
			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			return false;

		}
		try {

			connection = DriverManager.getConnection(URL, username, password);
			String checker = "SET search_path TO A2";
			ps = connection.prepareStatement(checker);
			ps.executeUpdate();
			ps.close();

			if (connection != null) {
				return true;
			} else {

				return false;
			}

		}

		catch (SQLException e) {

			return false;
		}

	}

	// Closes the connection. Returns true if closure was sucessful
	public boolean disconnectDB() {

		try {
			connection.close();
			return true;
		}

		catch (SQLException e) {
			return false;
		}
	}

	public boolean insertStock(int pid, int wid, double qty) throws SQLException {
		try {
			// checking if pid exists in product table

			String checker = "SELECT * FROM product WHERE pid = ?";
			ps = connection.prepareStatement(checker);
			ps.setInt(1, pid);
			rs = ps.executeQuery();

			if (!rs.isBeforeFirst()) {

				return false;
			}

			// checking if wid exists in warehouse table
			String checker2 = "SELECT * FROM warehouse WHERE wid = ?";
			ps = connection.prepareStatement(checker2);
			ps.setInt(1, wid);
			rs = ps.executeQuery();

			if (!rs.isBeforeFirst()) {

				return false;
			}

			String checker3 = "SELECT pid, wid  FROM stock WHERE pid = ? AND wid = ?";
			ps = connection.prepareStatement(checker3);
			ps.setInt(1, pid);
			ps.setInt(2, wid);
			rs = ps.executeQuery();

			if (rs.isBeforeFirst()) {
				return false;
			}

			// checking if qty is positive
			if (qty < 0) {

				return false;
			}
			// inserting row

			String sqlText = "INSERT INTO stock" + "(pid, wid, quantity) VALUES" + "(?, ?, ?)";
			ps = connection.prepareStatement(sqlText);

			ps.setInt(1, pid);
			ps.setInt(2, wid);
			ps.setDouble(3, qty);
			ps.executeUpdate();

			if (ps != null && rs != null) {
				rs.close();
				ps.close();
				return true;
			} else {

				return false;
			}
		}

		catch (SQLException e) {

			return false;
		}

	}

	public boolean updateStock(int pid, int wid, double qty) throws SQLException {
		try {
			// checking if pid exists in stock table
			String checker = "SELECT * FROM stock WHERE pid = ?";
			ps = connection.prepareStatement(checker);
			ps.setInt(1, pid);
			rs = ps.executeQuery();

			if (!rs.isBeforeFirst()) {

				return false;
			}
			// checking if wid exists in stock table

			String checker2 = "SELECT * FROM stock WHERE wid = ?";
			ps = connection.prepareStatement(checker2);
			ps.setInt(1, wid);
			rs = ps.executeQuery();
			// in case the qty is negative, the absolute value of qty must be
			// less or equal than quantity on the corresponding row
			String checker3 = "SELECT quantity FROM stock WHERE wid = ? AND pid = ?";
			ps = connection.prepareStatement(checker3);
			ps.setInt(1, wid);
			ps.setInt(2, pid);
			rs = ps.executeQuery(); // corresponding qty
			rs.next();

			if (qty < 0) {
				if (Math.abs(qty) > rs.getDouble("quantity")) {
					return false;
				}
			}

			qty = qty + rs.getDouble("quantity");
			// updating row
			String sqlText = "UPDATE stock " + "SET pid = ?, wid = ?, quantity = ?" + "WHERE pid = ? AND wid = ?";
			ps = connection.prepareStatement(sqlText);
			ps.setInt(1, pid);
			ps.setInt(2, wid);
			ps.setDouble(3, qty);
			ps.setInt(4, pid);
			ps.setInt(5, wid);
			ps.executeUpdate();

			if (ps != null && rs != null) {
				rs.close();
				ps.close();
				return true;
			} else {

				return false;
			}
		}

		catch (SQLException e) {

			return false;
		}

	}

	public int insertOrder(int cid, int pid, int wid, double qty, double price) throws SQLException {

		try {

			// checking if cid exists in customer table
			String checkcid = "SELECT * FROM customer WHERE cid = ?";
			ps = connection.prepareStatement(checkcid);
			ps.setInt(1, cid);
			rs = ps.executeQuery();

			if (!rs.isBeforeFirst()) {

				return -1;
			}

			// checking if pid exists in product table
			String checkpid = "SELECT * FROM product WHERE pid = ?";
			ps = connection.prepareStatement(checkpid);
			ps.setInt(1, pid);
			rs = ps.executeQuery();

			if (!rs.isBeforeFirst()) {

				return -1;
			}

			// checking if wid exists in warehouse table
			String checkwid = "SELECT * FROM warehouse WHERE wid = ?";
			ps = connection.prepareStatement(checkwid);
			ps.setInt(1, wid);
			rs = ps.executeQuery();

			if (!rs.isBeforeFirst()) {

				return -1;
			}

			// inserting row

			ps = connection.prepareStatement("select nextval('orders_oid_seq')");
			rs = ps.executeQuery();
			int nval = 0;
			if (rs != null) {
				while (rs.next()) {
					nval = rs.getInt("nextval");

				}
			}

			String sqlText = "INSERT INTO orders" + "(oid, cid, pid, odate, shipwid, quantity, price, status) VALUES"
					+ "(?, ?, ?, ?, ?, ?, ?, ?)";

			ps = connection.prepareStatement(sqlText);

			ps.setInt(1, nval);
			ps.setInt(2, cid);
			ps.setInt(3, pid);
			ps.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
			ps.setInt(5, wid);
			ps.setDouble(6, qty);
			ps.setDouble(7, price);
			ps.setString(8, "O");
			ps.executeUpdate();

			if (ps != null && rs != null) {
				rs.close();
				ps.close();
				return nval; // latest order number
			} else {

				return -1;
			}
		}

		catch (SQLException e) {

			return -1;
		}

	}

	public boolean cancelOrder(int oid) throws SQLException {

		try {
			String checkstatus = "SELECT * FROM orders WHERE oid = ? AND status = ?";
			ps = connection.prepareStatement(checkstatus);
			ps.setInt(1, oid);
			ps.setString(2, "O");
			rs = ps.executeQuery();

			if (!rs.isBeforeFirst()) { // checking if the oid has any rows with status 'O'

				return false;
			}
			rs.next();

			String removeoid = "DELETE FROM orders WHERE oid = ?";
			ps = connection.prepareStatement(removeoid);
			ps.setInt(1, oid);
			ps.executeUpdate();// removed row with status 'O' and id oid

			if (ps != null && rs != null) {
				rs.close();
				ps.close();
				return true; // latest order number
			} else {

				return false;
			}
		} catch (SQLException e) {

			return false;
		}
	}

	public boolean shipOrder(int oid) {
		try {

			String check = "SELECT o.pid AS pid, o.shipwid AS wid, o.quantity AS qo, w.quantity AS qw "
					+ "FROM orders o JOIN stock w ON o.shipwid = w.wid "
					+ "WHERE o.oid = ? AND o.status = 'O' AND o.quantity <= w.quantity AND o.pid = w.pid";
			ps = connection.prepareStatement(check);
			ps.setInt(1, oid);
			rs = ps.executeQuery();

			if (!rs.isBeforeFirst()) {
				return false;
			}
			rs.next();
			updateStock(rs.getInt("pid"), rs.getInt("wid"), (-1 * rs.getDouble("qo")));

			String sqlText = "UPDATE orders " + "SET status = 'S' " + "WHERE oid = ?";
			ps = connection.prepareStatement(sqlText);
			ps.setInt(1, oid);
			ps.executeUpdate();

			if (ps != null && rs != null) {
				rs.close();
				ps.close();
				return true;
			} else {

				return false;
			}

		} catch (SQLException e) {

			return false;

		}
	}

	public String listStock(int pid) {
		try {
			// checking if pid exists in Stock table
			String checkpid = "SELECT pid FROM stock WHERE pid = ?";
			ps = connection.prepareStatement(checkpid);
			ps.setInt(1, pid);
			rs = ps.executeQuery();

			if (!rs.isBeforeFirst()) {
				return "";
			}

			String sqlquery = "SELECT wid, quantity FROM stock WHERE pid = ? AND quantity > 0";
			ps = connection.prepareStatement(sqlquery);
			ps.setInt(1, pid);
			rs = ps.executeQuery();

			if (!rs.isBeforeFirst()) {
				return "";
			}
			String list = "";
			while (rs.next()) {
				list += rs.getInt(1) + ":" + rs.getString(2) + "#";
			}

			if (ps != null && rs != null) {
				rs.close();
				ps.close();
				return list.substring(0, list.length() - 1); // list of stocks
			} else {
				return "";
			}
		} catch (SQLException e) {
			return "";

		}
	}

	public String listReferrals(int cid) {
		try {
			// checking if cid exists in referral table
			String checkcid = "SELECT custref FROM referral WHERE custid = ?";
			ps = connection.prepareStatement(checkcid);
			ps.setInt(1, cid);
			rs = ps.executeQuery();

			if (!rs.isBeforeFirst()) {
				return "";
			}

			String sqlquery = "SELECT r.cid, r.cname FROM customer c JOIN referral ON c.cid = custid JOIN customer r ON r.cid = custref WHERE custid = ? ORDER BY c.cname";
			ps = connection.prepareStatement(sqlquery);
			ps.setInt(1, cid);
			rs = ps.executeQuery();

			if (!rs.isBeforeFirst()) {
				return "";
			}
			String list = "";
			while (rs.next()) {
				list += rs.getInt(1) + ":" + rs.getString(2) + "#";
			}

			if (ps != null && rs != null) {
				rs.close();
				ps.close();
				return list.substring(0, list.length() - 1); // list of referrals
			} else {
				return "";
			}
		} catch (SQLException e) {
			return "";

		}

	}

	public boolean updateDB() throws SQLException {
		try {

			String sql1 = "DROP TABLE IF EXISTS bestsellers";

			String sql2 = "CREATE TABLE IF NOT EXISTS bestsellers (pid INTEGER, sales NUMERIC(10,2))";

			ps = connection.prepareStatement(sql1);
			ps.executeUpdate();

			ps = connection.prepareStatement(sql2);
			ps.executeUpdate();

			String sqltext0 = "DROP VIEW IF EXISTS best";
			ps = connection.prepareStatement(sqltext0);
			ps.executeUpdate();

			String sqltext1 = "CREATE VIEW best AS SELECT p.pid as pid, ROUND(SUM(o.price*o.quantity), 2) as sales FROM orders o JOIN product p ON o.pid = p.pid WHERE o.status = 'S' GROUP BY p.pid";
			String sqltext2 = "INSERT INTO bestsellers(SELECT pid, sales FROM best WHERE sales > ROUND(10000,2))";
			String sqltext3 = "DROP VIEW best";
			ps = connection.prepareStatement(sqltext1);
			ps.executeUpdate();

			ps = connection.prepareStatement(sqltext2);
			ps.executeUpdate();

			ps = connection.prepareStatement(sqltext3);
			ps.executeUpdate();

			rs.close();
			ps.close();
			return true;

		} catch (SQLException e) {

			return false;
		}
	}
}
