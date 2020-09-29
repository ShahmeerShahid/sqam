import java.sql.*;
import java.text.DecimalFormat;

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
		  Class.forName("org.postgresql.Driver");
	  } catch (ClassNotFoundException e) {
		  return false;
	  }
	  try{
		  connection = DriverManager.getConnection(URL, username, password); 
	  } catch (SQLException e) {
		  return false;
	  }
	  
		
	  
      return true;
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
	  
	if (rs != null) {
        try {
            rs.close();
        } catch (SQLException e) { 
			return false; //???
		}
    }
    if (ps != null) {
        try {
            ps.close();
        } catch (SQLException e) {
			return false; //???
		}
    }
    if (connection != null) {
        try {
            connection.close();
        } catch (SQLException e) { 
			return false;
		}
    }

    return true;    
  }
    
  public boolean insertStock(int id, int wid, double qty) {
	  
	
		try{
			
			String sqly = "SELECT pid FROM product WHERE pid = ?;" ;
			
			ps = connection.prepareStatement(sqly);
			ps.setInt(1,id);
			rs = ps.executeQuery();
			rs.next();
			int x = rs.getInt("pid");
			if (x!=id) {
				return false;
			}
			sqly ="SELECT wid FROM warehouse WHERE wid = ?;";
			
			ps = connection.prepareStatement(sqly);
			ps.setInt(1,wid);
			rs = ps.executeQuery();
			rs.next();
			int y = rs.getInt("wid");
			if (y!=wid) {
				return false;
			}
			
			sqly = "SELECT pid, wid, quantity FROM stock WHERE pid = ?  AND wid = ? AND quantity >= 0;";
			ps = connection.prepareStatement(sqly);
			ps.setInt(1,id);
			ps.setInt(2,wid);
			rs = ps.executeQuery();
			rs.next();
			x = rs.getInt("pid");
			y = rs.getInt("wid");
			double z = rs.getInt("quantity");
			if (x==id && y==wid && z >= 0) {
				return false;
			}
			
			sqly = "INSERT INTO stock VALUES( ? , ? , ?);";
			
			ps = connection.prepareStatement(sqly);
			ps.setInt(1,id);
			ps.setInt(2,wid);
			ps.setDouble(3,qty);
			rs = ps.executeUpdate();

			
		} catch (SQLException e) {
			return false;
		} finally {
			return true;
		}
		
	

   
  }
  
   public boolean updateStock(int id, int wid, double qty) {
	   
	try {
		String sqlus = "SELECT quantity FROM stock WHERE pid = ? AND wid = ?;";
		ps = connection.prepareStatement(sqlus);
		ps.setInt(1,id);
		ps.setInt(2,wid);
		rs = ps.executeQuery();
		double q = 0;
		if (rs.next()==true) {
			q = rs.getDouble("quantity");
		} else {
			return false;
		}
		if (Math.abs(qty) > q) {
			return false;
		}
		
		sqlus = "UPDATE stock SET quantity = ? WHERE pid = ? AND wid = ?;";
		ps = connection.prepareStatement(sqlus);
		ps.setDouble(1,q+qty);
		ps.setInt(2,id);
		ps.setInt(3,wid);
		ps.executeUpdate();
		return true;
	} catch (SQLException e){
		return false;
	}
	
  }
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
   return -1;
  }

  public boolean cancelOrder(int oid) {
	
	try {
		String sqlc = "SELECT * FROM orders WHERE oid = ? ;";
		
		ps = connection.prepareStatement(sqlc);
		ps.setInt(1,oid);
		rs = ps.executeQuery();
		rs.next();
		String c = rs.getString("status");
		if (c.equals("S")) {
			return false;
		} else {
			sqlc="DELETE FROM orders WHERE oid = ?;";
			ps = connection.prepareStatement(sqlc);
			ps.setInt(1,oid);
			rs = ps.executeQuery();
			return true;
		}
	} catch (SQLException e){
		return false;
	}
	
  }

  public boolean shipOrder(int odi){
	
	try {
		String sqlso = "SELECT pid, shipwid, quantity FROM orders WHERE oid = ? ;";
		ps = connection.prepareStatement(sqlso);
		ps.setInt(1,odi);
		rs = ps.executeQuery();
		rs.next();
		int w = rs.getInt("shipwid");
		int p = rs.getInt("pid");
		int q = rs.getInt("quantity");
		
		sqlso = "SELECT * FROM stock WHERE pid = ? AND wid = ? ;";
		ps = connection.prepareStatement(sqlso);
		ps.setInt(1,p);
		ps.setInt(2,w);
		rs = ps.executeQuery();
		rs.next();
		int q2 = rs.getInt("quantity");
		if (q2 >= q) {
			if (updateStock(p,w,-q)==false){
				return false;
			}
			sqlso = "UPDATE orders SET status TO 'S' WHERE oid = ? ;";
			ps = connection.prepareStatement(sqlso);
			ps.setInt(1,odi);
			ps.executeUpdate();
			return true;
		} else {
			return false;
		}
		
		
		
	} catch (SQLException e){
		return false;
	}
	    
  }
  
  public String listStock(int pid){
	
	String str = "";
	try {
		String sqlls = "SELECT w.wid AS wid, s.quantity AS qty FROM warehouse w JOIN stock s ON s.pid = ? WHERE qty > 0 ORDER BY qty DESC;";
		ps = connection.prepareStatement(sqlls);
		ps.setInt(1,pid);
		rs = ps.executeQuery();
		DecimalFormat df = new DecimalFormat("#.00");
		while(rs.next()==true){
			str += Integer.toString(rs.getInt("wid"));
			str += ":";
			str += df.format(rs.getDouble("qty"));
			str += "#";
		}
		if(str.length() > 0 && str != null){
			str.substring(0,str.length()-1);
		}
	} catch (SQLException e) {
		return "";
	} finally {
		return str;
	}
	
  }
  
  public String listReferrals(int cid){
	
	String str = "";
	try {
		String sqllr = "SELECT cid, cname FROM customer ORDER BY cnane ASC;";
		ps = connection.prepareStatement(sqllr); // switch for regular statement
		rs = ps.executeQuery();
		
		while(rs.next() == true){
			str += Integer.toString(rs.getInt("cid"));
			str += ":";
			str += rs.getString("cname");
			str += "#";
		}
		if(str.length() > 0 && str != null){
			str.substring(0,str.length()-1);
		}
	} catch (SQLException e) {
		return "";
	} finally {
		return str;
	}
	
  }
    
  public boolean updateDB(){
	
	try {
		String sqludb = "DROP TABLE IF EXISTS bestsellers;";
		ps = connection.prepareStatement(sqludb);
		ps.executeUpdate();
		sqludb = "CREATE TABLE bestsellers (pid int , sales NUMERIC(10,2));";
		ps = connection.prepareStatement(sqludb);
		ps.executeUpdate();
		String ubd = "SELECT p.pid as pid, SUM(price*quantity) as sales FROM products p JOIN orders o ON p.pid = o.pid WHERE status = 'S' GROUP BY p.pid ;";
		ps = connection.prepareStatement(ubd);
		rs = ps.executeQuery();
		String str = "";
		while(rs.next()==true){
			if (rs.getDouble("sales") > 10000.00) {
				str = "INSERT INTO bestsellers VALUES(? , ?);";
				ps.setInt(1,rs.getInt("pid"));
				ps.setDouble(1,rs.getDouble("sales"));
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
