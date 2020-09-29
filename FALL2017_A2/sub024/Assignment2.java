import java.sql.*;
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
  }
  
  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password){
      try {
      Class.forName("org.postgresql.Driver");
      } catch (ClassNotFoundException e) {
	  return false;
      }

      try {
	  connection = DriverManager.getConnection(URL, username, password);
	  sql = connection.createStatement();
	  sql.executeUpdate("SET search_path TO a2");
	  return true;
      } catch (SQLException se) {
	  System.out.println(se.getMessage());
	  return false;
      }
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
      if (connection != null) {
	  try {
	      connection.close();
	      return true;
	  } catch (SQLException se) {
	      System.out.println(se.getMessage());
	      return false;
	  }
      }
      return false;
  }
    
  public boolean insertStock(int id, int wid, double qty) {
      try {
	  String qString = "INSERT INTO stock VALUES("+ id + "," + wid + "," +qty+")";
	  sql = connection.createStatement();
	  sql.executeUpdate(qString);
	  System.out.println("insert successful");
	  return true;
      } catch (SQLException se) {
	  System.out.println("insert unsuccessful");
	  System.out.println(se.getMessage());
	  return false;
      }
  }
  
   public boolean updateStock(int id, int wid, double qty) {
       try {
	   String qString = "SELECT * FROM stock WHERE pid = "
				+ id + " AND wid = " + wid;
	   ps = connection.prepareStatement(qString);
	   rs = ps.executeQuery();
	   while (rs.next()) {
	       int quantity = rs.getInt("quantity");
	       if (qty < 0 && Math.abs(qty) > quantity) {
		   return false;
	       }
	       qString = "UPDATE stock SET quantity = ? WHERE pid = ? AND wid = ?";
	       ps = connection.prepareStatement(qString);
	       ps.setDouble(1, qty);
	       ps.setInt(2, id);
	       ps.setInt(3, wid);
	       ps.executeUpdate();
	       return true;
	   }
	   return false;
       } catch (SQLException se) {
	  System.out.println(se.getMessage());
	   return false;
       }
  }
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
      try {
	  java.sql.Date sqlDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
	  String qString = "SELECT nextval('orders_oid_seq');";
	  sql = connection.createStatement();
	  rs = sql.executeQuery(qString);
	  String nval = "";
	  if (rs != null) {
	      while (rs.next()) {
		  nval = Integer.toString(rs.getInt("nextval"));
	      }
	  }
	  qString = "INSERT INTO orders VALUES(" + nval + "," + cid + "," + pid
	      + ",'" + sqlDate + "'," + wid + "," + qty + "," + price + ", 'O')";
	  sql = connection.createStatement();
	  sql.executeUpdate(qString);
	  return Integer.parseInt(nval);
      } catch (SQLException se) {
	  System.out.println(se.getMessage());
	  return -1;
      }
  }

  public boolean cancelOrder(int oid) {
      try {
	  String qString = "SELECT oid FROM orders WHERE oid = ? AND status = 'O'";
	  ps = connection.prepareStatement(qString);
	  ps.setInt(1,oid);
	  rs = ps.executeQuery();
	  if (!rs.next()) {
	      return false;
	  }
	  qString = "DELETE FROM orders WHERE oid = ?";
	  ps = connection.prepareStatement(qString);
	  ps.setInt(1,oid);
	  ps.executeUpdate();
	  return true;
      } catch (SQLException se) {
	  System.out.println(se.getMessage());
	  return false;
      }
  }

  public boolean shipOrder(int oid){
      try {
	  String qString = "SELECT * FROM orders WHERE oid = ?";
	  ps = connection.prepareStatement(qString);
	  ps.setInt(1,oid);
	  rs = ps.executeQuery();
	  if (!rs.next()) {
	      return false;
	  }
	  int reqStock = rs.getInt("quantity");
	  qString = "SELECT quantity FROM stock WHERE pid = ? AND wid = ?";
	  ps = connection.prepareStatement(qString);
	  ps.setInt(1,rs.getInt("pid"));
	  ps.setInt(2,rs.getInt("shipwid"));
	  rs = ps.executeQuery();
	  if (!rs.next() || rs.getInt("quantity") < reqStock) {
	      return false;
	  }

	  qString = "UPDATE orders SET status = 'S' WHERE oid = ?";
	  ps = connection.prepareStatement(qString);
	  ps.setInt(1,oid);
	  ps.executeUpdate();
	  return true;
      } catch (SQLException se) {
	  System.out.println(se.getMessage());
	  return false;
      }
  }
  
  public String listStock(int pid){
      try {
	  String ret = "";
	  String qString = "SELECT wid, quantity FROM stock WHERE pid = ? AND quantity > 0 ORDER BY quantity DESC";
	  ps = connection.prepareStatement(qString);
	  ps.setInt(1,pid);
	  rs = ps.executeQuery();
	  if (!rs.next()) {
	      return ret;
	  }
	  ret += Integer.toString(rs.getInt("wid"));
	  ret += ":" + Integer.toString(rs.getInt("quantity"));
	  while (rs.next()) {
	      ret += "#" + Integer.toString(rs.getInt("wid"));
	      ret += ":" + Integer.toString(rs.getInt("quantity"));
	  }
	  return ret;
      } catch (SQLException se) {
	  System.out.println(se.getMessage());
	  return "";
      }
  }
  
  public String listReferrals(int cid){
      try {
	  String ret = "";
	  String qString = "SELECT custref, cname FROM referral, customer WHERE cid = custref AND custid = ? ORDER BY cname";
	  ps = connection.prepareStatement(qString);
	  ps.setInt(1,cid);
	  rs = ps.executeQuery();
	  if (!rs.next()) {
	      return ret;
	  }
	  ret += Integer.toString(rs.getInt("custref"));
	  ret += ":" + rs.getString("cname");
	  while (rs.next()) {
	      ret += "#" + Integer.toString(rs.getInt("custref"));
	      ret += ":" + rs.getString("cname");
	  }
	  return ret;
      } catch (SQLException se) {
	  System.out.println(se.getMessage());
	  return "";
      }
  }
    
  public boolean updateDB(){
      try {
	  String qString = "CREATE TABLE bestsellers" +
	      "(pid INTEGER REFERENCES product(pid) ON DELETE RESTRICT," +
	      "sales NUMERIC(10,2), PRIMARY KEY (pid))";
	  sql = connection.createStatement();
	  sql.executeUpdate(qString);
	  qString = "SELECT pid, SUM(price) AS tot FROM orders WHERE status = 'S' GROUP BY pid HAVING SUM(price) > 10000";
	  sql = connection.createStatement();
	  rs = sql.executeQuery(qString);
	  while (rs.next()) {
	      qString = "INSERT INTO bestsellers VALUES(?,?)";
	      ps = connection.prepareStatement(qString);
	      ps.setInt(1,rs.getInt("pid"));
	      ps.setDouble(2,rs.getDouble("tot"));
	      ps.executeUpdate();
	  }
	  return true;
      } catch (SQLException se) {
	  System.out.println(se.getMessage());
	  return false;
      }
  }

  
}
