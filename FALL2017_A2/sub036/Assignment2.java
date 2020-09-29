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
	  connection = DriverManger.getConnection(URL, username, password);
	  if (connection == null){
      	  return false;
      	  }
	  else {
		  return true; 
		  }
		  
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
	  connection.close();
	  if(connection == null){
		  return true;
	  }
	  else{
      return false;
      }    
  }
    
  public boolean insertStock(int pid, int wid, double qty) {
	  int pcount;
	  int wcount;
	  if(qty < 0){
		  return false;
	  }
	  PreparedStatement ps1;
	  ResultSet rs1;
	  String q1 = ("SELECT COUNT(?) AS c FROM stock");
	  ps1 = connection.prepareStatement(q1);
	  ps1.setInt(1, pid);
	  rs1 = ps.executeQuery();
	  rs1.next();
	  else if(rs1.getInt("c") != 0){
		  return false;
	  }
	  PreparedStatement ps2;
	  ResultSet rs2;
	  String q2 = ("SELECT COUNT(?) AS c FROM stock");
	  ps2 = connection.prepareStatement(q2);
	  ps2.setInt(1, wid);
	  rs2 = ps.executeQuery();
	  rs2.next();
	  else if(rs2.getInt("c") == 0){
		  return false;
	  }
	
	  else{
	  String quary = "INSERT INTO stock(?, ?, ?)";
	  ps = connection.prepareStatement(quary);
	  ps.setInt(1, pid);
	  ps.setInt(2, wid);
	  ps.setDouble(3, qty);
	  ps.execute();
	  return true;
  }
  
   public boolean updateStock(int pid, int wid, double qty) {
	   String quary = "UPDATE  stock SET quantity = ? WHERE pid = ? wid = ?";
	   ps = connection.prepareStatement(quary);
	   ps.setDouble(1, qty);
	   ps.setInt(2, pid);
	   ps.setInt(3, wid);
	   return ps.execute();
	   
	   
  }
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
   return -1;
  }

  public boolean cancelOrder(int oid) {
	  PreparedStatement ps1;
	  ResultSet rs1;
	  String q1 = ("SELECT status FROM orders WHERE oid = ?");
	  ps1 = connection.prepareStatement(q1);
	  ps1.setInt(1, oid);
	  rs1 = ps.executeQuery();
	  rs1.next();
	  if(rs1.getString("status") == "S"){
		  return false;
	  }
	  else{
		  String quary = "DELETE FROM orders WHERE oid = ?";
		  ps = connection.prepareStatement(quary);
		  ps.setInt(1, oid);
		  ps.execute();
		  return true;
	  
	  }	
   
  }

  public boolean shipOrder(int odi){
   return false;        
  }
  
  public String listStock(int pid){
	  String result = "";
	  String quary = "SELECT wid,quantiry FROM stock WHERE pid = ? ORDER BY quantity DESC";
	  ps = connection.prepareStatement(quary);
	  ps.setInt(1, pid);
	  rs = ps.executeQuery();
	  while (rs.next()){
		  int wid = rs.getInt("wid");
		  double qty = rs.getDouble("quantity");
		  if(qty > 0){
		  result = result + wid + ":" + qty + "#";}
		  else{}
	  }
	  return result;
  }
  
  public String listReferrals(int cid){
	  String result = "";
	  String quary = "SELECT ref.custref, cust.cname FROM referral ref JOIN customer cust ON ref.custref = cust.cid "
	  		+ "WHERE ref.custid = ? ORDER BY cust.cname ASC";
	  ps = connection.prepareStatement(quary);
	  ps.setInt(1, cid);
	  rs = ps.executeQuery();
	  while (rs.next()){
		  int cid = rs.getInt("custref");
		  String cname = rs.getString("cname");
		  if(cname != ""){
		  result = result + cid + ":" + cname + "#";}
		  else{}
	  }
	  return result; 
    return false;
  }
    
  public boolean updateDB(){
	String quary = "CREATE TABLE bestsellers"
			+ "pid INTEGER NOT NULL, sales NUMBERIC(10,2) NOT NULL"; 
	sql = connection.createStatement();
	sql.executeUpdate(quary);
	String quary2 = "INSERT INTO bestsellers (SELECT pid FROM orders WHERE quantity*price ¡·10000 GROUP BY pid HAVING status = "s")"
	ps = connection.prepareStatement(quary2);		
	return ps.execute();    
  }
  
}
