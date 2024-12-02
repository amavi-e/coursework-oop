package com.example.courseworkoop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/personalizedArticles";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Method to establish a connection to the database
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Method to check if a username already exists in the UserDetails table
    public boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM UserDetails WHERE Username = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            // Check if the username exists
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to check if an admin username already exists in the adminDetails table
    public boolean adminUsernameExists(String username) {
        String query = "SELECT COUNT(*) FROM adminDetails WHERE Username = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            // Check if the admin username exists
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to register a new user in the UserDetails table
    public void registerUser(String username, String password) {
        String insertQuery = "INSERT INTO UserDetails (Username, password) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to register a new admin in the adminDetails table
    public void registerAdmin(String username, String password) {
        String insertQuery = "INSERT INTO adminDetails (Username, password) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to validate a regular user (checking UserDetails table)
    public boolean validateUser(String username, String password) {
        String query = "SELECT password FROM UserDetails WHERE Username = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            // Check if username exists and password matches
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);  // return true if password matches
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // return false if username doesn't exist or password doesn't match
    }

    // Method to validate an admin user (checking adminDetails table)
    public boolean validateAdmin(String username, String password) {
        String query = "SELECT password FROM adminDetails WHERE Username = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            // Check if username exists and password matches
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);  // return true if password matches
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // return false if username doesn't exist or password doesn't match
    }

    // Method to update password for regular users
    public boolean updatePassword(String username, String newPassword) {
        String updateQuery = "UPDATE UserDetails SET password = ? WHERE Username = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, newPassword);
            statement.setString(2, username);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // Returns true if the password was updated successfully
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to update password for admins
    public boolean updateAdminPassword(String username, String newPassword) {
        String updateQuery = "UPDATE adminDetails SET password = ? WHERE Username = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, newPassword);
            statement.setString(2, username);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // Returns true if the password was updated successfully
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
