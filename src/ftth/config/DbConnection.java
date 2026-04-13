package ftth.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/testdb";
    private static final String USER = "root";
    private static final String PASSWORD = "Aaha@6598";

    private DbConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
