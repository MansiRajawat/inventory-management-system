package com.project.inventory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.*;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner testDatabaseConnection() {
		return args -> {
			System.out.println("Testing MySQL connection...");

			// Connection parameters
			String url = "jdbc:mysql://localhost:3306/springjdbc";
			String username = "root";
			String password = "M@nsi123*";

			try {
				// Load MySQL JDBC Driver
				Class.forName("com.mysql.cj.jdbc.Driver");

				// Open a connection
				System.out.println("Connecting to database...");
				Connection connection = DriverManager.getConnection(url, username, password);

				// If connection is successful
				System.out.println("Connection successful!");

				// Get schema information
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT DATABASE()");

				if (rs.next()) {
					System.out.println("Current schema: " + rs.getString(1));
				}

				// Get tables in schema
				rs = stmt.executeQuery("SHOW TABLES");
				System.out.println("Tables in schema:");
				while (rs.next()) {
					System.out.println("- " + rs.getString(1));
				}

				// Close connection
				connection.close();
				System.out.println("Connection closed.");

			} catch (SQLException e) {
				System.out.println("Connection failed! Error: " + e.getMessage());
			} catch (ClassNotFoundException e) {
				System.out.println("MySQL JDBC Driver not found! Error: " + e.getMessage());
			}
		};
	}
}