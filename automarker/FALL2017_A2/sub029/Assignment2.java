import java.sql.*;

public class Assignment2 {
    
  // A connection to the database  
  Connection connection;
  
  // Statement to run queries
  Statement sql;
  
  // Prepared Statement
  // PreparedStatement ps;
  PreparedStatement pStatement;
  
  // Resultset for the query
  ResultSet rs;

  String queryString;
  
  //CONSTRUCTOR
  Assignment2(){
     try {
		  Class.forName("org.postgresql.Driver");
	    }
	    catch (ClassNotFoundException e) {
		    System.out.println("Failed to find the JDBC driver");
	    }

  }
  
  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password){
    try{
      connection = DriverManager.getConnection(URL, username, password);
    }
    catch (SQLException se){
      return false;
    }

      return true;
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){

    try
    {
      connection.close();
    }
    catch(SQLException se)
    {
      return false;
    }

    return true;    
  }
    

/******************

https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html
https://docs.oracle.com/javase/7/docs/api/java/sql/PreparedStatement.html

I think, if we are going to use prepared statements, we need to use the following syntax

queryString ="SELECT * FROM orders WHERE oid = ?";
pStatement = connection.prepareStatement(queryString);
pStatement.setInt(1, some_oid);


And we have to use executeQuery to get rs, executeUpdate returns int

*******************/

public boolean checkCID(int cid){
  try{
    // Check if pid exists
    queryString = "SELECT cid FROM product WHERE cid = ?";
    pStatement = connection.prepareStatement(queryString);
    // Oh my god, index starts at 1! Madness!!
    pStatement.setInt(1, cid);
    rs = pStatement.executeQuery();
    // If so, return true
    if (rs.next()){
      return true;
    }
    return false;
  }
  catch (SQLException se){
    return false;
  }
}

public boolean checkPID(int pid){
  try{
    // Check if pid exists
    queryString = "SELECT pid FROM product WHERE pid = ?";
    pStatement = connection.prepareStatement(queryString);
    pStatement.setInt(1, pid);
    rs = pStatement.executeQuery();
    // If so, return true
    if (rs.next()){
      return true;
    }
    return false;
  }
  catch (SQLException se){
    return false;
  }
}

public boolean checkWID(int wid){
  try{
    // Check if wid exists
    queryString = "SELECT wid FROM warehouse WHERE wid = ?";
    pStatement = connection.prepareStatement(queryString);
    pStatement.setInt(1, wid);
    rs = pStatement.executeQuery();
    // If so, return true
    if (rs.next()){
      return true;
    }
    return false;
  }
  catch (SQLException se){
    return false;
  }
}

public boolean checkPIDWID(int pid, int wid){
  try{
    // Check if pid, wid is in stock
    queryString = "SELECT pid, wid FROM stock WHERE wid = ? AND pid = ?" ;
    pStatement = connection.prepareStatement(queryString);
    pStatement.setInt(1, wid);
    pStatement.setInt(2, pid);
    rs = pStatement.executeQuery();
    // If it does, return true
    if (rs.next()){
      return true;
    }
    return false;
  }
  catch (SQLException se){
    return false;
  }
}

/*
  Inserts a row in stock table for the product pid, warehouse wid,
  and quantity qty. Return true iff the insertion is successful. An
  insertion can be successful if and only if product pid does exist
  in the product table, wid warehouse does exist in the warehouse
  table, no item with key pid, wid exists in stock table, and qty≥
  0.

  - pid in product
  - wid in warehouse
  - wid exists in stock
  - pid does not exist in stock
  - qty >= 0
  DOES THE DDL PREVENT SOME OF THIS?? NEED TO CHECK
    - REFERENCES should prevent this. Need test case for inserting with invalid pid/wid
*/
  public boolean insertStock(int pid, int wid, double qty) {
    try{
        // Check correct quantity and pid/wid existance
        if (qty < 0.0 || checkPIDWID(pid, wid)){
          return false;
        }

        // Insert the stock
        queryString = "INSERT INTO stock VALUES( ?, ?, ?)";
        pStatement.setInt(1, pid);
        pStatement.setInt(2, wid);
        pStatement.setDouble(3, qty);
        pStatement = connection.prepareStatement(queryString);
        // rs isn't needed so we can use executeUpdate
        pStatement.executeUpdate();
      }
      catch(SQLException se){
           return false;
      }

      return true;
  }
  

  /*
    Updates the quantity for a stock row for the product pid, warehouse
    wid, and quantity qty. For an update to be successful, the
    item with key pid, wid must exist in the stock table, and in
    case the qty is negative, the absolute value of qty must be less or
    equal than quantity on the corresponding row. Returns true iff
    the update is successful.

    - wid and pid must exist in stock
    - if qty is negative then qty must be <= qty in stock. 
  */
  public boolean updateStock(int pid, int wid, double qty) {
    //Check wid and pid exists in stock
    // SELECT * FROM stock WHERE wid = $wid AND pid = $pid;
    // Should be 1 row
    // May not need to do??

    // Check pid/wid existance
    if (!checkPIDWID(pid, wid)){
      return false;
    }

    try{
      queryString = "SELECT quantity FROM stock WHERE wid = ? AND pid = ?" ;
      pStatement = connection.prepareStatement(queryString);
      pStatement.setInt(1, wid);
      pStatement.setInt(2, pid);
      rs = pStatement.executeQuery();
      rs.next();
      float og_qty = rs.getFloat("quantity");

      //Deals with a negative quantity
      if(qty < 0){
        if (Math.abs(qty) > og_qty){return false;}
      }

      og_qty += qty;

      // Update the stock
      // Should return 1 row updated, if zero rows updated then pid/wid combo does not exist
      // Is this supposed to return the sum of wty and og_qty? Otherwise, why did we ensure |qty| <= og_qty
      queryString = "UPDATE stock SET qty = ? WHERE pid = ? AND wid = ?";
      pStatement = connection.prepareStatement(queryString);
      // rs isn't needed so we can use executeUpdate
      pStatement.setFloat(1, og_qty);
      pStatement.setInt(2, pid);
      pStatement.setInt(3, wid);
      pStatement.executeUpdate();
      return true;
    }
    catch(SQLException se){
         return false;
    }
  }

  /*
  Inserts a new order in orders table. The cid, pid, wid must
  exist respectively in the customer, product, warehouse tables,
  else the insertion cannot be successful. The order date must be set
  to current system date, order status must be set to ‘O’. If insertion
  is successful, return the order number, else return -1. Hint:
  select currval(pg get serial sequence(‘orders’,’oid’));
  will give you the latest order number.
  f
  - Set orderdate to current system date
  - Set orderstatus to 'O'
  - Get last order number -> select currval(pg get serial sequence(‘orders’,’oid’));
  - CHECK if pid, cid, wid exists in tables
  */
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
    if (!checkPID(pid) || !checkWID(wid) || !checkCID(cid)){
      return -1;
    }

    try{
      // Insert order
      // 
      // oid, cid, pid, odate, shipwid, quantity, price, status
      // We can generate oid using:
      // select currval(pg_get_serial_sequence(‘orders’,’oid’));
      
      // Generate oid
      queryString = "select currval(pg_get_serial_sequence(orders,oid))";
      pStatement = connection.prepareStatement(queryString);
      rs = pStatement.executeQuery();
      rs.next();
      // Must be int 
      int oid = rs.getInt("currval");
      
      queryString = "INSERT INTO orders VALUES(?, ?, ?, ?, ?, ?, O )";
      pStatement = connection.prepareStatement(queryString);
      pStatement.setInt(1, oid);
      pStatement.setInt(2, cid);
      pStatement.setInt(3, pid);
      pStatement.setInt(4, wid);
      pStatement.setDouble(5, qty);
      pStatement.setDouble(6, price);
      // rs isn't needed so we can use executeUpdate
      pStatement.executeUpdate();

      return oid;
    }
    catch (SQLException se){
      return -1;
    }
  }

  /*
    This method attempts to delete the order oid from the order
    table in case the order status is ‘O’. A shipped order (i.e. an
    order with status euqal to ‘S’) cannot be canceled. Return true
    iff the cancellation succeeds.

    - Cannot cancel a S order
    - DELETE otherwise
  */
  public boolean cancelOrder(int oid) {
    try{
      queryString = "SELECT status FROM orders WHERE oid = ?)";
      pStatement = connection.prepareStatement(queryString);
      pStatement.setInt(1, oid);
      rs = pStatement.executeQuery();
      rs.next();
      String status = rs.getString("status");
      if (status.equals("S")){return false;}

      queryString = "DELETE FROM orders WHERE oid = ?)";
      pStatement = connection.prepareStatement(queryString);
      pStatement.setInt(1, oid);
      // rs isn't needed so we can use executeUpdate
      pStatement.executeUpdate();

      return true;
    }
    catch (SQLException se){
      return false;
    }
  }


  /*
    This method attempts to ship the order oid in case there is enough
    stock to fulfill the order in the warehouse wid assigned to this
    order. In case shipping is possible, you must perform a database
    transaction that updates the order oid status to ‘S’ and at the
    same time, reduces the stock quantity with a magnitude equal to
    the order quantity. An order cannot be shipped in part. This
    method must make use of updateStock method. Return true iff
    the shipping succeeds.

    - Perform a transaction (ability to roll back if one command is unable to run)
    - Update order status to 'S'
    - Reduce stock quantity by the order quantity
    - use update stock
  */
  public boolean shipOrder(int oid){
    // Update Order to 'S'
    // UPDATE orders SET status = 'S' WHERE oid = $oid$;
    // Should update 1 row

    int pid = -1;
    float requested_quantity = -1.0f;
    int wid = -1;
    try{
      queryString = "SELECT pid, quantity, shipwid, status FROM orders WHERE oid = ?";
      pStatement = connection.prepareStatement(queryString);
      pStatement.setInt(1, oid);
      rs = pStatement.executeQuery();
      rs.next();
      pid = rs.getInt("pid");
      requested_quantity = rs.getFloat("quantity");
      wid = rs.getInt("shipwid");
      String status = rs.getString("status");

      queryString = "SELECT quantity FROM stock WHERE wid = ?";
      pStatement = connection.prepareStatement(queryString);
      pStatement.setInt(1, wid);
      rs = pStatement.executeQuery();
      rs.next();
      float stock_quantity = rs.getFloat("quantity");

      if (stock_quantity < requested_quantity || status.equals("S")){return false;}
      
      //Reduce Stock
      // queryString = "SELECT pid, shipwid, quantity FROM orders WHERE oid = "+oid;
      // SELECT quantity FROM warehouse WHERE wid = $shipwid$ AND pid = $pid$;
      // Subtract the first quantity from the second and set it to the updated value for stock
      
      // Depends on how updateStock works. Does it set the value or add to it?
    }
    catch (SQLException se){
      return false;
    }
    try{
      connection.setAutoCommit(false);

      updateStock(pid, wid, requested_quantity);
      // Update order status
      queryString = "UPDATE orders SET status = 'S' WHERE oid = ?";
      pStatement = connection.prepareStatement(queryString);
      pStatement.setInt(1, oid);
      pStatement.executeUpdate();
      // rs.next();

      connection.commit();
      return true;   
    }
    catch (SQLException se){
      try{
        connection.rollback();        
      }
      catch (SQLException se2){
        throw se;
      }
      finally{
        return false;
      }
    }     
  }


  /*
    This method returns a string formatted as follows:
    wid1:qty1#wid2:qty2 ...
    where wid1, wid2, ... are warehouse IDs where the product
    pid is available (quantity > 0) and qty1, qty2, ... respective
    quantities, formatted as decimal numbers with two digits after the
    decimal point, sorted in decreasing order of quantities. Return the
    empty string if no quantity avaliable.
  */ 
  public String listStock(int pid){
    try{
      // Get the warehouses with stock greater than zero
      // SELECT wid, quantity FROM stock WHERE quantity > 0 AND pid = $pid$ ORDER BY quantity DESC;
      // Loop through entries creating string... wid1:qty1 + "#";
      String out = "";
      queryString = "SELECT wid, quantity FROM stock WHERE quantity > 0 AND pid = ? ORDER BY quantity DESC";
      pStatement = connection.prepareStatement(queryString);
      pStatement.setInt(1, pid);
      rs = pStatement.executeQuery();

      int wid;
      float quantity;
      while (rs.next()){
        wid = rs.getInt("wid");
        quantity = rs.getFloat("quantity");
        out+=wid+":"+quantity+"#";
      }
      // Remove last #
      return out.substring(0, out.length()-1);
    }
    catch (SQLException se){
      return "";
    }
  }
  
  /*
    This method returns a string formatted as follows:
    cid1:name1#cid2:name2 ...
    where cid1, cid2, ... are customer IDs referred by cid and
    name1, name2, ... are their names, sorted alphabetically by
    name. Return the empty string if cid has made no referrals.

    -
  */
  public String listReferrals(int cid){
    try{
      // SELECT r.custref, c.cname AS name FROM referral r JOIN customer c ON r.custref = c.cname WHERE r.custid = $cid$ ORDER BY name ASC;
      String out = "";
      queryString = "SELECT r.custref AS custref, c.cname AS name FROM referral r JOIN customer c ON r.custref = c.cname WHERE r.custid = ? ORDER BY name ASC";
      pStatement = connection.prepareStatement(queryString);
      pStatement.setInt(1, cid);
      rs = pStatement.executeQuery();

      int _cid;
      String name;
      while (rs.next()){
        _cid = rs.getInt("custref");
        name = rs.getString("name");
        out+=_cid+":"+name+"#";
      }
      // Remove last #
      return out.substring(0, out.length()-1);
    }
    catch (SQLException se){
      return "";
    }
  }

  /*
    Create a new table called bestsellers for products with total
    sales amount greater than 10,000.00 dollars. The attributes of
    this table must be:
    pid INTEGER -- product ID
    sales NUMERIC(10,2) -- total sales amount
    Please note the sales amount for each shipped order equals the
    product of the quantity and the price rounded up two decimals.
    Orders with status ‘O’ do not count. Return true iff the database
    is updated successfully. Otherwise return false
  */
  public boolean updateDB(){
    try{
      queryString = "CREATE TABLE bestsellers " +
                     "(pid INTEGER NOT NULL, " +
                     " sales INTEGER NOT NULL, " + 
                     " PRIMARY KEY ( pid ))";
      pStatement = connection.prepareStatement(queryString);
      // rs isn't needed so we can use executeUpdate
      pStatement.executeUpdate();

      queryString = "INSERT INTO bestsellers (SELECT pid, sum(price*quantity) AS sales FROM orders WHERE status = 'S' AND sales > 10000 GROUP BY pid)";
      pStatement = connection.prepareStatement(queryString);
      // rs isn't needed so we can use executeUpdate
      pStatement.executeUpdate();

      return true;
    }
    catch (SQLException se){
      return false;
    }

    /*
    If above doesn't work, we can insert each row using the following loop
    
    int pid, sales;
    while(rs.next()){
      pid = rs.getInt("pid");
      sales = rs.getInt("sales")
    }
    */
  }

  public static void main(String[] args){    

  }
}
