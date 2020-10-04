import java.sql.*;

public class Assignment2 {

	// A connection to the database
	Connection conn;

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
	// session. Returns true if connection is sucessful
	public boolean connectDB(String URL, String username, String password) {
		try {
			// Load JDBC driver
			Class.forName("org.postgresql.Driver");

		}

		catch (ClassNotFoundException e) {
			return false;
		}

		try {
			conn = DriverManager.getConnection(URL, username, password);

			String s = "SET search_path to A2";

			sql = conn.createStatement();
			sql.executeUpdate(s);
		} catch (SQLException se) {
			return false;
		}

		return true;
	}

	// Closes the connection. Returns true if closure was sucessful
	public boolean disconnectDB() {
		try {
			conn.close();
		}

		catch (SQLException se) {
			return false;
		}

		return true;
	}

	public boolean insertStock(int id, int wid, double qty) {
		if (qty < 0) {
			return false;
		}

		try {
			String s = "INSERT INTO stock (pid,wid,quantity)" + "VALUES (" + id
					+ "," + wid + "," + qty + ")";
			sql = conn.createStatement();
			sql.executeUpdate(s);
		}

		catch (SQLException se) {
			return false;
		}

		return true;
	}

	public boolean updateStock(int id, int wid, double qty) {
		try {
			int change = 1;

			if (qty >= 0) {
				String s = "UPDATE stock SET quantity = quantity + " + qty
						+ " WHERE " + "pid = " + id + " AND" + " wid = " + wid;
				sql = conn.createStatement();
				change = sql.executeUpdate(s);
			}

			else {
				String s = "UPDATE stock SET quantity = quantity - "
						+ Math.abs(qty) + " WHERE " + " pid = " + id + " AND"
						+ " wid = " + wid + " AND " + Math.abs(qty)
						+ " <= quantity";
				sql = conn.createStatement();
				change = sql.executeUpdate(s);
			}

			if (change != 1) {
				return false;
			}
		}

		catch (SQLException se) {
			return false;
		}

		return true;
	}

	public int insertOrder(int cid, int pid, int wid, double qty, double price) {
		if (qty <= 0) {
			return -1;
		}

		try {
			sql = conn.createStatement();
			String s = "SELECT nextval('orders_oid_seq')";
			rs = sql.executeQuery(s);

			if (!rs.next()) {
				rs.close();
				return -1;
			}

			int oid = rs.getInt("nextval");

			s = "INSERT INTO orders VALUES" + "(?, ?, ?, ?, ?, ?, ?, 'O') ";

			ps = conn.prepareStatement(s);

			long millis = System.currentTimeMillis();
			java.sql.Date date = new java.sql.Date(millis);

			ps.setInt(1, oid);
			ps.setInt(2, cid);
			ps.setInt(3, pid);
			ps.setDate(4, date);
			ps.setInt(5, wid);
			ps.setDouble(6, qty);
			ps.setDouble(7, price);

			ps.executeUpdate();

			ps.close();
			rs.close();

			return oid;
		}

		catch (SQLException se) {
			return -1;
		}

	}

	public boolean cancelOrder(int oid) {
		try {
			String s = "SELECT * FROM orders WHERE " + "oid = " + oid;
			ps = conn.prepareStatement(s);
			rs = ps.executeQuery();

			if (!rs.next()) {
				ps.close();
				rs.close();
				return false;
			}

			String status = rs.getString("status");

			if (status.equals("O")) {
				s = "DELETE FROM orders WHERE oid = " + oid;
				sql = conn.createStatement();

				sql.executeUpdate(s);

				ps.close();
				rs.close();

				return true;
			}

			else {
				return false;
			}
		} catch (SQLException se) {
			return false;
		}

	}

	public boolean shipOrder(int odi) {
		try {
			conn.setAutoCommit(false);

			String s = "SELECT * from orders WHERE oid = " + odi;
			ps = conn.prepareStatement(s);
			rs = ps.executeQuery();

			if (!rs.next()) {
				ps.close();
				rs.close();
				return false;
			}

			String status = rs.getString("status");

			if (status.equals("S")) {
				return false;
			}

			int pid = rs.getInt("pid");
			int wid = rs.getInt("shipwid");
			double qty = rs.getDouble("quantity");

			// Decreases qty for product in stock table.
			boolean upd = this.updateStock(pid, wid, -qty);

			ps.close();
			rs.close();

			if (!upd) {
				return false;
			}

			s = "UPDATE orders SET status = 'S' WHERE " + "oid = " + odi;
			sql = conn.createStatement();
			sql.executeUpdate(s);

			conn.commit();
		} catch (SQLException se) {
			return false;
		}

		return true;
	}

	public String listStock(int pid) {
		String lst = "";

		try {
			String s = "SELECT * FROM stock WHERE pid = " + pid
					+ "ORDER BY quantity DESC";
			ps = conn.prepareStatement(s);
			rs = ps.executeQuery();

			if (!rs.next()) {
				ps.close();
				rs.close();
				return "";
			}

			int wid = rs.getInt("wid");
			double qty = rs.getDouble("quantity");

			lst += wid + ":" + qty;

			while (rs.next()) {
				wid = rs.getInt("wid");
				qty = rs.getDouble("quantity");

				lst += "#" + wid + ":" + qty;
			}

			ps.close();
			rs.close();
		} catch (SQLException se) {
			return "";
		}

		return lst;
	}

	public String listReferrals(int cid) {
		String lst = "";

		try {
			String s = "SELECT * FROM referral, customer WHERE custid = " + cid
					+ "AND custref = cid " + "ORDER BY cname ASC";
			ps = conn.prepareStatement(s);
			rs = ps.executeQuery();

			if (!rs.next()) {
				ps.close();
				rs.close();
				return "";
			}

			int cuid = rs.getInt("cid");
			String name = rs.getString("cname");

			lst += cuid + ":" + name;

			while (rs.next()) {
				cuid = rs.getInt("cid");
				name = rs.getString("cname");

				lst += "#" + cuid + ":" + name;
			}

			ps.close();
			rs.close();

		} catch (SQLException se) {
			return "";
		}

		return lst;
	}

	public boolean updateDB() {
		try {
			conn.setAutoCommit(false);

			sql = conn.createStatement();

			String s = "DROP TABLE IF EXISTS bestsellers";

			sql.executeUpdate(s);

			s = "CREATE TABLE bestsellers (" 
					+ "						 pid INTEGER,"
					+ "						 sales NUMERIC(10,2)" 
					+ "					   )";

			sql.executeUpdate(s);

			s = "INSERT INTO bestsellers (SELECT pid, sales FROM (SELECT pid, SUM(CAST((price * quantity) AS NUMERIC(10,2))) AS sales "
					+ "FROM orders "
					+ "WHERE status = 'S' "
					+ "GROUP BY pid) AS best " + "WHERE sales > 10000)";

			sql.executeUpdate(s);

			conn.commit();
		} catch (SQLException se) {
			return false;
		}

		return true;
	}

}
