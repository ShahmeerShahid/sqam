import java.sql. * ;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Assignment2 {

	// A connection to the database  
	Connection connection;

	// Statement to run queries
	Statement sql;

	// Prepared Statement
	PreparedStatement ps,
	ps2,
	ps3;

	// Resultset for the query
	ResultSet rs,
	rs2,
	rs3;

	//CONSTRUCTOR
	Assignment2() {}


	//Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
	public boolean connectDB(String URL, String username, String password) {
		try {
			Class.forName("org.postgresql.Driver");
		}
		catch(ClassNotFoundException e) {
			return false;
		}
		try {
			connection = DriverManager.getConnection(URL, username, password);
			if (connection != null) {
				return true;
			}
			return false;
		}

		catch(SQLException e) {
			return false;

		}
	}

	//Closes the connection. Returns true if closure was sucessful
	public boolean disconnectDB() {
		try {
			connection.close();
			return true;
		} catch(SQLException e) {
			return false;
		}
	}

	public boolean insertStock(int pid, int wid, double qty) {
		try {
			sql = connection.createStatement();
			sql.executeUpdate("SET search_path TO A2;");
			if (qty < 0) {
				return false;
			}

			String sqlText = "INSERT INTO stock VALUES (?,?,?);";

			ps = connection.prepareStatement(sqlText);
			ps.setInt(1, pid);
			ps.setInt(2, wid);
			ps.setDouble(3, qty);
			ps.executeUpdate();
			ps.close();
			return true;

		} catch(SQLException e) {
			
			return false;
		}
	}

	public boolean updateStock(int pid, int wid, double qty) {
		try {
			sql = connection.createStatement();
			sql.executeUpdate("SET search_path TO A2;");
			boolean ret = false;
			double current_val = getStockQty(pid, wid);
			if (current_val == -1) {
				return false;
			}
			if ((qty * -1) > current_val) {
				return false;
			}

			qty += current_val;
			String sqlText = "UPDATE stock SET quantity=? WHERE pid=? AND wid=?;";
			ps2 = connection.prepareStatement(sqlText);
			ps2.setDouble(1, qty);
			ps2.setInt(2, pid);
			ps2.setInt(3, wid);
			ps2.executeUpdate();
			ps2.close();
			return true;

		} catch(SQLException e) {
			
			return false;
		}
	}

	public int insertOrder(int cid, int pid, int wid, double qty, double price) {
		try {
			
			sql = connection.createStatement();
			sql.executeUpdate("SET search_path TO A2;");
			int ret = -1;
            
            Date date = new Date();
			String modifiedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

      String sqlText = "INSERT INTO orders(cid, pid, odate, shipwid, quantity, price, status) VALUES (?,?, Date'" + modifiedDate + "',?,?,?,?);";
			ps = connection.prepareStatement(sqlText);
			ps.setInt(1, cid);
			ps.setInt(2, pid);
			ps.setInt(3, wid);
			ps.setDouble(4, qty);
			ps.setDouble(5, price);
			ps.setString(6, "O");
			ps.executeUpdate();

			sqlText = "select currval(pg_get_serial_sequence('orders', 'oid'));";
			rs2 = sql.executeQuery(sqlText);
			if (rs2 != null) {
				rs2.next();
				ret = rs2.getInt(1);
			}

			rs2.close();
			ps.close();
			return ret;

		} catch(SQLException e) {
			
			return - 1;
		}
	}

	public boolean cancelOrder(int oid) {
		try {
			sql = connection.createStatement();
			sql.executeUpdate("SET search_path TO A2;");
			boolean ret = false;
			String sqlText = "SELECT COUNT(oid) FROM orders WHERE oid=? AND status=?;";
			ps = connection.prepareStatement(sqlText);
			ps.setInt(1, oid);
			ps.setString(2, "O");
			rs = ps.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					if (rs.getInt(1) > 0) {
						sqlText = "DELETE FROM orders WHERE oid=?;";
						ps2 = connection.prepareStatement(sqlText);
						ps2.setInt(1, oid);
						ps2.executeUpdate();
						ps2.close();
						ret = true;
					}
				}
			}
			rs.close();
			ps.close();
			return ret;

		} catch(SQLException e) {
			
			return false;
		}
	}

	public boolean shipOrder(int oid) {
		try {
			sql = connection.createStatement();
			sql.executeUpdate("SET search_path TO A2;");
			boolean ret = false;
			String sqlText = "SELECT * FROM orders WHERE oid=?;";
			ps = connection.prepareStatement(sqlText);
			ps.setInt(1, oid);
			rs = ps.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					int wid = rs.getInt("shipwid");
					int pid = rs.getInt("pid");
					double qty = rs.getDouble("quantity");
					double current_val = getStockQty(pid, wid);
					if (qty < current_val) {
						connection.setAutoCommit(false);
						updateStock(pid, wid, qty * ( - 1));
						sqlText = "UPDATE orders SET status=? WHERE oid=?;";
						ps3 = connection.prepareStatement(sqlText);
						ps3.setString(1, "S");
						ps3.setInt(2, oid);
						ps3.executeUpdate();
						connection.commit();
						connection.setAutoCommit(true);
						ps3.close();
						ret = true;
					}
				}
			}
			rs.close();
			ps.close();
			return ret;

		} catch(SQLException e) {
			
			return false;
		}
	}

	public String listStock(int pid) {
		try {
			sql = connection.createStatement();
			sql.executeUpdate("SET search_path TO A2;");
			String ret = "";
			String sqlText = "SELECT wid, quantity FROM stock WHERE pid=? ORDER BY quantity DESC;";
			ps = connection.prepareStatement(sqlText);
			ps.setInt(1, pid);
			rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					ret += rs.getInt("wid") + ":" + rs.getInt("quantity") + "#";
				}
			}
			ps.close();
			rs.close();
			if (ret.length() > 0) {
				return ret.substring(0, ret.length() - 1);
			}
			return "";
		}
		catch(SQLException e) {
			
			return "";
		}
	}

	public String listReferrals(int cid) {
		try {
			sql = connection.createStatement();
			sql.executeUpdate("SET search_path TO A2;");
			String ret = "";
			String sqlText = "SELECT r.custref, c.cname FROM referral r, customer c WHERE r.custref=c.cid AND r.custid=?;";

			ps = connection.prepareStatement(sqlText);
			ps.setInt(1, cid);
			rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					ret += rs.getInt("custref") + ":" + rs.getString("cname") + "#";
				}
			}

			ps.close();
			rs.close();

			if (ret.length() > 0) {
				return ret.substring(0, ret.length() - 1);
			}
			return "";
		} catch(SQLException e) {
			
			return "";
		}
	}

	public boolean updateDB() {
		try {
			sql = connection.createStatement();
			sql.executeUpdate("SET search_path TO A2;");
			String sqlText;
			sqlText = "CREATE TABLE  bestsellers( pid INTEGER, sales NUMERIC(10,2));";
			sql = connection.createStatement();
			sql.executeUpdate(sqlText);

			sqlText = "Insert INTO bestsellers SELECT pid, CAST(SUM(price*quantity) as NUMERIC(10,2)) AS sales FROM orders WHERE status =? AND price*quantity>10000 GROUP BY pid;";
			ps = connection.prepareStatement(sqlText);
			ps.setString(1, "S");
			ps.executeUpdate();

			ps.close();
			return true;
		} catch(SQLException e) {
			
			return false;
		}
	}

	private double getStockQty(int pid, int wid) {
		PreparedStatement p;
		ResultSet r;
		double ret = -1;
		String sqlText = "SELECT * FROM stock WHERE pid=? AND wid=?;";
		try {
			p = connection.prepareStatement(sqlText);
			p.setInt(1, pid);
			p.setInt(2, wid);
			r = p.executeQuery();
			if (r != null) {
				r.next();
				ret = r.getDouble("quantity");
			}
			p.close();
			r.close();
			return ret;
		} catch(SQLException e) {
			return - 1;
		}
	}

}
