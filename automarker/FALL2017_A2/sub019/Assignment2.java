
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
	// session. Returns true if connection is sucessful
	public boolean connectDB(String URL, String username, String password) {
		try {

			// customURL = URL + ""
			connection = DriverManager.getConnection(URL, username, password);
			sql = connection.createStatement();
			sql.execute("SET search_path TO a2");
			sql.close();
			return true;
		} catch (SQLException e) {

		}
		return false;
	}

	// Closes the connection. Returns true if closure was sucessful
	public boolean disconnectDB() {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception x) {

			}
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (Exception x) {

			}
		}
		if (connection != null) {
			try {
				connection.close();

				return true;
			} catch (Exception e) {

			}
		}
		return false;
	}

	public boolean insertStock(int id, int wid, double qty) {

		if (checkInsertStockConstrain(id, wid, qty) != true) {
			return false;
		}

		try {

			String sqlText = "INSERT INTO stock " + "VALUES (?, ?, ?)";

			ps = connection.prepareStatement(sqlText);
			ps.setInt(1, id);
			ps.setInt(2, wid);
			ps.setDouble(3, qty);

			int rowEffect = ps.executeUpdate();

			ps.close();

			return rowEffect == 1;

		} catch (SQLException e) {

		}

		return false;
	}

	private boolean checkInsertStockConstrain(int id, int wid, double qty) {

		if (qty < 0) {
			return false;
		}

		boolean pidCon = false;
		boolean widCon = false;
		boolean stockCon = true;

		// pid constrain
		try {
			String sqlText = "SELECT pid FROM product";
			sql = connection.createStatement();
			rs = sql.executeQuery(sqlText);
			if (rs != null) {
				while (rs.next()) {
					if (rs.getInt(1) == id) {
						pidCon = true;
						break;
					}
				}
			}

			sql.close();
			rs.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block

		} catch (Exception e) {
			// TODO: handle exception
		}

		// wid constrain
		try {
			String sqlText = "SELECT wid FROM warehouse";
			sql = connection.createStatement();
			rs = sql.executeQuery(sqlText);
			if (rs != null) {
				while (rs.next()) {
					if (rs.getInt(1) == wid) {
						widCon = true;
						break;
					}
				}
			}

			sql.close();
			rs.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block

		} catch (Exception e) {
			// TODO: handle exception

		}

		try {
			String sqlText = "SELECT pid, wid FROM stock";
			sql = connection.createStatement();
			rs = sql.executeQuery(sqlText);
			if (rs != null) {
				while (rs.next()) {
					if (rs.getInt(1) == id && rs.getInt(2) == wid) {
						stockCon = false;
						break;
					}
				}
			}

			sql.close();
			rs.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block

		} catch (Exception e) {
			// TODO: handle exception

		}

		return (pidCon && widCon && stockCon);
	}

	public boolean updateStock(int id, int wid, double qty) {
		boolean sucess = false;
		try {
			String sqlText = "SELECT * FROM stock";
			sql = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

			rs = sql.executeQuery(sqlText);
			if (rs != null) {
				while (rs.next()) {
					if (rs.getInt(1) == id && rs.getInt(2) == wid) {
						double newQty = rs.getDouble("quantity") + qty;
						if (newQty < 0) {
							return false;
						}

						rs.updateDouble("quantity", newQty);
						rs.updateRow();
						sucess = true;
						break;
					}
				}
			}

			rs.close();
			sql.close();
			return sucess;
		} catch (SQLException e) {
			// TODO: handle exception

		} catch (Exception e) {
			// TODO: handle exception
		}

		return false;
	}

	public int insertOrder(int cid, int pid, int wid, double qty, double price) {
		
		
		if (price <= 0){
			return -1;
		}
		
		if (checkInsertOrderConstrain(pid, wid, cid) == false) {
			return -1;
		}
		

		try {
			String sqlText = "INSERT INTO orders (cid, pid, odate, shipwid,  quantity, price, status) "
					+ "VALUES(?, ?, CAST(TO_CHAR(Now(), 'YYYY-MM-DD') as DATE), ?, ?, ?, 'O');";

			ps = connection.prepareStatement(sqlText);
			ps.setInt(1, cid);
			ps.setInt(2, pid);
			ps.setInt(3, wid);
			ps.setDouble(4, qty);
			ps.setDouble(5, price);

			ps.executeUpdate();

			ps.close();

		} catch (SQLException e) {

			return -1;
		}

		int currOrderId = -1;

		try {
			String sqlText = "select currval(pg_get_serial_sequence('orders','oid'))";
			sql = connection.createStatement();
			rs = sql.executeQuery(sqlText);

			if (rs != null) {
				while (rs.next()) {
					currOrderId = rs.getInt(1);
				}
			}
			sql.close();
			rs.close();

			return currOrderId;

		} catch (SQLException e) {
			return -1;
		}
	}

	private boolean checkInsertOrderConstrain(int pid, int wid, int cid) {

		boolean pidCon = false;
		boolean widCon = false;
		boolean cidCon = false;

		// pid constrain
		try {
			String sqlText = "SELECT pid FROM product";
			sql = connection.createStatement();
			rs = sql.executeQuery(sqlText);
			if (rs != null) {
				while (rs.next()) {
					if (rs.getInt(1) == pid) {
						pidCon = true;
						break;
					}
				}
			}

			sql.close();
			rs.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block

		} catch (Exception e) {
			// TODO: handle exception
		}

		// wid constrain
		try {
			String sqlText = "SELECT wid FROM warehouse";
			sql = connection.createStatement();
			rs = sql.executeQuery(sqlText);
			if (rs != null) {
				while (rs.next()) {
					if (rs.getInt(1) == wid) {
						widCon = true;
						break;
					}
				}
			}

			sql.close();
			rs.close();

		} catch (SQLException e) {

		} catch (Exception e) {

		}

		try {
			String sqlText = "SELECT cid FROM customer";
			sql = connection.createStatement();
			rs = sql.executeQuery(sqlText);
			if (rs != null) {
				while (rs.next()) {
					if (rs.getInt(1) == cid) {
						cidCon = true;
						break;
					}
				}
			}

			sql.close();
			rs.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
		} catch (Exception e) {
			// TODO: handle exception

		}

		return (pidCon && widCon && cidCon);
	}

	public boolean cancelOrder(int oid) {

		int roweffect = -1;
		try {
			String sqlText = "DELETE FROM orders WHERE oid = ? and status = 'O'";
			ps = connection.prepareStatement(sqlText);
			ps.setInt(1, oid);

			roweffect = ps.executeUpdate();

			sql.close();
			rs.close();

		} catch (SQLException e) {
			return false;
		} catch (Exception e) {
			// TODO: handle exception

		}

		if (roweffect == 1) {
			return true;
		} else {
			return false;
		}
	}

	public boolean shipOrder(int oid) {
		String status = null;
		double qty = -1;
		int shipwid = -1;
		int pid = -1;
		try {
			connection.setAutoCommit(false);
			String sqlText = "SELECT * FROM orders WHERE oid = ?";
			ps = connection.prepareStatement(sqlText);
			ps.setInt(1, oid);

			rs = ps.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					status = rs.getString("status");
					qty = rs.getDouble("quantity");
					shipwid = rs.getInt("shipwid");
					pid = rs.getInt("pid");
				}
			}

			ps.close();
			rs.close();

			if (status == null) {
				connection.rollback();
				connection.setAutoCommit(true);
				return false;
			}

			boolean sucess = updateStock(pid, shipwid, -qty);

			int rowEffect = -1;
			if (sucess) {
				sql = connection.createStatement();

				rowEffect = sql.executeUpdate("UPDATE orders SET status = 'S' WHERE oid =" + oid + "AND status = 'O'");

				sql.close();

			} else {
				connection.rollback();
				connection.setAutoCommit(true);

				return false;

			}

			if (rowEffect == 1) {
				connection.commit();
				connection.setAutoCommit(true);
				return true;
			} else {
				connection.rollback();
				connection.setAutoCommit(true);
				return false;
			}

		} catch (SQLException e) {

		}

		return false;
	}

	public String listStock(int pid) {
		String re = "";

		try {

			String sqlText = "SELECT * FROM stock WHERE quantity > 0 AND pid = ? ORDER BY quantity DESC";

			ps = connection.prepareStatement(sqlText);

			ps.setInt(1, pid);

			rs = ps.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					int wid = rs.getInt("wid");
					double qty = rs.getDouble("quantity");

					String row = String.format("%d:%.2f#", wid, qty);

					re = re + row;
				}
			}

			ps.close();

			rs.close();

			if (re.length() > 0) {
				re = re.substring(0, re.length() - 1);
			}

		} catch (SQLException e) {

			return "";
		}

		return re;
	}

	public String listReferrals(int cid) {
		String re = "";

		try {

			String sqlText = "SELECT cid, cname FROM (SELECT custref FROM referral WHERE custid = ?)"
					+ "AS custr, customer WHERE customer.cid = custr.custref ORDER BY cname ASC;";

			ps = connection.prepareStatement(sqlText);

			ps.setInt(1, cid);

			rs = ps.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					int rcid = rs.getInt("cid");
					String cname = rs.getString("cname");

					String row = String.format("%d:%s#", rcid, cname);

					re = re + row;
				}
			}

			ps.close();

			rs.close();

			if (re.length() > 0) {
				re = re.substring(0, re.length() - 1);
			}

		} catch (SQLException e) {

			return "";
		}

		return re;
	}

	public boolean updateDB() {
		// start

		try {
			connection.setAutoCommit(false);
			String ddlText = "CREATE TABLE bestsellers("
					+ "pid         INTEGER           REFERENCES product(pid) ON DELETE RESTRICT,"
					+ "sales  NUMERIC(10,2)," + "PRIMARY KEY(pid))";

			sql = connection.createStatement();

			sql.execute(ddlText);

			sql.close();

		} catch (SQLException e) {

			try {
				connection.rollback();
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block

			}
			return false;

		}

		int rowEffect = -1;

		try {
			String sqlText = "INSERT INTO bestsellers(pid, sales) "
					+ "SELECT pid,  CAST(SUM(price*quantity) as NUMERIC(10,2)) "
					+ "AS sales FROM orders WHERE status = 'S' " + "GROUP BY pid "
					+ "HAVING CAST(SUM(price*quantity) as NUMERIC(10,2))  > 10000.00";

			sql = connection.createStatement();

			rowEffect = sql.executeUpdate(sqlText);

			sql.close();

			if (rowEffect != -1) {
				connection.commit();
				connection.setAutoCommit(true);
				return true;
			} else {

				throw new SQLException();
			}

		} catch (SQLException e) {

			try {
				connection.rollback();
				connection.setAutoCommit(true);
				return false;
			} catch (SQLException e1) {

				return false;
			}

		}

	}

}
