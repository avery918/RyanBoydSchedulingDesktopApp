package dbUtils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    /** These Strings form the name need to make the connection to the database*/
    private static final String protocol= "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//wgudb.ucertify.com:3306/";
    private static final String dbName = "WJ07su1";

    //JDBC URL
    private static final String jdbcURL = protocol + vendorName + ipAddress + dbName + "?connectionTmeZone=SERVER";

    /** driver and connection  */
    private static final String mysqJDBCDriver = "com.mysql.cj.jdbc.Driver";
    private static Connection conn = null;

    /** UserName */
    private static final String userName ="U07su1";
    /** Password */
    private static final String password = "53689121985";


    /** Method used to start the connection to the database.
     * @return  Returns connection to database */
    public static Connection startConnection(){
        try{
            Class.forName(mysqJDBCDriver);
            conn = DriverManager.getConnection(jdbcURL, userName, password);
            System.out.println("Connection was successful!");
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return conn;
    }

    /** Method used to close the connection to the database.
     */
    public static void closeConnection(){
       try{
           conn.close();
           System.out.println("Connection Close!");
       }catch (SQLException e){
       }
    }


    /** Method used to get the connection to the database.
     * @return  Returns connection to database */
    public static Connection getConnection(){
        return conn;
    }





}
