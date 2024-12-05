package com.example.courseworkoop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminUserViewController {

    @FXML
    public VBox userDetailsBox; // VBox to dynamically display user details
    @FXML
    public Label userLabel;
    @FXML
    public Label usernameLabel;
    @FXML
    public Button backButton;
    @FXML
    public Button deleteUserButton;
    @FXML
    public Button signInButton;
    @FXML
    public Button signUpButton;
    @FXML
    public Button logOutButton;

    private String user; // User to display/manage
    private String username; // Admin username

    public void setUsername(String username) { // Set admin name
        this.username = username;
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        }
    }

    public void setUser(String user) { // Set user's username
        this.user = user;
        if (userLabel != null) {
            userLabel.setText("User: " + user);
        }
        populateUserHistory();
    }

    private void populateUserHistory() {
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String dbUser = "root";
        String dbPassword = "";

        String query = "SELECT articleTitle, articleCategory, viewCount, likeDislikeStatus " +
                "FROM UserArticleHistory WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, user); // Use the correct user for query
            ResultSet resultSet = statement.executeQuery();

            // Add user details to the VBox
            Label header = new Label("Articles Read:");
            header.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            userDetailsBox.getChildren().add(header);

            while (resultSet.next()) {
                String articleTitle = resultSet.getString("articleTitle");
                String articleCategory = resultSet.getString("articleCategory");
                int viewCount = resultSet.getInt("viewCount");
                String likeDislikeStatus = resultSet.getString("likeDislikeStatus");

                String details = String.format(
                        "Title: %s | Category: %s | Views: %d | Status: %s",
                        articleTitle, articleCategory, viewCount, likeDislikeStatus
                );

                Label articleLabel = new Label(details);
                articleLabel.setWrapText(true);
                userDetailsBox.getChildren().add(articleLabel);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackButtonClick(ActionEvent actionEvent) {
        try {
            Stage previousStage = (Stage) backButton.getScene().getWindow();

            // Load the admin articles management screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-manage-users.fxml"));
            Parent root = loader.load();

            AdminManageUsersController adminManageUsersController = loader.getController();
            adminManageUsersController.setUsername(username);

            // Set the scene and show the stage
            previousStage.setScene(new Scene(root, 743, 495));
            previousStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDeleteUserButton(ActionEvent actionEvent) {
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String dbUser = "root";
        String dbPassword = "";

        String deleteQuery = "DELETE FROM UserDetails WHERE Username = ?";
        String deleteHistoryQuery = "DELETE FROM UserArticleHistory WHERE Username = ?";

        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword)) {
            // Delete user from UserDetails
            try (PreparedStatement userStmt = connection.prepareStatement(deleteQuery)) {
                userStmt.setString(1, user);
                userStmt.executeUpdate();
            }

            // Delete user's article history
            try (PreparedStatement historyStmt = connection.prepareStatement(deleteHistoryQuery)) {
                historyStmt.setString(1, user);
                historyStmt.executeUpdate();
            }

            // Redirect back to AdminManageUsers
            onBackButtonClick(actionEvent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSignInButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage)this.signInButton.getScene().getWindow();
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("admin-sign-in.fxml"));
        previousStage.setScene(new Scene(root, 331, 400));
        previousStage.show();
    }

    public void onSignUpButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage)this.signUpButton.getScene().getWindow();
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("admin-sign-up.fxml"));
        previousStage.setScene(new Scene(root, 516, 400));
        previousStage.show();
    }

    public void onLogOutButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage)this.logOutButton.getScene().getWindow();
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("portal-selection-page.fxml"));
        previousStage.setScene(new Scene(root, 476, 167));
        previousStage.show();
    }
}
