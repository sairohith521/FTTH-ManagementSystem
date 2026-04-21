package ftth.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbConnection {

    private static final String URL =
        "jdbc:mysql://mysql-2e05bfbd-jaashish925-af58.e.aivencloud.com:28677/testdb"
      + "?sslMode=REQUIRED";

    private static final String USER = "avnadmin";
    private static final String PASSWORD = "AVNS_5yuCw7BefKFVh3jZMBq";

    private DbConnection() {}

    public static Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection(URL, USER, PASSWORD);

        // ✅ LOG CONFIRMATION
     //   System.out.println("[DB] ✅ Connected to MySQL at " + con.getMetaData().getURL());

        return con;
    }
}