import java.sql.*;
import java.time.LocalDate;

public class Assignment2 {

	// A connection to the database
	Connection conn;

	// Statement to run queries
	//Statement sql;

	// Prepared Statement
	PreparedStatement ps;

	// Resultset for the query
	ResultSet rs;

	// CONSTRUCTOR
	Assignment2() {
	}

	// Using the input parameters, establish a connection to be used for this
	// session. Returns true if connection is successful
	public boolean connectDB(String URL, String username, String password) throws ClassNotFoundException {
		//String URL, String username, String password
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(URL, username, password);
			return true;
		} catch (SQLException s) {
			return false;
		}
	}

	// Closes the connection. Returns true if closure was successful
	public boolean disconnectDB() {
		try {
			if (ps != null) ps.close();
			if (rs != null) rs.close();
			if (conn != null) conn.close();
			return true;
		} catch (SQLException s) {
			return false;
		}
	}
	

	public boolean insertStock(int pid, int wid, double qty) {
		if (qty < 0) {
			return false;
		}
		try {
			// Insert row into stock table
			String q = "INSERT INTO a2.stock VALUES(?, ?, ?);";
			ps = conn.prepareStatement(q);
			ps.setInt(1, pid);
			ps.setInt(2, wid);
			ps.setDouble(3, qty);
			ps.executeUpdate();
			ps.close();
			return true;
		} catch (SQLException s) {
			return false;
		}
	}

	public boolean updateStock(int pid, int wid, double qty) {
		try {
			String q1 = "SELECT quantity FROM a2.stock WHERE pid = ? AND wid = ?;";
			ps = conn.prepareStatement(q1);
			ps.setInt(1, pid);
			ps.setInt(2, wid);
			rs = ps.executeQuery();
			if (!rs.next()) {
				return false;
			}
			double quantity = rs.getDouble(1);
			if (qty < 0 && (quantity + qty < 0)) {
				return false;
			}
			String q2 = "UPDATE a2.stock SET quantity = ? WHERE pid = ? AND wid = ? ;";
			ps = conn.prepareStatement(q2);
			ps.setDouble(1, quantity + qty);
			ps.setInt(2, pid);
			ps.setInt(3, wid);
			ps.executeUpdate();
			return true;
		} catch (SQLException s) {
			return false;
		}
	}

	public int insertOrder(int cid, int pid, int wid, double qty, double price) {
		try {
			
			if (qty <= 0) return -1;
			//Make sure pid, wid, cid are valid pid, wid, cid in product, warehouse, customer tables
			if (	   !checkInTable("a2.product", "pid", pid, -1, null) 
					|| !checkInTable("a2.warehouse", "wid", wid, -1, null) 
					|| !checkInTable("a2.customer", "cid", cid, -1, null)) {
				return -1;
			}
			
			String oidQuery = "SELECT nextval(pg_get_serial_sequence('a2.orders', 'oid'));";
			ps = conn.prepareStatement(oidQuery);
			rs = ps.executeQuery();
			if (!rs.next()) {
				return -1;
			}
			int oid = rs.getInt(1);
			
			java.time.LocalDate date = LocalDate.now();
			java.sql.Date odate = java.sql.Date.valueOf(date);
			
			String insertSmt = "INSERT INTO a2.orders VALUES(?, ?, ?, ?, ?, ?, ?, 'O');";
			ps = conn.prepareStatement(insertSmt);
			ps.setInt(1, oid);
			ps.setInt(2, cid);
			ps.setInt(3, pid);
			ps.setDate(4, odate);
			ps.setInt(5, wid);
			ps.setDouble(6, qty);
			ps.setDouble(7, price);
			
			ps.executeUpdate();
			return oid;
		} catch (SQLException s) {
			return -1;
		}
	}
	
	private boolean checkInTable(String tableName, String attrName, int valueInt, double valueDbl, String valueStr) throws SQLException {
		/* Checks for only one attribute in a table */
		String q = "SELECT * FROM " + tableName + " WHERE " + attrName + " = ?;";
		ps = conn.prepareStatement(q);
		if (valueInt != -1) {
			ps.setInt(1, valueInt);
		} else if (valueDbl != -1) {
			ps.setDouble(1, valueDbl);
		} else if (valueStr != null) {
			ps.setString(1, valueStr);
		}
		rs = ps.executeQuery();
		if (!rs.next()) {
			ps.close();
			return false;
		}
		ps.close();
		return true;
	}

	public boolean cancelOrder(int oid) {
		try {
			String s = "SELECT status FROM a2.orders WHERE oid = ?;";
			ps = conn.prepareStatement(s);
			ps.setInt(1, oid);
			rs = ps.executeQuery();
			
			// Make sure oid is in orders
			if (!rs.next()) {
				return false;
			}
			String status = rs.getString(1);
			if (status.contentEquals("S")) {
				return false;
			}
			ps.close();
			String d = "DELETE FROM a2.orders WHERE oid = ?;";
			ps = conn.prepareStatement(d);
			ps.setInt(1, oid);
			ps.executeUpdate();
			return true;
		} catch (SQLException s) {
			return false;
		}
	}

	public boolean shipOrder(int oid) {
		try {
			String s1 = "SELECT shipwid, pid, quantity, status FROM a2.orders WHERE oid = ? ;";
			ps = conn.prepareStatement(s1);
			ps.setInt(1, oid);
			ResultSet ordersResult = ps.executeQuery();
			// Get the order information for OID
			if (!ordersResult.next()) {
				return false;
			}
			// Ensure that status is not already shipped
			String status = ordersResult.getString("status");
			if (status.contentEquals("S")) {
				return false;
			}
			int pid = ordersResult.getInt("pid");
			int wid = ordersResult.getInt("shipwid");
			double orderQty = ordersResult.getDouble("quantity");
			
			// Get corresponding stock for the warehouse where order is being shipped from
			String s2 = "SELECT quantity FROM a2.stock WHERE pid = ? AND wid = ? ;";
			ps =  conn.prepareStatement(s2);
			ps.setInt(1, pid);
			ps.setInt(2, wid);
			ResultSet stockResult = ps.executeQuery();
			if (!stockResult.next()) {
				return false;
			}
			double stockQty = stockResult.getDouble("quantity");
			if (stockQty < orderQty) {
				return false;
			}		
			updateStock(pid, wid, -orderQty);
			
			String updateOrder = "UPDATE a2.orders SET status = 'S' WHERE oid = ?;";
			ps = conn.prepareStatement(updateOrder);
			ps.setInt(1, oid);
			ps.executeUpdate();
			return true;
			
		} catch (SQLException s) {
			return false;
		}
	}

	public String listStock(int pid) {
		try {
			String s = "SELECT wid, quantity FROM a2.stock WHERE pid = ? AND quantity > 0 ORDER BY quantity DESC;";
			ps = conn.prepareStatement(s);
			ps.setInt(1, pid);
			rs = ps.executeQuery();
			
			StringBuffer sb = new StringBuffer();
			// If no entries for pid, return empty string buffer
			while (rs.next()) {
				Double qty = rs.getDouble("quantity");
				String qty2 = String.format("%.2f",qty);
				// If quantity = 0, return empty string
				if (qty != 0) {
					sb.append(rs.getInt("wid"));
					sb.append(":");
					sb.append(qty2);
					if (!rs.isLast()) {
						sb.append("#");
					}
				}
			}
			String results = sb.toString();
			return results;
		} catch (SQLException s) {
			return "";
		}
	}

	public String listReferrals(int cid) {
		try {
			String s = "SELECT r.custref, c.cname FROM a2.referral AS r, a2.customer AS c WHERE r.custref = c.cid AND r.custid = ?"
					+ "ORDER BY cname;";
			ps = conn.prepareStatement(s);
			ps.setInt(1, cid);
			rs = ps.executeQuery();
			
			StringBuilder sb = new StringBuilder();
			while (rs.next()) {
				sb.append(rs.getString("custref"));
				sb.append(":");
				sb.append(rs.getString("cname"));
				if (!rs.isLast()) {
					sb.append("#");
				}
			}
			return sb.toString();
			
		} catch (SQLException s) {
			return "";
		}
	}

	public boolean updateDB() {
		try {
			String s1 = "DROP TABLE IF EXISTS a2.bestsellers CASCADE;";
			ps = conn.prepareStatement(s1);
			ps.executeUpdate();
			ps.close();
			
			String s2 = "CREATE TABLE a2.bestsellers("
					+ "pid		INTEGER,		"
					+ "sales	NUMERIC(10,2));";
			ps = conn.prepareStatement(s2);
			ps.executeUpdate();
			ps.close();
			
			String s3 = "INSERT INTO a2.bestsellers (pid, sales) "
					+ "SELECT o.pid, SUM(o.price*o.quantity) AS sales "
					+ "FROM a2.orders AS o "
					+ "WHERE o.status = 'S' "
					+ "GROUP BY pid "
					+ "HAVING SUM(round(ceil(o.price*o.quantity*100)/100,2)) > 10000;";
			
			ps = conn.prepareStatement(s3);
			ps.executeUpdate();
			ps.close();
			return true;
			
		} catch (SQLException s) {
			return false;
		}
	}

}
