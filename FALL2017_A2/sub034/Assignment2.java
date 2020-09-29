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
        return false;
      }
      if (connection == null) {
        return false;
      }
      return true;
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
      try {
        connection.close();
      } catch (SQLException e) {
        return false;
      }
      return true;    
  }
    
  public boolean insertStock(int id, int wid, double qty) {
	   // qty must be >= 0
	   if (qty < 0) {
		 return false;
	   }
	   try {
		   sql = connection.createStatement();
		   String sqlText;
		   
		   // Check if pid exists in the product table
		   sqlText = "SELECT pid FROM product";
		   rs = sql.executeQuery(sqlText);
		   boolean flag = false;
		   while (rs.next() && !flag) {
			   int pid = rs.getInt("pid");
			   if (pid == id) {
				   flag = true;
				}
			}
			if (!flag) {
				return false;
			}
			rs.close();
			
			// Check if wid exists in the warehouse table
			sqlText = "SELECT wid FROM warehouse";
			rs = sql.executeQuery(sqlText);
			flag = false;
			while (rs.next() && !flag) {
				int w = rs.getInt("wid");
				if (w == wid) {
					flag = true;
				}
			}
			if (!flag) {
				return false;
			}
			rs.close();
			
			// Check if pid already exists in stock
			sqlText = "SELECT pid, wid FROM stock";
			rs = sql.executeQuery(sqlText);
			flag = false;
			while (rs.next() && !flag) {
				int pid = rs.getInt("pid");
				int w = rs.getInt("wid");
				if (pid == id || w == wid) {
					flag = true;
				}
			}
			if (flag) {
				return false;
			}
			rs.close();
			
			// Now we can execute the insert
			sqlText = "INSERT INTO stock VALUES (?, ?, ?)";
			ps = connection.prepareStatement(sqlText);
			ps.setInt(1, id);
			ps.setInt(2, wid);
			ps.setDouble(3, qty);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
  
   public boolean updateStock(int id, int wid, double qty) {
	   double qtyabs;
	   boolean flag;
	   String sqlText;
	   if (qty < 0) {
		   qtyabs = -qty;
	   } else {
		   qtyabs = qty;
	   }
	   try {
		   sql = connection.createStatement();
		   // Check if pid, wid exists in table stock
		   sqlText = "SELECT pid, wid, quantity FROM stock";
		   rs = sql.executeQuery(sqlText);
		   flag = false;
		   while (rs.next() && !flag) {
			   int pid = rs.getInt("pid");
			   int w = rs.getInt("wid");
			   int quantity = rs.getInt("quantity");
			   if (pid == id && w == wid) {
				   if (qtyabs <= quantity) {
					   flag = true;
					}
				}
			}
			if (!flag) {
				return false;
			}
			rs.close();
			
			// Update the quantity with absolute value of qty
			sql = connection.createStatement();
			sqlText = "UPDATE stock SET quantity = ? WHERE pid = ? AND wid = ?";
			ps = connection.prepareStatement(sqlText);
			ps.setDouble(1, qtyabs);
			ps.setInt(2, id);
			ps.setInt(3, wid);
			ps.executeUpdate();
		} catch (SQLException e) {
			return false;
		}
		return true;
    }
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price) {
	  String sqlText;
	  boolean flag = false;
	  int w;
	  int oid = -1;
	  try {
		  sql = connection.createStatement();
		  // Check if cid exists in customer table
		  sqlText = "SELECT cid FROM customer";
		  rs = sql.executeQuery(sqlText);
		  while (rs.next() && !flag) {
			  w = rs.getInt("cid");
			  if (cid == w) {
				flag = true;
			  }
		  }
		  if (!flag) {
			return -1;
		  }
		  rs.close();
		  
		  // Check if wid exists in warehouse table
		  flag = false;
		  sqlText = "SELECT wid FROM warehouse";
		  rs = sql.executeQuery(sqlText);
		  while (rs.next() && !flag) {
			  w = rs.getInt("wid");
			  if (w == wid) {
				flag = true;
			  }
		  }
		  if (!flag) {
			return -1;
		  }
		  rs.close();
		  
		  // Check if pid exists in product table
		  flag = false;
		  sqlText = "SELECT pid FROM product";
		  rs = sql.executeQuery(sqlText);
		  while (rs.next() && !flag) {
			  w = rs.getInt("pid");
			  if (w == pid) {
				flag = true;
			  }
		  }
		  if (!flag) {
			return -1;
		  }
		  rs.close();
		  
		  // Insert the order into orders table
		  // Grab the next order id
		  sqlText = "SELECT oid FROM orders ORDER BY oid DESC LIMIT 1";
		  rs = sql.executeQuery(sqlText);
		  while (rs.next()) {
			  oid = rs.getInt("oid") + 1;
		  }
		  rs.close();
		  if (oid == -1) {
			return -1;
		  }
		  Date odate = new Date(System.currentTimeMillis());
		  
		  sqlText = "INSERT INTO orders VALUES (?, ?, ?, ?, ?, ?, ?, 'O')";
		  ps = connection.prepareStatement(sqlText);
		  ps.setInt(1, oid);
		  ps.setInt(2, cid);
		  ps.setInt(3, pid);
		  ps.setDate(4, odate);
		  ps.setInt(5, wid);
		  ps.setDouble(6, qty);
		  ps.setDouble(7, price);
		  ps.executeUpdate();
	  } catch (SQLException e) {
		  return -1;
	  }
	  return oid;
  }

  public boolean cancelOrder(int oid) {
	  String sqlText;
	  try {
		  sql = connection.createStatement();
		  sqlText = "DELETE FROM orders WHERE oid = ? AND status = 'O'";
		  ps = connection.prepareStatement(sqlText);
		  ps.setInt(1, oid);
		  int deletedAmount = ps.executeUpdate();
		  if (deletedAmount == 0) {
			return false;
		  }
	  } catch (SQLException e) {
		return false;
	  }
	  return true;
  }

  public boolean shipOrder(int oid) {
	  String sqlText;
	  int pid = 0;
	  int wid = 0;
	  double quantity = 0;
	  double qty = 0;
	  try {
		  sql = connection.createStatement();
		  sqlText = "SELECT * FROM orders WHERE oid = ? AND status = 'O'";
		  ps = connection.prepareStatement(sqlText);
		  ps.setInt(1, oid);
		  rs = ps.executeQuery();
		  if (!rs.isBeforeFirst()) {
			return false;
		  }
		  while (rs.next()) {
			pid = rs.getInt("pid");
			wid = rs.getInt("shipwid");
			quantity = rs.getDouble("quantity");
		  }
		  rs.close();
		  
		  sqlText = "SELECT * FROM stock WHERE wid = ? AND pid = ?";
		  ps = connection.prepareStatement(sqlText);
		  ps.setInt(1, wid);
		  ps.setInt(2, pid);
		  rs = ps.executeQuery();
		  while (rs.next()) {
			qty = rs.getDouble("quantity");
		  }
		  double total = qty - quantity;
		  if (!updateStock(pid, wid, qty - quantity)) {
			return false;
		  }
		  
		  sqlText = "UPDATE orders SET status = 'S' WHERE oid = ?";
		  ps = connection.prepareStatement(sqlText);
		  ps.setInt(1, oid);
		  ps.executeUpdate();
	  } catch (SQLException e) {
		  return false;
	  }
	  return true;        
   }
  
  public String listStock(int pid) {
	  String result = "";
	  String sqlText;
	  int wid;
	  double quantity;
	  try {
		  sql = connection.createStatement();
		  sqlText = "SELECT wid, quantity FROM stock WHERE pid = ?";
		  ps = connection.prepareStatement(sqlText);
		  ps.setInt(1, pid);
		  rs = ps.executeQuery();
		  while (rs.next()) {
			wid = rs.getInt("wid");
			quantity = rs.getDouble("quantity");
			result += wid + ":" + quantity + "#";
		  }
		  rs.close();
		  if (result != "") {
			result = result.substring(0, result.length() - 1);
		  }
	  } catch (SQLException e) {
		return "";
	  }
	  return result;
  }
  
  public String listReferrals(int cid) {
	  String result = "";
	  String sqlText;
	  String cname;
	  int custref;
	  try {
		  sql = connection.createStatement();
		  sqlText = "SELECT custref, cname FROM referral LEFT JOIN customer ON custref = cid WHERE custid = ? ORDER BY cname ASC";
		  ps = connection.prepareStatement(sqlText);
		  ps.setInt(1, cid);
		  rs = ps.executeQuery();
		  while (rs.next()) {
			custref = rs.getInt("custref");
			cname = rs.getString("cname");
			result += custref + ":" + cname + "#";
		  }
		  rs.close();
		  if (result != "") {
			  result = result.substring(0, result.length() - 1);
		  }
	  } catch (SQLException e) {
		return "";
	  }		  
	  return result;
  }
    
  public boolean updateDB() {
	  String sqlText;
	  int pid;
	  double sales;
	  try {
		  sql = connection.createStatement();
		  // Create the bestsellers table
		  sqlText = "CREATE TABLE bestsellers (pid INTEGER, sales NUMERIC(10, 2))";
		  sql.executeUpdate(sqlText);
		  
		  // Insert best sellers into the table
		  sqlText = "SELECT pid, salesamt FROM (SELECT pid, price * quantity AS salesamt FROM orders WHERE status = 'S')" + 
					" AS best WHERE salesamt > 10000";
		  rs = sql.executeQuery(sqlText);
		  while (rs.next()) {
			  pid = rs.getInt("pid");
			  sales = rs.getDouble("salesamt");
			  sqlText = "INSERT INTO bestsellers VALUES (?, ?)";
			  ps = connection.prepareStatement(sqlText);
			  ps.setInt(1, pid);
			  ps.setDouble(2, sales);
			  ps.executeUpdate();
		  }
		  rs.close();
	  } catch (SQLException e) {
		  return false;
	  }
	  return true;    
  }
  
}
