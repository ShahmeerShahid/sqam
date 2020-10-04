import java.sql.*;

public class Assignment2 {

    // A connection to the database  
    Connection connection;

    // Statement to run queries
    Statement sql, spsql;

    // Prepared Statement
    PreparedStatement ps, bsps;

    // Resultset for the query
    ResultSet rs;

    //CONSTRUCTOR
    Assignment2(){ 
    }

    //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
    public boolean connectDB(String URL, String username, String password){
        try{
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, username, password);

            String searchPath = "SET search_path TO A2";
            spsql = connection.createStatement();
            spsql.execute(searchPath);
            return true;
        }
        catch(SQLException se){
            return false;
        }
        catch(ClassNotFoundException e){
            return false;
        }
    }

    //Closes the connection. Returns true if closure was sucessful
    public boolean disconnectDB(){
        try{
            spsql.close();
            connection.close();
            return true;
        }
        catch(SQLException se){
            return false; 
        }
    }

    public boolean insertStock(int pid, int wid, double qty) {
        
        try{
            //Quantity cannot be negative
            if(qty < 0){
                return false;
            }
            
            //Checks if pID in product table is not found
            String pidMatch = "SELECT pid FROM product WHERE pid = ?";
            ps = connection.prepareStatement(pidMatch);
            ps.setInt(1, pid);
            rs = ps.executeQuery();
            
            if(!rs.isBeforeFirst()){
            	rs.close();
                ps.close();
                return false;
            }
            rs.close();
            ps.close();

            //Checks if wid in warehouse table is not found
            String widMatch = "SELECT wid FROM warehouse WHERE wid = ?";
            ps = connection.prepareStatement(widMatch);
            ps.setInt(1, wid);
            rs = ps.executeQuery();
            
            if(!rs.isBeforeFirst()){
            	rs.close();
                ps.close();
                return false;
            }
            rs.close();
            ps.close();

            //Checks if pid, wid is in stock table
            String pidWidMatch = "SELECT pid, wid FROM stock " +
                "WHERE pid = ? AND wid = ?";
            ps = connection.prepareStatement(pidWidMatch);
            ps.setInt(1, pid);
            ps.setInt(2, wid);
            rs = ps.executeQuery();
            
            if(rs.isBeforeFirst()){
            	rs.close();
                ps.close();
                return false;
            }
            rs.close();
            ps.close();

            //Insert given values to stock table
            String queryString = "INSERT INTO stock(pid,wid,quantity) " +
                "VALUES(?,?,?)";
            ps = connection.prepareStatement(queryString);
            ps.setInt(1, pid);
            ps.setInt(2, wid);
            ps.setDouble(3, qty);
            ps.executeUpdate();
            ps.close();
            return true;
        }
        catch(SQLException se){
            return false;
        }
    }

    public boolean updateStock(int pid, int wid, double qty) {
        try{
            //Checks if pid, wid is not in stock table
            String pidWidMatch = "SELECT pid, wid, quantity " +
                "FROM stock WHERE pid = ? AND wid = ?";
            ps = connection.prepareStatement(pidWidMatch);
            ps.setInt(1, pid);
            ps.setInt(2, wid);
            rs = ps.executeQuery();
            
            if(!rs.next()){
            	rs.close();
                ps.close();
                return false;
            }

            //Cannot have a negative quantity
            double queryQuantity = rs.getDouble("quantity");
            double newQuantity = queryQuantity + qty;
            if((newQuantity) < 0){
            	rs.close();
                return false;
            }
            
            rs.close();
            ps.close();
            //Update the quantity and set the changes
            String updateStock = "UPDATE stock SET quantity = ? " +
                "WHERE pid = ? AND wid = ?";
            ps = connection.prepareStatement(updateStock);
            ps.setDouble(1, newQuantity);
            ps.setInt(2, pid);
            ps.setInt(3, wid);
            ps.executeUpdate();
            ps.close();
            return true;
        }
        catch(SQLException se){
            return false;
        }
    }

    public int insertOrder(int cid, int pid, int wid, double qty, double price){
        try{
            //Checks if cid is in customer table
            String cidMatch = "SELECT cid FROM customer WHERE cid = ?";
            ps = connection.prepareStatement(cidMatch);
            ps.setInt(1, cid);
            rs = ps.executeQuery();
            
            if(!rs.isBeforeFirst()){
            	rs.close();
                ps.close();
                return -1;
            }
            rs.close();
            ps.close();

            //Checks if wid is in warehouse table
            String widMatch = "SELECT wid FROM warehouse WHERE wid = ?";
            ps = connection.prepareStatement(widMatch);
            ps.setInt(1, wid);
            rs = ps.executeQuery();
        
            if(!rs.next()){
            	rs.close();
                ps.close();
                return -1;
            }
            rs.close();
            ps.close();
            
            //Checks if pID in product table is not found
            String pidMatch = "SELECT pid FROM product WHERE pid = ?";
            ps = connection.prepareStatement(pidMatch);
            ps.setInt(1, pid);
            rs = ps.executeQuery();
            
            if(!rs.next()){
            	rs.close();
                ps.close();
                return -1;
            }
            rs.close();
            ps.close();

            //Get the new order ID of the new order
            String newOid = "SELECT nextval(pg_get_serial_sequence('orders','oid'))";
            sql = connection.createStatement();
            rs = sql.executeQuery(newOid);
            rs.next();

            int oid = rs.getInt(1);
            String orderStatus = "O";
            rs.close();
            sql.close();

            java.util.Date currentDate = new java.util.Date();
            java.sql.Date ldt = new java.sql.Date(currentDate.getTime());

            //Update the query and set the changes
            String queryString = "INSERT " +
                "INTO orders(oid,cid,pid,odate,shipwid,quantity,price,status) " +
                "VALUES(?,?,?,?,?,?,?,?)";
            ps = connection.prepareStatement(queryString);
            ps.setInt(1, oid);
            ps.setInt(2, cid);
            ps.setInt(3, pid);
            ps.setDate(4, ldt);
            ps.setInt(5, wid);
            ps.setDouble(6, qty);
            ps.setDouble(7, price);
            ps.setString(8, orderStatus);
            ps.executeUpdate();
            ps.close();
            return oid;
        }
        catch(SQLException se){
            return -1;
        }
    }

    public boolean cancelOrder(int oid) {
        try{
            //Check if order status is "shipped"
            String orderStatus = "SELECT status FROM orders WHERE oid = ?";
            ps = connection.prepareStatement(orderStatus);
            ps.setInt(1, oid);
            rs = ps.executeQuery();
            rs.next();
            String status = rs.getString("status");
            rs.close();
            ps.close();
            if(status.equals("S")){
                return false;
            }
    
            //Delete the query and set the changes
            String removeOrder = "DELETE FROM orders WHERE oid = ?";
            ps = connection.prepareStatement(removeOrder);
            ps.setInt(1, oid);
            ps.executeUpdate();
            ps.close();
            return true;
        }
        catch(SQLException se){
            return false;
        }
    }

    public boolean shipOrder(int oid){
        try{
            //Check if order status is "shipped"
            String shipped = "S";
            String orderStatus = "SELECT * FROM orders WHERE oid = ?";
            ps = connection.prepareStatement(orderStatus);
            ps.setInt(1, oid);
            rs = ps.executeQuery();

            rs.next();
            String status = rs.getString("status");
            int pid = rs.getInt("pid");
            int wid = rs.getInt("shipwid");
            double orderQty = rs.getDouble("quantity");
            rs.close();
            ps.close();
            if(status.equals(shipped)){
                return false;
            }

            //Get order and stock quantities
            String quantities = "SELECT stock.quantity " +
                "FROM orders JOIN stock ON shipwid = wid " +
                "WHERE oid = ?";
            ps = connection.prepareStatement(quantities);
            ps.setInt(1, oid);
            rs = ps.executeQuery();

            rs.next();
            double stockQty = rs.getDouble("quantity");
            rs.close();
            ps.close();

            //Check if it's not possible to complete the order with the current stock
            if(orderQty > stockQty){
                return false;
            }

            //Ship the order
            String updateOrder = "UPDATE orders SET status = ? WHERE oid = ?";
            ps = connection.prepareStatement(updateOrder);
            ps.setString(1, shipped);
            ps.setInt(2, oid);
            ps.executeUpdate();
            ps.close();

            //Update stock
            return this.updateStock(pid, wid, orderQty);

        }
        catch(SQLException se){
            return false; 
        }   
    }

    public String listStock(int pid){
        try{
            //Check if no product in the warehouse is in stock
            String warehouseStock = "SELECT wid, quantity " + 
                "FROM warehouse NATURAL JOIN stock NATURAL JOIN product " +
                "WHERE quantity > ? AND pid = ?";
            ps = connection.prepareStatement(warehouseStock);
            ps.setInt(1, 0);
            ps.setInt(2, pid);
            rs = ps.executeQuery();
            
            if(!rs.isBeforeFirst()){
            	rs.close();
                return "";
            }

            String warehouseStocks = "";
            String wid;
            String quantity;

            //loop through the table and record the wid, quantity
            while(rs.next()){
                wid = String.valueOf(rs.getInt("wid"));
                quantity = String.valueOf(rs.getDouble("quantity"));
                warehouseStocks += wid + ":" + quantity;
                //Only add a separator "#" if not in the last row/tuple
                if(!rs.isLast()){
                    warehouseStocks += "#";
                }
            }
            rs.close();
            ps.close();
            return warehouseStocks;

        }
        catch(SQLException se){
            return "";
        }
    }

    public String listReferrals(int cid){
        try{
            //Check if there are any customers referred to by customer cid
            String custReferred = "SELECT cid, cname " + 
                "FROM customer JOIN referral ON custref = cid " + 
                "WHERE custid = ?";
            ps = connection.prepareStatement(custReferred);
            ps.setInt(1, cid);
            rs = ps.executeQuery();
            
            if(!rs.isBeforeFirst()){
            	rs.close();
                return "";
            }

            String referredList = "";
            String referredId;
            String referredName;

            //loop through the table and record the referred customer
            while(rs.next()){
                referredId = String.valueOf(rs.getInt("cid"));
                referredName = rs.getString("cname");
                referredList += referredId + ":" + referredName;

                //Only add a separator "#" if not in the last row/tuple
                if(!rs.isLast()){
                    referredList += "#";
                }
            }
            rs.close();
            ps.close();
            return referredList;
        }
        catch(SQLException se){
            return "";
        }
    }

    public boolean updateDB(){
        try{
            //Get the products with sales greater than 10000
        	String newTable = "CREATE TABLE IF NOT EXISTS bestsellers " +
        		"(pid INTEGER PRIMARY KEY , sales NUMERIC(10,2))";
        	sql = connection.createStatement();
        	sql.executeUpdate(newTable);
            sql.close();

            String productMatch = "SELECT pid, SUM(price) " +
                "FROM orders WHERE status = 'S' " +
                "GROUP BY pid " +
                "HAVING SUM(price) > ?";
            ps = connection.prepareStatement(productMatch);
            ps.setDouble(1, 10000);
            rs = ps.executeQuery();

            int pid;
            double sales;
            String searchBS;
            String updateQuery;
            ResultSet bestsellersRs;
            //loop through the table and update when needed
            while(rs.next()){
                pid = rs.getInt(1);
                sales = rs.getDouble(2);

                //Checks if product already exists in bestsellers
                searchBS = "SELECT pid FROM bestsellers WHERE pid = ?";
                bsps = connection.prepareStatement(searchBS);
                bsps.setInt(1, pid);
                bestsellersRs = bsps.executeQuery();

                //Update - pid is in best sellers, just change sales amount
                if(bestsellersRs.next()){
                    updateQuery = "UPDATE bestsellers SET sales = ? WHERE pid = ?";
                    ps = connection.prepareStatement(updateQuery);
                    ps.setDouble(1, sales);
                    ps.setInt(2, pid);
                    ps.executeUpdate();
                    ps.close();
                }
                //Insert - pid is not in bestsales. Include a new row for the product
                else{
                    updateQuery = "INSERT INTO bestsellers(pid,sales) VALUES(?,?)";
                    ps = connection.prepareStatement(updateQuery);
                    ps.setInt(1, pid);
                    ps.setDouble(2, sales);
                    ps.executeUpdate();
                    ps.close();
                }
                bestsellersRs.close();
                bsps.close();
            }
            rs.close();
            sql.close();
            return true;
        }
        catch(SQLException se){
            return false;
        }      
    }
}
