package com.example.courseworkoop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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
    public Button signInButton;
    @FXML
    public Button signUpButton;
    @FXML
    public Button logOutButton;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label urlLabel;
    @FXML
    protected Label usernameLabel;

    private String url;
    private User user; // Use the User class
    private String articleTitle;
    private String currentLikeDislikeStatus = "none";

    public void setArticleDetails(String title, String description, String url, User user) {
        titleLabel.setText(title);
        descriptionLabel.setText("Description: " + description);
        urlLabel.setText("URL: " + url);
        this.user = user; // Store the User object
        this.articleTitle = title;
        this.url = url;

        setUser(user); // Use the User object to set the username
        retrieveLikeDislikeStatus();

        urlLabel.setOnMouseClicked(this::openArticleUrl);
    }

    public void setUser(User user) {
        this.user = user; // Store the User object
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + user.getUsername() + "!");
        }
    }

    public void retrieveLikeDislikeStatus() {
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String dbUser = "root";
        String password = "";
        String query = "SELECT likeDislikeStatus FROM UserArticleHistory WHERE username = ? AND articleTitle = ?";

        try (Connection connection = DriverManager.getConnection(url, dbUser, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, user.getUsername()); // Use the User object
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

    public void updateButtonStates() {
        if ("liked".equals(currentLikeDislikeStatus)) {
            likeButton.setStyle("-fx-background-color: #388E3C;");
            dislikeButton.setStyle("");
        } else if ("disliked".equals(currentLikeDislikeStatus)) {
            likeButton.setStyle("");
            dislikeButton.setStyle("-fx-background-color: #D32F2F;");
        } else {
            likeButton.setStyle("");
            dislikeButton.setStyle("");
        }
    }

    public void onBackButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage) this.backButton.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
        Parent root = loader.load();

        UserViewController userViewController = loader.getController();
        userViewController.setUser(this.user); // Pass the User object

        previousStage.setScene(new Scene(root, 743, 558));
        previousStage.show();
    }

    public void onLikeButtonClick(ActionEvent actionEvent) {
        currentLikeDislikeStatus = "liked".equals(currentLikeDislikeStatus) ? "none" : "liked";

        logUserHistory(user.getUsername(), articleTitle, currentLikeDislikeStatus);
        updateButtonStates();
    }

    public void onDislikeButtonClick(ActionEvent actionEvent) {
        currentLikeDislikeStatus = "disliked".equals(currentLikeDislikeStatus) ? "none" : "disliked";

        logUserHistory(user.getUsername(), articleTitle, currentLikeDislikeStatus);
        updateButtonStates();
    }

    public void logUserHistory(String username, String articleTitle, String likeDislikeStatus) {
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String dbUser = "root";
        String password = "";
        String query;

        String checkQuery = "SELECT * FROM UserArticleHistory WHERE username = ? AND articleTitle = ?";

        try (Connection connection = DriverManager.getConnection(url, dbUser, password);
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
                        insertStatement.setString(3, "Category");
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
        String dbUser = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, dbUser, password)) {
            String checkQuery = "SELECT viewCount FROM UserArticleHistory WHERE username = ? AND articleTitle = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, user.getUsername());
                checkStatement.setString(2, articleTitle);

                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int viewCount = resultSet.getInt("viewCount");

                        if (viewCount > 1) {
                            String updateQuery = "UPDATE UserArticleHistory SET viewCount = viewCount - 1 WHERE username = ? AND articleTitle = ?";
                            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                                updateStatement.setString(1, user.getUsername());
                                updateStatement.setString(2, articleTitle);
                                updateStatement.executeUpdate();
                                System.out.println("View count reduced by 1 for the article.");
                            }
                        } else {
                            String deleteQuery = "DELETE FROM UserArticleHistory WHERE username = ? AND articleTitle = ?";
                            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                                deleteStatement.setString(1, user.getUsername());
                                deleteStatement.setString(2, articleTitle);
                                deleteStatement.executeUpdate();
                                System.out.println("Article removed from user history.");
                            }
                        }
                    }
                }
            }

            // Navigate back to the user view
            Stage stage = (Stage) skipButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
            Parent root = loader.load();

            UserViewController userViewController = loader.getController();
            userViewController.setUser(this.user); // Pass the User object

            stage.setScene(new Scene(root, 743, 495));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void openArticleUrl(MouseEvent event) {
        if (url != null && !url.isEmpty()) {
            try {
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onSignInButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage) this.signInButton.getScene().getWindow();
        Parent root = FXMLLoader.load(this.getClass().getResource("sign-in-page.fxml"));
        previousStage.setScene(new Scene(root, 331, 400));
        previousStage.show();
    }

    public void onSignUpButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage) this.signUpButton.getScene().getWindow();
        Parent root = FXMLLoader.load(this.getClass().getResource("sign-up-page.fxml"));
        previousStage.setScene(new Scene(root, 516, 400));
        previousStage.show();
    }

    public void onLogOutButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage) this.logOutButton.getScene().getWindow();
        Parent root = FXMLLoader.load(this.getClass().getResource("portal-selection-page.fxml"));
        previousStage.setScene(new Scene(root, 476, 167));
        previousStage.show();
    }
}
