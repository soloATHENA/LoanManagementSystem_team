package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Database URL, adjust 'localhost' and '3306' if your MySQL server is running on a different host/port.
    private static final String DB_URL = "jdbc:mysql://localhost:3306/loan_management";
    
    // Default MySQL credentials (change these to match your local MySQL setup)
    private static final String USER = "root";
    private static final String PASS = "123456"; // Change to your MySQL password

    /**
     * Establishes and returns a connection to the database.
     * @return Connection object if successful, null otherwise.
     */
    public static Connection getConnection() {
        try {
            // Load the MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found! Ensure the connector .jar is in your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed! Check your credentials and ensure MySQL is running.");
            e.printStackTrace();
        }
        return null;
    }
}
