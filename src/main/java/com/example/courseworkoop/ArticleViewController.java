package com.example.courseworkoop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ArticleViewController {
    @FXML
    public Button backButton;
    @FXML
    public Button likeButton;
    @FXML
    public Button dislikeButton;
    @FXML
    public Button skipButton;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label urlLabel; // Updated label for URL
    @FXML
    protected Label usernameLabel;

    private String username; // Store username
    private String articleTitle; // Store article title
    private String currentLikeDislikeStatus = "none"; // To track like/dislike status

    // Method to set article details along with username
    public void setArticleDetails(String title, String description, String url, String username) { // Updated parameter
        titleLabel.setText(title);
        descriptionLabel.setText("Description: " + description);
        urlLabel.setText("URL: " + url); // Display URL instead of content
        this.username = username; // Store the username
        this.articleTitle = title;

        setUsername(username); // Ensure username is displayed in the view
        retrieveLikeDislikeStatus(); // Fetch current like/dislike status
    }

    public void setUsername(String username) {
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        }
    }

    // Rest of the code remains unchanged...
    private void retrieveLikeDislikeStatus() {
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";
        String query = "SELECT likeDislikeStatus FROM UserArticleHistory WHERE username = ? AND articleTitle = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, articleTitle);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    currentLikeDislikeStatus = resultSet.getString("likeDislikeStatus");
                    updateButtonStates();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateButtonStates() {
        if ("liked".equals(currentLikeDislikeStatus)) {
            likeButton.setStyle("-fx-background-color: #388E3C;"); // Highlight like button
            dislikeButton.setStyle(""); // Reset dislike button
        } else if ("disliked".equals(currentLikeDislikeStatus)) {
            likeButton.setStyle(""); // Reset like button
            dislikeButton.setStyle("-fx-background-color: #D32F2F;"); // Highlight dislike button
        } else {
            likeButton.setStyle("");  // Reset like button
            dislikeButton.setStyle("");  // Reset dislike button
        }
    }

    public void onBackButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage) this.backButton.getScene().getWindow();

        // Load the user-view.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
        Parent root = loader.load();

        // Get the controller for UserViewController and pass the username
        UserViewController userViewController = loader.getController();
        userViewController.setUsername(this.username); // Ensure the username is passed to the UserViewController

        // Set the scene and show the previous stage
        previousStage.setScene(new Scene(root, 743, 495));
        previousStage.show();
    }

    public void onLikeButtonClick(ActionEvent actionEvent) {
        if ("liked".equals(currentLikeDislikeStatus)) {
            currentLikeDislikeStatus = "none"; // Un-like the article
        } else {
            currentLikeDislikeStatus = "liked"; // Like the article
        }

        logUserHistory(username, articleTitle, currentLikeDislikeStatus);
        updateButtonStates();
    }

    public void onDislikeButtonClick(ActionEvent actionEvent) {
        if ("disliked".equals(currentLikeDislikeStatus)) {
            currentLikeDislikeStatus = "none"; // Un-dislike the article
        } else {
            currentLikeDislikeStatus = "disliked"; // Dislike the article
        }

        logUserHistory(username, articleTitle, currentLikeDislikeStatus);
        updateButtonStates();
    }

    public void logUserHistory(String username, String articleTitle, String likeDislikeStatus) {
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";
        String query;

        String checkQuery = "SELECT * FROM UserArticleHistory WHERE username = ? AND articleTitle = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {

            checkStatement.setString(1, username);
            checkStatement.setString(2, articleTitle);

            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next()) {
                    query = "UPDATE UserArticleHistory SET likeDislikeStatus = ? WHERE username = ? AND articleTitle = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(query)) {
                        updateStatement.setString(1, likeDislikeStatus);
                        updateStatement.setString(2, username);
                        updateStatement.setString(3, articleTitle);
                        updateStatement.executeUpdate();
                        System.out.println("User history updated successfully!");
                    }
                } else {
                    query = "INSERT INTO UserArticleHistory (username, articleTitle, articleCategory, likeDislikeStatus) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStatement = connection.prepareStatement(query)) {
                        insertStatement.setString(1, username);
                        insertStatement.setString(2, articleTitle);
                        insertStatement.setString(3, "Category"); // Replace with actual article category
                        insertStatement.setString(4, likeDislikeStatus);
                        insertStatement.executeUpdate();
                        System.out.println("User history logged successfully!");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSkipButtonClick(ActionEvent actionEvent) {
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String checkQuery = "SELECT viewCount FROM UserArticleHistory WHERE username = ? AND articleTitle = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, username);
                checkStatement.setString(2, articleTitle);

                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int viewCount = resultSet.getInt("viewCount");

                        if (viewCount > 1) {
                            String updateQuery = "UPDATE UserArticleHistory SET viewCount = viewCount - 1 WHERE username = ? AND articleTitle = ?";
                            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                                updateStatement.setString(1, username);
                                updateStatement.setString(2, articleTitle);
                                updateStatement.executeUpdate();
                                System.out.println("ViewCount reduced by 1 for the article.");
                            }
                        } else {
                            String deleteQuery = "DELETE FROM UserArticleHistory WHERE username = ? AND articleTitle = ?";
                            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                                deleteStatement.setString(1, username);
                                deleteStatement.setString(2, articleTitle);
                                deleteStatement.executeUpdate();
                                System.out.println("Article removed from user history.");
                            }
                        }
                    }
                }
            }

            Stage stage = (Stage) skipButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
            Parent root = loader.load();

            UserViewController userViewController = loader.getController();
            userViewController.setUsername(this.username);

            stage.setScene(new Scene(root, 743, 495));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
