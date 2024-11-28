package com.example.courseworkoop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserViewController {
    @FXML
    public Button recommendArticlesButton;
    @FXML
    public Label usernameLabel;
    @FXML
    public ListView<Article> articlesListView;

    private String username;

    // Set username and populate articles when set
    public void setUsername(String username) {
        this.username = username;
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        }
        // Populate articles when the username is set
        populateArticles();
    }

    private void populateArticles() {
        List<Article> articles = fetchArticles();
        ObservableList<Article> articleObservableList = FXCollections.observableArrayList(articles);
        articlesListView.setItems(articleObservableList);

        // Set a custom cell factory to display title and description
        articlesListView.setCellFactory(param -> new ArticleListCell());
    }

    private List<Article> fetchArticles() {
        List<Article> articles = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";

        String query = "SELECT title, description, content, category FROM Articles";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String content = resultSet.getString("content");
                String category = resultSet.getString("category");
                articles.add(new Article(title, description, content, category));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return articles;
    }

    @FXML
    public void onArticleClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) { // Double-click to view article content
            Article selectedArticle = articlesListView.getSelectionModel().getSelectedItem();
            if (selectedArticle != null) {
                showArticleContent(selectedArticle);
            }
        }
    }

    private void showArticleContent(Article article) {
        try {
            // Load the FXML file for the article view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("article-view.fxml"));
            Parent root = loader.load();

            // Get the controller for the article view
            ArticleViewController articleViewController = loader.getController();

            // Set the article details in the new scene (passing title, description, content)
            articleViewController.setArticleDetails(article.getTitle(), article.getDescription(), article.getContent(), this.username);

            // Get the current stage (previous window)
            Stage previousStage = (Stage) articlesListView.getScene().getWindow();

            // Set the new scene with article details
            previousStage.setScene(new Scene(root, 743, 495));
            previousStage.show();

            // Log the user's selected article to the database
            logUserHistory(article);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logUserHistory(Article article) {
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";

        String checkQuery = "SELECT viewCount FROM UserArticleHistory WHERE username = ? AND articleTitle = ?";
        String insertQuery = "INSERT INTO UserArticleHistory (username, articleTitle, articleCategory, viewCount) VALUES (?, ?, ?, ?)";
        String updateQuery = "UPDATE UserArticleHistory SET viewCount = viewCount + 1 WHERE username = ? AND articleTitle = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {

            checkStatement.setString(1, username);
            checkStatement.setString(2, article.getTitle());

            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next()) {
                    int currentViewCount = resultSet.getInt("viewCount");
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setString(1, username);
                        updateStatement.setString(2, article.getTitle());
                        updateStatement.executeUpdate();
                        System.out.println("User history updated successfully. View count: " + (currentViewCount + 1));
                    }
                } else {
                    try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                        insertStatement.setString(1, username);
                        insertStatement.setString(2, article.getTitle());
                        insertStatement.setString(3, article.getCategory());
                        insertStatement.setInt(4, 1); // Start viewCount from 1
                        insertStatement.executeUpdate();
                        System.out.println("User history logged successfully. View count: 1");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onRecommendArticleButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("recommendation-view.fxml"));
            Parent root = loader.load();

            RecommendationController controller = loader.getController();
            controller.setUsername(this.username);
            controller.populateRecommendations();

            Stage currentStage = (Stage) recommendArticlesButton.getScene().getWindow();
            currentStage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
