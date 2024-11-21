package com.example.courseworkoop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    @FXML
    public void onRecommendArticleButtonClick(ActionEvent actionEvent) {
        System.out.println("Recommend Articles button clicked!");
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

    private void showArticleContent(Article article) {
        Stage stage = new Stage();
        AnchorPane pane = new AnchorPane();

        Label titleLabel = new Label(article.getTitle());
        titleLabel.setLayoutX(20);
        titleLabel.setLayoutY(20);

        Label contentLabel = new Label(article.getContent());
        contentLabel.setLayoutX(20);
        contentLabel.setLayoutY(60);
        contentLabel.setWrapText(true);
        contentLabel.setPrefWidth(400);

        pane.getChildren().addAll(titleLabel, contentLabel);

        Scene scene = new Scene(pane, 500, 300);
        stage.setScene(scene);
        stage.setTitle("Article Content");
        stage.show();

        // Log the user's selected article to the database
        logUserHistory(article);
    }

    private void logUserHistory(Article article) {
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";

        String query = "INSERT INTO UserArticleHistory (username, articleTitle, articleCategory) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, article.getTitle());
            statement.setString(3, article.getCategory());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("User history logged successfully!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
