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
    	 connection = DriverManager.getConnection(
    			  URL,
    			  username, 
    			  password);
      }catch(SQLException e) {
    	  
      }
      return connection != null;
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
	  boolean connection_closed = false;
	  try {
		  connection.close();
		  connection_closed = true;
	  }catch(SQLException e){
		  connection_closed = false;
	  }
      return connection_closed;    
  }
    
  public boolean insertStock(int id, int wid, double qty) {
   boolean insert = false;
   if(qty < 0 )return false;
   String sqlText = "INSERT INTO stock VALUES (" + id + ", " + wid + ", " + qty + ")";
   try {
	   sql = connection.createStatement();
	   sql.executeUpdate(sqlText);
	   insert = true;
   }catch(SQLException e) {
	   insert = false;
   }
   return insert;
  }
  
   public boolean updateStock(int id, int wid, double qty) {
	   boolean update = false;
	   String sqlText;
	   if (qty < 0) {
		 sqlText = "UPDATE stock SET quantity = CASE WHEN -1*"+ qty + " " + 
			   "<= quantity THEN " + qty + 
			   " ELSE quantity END WHERE pid = "+id + " AND wid = "+ wid+ ")";
	   }else {
		 sqlText = "UPDATE stock SET quantity = "+ qty +
				 "WHERE pid = "+ id + " AND wid = "+ wid + ")";
	   } 
	   try {
		   sql = connection.createStatement();
		   sql.executeUpdate(sqlText);
		   update = true;
	   }catch(SQLException e) {
		   update = false;
	   }
	   return update; 
  }
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
	  int result = -1;
	  int oid;
	  String GetLast = "select currval(pg_get_serial_sequence(‘orders’,’oid’)";
		try {
			 sql = connection.createStatement();
			 rs = sql.executeQuery(GetLast);
			 oid = rs.getInt("currval");
			 
			 String sqlText = "insert into orders values (?, ?, ?, ?, ?, ?, ?, ?)";
			 ps = connection.prepareStatement(sqlText);
			 ps.setInt(1,oid);
			 ps.setInt(2,cid);
			 ps.setInt(3,pid);
			 ps.setString(4, "current_timestamp");
			 ps.setInt(5,wid);
			 ps.setDouble(6,qty);
			 ps.setDouble(7,price);
			 ps.setString(8,"O");
			 
			 ps.executeUpdate();
			 result = oid;
			 rs.close();
			 ps.close();
			 
		}catch(SQLException e) {
			result = -1;
		}
	  return result;
  }

  public boolean cancelOrder(int oid) {
   boolean canceled = false;
   try {
	   String sqlText = "delete from orders where oid = ? AND status = ?";
	   ps = connection.prepareStatement(sqlText);
	   ps.setInt(1, oid);
	   ps.setString(2, "O");
	   int result = ps.executeUpdate();
	   canceled = true;
	   if (result < 1) {
		   canceled = false;
	   }
	   ps.close();
   }catch(SQLException e) {
	   canceled = false;
   }
   return canceled;
  }

  public boolean shipOrder(int odi){
   return false;        
  }
  
  public String listStock(int pid){
	String stock = "";
	try {
		String sqlText = "select wid, quantity from stock where pid = ?";
		ps = connection.prepareStatement(sqlText);
		ps.setInt(1, pid);
		rs = ps.executeQuery();
		if(rs != null) {
			while(rs.next()){
			stock+= rs.getInt(1) +":" + rs.getInt(2) + "#";
			}
			stock = stock.substring(0, stock.length() - 1);
		}
		rs.close();
		ps.close();
		
	}catch(SQLException e) {
		stock = "";
	}
	return stock;
  }
  
  public String listReferrals(int cid){
	  String ref = "";
		try {
			String sqlText = "select custref, cname from referral join customer"
					+ " on referral.custref = customer.cid where cid = ?"
					+ " group by custref, cname";
			ps = connection.prepareStatement(sqlText);
			ps.setInt(1, cid);
			rs = ps.executeQuery();
			if(rs != null) {
				while(rs.next()){
				ref+= rs.getInt(1) +":" + rs.getString(2) + "#";
				}
				ref = ref.substring(0, ref.length() - 1);
			}
			rs.close();
			ps.close();
			
		}catch(SQLException e) {
			ref = "";
		}
		return ref;
  }
    
  public boolean updateDB(){
	boolean updated = false;
	try {
		String sqlText = "create table bestsellers as (select pid, sum(price*quantity) as sales"
				+ "from orders where sum(price*quantity) > 10000000 AND status = 'S' group by pid)";
		sql = connection.createStatement();
		sql.executeQuery(sqlText);
		updated = true;
		
	}catch(SQLException e) {
		updated =false;
	}
	return updated;    
  }
  
}
