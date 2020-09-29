//TODO: dont forget to remove this, removed it
//package pkg;

import java.sql.*;
import java.lang.Exception;
import java.util.*;
import java.math.*;

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
  
  /**
  Creates a connection to the database. Puts that connection in 'connection'.
  @param url	  url of the database.
  @param username	  username of db.
  @param password   password of db.
  @return           true, if the connection was succesfull, false otherwise.  
  **/
  public boolean connectDB(String URL, String username, String password){
  	try{
      Class.forName("org.postgresql.Driver");
	Properties props = new Properties();
	props.setProperty("user",username);
	props.setProperty("password", password);
	props.setProperty("currentSchema", "a2");
  		connection = DriverManager.getConnection(URL, props);

  	}
  	catch(Exception e){
		//System.out.println(e);
  		return false;
  	}
  	return true;
  }
  
  //Closes the connection. Returns true if closure was sucessful
  /**
  Closes the connection stored in variable connection. And any other variables associated with the connection; like ps, sql and rs.
  @return	returns true, if connection closed without error, false otherwise.  
  **/
  public boolean disconnectDB(){
	try{
		if(connection != null){connection.close();}

		if(rs != null){rs.close();}

		if(sql != null){sql.close();}

		if(ps != null){ps.close();}	
	}
	catch(Exception e){
		////System.out.println(e);
      		return false;
	} 
	return true;
  }
  
  /**
  Insert stock into database.
  @param id   product id of item
  @param wid  warehouse id of warehouse holding item
  @param qty  quantity of item in warehouse
  **/
  public boolean insertStock(int id, int wid, double qty) {
    int rowsEffected = 0;

    try{
      String insert="INSERT INTO stock(pid, wid, quantity) VALUES (?,?,?);";
  
      // quantity check
      if(qty < 0){
        return false;
      }      

      ps = connection.prepareStatement(insert);
      ps.setInt(1, id); // Fill in the first ?
      ps.setInt(2, wid); // Fill in the second ?
      ps.setDouble(3, qty); // Fill in the third ?
      rowsEffected=ps.executeUpdate();
      
      ps.close();

    }catch(Exception e){
      ////System.out.println(e);
      return false;
    }

    if (rowsEffected == 1){
        return true;
      }else{
        return false;
      }
  }
  
   /**
  Update an already existing stock in the database.
  @param id   product id of item
  @param wid  warehouse id of warehouse holding item
  @param qty  quantity of item in warehouse
  **/
   public boolean updateStock(int id, int wid, double qty) {
   int rowsEffected = 0;

   try{
      String insert="UPDATE stock SET quantity = ? WHERE pid = ? AND wid = ? AND quantity >= ?;";
  
      // quantity check
      if(qty < 0){
        return false;
      }      

      ps = connection.prepareStatement(insert);
      ps.setDouble(1, qty); // Fill in the first ?
      ps.setInt(2, id); // Fill in the second ?
      ps.setInt(3, wid); // Fill in the third ?
      ps.setDouble(4, qty); // Fill in the fourth ?
      rowsEffected=ps.executeUpdate();
      
      ps.close();

    }catch(Exception e){
      //System.out.println(e);
      return false;
    }

    if (rowsEffected == 1){
        return true;
      }else{
        return false;
      }
  }
   
  /**
  Inserts an order into the database if cid, pid, wid exists and qty, price > 0.
  **/
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
    
    int rowsEffected = 0;

    try{
      String insert="INSERT INTO orders(cid, pid, shipwid, quantity, price, odate, status) VALUES (?,?,?,?,?,?,?);";

      // quantity check
      if(qty < 0 || price < 0){
        return -1;
      }

      ps = connection.prepareStatement(insert);
      ps.setInt(1, cid);
      ps.setInt(2, pid);
      ps.setInt(3, wid);
      ps.setDouble(4, qty);
      ps.setDouble(5, price);

      java.util.Date date = new java.util.Date();
      java.sql.Date sqlDate = new java.sql.Date(date.getTime());
      ps.setDate(6, sqlDate);
      
      ps.setString(7, "O");
      rowsEffected = ps.executeUpdate();
      ps.close();

      // try to find that order again, and get its 'oid'
      sql = connection.createStatement();
      rs = sql.executeQuery("select currval(pg_get_serial_sequence('orders','oid'));");

      if(!rs.next()) return -1;

      int oid = rs.getInt(1);
      sql.close();
      rs.close();
      return oid;

    }catch(Exception e){
      //System.out.println(e);
      return -1;
    }
  }

  public boolean cancelOrder(int oid) {
    int rowsEffected = 0;

    try{
      String delete="DELETE FROM orders WHERE oid = ? AND status = 'O';";
      ps = connection.prepareStatement(delete);
      ps.setInt(1, oid);
      rowsEffected = ps.executeUpdate();
      ps.close();

      if(rowsEffected == 1){
        return true;
      }


    }catch(Exception e){
      //System.out.println(e);
      return false;
    }
	
	 return false;
  }

  public boolean shipOrder(int oid){

    try{

      // dont commit everything at once, 
      // incase something bad happens half way.
      if (!setAutoCommitFalse()) return false;

      // get the order with that oid
      String selectSql = "SELECT * FROM orders WHERE oid = ? AND status = 'O' LIMIT 1";
      ps = connection.prepareStatement(selectSql);
      ps.setInt(1, oid);
      rs = ps.executeQuery();

      if(!rs.next()) {rollback(); return false;}

      int pid = rs.getInt(3);
      double qty = rs.getDouble(6);
      int wid = rs.getInt(5);

      ps.close();
      rs.close();

      // get the tuple in stock that has that pid, wid and quantity >= qty
      selectSql = "SELECT * FROM stock WHERE pid = ? AND wid = ? AND quantity >= ? LIMIT 1";
      ps = connection.prepareStatement(selectSql);
      ps.setInt(1, pid);
      ps.setDouble(2, wid);
      ps.setDouble(3, qty);
      rs = ps.executeQuery();

      if(!rs.next()) {rollback(); return false;}

      double stockQty = rs.getDouble(3);

      double newQty = stockQty - qty;

      if(!updateStock(pid, wid, newQty)) {rollback(); return false;}

      // finally update the order status to shipped
      int rowsEffected = 0;
      String update = "UPDATE orders SET status = 'S' WHERE oid = ?;";
      ps = connection.prepareStatement(update);
      ps.setInt(1, oid);
      rowsEffected = ps.executeUpdate();
      ps.close();

      if (rowsEffected == 0) {rollback(); return false;}

      if (!commitChanges()) {rollback(); return false;}

      return true;

    }catch(Exception e){
      //System.out.println(e);
      rollback();
      return false;
    }

  }
  
  public String listStock(int pid){

    String resultString = "";

    try{
      String selectSql = "SELECT * FROM stock WHERE pid = ? AND quantity > 0 ORDER BY quantity DESC;";
      ps = connection.prepareStatement(selectSql);
      ps.setInt(1, pid);
      rs = ps.executeQuery();

      while(rs.next()){
        int wid = rs.getInt(2);
        double qty = rs.getDouble(3);

        resultString += String.valueOf(wid);
        resultString += ":";
        resultString += String.format("%.2f", qty);

        // check if result set has a next element
        if((!rs.isLast() && ((rs.getRow() != 0) || rs.isBeforeFirst()))){
          resultString += "#";
        }
        
      }

      ps.close();
      rs.close();

    }catch(Exception e){
      //System.out.println(e);
      return "";
    }

	return resultString;
  }
  
  public String listReferrals(int cid){
    String resultString = "";

    try{
      String selectSql = "SELECT custref, cname FROM (SELECT custref FROM referral WHERE custid = ?) AS cust, customer WHERE customer.cid = cust.custref ORDER BY cname DESC;";
      ps = connection.prepareStatement(selectSql);
      ps.setInt(1, cid);
      rs = ps.executeQuery();

      while(rs.next()){
        int custref = rs.getInt(1);
        String cname = rs.getString(2);

        resultString += String.valueOf(custref);
        resultString += ":";
        resultString += cname;

        // check if result set has a next element
        if((!rs.isLast() && ((rs.getRow() != 0) || rs.isBeforeFirst()))){
          resultString += "#";
        }
        
      }

      ps.close();
      rs.close();

    }catch(Exception e){
      //System.out.println(e);
      return "";
    }

  return resultString;
  }
    
  public boolean updateDB(){
    // incase something bad happens half way.
    if (!setAutoCommitFalse()) return false;

   int rowsEffected = 0;

   try{
      String create="CREATE TABLE bestsellers as (SELECT pid, quantity * price as sales FROM orders WHERE status = 'S' AND quantity * price > 10000);";     
      ps = connection.prepareStatement(create);
      rowsEffected=ps.executeUpdate();

      create="ALTER Table bestsellers ALTER COLUMN sales TYPE NUMERIC(10,2);";     
      ps = connection.prepareStatement(create);
      rowsEffected=ps.executeUpdate();

      if (!commitChanges()) {rollback(); return false;}
      
      ps.close();

      return true;

    }catch(Exception e){
      //System.out.println(e);
      rollback();
      return false;
    }    
  }

  // might need to remove the below methods before submitting
  public boolean setAutoCommitFalse(){
    try{
      connection.setAutoCommit(false);
    }
    catch(Exception e){
      return false;
    }

    return true;
  }

  public boolean setAutoCommitTrue(){
    try{
      connection.setAutoCommit(true);
    }
    catch(Exception e){
      return false;
    }

    return true;
  }

  public boolean commitChanges(){
    try{
      connection.commit();
    }
    catch(Exception e){
      return false;
    }

    return true;
  }

 public boolean rollback(){
    try{
      connection.rollback();
    }
    catch(Exception e){
      return false;
    }

    return true;
  }
  
}
