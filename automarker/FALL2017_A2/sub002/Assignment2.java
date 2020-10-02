import java.sql.*;
import java.util.*;
import java.text.*;
import java.time.*;
import java.io.*;


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
  		System.out.println("SURPRISE!!!");
  }


  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password){
  
      try {

          connection = DriverManager.getConnection(URL, username, password);

          if (connection != null) {
              sql = connection.createStatement();
              String setPath = "SET search_path TO A2";
              sql.executeUpdate(setPath);
              return true;
              
          } else {
              return false;
          }
        }
        
      catch (SQLException e) {
        
          System.out.println("error when connecting");
          return false;
      }
  }
  

  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
  
      try{
      
          if (connection != null){
              connection.close();
          }	
      }
      
      catch (SQLException e) {
          return false;
      }
      
      return true;
  }


  public boolean insertStock(int id, int wid, double qty){
  
      try{
      
          if (qty > 0){
          
              String sqlinsert = "INSERT INTO stock VALUES (?, ?, ?);";
              
              ps = connection.prepareStatement(sqlinsert);
		  	  ps.setInt(1, id);
			  ps.setInt(2, wid);
			  ps.setDouble(3, qty);

			  int rowsEffected = ps.executeUpdate();
			  //System.out.println(rowsEffected);
			  
			  if (rowsEffected > 0) {
			      return true;
     	      }
          }   
      }
      
      catch (SQLException e) {
      
          //System.out.print("other error");
          return false;
      }
      return false;
  }


  public boolean updateStock(int pid, int wid, double qty) {
  
      String sqlText;
      
      try {
      
          String sqlqty = "SELECT quantity FROM stock WHERE pid = ? AND wid = ?;";
          
          ps = connection.prepareStatement(sqlqty);
          ps.setInt(1,pid);
          ps.setInt(2,wid);
          rs = ps.executeQuery();

          // if product not in stock table
          if (rs.next() == false) {
              return insertStock(pid, wid, qty);
          }
		  double quan = rs.getInt("quantity");
		  
		  // we don't want to have a negative quantity in the table
		  if (qty < 0 && Math.abs(qty) > quan) {
		      return false;
          }
          
          sqlText = "UPDATE stock SET quantity = ? WHERE pid = ? and wid = ?;";

		  ps = connection.prepareStatement(sqlText);

		  //Populate the prepared statement
		  ps.setInt(2,pid);
		  ps.setInt(3,wid);
		  double newqty = qty + quan;
		  ps.setDouble(1,newqty);

		  //Execute Update
		  ps.executeUpdate();
		  ps.close();

		  return true;
	  }
	  
      catch (SQLException e) {
      
          System.out.println("Query Exection Failed!");
          e.printStackTrace();
          return false;
	  }  
  }
  
  
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
  
      try{
      
          LocalDate today = LocalDate.now(ZoneId.of("America/Toronto"));

          String sinsert = "INSERT INTO orders(cid, pid, odate, shipwid, quantity, price, status) VALUES (?, ?, ?, ?, ?, ?, ?);";
          
          ps = connection.prepareStatement(sinsert);
          
          ps.setInt(1, cid);
          ps.setInt(2, pid);
          ps.setObject(3, today);
          ps.setInt(4, wid);
          ps.setDouble(5, qty);
          ps.setDouble(6, price);
          ps.setString(7, "O");
          ps.executeUpdate();

          sql = connection.createStatement();
          
          int OID = -1;
          String getOID = "SELECT currval(pg_get_serial_sequence('orders','oid'));";
          
          rs = sql.executeQuery(getOID);
          
          while(rs.next()) {
              OID = rs.getInt("currval");
          }
          return OID;
      }
      
      catch (SQLException e) {
      
          return -1;
      }
  }


  public boolean cancelOrder(int oid) {
	
      String sqlText;
	
      try {
      
          sqlText = "DELETE FROM orders WHERE oid = ? AND STATUS = 'O';";
	
		  ps = connection.prepareStatement(sqlText);
		  ps.setInt(1,oid);         

		  int change = ps.executeUpdate();
			
		  ps.close();
			
		  return (change!=0);
      }
			
	  catch (SQLException e) {
	  
          System.out.println("Query Exection Failed!");
          e.printStackTrace();
          return false;
	  }
  }

  
  public boolean shipOrder(int oid){
  	
      try{
      
          String sqlqty = "SELECT status, quantity, shipwid, pid FROM orders WHERE oid = ?;";
          
          ps = connection.prepareStatement(sqlqty);
          ps.setInt(1,oid);

          rs = ps.executeQuery();
          rs.next(); 

          // see if order has been shipped or not
          char[] cbuf = new char[1];
          
          try {
          
              rs.getCharacterStream("status").read(cbuf);
          }
          
          catch(IOException e){
          
              return false;
          }
          
          char status = cbuf[0];

          double qty = rs.getInt("quantity");
          int wid = rs.getInt("shipwid");
          int pid = rs.getInt("pid");

          //System.out.println(status);
          
          if (status == 'S') {
              System.out.println("Cannot ship order that has already been shipped");
              return false;
          }

          boolean update = updateStock(pid, wid, -qty);
          
          if (update == false) {
              System.out.println("update stock failed");
              return false;
          }

          String sqlText = "UPDATE orders SET status = ? WHERE oid = ?;";

          ps = connection.prepareStatement(sqlText);

          ps.setString(1,"S");
          ps.setInt(2, oid);
          ps.executeUpdate();

          return true;
      }

      catch (SQLException e) {
    
          e.printStackTrace();
          return false;
      }
  }
  

  public String listStock(int pid){
  
      String sqlText;
	  String retString = "";
	
	  try {
	  
	      sqlText = "SELECT * FROM Stock WHERE pid=? ORDER BY quantity DESC;";
			
		  ps = connection.prepareStatement(sqlText);
	      ps.setInt(1,pid);
			
		  rs = ps.executeQuery();
			
		  if (rs.next()) {
			
		      int wid=rs.getInt("wid");
			  double qty=rs.getDouble("quantity");
					
			  retString += wid + ":" + qty;
			
			  while(rs.next()){
				
			      wid=rs.getInt("wid");
				  qty=rs.getDouble("quantity");
					
				  retString += "#" + wid + ":" + qty;
			  }	
              ps.close();
			  rs.close();
		  }	
      }
			
      catch (SQLException e) {
      
          System.out.println("Query Exection Failed!");
          e.printStackTrace();
	  }
		return retString;
  }
  

  public String listReferrals(int cid){
  
        String sqlText;
		String retString = ""; 
	
		try {
		
            sqlText =  
			"SELECT r.custid, r.custref, c2.cname AS c2cname " + 
			"FROM referral r JOIN customer c1 ON r.custid=c1.cid JOIN customer c2 ON r.custref=c2.cid " +
			"WHERE r.custid=? " +
		  	"ORDER BY c1.cname;";
			 
			ps = connection.prepareStatement(sqlText);
			ps.setInt(1,cid);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
			
				int cid2=rs.getInt("custref");
				String cname=rs.getString("c2cname");
					
				retString += cid2 + ":" + cname;
			
				while(rs.next()){
				
					cid2=rs.getInt("custref");
					cname=rs.getString("c2cname");
					
					retString += "#" + cid2 + ":" + cname;
				}	
				ps.close();
				rs.close();
			}
		}
			
		catch (SQLException e) {
		
            System.out.println("Query Exection Failed!");
            e.printStackTrace();
		}
		return retString;
  }


  public boolean updateDB(){
  
        String sqlText;
	
		try {
			sqlText =  
			"DROP TABLE IF EXISTS bestsellers CASCADE;" +
			
			"CREATE TABLE bestsellers (" +
			" 	pid		INTEGER 	REFERENCES product(pid) ON DELETE RESTRICT," +
			" 	sales	NUMERIC(10,2)" +
			");" +

			"INSERT INTO bestsellers(" +
			"SELECT pid, SUM(quantity * price) AS sales " +
			"FROM orders " +
			"WHERE  status='S' " +
			"GROUP BY pid " +
			"HAVING SUM(quantity * price)>10000 "+
		  	"ORDER BY sales);"; 
			
			ps = connection.prepareStatement(sqlText);
			ps.executeUpdate(); 
			ps.close();
			
			return true;

		}	
		catch (SQLException e) {
		
            System.out.println("Query Exection Failed! INSERT INTO bestsellers failed.");
            e.printStackTrace();
            return false;
		}   
  }
}





































