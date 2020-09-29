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
  
  //CONSTRUCTOR
  Assignment2(){
  }
  
  
  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password){
	  try {	
			// Load JDBC driver
			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {
			// System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
			// e.printStackTrace();
			return false;
		}
	  
	  try {
			//Make the connection to the database, ****** but replace "username" with your username ******
			//System.out.println("*** Please make sure to replace 'username' with your <UTorID> username in the jdbc connection string!!!");
			connection = DriverManager.getConnection(URL, username, password); 
			
		} catch (SQLException e) {
			// System.out.println("Connection Failed! Check output console");
			// e.printStackTrace();
			return false;	
		} finally {
			return (connection != null);
		} 
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
	  try {
		  if (rs != null){rs.close();}
		  if (ps != null){ps.close();}
		  if (connection != null){ connection.close();}
	  } catch (SQLException e){
		  // System.out.println("Query Exection Failed!");
          // e.printStackTrace();
          return false;
	  } finally {
		  return (connection == null);
	  }
  }
    
  public boolean insertStock(int id, int wid, double qty) {
	  if (qty < 0){
		  return false;
	  } else {
		  try {
			  String sqlText;
			  sql = connection.createStatement();
			  
			  // check if id exists
			  sqlText = String.format("SELECT * FROM product WHERE pid = %d", id);
			  rs = sql.executeQuery(sqlText);
			  if (rs == null){ return false;}
			  rs.close(); // close
			  
			  // check if wid exists
			  sqlText = String.format("SELECT * FROM warehouse WHERE wid = %d", wid);
			  rs = sql.executeQuery(sqlText);
			  if (rs == null){ return false;}
			  rs.close(); // close
			  
			  // check if id, wid exists in stock
			  sqlText = String.format("SELECT * FROM stock WHERE pid = %d AND wid = %d", id, wid );
			  rs = sql.executeQuery(sqlText);
			  if (rs != null) { return false;}
			  rs.close(); // close
			  
			  //  Insert
			  sqlText = String.format("INSERT INTO stock VALUE("
			  		+ "CAST(%1$d as INTEGER),"
			  		+ "CAST(%2$d as INTEGER),"
			  		+ "CAST(%3$10.2f as NUMERIC(10,2)))", 
					  id, wid, qty);
			  sql.executeUpdate(sqlText);
			  return true;
			 
		  } catch (SQLException e) {
	          // System.out.println("Query Exection Failed!");
	          // e.printStackTrace();
	          return false;
		  }	 
	  }
  }
  
  
   public boolean updateStock(int id, int wid, double qty) {
	   try {
		   String sqlText;
		   double quantity;
		   sql = connection.createStatement();
		   
		   // check if id, wid exists in stock
		  sqlText = String.format("SELECT * FROM stock WHERE pid = %d AND wid = %d", id, wid );
		  rs = sql.executeQuery(sqlText);
		  if (rs == null) { return false;}
		  
		  // check if qty is negative and its absolute value >= quantity in the table
		  rs.next(); // get the first row
		  quantity = rs.getDouble("quantity");
		  rs.close(); //close
		  
		  quantity = quantity + qty;
		  if (quantity < 0) { return false;}
		  
		  sqlText = String.format("UPDATE stock "
		  		+ "SET quantity = CAST(%1$10.2f as NUMERIC(10,2))"
		  		+ " WHERE pid = %2$d AND wid = %3$d",
				  quantity, id, wid);
		  sql.executeUpdate(sqlText);
		  return true;
		  
	   } catch (SQLException e) {
          // System.out.println("Query Exection Failed!");
          // e.printStackTrace();
          return false;
		  }
  }
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
	  try {
		  String sqlText;
		  sql = connection.createStatement();
		  int oid;
		  
		  // check if pid exists
		  sqlText = String.format("SELECT * FROM product WHERE pid = %d", pid);
		  rs = sql.executeQuery(sqlText);
		  if (rs == null){ return -1;}
		  rs.close(); //close
		  
		  // check if cid exists
		  sqlText = String.format("SELECT * FROM costumer WHERE cid = %d", cid);
		  rs = sql.executeQuery(sqlText);
		  if (rs == null){ return -1;}
		  rs.close(); //close
		  
		  // check if wid exists
		  sqlText = String.format("SELECT * FROM warehouse WHERE wid = %d", wid);
		  rs = sql.executeQuery(sqlText);
		  if (rs == null){ return -1;}
		  rs.close(); // close
		  
		  // get latest oid and create new oid
		  rs = sql.executeQuery("SELECT currval(pg GET serial sequence(‘orders’,’oid’))");
		  rs.next(); // get values
		  oid = rs.getInt("oid");
		  rs.close(); // close
		  oid += 1; // new oid
		  
		  // Insert
		  sqlText = String.format("INSERT INTO orders VALUE("
		  		+ "CAST(%1$d as INTEGER),"
		  		+ "CAST(%2$d as INTEGER), "
		  		+ "CAST(%3$d as INTEGER),"
		  		+ "CAST(now() as DATE), "
		  		+ "CAST(%4$d as INTEGER), "
		  		+ "CAST(%5$10.2f as NUMERIC(10,2)), "
		  		+ "CAST(%6$6.2f as NUMERIC(6,2)),"
		  		+ "'O')", 
				  oid, cid, pid, wid, qty, price);
		  sql.executeUpdate(sqlText);
		  return oid;
		  
	  } catch (SQLException e) {
          // System.out.println("Query Exection Failed!");
          // e.printStackTrace();
          return -1;
	  }
  }

  public boolean cancelOrder(int oid) {
	  try{
		  String sqlText;
		  sql = connection.createStatement();
		  
		  // check if oid exists or has shiped
		  sqlText = String.format("SELECT * FROM orders WHERE oid = %d AND status = 'O'", oid);
		  rs = sql.executeQuery(sqlText);
		  if (rs != null){ return false;}
		  rs.close(); //close
		  
		  // delete order
		  sqlText = String.format("DELETE FROM orders WHERE oid = %d", oid);
		  sql.executeUpdate(sqlText);
		  return true;
		  
	  } catch (SQLException e) {
          // System.out.println("Query Exection Failed!");
          // e.printStackTrace();
          return false;
	  }
  }

  public boolean shipOrder(int oid){
	  try {
		  String sqlText, status;
		  sql = connection.createStatement();
		  int pid, wid;
		  double qty, sqty;
		  
		  // get info
		  sqlText = String.format("SELECT * FROM orders o, stock s "
		  		+ "WHERE o.oid = %d AND o.shipwid = s.wid AND s.pid = o.pid"
				  , oid);
		  rs = sql.executeQuery(sqlText);
		  if (rs == null){ return false;}
		  rs.next();
		  qty = rs.getDouble("o.quantity");
		  sqty = rs.getDouble("s.quantity");
		  status = rs.getString("o.status");
		  pid = rs.getInt("o.pid");
		  wid = rs.getInt("o.shipwid");
		  rs.close(); // close
		  
		  if (status == "S"){ return true;} 
		  if (qty > sqty) { return false;}
		  qty = 0 - qty; // quantity decrease
		  
		  // update status
		  sqlText = String.format("UPDATE orders SET status = 'S' WHERE oid = %d", oid);
		  sql.executeUpdate(sqlText);
		  return updateStock(pid,wid,qty);
		  
	  } catch (SQLException e) {
	      // System.out.println("Query Exection Failed!");
	      // e.printStackTrace();
	      return false;
	  }       
  }
  
  public String listStock(int pid){
	  try{
		  String sqlText,avaliable;
		  sql = connection.createStatement();
		  
		  // get avaliable wid
		  sqlText = String.format("SELECT * FROM warehouse WHERE pid = %d AND quantity > 0 ORDER by quantity DESC", pid);
		  rs = sql.executeQuery(sqlText);
		  if (rs == null){ return "";}
		  
		  // 
		  rs.next();
		  avaliable  = rs.getInt("wid") +":"+rs.getDouble("quantity");
		  while (rs.next()) {
			  avaliable = avaliable + "#" + rs.getInt("wid") + ":" + rs.getDouble("quantity");
		  }
		  rs.close(); // close
		  return avaliable;
		  
	  } catch (SQLException e) {
          // System.out.println("Query Exection Failed!");
          // e.printStackTrace();
          return "";
	  }
  }
  
  public String listReferrals(int cid){
	  try{
		  String sqlText,ref;
		  sql = connection.createStatement();
		  
		  sqlText = String.format(
				  "SELECT cid, cname FROM referral, customer WHERE custid = %d AND custref = cid ORDER By cname ASC"
				  ,cid);
		  rs = sql.executeQuery(sqlText);
		  if (rs == null) { return "";}
		  
		  // build String ref
		  rs.next();
		  ref = rs.getInt("cid") + ":" + rs.getString("cname");
		  while (rs.next()){
			  ref = ref + "#" + rs.getInt("cid") + ":" + rs.getString("cname");
		  }
		  
		  rs.close(); // close
		  return ref;
		  
	  } catch (SQLException e) {
          // System.out.println("Query Exection Failed!");
          // e.printStackTrace();
          return "";
	  }
  }
    
  public boolean updateDB(){
	  try{
		  String sqlText;
		  sql = connection.createStatement();
		  
		  // get pid and sales 
		  sqlText = "SELECT * "
		  		+ "FROM ("
		  		+ "SELECT pid, CAST(SUM(quantity*price) as NUMERIC(10,2)) AS sales "
		  		+ "FROM orders WHERE status = 'S' GROUP BY pid"
		  		+ ")"
		  		+ "AS newtable "
		  		+ "WHERE sales > 10000.00 ORDER BY sales DESC";
		  rs = sql.executeQuery(sqlText); // need to close
		  
		  // create new table
		  sqlText = "CREATE TABLE bestsellers ( "
		  		+ "pid INTEGER, "
		  		+ "sales NUMERIC(10,2)"
		  		+ ")";
		  sql.executeUpdate(sqlText);
		  
		  if (rs == null){ return false;}
		  
		  // INSERT
		  sqlText = "INSERT INTO bestsellers VALUES (?,?)";
		  ps = connection.prepareStatement(sqlText);
		  while (rs.next()){
			  ps.setInt(1, rs.getInt("pid"));
			  ps.setDouble(2, rs.getDouble("sales"));
			  ps.executeUpdate();
		  }
		  
		  // close
		  ps.close();
		  if (rs != null){ rs.close(); return false;}
		  rs.close();
		  return true;
		  
	  } catch (SQLException e) {
          // System.out.println("Query Exection Failed!");
          // e.printStackTrace();
          return false;
	  }    
  }
  
}
