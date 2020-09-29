

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;


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
		}
	}

	//Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
	public boolean connectDB(String URL, String username, String password){

		try {
			connection = DriverManager.getConnection(URL, username, password); 
			if (connection != null) {			
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			//e.printStackTrace();
			return false;
		}

	}

	//Closes the connection. Returns true if closure was successful
	public boolean disconnectDB(){

		try {
			connection.close();
			return true;
		}
		catch (SQLException e) {
			//e.printStackTrace();
			return false;
		}   
	}

	public boolean insertStock(int id, int wid, double qty) {
		try{
			
			// pid exist in product table
			String pidCheck = "SELECT pid FROM a2.product WHERE pid = ?";
			ps = connection.prepareStatement(pidCheck);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next() == false){
				ps.close();
				rs.close();
				return false;
			}

			


			// wid doesnt exists in warehouse
			String widCheck = "SELECT wid FROM a2.warehouse WHERE wid = ?";
			ps = connection.prepareStatement(widCheck);
			ps.setInt(1, wid);
			rs = ps.executeQuery();
			if (rs.next() == false){
				ps.close();
				rs.close();
				return false;
			}
		

			// no pid and wid in stock
			String pidwidCheck = "SELECT pid, wid FROM a2.stock WHERE pid = ? AND wid = ?";
			ps = connection.prepareStatement(pidwidCheck);
			ps.setInt(1, id);
			ps.setInt(2, wid);
			rs = ps.executeQuery();
			if (rs.next() == true){
				ps.close();
				rs.close();
				return false;
			}
			


			// qty >= 0
			if (qty <= 0){
				ps.close();
				rs.close();
				return false;
			}

			String sqlText = "INSERT INTO a2.stock VALUES (?, ?, ?)";
			ps = connection.prepareStatement(sqlText);
			ps.setInt(1, id);
			ps.setInt(2, wid);
			ps.setDouble(3, qty);
			ps.executeUpdate();
			ps.close();
			rs.close();
			return true;

		}
		catch (SQLException e) {
			//e.printStackTrace();
			return false;
		}
	}

	public boolean updateStock(int id, int wid, double qty) {
		
		try{
			
			// pid, wid must exist in stock
			String pidwidCheck = "SELECT pid FROM a2.stock WHERE pid = ? AND wid = ?";
			ps = connection.prepareStatement(pidwidCheck);
			ps.setInt(1, id);
			ps.setInt(2, wid);
			rs = ps.executeQuery();
			if (rs.next() == false){
				ps.close();
				rs.close();
				return false;
			}
			
			// qty abs value must be less than or equal to cur qty
			String qtyCheck = "SELECT quantity FROM a2.stock WHERE pid = ? AND wid = ?";
			ps = connection.prepareStatement(qtyCheck);
			ps.setInt(1, id);
			ps.setInt(2, wid);
			rs = ps.executeQuery();
			Double curqty;
			if (rs.next()) {
				curqty = rs.getDouble(1);
				if (curqty < Math.abs(qty)){
					ps.close();
					rs.close();
					return false;
				}
			} else{
				ps.close();
				rs.close();
				return false;
			}
			
			// update quantity
			String updateText = "UPDATE a2.stock SET quantity = ? WHERE pid = ? AND wid = ?";
			ps = connection.prepareStatement(updateText);
			ps.setDouble(1, (curqty + qty));
			ps.setInt(2, id);
			ps.setInt(3, wid);
			ps.executeUpdate();
			ps.close();
			rs.close();
			return true;

		}
		catch (SQLException e){
			//e.printStackTrace();
			return false;
		}

	}

	public int insertOrder(int cid, int pid, int wid, double qty, double price){
		try {

			// cid must exist in customer
			String customerCheck = "SELECT cid FROM a2.customer WHERE cid = ?";
			ps = connection.prepareStatement(customerCheck);
			ps.setInt(1, cid);
			rs = ps.executeQuery();
			if (rs.next() == false){
				ps.close();
				rs.close();
				return -1;
			}

			// pid must exist in product
			String productCheck = "SELECT pid FROM a2.product WHERE pid = ?";
			ps = connection.prepareStatement(productCheck);
			ps.setInt(1, pid);
			rs = ps.executeQuery();
			if (rs.next() == false){
				ps.close();
				rs.close();
				return -1;
			}

			// wid must exist in warehouse
			String warehouseCheck = "SELECT wid FROM a2.warehouse WHERE wid = ?";
			ps = connection.prepareStatement(warehouseCheck);
			ps.setInt(1, wid);
			rs = ps.executeQuery();
			if (rs.next() == false){
				ps.close();
				rs.close();
				return -1;
			}
			
			// qty and price must be positive
			if (qty <= 0){
				return -1;
			}
			
			if (price <= 0){
				return -1;
			}

			// insert into order
			String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

			String insertOrder = "INSERT INTO a2.orders (oid, cid, pid, odate, shipwid, quantity, price, status) VALUES "
					+ "(?, ?, ?, ?::date, ?, ?, ?, 'O')";
			String getNextOID = "SELECT nextval('a2.orders_oid_seq')";
			ps = connection.prepareStatement(getNextOID);
			rs = ps.executeQuery();
			int nextoid;
			if (rs.next()){
				nextoid = rs.getInt(1);
			} else{
				ps.close();
				rs.close();
				return -1;
			}

			ps = connection.prepareStatement(insertOrder);
			ps.setInt(1, nextoid);
			ps.setInt(2, cid);
			ps.setInt(3, pid);
			ps.setString(4, currentDate);
			ps.setInt(5, wid);
			ps.setDouble(6, qty);
			ps.setDouble(7, price);
			ps.executeUpdate();
			ps.close();
			rs.close();
			return nextoid;
		

			



		} catch (SQLException e) {
			//e.printStackTrace();
			return -1;	
		}

	}

	public boolean cancelOrder(int oid) {
		// order status must not be 'S'
		try{
			
			String statusCheck = "SELECT status FROM a2.orders WHERE oid = ?";
			ps = connection.prepareStatement(statusCheck);
			ps.setInt(1, oid);
			rs = ps.executeQuery();
			if (rs.next() == true){
				String stat = rs.getString(1);
				
				
				// cancel order
				if (stat.equals("O")){
					String cancelText = "DELETE FROM a2.orders WHERE oid = ?";
					ps = connection.prepareStatement(cancelText);
					ps.setInt(1, oid);
					ps.executeUpdate();
					ps.close();
					rs.close();
					return true;
				} else{
					ps.close();
					rs.close();
					return false;
				}
			} else{
				ps.close();
				rs.close();
				return false;
			}

		} catch (SQLException e){
			//e.printStackTrace();
			return false;
		}




	}

	public boolean shipOrder(int oid){
		try{
			//get wid in orders
			String getWid = "SELECT shipwid, quantity, pid FROM a2.orders WHERE oid = ?";
			ps = connection.prepareStatement(getWid);
			ps.setInt(1, oid);
			rs = ps.executeQuery();
			int ware;
			double oquantity;
			int opid;
			if (rs.next() == true){
				ware = rs.getInt(1);
				oquantity = rs.getDouble(2);
				opid = rs.getInt(3);
			} else {
				ps.close();
				rs.close();
				return false;
			}

			//get quantity, pid in stock using wid
			String findQ = "SELECT quantity, pid FROM a2.stock WHERE wid = ? AND pid = ?";
			ps = connection.prepareStatement(findQ);
			ps.setInt(1, ware);
			ps.setInt(2, opid);
			rs = ps.executeQuery();
			double squantity;

			if (rs.next() == true){
				squantity = rs.getDouble(1);
			} else {
				ps.close();
				rs.close();
				return false;
			}

			// check if possible
			if (squantity >= oquantity){
				String updateStatus = "UPDATE a2.orders SET status = 'S' WHERE oid = ?";
				ps = connection.prepareStatement(updateStatus);
				ps.setInt(1, oid);
				ps.executeUpdate();
				updateStock(opid, ware, (- oquantity));
				ps.close();
				rs.close();
				return true;

			} else{
				ps.close();
				rs.close();
				return false;
			}

		}
		catch (SQLException e){
			//e.printStackTrace();
			return false;
		}

	}

	public String listStock(int pid){
		try{
			// get wids  and quantity from stock given pids

			String getWid = "SELECT wid, quantity FROM a2.stock WHERE pid = ? ORDER BY quantity DESC";
			ps = connection.prepareStatement(getWid);
			ps.setInt(1, pid);
			rs = ps.executeQuery();
			String result = "";
			
			// make string
			while (rs.next()){
				result = result + rs.getInt(1) + ":" + rs.getDouble(2) + "#";

			}
			if (result.equals("")){
				ps.close();
				rs.close();
				return result;
			}
			ps.close();
			rs.close();
			return result.substring(0, result.length() -1);
		}
		catch (SQLException e){
			//e.printStackTrace();
			return "";
		}

	}

	public String listReferrals(int cid){
		try{
			// get custrefs from referral
			String getCustRefs = "SELECT custref FROM a2.referral WHERE custid = ?";
			ps = connection.prepareStatement(getCustRefs);
			ps.setInt(1, cid);
			rs = ps.executeQuery();
		
			
			// get cname from customer using custrefs
			PreparedStatement ts;
			ResultSet tr;

			String getCnames = "SELECT cname FROM a2.customer WHERE cid = ?";
			ts = connection.prepareStatement(getCnames);
			
			
			// make string
			String result = "";
			int c;
			while (rs.next()){
				c = rs.getInt(1);
				ts.setInt(1, c);
				tr = ts.executeQuery();
				tr.next();
				result = result + c + ":" + tr.getString(1) + "#";
				tr.close();
			}
			ts.close();

			ps.close();
			rs.close();
			
			if (result.equals("")){
				return result;
			}
			return result.substring(0, result.length() -1);


		}
		catch (SQLException e) {
			//e.printStackTrace();
			return "";
		}

	}

	public boolean updateDB(){
		try {
			String drop = "DROP TABLE IF EXISTS a2.bestsellers";
			sql = connection.createStatement();
			sql.executeUpdate(drop);
			sql.close();
			
			// create table
			sql = connection.createStatement();

			String create = "CREATE TABLE a2.bestsellers(pid INTEGER, sales NUMERIC(10, 2))";
			sql.executeUpdate(create);
			sql.close();
			
			
			// get pids from order w/ sales amount greater than 10000
			String query = "SELECT pid, sum(quantity*price) as sales "
					+ "FROM a2.orders "
					+ "WHERE status = 'S' and (quantity*price) > 10000 GROUP BY pid;";
			ps = connection.prepareStatement(query);
			rs = ps.executeQuery();
			
			
			
			
			
			
			PreparedStatement ts;

			// insert 
			String insert = "INSERT INTO a2.bestsellers VALUES (?, ?)";
			while (rs.next()){
				ts = connection.prepareStatement(insert);
				ts.setInt(1, rs.getInt(1));
				ts.setDouble(2, rs.getDouble(2));
				ts.executeUpdate();
				ts.close();

			}
			
			ps.close();
			rs.close();
			return true;


		}
		catch (SQLException e) {
			////e.printStackTrace();
			return false;

		}

	}
}



