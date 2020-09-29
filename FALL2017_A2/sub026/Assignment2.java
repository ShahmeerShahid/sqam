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

	//String to house query
	String queryString;

	//CONSTRUCTOR
	Assignment2(){
	}

	//Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
	public boolean connectDB(String URL, String username, String password){
		try {
			// Load JDBC driver
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			return false;
		}

		try {
			connection = DriverManager.getConnection(URL, username, password);
		} catch (SQLException se){
			return false;
		}

		try {
			sql = connection.createStatement();
			queryString = "SET search_path TO a2test";
			sql.executeUpdate(queryString);
		} catch (SQLException se) {
			return false;
		}

		return true;
	}

	//Closes the connection. Returns true if closure was sucessful
	public boolean disconnectDB(){
		try {
			connection.close();
			sql.close();
			ps.close();

			return true;
		} catch (SQLException se) {
			return false;
		}
	}

	public boolean insertStock(int pid, int wid, double qty) {
		try {
			queryString = "SELECT * FROM product WHERE " + pid + " IN (SELECT pid FROM product)";
			ps = connection.prepareStatement(queryString);
			rs = ps.executeQuery();
			if (rs.next() == false) {
				return false;
			}

			queryString = "SELECT * FROM warehouse WHERE " + wid + " IN (SELECT wid FROM warehouse)";
			ps = connection.prepareStatement(queryString);
			rs = ps.executeQuery();

			if (rs.next() == false) {
				return false;
			}

			queryString = "SELECT * FROM stock WHERE " + pid + " IN (SELECT pid FROM stock)";
			ps = connection.prepareStatement(queryString);
			rs = ps.executeQuery();
			if (rs.next() == true) {
				return false;
			}

			queryString = "SELECT * FROM stock WHERE " + wid + " IN (SELECT wid FROM stock)";
			ps = connection.prepareStatement(queryString);
			rs = ps.executeQuery();
			if (rs.next() == false) {
				return false;
			}

			if (qty<0) {
				return false;
			}

			sql = connection.createStatement();
			queryString = "INSERT INTO stock VALUES(" + pid + "," + wid + "," + qty + ")";
			sql.executeUpdate(queryString);
			return true;
		} catch (SQLException se) {
			return false;
		}
	}

	public boolean updateStock(int pid, int wid, double qty) {
		try {
			if (qty < 0) {
				queryString = "SELECT pid FROM stock WHERE pid="+pid+" AND wid="+wid+" AND "+Math.abs(qty)+"<=quantity";
				ps = connection.prepareStatement(queryString);
				rs = ps.executeQuery();
				if (rs.next() == false) {
					return false;
				}
			} else {
				queryString = "SELECT pid FROM stock WHERE pid=" + pid + " AND wid=" + wid;
				ps = connection.prepareStatement(queryString);
				rs = ps.executeQuery();
				if (rs.next() == false) {
					return false;
				}
			}

			sql = connection.createStatement();
			queryString = "UPDATE stock SET quantity=quantity+"+qty+" WHERE pid="+pid+" AND wid="+wid;
			sql.executeUpdate(queryString);

			return true;

		} catch (SQLException se) {
			return false;
		}
	}

	public int insertOrder(int cid, int pid, int wid, double qty, double price){
		try {
			//Cannot insert an order with qty <= 0
			if (qty<=0) {
				return -1;
			}

			queryString = "SELECT cid FROM customer WHERE cid=" + cid;
			ps = connection.prepareStatement(queryString);
			rs = ps.executeQuery();
			if (rs.next() == false) {
				return -1;
			}

			queryString = "SELECT pid FROM product WHERE pid=" + pid;
			ps = connection.prepareStatement(queryString);
			rs = ps.executeQuery();
			if (rs.next() == false) {
				return -1;
			}

			queryString = "SELECT wid FROM warehouse WHERE wid=" + wid;
			ps = connection.prepareStatement(queryString);
			rs = ps.executeQuery();
			if (rs.next() == false) {
				return -1;
			}

			sql = connection.createStatement();
			queryString = "INSERT INTO orders(cid,pid,odate,shipwid,quantity,price,status) VALUES ("+cid+","+pid+",CURRENT_TIMESTAMP,"+wid+","+qty+","+price+",'O')";
			sql.executeUpdate(queryString);

			queryString = "SELECT nextval(pg_get_serial_sequence('orders','oid'))";
			ps = connection.prepareStatement(queryString);
			rs = ps.executeQuery();
			if (rs.next() == false) {
				return -1;
			}

			int nval = rs.getInt("nextval");
			return nval;

		} catch (SQLException se) {
			return -1;
		}
	}

	public boolean cancelOrder(int oid) {
		try {
			queryString = "SELECT oid FROM orders WHERE status='O' AND oid=" + oid;
			ps = connection.prepareStatement(queryString);
			rs = ps.executeQuery();
			if (rs.next() == false) {
				return false;
			}

			sql = connection.createStatement();
			queryString = "DELETE FROM orders WHERE oid="+oid;
			sql.executeUpdate(queryString);
			return true;
		} catch (SQLException se) {
			return false;
		}
	}

	public boolean shipOrder(int oid){
		try {
			int pid, wid = 0;
			float qty = 0;
			//Get the pid, wid and qty from orders with oid
			queryString = "SELECT pid, shipwid, quantity FROM orders WHERE oid=" + oid +" AND status='O'";
			ps = connection.prepareStatement(queryString);
			rs = ps.executeQuery();
			if (rs.next() == false) {
				return false;
			}

			pid = rs.getInt("pid");
			wid = rs.getInt("shipwid");
			qty = rs.getFloat("quantity");

			if (updateStock(pid, wid, -qty) == false) {
				return false;
			}

			sql = connection.createStatement();
			queryString = "UPDATE orders SET status='S' WHERE oid="+oid;
			sql.executeUpdate(queryString);
			return true;
		} catch (SQLException se) {
			return false;
		}
	}

	public String listStock(int pid){
		try {
			String table = "";
			queryString = "SELECT wid, quantity FROM stock WHERE pid="+pid+" ORDER BY quantity DESC";
			ps = connection.prepareStatement(queryString);
			rs = ps.executeQuery();
			boolean isNext = rs.next();
			while (isNext) {
				//Columns seperated by : and rows #
				table = table + rs.getInt("wid") + ":";
				table = table + rs.getFloat("quantity");
				if ((isNext = rs.next())) {
					table += "#";
				}
			}
			return table;
		} catch (SQLException se) {
			return "";
		}
	}

	public String listReferrals(int cid){
		try {
			String table = "";
			queryString = "SELECT custref, cname FROM customer C, referral R WHERE R.custid="+cid+" AND C.cid=R.custref ORDER BY cname ASC";
			ps = connection.prepareStatement(queryString);
			rs = ps.executeQuery();
			boolean isNext = rs.next();
			while (isNext) {
				//Columns seperated by : and rows #
				table = table + rs.getInt("custref") + ":";
				table = table + rs.getString("cname");
				if ((isNext = rs.next())) {
					table += "#";
				}
			}
			return table;
		} catch (SQLException se) {
			return "";
		}
	}

	public boolean updateDB(){
		try {
			sql = connection.createStatement();
			queryString = "DROP TABLE IF EXISTS bestsellers CASCADE";
			sql.executeUpdate(queryString);

			queryString = "CREATE TABLE bestsellers(pid INTEGER, sales NUMERIC(10,2))";
			sql.executeUpdate(queryString);

			queryString = "CREATE VIEW bestsells AS (SELECT pid, SUM(price*quantity) AS sales FROM orders WHERE status='S' GROUP BY pid ORDER BY pid)";
			sql.executeUpdate(queryString);

			queryString = "INSERT INTO bestsellers (SELECT pid, sales FROM bestsells WHERE sales > 10000.00)";
			sql.executeUpdate(queryString);

			queryString = "DROP VIEW bestsells";
			sql.executeUpdate(queryString);

			return true;
		} catch (SQLException se) {
			return false;
		}
	}
}
