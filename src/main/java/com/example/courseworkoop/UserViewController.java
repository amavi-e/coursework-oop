package com.example.courseworkoop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
    @FXML
    public Button signInButton;
    @FXML
    public Button signUpButton;
    @FXML
    public Button logOutButton;

    private User user;

    //set username and populate articles when set
    public void setUser(User user) {
        this.user = user;
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + user.getUsername() + "!");
        }
        // Populate articles when the User object is set
        populateArticles();
    }


    public void populateArticles() {
        List<Article> articles = fetchArticles(); //fetch articles using the fetchArticles method
        ObservableList<Article> articleObservableList = FXCollections.observableArrayList(articles); //store the articles in an ObservableList
        articlesListView.setItems(articleObservableList); //set items in the ObservableList to ListView

        //set a custom cell factory to display title and description
        articlesListView.setCellFactory(param -> new ArticleListCell());
    }

    public List<Article> fetchArticles() {
        List<Article> articles = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";

        String query = "SELECT title, description, url, category FROM Articles";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) { //iterates through result set and creates Article objects to store each article's data
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String articleUrl = resultSet.getString("url");
                String category = resultSet.getString("category");
                articles.add(new Article(title, description, articleUrl, category));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return articles;
    }

    @FXML
    public void onArticleClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) { //double-click to view article content
            Article selectedArticle = articlesListView.getSelectionModel().getSelectedItem();
            if (selectedArticle != null) {
                showArticleContent(selectedArticle);
            }
        }
    }

    public void showArticleContent(Article article) {
        try {
            // Load the FXML file for the article view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("article-view.fxml"));
            Parent root = loader.load();

            // Get the controller for the article view
            ArticleViewController articleViewController = loader.getController();

            // Set the article details in the new scene (passing title, description, URL, and User object)
            articleViewController.setArticleDetails(article.getTitle(), article.getDescription(), article.getUrl(), this.user);

            // Switch to the article view scene
            Stage previousStage = (Stage) articlesListView.getScene().getWindow();
            previousStage.setScene(new Scene(root, 743, 558));
            previousStage.show();

            // Log the user's selected article to the database
            logUserHistory(article);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void logUserHistory(Article article) {
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";

        String checkQuery = "SELECT viewCount FROM UserArticleHistory WHERE username = ? AND articleTitle = ?"; //check if the user has already viewed the article and retrieve the viewCount
        String insertQuery = "INSERT INTO UserArticleHistory (username, articleTitle, articleCategory, viewCount) VALUES (?, ?, ?, ?)"; //insert a new record when the user views the article for the first time
        String updateQuery = "UPDATE UserArticleHistory SET viewCount = viewCount + 1 WHERE username = ? AND articleTitle = ?"; //increase view count by one

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {

            checkStatement.setString(1, this.user.getUsername()); //replace placeholder with current username
            checkStatement.setString(2, article.getTitle()); //replace with the title of the  article the user is viewing

            try (ResultSet resultSet = checkStatement.executeQuery()) { //execute check query
                if (resultSet.next()) {
                    int currentViewCount = resultSet.getInt("viewCount"); //get the current view count
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setString(1, this.user.getUsername()); //identify the user
                        updateStatement.setString(2, article.getTitle()); //identify the article
                        updateStatement.executeUpdate(); //update view count
                        System.out.println("User history updated successfully. View count: " + (currentViewCount + 1));
                    }
                } else { //insert new record if no history exists
                    try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                        insertStatement.setString(1, this.user.getUsername());
                        insertStatement.setString(2, article.getTitle());
                        insertStatement.setString(3, article.getCategory());
                        insertStatement.setInt(4, 1); //start viewCount from 1
                        insertStatement.executeUpdate();
                        System.out.println("User history logged successfully. View count: 1");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasReadAnyArticles() {
        DatabaseManager dbManager = new DatabaseManager(); //create a new DatabaseManager instance
        return dbManager.hasUserReadArticles(this.user.getUsername()); //pass the username from the User object
    }



    @FXML
    public void onRecommendArticleButtonClick() {
        try {
            if (!hasReadAnyArticles()) { //check if the user has read at least one article
                showAlert("Error", "You need to read at least one article before getting recommendations.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("recommendation-view.fxml"));
            Parent root = loader.load();

            RecommendationController recommendationController = loader.getController();
            recommendationController.setUser(this.user); //pass the username to recommendationController
            recommendationController.populateRecommendations();

            Stage currentStage = (Stage) recommendArticlesButton.getScene().getWindow();
            currentStage.setScene(new Scene(root, 743, 558));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //go back to sign in page
    public void onSignInButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage)this.signInButton.getScene().getWindow();
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("sign-in-page.fxml"));
        previousStage.setScene(new Scene(root, 331, 400));
        previousStage.show();
    }

    //go back to sign up page
    public void onSignUpButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage)this.signUpButton.getScene().getWindow();
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("sign-up-page.fxml"));
        previousStage.setScene(new Scene(root, 516, 400));
        previousStage.show();
    }

    //log out of the account
    public void onLogOutButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage)this.logOutButton.getScene().getWindow();
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("portal-selection-page.fxml"));
        previousStage.setScene(new Scene(root, 476, 167));
        previousStage.show();
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
