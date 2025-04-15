package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DBPropertyUtil {

    public static String getPropertyString(String fileName) throws IOException {
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(fileName);
        props.load(fis);

        String host = props.getProperty("host");
        String port = props.getProperty("port");
        String dbname = props.getProperty("dbname");
        String username = props.getProperty("username");
        String password = props.getProperty("password");

        return "jdbc:mysql://" + host + ":" + port + "/" + dbname +
               "?user=" + username + "&password=" + password +
               "&useSSL=false&allowPublicKeyRetrieval=true";
    }
}
