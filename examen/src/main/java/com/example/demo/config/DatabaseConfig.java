package com.example.demo.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

    @Component 
public class DatabaseConfig {
    
    private final String url = "jdbc:postgresql://localhost:5432/techadvisor_db";
    private final String user = "techadvisor_manager";
    private final String password = "123456";

    
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL introuvable.", e);
        }
        return DriverManager.getConnection(url, user, password);
    }
}
