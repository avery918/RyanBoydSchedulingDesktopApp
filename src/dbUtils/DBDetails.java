//package dbUtils;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//public class DBDetails {
//
//    private static PreparedStatement statement;
//
//    /** This method creates a statement object. */
//    public static void setPreparedStatement(Connection conn, String sqlStatement) throws SQLException {
//        statement = conn.prepareStatement(sqlStatement);
//    }
//
//    /**This method returns a statement object. */
//    public static PreparedStatement getPreparedStatement(){
//        return statement;
//    }
//}
