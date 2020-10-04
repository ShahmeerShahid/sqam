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
  }
 
  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password){


    try {
        Class.forName("org.postgresql.Driver");
    }catch (ClassNotFoundException e) {
        return false;
    }

    try {
        connection = DriverManager.getConnection(URL, username, password);
    } catch (SQLException se) {
        return false;
    }

    return true;

  }
 
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
    
    try {
	
	if (ps != null) ps.close();
	if (rs != null) rs.close();

        connection.close();
        return true;

    } catch (SQLException se) {
        return false;
    }

  }
    
  public boolean insertStock(int id, int wid, double qty) {

    if(qty>=0){
        
        try {

		String test = "select * from stock where pid=? and wid=?;";
		ps  = connection.prepareStatement(test);
		ps.setInt(1,id);
		ps.setInt(2,wid);
		rs = ps.executeQuery();
		if (rs.next()) {
			return false;
		}
		
		String queryString = "insert into stock (pid, wid, quantity) values (?, ?, ?);";
		ps = connection.prepareStatement(queryString);
		ps.setInt(1,id);
		ps.setInt(2,wid);
		ps.setDouble(3,qty);

		ps.executeUpdate();
		
		return true;
            
        }
        catch(SQLException se) {
 
		return false;
            
        }
    }
    
    return false;

  }
 
  public boolean updateStock(int id, int wid, double qty) {

    try {
        String checkqty = "select quantity from stock where pid =? and wid =?;";
        ps = connection.prepareStatement(checkqty);
        ps.setInt(1,id);
        ps.setInt(2, wid);
        rs = ps.executeQuery();
	
        if(!rs.next()){
		return false;
	}
	int quantity = rs.getInt("quantity");

	if (qty<0){
		if (-qty > quantity) {
			return false;
		}
	}

        String queryString = "update stock set quantity = ? where pid = ? and wid = ?;";
        ps = connection.prepareStatement(queryString);
        ps.setDouble(1,qty);
        ps.setInt(2,id);
	ps.setInt(3,wid);
        ps.executeUpdate();
	
        return true;
    
    } catch(SQLException se){
        
        return false;
    }

  }
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price){

    try {

        String queryString = "insert into orders (cid,pid,odate,shipwid,quantity,price,status) VALUES (?, ?, ?, ?, ?, ?, 'O');";

	ps = connection.prepareStatement(queryString);
	ps.setInt(1,cid);
	ps.setInt(2,pid);
	Date date = new Date();
	java.sql.Date sqlDate = new java.sql.Date(date.getTime());
	ps.setDate(3,sqlDate);
	ps.setInt(4,wid);
	ps.setDouble(5,qty);
	ps.setDouble(6, price);
	ps.executeUpdate();

	String result = "select currval(pg_get_serial_sequence('orders', 'oid'));";
	ps = connection.prepareStatement(result);
	rs = ps.executeQuery();
	int res=-1;
	while(rs.next()) {
		res = rs.getInt(1);
	}
	
	return res;
		  
    }catch(SQLException se){
	return -1;

    	}  
  }

  public boolean cancelOrder(int oid) {

    try{
	String testOid = "select * from orders where oid = ? and status = 'O';";
	ps = connection.prepareStatement(testOid);	
	ps.setInt(1,oid);
	rs = ps.executeQuery();
	if (!rs.next()){
		return false;
	}
        
	String queryString = "delete from orders where oid = ? and status = 'O';";
	ps = connection.prepareStatement(queryString);
	ps.setInt(1,oid);
	ps.executeUpdate();
	return true;

    }catch(SQLException se){

        return false;
    }

  }

  public boolean shipOrder(int oid){
 
   	try{

		connection.setAutoCommit(false);
		String check1 = "select * from orders where oid = ? and status = 'O';";
		ps = connection.prepareStatement(check1);	
		ps.setInt(1,oid);
		rs = ps.executeQuery();
		if (!rs.next()) return false;

		int pid = rs.getInt("pid");
		int wid = rs.getInt("shipwid");
		double orders_qty = rs.getDouble("quantity");
		
		String check2 = "select * from stock where pid = ? and wid = ?;";
		ps = connection.prepareStatement(check2);
		ps.setInt(1,pid);
		ps.setInt(2,wid);
		rs = ps.executeQuery();
		if (!rs.next())
			return false;

		double stock_qty = rs.getDouble("quantity");

		if (stock_qty < orders_qty) return false;
		
		updateStock(pid, wid, stock_qty-orders_qty);

		String updateToShipped = "update orders set status = 'S' where oid = ?";
		ps = connection.prepareStatement(updateToShipped);
		ps.setInt(1,oid);
		ps.executeUpdate();
		connection.commit();
		return true;

	}catch(SQLException se){   
		return false;
	
	}finally{
		try{
			connection.setAutoCommit(true);
		}catch(SQLException se){
			return false;
		}
	}

  }
 
  public String listStock(int pid){
	
	try{

		String getList = "select * from stock where pid = ? and quantity > 0 order by quantity desc;";
		ps = connection.prepareStatement(getList);
		ps.setInt(1,pid);
		rs = ps.executeQuery();
		String result = "";
		if(rs.next()){
			int wid = rs.getInt("wid");
			double qty = rs.getDouble("quantity");
			result += wid;
			result += ":";
			result += String.format("%.2f",qty);
		}
		while(rs.next()){
			result += "#";
			int wid = rs.getInt("wid");
			double qty = rs.getDouble("quantity");
			result += wid;
			result += ":";
			result += String.format("%.2f",qty);
		}

		return result;
	} catch(SQLException se) {
		return "";	
	}

  }
 
  public String listReferrals(int cid){
	try{
		String getReferrals = "select C.cid as cid, C.cname as name from customer C, referral R where C.cid = R.custref and R.custid = ? order by C.cname;";
		ps = connection.prepareStatement(getReferrals);
		ps.setInt(1,cid);
		rs = ps.executeQuery();
		String result = "";
	
		if(rs.next()){
			int ref_cid = rs.getInt("cid");
			String name = rs.getString("name");
			result += ref_cid;
			result += ":";
			result += name;
		}
		while(rs.next()){
			result += "#";
			int ref_cid = rs.getInt("cid");
			String name = rs.getString("name");
			result += ref_cid;
			result += ":";
			result += name;
		}

		return result;
	} catch(SQLException se) {
	
		return "";
	}

  }
    
  public boolean updateDB(){
	try {

		String createTable = "drop table if exists bestsellers; create table bestsellers(pid integer, sales numeric(10,2));";
		ps = connection.prepareStatement(createTable);
		ps.executeUpdate();

		String getBestsellers = "select * from orders where status = 'S' and quantity*price>10000;";
		ps = connection.prepareStatement(getBestsellers);
		rs = ps.executeQuery();


		while(rs.next()){
			int pid = rs.getInt("pid");
			double salesAmount = rs.getDouble("quantity") * rs.getDouble("price");
			String insert = "insert into bestsellers values (?,?);";
			ps = connection.prepareStatement(insert);
			ps.setInt(1,pid);
			ps.setDouble(2,salesAmount);			
			ps.executeUpdate();
		}
		return true;

	} catch(SQLException se) {
		return false;
	}
  }
 }

