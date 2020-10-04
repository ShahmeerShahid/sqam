import java.sql.*;

public class Assignment2 {

  // A connection to the database  
  static Connection connection;

  // Statement to run queries
  Statement sql;

  // Prepared Statement
  PreparedStatement ps;

  // Resultset for the query
  ResultSet rs;

  //CONSTRUCTOR
  Assignment2(){
  }

  public boolean connectDB(String URL, String username, String password) {
    // Load JDBC driver
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    //connect to SQL database
    try {
      connection = DriverManager.getConnection(URL + username + "_343", username, password);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    if (connection != null) {
      return true;
    } else {
      return false;
    }
  }

  public boolean disconnectDB() {
    try {
      connection.close();
    } catch (SQLException e) {
      return false;
    }
    return true;
  }

  public boolean insertStock(int id, int wid, double qty) {
    if (qty < 0){
      return false;
    }

    try {
      String checkProductTable = "SELECT pid FROM product WHERE pid=?";
      PreparedStatement cpt = connection.prepareStatement(checkProductTable);
      cpt.setInt(1,id);
      rs = cpt.executeQuery();
      if (!rs.isBeforeFirst()){
        return false;
      }
      
      String checkWarehouseTable = "SELECT wid FROM warehouse WHERE wid=?";
      PreparedStatement cwt = connection.prepareStatement(checkWarehouseTable);
      cwt.setInt(1,wid);
      rs = cwt.executeQuery();
      if (rs.next()){
        int tableWid = rs.getInt(1);
        if (wid != tableWid){
          return false;
        }
      } else {
        return false;
      }
      
      String checkStockTable = "SELECT pid, wid FROM stock WHERE pid=? AND wid=?";
      PreparedStatement cst = connection.prepareStatement(checkStockTable);
      cst.setInt(1,id);
      cst.setInt(2,wid);
      rs = cst.executeQuery();
      if (rs.isBeforeFirst()){
        return false;
      }

      /*
      //quantity >= 0 in stock table
      String checkquantity = "SELECT quantity FROM stock WHERE pid=?";
      PreparedStatement ct = connection.prepareStatement(checkquantity);
      ct.setInt(1,id);
      rs = ct.executeQuery();
      if (rs.isBeforeFirst()){
        int tableQuantity = rs.getInt("quantity");
        if (tableQuantity < 0){
          return false;
        }
      }
      */
      
      connection.setAutoCommit(false);

      String updateString = "INSERT into stock VALUES (?, ?, ?)";
      PreparedStatement updateStock = connection.prepareStatement(updateString);
      updateStock.setInt(1, id);
      updateStock.setInt(2, wid);
      updateStock.setDouble(3, qty);
      int affectedrows = updateStock.executeUpdate();
      updateStock.close();
      connection.commit();

      connection.setAutoCommit(true);

    } catch (SQLException e) {
      return false;
    }
    return true;
  }

  public boolean updateStock(int id, int wid, double qty) {
    try {
      String checkStockTable = "SELECT pid, wid, quantity FROM stock WHERE pid=? AND wid=?";
      PreparedStatement cst = connection.prepareStatement(checkStockTable);
      cst.setInt(1,id);
      cst.setInt(2,wid);
      rs = cst.executeQuery();
      if (!rs.isBeforeFirst()){
        return false;
      }
      
      if (rs.next()){
        int quantity = rs.getInt(3);
        if (qty < 0){
          if (quantity < Math.abs(qty)){
            return false;
          } else {
            qty = qty - quantity;
          } 
        }
      } else {
        return false;
      } 

      String updateStock = "UPDATE stock SET quantity=? WHERE pid=? AND wid=?";
      int affectedrows = 0;
    
      PreparedStatement ups = connection.prepareStatement(updateStock);
      ups.setDouble(1, qty);
      ups.setInt(2, id);
      ups.setInt(3, wid);
      affectedrows = ups.executeUpdate();
      if (affectedrows > 0){
        return true;
      } else {
        return false;
      }    
    } catch (SQLException e){
      return false;
    }
  }

  public int insertOrder(int cid, int pid, int wid, double qty, double price) {
    try {
      String checkCid = "SELECT cid FROM customer WHERE cid=?";
      PreparedStatement cc = connection.prepareStatement(checkCid);
      cc.setInt(1,cid);
      rs = cc.executeQuery();
      if (!rs.isBeforeFirst()){
        return -1;
      } 

      String checkPid = "SELECT pid FROM product WHERE pid=?";
      PreparedStatement cp = connection.prepareStatement(checkPid);
      cp.setInt(1,pid);
      rs = cp.executeQuery();
      if (!rs.isBeforeFirst()){
        return -1;
      }

      String checkWid = "SELECT wid FROM warehouse WHERE wid=?";
      PreparedStatement cw = connection.prepareStatement(checkWid);
      cw.setInt(1,wid);
      rs = cw.executeQuery();
      if (!rs.isBeforeFirst()){
        return -1;
      }

      String insertOrder = "INSERT INTO orders VALUES (DEFAULT, ?, ?, CURRENT_DATE, ?, ?, ?, 'O')"; 
      PreparedStatement io = connection.prepareStatement(insertOrder);
      io.setInt(1,cid);
      io.setInt(2,pid);
      io.setInt(3,wid);
      io.setDouble(4,qty);
      io.setDouble(5,price);
      int affectedrows = io.executeUpdate();      
      
      if (affectedrows > 0) {
        String getOid = "SELECT currval(pg_get_serial_sequence('orders', 'oid'))";
        io = connection.prepareStatement(getOid);
        rs = io.executeQuery();
        if (rs.next()) {
            int id = rs.getInt(1);
            return id;
        } else {
          return -1;
        }
      }
    }catch (SQLException e){
      return -1;
    }
    
    return -1;
  }

  public boolean cancelOrder(int oid) {
    try {
      String checkCancelOrder = "SELECT oid, status FROM orders WHERE oid=? AND status='O'";
      PreparedStatement cco = connection.prepareStatement(checkCancelOrder);
      cco.setInt(1,oid);
      rs = cco.executeQuery();
      if (rs.isBeforeFirst()){
          String deleteOrder = "DELETE from orders WHERE oid=? AND status='O'";
          PreparedStatement dorder = connection.prepareStatement(deleteOrder);
          dorder.setInt(1,oid);
          int affectedrows = 0;
          affectedrows = dorder.executeUpdate();
          if (affectedrows > 0){
            return true;
          } else {
            return false;
          }
      }
    } catch (SQLException e) {
      return false;
    }
    return false;
  }

  public boolean shipOrder(int oid) {
    try {
      String checkOrderQuantity = "SELECT quantity,shipwid,pid, status FROM orders WHERE oid=?";
      PreparedStatement coq = connection.prepareStatement(checkOrderQuantity);
      coq.setInt(1,oid);
      rs = coq.executeQuery();

      int orderQuantity = 0;
      int orderWarehouse = 0;
      int orderPid = 0;
      String orderStatus = "";
      if (rs.next()){
        orderQuantity = rs.getInt(1);
        orderWarehouse = rs.getInt(2);
        orderPid = rs.getInt(3);
        orderStatus = rs.getString(4);
        if (orderStatus == "S"){
          return false;
        }
      } else {
        return false;
      }

      String checkStock = "SELECT quantity FROM stock WHERE wid=? AND pid=?";
      PreparedStatement cs = connection.prepareStatement(checkStock);
      cs.setInt(1,orderWarehouse);
      cs.setInt(2,orderPid);
      rs = cs.executeQuery();

      int stockQuantity = -1;
      if (rs.next()){
        stockQuantity = rs.getInt(1);
      } else {
        return false;
      }

      if (stockQuantity >= orderQuantity){
        String updateOrdersTable = "UPDATE orders SET status='S' WHERE oid=? AND pid=?";

        connection.setAutoCommit(false);

        if (! updateStock(orderPid, orderWarehouse, stockQuantity - orderQuantity)){
          return false;
        }
        PreparedStatement uot = connection.prepareStatement(updateOrdersTable);
        uot.setInt(1, oid);
        uot.setInt(2, orderPid);
        int affectedrows = uot.executeUpdate();
        connection.commit();
        connection.setAutoCommit(true);
        return true;

      } else {
        return false;
      }

    } catch (SQLException e){
      return false;
    }
  }

  public String listStock(int pid) {
    String res = "";

    try {
      String sql = "SELECT * FROM stock WHERE quantity>0 AND pid=? ORDER BY quantity DESC";
      PreparedStatement stmt = connection.prepareStatement(sql);
      stmt.setInt(1, pid);
      rs = stmt.executeQuery();
      if (! rs.isBeforeFirst()){
        return res;
      }

      while(rs.next()){
        int quantity  = rs.getInt("quantity");
        String wid = rs.getString("wid");

        res += wid + ":" + quantity + "#";
      }
      rs.close();

    } catch(SQLException se){
      return "";
    }

    res = res.replaceAll("#$", ""); // remove the # at the end of res
    return res;
  }

  public String listReferrals(int cid) {
    String res = "";

    try {
      String sql = "SELECT c.cid as cid, c.cname as cname FROM referral r JOIN customer c ON r.custref=c.cid WHERE r.custid=? ORDER BY cname";
      PreparedStatement stmt = connection.prepareStatement(sql);
      stmt.setInt(1, cid);
      rs = stmt.executeQuery();

      if (! rs.isBeforeFirst()){
        return res;
      }

      while(rs.next()){
        int id  = rs.getInt("cid");
        String cname = rs.getString("cname");

        res += id + ":" + cname + "#";
      }
      rs.close();

    } catch(SQLException se){
      return "";
    }

    res = res.replaceAll("#$", ""); // remove the # at the end of res
    return res;
  }

  public boolean updateDB() {
    try {
      Statement stmt = connection.createStatement();
      String sql = "DROP TABLE IF EXISTS bestsellers";
      stmt.executeUpdate(sql);

      stmt = connection.createStatement();
      sql = "CREATE TABLE IF NOT EXISTS bestsellers(" +
              "pid INTEGER PRIMARY KEY," +
              "sales NUMERIC(10,2))";
      stmt.executeUpdate(sql);

      sql = "INSERT INTO bestsellers " +
              "SELECT pid, CAST(SUM(price*quantity) AS NUMERIC(10,2)) as sales " +
              "FROM orders WHERE status='S' " +
              "GROUP BY pid " +
              "HAVING SUM(price*quantity)>10000.00";
      stmt = connection.createStatement();
      stmt.executeUpdate(sql);

      return true;

    } catch(SQLException se){
      return false;
    }

  }
}
