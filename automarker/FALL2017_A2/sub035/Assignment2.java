import java.sql.*;
import java.util.Date;
import java.util.Calendar;
import java.text.DecimalFormat;

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
    System.out.println("-------- PostgreSQL JDBC Connection Establishing ------------");
 
    try {

        // Load JDBC driver
        Class.forName("org.postgresql.Driver");

    } catch (ClassNotFoundException e) {

        System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
        e.printStackTrace();
        return;

    }

        System.out.println("PostgreSQL JDBC Driver Registered!");
  }
  
  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password){
      try {
 			
			connection = DriverManager.getConnection(URL, username, password); 
		} catch (SQLException e) {
 
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return false;
 
		}
 
		if (connection != null) {
			System.out.println("Established connection to database!");
            return true;
		} else {
			System.out.println("Failed to make connection!");
            return false;
		}
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
      
      try{
          
		if (connection != null){
			System.out.println("Disconnected from database!");
			connection.close();
		}

		if (ps != null){
			ps.close();
		}
	
		if (rs != null){
			rs.close();
		}
	  
      }
      catch(SQLException e){
          return false;
      }
		     
      return true;    
  }
    
  public boolean insertStock(int id, int wid, double qty) {
	
	// Cannot insert negative stock
	if (qty < 0){
	return false;	
	}	
	
	try{	

	
	// Check if pid is in product table
	String checkPID = "SELECT * FROM a2.product WHERE pid = ?";
	ps = connection.prepareStatement(checkPID);
	ps.setInt(1, id);

	rs = ps.executeQuery();
	
	// pid doesn't exist in product table
	if (rs.next() == false){
	return false;
	}

	// Check if wid is in warehouse table
	String checkWID = "SELECT * FROM a2.warehouse WHERE wid = ?";
	ps = connection.prepareStatement(checkWID);
	ps.setInt(1, wid);

	rs = ps.executeQuery();
	
	// wid doesn't exist in warehouse table
	if (rs.next() == false){
		return false;
	}

	// Check to make sure pid is not in stock before inserting
	String checkPIDInStock = "SELECT * FROM a2.stock WHERE pid = ? AND wid = ?";
	ps = connection.prepareStatement(checkPIDInStock);
	ps.setInt(1, id);
	ps.setInt(2, wid);
	
	rs = ps.executeQuery();
		
	// If pid and wid together already in stock table, cannot insert
	if (rs.next()){
		return false;	
	}
	
	// Can now insert stock	
	
	String insertStockSQL = "INSERT INTO a2.stock VALUES (?, ?, ?)";

	ps = connection.prepareStatement(insertStockSQL);
	ps.setInt(1, id);
	ps.setInt(2, wid);
	ps.setDouble(3, qty);

	ps.executeUpdate();
	
	}

	catch(SQLException e){
		e.printStackTrace();
        return false;	
	}
	
	return true;
  }
  
   public boolean updateStock(int id, int wid, double qty) {
   	try{

   	// Check if pid, wid are in stock table
   	String checkInStock = "SELECT quantity FROM a2.stock WHERE pid = ? AND wid = ?";
   	ps = connection.prepareStatement(checkInStock);
   	ps.setInt(1, id);
   	ps.setInt(2, wid);
   	rs = ps.executeQuery();
   	
   	// NO matching pid and wid were found in the stock table
   	if (rs.next() == false){
   		return false;
   	}
   	
	// Cannot have quantity deduction make the new quantity less than 0
	double quantityInTable = rs.getDouble(1);
	if (qty < 0){
		if (Math.abs(qty) > quantityInTable){
			return false;   		
		}  	
	}
   	
   	
   	// Can now update stock
   	double newQuantity = quantityInTable += qty;
   	String updateStock = "UPDATE a2.stock SET quantity = ? WHERE pid = ? AND wid = ?";
   	ps = connection.prepareStatement(updateStock);
   	ps.setDouble(1, newQuantity);
   	ps.setInt(2, id);
   	ps.setInt(3, wid);
   	
   	ps.executeUpdate();
   	
   	}
   	catch(SQLException e){
		e.printStackTrace();
   		return false;
   	
   	}

	return true;
  }
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price){

	int resultOID = 0;   	
	try{

   
   // Check if cid is in customer table
	String checkCID = "SELECT * FROM a2.customer WHERE cid = ?";
	ps = connection.prepareStatement(checkCID);
	ps.setInt(1, cid);

	rs = ps.executeQuery();
	
	// cid doesn't exist in customer table
	if (rs.next() == false){
	return -1;
	}
	
	// Check if pid is in product table
	String checkPID = "SELECT * FROM a2.product WHERE pid = ?";
	ps = connection.prepareStatement(checkPID);
	ps.setInt(1, pid);

	rs = ps.executeQuery();
	
	// pid doesn't exist in product table
	if (rs.next() == false){
	return -1;
	}

	// Check if wid is in warehouse table
	String checkWID = "SELECT * FROM a2.warehouse WHERE wid = ?";
	ps = connection.prepareStatement(checkWID);
	ps.setInt(1, wid);

	rs = ps.executeQuery();
	
	// wid doesn't exist in warehouse table
	if (rs.next() == false){
	return -1;
	}
	
	// Add order to orders table
	Date date = new Date();
	java.sql.Date sqlDate = new java.sql.Date(date.getTime());
	
	String insertOrder = "INSERT INTO a2.orders (cid, pid, odate, shipwid, price, quantity, status) VALUES(?, ?, ?, ?, ?, ?, ?)";
	ps = connection.prepareStatement(insertOrder);
	ps.setInt(1, cid);
	ps.setInt(2, pid);
	ps.setDate(3, sqlDate);
	ps.setInt(4, wid);
	ps.setDouble(5, price);
	ps.setDouble(6, qty);
	ps.setString(7, "O");
	
	ps.executeUpdate();
	
	// Grab the oid of the tuple added into orders
	sql = connection.createStatement();
	String grabOID = "SELECT currval(pg_get_serial_sequence('a2.orders', 'oid'))";
	rs = sql.executeQuery(grabOID);

	// Grabs the oid
	if (rs.next() == true){
		resultOID = rs.getInt(1);
	}
	
	
	}
	
	catch(SQLException e){
		e.printStackTrace();
		return -1;
	}
	
	return resultOID; 
   
   
  }

  public boolean cancelOrder(int oid) {
		try{

		// Finds the tuple with the provided oid, if it exists
		String findOID = "SELECT * FROM a2.orders WHERE oid = ?";		
		ps = connection.prepareStatement(findOID);
		ps.setInt(1, oid);
		rs = ps.executeQuery();
		
		// No match for oid
		if (rs.next() == false){
			return false;
		}

		rs = ps.executeQuery();

		// Order is shipped, cannot cancel
		if (rs.next() == true && rs.getString("status").equals("S")){
			return false;
		}

		rs = ps.executeQuery();
	
		// Can remove the order now

		String removeOrder = "DELETE FROM a2.orders WHERE oid = ?";
		ps = connection.prepareStatement(removeOrder);
		ps.setInt(1, oid);
		ps.executeUpdate();
		
		}

		catch(SQLException e){
			e.printStackTrace();
   			return false;
		}

		return true;
  }

  public boolean shipOrder(int odi){
		int oid = odi; 
		
		try{


		// Check if oid is valid in orders table
		String checkOID = "SELECT pid, shipwid, quantity, status FROM a2.orders WHERE oid = ?";
		ps = connection.prepareStatement(checkOID);
		ps.setInt(1, oid);
		
		rs = ps.executeQuery();
		
		// No matching oid, cannot attempt to ship order
		if (rs.next() == false){

			return false;
		}

		
		// We have an order, check if it has already been shipped
		if (rs.getString(4).equals("S")){

			return false;
		}

		boolean successfulStockUpdate = updateStock(rs.getInt(1), rs.getInt(2), (-1*rs.getDouble(3)));
		connection.setAutoCommit(false); // Turn off autocommit until both status is changed and stock is updated

		// Update failed, don't continue with commit	
		if (successfulStockUpdate == false){

			return false;
		}
		
		// Stock update occurred, try and change status
		String updateStatus = "UPDATE a2.orders SET status = 'S' WHERE oid = ?";
		ps = connection.prepareStatement(updateStatus);
		ps.setInt(1, oid);
		
		ps.executeUpdate();
		
		// Status updated correctly, manually commit changes
		connection.commit();
		connection.setAutoCommit(true);
			
		}   
		
		catch(SQLException e){

			e.printStackTrace();
			return false;        
		}
	
		return true;
  }
  
  public String listStock(int pid){

		String listOfStock = "";		
		try{		

	
		// Check if there are any rows with quantity > 0
		String checkQuantity = "SELECT wid, quantity FROM a2.stock WHERE pid = ? AND quantity > 0 ORDER BY quantity DESC";
		ps = connection.prepareStatement(checkQuantity);
		ps.setInt(1, pid);
		
		rs = ps.executeQuery();
		
		// No rows with quantity > 0		
		if (rs.next() == false){
		return "";
		}	
		
		rs = ps.executeQuery();	

		// Create list of stock string
		while (rs.next()){
		listOfStock += (Integer.toString(rs.getInt(1)) + ":");
		// Format the quantity to two decimal places
		DecimalFormat df = new DecimalFormat("######0.00");
		String roundedQuantity = df.format(rs.getDouble(2));
		
		listOfStock += roundedQuantity + "#";
		}
		
		listOfStock = listOfStock.substring(0, listOfStock.length() - 1); // Removes last '#'

		}
		catch(SQLException e){
			e.printStackTrace();
			return "";
		}
		return listOfStock;
  }
  
  public String listReferrals(int cid){
		String listOfReferrals = "";		
		try{		
		

		// Check if customer with the given cid referred anyone
		String checkCID = "SELECT customer.cid, customer.cname FROM a2.referral, a2.customer WHERE a2.referral.custid = ? AND a2.referral.custref = a2.customer.cid ORDER BY a2.customer.cname ASC";
		ps = connection.prepareStatement(checkCID);
		ps.setInt(1, cid);
		rs = ps.executeQuery();

		// If no referrals by the customer with given cid
		if (rs.next() == false){
			return "";
		}
	
		rs = ps.executeQuery();
		
		// Create list of referrals string
		while (rs.next()){
			listOfReferrals += (Integer.toString(rs.getInt(1)) + ":");
			listOfReferrals += (rs.getString(2) + "#");
		}
		
		listOfReferrals = listOfReferrals.substring(0, listOfReferrals.length() - 1); // Removes last '#'
		
		}
	
		catch(SQLException e){
			return "";
		}
	
    	return listOfReferrals;
  }
    
  public boolean updateDB(){
		try{
			sql = connection.createStatement();
			
			sql.executeUpdate("DROP TABLE IF EXISTS a2.bestsellers");
			
			// Create bestsellers table 
			sql.executeUpdate("CREATE TABLE a2.bestsellers(pid INTEGER, sales NUMERIC(10, 2))");			

			// Grab all of the products with best sellers 
			String checkBestsellers = "SELECT pid, sum(price*quantity) AS sales FROM a2.orders WHERE status = 'S' AND (price * quantity) > 10000 GROUP BY pid";
			rs = sql.executeQuery(checkBestsellers);

			// Insert into best sellers
			PreparedStatement insert = connection.prepareStatement("INSERT INTO a2.bestsellers VALUES(?, ?)");
			while (rs.next()){
				insert.setInt(1, rs.getInt(1));
				insert.setDouble(2, rs.getDouble(2));
				insert.executeUpdate();
			}						
						
		}
		catch(SQLException e){
		e.printStackTrace();	
		return false;    

		}

		return true;
  }
  
}
