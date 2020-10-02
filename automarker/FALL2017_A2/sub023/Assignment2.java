import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

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
	  try{
          Class.forName("org.postgresql.Driver");
      }catch (ClassNotFoundException e) {
          System.out.println("Failed to find the JDBC driver");
          return;
      }
  }
  
  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password){
	  try{
          connection = DriverManager.getConnection(URL+"?currentSchema=A2", username, password);
          sql = connection.createStatement();
      }catch(SQLException se){
          return false;
      }
      if(connection != null)
    	  return true;
      else
    	  return false;
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
	  try{
          
          if(sql != null && !sql.isClosed())
              sql.close();
          if(rs !=null && !rs.isClosed())
              rs.close();
          if(ps != null && !ps.isClosed())
              ps.close();
          if(connection != null)
              connection.close();
          if (connection == null || connection.isClosed())
              return true;
          else
        	  return false;
      }catch(SQLException se){
          return false;
      }   
  }
 
  public boolean insertStock(int id, int wid, double qty) {
	  try{

		  String queryString3 = "SELECT * FROM stock s WHERE "
		  		+ "pid = " + id + " AND wid = " + wid +";";
		  String queryString1 = "SELECT * FROM product WHERE " + "pid = "+ id +";";
		  String queryString2 = "SELECT * FROM warehouse WHERE wid = "+ wid +";";
		  
		  ResultSet rs1 = sql.executeQuery(queryString1);
		  ResultSet rs2 = sql.executeQuery(queryString2);
		  ResultSet rs3 = sql.executeQuery(queryString3);
		  

		  if(qty>0){
			  
			  String insertString="INSERT INTO stock VALUES(" + id + ", "
            		  + wid + ", " + qty + ")";
			  int row = sql.executeUpdate(insertString);
			  
			  if(row == 1)
				  return true; 
			  else
				  return false;
		  }else{
			  
			  return false;
		  }
			  
	  }catch(SQLException se){
		  return false;
	  }
  }

  
   public boolean updateStock(int id, int wid, double qty) {
	   try{
		   String queryString = "SELECT * FROM stock WHERE "
	          		+ "pid = "+id+" AND wid = " + wid + ";";
		   rs = sql.executeQuery(queryString);
		   if(rs.next()){
			   int oldQty = rs.getInt("quantity");
			   rs.close();
			   String updateString;
			   if(qty<0 && java.lang.Math.abs(qty) <= oldQty){
				   updateString = "UPDATE stock SET quantity = quantity - " //SET quantity = " + (oldQty+qt)
						   + java.lang.Math.abs(qty) + " WHERE pid = "
						   + id + " AND wid = " + wid;
			   }else if(qty>=0){
				   updateString = "UPDATE stock SET quantity = quantity + " + qty //SET quantity = " + (oldQty+qt)
			        		  + " WHERE pid = "+ id + " AND wid = " + wid;
			   }else{
				   return false;
			   }
			   int row = sql.executeUpdate(updateString);
			   if(row >= 1)
				   return true;
			   else
				   return false;
		   }else
			   return false;
	   }catch(SQLException se){
		   return false;
	   }
   }
   

   public int insertOrder(int cid, int pid, int wid, double qty, double price){
		  try{
			  String checkCid = "SELECT cid FROM customer WHERE cid = " + cid +";";
	          String checkPid = "SELECT pid FROM product WHERE pid = " + pid +";";
	          String checkWid = "SELECT wid FROM warehouse WHERE wid = " + wid +";";
	          
	          ResultSet rsc, rsp, rsw;
	          
	          ps = connection.prepareStatement(checkCid);
	          rsc = ps.executeQuery();
	          ps = connection.prepareStatement(checkPid);
	          rsp = ps.executeQuery();
	          ps = connection.prepareStatement(checkWid);
	          rsw = ps.executeQuery();

	          if(rsc.next() && rsp.next() && rsw.next()){
	        	  String sqlText = "select nextval('orders_oid_seq');";
	        	  ps = connection.prepareStatement(sqlText);
	  			rs = ps.executeQuery();
	  			int nval=0;

	  			while (rs.next()){

	  					nval = rs.getInt("nextval");

	        	}
	      		  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        	  Date date = new Date();
	        	  String odate= dateFormat.format(date);
	        	  String insertString="INSERT INTO orders VALUES(" + nval + ", " + cid + ", "
	            		  + pid + ", '" + odate + "', " + wid + ", " + qty + ", " + price + 
	            		  ", 'O')";
	        	  int row = sql.executeUpdate(insertString);

	              if(row==1)
	            	  return nval;
	              else
	            	  return -1;
	          }else
	        	  return -1;
	          
		  }catch(SQLException se){
			   return -1;
		   }
	  }

  public boolean cancelOrder(int oid) {
	  try{
		  String deleteString = "DELETE FROM orders WHERE oid = "
				  + oid + " AND status = 'O'";
		  int row = sql.executeUpdate(deleteString);
		  if (row>=1)
			  return true;
		  else
			  return false;
	  }catch(SQLException se){
	    	  return false;
	  }
  }

  public boolean shipOrder(int oid){
	  try{
		  String orderString = "SELECT o.oid, o.quantity, s.pid, s.wid FROM orders o, stock s WHERE o.oid = "
				+ oid +" AND o.shipwid = s.wid AND o.pid = s.pid AND o.quantity <= s.quantity"
	      		+ " AND o.status = 'O';";
		  

		  ps = connection.prepareStatement(orderString);
		  rs = ps.executeQuery();

		  if (rs.next()){

			  int oldQty = rs.getInt("quantity");

			  int spid = rs.getInt("pid");

			  int swid = rs.getInt("wid");
			  
			  updateStock(spid, swid, -oldQty);

			  String updateOrder = "UPDATE orders SET status = 'S' WHERE oid = "+oid;
			  int row = sql.executeUpdate(updateOrder);

			  
			  if (row==1)
				  return true;
			  else
				  return false;
		  }else
			  return false;
	  }catch(SQLException se){
	    	  return false;
	  }
  }
  
  public String listStock(int pid){
	  try{
		  String checkStock = "SELECT wid, quantity FROM stock WHERE pid = "+ pid + 
				  " AND quantity > 0;";
		  rs = sql.executeQuery(checkStock);
		  String result = "";
		  if(rs!=null){
			  while(rs.next()){
				  result = result + Integer.toString(rs.getInt("wid")) + ":" +
						  Integer.toString(rs.getInt("quantity")) + "#";
				  
			  }
		  }
		  rs.close();
		  return result;
	  }catch(SQLException se){
		  return "";
	  }
  }
  
  public String listReferrals(int cid){
	  try{
		  String checkReferral = "SELECT cid, cname FROM referral, customer "
		  		+ " WHERE custid = "+ cid + " AND custref = cid;";
		  rs = sql.executeQuery(checkReferral);
		  String result = "";
		  if(rs!=null){
			  while(rs.next()){
				  result = result + Integer.toString(rs.getInt("cid")) + ":" +
						  rs.getString("cname") + "#";
			  }
		  }
		  rs.close();
		  return result;
		  
	  }catch(SQLException se){
		  return "";
	  }
  }
    
  public boolean updateDB(){
	  try{
		  int row = 0;
		  String updateString = "CREATE TABLE bestsellers " 
		  		+ "(pid INTEGER NOT NULL, "
		  		+ "sales  NUMERIC(10, 2))";
		  		
		  sql.execute(updateString);
		  
		  String insertString = "SELECT pid, CAST(SUM(quantity * price)as NUMERIC(12, 2)) AS sales "
		  		+ "FROM orders "
		  		+ "WHERE status = 'S' AND quantity > 0 "
		  		+ "GROUP BY pid "
		  		+ "HAVING CAST(SUM(quantity * price)as NUMERIC(12, 2)) > 10000;"; 
		  ps = connection.prepareStatement(insertString);
		  rs = ps.executeQuery();
		  
		  while(rs.next()){
			  
			  int pid = rs.getInt("pid");
			  int sales = rs.getInt("sales");
			  String insert = "INSERT INTO bestsellers VALUES("+
					  pid + ", " + sales + ")";
			  row = sql.executeUpdate(insert);
			  if (row>=1)
				  return true;
			  else
				  return false;
		  }
		  
		  if(row!=0)
			  return true;
		  else
			  return false;
	  }catch(SQLException se){
		  return false;    
	  }
  }


}
