
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
            
            // Load JDBC driver
            Class.forName("org.postgresql.Driver");
            
        } catch (ClassNotFoundException e) {
            
            //System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
            e.printStackTrace();
            return false;
        }
        
        try {
            
            connection = DriverManager.getConnection(URL,username,password);
            sql = connection.createStatement();
        } catch (SQLException e) {
            
            //System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return false;
            
        }
        
        if (connection != null) {
            //System.out.println("You made it, take control of your database now!");
            return true;
        } else {
            //System.out.println("Failed to make connection!");
            return false;
        }
    }
    
    //Closes the connection. Returns true if closure was sucessful
    public boolean disconnectDB(){
        try
        {
            if (rs!= null)
                rs.close();
            if (ps != null)
                ps.close();
            if (connection != null)
                connection.close();
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("Can not close driver");
            return false;
        }
    }
    
    
    
    public boolean insertStock(int pid, int wid, double qty) {
        try{
            //check if id exist in the product table
            String mysql = "SELECT * FROM product WHERE pid = " + pid + ";";
            ResultSet p_rs = sql.executeQuery(mysql);
            if (p_rs.next()){
                // if exist check if wid exist in warehouse
                mysql = "SELECT * FROM warehouse WHERE wid = " + wid + ";";
                ResultSet w_rs = sql.executeQuery(mysql);
                if(w_rs.next()){
                    //if wid exist
                    // check if this stock is already exist
                    mysql = "SELECT * FROM stock WHERE pid = " + pid + "AND wid = " + wid + ";";
                    rs = sql.executeQuery(mysql);
                    if (!rs.next()){
                        // if stock with pid, wid does not exist, can insert
                        if(qty>=0){
                            // pass all the test, insert stock
                            String insertsql = "INSERT INTO stock VALUES (" + pid + ", " + wid + " , " +
                            qty + ");";
                            sql.executeUpdate(insertsql);
                            
                            // finish inserting , close all rs
                            p_rs.close();
                            w_rs.close();
                            rs.close();
                            return true;
                            
                        }else{
                            // negative qty
                            rs.close();
                            p_rs.close();
                            w_rs.close();
                            System.out.println("qty needs to be greater equal than 0");
                            return false;
                        }
                    }else{
                        //if stock already exist, cannot insert
                        rs.close();
                        p_rs.close();
                        w_rs.close();
                        System.out.println("Stock already existed, cannot insert!!");
                        return false;
                    }
                    
                }else{
                    // if stock wid does not exist in warehouse
                    p_rs.close();
                    w_rs.close();
                    System.out.println("stock wid is needed to exist in warehouse wid");
                    return false;
                    
                }
                
            }else{
                // if stock pid does not exist in product
                p_rs.close();
                System.out.println("stock id is needed to exist in product id");
                return false;
            }
        }
        catch(SQLException e){
            // TODO Auto-generated catch block
            System.out.println("Can not insert into stock table!");
            return false;
        }
    }
    
    
    public boolean updateStock(int pid, int wid, double qty) {
        
        try{
            //check if pid and wid exist in stock
            String mysql = "SELECT * FROM stock WHERE pid = " + pid + " AND wid = "
            + wid + ";";
            ResultSet stock_rs = sql.executeQuery(mysql);
            if(stock_rs.next()){
                // if exist then we are able to update
                // get the current qty from product table
                int curr_qty = stock_rs.getInt("quantity");
                if (curr_qty >= Math.abs(qty)){
                    double update_qty = curr_qty + qty;
                    // have enough quan to ship
                    String updatesql = "UPDATE stock SET quantity =" + update_qty + " WHERE pid = " +
                    pid + " AND wid = " + wid +";";
                    sql.executeUpdate(updatesql);
                    
                    // finish
                    stock_rs.close();
                    return true;
                    
                }else{
                    // if curr_qty < abs(qty)
                    stock_rs.close();
                    System.out.println("Does not have enough product in stock!");
                    return false;
                }
            }else{
                // if cant find the stock with pid, wid
                stock_rs.close();
                System.out.println("Somehow cannot find the stock!");
                return false;
            }
        }
        catch(SQLException e){
            // TODO Auto-generated catch block
            System.out.println("Can not update stock table!");
            return false;
        }
        
    }
    
    
    
    public int insertOrder(int cid, int pid, int wid, double qty, double price){
        
        try{
            //check if cid comes from customer
            String mysql = "SELECT * FROM customer WHERE cid = " + cid + ";";
            ResultSet c_rs = sql.executeQuery(mysql);
            
            if(c_rs.next()){
                //so cid exist in customer table
                //check pid
                mysql = "SELECT * FROM product WHERE pid = " + pid + ";";
                ResultSet p_rs = sql.executeQuery(mysql);
                
                if(p_rs.next()){
                    // so pid exist in product table
                    // check wid
                    mysql = "SELECT * FROM warehouse WHERE wid = " + wid +";";
                    ResultSet w_rs = sql.executeQuery(mysql);
                    if(w_rs.next()){
                        // so wid exist in warehouse table
                        // pass all condition
                        
                        //get time
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                        LocalDate localDate = LocalDate.now();
                        String odate = dtf.format(localDate);
                        
                        // status = 'O'
                        char status = 'O';
                        
                        //get oid get the last oid from the table then add one to it
                        mysql = "SELECT oid FROM orders WHERE oid >= all(select oid FROM orders);";
                        ResultSet find_rs = sql.executeQuery(mysql);
                        int oid = find_rs.getInt("oid") + 1;
                        
                        String insertsql = "INSERT INTO orders VALUES (" + oid +
                        "," + cid + "," + pid + "," + wid +
                        "," + odate + ","  + qty +
                        "," + price + "," + status + ");";
                        
                        sql.executeUpdate(insertsql);
                        
                        //finish
                        c_rs.close();p_rs.close();w_rs.close();find_rs.close();return oid;
                    }else{// wid does not come from warehouse
                        c_rs.close();p_rs.close();w_rs.close();return -1;}
                    
                }else{// pid does not come from product
                    c_rs.close();p_rs.close();return -1;}
            }else{// cid does not come from customer
                c_rs.close();return -1;}
        }
        catch(SQLException e){
            // TODO Auto-generated catch block
            System.out.println("Unable to insert Order");
            return -1;
        }
    }
    
    public boolean cancelOrder(int oid) {
        try{
            String mysql = "SELECT * FROM orders WHERE oid = " + oid +";";
            rs = sql.executeQuery(mysql);
            if (rs.next()) {
                String status = rs.getString("status") ;
                char new_status = status.charAt(0);
                if (new_status == 'O'){
                    // able to to cancel
                    String deletesql = "DELETE FROM orders WHERE oid = " + oid +";";
                    sql.executeUpdate(deletesql);
                    
                    rs.close();
                    return true;
                    
                }else{
                    rs.close();
                    System.out.println("'S' status can not be cancelled!");
                    return false;}
            }
            else{
                rs.close();
                System.out.println("Can not find this oid from orders table!");
                return false;}
        }
        
        catch(SQLException e){
            System.out.println("Unable to cancel Order");
            return false;
        }
    }
    
    public boolean shipOrder(int oid){
        try{
            String mysql = "SELECT * FROM orders WHERE oid = " + oid + ";";
            ResultSet o_rs = sql.executeQuery(mysql);
            if(o_rs.next()){
                // this oid exist
                // locate the wid
                int target_wid = o_rs.getInt("shipwid");
                // locate the pid
                int target_pid = o_rs.getInt("pid");
                // save need how many quan for this product
                double needed_quan = o_rs.getInt("quantity");
                
                // now get the info for stock with oid and wid
                mysql = "SELECT * FROM stock WHERE pid = " + target_pid + "AND wid = " + target_wid +";" ;
                ResultSet stock_rs = sql.executeQuery(mysql);
                if (stock_rs.next()){
                    // located this stock
                    // check if stock can fullfill the product
                    double stockquan = stock_rs.getInt("quantity");
                    if (stockquan >= needed_quan){
                        // able to ship, update stock first
                        updateStock(target_pid, target_wid, - needed_quan);
                        
                        //now update orders
                        String updatesql = "UPDATE orders SET status = 'S' WHERE oid = " + oid +";";
                        sql.executeUpdate(updatesql);
                        
                        //finish
                        o_rs.close();
                        stock_rs.close();
                        return true;
                    }else{
                        // if does not have enough product in stock
                        o_rs.close();
                        stock_rs.close();
                        System.out.println("Does not have enough product in stock!");
                        return false;}
                }else{
                    // if can not find this stock raise false
                    o_rs.close();
                    stock_rs.close();
                    System.out.println("Somehow can not find the stock");
                    return false;}
            }else{
                // can not find the orders
                o_rs.close();
                System.out.println("Somehow can not find the orders");
                return false;}
        }
        
        
        catch(SQLException e){
            System.out.println("Unable to cancel Order");
            return false;
        }
        
    }
    
    public String listStock(int pid){
        try{
            // find the stock table with pid
            String mysql = "SELECT wid,cast( quantity as decimal(10,2)) FROM stock WHERE pid = "
            + pid + "AND quantity > 0"
            + "ORDER BY quantity DESC;";
            rs = sql.executeQuery(mysql);
            String answer = "";
            if (rs != null){
                while (rs.next()){
                    answer += Integer.toString(rs.getInt("wid")) + ":" + Integer.toString(rs.getInt("quantity")) + "#";
                }
                // get over the last #
                String new_answer = answer.substring(0,answer.length() - 2);
                return new_answer;
            }else{// cant find
                return "";}
        }
        catch(SQLException e){
            System.out.println("Unable to list stock");
            return "";
        }
        
    }
    
    public String listReferrals(int cid){
        try{
            //check cid
            String mysql = "SELECT cid,name FROM customer WHERE cid = "
            + cid + "ORDER BY name;";
            rs = sql.executeQuery(mysql);
            String answer = "";
            if (rs != null){
                while(rs.next()){
                    answer += Integer.toString(rs.getInt("cid")) + ":" + rs.getString("name") + "#";
                }
                // get over the last #
                String new_answer = answer.substring(0,answer.length() - 2);
                return new_answer;
            }else{
                //cant find
                return "";
            }
        }
        catch(SQLException e){
            System.out.println("Unable to list refferral");
            return "";
        }
    }
    
    public boolean updateDB(){
        try {
            // create table
            String createsql = "CREATE TABLE bestsellers("
            + "pid  INTEGER "
            + "sales NUMERIC(10,2));";
            sql.executeUpdate(createsql);
            
            //get table needed info
            String mysql = "select p.pid, sum(o.quantity * o.price) from product p,orders o  where status"
            + " = 'S' AND o.pid = p.pid GROUP BY p.pid;";
            rs = sql.executeQuery(mysql);
            if (rs != null) {
                String insertsql = "";
                while (rs.next()) {
                    // rs has thing
                    int pid = rs.getInt("pid");
                    double sales = rs.getDouble("sum");
                    insertsql = "INSERT INTO stock VALUES (" + pid +"," + sales +");";
                    sql.executeUpdate(insertsql);
                }
                return true;
            }else {
                // unable to find table info
                return false;
            }
            
        }catch(SQLException e){
            System.out.println("Unable to run updateDB");
            return false;
        }
        
        
    }
    
    
    
}
