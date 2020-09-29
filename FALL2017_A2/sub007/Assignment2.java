import java.sql.*;
import java.util.*;
import java.text.*;
import java.time.*;

public class Assignment2 {

  // A connection to the database
  Connection connection;

  // Statement to run queries
  Statement sql;

  // Prepared Statement
  PreparedStatement ps;

  // Resultset for the query
  ResultSet rs;

  // String for queries
  String q;

  String cid = ""; // Customer id
  String cname = ""; // Customer name
  int pid = 0; // Product id
  int wid = 0; // Warehouse id
  int qty = 0; // Quantity of orders
  String status = ""; // Status 'S' or 'O'
  int quantity = 0; // Quantity in stock
  int sales = 0; // Sales sum (qty * price)

  //CONSTRUCTOR
  Assignment2(){
      try {
        Class.forName("org.postgresql.Driver");
      } catch (ClassNotFoundException e) {
          System.out.println("Failed to find the JDBC driver");
      }
  }

  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password){
      try {
          connection = DriverManager.getConnection(URL, username, password);
          sql = connection.createStatement();
      } catch (SQLException e) {
          return false;
      }
      return true;
  }

  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
      try {
        // Close Result Set
          if (rs != null) {
            rs.close();
          }
          // Clost PreparedStatement
          if (ps != null) {
            ps.close();
          }
          // Clost PreparedStatement
          if (sql != null) {
            sql.close();
          }
          // Close conneciton
          if (connection != null) {
            connection.close();
          }
      } catch (SQLException e) {
          return false;
      }
      return true;
  }

  public boolean insertStock(int pid, int wid, double qty) {
      try {
        // Return false if quantity is negative
          if (qty < 0) {
              throw new SQLException ("Quantity can't be negative!\n");
          }
          q = "INSERT INTO stock VALUES(" + pid + ", " + wid + ", " + qty + ")";
          sql = connection.createStatement();
          sql.executeUpdate(q);
      } catch (SQLException e) {
          return false;
      }
      return true;
  }

   public boolean updateStock(int pid, int wid, double qty) {
       int quantity = 0;
       try {
           ps = connection.prepareStatement("SELECT quantity FROM stock WHERE pid=" + pid + "");
           rs = ps.executeQuery();
           if (rs.next()) {
                quantity = rs.getInt(1);
           }
           // Value is negative and greater then current stock
           if (Math.abs(qty) > quantity && qty < 0) {
                throw new SQLException("Value is negative and absolute is greater then current quantity!\n");
           }
           q = "UPDATE stock SET quantity=" + (quantity + qty) + " WHERE pid=" + pid + " AND wid=" + wid + "";
           sql.executeUpdate(q);
       } catch (SQLException e) {
            return false;
       }
       return true;
  }

  public int insertOrder(int cid, int pid, int wid, double qty, double price){
   int recentOid = -1;
      try {
          // Can't order a negative quantity of products
          if (qty < 0) {
              throw new SQLException ("Negative qty!");
          }
          // Gets next posible oid in orders_oid_seq
          q = "SELECT nextval('orders_oid_seq')";
          ps = connection.prepareStatement(q);
          rs = ps.executeQuery();
          while (rs.next()) {
            recentOid = rs.getInt(1);
          }
          // Gets LocalDate
          LocalDate today = LocalDate.now();
          q = "INSERT INTO orders VALUES(" + recentOid + ", " + cid + ", " + pid + ", '" + today + "', " + wid + ", " + qty + ", " + price + ", 'O')";
          sql.executeUpdate(q);
      } catch (SQLException e) {
          return -1;
      }
      return recentOid;

  }

  public boolean cancelOrder(int oid) {
   try{
	     q = "DELETE FROM orders where oid = " + oid + " and status = 'O'";
       ps = connection.prepareStatement(q);
       // Can't cancel an order if it does not exist
       if (ps.executeUpdate() == 0) {
            return false;
        }
   }
   catch(SQLException e){
   	return false;
   }
   return true;
  }

  public boolean shipOrder(int oid){
    boolean notEmpty = false;
   try{
   	q = "SELECT orders.pid, shipwid, orders.quantity, status, stock.quantity FROM stock, orders WHERE shipwid=wid AND orders.pid=stock.pid AND oid=" + oid + "";
    ps = connection.prepareStatement(q);
	  rs = ps.executeQuery();
    if (rs.next()) {
            pid = rs.getInt(1);
            wid = rs.getInt(2);
            qty = rs.getInt(3);
            status = rs.getString(4);
            quantity = rs.getInt(5);
            notEmpty = true;
        // Order was already shipped
        if (status.equals("S")) {
            return false;
        } else {
          // Order is not shipped
            return updateStock(pid, wid, (qty * -1));
        }
    }
   }
   catch (SQLException e){
   	return false;
   }
   return notEmpty;
  }

  public String listStock(int pid){
      String stock = "";
      try {
            q = "SELECT wid, quantity FROM stock WHERE pid=" + pid + " ORDER BY quantity DESC";
            ps = connection.prepareStatement(q);
            rs = ps.executeQuery();
            while (rs.next()) {
                wid = rs.getInt(1);
                quantity = rs.getInt(2);
                // Only add wid:qty# if qty > 0
                if (quantity > 0) {
                    String s = wid + ":" + quantity + "#";
                    stock = stock + s;
                }
            }
        } catch (SQLException e) {
            return "";
        }
        stock = removePound(stock); //String formatting
	return stock;
  }

  private String removePound (String output) {
        if (!output.equals("") && output.charAt(output.length() - 1) == '#') {
            output = output.substring(0, output.length() - 1);
        }
        return output;
  }
  public String listReferrals(int cid){
      String result = "";
      try {
    q = "SELECT cid, cname FROM customer, referral WHERE cid = custid AND custref=" + cid + " ORDER BY cname ASC";
    ps = connection.prepareStatement(q);
    rs = ps.executeQuery();
    while (rs.next()) {
       cid = rs.getInt(1);
       cname = rs.getString(2);
       result = result + cid + ":" + cname + "#"; // Compile referrals
    }
      } catch (SQLException e) {
            return "";
        }
        result = removePound(result);
    return result;
  }

  public boolean updateDB(){
    try {
        q = "SELECT * FROM (SELECT pid, sum(quantity*price) AS sales FROM orders WHERE status='S' GROUP BY pid) AS bestsellers WHERE bestsellers.sales > 10000.00";
        ps = connection.prepareStatement(q);
        rs = ps.executeQuery();
        // Create the bestsellers table
        String sqlText = "CREATE TABLE bestsellers(pid INT,   " +
                                        "sales NUMERIC(10, 2))";
        sqlText.replaceAll("\\s+", " ");
        sql = connection.createStatement();
        sql.executeUpdate(sqlText);
        while (rs.next()) {
            pid = rs.getInt(1);
            sales = rs.getInt(2);
            q = "INSERT INTO bestsellers VALUES(" + pid + ", " + sales + ")";
            ps = connection.prepareStatement(q);
            ps.executeUpdate();
        }
    } catch (SQLException e) {
        System.out.println(e);
        return false;
    }
	return true;
  }
}
