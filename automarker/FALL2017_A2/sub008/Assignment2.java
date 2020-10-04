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
		connection = DriverManager.getConnection("jdbc:postgresql://mcsdb.utm.utoronto.ca:5432/" + username + "_343", username, password);  
		return true;
	  }
	  catch (SQLException e){
		  return false;
	  }
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
	  try{
		  connection.close();
		  return true;
	  }
	  catch (SQLException e){
		  return false;
	  }    
  }
  
  //inserts a new row into stock relation. Returns true if insert was successful
  public boolean insertStock(int id, int wid, double qty) {
//intitialize variables
      boolean isIn = false;

      //cant insert less then 0 items into stock
      if (qty < 0){
          return false;
      }

      //going through current stock relation to check for multiple reasons we would return false
      ps = connection.prepareStatement("SELECT pid, wid FROM stock");
      rs = ps.executeQuery();
      while (rs.next()){
          //pid + wid is a key so check for that
          if (id == rs.getInt("pid") && wid == rs.getInt("wid")){
              return false;
          }
      }
      //making sure the pid we are adding is in relation product
      ps = connection.prepareStatement("SELECT pid FROM product");
      rs = ps.executeQuery;
      while(rs.next()){
          if (id == rs.getInt("pid")){
              isIn = true;
          }
      }
      if (isIn == false){
          return false;
      }
      isIn = false;
      //making sure the wid we are adding is in relation warehouse
      ps = connection.prepareStatement("SELECT wid FROM warehouse");
      rs = ps.executeQuery;
      while(rs.next()){
          if (wid == rs.getInt("wid")){
                  isIn = true;
              }
          }
          if (isIn == false){
              return false;
          }
      //adding row into stock relation
      ps = connection.prepareStatement("INSERT INTO stock VALUES (?, ?, ?)");
      ps.setInt(1, id); ps.setInt(2, wid); ps.setDouble(3, qty);
      try{
          ps.executeUpdate();
      }
      catch(SQLException e){
          return false;
      }
      return true;
  }
  
   public boolean updateStock(int id, int wid, double qty) {
	   if (qty < 0){
			qty = qty*qty;
			qty = qty/qty;
	   }
	  ps = connection.prepareStatement("SELECT pid, wid FROM stock");
	  rs = ps.executeQuery();
	  while(rs.next()){
		  if(id == rs.getInt("pid") && wid == rs.getInt("wid")){
			  ps = connection.prepareStatement("UPDATE stock SET qty = ? WHERE pid = ? AND wid = ?");
			  ps.setDouble(1, qty); ps.setInt(2, id); ps.setInt(3, wid);
			  try{
				  ps.executeUpdate();
			  }
			  catch(SQLException e){
				  return false;
			  }
			  return true;
		  }
	  }
   return false;
  }
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
  	int oid;
   if (qty < 0){
   	return -1;
   }
   boolean isIn = false;
   ps = connection.prepareStatement("SELECT cid FROM customer");
   rs = ps.executeQuery();
   while(rs.next()){
   	if(cid == rs.getInt("cid")){
   		isIn = true;
   	}
   }
   if(!isIn){
   	return -1;
   }
   isIn = false; 
   ps = connection.prepareStatement("SELECT pid FROM product");
   rs = ps.executeQuery();
   while(rs.next()){
   	if(pid == rs.getInt("pid")){
   		isIn = true;
   	}
   }
   if(!isIn){
   	return -1;
   }
   isIn = false; 
   ps = connection.prepareStatement("SELECT wid FROM warehouse");
   rs = ps.executeQuery();
   while(rs.next()){
   	if(wid == rs.getInt("wid")){
   		isIn = true;
   	}
   }
   if(!isIn){
   	return -1;
   }

   ps = connection.prepareStatement("SELECT currval(pg get serial sequence(‘orders’,’oid’))");
   rs = ps.executeQuery();
   rs.next();
   oid = rs.getInt("oid") + 1;

   ps = connection.prepareStatement("INSERT INTO orders VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
	ps.setInt(1, oid); ps.setInt(2, cid); ps.setInt(3, pid); ps.setDate(4, java.sql.Date(utilDate.getTime()));
	ps.setInt(5, wid); ps.setDouble(6, qty); ps.setDouble(7, price); ps.setString(8, "O");
   try{
		  ps.executeUpdate();
	  }
	  catch(SQLException e){
		  return -1;
	  }
	  return oid;

  }
	//TODO
  public boolean cancelOrder(int oid) {
   boolean isIn = false;
   boolean isord = false;
   ps = connection.prepareStatement("SELECT oid FROM orders");
   rs = ps.executeQuery();
   while(rs.next()){
   	if(oid == rs.getInt("oid")){
   		isIn = true;
   	}
   	if("O" == rs.getString("status")){
   		isIn = true;
   	}
   }
   if(!isIn || !isord){
   	return false;
   }

   ps = connection.prepareStatement("DELETE FROM orders WHERE oid = ?");
   ps.setInt(1, oid);
   try{
		  ps.executeUpdate();
	  }
	  catch(SQLException e){
		  return false;
	  }
	  return true;
  }

  public boolean shipOrder(int oid){
  	//has enough stock 
  	int diff = 0;
  	ps.connection.prepareStatement("SELECT orders.quantity, stock.quantity FROM orders, stock WHERE orders.shipwid = stock.wid, orders.pid = stock.pid, orders.oid = ?");
  	ps.setInt(1, oid);
  	rs = ps.executeQuery();
  	while(rs.next()){
  		diff = rs.getInt("stock.quantity") - rs.getInt("orders.quantity");
  		if(diff < 0){
			return false;
  		}
  	}
  	ps.connection.prepareStatement("SELECT wid, pid, quantity, oid FROM orders WHERE oid = ?");
  	ps.setInt(1, oid);
  	rs = ps.executeQuery();
  	rs.next();
  	updateStock(rs.getInt("wid"), rs.getInt("pid"), diff);

  	ps.connection.prepareStatement("UPDATE orders SET status = 'S' WHERE oid = ?");
  	ps.setInt(1, oid);
  	try{
		  ps.executeUpdate();
	  }
	  catch(SQLException e){
		  return false;
	  }
	  return true;
  }      
  
  //create a list of wid and quantity from stock with the format wid1:qty1#wid2:qty2:...widn:qtyn#
  public String listStock(int pid){
    String wid;
    String qty;
	String endListStock = "";
	ps = connection.prepareStatement("SELECT wid, quantity AS qty FROM stock AS s WHERE s.pid = ? ORDER BY quantity DESC");
	ps.setInt(1, pid);
	rs = ps.executeQuery();
	while(rs.next()){
        wid = Integer.toString(rs.getInt("wid"));
        qty = Integer.toString(rs.getInt("qty"));
		endListStock.concat(wid + ":");
		endListStock.concat(qty + "#");
	}
	return endListStock;
  }
  //TODO
  public String listReferrals(int cid){
    String referrallist = "";
    ps = connection.prepareStatement("SELECT cid, cname FROM referral, customer WHERE custid = ?, referral.custref = customer.cid ORDER BY cname ASC");
    ps.setInt(1, cid);
    rs = ps.executeQuery();
    while(rs.next()){
    	referrallist.concat(rs.getInt("cid") + ":");
    	referrallist.concat(rs.getInt("cname") + "#");
    }
    return referrallist;

  }
    //make a new table best sellers and populate it with total sales greater then 10000
  public boolean updateDB(){
	  ResultSet rsins;
	  sql = connection.createStatement();
	  try{
		sql.executeUpdate("CREATE TABLE bestsellers" +
	                    "(pid INTEGER, sales NUMERIC(10,2), PRIMARY KEY(pid))");
	  }
	  catch(SQLException e){
		  return false;
	  }
	  ps = connection.prepareStatement("SELECT p.pid, SUM(o.price*o.quantity) AS sales " +
										"FROM product AS p JOIN orders AS o ON p.pid = o.pid "+
										"WHERE o.status = 'S' " +
										"GROUP BY o.pid HAVING SUM(o.price*o.quantity) > 10000.00");
	  rs = ps.executeQuery();
	  while(rs.next()){
		  ps = connection.prepareStatement("INSERT INTO bestsellers VALUES(?, ?)");
		  ps.setInt(1, rs.getInt("pid"));
		  ps.setDouble(2, rs.getDouble("sales"));
		  try{
			 ps.executeUpdate();
		  }
		  catch(SQLException e){
			  return false;
		  }
	  }
	  return true;
  }
  
}
