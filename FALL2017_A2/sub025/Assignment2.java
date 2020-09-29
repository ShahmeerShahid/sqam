import java.sql.*;
import java.util.Date;

import java.lang.reflect.Method;

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
      
    System.out.println("-------- PostgreSQL JDBC Connection Testing ------------");

    /*
    try {
    
      // Load JDBC driver
      //Class.forName("org.postgresql.Driver");
      Class.forName("postgresql-42.1.4.jar");
      
    } catch (ClassNotFoundException e) {
 
      System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
      e.printStackTrace();
      //return false;
 
    }*/
 
    System.out.println("PostgreSQL JDBC Driver Registered!");
 
    try {
      //Make the connection to the database, ****** but replace "username" with your username ******
      System.out.println("*** Please make sure to replace 'username' with your <UTorID> username in the jdbc connection string!!!");
      connection = DriverManager.getConnection("jdbc:postgresql://mcsdb.utm.utoronto.ca:5432/" + username + "_343", username, password); 
    } catch (SQLException e) {
 
      System.out.println("Connection Failed! Check output console");
      e.printStackTrace();
      return false;
 
    }

    /* Ignore this, we just set path to a2 instead

        try{

      //Create a Statement for executing SQL queries
      sql = connection.createStatement(); 

      String sqlText;
      sqlText = "SET search_path TO A2"; 
      System.out.println("Executing this command: \n" + sqlText.replaceAll("\\s+", " ") + "\n");
      sql.execute(sqlText);
      System.out.println (sql.getUpdateCount() + " rows were update by this statement.\n");

    } catch (SQLException e) {

      System.out.println("Query Exection Failed!");
      e.printStackTrace();
      return false;
    } */
  

    /* Return */

    if (connection == null) {
      System.out.println("Failed to make connection!");
      return false;
    }
    return true;

  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){

    try {
      if (connection != null)
        connection.close();
    }
    catch(SQLException e) {
      return false;    
    }
    return true;
  }
  
  public boolean insertStock(int id, int wid, double qty) {


    try{

      //Create a Statement for executing SQL queries
      sql = connection.createStatement(); 

      String sqlText;

      // Check that the input satisfies the integrity constraints 
      //this is alos how u check theyr the correct type
      //do we need to prepare the statements?

      //We dont need to check integrity constraints

      //sqlText = "pid IN SELECT pid FROM product";
      //sqlText = "wid IN SELECT wid FROM stock";
      //sqlText = "SELECT pid FROM product WHERE pid = " + id;
      if (qty < 0){
        System.out.println("Invalid qty input.\n");
        return false;
      }


      // Perform the insertion 
      sqlText = "INSERT INTO stock VALUES (" + id + ", " + wid + ", " + qty + ")";
      System.out.println("Executing this command: \n" + sqlText.replaceAll("\\s+", " ") + "\n");
      sql.executeUpdate(sqlText);
      return true;

    } catch (SQLException e) {

      System.out.println("Query Exection Failed!");
      e.printStackTrace();
      return false;
    } 
    
/*
    boolean pidin = false;
    boolean widin = false;
    boolean tuplein = false;
    String sqlText;
    Statement sql = null;
    ResultSet rs = null;
    
    try {
      sqlText = "SELECT pid FROM product";
      rs = sql.executeQuery(sqlText);
      while (rs.next() && !pidin) {
        if (pid == rs.getInt(1))
          pidin = true;
      }

      sqlText = "SELECT wid FROM warehouse";
      rs = sql.executeQuery(sqlText);
      while (rs.next() && !widin) {
        if (pid == rs.getInt(1))
          widin = true;
      }

      sqlText = "SELECT pid, wid FROM stock";
      rs = sql.executeQuery(sqlText);
      while (rs.next()) {
        if (pid == rs.getInt(1) && wid == rs.getInt(1))
          tuplein = true;
      }

      if (pidin && widin && !tuplein && qty >= 0) {
        sqlText = String.format("INSERT INTO stock VALUES(%d, %d, %f)", pid, wid, qty);
        sql.executeUpdate(sqlText);
        return true;
      }
      else {
        return false;
      }
    }
    catch (SQLException e) {
      return false;
    }
   */
  }

  // Do we implement a lock? damn dont need to if u do it in one operation
  //i.e. put it in the where clause
  //The handount says parameter is pid not id, shoud we change it?
   public boolean updateStock(int id, int wid, double qty) {
    try{

      //Create a Statement for executing SQL queries
      sql = connection.createStatement(); 

      String sqlText;

      /* Perform a query to get the old stock qty */
      double old_qty;
      sqlText = "SELECT quantity FROM stock WHERE pid = " + id +" AND wid = " + wid;
      System.out.println("Now executing the command: " + sqlText.replaceAll("\\s+", " ") + "\n");
      //pid and wid are a key so ther should only be one
      rs = sql.executeQuery(sqlText);
      int i = 0;
      old_qty = 0;
      if (rs != null){
        while (rs.next()){

          old_qty = rs.getInt("quantity");
          i++;
        }
      }
      //System.out.println("Num entries:" + i + "\n");
      //Prints out 1
      
      rs.close();

      /* Check that constraints are satisifed */

      if (qty < 0 && qty + old_qty < 0){
        System.out.println("Invalid qty input.\n");
        return false;
      }

      /* Perform the update */

      sqlText = "UPDATE stock SET quantity = " + (old_qty + qty) +
                " WHERE  pid = " + id +" AND wid = " + wid; 
      System.out.println("Executing this command: \n" + sqlText.replaceAll("\\s+", " ") + "\n");
      sql.executeUpdate(sqlText);
      System.out.println (sql.getUpdateCount() + " rows were update by this statement.\n");
      if(sql.getUpdateCount() == 0){
        System.out.println("Invalid pid and/or wid. \n");
        return false;
      }
      return true;

    } catch (SQLException e) {

      System.out.println("Query Exection Failed!");
      e.printStackTrace();
      return false;
    } 
  }
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
    try{

      //Create a Statement for executing SQL queries
      sql = connection.createStatement(); 

      String sqlText;

      /* TODO: Check that the input satisfies the integrity constraints */

      //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      Date utilDate = new Date();
      //System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
      java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
      //int odate = (int) sqlDate;

      //char status = 'O';


      /* Perform the insertion */
      sqlText = "INSERT INTO orders(cid, pid, odate, shipwid, quantity, price, status)" +
          " VALUES (" + cid + ", " + pid + ", \'" + sqlDate + "\'::date, " 
                + wid + ", " + qty + ", " + price + ", 'O')";

      System.out.println("Executing this command: \n" + sqlText.replaceAll("\\s+", " ") + "\n");
      sql.executeUpdate(sqlText);

            /* Get new_oid */
      int new_oid = -1;
      sqlText = "SELECT currval(pg_get_serial_sequence('orders','oid')) AS oid";
      System.out.println("Now executing the command: " + sqlText.replaceAll("\\s+", " ") + "\n");
      rs = sql.executeQuery(sqlText);
          if (rs != null){
            while (rs.next()){
              new_oid = rs.getInt("oid") + 1;
            }
          }

      //Close the resultset
      rs.close();

      return new_oid;

    } catch (SQLException e) {

      System.out.println("Query Exection Failed!");
      e.printStackTrace();
      return -1;
    } 
  }

  public boolean cancelOrder(int oid) {

    try{

      //Create a Statement for executing SQL queries
      sql = connection.createStatement(); 

      String sqlText;

      /* Perform the update */

      sqlText = "DELETE FROM orders WHERE  oid = " + oid + " AND status = 'O'";
      System.out.println("Executing this command: \n" + sqlText.replaceAll("\\s+", " ") + "\n");
      sql.executeUpdate(sqlText);
      System.out.println (sql.getUpdateCount() + " rows were update by this statement.\n");
        if(sql.getUpdateCount() == 0){ //i.e. if status = 'S'
          System.out.println("Status of oid is shipped or NULL or oid does not exist.\n");
          return false;
        }
      return true;

    } catch (SQLException e) {

      System.out.println("Query Exection Failed!");
      e.printStackTrace();
      return false;
    } 
  }

  //Do ppl have a typo here too
  //Theres more typos too see the pdf

  /*
  This method attempts to ship the order oid in case there is enough stock to fulﬁll the order 
  in the warehouse wid assigned to this order. In case shipping is possible, you must perform 
  a database transaction that updates the order oid status to ‘S’ and at the same time, reduces
  the stock quantity with a magnitude equal to the order quantity. An order cannot be shipped 
  in part. This method must make use of updateStock method. Return true iﬀ the shipping succeeds.
  */
  public boolean shipOrder(int oid){

    try{

      //Create a Statement for executing SQL queries
      sql = connection.createStatement(); 

      String sqlText;

      /* Get pid, wid, o_qty from orders */
      int pid = 0;
      int wid = 0;
      int oqty = 0;

      sqlText = "SELECT o.pid AS pid, wid, o.quantity AS oqty "
              + "FROM orders o, stock s " 
              + "WHERE o.pid = s.pid AND o.quantity < s.quantity AND status = 'O' AND oid = " + oid;

      System.out.println("Executing this command: \n" + sqlText.replaceAll("\\s+", " ") + "\n");
      rs = sql.executeQuery(sqlText);

      //There is only one oid corresponding to the parameter, since oid is a key for orders

          if (rs != null){
            while (rs.next()){
                    pid = rs.getInt("pid");
                    wid = rs.getInt("wid");
                    oqty = rs.getInt("oqty");
            }
          }


      /* Perform the update */

      sqlText = "UPDATE orders SET status = 'S' WHERE oid = " + oid;
      System.out.println("Executing this command: \n" + sqlText.replaceAll("\\s+", " ") + "\n");
      sql.executeUpdate(sqlText);
      System.out.println (sql.getUpdateCount() + " rows were update by this statement.\n");

      updateStock(pid, wid, -oqty);

      //Close the resultset
      rs.close();

      return true;

    } catch (SQLException e) {

      System.out.println("Query Exection Failed!");
      e.printStackTrace();
      return false;
    }    
  }
  
  public String listStock(int pid){

    String result = "";

    try{

      //Create a Statement for executing SQL queries
      sql = connection.createStatement(); 

      String sqlText;

      /* Perform the query */

      sqlText = "SELECT * FROM stock WHERE pid = " + pid + " AND quantity > 0 ORDER BY quantity DESC";
      System.out.println("Now executing the command: " + sqlText.replaceAll("\\s+", " ") + "\n");
      rs = sql.executeQuery(sqlText);

      if (rs != null){
        while (rs.next()){
          result += "" + rs.getInt("wid") + ":" + rs.getDouble("quantity") + "#";
              }
          }

      //Close the resultset
      rs.close();

      return result;

    } catch (SQLException e) {

      System.out.println("Query Exection Failed!");
      e.printStackTrace();
      return result;
    } 
  }
  
  public String listReferrals(int cid){

    String result = "";

    try{

      //Create a Statement for executing SQL queries
      sql = connection.createStatement(); 

      String sqlText;

      /* Perform the query */

      sqlText = "SELECT * FROM referral, customer WHERE custid = " + cid + " AND custref = cid ORDER BY cname";
      System.out.println("Now executing the command: " + sqlText.replaceAll("\\s+", " ") + "\n");
      rs = sql.executeQuery(sqlText);

      if (rs != null){
        while (rs.next()){
          result += "" + rs.getInt("cid") + ":" + rs.getString("cname") + "#";
        }
      }

      //Close the resultset
      rs.close();

      return result;

    } catch (SQLException e) {

      System.out.println("Query Exection Failed!");
      e.printStackTrace();
      return result;
    } 
  }
    
  public boolean updateDB(){

    try{

      //Create a Statement for executing SQL queries
      sql = connection.createStatement(); 

      String sqlText;

      /* Perform the update */
      
      sqlText = "CREATE TABLE bestsellers(" +
                "pid INTEGER," +
                "sales NUMERIC(10, 2)" +
                ")";
      System.out.println("Executing this command: \n" + sqlText.replaceAll("\\s+", " ") + "\n");
      sql.executeUpdate(sqlText);
      System.out.println (sql.getUpdateCount() + " rows were update by this statement.\n");
  
         sqlText =  "INSERT INTO bestsellers " +
                "SELECT pid, SUM(quantity*price) AS sales FROM orders " +
                "WHERE status = 'S' GROUP BY pid HAVING SUM(quantity*price) > 10000";
//"INSERT INTO bestsellers SELECT pid, SUM(quantity*price) AS sales FROM orders WHERE status = 'S' GROUP BY pid HAVING SUM(quantity*price) > 10000";

//"INSERT INTO bestsellers SELECT pid, SUM(quantity*price) AS sales FROM orders WHERE status = 'S' GROUP BY pid HAVING SUM(quantity*price) > 10000";
//"INSERT INTO bestsellers SELECT pid, SUM(quantity*price) AS sales FROM orders WHERE status = 'S' GROUP BY pid HAVING SUM(quantity*price) > 10000";

      System.out.println("Executing this command: \n" + sqlText.replaceAll("\\s+", " ") + "\n");
      sql.executeUpdate(sqlText);
      System.out.println (sql.getUpdateCount() + " rows were update by this statement.\n");

      return true;

    } catch (SQLException e) {

      System.out.println("Query Exection Failed!");
      e.printStackTrace();
      return false;
    }
  }
}
