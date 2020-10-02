import java.sql.*;
import java.util.Collections;
import java.util.ArrayList;
import java.io.*;
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
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      return;
    }
  }
  
  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password){

   try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      System.err.println("Failed to find the JDBC driver. Error:" + e.getMessage());
      return false;
    }

    try {
      connection = DriverManager.getConnection(URL, username, password);
      return true;
    } catch (SQLException se) {
      System.err.println("SQL Exception. Error: " + se.getMessage());
      return false;
    }
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
    try {
      connection.close();
      return true;
    } catch (SQLException se) {
      System.err.println("SQL Exception. Error: " + se.getMessage());
      return false;
    }
  }
  
  private int executeUpdate(String query) throws SQLException {
    ps = connection.prepareStatement(query);
    int numberRows = ps.executeUpdate();
    ps.close();
    
    return numberRows;
  }
    
  public boolean insertStock(int id, int wid, double qty) {
    
    if(qty < 0)
      return false;
    
    try {
      String query = "INSERT INTO stock VALUES (" + id + ", " + wid + ", " + qty + ")"; 

      int numberRows = executeUpdate(query);

      return numberRows == 1;
    }
    catch (SQLException se) {
      System.err.println("SQL Exception. Error: " + se.getMessage());
      return false;
    }
  }
  
  public boolean updateStock(int id, int wid, double qty) {

    try {
      double qtyAbs = java.lang.Math.abs(qty);
      String query = "UPDATE stock SET quantity = quantity + (" + Double.toString(qty) 
          + ") WHERE pid = " + Integer.toString(id) + " AND wid = " + Double.toString(wid)
          + " AND quantity >= " + Double.toString(qtyAbs);

      
      int numberRows = executeUpdate(query);
      
      return numberRows == 1;
    }
    catch (SQLException se) {
      System.err.println("SQL Exception. Error: " + se.getMessage());
      return false;
    }
  }
   

  public int insertOrder(int cid, int pid, int wid, double qty, double price){

    Date now = new Date();
    SimpleDateFormat ft = new SimpleDateFormat ("YYYY-MM-dd");
    String date = ft.format(now);

    try{
      String query = "INSERT INTO orders (cid, pid, odate, shipwid, quantity, price, status) VALUES (" + cid + ", " + pid + ", '" + date + "', " + wid + ", " + qty + ", " + price + ", 'O')";

      int numberRows = executeUpdate(query);

      if(numberRows == 1) {
        query = "select currval(pg_get_serial_sequence('orders','oid'))";
        sql = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = sql.executeQuery(query);
        if (rs.first())
          return rs.getInt(1); //Since we're only retrieving the column oid, this should be it.
        
        sql.close();
        rs.close();
      }
      
      return -1;
    }
    catch (SQLException se) {
      System.err.println("SQL Exception. Error: " + se.getMessage());
      return -1;
    }
  }

  public boolean cancelOrder(int oid) {
    try {
      String query = "DELETE FROM orders WHERE oid = " + oid + " AND status = 'O'";
      
      int numberRows = executeUpdate(query);
      
      return numberRows == 1;
    }
    catch (SQLException se) {
      System.err.println("SQL Exception. Error: " + se.getMessage());
      return false;
    }
  }

  public boolean shipOrder(int oid){
    try {
      String query = "SELECT oid, o.pid, shipwid, o.quantity FROM orders o INNER JOIN stock s ON o.pid = s.pid AND o.shipwid = s.wid" 
                   + "WHERE o.status = 'O' AND o.quantity <= s.quantity AND o.oid = " + oid;
      
      sql = connection.createStatement();
      rs = sql.executeQuery(query);
      
      if (rs.first()) {
        int pid = rs.getInt("pid"); //If the select query above returns no values, then pid will be null and we know we don't have any stock.
        if (pid != 0) { //The function getInt returns 0 if the column pid is NULL, we have stock available: decrease stock by order quantity, and Ship the order
          
          int shipwid = rs.getInt("shipwid");
          int quantity = rs.getInt("quantity");
          boolean updateStockSuccess = updateStock(pid, shipwid, quantity * -1); //make sure we deduct the quantity from the stock available.
          
          if(updateStockSuccess) {
            query = "UPDATE orders SET status = 'S' WHERE oid = " + oid;

            int numberRows = executeUpdate(query);

            return numberRows == 1;
          } 
        }
      }
      
      sql.close();
      rs.close();
    }
    catch (SQLException se) {
      System.err.println("SQL Exception. Error: " + se.getMessage());
      return false;
    }
    
    return false;
  }
  
  public String listStock(int pid){
    
    try {
      String sqlText = 
              "SELECT wid, quantity FROM stock WHERE pid = " + pid + " AND quantity > 0 ORDER BY quantity DESC;";
      sql = connection.createStatement();
      rs = sql.executeQuery(sqlText);

      String answer = "";
      int i = 1;

      if (rs != null) {
          while (rs.next()) {
            answer += Integer.toString(rs.getInt("wid")) + ":" + Double.toString(rs.getInt("quantity")); 
            if(!rs.isLast())
              answer += "#"; 
          }
      }
      else {
          answer = "";
      }
      
      sql.close();
      rs.close();
      
      return answer;       
    }

    catch (SQLException se) {
      System.err.println("SQL Exception. Error: " + se.getMessage());
      return "";
    }
  }
  
  public String listReferrals(int cid){
    try {
      String query = "SELECT c.cid, c.cname FROM referral r INNER JOIN customer c on r.custref = c.cid WHERE r.custid = " + cid + " ORDER BY cname";
      sql = connection.createStatement();
      rs = sql.executeQuery(query);

      String answer = "";
      int i = 1;

      if (rs != null) {
          while (rs.next()) {
            answer += Integer.toString(rs.getInt("cid")) + ":" + rs.getString("cname");
            if(!rs.isLast())
              answer += "#"; 
          }
      }
      else {
          answer = "";
      }
      
      sql.close();
      rs.close();
      return answer;       
    }

    catch (SQLException se) {
      System.err.println("SQL Exception. Error: " + se.getMessage());
      return "";
    }
  }
    
  public boolean updateDB(){
    try {
      String createQuery = "CREATE TABLE bestsellers (pid INTEGER, sales NUMERIC(10,2))";
      sql = connection.createStatement();
      sql.executeUpdate(createQuery);
      
      String query = "SELECT pid, SUM(price * quantity) AS sales FROM orders where status = 'S'"
                   + "GROUP BY pid HAVING SUM(price * quantity) > 10000";
      sql = connection.createStatement();
      rs = sql.executeQuery(query);

      String insertQuery = "INSERT INTO bestsellers VALUES (?,?)";
      ps = connection.prepareStatement(insertQuery);
      
      while(rs.next()){
        ps.setInt(1, rs.getInt("pid"));
        ps.setBigDecimal(2, rs.getBigDecimal("sales"));
        ps.executeUpdate();
      }
      
      rs.close();
      ps.close();
      sql.close();
      
      return true;
    } catch (SQLException se) {
      System.err.println("SQL Exception. Error: " + se.getMessage());
      return false;
    }
  }
}
