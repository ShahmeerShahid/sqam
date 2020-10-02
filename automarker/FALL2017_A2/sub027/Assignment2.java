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

	public boolean connectDB(String URL, String username, String password) {
		try {
			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			return false;

		}

		try {
			connection = DriverManager.getConnection(URL, username, password);
			sql = connection.createStatement();
		} catch (SQLException e) {

			return false;

		} finally {
			if (connection != null) {
				return true;
			} else {
				return false;
			}
		}

	}

	public boolean disconnectDB() {

		try {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return false;
		}

		return connection == null;
	}

	public boolean insertStock(int pid, int wid, double qty) {
		String sqlText;
		try {

			sqlText = "SELECT pid FROM product WHERE pid =" + pid;
			rs = sql.executeQuery(sqlText);
			if (!rs.next()) {
				rs.close();
				return false;
			}
			rs.close();

			sqlText = "SELECT wid FROM warehouse " + "WHERE wid =" + wid;
			rs = sql.executeQuery(sqlText);
			if (!rs.next()) {
				rs.close();
				return false;
			}
			rs.close();

			sqlText = "SELECT pid, wid FROM stock " + "WHERE pid = " + pid + " AND wid = " + wid;
			rs = sql.executeQuery(sqlText);
			if (rs.next()) {
				rs.close();
				return false;
			}
			rs.close();

			sqlText = "INSERT INTO stock" + "Values(" + pid + "," + wid + "," + qty + ")";
			if (sql.executeUpdate(sqlText) == 0) {
				return false;
			}
			return true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block

			return false;
		}

	}

	public boolean updateStock(int pid, int wid, double qty) {
		try {
			String sqlText;
			double quantity;

			sqlText = "SELECT quantity FROM stock " + "WHERE pid = " + pid + " AND wid = " + wid;
			rs = sql.executeQuery(sqlText);

			if (!rs.next()) {
				rs.close();
				return false;
			}
			quantity = rs.getDouble(1);
			rs.close();

			quantity += qty;
			if (quantity < 0) {
				rs.close();
				return false;
			}

			sqlText = "UPDATE stock " + "SET quantity = CAST(" + quantity + "as NUMERIC(10,2))" + " WHERE pid = " + pid
					+ " AND wid = " + wid;

			sql.executeUpdate(sqlText);
			rs.close();
			return true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return false;
		}

	}

	public int insertOrder(int cid, int pid, int wid, double qty, double price) {
		String sqlText;

		try {
			sqlText = "SELECT * FROM costumer WHERE cid = " + cid;
			rs = sql.executeQuery(sqlText);
			if (!rs.next()) {
				rs.close();
				return -1;
			}
			rs.close();

			sqlText = "SELECT * FROM product WHERE pid = " + pid;
			rs = sql.executeQuery(sqlText);
			if (!rs.next()) {
				rs.close();
				return -1;
			}
			rs.close();

			sqlText = "SELECT * FROM warehouse WHERE wid = " + wid;
			rs = sql.executeQuery(sqlText);
			if (!rs.next()) {
				rs.close();
				return -1;
			}
			rs.close();

			sqlText = "SELECT currval(pg_get_serial_sequence('orders', 'oid'))";
			rs = sql.executeQuery(sqlText);
			if (!rs.next()) {
				rs.close();
				return -1;
			}

			int currOid = rs.getInt("oid");
			currOid++;
			sqlText = "INSERT INTO orders VALUES(" + currOid + ", " + cid + ", " + pid + ", CAST(now() as DATE), " + wid
					+ ", " + qty + ", " + price + ", 'O')";
			sql.executeUpdate(sqlText);

			rs.close();
			return currOid;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return -1;
		}

	}

	public boolean cancelOrder(int oid) {
		try {
			String sqlText;

			sqlText = "SELECT * FROM orders WHERE oid = " + oid + " AND status = 'O'";
			rs = sql.executeQuery(sqlText);
			if (rs.next()) {
				rs.close();
				return false;
			}
			rs.close();

			sqlText = "DELETE FROM orders WHERE oid = " + oid;
			sql.executeUpdate(sqlText);
			return true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	public boolean shipOrder(int oid) {
		String sqlText;
		try {
			sqlText = "SELECT o.quantity,s.quantity, s.pid ,o.wid" + "FROM order o, stock s"
					+ "WHERE o.quantity <= s.quantity" + "AND o.shipwid = s.wid" + "AND o.oid = " + oid;
			rs = sql.executeQuery(sqlText);
			float oq, sq;
			int pd, wd;
			if (rs.next()) {
				oq = rs.getFloat(1);
				sq = rs.getFloat(2);
				pd = rs.getInt(3);
				wd = rs.getInt(4);

				rs.close();

			} else {
				rs.close();
				return false;
			}

			sqlText = "UPDATE orders SET status = 'S' WHERE oid =" + oid;
			sql.execute(sqlText);
			return updateStock(pd, wd, sq - oq);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return false;
		}

	}

	public String listStock(int pid) {
		String sqlText;
		sqlText = "SELECT wid,quantity" + "FROM stock" + "WHERE stock.pid = " + pid + "AND quantity > 0"
				+ "ORDER BY quantity DESC";
		try {
			rs = sql.executeQuery(sqlText);
			if (!rs.next()) {
				rs.close();
				return "";
			}

			String result = "";
			int wd = rs.getInt(1);
			int qd = rs.getInt(2);
			result += wd + ":" + qd;
			while (rs.next()) {
				result += "#";
				wd = rs.getInt(1);
				qd = rs.getInt(2);
				result += wd + ":" + qd;
			}
			rs.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return "";
		}
	}

	public String listReferrals(int cid) {
		String sqlText;
		sqlText = "SELECT cid,cname" + "FROM referral, customer" + "WHERE custid = " + cid + "AND custref = cid"
				+ "ORDER BY cname ASC";
		try {
			rs = sql.executeQuery(sqlText);
			if (!rs.next()) {
				rs.close();
				return "";
			}
			int id = rs.getInt(1);
			int nm = rs.getInt(2);
			String result = id + ":" + nm;
			while (rs.next()) {
				result += "#" + id + ":" + nm;
			}
			rs.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return "";
		}

	}

	public boolean updateDB() {
		String sqlText;
		sqlText = "SELECT * " + "FROM (SELECT pid, CAST(SUM(quantity*price) as NUMERIC(10,2)) AS sales "
				+ "FROM orders WHERE status = 'S' GROUP BY pid) as temp" + "WHERE sales > 10000.00 ORDER BY sales DESC";
		try {
			rs = sql.executeQuery(sqlText);
			sql.executeUpdate("CREATE TABLE bestsellers (pid INTEGER, sales NUMERIC(10,2))");
			if (!rs.next()) {
				rs.close();
				return false;
			}
			sqlText = "INSERT INTO bestsellers VALUES(" + rs.getInt(1) + "," + rs.getDouble(2) + ")";
			sql.executeUpdate(sqlText);
			while (rs.next()) {
				sqlText = "INSERT INTO bestsellers VALUES(" + rs.getInt(1) + "," + rs.getDouble(2) + ")";
				sql.executeUpdate(sqlText);
			}
			rs.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return false;
		}

	}

}
