import java.sql.*;
import java.lang.*;

public class Assignment2 {
    
  // A connection to the database  
  Connection connection;
  
  // Statement to run queries
  Statement sql;
  
  // Prepared Statement
  PreparedStatement ps;
  
  // Resultset for the query
  ResultSet rs;

  // String to store our queries before execution
  String queryString;
  
  //CONSTRUCTOR
  Assignment2(){
	try{
		Class.forName("org.postgresql.Driver");
	}
	catch (ClassNotFoundException e){
		System.out.println("Failed to find the JDBC driver");
	}
  }
  
  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password){
	try{
		this.connection = DriverManager.getConnection(URL, username, password);
		this.sql = connection.createStatement();
		this.sql.execute("SET search_path TO A2");
	}
	catch(SQLException s){
		System.err.println("SQL Error Found." + "(Error): " + s.getMessage());
		return false;
	}
	return true;
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
	try{
		this.connection.close();
		this.sql.close();
	}
	catch(SQLException s){
		System.err.println("SQL Error Found." + "(Error): " + s.getMessage());
		return false;
	}
	return true;
  }
    
  public boolean insertStock(int id, int wid, double qty) {
	try{
		if (qty >= 0){
			queryString = "INSERT INTO stock VALUES (" + id + "," + wid + "," + qty + ")";
			this.sql.executeUpdate(queryString);
		}
		else{
			System.out.println("Quantity must be greater than 0");
			return false;
		}
	}
	catch(SQLException s){
		System.err.println("SQL Error Found." + "(Error): " + s.getMessage());
		return false;
	}
	return true;
  }
  
   public boolean updateStock(int id, int wid, double qty) {
	try{
		int quantityCurrent;
		queryString = "SELECT quantity FROM stock where pid =" + id + " AND wid =" + wid;
		this.ps = this.connection.prepareStatement(queryString);
		this.rs = this.ps.executeQuery();
		if (this.rs.next()){
			quantityCurrent = this.rs.getInt("quantity");
			System.out.println(quantityCurrent);
			if (qty < 0){
				if (quantityCurrent == 0){
					System.out.println("No Product with id given found in warehouse or quantity available is 0");
					return false;
				}
				if (Math.abs(qty) <= quantityCurrent){
					queryString = "UPDATE stock set quantity =" + (quantityCurrent + qty) + " WHERE pid =" + id + " AND wid =" + wid;
					this.sql.executeUpdate(queryString);
				}
				else{
					System.out.println("Quantity to remove cannot be greater than quantity available");
					return false;
				}
			}
			else{
				queryString = "UPDATE stock set quantity =" + (quantityCurrent + qty) + " WHERE pid =" + id + " AND wid =" + wid;
				this.sql.executeUpdate(queryString);
			}
		}
		else{
			System.out.println("No product with id given found in warehouse");
			return false;
		}
	}
	catch(SQLException s){
		System.err.println("SQL Error Found." + "(Error): " + s.getMessage());
		return false;
	}
	return true;
  }
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
	int currentOID = 0;
	try{
		queryString = "SELECT nextval('orders_oid_seq');";
		this.ps = this.connection.prepareStatement(queryString);
		this.rs = this.ps.executeQuery();
		if (this.rs.next()){
			currentOID = this.rs.getInt("nextval");
		}
	}
	catch (SQLException s){
		System.err.println("SQL Error Found." + "(Error): " + s.getMessage());
		return -1;

	}
	try{
		queryString = "INSERT INTO orders(oid,cid,pid,odate,shipwid,quantity,price,status) VALUES (" + currentOID + "," + cid + ","
		+ pid + "," + "current_date," + wid + "," + qty + "," + price + ",'O');";
		this.sql.executeUpdate(queryString);
	}
	catch (SQLException s){
		System.err.println("SQL Error Found." + "(Error): " + s.getMessage());
		return -1;
	}
	return currentOID;
  }
  public boolean cancelOrder(int oid) {
	try{
		String status;
		queryString = "SELECT status FROM orders where oid =" + oid;
                this.ps = this.connection.prepareStatement(queryString);
                this.rs = this.ps.executeQuery();
		if (this.rs.next()){
			status = this.rs.getString("status");
			if (status.equals("S")){
				System.out.println("Cannot cancel shipped orders");
				return false;
			}
			else{
				queryString = "DELETE FROM orders where oid =" + oid;
				this.sql.executeUpdate(queryString);
			}
		}
		else{
			System.out.println("No order found with id given");
			return false;
		}
		return true;
	}
	catch(SQLException s){
		System.err.println("SQL Error Found." + "(Error): " + s.getMessage());
		return false;
	}
  }
  public boolean shipOrder(int oid){
		try{
			String status;
			String toSet = "S";
			int quantityToShip;
			int quantityAvailable;
			int pid;
			int shipwid;
			queryString = "SELECT status,quantity,pid,shipwid FROM orders where oid =" + oid;
			this.ps = this.connection.prepareStatement(queryString);
			this.rs = this.ps.executeQuery();
			if (this.rs.next()){
				quantityToShip = this.rs.getInt("quantity");
				status = this.rs.getString("status");
				pid = this.rs.getInt("pid");
				shipwid = this.rs.getInt("shipwid");
				if (status.equals("S")){
					System.out.println("Cannot ship shipped orders");
					return false;
				}
				else{
					queryString = "SELECT quantity FROM stock where wid =" + shipwid + " AND pid =" + pid;
					this.ps = this.connection.prepareStatement(queryString);
					this.rs = this.ps.executeQuery();
					if (this.rs.next()){
						quantityAvailable = this.rs.getInt("quantity");
						if (quantityAvailable >= quantityToShip){
							this.updateStock(pid,shipwid,-quantityToShip);
							queryString = "UPDATE orders SET status = 'S' WHERE oid =" + oid;
							this.sql.executeUpdate(queryString);
						}
						else{
							System.out.println("Insufficient quantity available to ship");
							return false;
						}
					}
					else{
						System.out.println("No stock found in this warehouse");
						return false;
					}
				}
			}
			else{
				System.out.println("No order found with this id");
				return false;
			}
		return true;
		}
		catch(SQLException s){
                System.err.println("SQL Error Found." + "(Error): " + s.getMessage());
                return false;
        }
  }
  
  public String listStock(int pid){
	String stock = "";
	try{
		queryString = "SELECT wid,quantity FROM stock where pid =" + pid + " AND quantity > 0";
		this.ps = this.connection.prepareStatement(queryString);
		this.rs = this.ps.executeQuery();
		while(this.rs.next()){
			stock += this.rs.getInt("wid") + ":" + this.rs.getInt("quantity") + "#";
		}
		return stock;
	}
	catch(SQLException s){
		System.err.println("SQL Error Found." + "(Error): " + s.getMessage());
	}
	return stock;
}
  
  public String listReferrals(int cid){
	String referrals = "";
	try{
		queryString = "SELECT r.custref, c.cname FROM referral r LEFT JOIN customer c ON (r.custref=c.cid) WHERE r.custid =" + cid;
		this.ps = this.connection.prepareStatement(queryString);
		this.rs = this.ps.executeQuery();
		while(this.rs.next()){
			referrals += this.rs.getInt("custref") + ":" + this.rs.getString("cname") + "#";
		}
		return referrals;
	}
	catch(SQLException s){
		System.err.println("SQL Error Found." + "(Error): " + s.getMessage());
	}
	return referrals;
  }
    
  public boolean updateDB(){
	try{
		queryString = "CREATE TABLE IF NOT EXISTS bestsellers (\n"
		+ " pid	integer	REFERENCES product(pid) ON DELETE RESTRICT,\n"
		+ " sales	NUMERIC(10,2)\n"
		+ ");";
		this.sql.executeUpdate(queryString);
		queryString = "CREATE VIEW productsales as (SELECT p.pid, sum((o.price * o.quantity)) as sales FROM product p"
		+ " LEFT JOIN orders o ON (p.pid = o.pid) WHERE o.status = 'S' GROUP BY p.pid);";
		this.sql.executeUpdate(queryString);
		queryString = "INSERT INTO bestsellers (SELECT pid, sales FROM productsales WHERE sales > 10000);";
		this.sql.executeUpdate(queryString);
		queryString = "DROP VIEW productsales;";
		this.sql.executeUpdate(queryString);
	}
	catch(SQLException s){
		System.err.println("SQL Error Found." + "(Error): " + s.getMessage());
		return false;
	}
	return true;
  }
  
}
