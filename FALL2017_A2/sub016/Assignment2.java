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
        try {
                Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e) {
                return;
        }
  }

  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password){
        try {
		connection = DriverManager.getConnection(URL, username, password);
                sql = connection.createStatement();
        }
        catch (SQLException e) {
                return false;
        }
        return (connection != null);
  }

  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
        try {
                connection.close();
                return true;
        }
        catch (SQLException e) {
                return false;
        }
  }

  public boolean insertStock(int id, int wid, double qty) {
        try {
		String select = "SELECT s.pid, s.wid, quantity  FROM a2.stock as s INNER JOIN a2.product as p ON s.pid = p.pid INNER JOIN a2.warehouse as w ON s.wid = w.wid  WHERE quantity >= 0;";
		rs = sql.executeQuery(select);
		if(!rs.next()){
	 		String insert = "INSERT INTO a2.stock VALUES (?, ?, ?, ?);";
         		ps = connection.prepareStatement(insert);
              		ps.setInt(1, id);
                	ps.setInt(2, wid);
                	ps.setDouble(3, qty);
                	int rowsEffected = ps.executeUpdate();
			ps.close();
			rs.close();
			if(rowsEffected == 1) {
				return true;
			}
			else{
				return false;
			}
        	}
		else{
			return false;
		}
	}
        catch (SQLException e){
                return false;
        }
  }
   public boolean updateStock(int id, int wid, double qty) {
        try {
		String updating = "UPDATE a2.stock SET quantity=quantity+? WHERE id=? AND wid=?;";
                ps = connection.prepareStatement(updating);
                ps.setDouble(1, qty);
                ps.setInt(2, id);
                ps.setInt(3, wid);
 		int rowsEffected = ps.executeUpdate();
        }
        catch (SQLException e) {
                return false;
        }
        return true;
  }

  public int insertOrder(int cid, int pid, int wid, double qty, double price){
        try {
		String inserting = "INSERT into a2.orders (cid, pid, wid, qty, price) VALUES (?, ?, ?, ?, ?);";
                ps = connection.prepareStatement(inserting);
                ps.setInt(1, cid);
                ps.setInt(2, pid);
                ps.setInt(3, wid);
                ps.setDouble(4, qty);
                ps.setDouble(5, price);
                int rowsEffected = ps.executeUpdate();
        }
        catch (SQLException e) {
                return -1;
        }
        return 0;
  }

  public boolean cancelOrder(int oid) {
       	try {
		String cancelling = "DELETE FROM a2.orders WHERE oid=?;";
                ps = connection.prepareStatement(cancelling);
                ps.setInt(1, oid);
		int rowsEffected = ps.executeUpdate();
                if(rowsEffected == 2) {
                        return true;
                }
        }
        catch (SQLException e) {}
        return false;
  }

  public boolean shipOrder(int oid){
        try{
		String shipping = "UPDATE orders SET status = 'S' WHERE oid=?;";
                ps = connection.prepareStatement(shipping);
                ps.setInt(1, oid);
                int rowsEffected = ps.executeUpdate();
        }
        catch (SQLException e) {
                return false;
        }
        return true;
  }

  public String listStock(int pid){
	try{
		String sqlQuery = "SELECT wid, ROUND(quantity, 2) FROM a2.stock, a2.product WHERE quantity > 0 AND pid=? ORDER BY quantity DESC;";
		ps = connection.prepareStatement(sqlQuery);	
		ps.setInt(1, pid);
		rs = ps.executeQuery();
		String result = "";
		if (rs != null){
			while(rs.next()){
				String wareid = Integer.toString(rs.getInt("wid"));
				String qty = Integer.toString(rs.getInt("quantity"));
			 	result += wareid + ":" + qty + "#";
			}
		}
		else {
			result = "";
		}
		return result;
	}
	catch (SQLException e){
		return "";
	} 
  }

  public String listReferrals(int cid){
	return "";
  }

  public boolean updateDB(){
        try {
		String updating = "CREATE TABLE a2.bestsellers (pid INTEGER, sales NUMERIC(10,2))";
		sql.executeUpdate(updating);
		String inserting = "INSERT INTO a2.bestsellers (SELECT pid, ROUND(price * quantity, 2) as sales FROM a2.orders WHERE sales > 10000 AND status = 'S');";
		sql.executeUpdate(inserting);
		                
        }
        catch (SQLException e) {
		return false;
	}
	return true;
  	    
  }

}

