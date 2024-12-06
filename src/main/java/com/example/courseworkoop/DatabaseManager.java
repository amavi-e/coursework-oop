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

    //method which establishes a connection to the database
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Method to check if a username already exists in the UserDetails table
    public boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM UserDetails WHERE Username = ?"; //counts the number of rows where the username matches the input
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) { //a statement is prepared with the query

            stmt.setString(1, username); //sets the username parameter
            ResultSet rs = stmt.executeQuery(); //query is executed, retrieving the count

            //check if the username exists
            if (rs.next()) {
                return rs.getInt(1) > 0; //return true if count > 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //method to check if an admin username already exists in the adminDetails table
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

    // Method to register a new user with full name, username, and password
    public void registerUser(String fullName, String username, String password) {
        String insertQuery = "INSERT INTO UserDetails (FullName, Username, password) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, fullName);  // Set value for full name
            stmt.setString(2, username); // Set value for username
            stmt.setString(3, password); // Set value for password
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //method to register a new admin in the adminDetails table
    public void registerAdmin(String fullName, String username, String password) {
        String insertQuery = "INSERT INTO adminDetails (FullName, Username, Password) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, fullName);   // Set value for FullName
            stmt.setString(2, username);  // Set value for Username
            stmt.setString(3, password);  // Set value for Password
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //method to validate a regular user (checking UserDetails table)
    public boolean validateUser(String username, String password) {
        String query = "SELECT password FROM UserDetails WHERE Username = ?"; //to get the stored password for the username
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            //check if username exists and password matches
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);  //return true if password matches
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // return false if username doesn't exist or password doesn't match
    }

    //method to validate an admin user (checking adminDetails table)
    public boolean validateAdmin(String username, String password) {
        String query = "SELECT password FROM adminDetails WHERE Username = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            //check if username exists and password matches
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);  //return true if password matches
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; //return false if username doesn't exist or password doesn't match
    }

    //method to update password for regular users
    public boolean updatePassword(String username, String newPassword) {
        String updateQuery = "UPDATE UserDetails SET password = ? WHERE Username = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, newPassword); //set new password
            statement.setString(2, username);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; //returns true if the password was updated successfully
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //method to update password for admins
    public boolean updateAdminPassword(String username, String newPassword) {
        String updateQuery = "UPDATE adminDetails SET password = ? WHERE Username = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, newPassword);
            statement.setString(2, username);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; //returns true if the password was updated successfully
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to check if a user with the same full name, username, and password already exists
    public boolean userAlreadyRegistered(String fullName, String username, String password) {
        String query = "SELECT COUNT(*) FROM UserDetails WHERE FullName = ? AND Username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, fullName);  // Set the full name parameter
            stmt.setString(2, username); // Set the username parameter
            stmt.setString(3, password); // Set the password parameter
            ResultSet rs = stmt.executeQuery();

            // Check if a matching user exists
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if count > 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to check if an admin with the same full name, username, and password already exists
    public boolean adminAlreadyRegistered(String fullName, String username, String password) {
        String query = "SELECT COUNT(*) FROM adminDetails WHERE FullName = ? AND Username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, fullName);  // Set the full name parameter
            stmt.setString(2, username); // Set the username parameter
            stmt.setString(3, password); // Set the password parameter
            ResultSet rs = stmt.executeQuery();

            // Check if a matching user exists
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if count > 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasUserReadArticles(String username) {
        String query = "SELECT COUNT(*) FROM UserArticleHistory WHERE username = ?";
        try (Connection conn = connect(); //establish the database connection
             PreparedStatement stmt = conn.prepareStatement(query)) { //prepare the SQL query
            stmt.setString(1, username); //set the username parameter in the query
            ResultSet rs = stmt.executeQuery(); //execute the query
            if (rs.next()) { //if a result is found
                return rs.getInt(1) > 0; //return true if the count is greater than 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; //return false if no articles are found or an error occurs
    }


}
