import java.sql.*;
import java.util.*;
import java.lang.*;
import java.text.*;

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

	  System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
	  e.printStackTrace();
	  return false;

	}

	System.out.println("PostgreSQL JDBC Driver Registered!");

	try {
	  String server = "jdbc:postgresql://" + URL + ":5432/" + username + "_343" + "?currentSchema=a2";

	  System.out.println("*** Please make sure to replace 'username' with your <UTorID> username in the jdbc connection string!!!");
	  connection = DriverManager.getConnection(server, username, password); 
	} catch (SQLException e) {

		System.out.println("Connection Failed! Check output console");
		e.printStackTrace();
		return false;

	}

	if (connection != null) {
		System.out.println("You made it, take control of your database now!");
		return true;
	} else {
		System.out.println("Failed to make connection!");
	}
      return false;
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
      try {
      	connection.close();
      	System.out.println("Connection closed!");
      } catch (SQLException e) {
        System.out.println("Could not disconnect from database!");
	e.printStackTrace();
	return false;
      }
      return true;    
  }
    
  public boolean insertStock(int id, int wid, double qty) {
  
      if (qty < 0) {
          return false;
      } 
      
      try {
      	//get all of the pIdS
      	ArrayList<Integer> pidList = new ArrayList<>();
      	sql = connection.createStatement(); 
      	String sqlText = "SELECT * FROM product";
      	rs = sql.executeQuery(sqlText);
      	if (rs != null){
          while (rs.next()){
              pidList.add(rs.getInt("pid"));
          }
      	}
      
      	rs.close();
      	
      	//get all of the WIdS
      	ArrayList<Integer> widList = new ArrayList<>();
      	sql = connection.createStatement(); 
      	sqlText = "SELECT * FROM warehouse";
      	rs = sql.executeQuery(sqlText);
      	if (rs != null){
          while (rs.next()){
              widList.add(rs.getInt("wid"));
          }
      	}
      
      	rs.close();
      	
      	// pid and wid are valid values
      	// INSERT into stock table
      	if (pidList.contains(id) && widList.contains(wid)) {
      	   sqlText = "INSERT INTO stock VALUES (?, ?, ?)";
      	   ps = connection.prepareStatement(sqlText);
      	   ps.setInt(1,id); 
      	   ps.setInt(2,wid); 
      	   ps.setDouble(3, qty);
      	   ps.executeUpdate();
      	}
      	
      	ps.close();
      
      } catch (SQLException e) {
        System.out.println("Could not insertStock");
	e.printStackTrace();
	return false;
      }
      return true;
  }
  
   public boolean updateStock(int id, int wid, double qty) {
      try {
      	//check if item with key pid and wid exists in Stock table
      	sql = connection.createStatement(); 
      	String sqlText = "SELECT * FROM stock";
      	rs = sql.executeQuery(sqlText);
      	if (rs != null){
          while (rs.next()){
              if (rs.getInt("pid") == id && rs.getInt("wid") == wid) {
              	if (qty < 0 && Math.abs(qty) > rs.getDouble("quantity")) {
              	  return false;
              	} else {
              	  sqlText = "UPDATE stock SET quantity = ? WHERE pid = ? AND wid = ?";
              	  ps = connection.prepareStatement(sqlText);
              	  double newQty = rs.getDouble("quantity") + qty;
              	  ps.setDouble(1,newQty); 
      	   	  ps.setInt(2, id); 
      	          ps.setInt(3, wid);
      	          ps.executeUpdate(); 	
      	          ps.close();
      	          return true;
              	}
              }
          }
      	}
      	
      
      } catch (SQLException e) {
        System.out.println("Could not updateStock");
	e.printStackTrace();
	return false;
      }
      return false;
  }
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
      try {
        //get all of the cIdS
      	ArrayList<Integer> cidList = new ArrayList<>();
      	sql = connection.createStatement(); 
      	String sqlText = "SELECT * FROM customer";
      	rs = sql.executeQuery(sqlText);
      	if (rs != null){
          while (rs.next()){
              cidList.add(rs.getInt("cid"));
          }
      	}
      
      	rs.close();
      	
      	//get all of the pIdS
      	ArrayList<Integer> pidList = new ArrayList<>();
      	sql = connection.createStatement(); 
      	sqlText = "SELECT * FROM product";
      	rs = sql.executeQuery(sqlText);
      	if (rs != null){
          while (rs.next()){
              pidList.add(rs.getInt("pid"));
          }
      	}
      
      	rs.close();
      	
      	//get all of the WIdS
      	ArrayList<Integer> widList = new ArrayList<>();
      	sql = connection.createStatement(); 
      	sqlText = "SELECT * FROM warehouse";
      	rs = sql.executeQuery(sqlText);
      	if (rs != null){
          while (rs.next()){
              widList.add(rs.getInt("wid"));
          }
      	}
      
      	rs.close();
      	
      	// pid and wid are valid values
      	// INSERT into stock table
      	if (cidList.contains(cid) && pidList.contains(pid) && widList.contains(wid)) {
      	   //get date
      	   
      	   java.util.Date today = new java.util.Date();
      	   
      	   sqlText = "INSERT INTO orders VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, 'O')";
      	   ps = connection.prepareStatement(sqlText);
      	   ps.setInt(1,cid); 
      	   ps.setInt(2,pid); 
      	   ps.setDate(3, new java.sql.Date(today.getTime()));
      	   ps.setInt(4, wid);
      	   ps.setDouble(5, qty);
      	   ps.setDouble(6, price);
      	   ps.executeUpdate();
      	   ps.close();
      	   
      	   sql = connection.createStatement(); 
      	   sqlText = "SELECT currval('orders_oid_seq')";
      	   rs = sql.executeQuery(sqlText);
      	   if (rs != null){
             while (rs.next()){
               System.out.println(rs.getInt("currval"));
               return rs.getInt("currval");
             }
      	   }
      
      	   rs.close();
      	}
      	
      
      } catch (SQLException e) {
        System.out.println("Could not insertOrder");
	e.printStackTrace();
	return -1;
      }
      return -1;
  }

  public boolean cancelOrder(int oid) {
      try {
      	//get all of the oIdS
      	ArrayList<Integer> oidList = new ArrayList<>();
      	sql = connection.createStatement(); 
      	String sqlText = "SELECT * FROM orders";
      	rs = sql.executeQuery(sqlText);
      	if (rs != null){
          while (rs.next()){
              oidList.add(rs.getInt("oid"));
          }
      	}
      
      	rs.close();
      	
      	if (oidList.contains(oid)) {
      	  sql = connection.createStatement(); 
      	  sqlText = "DELETE from orders where oid = ? AND status = 'O'";
      	  ps = connection.prepareStatement(sqlText);
      	  ps.setInt(1, oid);
      	  ps.executeUpdate();
      	} else {
      	  return false;
      	}
      } catch (SQLException e) {
        System.out.println("Could not cancel Order");
	e.printStackTrace();
	return false;
      }
      return true;
  }

  public boolean shipOrder(int oid){
      
      try {
      	sql = connection.createStatement(); 
      	String sqlText = "SELECT * FROM orders";
      	rs = sql.executeQuery(sqlText);
      	if (rs != null){
          while (rs.next()){
              if (rs.getInt("oid") == oid && rs.getString("status") == "O") {
              	int wid = rs.getInt("shipwid");
              	int pid = rs.getInt("pid");
              	double qty = rs.getDouble("quantity");
              	
              	boolean check = updateStock(wid, pid, -qty);
              	
              	if(check) {
              	  sqlText = "UPDATE orders SET status = ? WHERE oid = ?";
              	  ps = connection.prepareStatement(sqlText);
              	  ps.setString(1, "S");
              	  ps.setInt(2, oid);
      	          ps.executeUpdate(); 	
      	          ps.close();
      	          return true;
              	} 
              }
          }
      	}
      	
        rs.close();
      } catch (SQLException e) {
        System.out.println("Could not ship order!");
	e.printStackTrace();
	return false;
      }
      return false;       
  }
  
  public String listStock(int pid){
	try {
      	sql = connection.createStatement(); 
      	String sqlText = "SELECT * FROM stock ORDER BY quantity DESC";
      	rs = sql.executeQuery(sqlText);
      	String answer = "";
      	if (rs != null){
          while (rs.next()){
              if (rs.getInt("pid") == pid) {
            	 String wid = Integer.toString(rs.getInt("wid"));
                 String qty = Double.toString(rs.getDouble("quantity"));
                 answer = answer + wid + ":" + qty + "#";
              }
          }
          
          return answer.substring(0, answer.length() - 1);
      	}
      	
        rs.close();
      } catch (SQLException e) {
        System.out.println("Could not list stock!");
	e.printStackTrace();
	return "";
      }
      return "";       
  }
  
  public String listReferrals(int cid){
      try {
      	sql = connection.createStatement(); 
      	String sqlText = "CREATE view temp as (SELECT custref from referral where custid = " + Integer.toString(cid) + ")";
      	sql.executeUpdate(sqlText);
      	
      	sqlText = "SELECT cid, cname FROM customer, temp WHERE custref = cid ORDER BY cname";
      	rs = sql.executeQuery(sqlText);
      	String answer = "";
      	if (rs != null){
          while (rs.next()){
            	 String cid2 = Integer.toString(rs.getInt("cid"));
                 String cname = rs.getString("cname");
                 answer = answer + cid2 + ":" + cname + "#";
              
          }
          
          sqlText = "DROP view temp";
      	  sql.executeUpdate(sqlText);
          
          System.out.println(answer.substring(0, answer.length() - 1));
          return answer.substring(0, answer.length() - 1);
      	}
      	
        rs.close();
      } catch (SQLException e) {
        System.out.println("Could not list referral!");
	e.printStackTrace();
	return "";
      }
      return "";       
  }
    
  public boolean updateDB(){
	try {
      	sql = connection.createStatement(); 
      	String sqlText = "CREATE TABLE bestsellers(                  " +
				  "                       pid int,         " +
                	          "                       sales numeric(10, 2)  " +
                        	  "                      ) ";
      	sql.executeUpdate(sqlText);
      	
      	sqlText = "SELECT * FROM orders";
      	rs = sql.executeQuery(sqlText); 
      	if (rs != null){
          while (rs.next()){
              if (rs.getString("status").equals("S")) {
              	int pid = rs.getInt("pid");
              	double sales = rs.getDouble("quantity") * rs.getDouble("price");
              	if (sales > 10000.00) {
              	   sqlText = "INSERT INTO bestsellers VALUES (?, ?)";
      	           ps = connection.prepareStatement(sqlText);
      	           ps.setInt(1,pid); 
      	           ps.setDouble(2, sales);
      	           ps.executeUpdate();
      	           ps.close();
              	}
        
              } 
          }
          
          return true;
        }
          
      	
        rs.close();
      } catch (SQLException e) {
        System.out.println("Could not update DB!");
	e.printStackTrace();
	return false;
      }
      return false;         
  }
  
}
