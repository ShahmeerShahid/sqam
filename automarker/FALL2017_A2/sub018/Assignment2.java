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

  // String for the query
  String sqlText;
  
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
		sql = connection.createStatement();
		sqlText = "SET search_path TO A2";
		sql.executeUpdate(sqlText);
	} catch (SQLException e) {
		return false;
	}

	if (connection != null) {
		return true;
		
	} else {
		return false;
	}
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
	try {
		if (!sql.isClosed())
			sql.close();
		if (!rs.isClosed())
			rs.close();
		if (!ps.isClosed())
			ps.close();
		connection.close();
	} catch (SQLException e) {
		return false;
	}
	return true;
  }
    
  public boolean insertStock(int id, int wid, double qty) {
	
	try {
		

		// make sure pid exists in products table
		sqlText = "SELECT * FROM product WHERE pid = " + id;
		rs = sql.executeQuery(sqlText);
		if ((rs != null) & (rs.next() == false)) {
			return false;
		}

		// make sure wid exists in products table
		sqlText = "SELECT * FROM warehouse WHERE wid = " + wid;
		rs = sql.executeQuery(sqlText);
		if ((rs != null) & (rs.next() == false)) {
			return false;
		}

		// make sure stock with pid and wid not already in table
		sqlText = "SELECT * FROM stock WHERE pid = " + id + "AND wid = " + wid;
		rs = sql.executeQuery(sqlText);
		if ((rs != null) & (rs.next() == true)) {
			return false;
		}

		// make sure qty >= 0
		if (qty < 0) {
			return false;
		}

		// safe to insert stock
		sqlText = "INSERT INTO stock " + 
			"VALUES (" + Integer.toString(id) + ", " +
			Integer.toString(wid) + " , " +
			Double.toString(qty) + ")";
		sql.executeUpdate(sqlText);
		
	} catch (SQLException e) {
		e.printStackTrace();
		return false;
	}

	return true;
  }
  
   public boolean updateStock(int id, int wid, double qty) {
	try {
		// make sure stock with pid and wid is in table
		sqlText = "SELECT * FROM stock WHERE pid = " + id + "AND wid = " + wid;
		rs = sql.executeQuery(sqlText);
		if ((rs != null) & (rs.next() == false)) {
			return false;
		}

		// if qty is negative, make sure abs(qty) <= quantity on corresponding row
		sqlText = "SELECT quantity FROM stock WHERE pid = " + id + " AND wid = " + wid;
		rs = sql.executeQuery(sqlText);
		rs.next();
		double new_qty = rs.getDouble("quantity") + qty;
		
		if (new_qty < 0) {
			return false;
		}

		// safe to update stock, have new_qty
		sqlText = "UPDATE stock SET quantity = " + (new_qty) +
				" WHERE pid = " + id + " AND wid = " + wid;
		sql.executeUpdate(sqlText);
		
	} catch (SQLException e) {
		e.printStackTrace();
		return false;
	}
	return true;
  }
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
	try {

		// make sure cid exists in customer table
		sqlText = "SELECT * FROM customer WHERE cid = " + cid;
		rs = sql.executeQuery(sqlText);
		if ((rs != null) & (rs.next() == false)) {
			return -1;
		}

		// make sure pid exists in product table
		sqlText = "SELECT * FROM product WHERE pid = " + pid;
		rs = sql.executeQuery(sqlText);
		if ((rs != null) & (rs.next() == false)) {
			return -1;
		}

		// make sure wid exists in warehouse table
		sqlText = "SELECT * FROM warehouse WHERE wid = " + wid;
		rs = sql.executeQuery(sqlText);
		if ((rs != null) & (rs.next() == false)) {
			return -1;
		}

		// retrieve curr system date
		sqlText = "SELECT CURRENT_DATE";
		rs = sql.executeQuery(sqlText);
		rs.next();
		Date curr_date = rs.getDate(1);


		sqlText = "select nextval('orders_oid_seq');";

             	rs = sql.executeQuery(sqlText);
		String nval="";
		if (rs != null){
			while (rs.next()){
				nval = Integer.toString(rs.getInt("nextval"));
      			}
    		}

		// make sure nextval (oid) does NOT exist in orders table
		sqlText = "SELECT * FROM orders WHERE oid = " + nval;
		rs = sql.executeQuery(sqlText);
		if ((rs != null) & (rs.next() == true)) {
			return -1;
		}
		
		String date = "to_date('"+curr_date+"', 'YYYY-MM-DD')";
			
            	sqlText = "INSERT INTO orders(\"oid\", \"cid\", \"pid\", \"odate\", \"shipwid\", \"quantity\", \"price\", \"status\") " +
                       "VALUES ("+nval+", "+cid+", "+pid+", "+date+", "+wid+", "+qty+", "+price+", 'O')";
		sql.executeUpdate(sqlText);
		return Integer.parseInt(nval);


	} catch (SQLException e) {
		e.printStackTrace();
		return -1;
	}
  }

  public boolean cancelOrder(int oid) {

	try {
		// make sure oid exists in orders table
		sqlText = "SELECT * FROM orders WHERE oid = " + oid;
		rs = sql.executeQuery(sqlText);
		if ((rs != null) & (rs.next() == false)) {
			return false;
		}

		// make sure existing oid is not 'S'
		sqlText = "SELECT * FROM orders WHERE oid = " + oid + " AND status = 'S'";
		rs = sql.executeQuery(sqlText);
		if ((rs != null) & (rs.next() == true)) {
			return false;
		}

		// safe to cancel oid
		sqlText = "DELETE FROM orders WHERE oid = " + oid;
		sql.executeUpdate(sqlText);

	} catch (SQLException e) {
		return false;
	}
	

   return true;
  }

  public boolean shipOrder(int oid){
	
	boolean ret = false;
	try {
		
		// make sure oid exists ans status = 'O' in orders table
		sqlText = "SELECT * FROM orders WHERE oid = " + oid +
				" AND status = 'O'";
		rs = sql.executeQuery(sqlText);
		if ((rs != null) & (rs.next() == false)) {
			return false;
		}

		// make sure enough stock in wid for existing oid
		  // get pid, wid, quantity(order)  to look up quantity in stock
		sqlText = "SELECT orders.quantity FROM orders WHERE oid = " + oid;
		rs = sql.executeQuery(sqlText);
		double ord_qty = 0;
		if (rs != null) {
			while (rs.next()) {
				ord_qty = rs.getDouble("quantity");
			}
		} else {
			return false;
		}


		sqlText = "SELECT stock.quantity, orders.pid, wid FROM stock, orders WHERE oid = " + oid;
		rs = sql.executeQuery(sqlText);
		double stock_qty = 0;
		int ord_pid = 0;
		int ord_wid = 0;
		if (rs != null) {
			while (rs.next()) {
				stock_qty = rs.getDouble("quantity");
				ord_pid = rs.getInt("pid");
				ord_wid = rs.getInt("wid");
			}
		} else {
			return false;
		}


		
		// autocommit to false
		connection.setAutoCommit(false);

		// reduce stock in wid by quantity in existing oid
		// use updateStock
		ret = updateStock(ord_pid, ord_wid, -ord_qty);

		if (ret) {
			// safe to update stock, have new_qty
			sqlText = "UPDATE orders SET status = 'S' WHERE oid = " + oid;
			sql.executeUpdate(sqlText);
		}

		// commit changes
		connection.commit();
	} catch (SQLException e) {
		// rollback
		try {
			connection.rollback();
		} catch (SQLException e2) {
			return false;
		}
	} finally {
		// autocommit back to true
		try {
			connection.setAutoCommit(true);
			return ret;
		} catch (SQLException e3) {
			return false;
		}
	}
  }
  
  public String listStock(int pid){
	try {
		String qty = "";
		// make sure pid in stock
		sqlText = "SELECT * FROM stock WHERE pid = " + pid;
		rs = sql.executeQuery(sqlText);
		if ((rs != null) & (rs.next() == false)) {
			return "";
		}

		sqlText = "SELECT quantity AS q, wid AS w FROM stock WHERE pid = " +
				pid + " AND quantity > 0 ORDER BY quantity DESC";
		rs = sql.executeQuery(sqlText);
		String result = "";
		while (rs.next()) {
			qty = ("".format("%.2f",rs.getDouble("q")));
			result += rs.getInt("w")
				+ ":" + qty + "#";
		}
		return result.isEmpty() ? result : result.substring(0, result.length() - 1);
	} catch (SQLException e) {
		e.printStackTrace();
		return "";
	}
  }
  
  public String listReferrals(int cid){
	try {
		// check if cid exists in customer
		sqlText = "SELECT * FROM customer WHERE cid = " + cid;
		rs = sql.executeQuery(sqlText);
		if ((rs != null) & (rs.next() == false)) {
			return "";
		}

		// check if referrals exist for cid
		sqlText = "SELECT * FROM referral WHERE custid = " + cid;
		rs = sql.executeQuery(sqlText);
		if ((rs != null) & (rs.next() == false)) {
			return "";
		}

		sqlText = "SELECT custref, cname FROM customer, referral WHERE custref = cid " +
			"AND custid = " + cid + " ORDER BY cname ASC";
		rs = sql.executeQuery(sqlText);
		String result = "";
		while (rs.next()) {
			result += rs.getInt("custref")
				+ ":" + rs.getString("cname") + "#";
		}
		return result.isEmpty() ? result : result.substring(0, result.length() - 1);
	} catch (SQLException e) {
		e.printStackTrace();
	return "";
	}
  }
    
  public boolean updateDB(){
	try {
		String sub_quer = "SELECT pid, round((quantity * price), 2) AS sale "
				+ "FROM orders "
				+ "WHERE status = 'S'";

		String quer = "SELECT sub.pid, sum(sale) AS sales "
				+ "FROM (" + sub_quer + ") AS sub "
				+ "GROUP BY sub.pid";

		String main_quer = "SELECT pid, sales "
				+ "FROM (" + quer + ") AS quer "
				+ "WHERE sales > 10000";

		sql = connection.createStatement();
		
		

		String table = "CREATE TABLE bestsellers (pid int, sales numeric(10, 2))";
		sql.executeUpdate(table);
		rs = sql.executeQuery(main_quer);

		String insert = "INSERT INTO bestsellers VALUES (?, ?)";
		ps = connection.prepareStatement(insert);
		while (rs.next()) {
			ps.setInt(1, rs.getInt("pid"));
			ps.setDouble(2, rs.getDouble("sales"));
			ps.executeUpdate();
		}
		return true;
		
	} catch (SQLException e) {
		e.printStackTrace();
		return false;
	}
  }
  
}
