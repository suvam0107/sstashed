package com.sbsr.sstashed;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

// In your main application class or a test class
@SpringBootApplication
public class SstashedApplication {

    @Autowired
    private final DataSource dataSource;

    public SstashedApplication(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    static void main(String[] args) {
        SpringApplication.run(SstashedApplication.class, args);
    }

    @PostConstruct
    public void testDatabaseConnection() {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("✅ Database connected successfully!");
            System.out.println("Database: " + conn.getCatalog());
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
        }
    }
}
