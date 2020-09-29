import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

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
		connection = DriverManager.getConnection(URL, username, password);
		return true;
	}
	    catch (Exception e) {
		return false;
	    }
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
	try{
		connection.close();
		return true;
	}
	catch (Exception e){
		return false;  
	}  
  }
    
  public boolean insertStock(int id, int wid, double qty) {
	try {
		if (qty < 0){
			return false;
		}
		sql = connection.createStatement();
		sql.executeUpdate("SET search_path TO a2;");
		String row = "INSERT INTO stock VALUES ("
		+ Integer.toString(id) + ", " + Integer.toString(wid)
		+ ", " + Double.toString(qty) + ");";
		sql.executeUpdate(row);
		sql.close();
		return true;
	}
	catch (Exception e) {
		//System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		return false;
	}
  }
  
   public boolean updateStock(int id, int wid, double qty) {
	try {
		sql = connection.createStatement();
		sql.executeUpdate("SET search_path TO a2;");
		String query = "SELECT quantity FROM stock WHERE pid = " + Integer.toString(id) + " and wid = " 
		+ Integer.toString(wid) + ";";
		rs = sql.executeQuery(query);
		float curqty = 0;
		while (rs.next()) {
			curqty = rs.getFloat("quantity");
			if (qty < 0 && Math.abs(qty) > curqty){
				sql.close();
				rs.close();
				return false;
			}
		}
		String row = "UPDATE stock SET quantity = "+ Double.toString(curqty + qty) + " where pid = "
		+ Integer.toString(id) + " and wid = " + Integer.toString(wid) + ";";
		if (sql.executeUpdate(row) != 1){
			sql.close();
			rs.close();
			return false;
		}
		sql.close();
		rs.close();
		return true;
	}
	catch (Exception e) {
		//System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		return false;
	}
  }
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
   	try {
		DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
		Date date = new Date();
		sql = connection.createStatement();
		sql.executeUpdate("SET search_path TO a2;");
		String row = "INSERT INTO orders VALUES (DEFAULT, "
		+ Integer.toString(cid) + ", " + Integer.toString(pid)
		+ ", '" + dateFormat.format(date) + "', " + Integer.toString(wid) + ", " 
		+ Double.toString(qty) + ", " + Double.toString(price) + ", 'O');";
		sql.executeUpdate(row);
		String query = "SELECT CURRVAL(pg_get_serial_sequence('orders', 'oid'));";
		rs = sql.executeQuery(query);
		while (rs.next()) {
			int oid = rs.getInt("currval");
			sql.close();
			rs.close();
			return oid;
		}
		return -1;
	}
	catch (Exception e) {
		//System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		return -1;
	}
  }

  public boolean cancelOrder(int oid) {
   	try {
		sql = connection.createStatement();
		sql.executeUpdate("SET search_path TO a2;");
		String query = "SELECT status FROM orders WHERE oid = " + Integer.toString(oid) + ";";
		rs = sql.executeQuery(query);
		while (rs.next()) {
			if (rs.getString("status").startsWith("S")){
				sql.close();
				rs.close();
				return false;
			}
		}
		String row = "DELETE FROM orders where oid = " + Integer.toString(oid) + ";";
		if (sql.executeUpdate(row) != 1){
			sql.close();
			rs.close();
			return false;
		}
		sql.close();
		rs.close();
		return true;
	}
	catch (Exception e) {
		//System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		return false;
	}
  }

  public boolean shipOrder(int odi){
   	try {
		connection.setAutoCommit(false);
		sql = connection.createStatement();
		sql.executeUpdate("SET search_path TO a2;");
		String query = "SELECT * FROM orders WHERE oid = " + Integer.toString(odi) + ";";
		rs = sql.executeQuery(query);
		int prod = 0;
		int ware = 0;
		float order = 0;
		String stat = "";
		while (rs.next()) {
			prod = rs.getInt("pid");
			ware = rs.getInt("shipwid");
			order = rs.getFloat("quantity");
			stat = rs.getString("status");
			if (stat.startsWith("S")){
				sql.close();
				rs.close();
				return false;
			}
		}
		String row = "UPDATE orders SET status = 'S' where oid = " + Integer.toString(odi) + ";";
		if (sql.executeUpdate(row) != 1){
			sql.close();
			rs.close();
			return false;
		}
		boolean val = updateStock(prod, ware, -order);
		sql.close();
		rs.close();
		if (val){
			connection.commit();
			return true;
		}
		connection.rollback();
		return false;
	}
	catch (Exception e) {
		//System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		try {
                	connection.rollback();
			return false;
            	} 
		catch(SQLException except) {
                	return false;
            	}
	}      
  }
  
  public String listStock(int pid){
	try {
		sql = connection.createStatement();
		sql.executeUpdate("SET search_path TO a2;");
		String query = "SELECT * FROM stock WHERE pid = " + Integer.toString(pid) + " ORDER BY quantity DESC;";
		rs = sql.executeQuery(query);
		String stockTable = "";
		boolean firstrow = true;
		while (rs.next()) {
			if (rs.getFloat("quantity") > 0){
				if (firstrow){
					stockTable = stockTable + Integer.toString(rs.getInt("wid")) + ":"
					+ String.format("%.02f", rs.getFloat("quantity"));
					firstrow = false;
				}
				else{
					stockTable = stockTable + "#" + Integer.toString(rs.getInt("wid")) + ":"
					+ String.format("%.02f", rs.getFloat("quantity"));
				}
			}
		}
		sql.close();
		rs.close();
		return stockTable;
	}
	catch (Exception e) {
		//System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		return "";
	}
  }
  
  public String listReferrals(int cid){
	try {
		sql = connection.createStatement();
		sql.executeUpdate("SET search_path TO a2;");
		String query = "SELECT r.custid, r.custref, c.cname FROM referral AS r, customer AS c " + 
		"WHERE r.custref = c.cid and r.custid = " + Integer.toString(cid) + " ORDER BY cname ASC;";
		rs = sql.executeQuery(query);
		String refTable = "";
		boolean firstrow = true;
		while (rs.next()) {
			if (firstrow){
				refTable = refTable + Integer.toString(rs.getInt("custref")) + ":"
				+ rs.getString("cname");
				firstrow = false;
			}
			else{
				refTable = refTable + "#" + Integer.toString(rs.getInt("custref")) + ":"
				+ rs.getString("cname");
			}
		}
		sql.close();
		rs.close();
		return refTable;
	}
	catch (Exception e) {
		//System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		return "";
	}
  }
    
  public boolean updateDB(){
	try {
		sql = connection.createStatement();
		sql.executeUpdate("SET search_path TO a2;");
		String table = "DROP TABLE IF EXISTS bestsellers CASCADE;";
		sql.executeUpdate(table);
		table = "CREATE TABLE bestsellers (pid INTEGER, sales NUMERIC(10, 2));";
		sql.executeUpdate(table);
		String query = "SELECT pid, SUM(quantity*price) AS sales FROM orders WHERE status = 'S' " + 
		"GROUP BY pid HAVING SUM(quantity*price) > 10000";
		String row = "INSERT INTO bestsellers (" + query + ");";
		sql.executeUpdate(row);
		sql.close();
		return true;
	}
	catch (Exception e) {
		//System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		return false;
	}   
  }
  
}
