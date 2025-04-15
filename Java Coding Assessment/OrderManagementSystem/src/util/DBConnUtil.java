package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnUtil {

    public static Connection getConnection(String propertyFile) {
        Connection conn = null;
        try {
            Properties props = DBPropertyUtil.getProperties(propertyFile);
            String host = props.getProperty("host");
            String port = props.getProperty("port");
            String dbname = props.getProperty("dbname");
            String username = props.getProperty("username");
            String password = props.getProperty("password");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + dbname;

            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
