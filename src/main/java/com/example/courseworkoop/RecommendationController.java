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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecommendationController {
    @FXML
    public ListView<Article> recommendationsListView;
    @FXML
    public Label usernameLabel;
    @FXML
    public Button backButton;
    @FXML
    public Button signInButton;
    @FXML
    public Button signUpButton;
    @FXML
    public Button logOutButton;

    private User user; // Replaced username with a User instance

    public void setUser(User user) {
        this.user = user; // Ensure the user field is properly initialized
        if (usernameLabel != null && user != null) {
            usernameLabel.setText("Welcome, " + user.getUsername() + "!");
        }
    }


    public void populateRecommendations() {
        ExecutorService executorService = Executors.newSingleThreadExecutor(); //tasks are executed sequentially
        executorService.submit(() -> {
            List<Article> recommendedArticles = fetchRecommendedArticles(); //method to fetch articles
            ObservableList<Article> observableList = FXCollections.observableArrayList(recommendedArticles); //articles stored in an observable list
            recommendationsListView.setItems(observableList); //observable list items set in the list view.

            //custom cell factory to display articles
            recommendationsListView.setCellFactory(param -> new ArticleListCell());

            recommendationsListView.setOnMouseClicked(event -> { //article clicked
                Article selectedArticle = recommendationsListView.getSelectionModel().getSelectedItem();
                if (selectedArticle != null) {
                    openArticleView(selectedArticle);
                }
            });
        });
        executorService.shutdown();
    }

    public List<Article> fetchRecommendedArticles() {
        List<Article> recommendedArticles = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {

            List<String> viewedCategories = new ArrayList<>();
            List<String> dislikedCategories = new ArrayList<>();
            List<Object> parameters = new ArrayList<>();

            String viewQuery = "SELECT articleCategory, COUNT(*) AS viewCount " +
                    "FROM UserArticleHistory " +
                    "WHERE username = ? AND likeDislikeStatus != 0 " +
                    "GROUP BY articleCategory " +
                    "ORDER BY viewCount DESC"; //categories the user has viewed (excluding dislikes)
            String dislikeQuery = "SELECT DISTINCT articleCategory FROM UserArticleHistory " +
                    "WHERE username = ? AND likeDislikeStatus = 0"; //categories the user dislikes.

            //categories viewed by the user
            try (PreparedStatement viewStmt = connection.prepareStatement(viewQuery)) {
                viewStmt.setString(1, this.user.getUsername());
                ResultSet rs = viewStmt.executeQuery();
                while (rs.next()) {
                    viewedCategories.add(rs.getString("articleCategory"));
                }
            }

            //categories disliked by the user
            try (PreparedStatement dislikeStmt = connection.prepareStatement(dislikeQuery)) {
                dislikeStmt.setString(1, this.user.getUsername());
                ResultSet rs = dislikeStmt.executeQuery();
                while (rs.next()) {
                    dislikedCategories.add(rs.getString("articleCategory"));
                }
            }

            //build SQL query for recommendations
            StringBuilder queryBuilder = new StringBuilder("SELECT title, description, content, url, category FROM Articles WHERE ");
            List<String> conditions = new ArrayList<>();

            if (!viewedCategories.isEmpty()) {
                String viewedCondition = "category IN (" + String.join(",", viewedCategories.stream().map(c -> "?").toArray(String[]::new)) + ")";
                conditions.add(viewedCondition);
                parameters.addAll(viewedCategories);
            }

            if (!dislikedCategories.isEmpty()) {
                String dislikedCondition = "category NOT IN (" + String.join(",", dislikedCategories.stream().map(c -> "?").toArray(String[]::new)) + ")";
                conditions.add(dislikedCondition);
                parameters.addAll(dislikedCategories);
            }

            if (conditions.isEmpty()) {
                // No preferences found; return an empty list or a default set of recommendations
                return recommendedArticles;
            }

            queryBuilder.append(String.join(" AND ", conditions)).append(" ORDER BY RAND()");

            // Step 3: Execute query
            try (PreparedStatement stmt = connection.prepareStatement(queryBuilder.toString())) {
                for (int i = 0; i < parameters.size(); i++) {
                    stmt.setString(i + 1, parameters.get(i).toString());
                }

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    recommendedArticles.add(new Article(
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("url"),
                            rs.getString("category")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recommendedArticles;
    }

    public void openArticleView(Article article) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("article-view.fxml"));
            Parent root = loader.load();

            // Get controller and pass article details and username
            ArticleViewController articleViewController = loader.getController();
            articleViewController.setArticleDetails(article.getTitle(), article.getDescription(), article.getUrl(), this.user); // Pass URL here

            // Set the scene for the article view
            Stage stage = (Stage) recommendationsListView.getScene().getWindow();
            stage.setScene(new Scene(root, 743, 558));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onBackButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage) this.backButton.getScene().getWindow();

        // Load the user-view.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
        Parent root = loader.load();

        // Get the controller for UserViewController and pass the username
        UserViewController userViewController = loader.getController();
        userViewController.setUser(this.user); // Ensure the username is passed to the UserViewController

        // Set the scene and show the previous stage
        previousStage.setScene(new Scene(root, 743, 495));
        previousStage.show();
    }

    public void onSignInButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage)this.signInButton.getScene().getWindow();
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("sign-in-page.fxml"));
        previousStage.setScene(new Scene(root, 331, 400));
        previousStage.show();
    }

    public void onSignUpButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage)this.signUpButton.getScene().getWindow();
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("sign-up-page.fxml"));
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
