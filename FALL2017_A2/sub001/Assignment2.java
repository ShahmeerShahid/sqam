import java.sql.*;
import java.util.Date;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
  public Assignment2() {}
  
  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password){


	//Load JDBC Driver
	try {
		Class.forName("org.postgresql.Driver");
	} catch (ClassNotFoundException e) {
		return false;
	}
	
	
	//Connect to database
	try {
                        connection = DriverManager.getConnection(URL , username, password);
			ps = connection.prepareStatement("set search_path to a2");
        		ps.execute();
        		ps.close();

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
	
	if (connection != null) {
		try {
			connection.close();
		} catch (SQLException e) {
			return false;
		}
      		return true;
	}
	return false;    
  }

  
  public boolean insertStock(int id, int wid, double qty) {

	if (qty < 0) {
		return false;
	}

	try {	
		// insert a tuple into stock
		String insert = "INSERT INTO a2.stock VALUES (?,?,?)";
		ps = connection.prepareStatement(insert);
		ps.setInt(1, id);
		ps.setInt(2, wid);
		ps.setDouble(3, qty);
		ps.executeUpdate();
		ps.close();
	
	} catch (SQLException e) {
		return false;
	}
	
	return true;
  }
  
   public boolean updateStock(int id, int wid, double qty) {

	try {
		
		String query;

		//Check if id and wid are in stock
		if (qty < 0) {

			query  = "SELECT * FROM a2.stock " +
				 "WHERE pid = ? and wid = ? and quantity >= ABS(?)";
			ps = connection.prepareStatement(query);
			ps.setInt(1, id);
			ps.setInt(2, wid);
			ps.setDouble(3, qty);
			rs = ps.executeQuery();

			if (rs.next()) {
				
				//Update stock
				query = "UPDATE a2.stock SET quantity = quantity + ? " +
					"WHERE pid = ? and wid = ?";

				ps = connection.prepareStatement(query);
				ps.setDouble(1, qty);
				ps.setInt(2, id);
				ps.setInt(3, wid);
				ps.executeUpdate();

				return true;

			} else {
				return false;
			}

		} else {

			query = "SELECT * FROM a2.stock WHERE pid = ? and wid = ?";			

			ps = connection.prepareStatement(query);
			ps.setInt(1, id);
			ps.setInt(2, wid);
			rs = ps.executeQuery();

			if (rs.next()) {
				
				//Update stock				
				query = "UPDATE a2.stock SET quantity = ? " +
                                        "WHERE pid = ? and wid = ?";

                                ps = connection.prepareStatement(query);
                                ps.setDouble(1, qty);
                                ps.setInt(2, id);
                                ps.setInt(3, wid);
				ps.executeUpdate();

                                return true;
					
			} else {
				return false;
			}

		}
		

	} catch (SQLException e) {
		return false;
	}

  }



 
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
   
	try {
		
		String insert;
		
		//Insert tuple	
		insert = "INSERT INTO a2.orders (cid, pid, odate, shipwid, quantity, price, status) VALUES (?, ?, ?::date, ?, ?, ?, ?)";

		ps = connection.prepareStatement(insert);
		ps.setInt(1, cid);
		ps.setInt(2, pid);	
		
		Date date = new Date();
		String time = date.toString();

		ps.setString(3, time);
		ps.setInt(4, wid);
		ps.setDouble(5, qty);
		ps.setDouble(6, price);
		ps.setString(7, "O");
		ps.executeUpdate();
		
		ps.close();

		//get oid
		String query = "select currval(pg_get_serial_sequence('a2.orders','oid'))";

                ps = connection.prepareStatement(query);
                rs = ps.executeQuery();

		if (rs.next()) {
	                int oid = rs.getInt("currval");
			return oid;
		} else {
			return -1;
		}


	} catch (SQLException e) {
		return -1;
	}
		
  }

  public boolean cancelOrder(int oid) {

	try {
		String sqlText;

		sqlText = "DELETE FROM a2.orders WHERE oid = ? and status = 'O'";

		ps = connection.prepareStatement(sqlText);
		ps.setInt(1, oid);
		ps.executeUpdate();
		ps.close();
	
	} catch (SQLException e) {
		return false;
	}
   
	return true;
  }

  public boolean shipOrder(int oid){

	try {

		String query = "SELECT * FROM a2.orders WHERE oid = ? and status = 'O'";

		ps = connection.prepareStatement(query);
		ps.setInt(1, oid);
		rs = ps.executeQuery();

		if (rs.next()) {

			int pid = rs.getInt("pid");
			int wid = rs.getInt("shipwid");
			double qty = rs.getDouble("quantity");

			rs.close();
			ps.close();

			connection.setAutoCommit(false);
				
			if (updateStock(pid, wid, -qty)) {
				
				String update = "UPDATE a2.orders SET status = 'S' WHERE oid = ? ";

				ps = connection.prepareStatement(update);
				ps.setInt(1, oid);
				ps.executeUpdate();
				connection.commit();
			
			} else {
				return false;
			}


	
		} else {
			return false;
		}
        
	} catch (SQLException e) {

		return false;
	
	} finally {

		try { 
			connection.setAutoCommit(true);
			rs.close();
			ps.close();
		} catch (SQLException e) {
		}
	}
	
	return true;
  }
  
  public String listStock(int pid){
	
	String list = "";

	try { 
		String query;

		query = "SELECT * FROM a2.stock WHERE pid = ? AND quantity > 0 ORDER BY quantity DESC";

		ps = connection.prepareStatement(query);
		ps.setInt(1, pid);
		rs = ps.executeQuery();
		
		while (rs.next()) {
			
			list += String.valueOf(rs.getInt("wid")) + ":";

			BigDecimal bd = new BigDecimal(rs.getDouble("quantity"));
			bd = bd.setScale(2, RoundingMode.CEILING);

			list += bd.toString() + "#";

		}

		list = list.substring(0, list.length() - 1);
		rs.close();
		ps.close();
		
	} catch (SQLException e) {
	}

	return list;
  }
  
  public String listReferrals(int cid){

	String list = "";

	try {

		String query = "SELECT * FROM a2.referral JOIN a2.customer ON custref = cid WHERE custid = ?";

		ps = connection.prepareStatement(query);
		ps.setInt(1, cid);
		rs = ps.executeQuery();
		
		String refcid;
		String name;

		while (rs.next()) {
			
			refcid = String.valueOf(rs.getInt("custref"));
			name = rs.getString("cname");
			list += refcid + ":" + name + "#";
			
		}

		list = list.substring(0, list.length() - 1);
		rs.close();
		ps.close();

	} catch (SQLException e) {
	}

	return list;
  }
    
  public boolean updateDB(){

	try {

		String table = "DROP TABLE IF EXISTS bestsellers"; 
		ps = connection.prepareStatement(table);
		ps.executeUpdate();


		table = "CREATE TABLE bestsellers (pid INTEGER NOT NULL, sales NUMERIC(10,2), PRIMARY KEY (pid));";		

	
		ps = connection.prepareStatement(table);
		ps.execute();		

		String query = "SELECT pid, SUM(quantity * price) AS sales FROM a2.orders WHERE status = 'S' GROUP BY pid HAVING SUM(quantity * price) > 10000";
		PreparedStatement ps1;
		ps1 = connection.prepareStatement(query);
		rs = ps1.executeQuery();
		
		while (rs.next()) {

			int pid = rs.getInt("pid");
			double sales = rs.getDouble("sales");

			String insert = "INSERT INTO bestsellers VALUES (?, ?)";
			
			ps = connection.prepareStatement(insert);
			ps.setInt(1, pid);
			ps.setDouble(2, sales);
			ps.executeUpdate();

		}

		rs.close();
		ps.close();

	} catch (SQLException e) {
		return false;
	} 


	
	return true;    
  }
  
}














