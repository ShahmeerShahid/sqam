import java.sql.*;

public class Assignment2 
{
    
  // A connection to the database  
  Connection connection;
  
  // Statement to run queries
  Statement sql;
  
  // Prepared Statement
  PreparedStatement ps;
  
  // Resultset for the query
  ResultSet rs;
  
  //CONSTRUCTOR
  Assignment2()
  {
	try
	{
		Class.forName("org.postgresql.Driver");
	}
	catch (ClassNotFoundException e) 
      	{
    		System.out.println("Failed to find the JDBC driver");
      	}
  }
  
  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password){
    try 
    {
    connection = DriverManager.getConnection(URL, username, password);
    ps = connection.prepareStatement("set search_path to a2");
    ps.execute();
    ps.close();    
    return true;
      

    }
      catch(SQLException e)
      {
        System.out.println("Could not connect to DB. " + e);
        return false;
      }  
  }
  
  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB()
  {
      try
      {
        if(rs != null)
        {
          rs.close();
        }

        if(ps != null)
        {
          ps.close();
        }

        if(connection != null)
        {
          connection.close();
        }

        return true;

      }catch(SQLException e)
      {
        System.out.println("Could not disconnct from DB. " + e);
        return false;
      }    
  }
    
  public boolean insertStock(int id, int wid, double qty) throws SQLException
  {
    if(qty < 0)
	 {
      System.out.println("Error: Negative quantity.");
      return false;
	 }
   //The search_path to use
   String search_path = "SET search_path TO a2;";

   // Check if the pid is in product. Should return 1 row
   String check_in_product = "SELECT COUNT(pid) FROM product WHERE pid = " + id + ";"; 

   //Check if wid in warehouse. Shou;d return 1 row
   String check_in_warehouse = "SELECT COUNT(wid) FROM warehouse WHERE wid = " + wid + ";";

   //Check if (pid,wid) not in stock, should return 0 rows.
   String check_in_stock = "SELECT COUNT(*) FROM stock WHERE pid = "+id+" AND wid = "+wid+";";
	
   // If any of these conditions fail, then the stock cannot be added and return false. 

   //Statement to insert the stock.
   String insert_stock = "INSERT INTO stock (pid, wid, quantity) VALUES (" + id + ", " + wid + ", " + qty + ")";
   // READS: "INSERT INTO stock (pid, wid, qty) VALUES (id, wid, qty);"

   // TODO: Execute statements

   try
   {
    ps = connection.prepareStatement(check_in_product);
    //ps.execute(search_path);


    rs = ps.executeQuery();
    rs.next();
    if(rs.getInt(1) != 1) // if it's not in product.
    {
      throw new SQLException("Couldn't add stock.");
    }
    else // is in stock.
    {
      rs.close();
      ps.close();
      ps = connection.prepareStatement(check_in_warehouse);
      rs = ps.executeQuery();
      rs.next();
      if(rs.getInt(1) != 1) // not in warehouse.
        throw new SQLException("Warehouse doesn't exist.");
      else // is in warehouse.
      {
        rs.close();
        ps.close();
        ps = connection.prepareStatement(check_in_stock);
        rs = ps.executeQuery();
        rs.next();
        if(rs.getInt(1) != 0) //Already in stock.
          throw new SQLException("Couldn't add to stock. Already exists.");
        else // Not in stock.
        {
          rs.close();
          ps.close();
          ps = connection.prepareStatement(insert_stock);
          //Good to insert the new stock.
          ps.executeUpdate();

        }
      }
    }
    
   }
   catch(SQLException e)
   {
    System.out.println("Couldn't insert stock." + e.getMessage());
    return false;
   }
   finally
   {
    if(rs != null)
      rs.close();
    if(ps != null)
      ps.close();
    return true;
   }


  }
  
   public boolean updateStock(int id, int wid, double qty) throws SQLException
   {

    String search_path = "SET search_path TO a2;";

    String check_qty_valid = "SELECT COUNT(*) FROM stock WHERE pid = " + id + " AND wid = " + wid + " AND quantity >= " + Math.abs(qty) + ";";
    //READS: SELECT COUNT(*) FROM stock WHERE pid = id AND wid = wid AND qty >= abs(qty);
    //Should return 1 row if valid.

    String update_stock = "UPDATE stock SET quantity = quantity + " + qty + " WHERE pid = " + id + " AND wid = " + wid + ";";
    //READS: UPDATE stock SET quantity = qty WHERE pid = id AND wid = wid;

    try
    {
      if(qty < 0)
      {
          ps = connection.prepareStatement(check_qty_valid);
        //ps.execute(search_path);
        rs = ps.executeQuery();

        rs.next();
        if(rs.getInt(1) == 1)
        {
          if (rs != null)
            rs.close();
          if(ps != null)
            ps.close();
          ps = connection.prepareStatement(update_stock);
          ps.executeUpdate();

          ps.close();
          return true;
        }
        else
        {
          if (rs != null)
            rs.close();
          if(ps != null)
            ps.close();
          System.out.println("Couldn't udpate stock. b/c DNE");
          return false;
        }
      }
      else
      {
        if (rs != null)
          rs.close();
        if(ps != null)
          ps.close();

        ps = connection.prepareStatement(update_stock);
          if(ps.executeUpdate() != 1)
            throw new SQLException("Could not update.");
          

      }
      
    }
    catch(SQLException e)
    {
      System.out.println("SQL Exception raised. " + e.getMessage() + e);
      return false;
    }
    finally
    {
      if(rs != null)
        rs.close();
      if(ps != null)
        ps.close();
      return true;
    }
  }
   
  public int insertOrder(int cid, int pid, int wid, double qty, double price) throws SQLException
  {
    

    //Date
      java.util.Date date = new java.util.Date();;
      java.sql.Date d = new java.sql.Date(date.getTime());
    //STATEMENTS
    String search_path = "SET search_path TO a2;";
    String check_in_customer = "SELECT COUNT(cid) FROM customer WHERE cid = " + cid + ";";
    String check_in_product = "SELECT COUNT(pid) FROM product WHERE pid = " + pid + ";"; 
    String check_in_warehouse = "SELECT COUNT(wid) FROM warehouse WHERE wid = " + wid + ";";
    String insert_order = "INSERT INTO orders (oid,cid,pid,odate,shipwid,quantity,price,status) "+
                          "VALUES (NEXTVAL('orders_oid_seq'), " + cid + ", " + pid + ", to_date('" +d.toString() + "', 'YYYY-MM-DD'), " + wid + ", " + qty + ", " + price + ", 'O');";
    
    String get_oid = "SELECT currval(pg_get_serial_sequence('orders','oid'));";
    int oid = -1;
    try
    {
      if(qty < 0)
        throw new SQLException("Not a valid quantity");


      ps = connection.prepareStatement(check_in_customer);
      //ps.execute(search_path);
      rs = ps.executeQuery();
      rs.next();
      if(rs.getInt(1) != 1)
        throw new SQLException("Not in customer.");
      
      rs.close();
      ps.close();
      ps = connection.prepareStatement(check_in_product);
      rs = ps.executeQuery();
      rs.next();
      if(rs.getInt(1) != 1)
        throw new SQLException("Not in product.");
      
      rs.close();
      ps.close();
      ps = connection.prepareStatement(check_in_warehouse);
      rs = ps.executeQuery();
      rs.next();
      if(rs.getInt(1) != 1)
        throw new SQLException("Not in warehouse");
      rs.close();
      ps.close();
      

      ps = connection.prepareStatement(insert_order);

      //Insert the order
      ps.executeUpdate();
      //Get the oid
      ps.close();
      ps = connection.prepareStatement(get_oid);
      rs = ps.executeQuery();
      rs.next();
      oid = rs.getInt(1);
      
    }
    catch(SQLException e)
    {
      System.out.println("Whoops!" + e.getMessage() + e);
      return -1;
    }
    finally
    {
    if(rs != null)
      rs.close();
    if(ps != null)
      ps.close();
    return oid;
    }
  }

  public boolean cancelOrder(int oid) throws SQLException
  {
    String search_path = "set search_path to a2;";
    String check_order_condition = "SELECT COUNT(*) FROM orders WHERE oid = " + oid + "AND status = 'O';";
    String cancel_order = "DELETE FROM orders WHERE oid = " + oid + ";";

    try
    {
      ps = connection.prepareStatement(check_order_condition);
      //ps.execute(search_path);

      rs = ps.executeQuery();
      rs.next();
      if(rs.getInt(1) != 1)
      {
        rs.close();
        ps.close();
        throw new SQLException("Order does not exist, or order has already been shipped.");
      }

      else
      {
        rs.close();
        ps.close();
        ps = connection.prepareStatement(cancel_order);
        if(ps.executeUpdate() != 1)
          throw new SQLException("DIDNT DELETE!");
      }

      
    }
    catch(SQLException e)
    {
      System.out.println("Could not cancel order." + e.getMessage());
      return false;
    }
    finally
    {
      if (rs != null)
        rs.close();
      if(ps != null)
        ps.close();
      return true;
    }
  }

  public boolean shipOrder(int oid) throws SQLException
  {
    String search_path = "set search_path to a2;";
    String statement = "SELECT COUNT(*) FROM orders o JOIN stock s " + 
    " ON o.pid = s.pid AND o.shipwid = s.wid " + 
    " WHERE o.oid = " + oid +  
    "AND o.quantity <= s.quantity AND o.status = 'O';";

    String ship_order = "UPDATE orders SET status = 'S' WHERE oid = "+oid+";";
    String get_stock_info = "SELECT pid,shipwid,quantity FROM orders WHERE oid = " + oid +";";
    
    try
    {
      ps = connection.prepareStatement(statement);
      //ps.execute(search_path);
      rs = ps.executeQuery();
      rs.next();
      if(rs.getInt(1) != 1)
        throw new SQLException("Whoops!");
      
      else
      {
        rs.close();
        ps.close();
        ps = connection.prepareStatement(get_stock_info);
        rs = ps.executeQuery();
        rs.next();
        updateStock(rs.getInt("pid"), rs.getInt("shipwid"), -(rs.getInt("quantity")));
        ps.close();
        ps = connection.prepareStatement(ship_order);
        ps.executeUpdate();

        
      }
    } 
    catch(SQLException e)
    {
      System.out.println("Something went wrong.");
      return false;

    }
    finally
    {
      if (rs != null)
        rs.close();
      if(ps != null)
        ps.close();
      return true;
    }


  }
  
  public String listStock(int pid) throws SQLException
  {
    String select_statement = "SELECT * FROM stock WHERE pid = " + pid + 
                                " AND quantity > 0 ORDER BY quantity DESC;";
    String search_path = "set search_path to a2;";
    String result = "";

    try
    {
      ps = connection.prepareStatement(select_statement);
      //ps.execute(search_path);

      //Get all the data
      rs = ps.executeQuery();
      
      while(rs.next())
      {
        String new_wid = Integer.toString(rs.getInt("wid"));
        String new_amt = Integer.toString(Math.round(rs.getInt("quantity")*100)/100);
        result += (new_wid+ ":" + new_amt +"#");
      }

    }
    catch(SQLException e)
    {
      System.out.println("Could not print. ");
      return "";

    }
    finally
    {
      if(rs != null)
        rs.close();
      if(ps != null)
        ps.close();

      if(result.length() > 0)
        return result.substring(0,result.length()-1);
      return "";
    }
  }
  
  public String listReferrals(int cid) throws SQLException
  {
    String search_path = "set search_path to a2;";
    String list_refs = "SELECT cid, cname FROM customer ORDER BY name ASC;";
    String result = "";

    try
    {
      ps = connection.prepareStatement(list_refs);

      //ps.execute(search_path);
      rs = ps.executeQuery();

      while(rs.next())
      {
        result += (rs.getInt("cid") + ":" + rs.getNString("cname") + "#");
      }

    }
    catch(SQLException e)
    {
      System.out.println("Could not list referrals. ");
      return "";
    }
    finally
    {
      if(rs != null)
        rs.close();
      if(ps != null)
        ps.close();

      if(result.length() > 0)
        return result.substring(0,result.length()-1);

      return "";
    }
  }
    
  public boolean updateDB() throws SQLException
  {
    String search_path = "set search_path to a2;";
    String new_table = "CREATE TABLE bestsellers " +
                        "(pid INTEGER NOT NULL, " + 
                        "sales NUMERIC(10,2), " +
                        "PRIMARY KEY ( pid ));";
    String select_statement = "SELECT pid, SUM(quantity * price) AS salesamount" +
                              " FROM orders" +
                              " WHERE status = 'S'"+
			      " GROUP BY pid"+
			      " HAVING SUM(quantity*price)>10000.00;";

    try
    {
      String checkertbl = "DROP TABLE IF EXISTS bestsellers;";
      ps = connection.prepareStatement(checkertbl);
      ps.executeUpdate();
      ps.close();

      ps = connection.prepareStatement(new_table);
      //ps.execute(search_path);
      ps.execute();
      ps.close();
      ps = connection.prepareStatement(select_statement);
      rs = ps.executeQuery();
      //Used a temp ps so that rs doesn't close.
      PreparedStatement temp;
      while(rs.next())
      {
        int new_pid = rs.getInt("pid");
        double new_sales_amount = rs.getDouble("salesamount");
        temp = connection.prepareStatement("INSERT INTO bestsellers (pid, sales) VALUES ("+new_pid+", "+new_sales_amount+");");
        temp.executeUpdate();
        temp.close();
      }

      

    }
    catch(SQLException e)
    {
      System.out.println("Whoops! Something went wrong! "+ e);
	e.printStackTrace();
      return false;
    }
    finally
    {
      if(rs != null)
        rs.close();
      if(ps != null)
        ps.close();

      return true;
    }

  }
  
}










