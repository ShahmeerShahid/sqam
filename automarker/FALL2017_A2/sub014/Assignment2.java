import java.sql.*;
import java.io.*;
import java.util.Date;
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
		//System.out.println("Failed to find the JDBC driver");
	    }	
  }

 public static void main(String args[]) throws IOException {
	Assignment2 a = new Assignment2();
	if (!a.connectDB("jdbc:postgresql://mcsdb.utm.utoronto.ca:5432/surisaga_343", "surisaga", "04667")) {
		//System.out.println("fail connection");
	}
	//a.insertStock(1, 3, 99);
	//a.updateStock(1, 3, -101);
	//a.insertOrder(300, 1, 3, 6, 100);
	//a.insertOrder(200, 1, 1, 1, 100);
	//a.cancelOrder(28);
	//a.shipOrder(30);
	//a.listStock(1);
	//a.listReferrals(100);
	//a.updateDB();
	//a.disconnectDB();
	}
  
  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password){
	try
            {
		connection = DriverManager.getConnection(URL, username, password);
	} catch (SQLException e) {
 		//System.out.println("Connection Failed! Check output console");
		//System.out.println(e);
		return false;
	}     
      	return true;
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
      try {
	 rs.close();
	 ps.close();
	 sql.close();
	 
     	 connection.close();
	 return true;    
        } catch (SQLException e) {   
	//	System.out.println("Disconnection Failed!");
		return false;  
	}
}
 public boolean insertStock(int id, int wid, double qty) {
	
	try {
		String queryStatement = 
			"SELECT count(pid) as num FROM product WHERE pid ="
				+ Integer.toString(id);
		ps = connection.prepareStatement(queryStatement);
		rs = ps.executeQuery();
		rs.next();
		// Check if there actually is a pid in product;
		int amount = rs.getInt("num");
		if (amount==0) {
			return false;
		}
		
		queryStatement = 
			"SELECT count(*) as num FROM warehouse WHERE wid ="
				 + Integer.toString(wid);
		ps = connection.prepareStatement(queryStatement);
		rs = ps.executeQuery();
		rs.next();
		// Check if there is a wid in warehouse
		amount = rs.getInt("num");
		if (amount==0) {
			return false;
		}
	
		queryStatement = 
			"SELECT count(*) as num FROM stock WHERE pid =" 
			+ Integer.toString(id) + "AND wid ="
			 + Integer.toString(wid);
		ps = connection.prepareStatement(queryStatement);
		rs = ps.executeQuery();
		rs.next();
		// check that no item with key pid, wid exists in stock
		amount = rs.getInt("num");
		if (amount>0) {
			return false;
		}

		// Create the query and update the table
		String insertString = "INSERT INTO stock VALUES (" + Integer.toString(id) +
							  ", " + Integer.toString(wid) +
							  ", " + Double.toString(qty) + ")";
		ps = connection.prepareStatement(insertString);
		ps.executeUpdate();
		return true;

	}  catch (SQLException se) {
		//System.out.println("insertStock failed");
		//se.printStackTrace();
		return false;
		}			
  }

 public boolean updateStock(int id, int wid, double qty) {
    try {
	String queryStatement = 
	"SELECT pid, wid, quantity FROM stock WHERE pid = "
		+ Integer.toString(id) + "AND wid ="
		+ Integer.toString(wid);
	ps = connection.prepareStatement(queryStatement);
	rs = ps.executeQuery();
        rs.next();
        int amount = rs.getRow();
	// check if id, wid exist in stock table;
	//if quantity is negative, abs(qty) must be less than quantity of corresponding row 
        if (amount==0) {
		return false;
        }
        if (qty < 0) {
		if (qty*qty > rs.getInt("quantity")*rs.getInt("quantity")){
			 return false;
                }
        }
	// Create String and perform update
        String updateString = "UPDATE STOCK SET quantity = "
                             + Double.toString(qty)
                             + "WHERE pid = "
                             +  Integer.toString(rs.getInt("pid"))
                             + "AND wid = "
                             + Integer.toString(rs.getInt("wid"));
	ps = connection.prepareStatement(updateString);
	ps.executeUpdate();
        return true;

    }  catch (SQLException se) {
	System.out.println("Stock Update Failed");
        //se.printStackTrace();
        return false;
    }



}  
   
 public int insertOrder(int cid, int pid, int wid, double qty, double price){

	 try {
		// Check if pid, cid, wid exist in product, customer, warehouse respectively
		String queryStatement = "SELECT count(pid) as num FROM product WHERE pid ="	
			 + Integer.toString(pid);
		ps = connection.prepareStatement(queryStatement);
		rs = ps.executeQuery();
		rs.next();
		int amount = rs.getInt("num");
		if (amount==0) {
			return -1;
		}
		
		queryStatement = "SELECT count(*) as num FROM customer WHERE cid ="
			 + Integer.toString(cid);
		ps = connection.prepareStatement(queryStatement);
		rs = ps.executeQuery();
		rs.next();
		amount = rs.getInt("num");
		if(amount == 0){
			return -1;
		}
		
		queryStatement = "SELECT count(*) AS num FROM warehouse WHERE wid ="
						 + Integer.toString(wid);
		ps = connection.prepareStatement(queryStatement);
		rs = ps.executeQuery();
		rs.next();
		amount = rs.getInt("num");
		if(amount == 0){
			return -1;
		}

		// Get the latest order number
		String getSerial = "SELECT nextval(pg_get_serial_sequence('orders','oid')) as serialNum";
		ps = connection.prepareStatement(getSerial);
		rs = ps.executeQuery();
		rs.next();
		int serial = rs.getInt("serialNum");
		// Create the new query		
		Date currentDate = new Date();
		
		String insertString = "INSERT INTO orders VALUES (?, ?, ?, ?::Date, ?, ?, ?, ?)";
		ps = connection.prepareStatement(insertString);
		ps.setInt(1, serial);
		ps.setInt(2, cid);
		ps.setInt(3, pid);
		ps.setDate(4, new java.sql.Date(currentDate.getTime()));
		ps.setInt(5, wid);
		ps.setDouble(6, qty);
		ps.setDouble(7, price);
		ps.setString(8, "O");
		ps.executeUpdate();	
		return serial;
			
		  
	  }catch(SQLException se){
		//System.out.println("InsertOrder failed");
		//se.printStackTrace();
		return -1;
	  }
  }

  public boolean cancelOrder(int oid) {
   try{	
	String queryString = "SELECT oid, status FROM orders WHERE oid = ? AND status = 'O'";
	ps = connection.prepareStatement(queryString);
	ps.setInt(1, oid);
	rs = ps.executeQuery();
	rs.next();
	if (rs.getRow() == 0) {
	return false;
	}
	String deleteString = "DELETE FROM orders WHERE oid = ?";
	ps = connection.prepareStatement(deleteString);
	ps.setInt(1, oid);
	ps.executeUpdate();

    } catch(SQLException se ){
           //System.out.println("failed cancel");
           //se.printStackTrace();
	    return false;	  
	}
	return true;
  }

  

 
  public boolean shipOrder(int oid){
    try{
        
        String queryString = "SELECT * FROM orders WHERE oid = ? AND status = 'O'";
        ps = connection.prepareStatement(queryString);
        ps.setInt(1, oid);
        rs = ps.executeQuery();
        rs.next();
	// Get relevant info for stock adjustments
        double orderQuantity = rs.getDouble("quantity");
	int productid = rs.getInt("pid");
	int shipwid = rs.getInt("shipwid");
 	// Get warehouse quantity
	queryString = "SELECT * FROM stock WHERE pid = ? AND wid = ?";
        ps = connection.prepareStatement(queryString);
        ps.setDouble(1, productid);
	ps.setInt(2, shipwid);
        rs = ps.executeQuery();
        rs.next();
        double warehouseQuantity = rs.getDouble("quantity");
        // Carry out the order 
        if(orderQuantity <= warehouseQuantity){
            updateStock(productid, shipwid, warehouseQuantity - orderQuantity);
            String updateString = "UPDATE orders SET status = 'S' WHERE oid = ?";
            ps = connection.prepareStatement(updateString);
            ps.setInt(1, oid);
            ps.executeUpdate();
        } else {
            return false;
        }
        
        
    } catch(SQLException se){
       // System.out.println("shipOrder failed");
       // se.printStackTrace();
        return false;
    }   
	return true; 
  }
  
  public String listStock(int pid){
	try { 
	  // Get query with all the required data
          String queryString = "SELECT * FROM stock where pid = ?";
          ps = connection.prepareStatement(queryString);
          ps.setInt(1, pid);
          rs = ps.executeQuery();
          String list_stock = "";
	  // Loop through it, editing list_stock on each iteration	
          while (rs.next()) {
              double quantity = rs.getDouble("quantity");
              int wid = rs.getInt("wid");
              list_stock = list_stock + Integer.toString(wid) 
		+ ":" + String.format("%.2f", quantity) +  "#";
          }
	 // System.out.println(list_stock.substring(0, list_stock.length() - 1));
	  return list_stock.substring(0, list_stock.length() - 1 );	


        } catch(SQLException se){
           //System.out.println("listStock failed");
           //se.printStackTrace();
           return "";
        }      
  }
 
  public String listReferrals(int cid){
    try { 
        // Get query with all the required data
	String queryString = 
	"SELECT * FROM referral, customer where custid = ? AND  custref = cid";
        ps = connection.prepareStatement(queryString);
        ps.setInt(1, cid);
        rs = ps.executeQuery();
        String list_refer = ""; 
	// Loop through it, editing list_refer on each iteration
        while (rs.next()) {
            int custref = rs.getInt("custref");
            String cname = rs.getString("cname");
            list_refer = list_refer + Integer.toString(custref) + ":" + cname + "#";
	    
	   }
	//System.out.println(list_refer.substring(0, list_refer.length() -1));
	return list_refer.substring(0, list_refer.length() -1);

    } catch(SQLException se){
      //System.out.println("listStock failed");
      //se.printStackTrace();
      return "";
      }      
  
}    
  public boolean updateDB(){
     try {
	String queryString = 
    		"CREATE TABLE bestsellers AS SELECT pid, sales "
       		+ "FROM (SELECT pid, sum(quantity*price) as sales "
		+ "FROM orders WHERE status = 'S' GROUP BY  pid) AS yo" +
   		" WHERE sales > 10000";

    	Statement ps = connection.createStatement();
    	ps.executeUpdate(queryString);
    	return true;    
      } catch(SQLException se){
        //System.out.println("DB failed");
        //se.printStackTrace();
        return false;
        }    
}
}
