


import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
	Assignment2(){
		try {
 			// Load JDBC driver
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			//System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
			//e.printStackTrace();
			return;
		}
	}
  
	//Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
	public boolean connectDB(String URL, String username, String password){
  		try {
  			connection = DriverManager.getConnection(URL, username, password);
  			if (connection == null) {
  				return false;
  			}
  			return true;
  		}
  		catch (SQLException Err) {
  			return false;
  		}
	}
  
  	//Closes the connection. Returns true if closure was sucessful
  	public boolean disconnectDB() {
  		try {
			ps.close();
			rs.close();
  			if (connection != null) {
	  			connection.close();
	  			return true;
  			}
  		}
  		catch (SQLException e) {
  			return false;
  		}
  		return false;
  	}
    
  	public boolean insertStock(int id, int wid, double qty) {
  		//String idCheck = "SELECT pid FROM stock WHERE pid=?";
  		
  		if (qty >= 0) {
			String insert = "INSERT INTO stock VALUES (?, ?, ?)";
			try {
				ps = connection.prepareStatement(insert);
				ps.setInt(1, id);
				ps.setInt(2, wid);
				ps.setDouble(3, qty);
				//System.out.println(ps.toString());
				if (ps.executeUpdate() == 1) {
					return true;
				}
			}
			catch (SQLException e) {
				//System.out.println(e);
				return false;
			}
  		}
  		return false;
  	}
  	
  	public double stockAvailable(int pid, int wid, double qty) {
  		String sqlString = "SELECT quantity FROM stock WHERE pid = ? AND wid = ?";
  		try {
   			ps = connection.prepareStatement(sqlString);
   			ps.setInt(1, pid);
   			ps.setInt(2, wid);
   			rs = ps.executeQuery();
   			
   			if (rs == null) {
   				return -1;
   			}
   			
   			if (rs.next()) {
   				double quantity = rs.getDouble(1);
   				if (Math.abs(qty) > quantity) {
   					return -1;
   				}
   				return quantity;
   			}
  		}
  		catch (SQLException e) {
   			return -1;
   		}
  		return -1;
  	}
  
   	public boolean updateStock(int id, int wid, double qty) {
   		String sqlString;
   		double quantity;
   		if ((quantity = stockAvailable(id, wid, qty)) == -1) {
   			return false;
   		}
   		qty += quantity;
   		
   		sqlString = "UPDATE stock SET quantity = ? WHERE pid = ? AND wid = ?";
   		try {
   			ps = connection.prepareStatement(sqlString);
   			ps.setDouble(1, qty);
   			ps.setInt(2, id);
   			ps.setInt(3, wid);
   			
   			if (ps.executeUpdate() == 1) {
   				return true;
   			}
   		}
   		catch (Exception e) {
   			return false;
   		}
   		
   		return false;
  	}
   
  	public int insertOrder(int cid, int pid, int wid, double qty, double price){
   		String sqlString;
   		try {
   			//check if pid and wid is in stock
   		//	sqlString = "SELECT pid FROM stock WHERE pid = ? AND wid = ?";
   			sqlString = "INSERT INTO orders (cid, pid, odate, shipwid, quantity, price, status) VALUES (?, ?, ?::date, ?, ?, ?, ?)";
   			ps = connection.prepareStatement(sqlString);
   			
   			ps.setInt(1, cid);
   			ps.setInt(2, pid);
   			
   			String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
   			ps.setString(3, timeStamp);
   			
   			ps.setInt(4, wid);
   			ps.setDouble(5, qty);
   			ps.setDouble(6, price);
   			ps.setString(7, "O");
   			
   			if (ps.executeUpdate() == 1) {
   				sqlString = "SELECT CURRVAL(pg_get_serial_sequence('orders', 'oid'))";
   				sql = connection.createStatement(); 
   	  			rs = sql.executeQuery(sqlString);
   	  			if(rs != null) {
   	  				if (rs.next()) {
   	  					return rs.getInt(1);
   	  				}
   	  			}
   			}
   		}
   		catch (SQLException e) {
   			//System.out.println(e);
   			return -1;
   		}
   		
   		return -1;
  	}

  	public boolean orderStatus(int oid) {
  		String sqlString;
  		try {
  			sqlString = "SELECT status FROM orders WHERE oid = ?";
  			ps = connection.prepareStatement(sqlString);
  			ps.setInt(1, oid);
  			
  			rs = ps.executeQuery();
  			
  			if (rs == null) {
  				return false;
  			}
  			
  			if (rs.next()) {
  				if (rs.getString(1).equals("O")) {
  					return true;
  				}
  			}
  		} 
  		catch (Exception e) {
  			//System.out.println(e);
  			return false;
  		}
  		return false;
  	}
  	
  	public boolean setCommit() {
  		try {
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			return false;
		}
  		return true;
  	}
  	
  	public boolean cancelOrder(int oid) {
  		String sqlString;
  		try {
			if (this.orderStatus(oid)) {
				sqlString = "DELETE FROM orders WHERE oid = ?";
				ps = connection.prepareStatement(sqlString);
				ps.setInt(1, oid);
				if (ps.executeUpdate() == 1) {
					return true;
				}
			}
  		}
  		catch (SQLException e) {
  			//System.out.println(e);
  			return false;
  		}
  		return false;
  	}

  	public boolean shipOrder(int oid) {
  		String sqlStatement;
  		int pid, wid;
  		double quantity;
  		double qty;
  		boolean statusOrder = false;
  		
  		sqlStatement = "SELECT pid, shipwid, quantity FROM orders WHERE oid = ? AND status = 'O'";
  		try {
  			ps = connection.prepareStatement(sqlStatement);
  			ps.setInt(1, oid);
  			rs = ps.executeQuery();
  			
  			if (rs == null) {
  				return false;
  			}
  			
  			if (rs.next()) {
  				pid = rs.getInt(1);
  				wid = rs.getInt(2);
  				qty = rs.getDouble(3);
  			}
  			else {
  				return false;
  			}
  		}
  		catch (SQLException e) {
  			return false;
  		}
  		
  		qty *= -1;
  		
  		if ((quantity = stockAvailable(pid, wid, qty)) == -1) {
   			return false;
   		}
  		
  		statusOrder = this.orderStatus(oid);
  		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			//System.out.println(e);
			return false;
		}
  		
  		if (!this.updateStock(pid, wid, qty)) { //have enough stock and if so change stock
  			this.setCommit();
  			return false;
  		}
  		if (statusOrder) { //is status order = 'O'
  			sqlStatement = "UPDATE orders SET status = 'S' WHERE oid = ?";
  			try {
				ps = connection.prepareStatement(sqlStatement);
				ps.setInt(1, oid);
	  			int affected = ps.executeUpdate();
	  			connection.commit();
	  			if (affected == 1) {
	  				this.setCommit();
	  				return true;
				}
			} catch (SQLException e) {
				this.setCommit();
				return false;
			}
  		}
  		this.setCommit();
   		return false;        
  	}
  
  	public String listStock(int pid){
		String result = "";
		String sqlQuery = "SELECT wid, quantity FROM stock "
				+ "WHERE pid = ? AND quantity > 0"
				+ "ORDER BY quantity DESC";
		try {
			ps = connection.prepareStatement(sqlQuery);
			ps.setInt(1, pid);
			rs = ps.executeQuery();
			NumberFormat formatter = new DecimalFormat("#0.00"); 
			
			if (rs == null) {
				return "";
			}
			
			while (rs.next()) {
				result += rs.getInt(1) + ":" + formatter.format(rs.getDouble(2)) + "#";
			}
		}
		catch (Exception e) {
			return "";
		}
		
		if (result.length() > 0) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
  	}
  
  	public String listReferrals(int cid){
  		String result = "";
  		String sqlStatement = "SELECT cid, cname FROM referral"
  				+ " JOIN customer ON customer.cid = referral.custref"
  				+ " WHERE referral.custid = ?"
  				+ " ORDER BY cname";
  		try {
  			ps = connection.prepareStatement(sqlStatement);
  			ps.setInt(1, cid);
  			rs = ps.executeQuery();
  			
  			if (rs == null) {
  				return "";
  			}
  			
  			while (rs.next()) {
  				result += rs.getInt(1) + ":" + rs.getString(2) + "#";
  			}
  		}
  		catch (SQLException e) {
  			return "";
  		}
  		
  		if (result.length() > 0) {
			result = result.substring(0, result.length() - 1);
		}
  		
    	return result;
  	}
    
  	public boolean updateDB(){
  		String sqlStatement = "DROP TABLE IF EXISTS bestsellers"; 
		
  		try {
  			//System.out.println("test12321");
  			sql = connection.createStatement(); 
			
			sql.executeUpdate(sqlStatement);

			sqlStatement = "CREATE TABLE bestsellers ("
  				+ "pid INT,"
  				+ "sales NUMERIC(10, 2)"
  				+ ")";
  			sql.executeUpdate(sqlStatement);
			
			sqlStatement = "SELECT pid, SUM(quantity * price) AS sales "
					+ "FROM orders "
					+ "WHERE status = 'S' "
					+ "GROUP BY pid "
					+ "HAVING SUM(quantity * price) > 10000.00";
			rs = sql.executeQuery(sqlStatement);
			sqlStatement = "INSERT INTO bestsellers VALUES ";
			
			int count = 0;
			
			if (rs == null) {
				return false;
			}
			
			if (rs != null) {
				while (rs.next()) {
					if (count != 0) {
						sqlStatement += ", ";
					}
					sqlStatement += "(" + rs.getInt(1) + ", "  +  rs.getDouble(2) + ")";
					count++;
				}
				sql.execute(sqlStatement);
			}
			return true;
		} catch (SQLException e) {
			//System.out.println(e);
			return false;
		}
  	}
}



