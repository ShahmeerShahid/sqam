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
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			return false;
		}catch(Exception ex){
			return false;
			
		}
		try {
			connection = DriverManager.getConnection(URL, username, password);
			
			String stmt = "SET search_path TO a2";
			sql = connection.createStatement();
			sql.executeUpdate(stmt);
		} catch (SQLException e) {
			return false;
		} catch(Exception e){
			return false;
		}
		return true;
	}
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
	try {
		if(rs!=null){rs.close();};
		if(ps!=null){ps.close();};
		if(connection!=null){connection.close();};
	} catch (SQLException e) {
		return false;
	}
    return true;    
  }
    
  public boolean insertStock(int pid, int wid, double qty) {
       try {
        if (qty >= 0) { 
            String[] querytests = new String[2];
            querytests[0] = String.format("SELECT * FROM product WHERE pid = %d;", pid);
            querytests[1] = String.format("SELECT * FROM warehouse WHERE wid = %d;", wid);
            
            for (int i = 0; i < 2; i++) { //check if pid and wid exist
                ps = connection.prepareStatement(querytests[i]);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    return false;
                }
            }
            
            String insert = String.format("INSERT INTO stock VALUES(%d, %d, %f);", pid, wid, qty);
            sql = connection.createStatement();
            sql.executeUpdate(insert);
            return true;
        }
        return false;
		} catch (SQLException e) {
			return false;
		}
  }
  
	public boolean updateStock(int id, int wid, double qty) {
		try{
			int stockQty;
			//initialize var
			String sid = Integer.toString(id);
			String swid = Integer.toString(wid);
			String query;
			String stmt;
			
			//get the original quantity
			query = "SELECT * FROM stock WHERE pid = " + sid + " AND wid = " + wid;
			ps = connection.prepareStatement(query);
			rs = ps.executeQuery();
			if(rs.next()){
				stockQty = rs.getInt("quantity");
				
				//validity check
				if(qty < 0){
					double absQty = -qty;
					
					if(absQty > stockQty){
						return false;
					}
				}
				
				//update
				qty += stockQty;
				sql = connection.createStatement();
				stmt = "UPDATE stock SET quantity = " + Double.toString(qty) + " WHERE pid = " + sid + " AND wid = " + wid;
				sql.executeUpdate(stmt);
			}else{
				return false;
			}
			
		}catch(SQLException sqlException){
			return false;
		}catch(Exception regException){
			return false;
		}
		return true;
	}
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
    try {
        String[] queryTests = new String[3];
        queryTests[0] = String.format("SELECT * FROM customer WHERE cid = %d;", cid);
        queryTests[1] = String.format("SELECT * FROM product WHERE pid = %d;", pid);
        queryTests[2] = String.format("SELECT * FROM warehouse WHERE wid = %d;", wid);
        
        for (int i = 0; i < 3; i++) { // check cid, pid and wid exists
            ps = connection.prepareStatement(queryTests[i]);
            rs = ps.executeQuery();
            if (!rs.next()) {
                return -1;
            }
        }
        
        String sqlNextVal = "SELECT NEXTVAL(pg_get_serial_sequence('orders', 'oid'));";
        ps = connection.prepareStatement(sqlNextVal);
        rs = ps.executeQuery();
        
        int nextoid = -1;
        
        if (rs.next()) {
            nextoid = rs.getInt("nextval"); //get integer right after current largest oid
        } else {
            return -1;
        }
        
        String insert = String.format("INSERT INTO orders " + 
                "VALUES(%d, %d, %d, (SELECT CURRENT_DATE), %d, %f, %f, 'O');",
                nextoid, cid, pid, wid, qty, price);
        
        sql = connection.createStatement();
        sql.executeUpdate(insert);
        
        return nextoid;
    } catch (SQLException e) {
        return -1;
    }
  }

	public boolean cancelOrder(int oid) {
		try{
			String ordered;
			String stmt;
			
			String query = "SELECT * FROM orders WHERE oid = ?";
			ps = connection.prepareStatement(query);
			ps.setInt(1, oid);
			rs = ps.executeQuery();
			if(rs.next()){
				ordered = rs.getString("status");
				
				if(ordered.equals("S")){
					return false;
				}
				
				sql = connection.createStatement();
				stmt = "DELETE FROM orders WHERE oid = " + Integer.toString(oid);
				sql.executeUpdate(stmt);
			}else{
				return false;
			}
			
		}catch(SQLException sqlEx){
			return false;
		}
		
		return true;
	}

  public boolean shipOrder(int oid){
	 try {
	  String order = String.format("SELECT * FROM orders WHERE oid = %d AND status = 'O';", oid);
	  String update = String.format("UPDATE orders SET status = 'S' WHERE oid = %d;", oid);
	  
	  ps = connection.prepareStatement(order);
	  rs = ps.executeQuery();
	  if (!rs.next()) { // if none that are matching oid or status is 'S'
		  return false;
	  }
	  
	  if (updateStock(rs.getInt("pid"), rs.getInt("shipwid"), -(rs.getDouble("quantity")))) {
		  sql = connection.createStatement();
		  sql.executeUpdate(update);
		  return true;
	  }
      } catch (SQLException e) {
          return false;
      }
      return false;    
  }
  
	public String listStock(int pid){
		String table = "";
		try{
			//declare
			String query;
			int wid;
			Double qty;
			
			//initialize stuff
			query = "SELECT * FROM stock WHERE pid = ? AND quantity > 0 ORDER BY quantity DESC";
			ps = connection.prepareStatement(query);
			ps.setInt(1, pid);
			rs = ps.executeQuery();
			//create table
			while(rs.next()){
				wid = rs.getInt("wid");
				qty = rs.getDouble("quantity");
				table = table+Integer.toString(wid)+":"+Double.toString(qty)+"#";
			}
			if(table.equals("")){
				return "";
			}
			table = table.substring(0, table.length()-1);
			
		}catch(SQLException sqlEx){
			return "";
		}
		return table;
	}
  
  public String listReferrals(int cid){
      try {
          String view = String.format("CREATE VIEW v1 AS SELECT custref AS cid FROM referral WHERE custid = %d;", cid);
          String query = "SELECT cid, cname FROM customer NATURAL JOIN v1 ORDER BY cname ASC;";
          
          sql = connection.createStatement();
          sql.executeUpdate(view);
          
          ps = connection.prepareStatement(query);
          rs = ps.executeQuery();
          
          String output = "";
          while (rs.next()) {
              int refid = rs.getInt("cid");
              String refname = rs.getString("cname");
              output += String.format("%d:%s#", refid, refname);
          }
          
          sql = connection.createStatement();
          sql.executeUpdate("DROP VIEW IF EXISTS v1 CASCADE;");
          
          if (output.length() == 0) {
              return output;
          } else {
              return output.substring(0, output.length() - 1); //remove #
          }
      } catch (SQLException e) {
          return "";
      }
  }
    
	public boolean updateDB(){
		try{
			//declare variables
			String tableCreate;
			String tableReset;
			String tableInsert;
			String query;
			
			//string initialize
			tableReset  = "DROP TABLE IF EXISTS bestsellers";
			tableCreate = "CREATE TABLE bestsellers (pid INTEGER PRIMARY KEY, sales NUMERIC(10,2))";
			tableInsert = "INSERT INTO bestsellers (SELECT pid, SUM(quantity*price) AS sales FROM orders WHERE status = 'S' GROUP BY pid HAVING SUM(quantity*price) > 10000)";
			
			//execute statements
			sql = connection.createStatement();
			sql.executeUpdate(tableReset);
			sql = connection.createStatement();
			sql.executeUpdate(tableCreate);
			sql = connection.createStatement();
			sql.executeUpdate(tableInsert);
		}catch(SQLException sqlEx){
			return false;
		}
	  
		return true;    
	}
	
}
