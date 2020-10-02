import java.sql.*;
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
	//Hi
	}

	//Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
	public boolean connectDB(String URL, String username, String password){
		try
		{
			Class.forName("org.postgresql.Driver");
		}
		catch(ClassNotFoundException classnotfoundexception)
		{
			return false;
		}
		try
		{
			//Get Driver
			connection = DriverManager.getConnection(URL, username, password);
			//Set Database
			sql = connection.createStatement();
			sql.executeUpdate("SET search_path TO a2");
		}
		catch(SQLException sqlexception)
		{
			return false;
		}
		return connection != null;
	}

	//Closes the connection. Returns true if closure was sucessful
	public boolean disconnectDB()
	{
		if(rs != null)
			try
			{
				rs.close();
			}
			catch(SQLException sqlexception)
			{
				return false;
			}
		if(ps != null)
			try
			{
				ps.close();
			}
		catch(SQLException sqlexception1)
		{
			return false;
		}
		if(connection != null)
			try
			{
				connection.close();
			}
			catch(SQLException sqlexception2)
			{
				return false;
			}
		return true;
	}

	public boolean insertStock(int id, int wid, double qty) {
		try{
			//Valid quantity
			if (qty < 0){
				return false;
			}
			ps = connection.prepareStatement("INSERT INTO stock VALUES(? , ? ,?);");
			ps.setInt(1,id);
			ps.setInt(2,wid);
			ps.setDouble(3,qty);
			ps.executeUpdate();
		}
		catch (SQLException sqle) 
		{
			return false;
		}
		return true;
	}

	public boolean updateStock(int id, int wid, double qty) {
		try{
			//Get quantity of id warehouse
			String mySqlText = ("SELECT * FROM stock WHERE pid = ? AND wid = ?");
			ps = connection.prepareStatement(mySqlText);
			ps.setInt(1, id);
			ps.setInt(2, wid);
			rs = ps.executeQuery();
			if (rs != null){
				rs.next();
				double quantity = rs.getDouble(3) + qty;
				//Check if qty is valid to update with
				if (quantity < 0) 
					return false;
				//Actually update if valid
				mySqlText = "UPDATE stock SET quantity = ? WHERE pid = ? AND wid = ?";
				ps = connection.prepareStatement(mySqlText);
				ps.setDouble(1, quantity);
				ps.setInt(2, id);
				ps.setInt(3, wid);
				ps.executeUpdate();
				return true;
			}
		}
		catch(SQLException e){
			return false;
		}
		return false;
	}

	public int insertOrder(int cid, int pid, int wid, double qty, double price){
		int oid = -1;
		try{
			//Get next oid from SERIAL using NEXTVAL, as a result it will automatically increase
			rs = sql.executeQuery("SELECT NEXTVAL('orders_oid_seq')");
			if (rs == null)
				return -1;
			rs.next();
			oid = rs.getInt(1);
			//Get Date
			rs = sql.executeQuery("SELECT CURRENT_DATE");
			if (rs == null)
				return -1;
			rs.next();
			ps = connection.prepareStatement("INSERT INTO orders VALUES(?,?,?,?,?,?,?,?);");
			ps.setInt(1,oid);
			ps.setInt(2,cid);
			ps.setInt(3,pid);
			ps.setDate(4,rs.getDate(1));
			ps.setInt(5,wid);
			ps.setDouble(6,qty);
			ps.setDouble(7,price);
			ps.setString(8,"O");
			ps.executeUpdate();
			
		}
		catch (SQLException sqle) {
			return -1;
		}
		return oid;
	}


	public boolean cancelOrder(int oid) {
		try{
			//Check if the order is 'O' or 'S' and continue as necessary
			String mySqlText = "SELECT oid, status FROM orders WHERE oid = ?";
			ps = connection.prepareStatement(mySqlText);
			ps.setInt(1, oid);
			//ps.setString(2, "O");
			rs = ps.executeQuery();
			if (rs != null){
				rs.next();
				if ((rs.getString(2)).equals("S"))
					return false;
				//Remove from table if status is 'O'
				mySqlText = ("DELETE FROM orders WHERE oid = ?");
				ps = connection.prepareStatement(mySqlText);
				ps.setInt(1, oid);
				ps.executeUpdate();
				return true;
			}
			return false;
		}
		catch(SQLException e){
			return false;
		}
	}

	public boolean shipOrder(int odi){
		try{ 
			//Get appropriate information from orders and evaluate if the order is 'O' or 'S'
			ps = connection.prepareStatement("SELECT pid, shipwid, status, quantity FROM orders WHERE oid = ? AND status = 'O'");
			ps.setInt(1, odi);
			rs = ps.executeQuery();
			if (rs == null)
				return false;
			rs.next();
			int pid = rs.getInt(1);
			int shipwid = rs.getInt(2);
			double amount = rs.getDouble(4);
			// Get the quantity in stock to see if the amount to be shipped is able to be shipped
			ps = connection.prepareStatement("SELECT quantity FROM stock WHERE pid = ? AND wid = ?");
			ps.setInt(1, pid);
			ps.setInt(2, shipwid);
			rs = ps.executeQuery();
			if (rs == null)
				return false;
			rs.next();
			double quantity = rs.getDouble(1);
			// check if valid quantity
			if (quantity < amount)
				return false;
			ps = connection.prepareStatement("UPDATE orders SET status = 'S' WHERE oid =?");
			ps.setInt(1,odi);
			//Actually change the order to 'S'
			ps.executeUpdate();
			//Return whether updateStock updated successfully since this is the last step and whether or not it succeeds determines the return value
			return updateStock(pid,shipwid,(-1*amount));
		}
		catch (SQLException sqle) {
			return false;
		}
	}	

	public String listStock(int pid){
		String retVal = "";
		try{
			//Get the appropriate information from stock
			String mySqlText = "SELECT wid, quantity FROM stock WHERE pid = ? ORDER BY quantity DESC";
			ps = connection.prepareStatement(mySqlText);
			ps.setInt(1, pid);
			rs = ps.executeQuery();
			if (rs != null){
				while (rs.next()){
					// if this is the first line don't append an '#' to the end
					if (retVal.equals(""))
						retVal += "" + rs.getInt(1) + ":" + rs.getInt(2);
					else
						retVal += "#" + rs.getInt(1) + ":" + rs.getInt(2);
				}
			}
		}
		catch(SQLException e){
			return "";
		}
		return retVal;
	}

	public String listReferrals(int cid){
		String list = "";
		try{
			//Get the referrals and names
			ps = connection.prepareStatement("SELECT custref, cname FROM referral JOIN customer ON (custref = cid) WHERE custid = ? ORDER BY cname;");
			ps.setInt(1, cid);
			rs = ps.executeQuery();

			if (rs != null)
				while(rs.next()){
					// if this is the first line don't append an '#' to the end
					if (list.equals("")){
						list += rs.getString("custref")+":"+rs.getString("cname");
					}
					else{
						list += "#" + rs.getString("custref")+":"+rs.getString("cname");
					}
				}
		}
		catch (SQLException sqle) {
			return "";
		}
		return list;
	}

	public boolean updateDB(){
		try{
			//Drop the table first so no duplicate rows
			sql.executeUpdate("DROP TABLE IF EXISTS bestsellers");
			//Create the table
			String mySqlText = "CREATE TABLE bestsellers (pid INTEGER, sales NUMERIC(10, 2))";
			sql.executeUpdate(mySqlText);
			//Insert into table values
			mySqlText = "INSERT INTO bestsellers SELECT pid, sales FROM (SELECT pid, SUM(quantity * price) AS sales FROM orders WHERE status = 'S' GROUP BY pid) AS step1 WHERE sales > 10000";
			sql.executeUpdate(mySqlText);
			return true;
		}
		catch(SQLException e){
			return false;
		}
	}
	
	
	
}
