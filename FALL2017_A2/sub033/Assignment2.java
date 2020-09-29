package pack;


import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;



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
	public void Assignment2(){
	  }
	  
	  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
	  public boolean connectDB(String URL, String username, String password){

	    //Register a JDBC Driver
	      try{
	          Class.forName("org.postgresql.Driver");
	      }catch (ClassNotFoundException e){
	          return false;
	      }

	      try{
	        connection = DriverManager.getConnection(URL, username, password);
		ps = connection.prepareStatement("SET search_path to a2");
		ps.execute();
		ps.close();

	      }catch (SQLException e){
	        return false;
	      }

	      if(connection != null){
	        return true;
	      }
	      return false;
	  }
	  
	  //Closes the connection. Returns true if closure was successful
	  public boolean disconnectDB(){
		try{
		ps.close();
		if(rs!= null){
		rs.close();
		}	
		connection.close();
		return true;
		}
		catch(SQLException e){
	      return false;    }
	  }
	    
	  public boolean insertStock(int id, int wid, double qty) {
		  boolean existence;
		  
		  //check qty >= 0 
		  if(qty>=0) {
			//check if pid does exist in the PRODUCT table 
			  if(existence = ifExists(id,"pid","a2.product")) {
				  
				//Check if the wid exists in the table WAREHOUSE
				 if(existence = ifExists(wid,"wid","a2.warehouse")) {
					 
	
					//check if there is not key with the WID and PID in STOCK
		
						if(existence = ifKeyexist(id,wid,"pid", "wid","a2.stock")) {
							 return false;
						 }
						// the key doesn't exist
						 else {
							 //attempt to make the insert into the table
							 try {
								 ps = connection.prepareStatement("INSERT INTO a2.stock " + " (pid,wid,quantity)" +" values(?,?,?);");
								 ps.setInt(1, id);
								 ps.setInt(2, wid);
								 ps.setDouble(3, qty);
								 
								//run the execution of the text
								 int row_changed = ps.executeUpdate();
								 //check how much rows were changed
								 if(row_changed > 0) {
								 return true;
								 }
								 return false;
								 
								 	}
								 catch(Exception e) {
									 return false;
								 }
						 	}
				 }
				  return false;
			  }
			  return false;
		  }	
	   return false;
	  }
	  
	   public boolean updateStock(int id, int wid, double qty) {
		   boolean  existence;
		   // Check if the key exists
		   if(existence = ifKeyexist(id,wid,"pid", "wid","a2.stock")) {
			   //check if the ABSOLUTE value is less than or equal to quantity 
			   try {
				if(qty<0) {
					ps = connection.prepareStatement("SELECT * FROM a2.stock WHERE stock.quantity >= ? AND pid = ? AND wid = ?;");
					ps.setDouble(1,(qty * -1));
					ps.setInt(2, id);
					ps.setInt(3, wid);
				}
				else {
					ps = connection.prepareStatement("SELECT * FROM a2.stock WHERE pid = ? AND wid = ?;");
					ps.setInt(1, id);
					ps.setInt(2, wid);
			
				}
				rs = ps.executeQuery();
				//satisfies all the requirements
				if(rs.next()) {
					//update the stock of the stock table
					ps = connection.prepareStatement("UPDATE a2.stock SET quantity = quantity + ? WHERE pid = ? AND wid = ?");
					ps.setDouble(1, qty);
					ps.setInt(2, id);
					ps.setInt(3, wid);
					int changed = ps.executeUpdate();
					//check that rows were actually changed
					if(changed > 0) {
					return true;
					}
					return false;
				}
				else{
				return false;
				}
			} catch (SQLException e) {
				return false;
			} 
		   }    
	   return false;
	  }
	   
	  public int insertOrder(int cid, int pid, int wid, double qty, double price){
		  boolean existence;
		  
		  // check if cid in customer 
		  if(existence = ifExists(cid,"cid","a2.customer")) {
			   //check if the pid existence in product
			  if(existence = ifExists(pid,"pid","a2.product")) {
				  //check if the wid existence in warehouse
				  if(existence = ifExists(wid,"wid","a2.warehouse")) {
					  
					  try {
						  int new_oid;
						  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						  Date date = new Date();
						  String new_date = dateFormat.format(new Date());
						  
						  ps = connection.prepareStatement("INSERT INTO a2.orders " + " (cid,pid,odate,shipwid,price,quantity,status)" +" values(?,?,DATE(?),?,?,?,?);");
						  ps.setInt(1, cid);
						  ps.setInt(2, pid);
						  ps.setString(3,new_date);
						  ps.setInt(4, wid);
						  ps.setDouble(5,price);
						  ps.setDouble(6, qty);
						  ps.setString(7, "O");
						  
						  int rows_changed = ps.executeUpdate();
						  
						  if(rows_changed == 0) {
							  return -1;
						  }
						  //return the new order number
						ps = connection.prepareStatement("SELECT currval(pg_get_serial_sequence('a2.orders', 'oid'));");
						  rs = ps.executeQuery();	
							if(rs.next()) {
							  new_oid = rs.getInt("currval");
							  return new_oid;
						  }

						  return -1;
					  }
					  catch(Exception e) {
						  return -1;
					  }  
				  }
				  return -1;
			  }
			  return -1;
		  }  
	   return -1;
	  }

	  public boolean cancelOrder(int oid) {
		  try {
			  //check if the requirement are met
			ps = connection.prepareStatement("DELETE FROM a2.orders WHERE oid = ? AND status = 'O';");
			ps.setInt(1, oid);
			int rowsAffected = ps.executeUpdate();
			//check if the a row was actually deleted
			if(rowsAffected == 0) {
				return false;
			}
			return true;
		} catch (SQLException e) {
			return false;
		}
	  }

	  public boolean shipOrder(int oid){
		  //get the order_quantity value
		  int order_quantity;
		  int pid;
		  int wid;
		  try {
			ps = connection.prepareStatement("SELECT * FROM a2.orders WHERE oid = ? AND status ='O';");
			ps.setInt(1, oid);
			rs = ps.executeQuery();
			//if the OID exists
			if(rs.next()) {
				order_quantity = rs.getInt("quantity");
				//subtract the value from stock b/c being shipped
				order_quantity = order_quantity * (-1);
				pid = rs.getInt("pid");
				wid = rs.getInt("shipwid");
			}
			//OID doesn't exist
			else {
				return false;
			}
			//update the stock using the method
			boolean update_store_check = updateStock(pid,wid,order_quantity);
			//stock update was successful
			if(update_store_check) {
				//update the orders status to 'S"
				ps = connection.prepareStatement("UPDATE a2.orders SET status = 'S' WHERE oid = ?;");
				ps.setInt(1, oid);
				int rows_changed = ps.executeUpdate();
				
				//Check if a rows was actually changed within 'orders'
				if(rows_changed > 0) {
					return true;
				}

				//No row was changed within the order DOUBLE CHEDCKKKK IF YOU HAVE T CHECK THIISSSSS
				return false;
			}
			else {
				return false;
			}
				
		} catch (SQLException e) {
			return false;
		}        
	  }
	  
	  public String listStock(int pid){
		  
		  // quanityt of of pid within warehouses
		  //quanitites sorted as decimals 
		  //return empty string if there is nothing
		  double quantity_stock;
		  int wid_id;
		  double quantity_ahead;
		  int wid_ahead;
		  boolean values_found = false;
		  boolean flick = false;
		  String return_data = null;
		  //do query to find the quantity of stock within warehouse of pid
		  
		  try {
			ps = connection.prepareStatement("SELECT wid,quantity FROM a2.stock WHERE pid = ? AND quantity > 0 ORDER BY quantity;");
			ps.setInt(1, pid);
			rs = ps.executeQuery();
			//check if there is something
			while(rs.next()) {
				
				values_found = true;
				//collect data from this portion
				quantity_stock = rs.getDouble("quantity");
				wid_id = rs.getInt("wid");
				
				 if(flick == true) {
					 //add the hash to the end 
					return_data = return_data + "#";
					flick = false;
				}
				//make the sentence
				return_data = String.valueOf(wid_id) + ":" + String.valueOf(quantity_stock);
				
				if(rs.next()) {
					flick = true;
					//add the hash to the end of it
					return_data = return_data + "#";
					//process the data
					quantity_stock = rs.getDouble("quantity");
					wid_id = rs.getInt("wid");
					//add the new data as well

					return_data = return_data + String.valueOf(wid_id) + ":" + String.valueOf(quantity_stock);
				}
				else {
					//dont add the hash to it
					//return the statement here
					return return_data;
					
				}
			}
			if(flick == true) {
				//add has to the end of the sentence ;
				return return_data;
			}
			
			if(values_found == false) {
				return "";
			}	
		} catch (SQLException e) {
			return "";
		}
		  	  
		  return "";
	  }
	  
	  public String listReferrals(int cid){
		  //SELECT custref,cname FROM referral,customer WHERE custid = 2 AND custref = customer.cid ORDER BY cname;
		  int cid_return;
		  String name_returned;
		  boolean values_found = false;
		  boolean flick = false;
		  String return_data = "";
		  try {
			ps = connection.prepareStatement("SELECT custref,cname FROM a2.referral,a2.customer WHERE custid = ? AND custref = customer.cid ORDER BY cname;");
			ps.setInt(1, cid);
			rs = ps.executeQuery();
			//check if there is something
			while(rs.next()) {
				
				values_found = true;
				//collect data from this portion
				cid_return = rs.getInt("custref");
				name_returned = rs.getString("cname");
				
				 if(flick == true) {
					 //add the hash to the end 
					return_data = return_data + "#";
					flick = false;
				}
				//make the sentence
				return_data = String.valueOf(cid_return) + ":" + name_returned;
				
				if(rs.next()) {
					flick = true;
					//add the hash to the end of it
					return_data = return_data + "#";
					//process the data
					cid_return = rs.getInt("custref");
					name_returned = rs.getString("cname");
					//add the new data as well
					return_data = return_data + String.valueOf(cid_return) + ":" + name_returned;
				}
				else {
					//dont add the hash to it
					//return the statement here
					return return_data;
					
				}
			}
			if(flick == true) {
				//add has to the end of the sentence 
				return return_data;
			}
			
			if(values_found == false) {
				return "";
			}	
			
			
		} catch (SQLException e) {
			return "";
		}
		  
		  return "";
	  }
	    
	  public boolean updateDB(){
		  //CREATE VIEW output AS SELECT pid,SUM(quantity * price) AS sales FROM orders WHERE status = 'S' GROUP BY pid;
		  //SELECT pid,sales FROM output WHERE sales > 10000;
		  //DROP VIEW output;
		  
		  try {
			sql = connection.createStatement();
			try{
			sql.executeQuery("DROP TABLE bestsellers;");
			}
			catch(SQLException e){
				

			}
			sql.executeUpdate("CREATE TABLE bestsellers_pre AS SELECT pid,SUM(quantity * price) AS sales FROM a2.orders WHERE status = 'S' GROUP BY pid;");
			int rows = sql.executeUpdate("CREATE TABLE bestsellers AS SELECT pid,sales FROM bestsellers_pre WHERE sales > 10000;");
			sql.executeUpdate("DROP TABLE bestsellers_pre;");
			return true;
			
			
		} catch (SQLException e) {
			return false;
		}
		     
	  }
	  //Check if the id enter exists in the table specified
	public boolean ifExists(int id, String column ,String table) {
		  
		  try {
			ps = connection.prepareStatement("SELECT " + column +" FROM "+ table + " WHERE "+ column +" = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if(rs.next()) {
				return true;
			}
			
		} catch (SQLException e) {
			return false;
		}
		return false;
		 
	  }


//check if the key exists within the table Return: True if exist else False
	public boolean ifKeyexist(int id_1, int id_2, String column_1, String column_2,  String table) {
		 
		 try {
				ps = connection.prepareStatement("SELECT * FROM " + table +" WHERE "+ column_1 +" = ?" +" AND "+ column_2 +" ="+ " ?;");

				ps.setInt(1, id_1 );
				ps.setInt(2, id_2);
				rs = ps.executeQuery();
			 
			//check if there is not key with the WID and PID in STOCK
				if(rs.next()) {
					 return true;
				}
		 }
		 catch (SQLException e) {
				return false;
			}
				
				return false;
	}
}

		
