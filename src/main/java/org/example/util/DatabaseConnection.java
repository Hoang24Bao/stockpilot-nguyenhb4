package org.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DatabaseConnection {
    // Kết nối H2 Database (Lưu ra file vật lý stockpilot_db.mv.db tại thư mục gốc)
    private static final String URL = "jdbc:h2:./stockpilot_db";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Đọc file schema và thực thi
            String sql = new String(Files.readAllBytes(Paths.get("src/main/resources/schema.sql")));
            stmt.execute(sql);
            System.out.println("The database has been initialized successfully!");

        } catch (Exception e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }
}