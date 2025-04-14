package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnUtil {

    public static Connection getConnection(String propFileName) {
        Connection conn = null;
        try {
            String connectionString = DBPropertyUtil.getPropertyString(propFileName);
            conn = DriverManager.getConnection(connectionString);
            System.out.println("Connected to the database successfully!");
        } catch (Exception e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
        return conn;
    }
}
