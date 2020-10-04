import java.sql.*;
import java.time.LocalDate;
import java.time.format.*;
import java.io.IOException;

public class Assignment2 {
    
  // A connection to the database  
  Connection connection;

  // Statement to run queries
  Statement sql;

  // Prepared Statement
  PreparedStatement ps;
  
  // Result set for the query
  ResultSet rs;
  
  //CONSTRUCTOR
  Assignment2() throws SQLException{      
          
          try {
                        // Load JDBC driver
                        Class.forName("org.postgresql.Driver");

                } catch (ClassNotFoundException e) {

                        e.printStackTrace();
                }
          
  }
  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password){
                try {
                        //Make the connection to the database, ****** but replace "username" with your username ******
			String sqlText;
                        connection = DriverManager.getConnection(URL, username, password);
                        connection.setAutoCommit(false);
                } catch (SQLException e) {

                        e.printStackTrace();
                        return false;

                }
                if (connection != null) {
                        return true;
                }
      return false;
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
          
          try {
                connection.close();
                return true;
        } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
      return false;    
  }
    
  public boolean insertStock(int id, int wid, double qty) {
          
           try {
                sql = connection.createStatement();
                String sqlText;
                String tupleExist = "SELECT * FROM stock WHERE pid = ? AND wid = ?";
                String pidExist = "SELECT * FROM product WHERE pid = ?";
                String widExist = "SELECT * FROM warehouse WHERE wid = ?";
                ps = connection.prepareStatement(tupleExist);
                ps.setInt(1,id);
                ps.setInt(2, wid);
                ResultSet tupleExistSet = ps.executeQuery();
                ps = connection.prepareStatement(pidExist);
                ps.setInt(1, id);
                ResultSet pidExistSet = ps.executeQuery();
                ps = connection.prepareStatement(widExist);
                ps.setInt(1, wid);
                ResultSet widExistSet = ps.executeQuery();

                if  (!tupleExistSet.next()){ 
                        if (pidExistSet.next() && widExistSet.next() && qty>=0){
                                sqlText = "INSERT INTO stock " + 
                                                "VALUES ("+id+ "," + wid + "," + qty+ ")";
                                sql.executeUpdate(sqlText);
                                connection.commit();
                                return true;
                        }
                }
                return false;
                
        } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
          
   return false;
  }
  
   public boolean updateStock(int id, int wid, double qty) {
           
           String tupleExist = "SELECT quantity FROM stock WHERE pid = ? AND wid = ?";
           try {
                String sqlText;
                double updated_value;
                ps = connection.prepareStatement(tupleExist);
                ps.setInt(1, id);
                ps.setInt(2, wid);
                ResultSet tupleExistSet = ps.executeQuery();
                if  (tupleExistSet.next()){
                        if(qty<0){
                                if(Math.abs(qty) <= tupleExistSet.getInt("quantity")){
                                        
                                        updated_value = tupleExistSet.getInt("quantity") + qty; 
                                        sqlText = "UPDATE stock     " +
                                                          "   SET quantity = " + updated_value 
                                                          + "WHERE pid = " + id + "AND wid = " + wid;
                                        sql.executeUpdate(sqlText);
                                        connection.commit();
                                        return true;
                                }
                                else{
                                        return false;
                                }
                        }
                        updated_value = tupleExistSet.getInt("quantity") + qty; 
                        sqlText = "UPDATE stock     " +
                                          "   SET quantity = " + updated_value 
                                          + "WHERE pid = " + id + "AND wid = " + wid;
                        sql.executeUpdate(sqlText);
                        connection.commit();
                        return true;
                }
        } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
                        
   return false;
  }
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price){
          String validValues = "SELECT * FROM customer c, product p, warehouse w WHERE c.cid = ? AND  p.pid = ? AND w.wid = ?";
          String sqlText;
          int newOid=0;
          
          try {
                ps = connection.prepareStatement(validValues);
                ps.setInt(1, cid);
                  ps.setInt(2, pid);
                  ps.setInt(3, wid);
                  ResultSet validValuesSet = ps.executeQuery();

                  if (validValuesSet.next()){
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate localDate = LocalDate.now();
                        String currentDate = dtf.format(localDate);
                        String getOid = "SELECT nextval(pg_get_serial_sequence('orders','oid'))";
                        ps = connection.prepareStatement(getOid);
                        ResultSet getOidSet = ps.executeQuery();
                        if (getOidSet != null){
				while (getOidSet.next()){
                                newOid = getOidSet.getInt("nextval");
				}
                        }
                        else{
                        return -1;
                        }

                        //will get oid SELECT nextval(pg_get_serial_sequence('orders','oid'))
                        sqlText = "INSERT INTO orders " +
                                                    "VALUES (? , ? , ? , TO_DATE(?,'YYYY-MM-DD') , ? , ? , ? , 'O')";
                        ps = connection.prepareStatement(sqlText);
                        ps.setInt(1, newOid);
                        ps.setInt(2, cid);
                        ps.setInt(3, pid);
                        ps.setString(4, currentDate);
                        ps.setInt(5, wid);
                        ps.setDouble(6, qty);
                        ps.setDouble(7, price);
                        ps.executeUpdate();
                        connection.commit();
                        return newOid;
                        }

        } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
                  return -1;
  }

  public boolean cancelOrder(int oid) {
          String tupleExist = "SELECT oid FROM orders WHERE oid = ? AND status = 'O'";
          try {
                String sqlText;
                ps = connection.prepareStatement(tupleExist);
                ps.setInt(1, oid);
                ResultSet tupleExistSet = ps.executeQuery();
                
                //set not empty so that oid has been ordered
                if(tupleExistSet.next()){
                        //cancel here
                        sqlText = "DELETE FROM orders " + 
                                  "WHERE oid = " + oid;
                        sql.executeUpdate(sqlText);
                        connection.commit();
                        return true;
                }
                return false;
        } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
          
   return false;
  }

  public boolean shipOrder(int oid){
          String tupleExist = "SELECT o.pid, s.wid, o.quantity FROM orders o, stock s WHERE o.oid = ? AND o.pid = s.pid AND s.wid = o.shipwid AND s.quantity>=o.quantity AND o.status = 'O'";
          try {
                String sqlText;
                ps = connection.prepareStatement(tupleExist);
                ps.setInt(1, oid);
                ResultSet tupleExistSet = ps.executeQuery();
                
                //update stock and status
                if(tupleExistSet.next()){
                        //update status for that order
                        sqlText = "UPDATE orders     " +
                                          "   SET status = 'S'" 
                                          + "WHERE oid = " + oid;
                        sql.executeUpdate(sqlText);
                        int negVal = 0 - tupleExistSet.getInt("quantity");
                        //update stock
                        connection.commit();
                        updateStock(tupleExistSet.getInt("pid"), tupleExistSet.getInt("wid"), negVal);
                        return true;
                }
        return false;
        } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }       
          
   return false;        
  }
  
  public String listStock(int pid){
        String tupleExist = "SELECT wid, quantity FROM stock WHERE quantity > 0 AND pid = ? ORDER BY quantity DESC";  
        String sqlText;
        try {
                String s = "";
                ps = connection.prepareStatement(tupleExist);
                ps.setInt(1, pid);
                ResultSet tupleExistSet = ps.executeQuery();
                
                //loops through each tuple
                if(tupleExistSet.next()){
		s = s + Integer.toString(tupleExistSet.getInt("wid")) + ":" + Double.toString(tupleExistSet.getDouble				("quantity")) + "#";
                while(tupleExistSet.next()){
                        s = s + Integer.toString(tupleExistSet.getInt("wid")) + ":" + Double.toString(tupleExistSet.getDouble("quantity")) + "#";
                }
                
                s = s.substring(0, s.length() - 1);
                return s;
                }
        } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }         
        return "";
  }
  
  public String listReferrals(int cid){
          String allRefferals = "SELECT c.cid, c.cname FROM customer c, referral r WHERE c.cid = r.custref AND r.custid = ? ORDER BY c.cname";
          String s = "";
          try {
                ps = connection.prepareStatement(allRefferals);
                ps.setInt(1, cid);
                  ResultSet allRefferalsSet = ps.executeQuery();
                  if (allRefferalsSet.next()){
			s = allRefferalsSet.getInt("cid") +  ":" +  allRefferalsSet.getString("cname") + "#";
                          while(allRefferalsSet.next()){
                                  s = s + allRefferalsSet.getInt("cid") +  ":" +  allRefferalsSet.getString("cname") + "#";
                          }
                
                          s = s.substring(0, s.length() -1);
                          return s;
                  }
        } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
          
          return "";
  }
    
  public boolean updateDB(){
          
          String drop = "DROP TABLE IF EXISTS bestsellers CASCADE";
	  String dropView = "DROP VIEW IF EXISTS bestSellersView CASCADE";
          String sqlText;
                sqlText = "CREATE TABLE bestsellers(                  " +
                          "                       pid int,         " +
                          "               sales numeric(10,2)" +
                          "                      ) ";
	String insertView = "CREATE VIEW bestSellersView AS (SELECT pid, sum(quantity*price) AS \"sales\" FROM orders WHERE status = 'S' GROUP BY pid)";
        String insert = "INSERT INTO bestsellers (SELECT pid, sales FROM bestSellersView WHERE sales>10000.00)";
        try {
                sql.executeUpdate(drop);
                sql.executeUpdate(sqlText);
		sql.executeUpdate(insertView);
                sql.executeUpdate(insert);
		sql.executeUpdate(dropView);
                connection.commit();
		return true;
        } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        
        return false;    
  }

}

