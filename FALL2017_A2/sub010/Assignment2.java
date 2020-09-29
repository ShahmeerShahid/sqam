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
  	try{
  		connection = DriverManager.getConnection(URL, username, password);
  	} catch (SQLException e){
  		return false;
  	}
      return true;
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
  	try{
  		connection.close();
  		
  	} catch (SQLException e){
  		return false;
  	}    
  }
  
  //Checks if single id exsists in respective table, returns false is any issues arise
  public boolean checkSingleId(int id, String tablename, String colname){
  	Boolean checker = false;
  	String querytxt = "SELECT " + colname + " FROM " + tablename;
    PreparedStatement pment = connection.prepareStatement(querytxt);
    ResultSet rset = pment.executeQuery();
    while (rset.next()){
    	if (rset.getInt(colname) == id){
    		checker = true;
    	}
    }
    try { rset.close(); } catch (SQLException e) { return false; }
    try { pment.close(); } catch (SQLException e) { return false; }
    return checker;
  }
  /*
  public boolean checkDoubleId(int id, String tablename1, String colname1, String tablename2, String colname2, String joinon){
  	Boolean checker = false;
  	String querytxt = String.format("SELECT %s, %s FROM %s INNER JOIN %s ON %s.", colname1, colname2, tablename1, tablename2,);
    PreparedStatement pment = connection.prepareStatement(pquerytxt);
    ResultSet rset = pment.executeQuery();
    while (rset.next()){
    	if (rset.getInt(colname) == id){
    		checker = true;
    	}
    }
    try { rset.close(); } catch (SQLException e) { return false; }
    try { pment.close(); } catch (SQLException e) { return false; }
    return checker;
  }
  */
    
  public boolean insertStock(int pid, int wid, double qty) {
  	if (qty < 0){
  		return false;
  	}
  	if (checkSingleId(pid, "product", "pid") && checkSingleId(wid, "warehouse", "wid")){
  		Boolean stockin = false;
		String querytxt = "SELECT pid, wid FROM stock";
		PreparedStatement pment = connection.prepareStatement(querytxt);
		ResultSet rset = pment.executeQuery();
		while (rset.next()){
			if (rset.getInt("pid") == pid && rset.getInt("wid") == wid){
				stockin = true;
			}
		}
		try { rset.close(); } catch (SQLException e) { return false; }
		try { pment.close(); } catch (SQLException e) { return false; }
		
		if (!stockin){
			querytxt = String.format("INSERT INTO stock VALUES (%d, %d, %f)", pid, wid, qty);
			PreparedStatement pment2 = connection.prepareStatement(querytxt);
			ResultSet rset2 = pment2.executeQuery();
			try { rset2.close(); } catch (SQLException e) { return false; }
			try { pment2.close(); } catch (SQLException e) { return false; }
			return true;
			
		}
		else{
			return false;
		}
		}
  	else{
  		return false;
  	}
    
    
   return false;
  }
  
   public boolean updateStock(int pid, int wid, double qty) {
   Boolean stockin = false;
		String querytxt = "SELECT pid, wid FROM stock";
		PreparedStatement pment = connection.prepareStatement(querytxt);
		ResultSet rset = pment.executeQuery();
		while (rset.next()){
			if (rset.getInt("pid") == pid && rset.getInt("wid") == wid){
				stockin = true;
			}
		}
   return false;
  }
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
   return -1;
  }

  public boolean cancelOrder(int oid) {
   return false;
  }

  public boolean shipOrder(int odi){
   return false;        
  }
  
  public String listStock(int pid){
	return "";
  }
  
  public String listReferrals(int cid){
    return false;
  }
    
  public boolean updateDB(){
	return false;    
  }
  
}
