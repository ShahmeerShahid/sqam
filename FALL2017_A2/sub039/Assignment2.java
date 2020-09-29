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
            connection = DriverManager.getConnection("jdbc:postgresql://mcsdb.utm.utoronto.ca:5432/utorid_343", username, password); 
        } catch (SQLException e) {
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
        try {
            ps = connection.prepareStatement("INSERT INTO stock (pid, wid, quantity) VALUES (?,?,?)");
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
        try {
            ps = connection.prepareStatement("UPDATE stock SET wid=?, quantity=? WHERE pid=?");
            ps.setInt(1, wid);
            ps.setDouble(2, qty);
            ps.setInt(3, id);
            ps.executeUpdate();
            ps.close();
        } catch(SQLException e) {
            return false;
        }
        return true;
    }
    
    public int insertOrder(int cid, int pid, int wid, double qty, double price){
        
        return -1;
    }
    
    public boolean cancelOrder(int oid) {
        int i = 0;
        try{
            ps = connection.prepareStatement("DELETE FROM orders WHERE oid=? AND status='O'");
            ps.setInt(1, oid);
            i = ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            return false;
        }
        return (i == 0) ? false : true;
    }
    
    public boolean shipOrder(int oid){
        int i = 0;
        try{
            ps = connection.prepareStatement("UPDATE orders SET status='S' WHERE oid=? AND orders.quantity < (SELECT MAX(stock.quantity) FROM stock WHERE stock.pid=orders.pid)");
            ps.setInt(1, oid);
            i = ps.executeUpdate();
            ps.close();
            ps = connection.prepareStatement("SELECT pid, wid, orders.quantity AS oqty, stock.quantity AS sqty FROM orders INNER JOIN stock ON orders.quantity=stock.quantity WHERE oid=?");
            ps.setInt(1, oid);
            rs = ps.executeQuery();
            if(rs != null){
                rs.next();
                updateStock(rs.getInt("pid"), rs.getInt("wid"), rs.getInt("sqty") - rs.getInt("oqty"));
            }else{
                return false;
            }
            ps.close();
        } catch (SQLException e) {
            return false;
        }
        return (i == 0) ? false : true;
    }
    
    public String listStock(int pid) throws SQLException{
        String retString = "";
        ps = connection.prepareStatement("SELECT wid, quantity FROM stock WHERE pid=? ORDER BY quantity DESC");
        ps.setInt(1, pid);
        rs = ps.executeQuery();
        if(rs != null){
            while(rs.next())
                retString += rs.getInt("wid") + ":" + rs.getInt("quantity") + "#";
            retString = retString.substring(0, retString.length() - 1);
        }
	return retString;
    }
    
    public String listReferrals(int cid) throws SQLException{
        String retString = "";
        ps = connection.prepareStatement("SELECT cid, cname FROM customer WHERE cid=? ORDER BY cname ASC");
        ps.setInt(1, cid);
        rs = ps.executeQuery();
        if(rs != null){
            while(rs.next())
                retString += rs.getInt("cid") + ":" + rs.getInt("cname") + "#";
            retString = retString.substring(0, retString.length() - 1);
        }
	return retString;
    }
    
    public boolean updateDB(){
        
	return false;    
    }
    
}
