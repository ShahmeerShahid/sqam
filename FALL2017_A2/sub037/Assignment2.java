import java.sql.*;
import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;

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
  Assignment2() {
  }

  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password) {
  	try {
  		Class.forName("org.postgresql.Driver");
  	} catch (ClassNotFoundException e) {
  		return false;
  	}

		try {
      connection = DriverManager.getConnection(URL, username, password);
      if (connection != null) {
      	return true;
      }
      return false;
    } catch (SQLException e) {
      return false;      
    }
  }

  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB() {
    try {
      connection.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public boolean insertStock(int id, int wid, double qty) {
    String queryString;
    int checkKey = 0;
    try {
      sql = connection.createStatement();
      sql.executeUpdate("SET search_path TO A2;");
      if (qty < 0) {
        return false;
      }

      rs = sql.executeQuery("SELECT * FROM product WHERE pid = " + id + ";");
      if (!(rs.next())) {
        return false;
      }

      rs = sql.executeQuery("SELECT * FROM warehouse WHERE wid = " + wid + ";");
      if (!(rs.next())) {
        return false;
      }

      rs = sql.executeQuery("SELECT * FROM stock WHERE pid = " + id + " AND wid = " + wid + ";");
      if (rs.next()) {
        return false;
      }

      queryString = "INSERT INTO stock VALUES(?, ?, ?);";
      ps = connection.prepareStatement(queryString);
      ps.setInt(1, id);
      ps.setInt(2, wid);
      ps.setDouble(3, qty);
      ps.executeUpdate();

      rs = sql.executeQuery("SELECT * FROM stock WHERE pid = " + id + " AND wid = " + wid + ";");
      while (rs.next()) {
        checkKey++;
      }
      if (checkKey != 1) {
        return false;
      }

      return true;
    } catch (SQLException e) {
      return false;
    } finally {
      try {
        rs.close();
        ps.close();
        sql.close();
      } catch (Exception e) {
      }
    }
  }

  public boolean updateStock(int id, int wid, double qty) {
    String queryString;
    int checkKey = 0;
    try {
      sql = connection.createStatement();
      sql.executeUpdate("SET search_path TO A2;");
      rs = sql.executeQuery("SELECT * FROM stock WHERE pid = " + id + " AND wid = " + wid + ";");
      while (rs.next()) {
        checkKey++;
      }
      if (checkKey != 1) {
        return false;
      }

      queryString = "UPDATE stock SET quantity = " + qty + " WHERE pid = " + id + " AND wid = " + wid + ";";
      sql.executeUpdate(queryString);
      rs = sql.executeQuery("SELECT quantity FROM stock WHERE pid = " + id + " AND wid = " + wid + ";");
      int stockQty = rs.getInt("quantity");
      if (stockQty != qty) {
        return false;
      }
      return true;
    } catch (SQLException e) {
      return false;
    } finally {
      try {
        rs.close();
        sql.close();
      } catch (Exception e) {
      }
    }
  }

  public int insertOrder(int cid, int pid, int wid, double qty, double price) {
    String queryString;
    String sqlText;
    int retoid = -1;
    try {
      sql = connection.createStatement();
      sql.executeUpdate("SET search_path TO A2;");
      Date date = new Date();

      String newDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

      sqlText = "select nextval(pg_get_serial_sequence('orders_oid_seq'));";
      rs = sql.executeQuery(sqlText);
      String oid = "";
      if (rs != null) {
      	while (rs.next()) {
      		oid = Integer.toString(rs.getInt("nextval"));
      	}
      }

      queryString = "INSERT INTO orders VALUES(" + oid + ", ?, ?, DATE'" + newDate + "', ?, ?, ?, 'O');";
      ps = connection.prepareStatement(queryString);
      ps.setInt(1, cid);
      ps.setInt(2, pid);
      ps.setInt(3, wid);
      ps.setDouble(4, qty);
      ps.setDouble(5, price);
      ps.executeUpdate();

      rs = sql.executeQuery("select currval(pg_get_serial_sequence('orders', 'oid'));");
      if (rs != null) {
      	while (rs.next()) {
      		retoid = rs.getInt("currval");
      	}
      }

      return retoid;
    } catch (SQLException e) {
      return -1;
    } finally {
      try {
        rs.close();
        ps.close();
        sql.close();
      } catch (Exception e) {
      }
    }
  }

  public boolean cancelOrder(int oid) {
    String queryString;
    try {
      sql = connection.createStatement();
      sql.executeUpdate("SET search_path TO A2;");
      queryString = "DELETE FROM orders WHERE oid = " + oid + " AND status = 'O';";
      sql.executeUpdate(queryString);
      rs = sql.executeQuery("SELECT oid FROM orders WHERE oid = " + oid + ";");
      if (rs == null) {
      	return true;
      }
      return false;
    } catch (SQLException e) {
      return false;
    } finally {
      try {
        rs.close();
        sql.close();
      } catch (Exception e) {
      }
    }
  }

  public boolean shipOrder(int oid) {
    String queryString;
    try {
      sql = connection.createStatement();
      sql.executeUpdate("SET search_path TO A2;");
      queryString = "UPDATE orders SET orders.status = 'S' WHERE oid = " + oid + " AND status = 'O';";
      sql.executeUpdate(queryString);
      rs = sql.executeQuery("SELECT * FROM orders WHERE oid = " + oid + " AND status = 'S';");
      if (rs != null) {
        return true;
      }
      return false;
    } catch (SQLException e) {
      return false;
    } finally {
      try {
        rs.close();
        sql.close();
      } catch (Exception e) {
      }
    }
  }

  public String listStock(int pid) {
    String queryString;
    String retStock = "";
    try {
      sql = connection.createStatement();
      sql.executeUpdate("SET search_path TO A2;");
      queryString = "SELECT wid, quantity FROM stock WHERE pid = " + pid + ";";
      rs = sql.executeQuery(queryString);
      if (rs != null) {
      	while (rs.next()) {
	        int wid = rs.getInt("wid");
	        double qty = rs.getDouble("quantity");
	        retStock = retStock + wid + ":" + qty + "#";
	      }
      }
      return retStock;
    } catch (SQLException e) {
      return "";
    } finally {
      try {
        rs.close();
        sql.close();
      } catch (Exception e) {
      }
    }
  }

  public String listReferrals(int cid) {
    String queryString;
    String retRef = "";
    try {
      sql = connection.createStatement();
      sql.executeUpdate("SET search_path TO A2;");
      queryString = "SELECT custid, custref FROM referral WHERE custid = " + cid + ";";
      rs = sql.executeQuery(queryString);
      if (rs != null) {
      	while (rs.next()) {
	        int custid = rs.getInt("custid");
	        int custref = rs.getInt("custref");
	        retRef = retRef + custid + ":" + custref + "#";
	      }
      }
      return retRef;
    } catch (SQLException e) {
      return "";
    } finally {
      try {
        rs.close();
        sql.close();
      } catch (Exception e) {
      }
    }
  }

  public boolean updateDB() {
    String queryString;
    queryString = "INSERT INTO bestsellers VALUES(?, ?);";
    try {
      sql = connection.createStatement();
      sql.executeUpdate("SET search_path TO A2;");
      ps = connection.prepareStatement(queryString);

      sql.executeUpdate("DROP TABLE IF EXISTS bestsellers;");

      sql.executeQuery("CREATE TABLE bestsellers (pid INTEGER, sales NUMERIC(10,2));");
      rs = sql.executeQuery("SELECT product.pid AS pid, SUM(quantity*price) AS sales FROM orders NATURAL LEFT JOIN product WHERE orders.pid = product.pid AND status = 'S' AND quantity*price > 10000 GROUP BY product.pid;");
      if (rs != null) {
      	while (rs.next()) {
	        int pid = rs.getInt("pid");
	        int sales = rs.getInt("sales");
	        ps.setInt(1, pid);
	        ps.setInt(2, sales);
	        ps.executeUpdate();
	      }
      }

      rs = sql.executeQuery("SELECT * FROM bestsellers WHERE sales < 10000");
      if (rs != null) {
      	return false;
      }
      return true;
    } catch (SQLException e) {
      return false;
    } finally {
      try {
        rs.close();
        ps.close();
        sql.close();
      } catch (Exception e) {
      }
    }
  }
}
