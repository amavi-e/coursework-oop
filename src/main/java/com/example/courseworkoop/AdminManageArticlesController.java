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

public class AdminManageArticlesController {
    @FXML
    public Label usernameLabel;
    @FXML
    public ListView<Article> articlesListView; 
    @FXML
    public Button deleteAllArticlesButton;
    @FXML
    public Button backButton;
    @FXML
    public Button signInButton;
    @FXML
    public Button signUpButton;
    @FXML
    public Button logOutButton;

    private String username;

    public void setUsername(String username) {
        this.username = username;
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, Admin " + username + "!");
        }
        populateArticles();
    }

    private void populateArticles() {
        List<Article> articles = fetchArticles();
        ObservableList<Article> articleObservableList = FXCollections.observableArrayList(articles);
        articlesListView.setItems(articleObservableList);

        //custom cell factory to include Delete button
        articlesListView.setCellFactory(param -> new ArticleAdminListCell(this::deleteArticle));
    }

    private List<Article> fetchArticles() {
        List<Article> articles = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";

        String query = "SELECT title, description, url, category FROM Articles";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
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

    private void deleteArticle(Article article) {
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";

        String query = "DELETE FROM Articles WHERE title = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, article.getTitle());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Article deleted successfully: " + article.getTitle());
                populateArticles(); //refresh the article list after deletion
            } else {
                System.out.println("Failed to delete article: " + article.getTitle());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-article-view.fxml"));
            Parent root = loader.load();

            AdminArticleViewController adminArticleViewController = loader.getController();
            adminArticleViewController.setArticleDetails(article.getTitle(), article.getDescription(), article.getUrl(), this.username);

            Stage previousStage = (Stage) articlesListView.getScene().getWindow();
            previousStage.setScene(new Scene(root, 743, 495));
            previousStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDeleteAllArticlesButton(ActionEvent actionEvent) {
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";

        String deleteQuery = "DELETE FROM Articles";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {

            //execute the delete query to remove all articles
            int rowsDeleted = deleteStatement.executeUpdate();
            System.out.println("Deleted " + rowsDeleted + " articles from the database.");

            //clear the articles from the ListView and show the "No Articles Available" message
            articlesListView.getItems().clear();
            articlesListView.getItems().add(new Article("No Articles Available", "", "", ""));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onBackButtonClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-dashboard.fxml"));
        Parent root = loader.load();

        AdminDashboardController adminDashboardController = loader.getController();
        adminDashboardController.setUsername(username);

        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root, 743, 165));
        stage.show();
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
