package util;

import java.sql.Connection;
import java.sql.DriverManager;

import util.DBPropertyUtil;  // ✅ This line is important

public class DBConnUtil {

    public static Connection getConnection(String propFileName) {
        Connection conn = null;
        try {
            String connectionString = DBPropertyUtil.getPropertyString(propFileName);
            conn = DriverManager.getConnection(connectionString);
            System.out.println("✅ Connected to the database!");
        } catch (Exception e) {
            System.out.println("❌ Failed to connect to DB: " + e.getMessage());
        }
        return conn;
    }
}
