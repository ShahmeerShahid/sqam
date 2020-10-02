import java.sql.*;  //added 
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Assignment2 {
    
  // A connection to the database  
  Connection connection;
  
  // Statement to run queries
  Statement sql;   //basically a string
  
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
        Class.forName("org.postgresql.Driver");
      }
      catch (ClassNotFoundException e) {
      return false; 
      }
      try  {
        connection = DriverManager.getConnection(URL, username, password);
      }
      catch (SQLException e){
        return false;
      }

      return true;
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
      if (rs != null && ps != null && connection != null) {
        try {
          rs.close();
          ps.close();
          connection.close();
        } catch (SQLException e) {
          return false;
        }
      } 

      return true;    
  }
    
  public boolean insertStock(int id, int wid, double qty) {
    if (qty < 0) {
	return false;
    }

    // create a Statement from the connection.  Statement statement = conn.createStatement();
    // insert the data   statement.executeUpdate(}
    try {
      String state1 = "SELECT pid FROM product WHERE pid = ?";
      ps = connection.prepareStatement(state1);
      ps.setInt(1, id);
      
      rs = ps.executeQuery();
      if (!rs.next()) {
      	return false;
      }
      int p = rs.getInt("pid");
      if (p != id) { //pid have to exist
        return false;
      }
      String state2 = "SELECT wid FROM warehouse WHERE wid = ?;";
      ps = connection.prepareStatement(state2);
      ps.setInt(1, wid);
      rs = ps.executeQuery();
      if (!rs.next()) {
          return false;
      }
	
      int w = rs.getInt("wid");
      if (w != wid) { //wid have to exist as well
        return false;
      }
      //check if the stock exist and if so return false because not unique
      String state3 = "SELECT pid, wid, quantity FROM stock WHERE pid = ? AND wid = ? AND quantity >= 0;";
      ps = connection.prepareStatement(state3);
      
      ps.setInt(1, id);
      ps.setInt(2, wid);
      rs = ps.executeQuery();
      if (rs.next()) {
          return false;
      } 

      //everthg good do insert. insert into stock (pid, wid, quantity) values (5, 3, 22);
      String state4 = "INSERT INTO stock VALUES(?, ?, ?);";   //check this out ?????
      ps = connection.prepareStatement(state4);       
      ps.setInt(1, id);
      ps.setInt(2, wid);
      ps.setDouble(3, qty);
      ps.executeUpdate();
      return true;
    } catch (SQLException e) {
      return false; 
    } 
    
  }
  
   public boolean updateStock(int id, int wid, double qty) {
    try {
	double quan;
      String checkStock = "SELECT quantity FROM stock WHERE pid  = ? AND wid = ?;";
      ps = connection.prepareStatement(checkStock);
      ps.setInt(1, id);
      ps.setInt(2, wid);
      rs = ps.executeQuery();
      //rs.next();

      if (rs == null) {
        return false;
      }
      if (!rs.next()){
        return false;
      }

      quan = rs.getDouble(1);

      double abs_qty = Math.abs(qty);
      if (abs_qty > quan) {
        return false;
      }

      qty += quan; // updating with stock 
      //ready to update stock 
      String updateStock = "UPDATE stock SET quantity = ? WHERE pid = ? AND wid = ?;";
      ps = connection.prepareStatement(updateStock); //prepare it to use further
      ps.setDouble(1, qty);
      ps.setInt(2, id);
      ps.setInt(3, wid);

      ps.executeUpdate();
      return true;

    } catch (SQLException e) {
      return false;

    }
   
  }
   

  public int insertOrder(int cid, int pid, int wid, double qty, double price){
      try {
          String sql1 = "SELECT cid FROM customer WHERE cid = ?;";
          ps = connection.prepareStatement(sql1);
	  ps.setInt(1, cid);
          rs = ps.executeQuery();
          rs.next(); //reads the first tuple from the resulting query
          int a = rs.getInt("cid");
          if (a!=cid) {
              return -1;
          }
          
          String sql2 = "SELECT pid FROM product WHERE pid = ?;";
          ps = connection.prepareStatement(sql2);
	  ps.setInt(1, pid);
          rs = ps.executeQuery();
          rs.next(); //reads the first tuple from the resulting query
          int b = rs.getInt("pid");
          if (b!=pid) {
              return -1;
          }
          String sql3 = "SELECT wid FROM warehouse WHERE wid = ?;";
          ps = connection.prepareStatement(sql3);
	  ps.setInt(1, wid);
          rs = ps.executeQuery();
          rs.next(); //reads the first tuple from the resulting query
          int c = rs.getInt("wid");
          if (c!=wid) {
              return -1;
          }
          
          String insert = "INSERT INTO orders VALUES(?, ?, ?, ?, ?, ?, ?);";
          //int oid = Integer.parseInt("SELECT currval(pg_get_serial_sequence(‘orders’,’oid’));") +1;
          // Adds 1 on to previous oid to make new oid
          //fix date try using calendar java 
	  String date = "SELECT GETDATE();"; //gets system date
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
          Date date2;
          try{
              date2 = dateFormat.parse(date);
          }
          catch (ParseException e) {
              return -1;
          }

          
          java.sql.Date date3 = new java.sql.Date(date2.getTime());
          String status = "O";
          ps = connection.prepareStatement(insert);
          //ps.setInt(1, oid);
          ps.setInt(1, cid);
          ps.setInt(2, pid);
          ps.setDate(3, date3);
          ps.setInt(4, wid);
          ps.setDouble(5, qty);
          ps.setDouble(6, price);
          ps.setString(7, status);
          ps.executeUpdate();
          rs.next();
          //int orderNumber = rs.getInt("oid");
	  ps = connection.prepareStatement("SELECT currval(pg_get_serial_sequence(‘orders’,’oid’))"); //prepare it to use further
      
	  rs = ps.executeQuery();
          return rs.getInt(1);
      } catch (SQLException e) {
	  System.out.println(e);
          return -1;
      }

  }


  public boolean cancelOrder(int oid) {
    try {
      String cancel = "SELECT * FROM orders WHERE oid = ? ;";
      ps = connection.prepareStatement(cancel);
      ps.setInt(1, oid);
      rs = ps.executeQuery();
      if (!rs.next()) { return false; }
      String s = rs.getString("status");
      if (s.equals('S'))  {
        return false;
      } else {
        String delete = "DELETE FROM orders WHERE oid = ?;";
	ps = connection.prepareStatement(delete);        
	ps.setInt(1, oid);
        ps.executeUpdate();
        return true;
      }
    } catch (SQLException e) {
      return false;
    } 
    //return false;

  }




  public boolean shipOrder(int odi){
   try {
    String shipOrder = "SELECT pid, shipwid, quantity FROM orders WHERE oid = ?;";
    ps = connection.prepareStatement(shipOrder);
    ps.setInt(1, odi);
    rs = ps.executeQuery();
    if (!rs.next()) {
	return false;
	}
    int pid = rs.getInt("pid");
    int wid = rs.getInt("shipwid");
    int quantity = rs.getInt("quantity");

    String shipStock = "SELECT pid, wid, quantity FROM stock WHERE pid = ? AND wid = ?;";
    ps = connection.prepareStatement(shipStock);
    ps.setInt(1, pid);
    ps.setInt(2, wid);
    rs = ps.executeQuery();
    if (!rs.next()) {
	return false;
	}
    int stockQuantity = rs.getInt("quantity");

    if (stockQuantity >= quantity) {
	quantity *= -1;
      if (updateStock(pid, wid, quantity) == false) {
        return false;
      }
      //After checking everything, now update it 
      String update = "UPDATE orders SET status = 'S' WHERE oid = ?;";
      ps = connection.prepareStatement(update);
      ps.setInt(1, odi);
      ps.executeUpdate();
      //transaction stuff didn't work out may be later 
      //con.commit(); // Commit changes to database
      return true;
    } else {
      return false;
    }
   } catch (SQLException e) {
      return false;
   }      
  }


  
  public String listStock(int pid){
	 String returnLst = "";
   try {
    String lst = "SELECT wid, quantity FROM stock WHERE quantity > 0 AND pid = ? ORDER BY quantity DESC;"; 
    ps = connection.prepareStatement(lst);
    ps.setInt(1, pid);
    rs = ps.executeQuery();

    DecimalFormat df = new DecimalFormat("#.00");
    //String angleFormated = df.format(rs.getDouble("qty")); //don't need it 

    while (rs.next()) { // in this order -> wid1:qty1#wid2:qty2 ...
      returnLst += Integer.toString(rs.getInt("wid"));
      returnLst += ":";
      returnLst += df.format(rs.getDouble("quantity"));
      returnLst += "#";
    }

    if (returnLst.length() > 0 ) {   
        returnLst = returnLst.substring(0, returnLst.length() -1 );
      }
    return returnLst;

   } catch (SQLException e) {
      return "";
   } finally {
      return returnLst;
   }
 }



  public String listReferrals(int cid){
    String returnLst = "";
    try {
      String lst = "SELECT c.cid, c.cname FROM customer c JOIN referral r ON c.cid = r.custref WHERE r.custid = ? ORDER BY c.cname ASC;";
      ps = connection.prepareStatement(lst);
      ps.setInt(1,cid);
      rs = ps.executeQuery();
      if (rs == null) {
	return "";
      }
      while (rs.next()) { //in this order -> cid1:name1#cid2:name2 ...
        returnLst += Integer.toString(rs.getInt("cid"));
        returnLst += ":";
        returnLst += rs.getString("cname");
        returnLst += "#";
      }

      if (returnLst.length() > 0 ) {
        returnLst = returnLst.substring(0, returnLst.length() -1 );
      }
      return returnLst;

    } catch (SQLException e) {
        return "";
    } finally {
        return returnLst;
    }
  }
   


    public boolean updateDB(){
        try {
            String bstable = "CREATE TABLE bestsellers(pid INTEGER, sales NUMERIC(10,2));";
            ps = connection.prepareStatement(bstable);
            ps.executeUpdate();
            
            String shipped = "SELECT pid, SUM(price * quantity) as sales FROM orders WHERE status = 'S' GROUP BY pid";
            ps = connection.prepareStatement(shipped);
            rs = ps.executeQuery();
            if (rs == null) {
               return false;
	    }
		
            while(rs.next()){
                if(rs.getDouble("sales") > 10000.00){
                    String s = "INSERT INTO bestsellers VALUES(?, ?);";
                    ps = connection.prepareStatement(s);
                    ps.setInt(1, rs.getInt("pid"));
                    ps.setDouble(2, rs.getDouble("sales"));
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            return false;
        } finally {
            return true;
        }
    }
  
}















