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
        connection = DriverManager.getConnection(URL, username, password); 
      } catch (SQLException e) {
    
          System.out.println("Connection Failed! Check output console");
          e.printStackTrace();
    
      }
    
      if (connection != null) {
          return true;
      }
          
      return false;
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
      try{
        connection.close();
        return true;
      }
      catch (SQLException e) {
  
                    System.out.println("Query Exection Failed!");
                    e.printStackTrace();

  
            }   
      return false;    
  }
    
  public boolean insertStock(int id, int wid, double qty) {
   return false;
  }
  
   public boolean updateStock(int id, int wid, double qty) {
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
