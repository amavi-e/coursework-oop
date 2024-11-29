package com.example.courseworkoop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
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

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public void populateRecommendations() {
        // Use concurrency to fetch recommendations
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            List<Article> recommendedArticles = fetchRecommendedArticles();
            ObservableList<Article> observableList = FXCollections.observableArrayList(recommendedArticles);
            recommendationsListView.setItems(observableList);

            // Set a custom cell factory to display articles
            recommendationsListView.setCellFactory(param -> new ArticleListCell());
        });
        executorService.shutdown();
    }

    private List<Article> fetchRecommendedArticles() {
        List<Article> recommendedArticles = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // Step 1: Fetch user preferences from UserArticleHistory
            String userHistoryQuery = "SELECT articleCategory, COUNT(*) AS preferenceCount " +
                    "FROM UserArticleHistory " +
                    "WHERE username = ? AND likeDislikeStatus = 1 " +
                    "GROUP BY articleCategory " +
                    "ORDER BY preferenceCount DESC";
            List<String> preferredCategories = new ArrayList<>();

            try (PreparedStatement userHistoryStmt = connection.prepareStatement(userHistoryQuery)) {
                userHistoryStmt.setString(1, this.username);
                ResultSet rs = userHistoryStmt.executeQuery();

                while (rs.next()) {
                    preferredCategories.add(rs.getString("articleCategory"));
                }
            }

            // Step 2: Call Python script for ML recommendations
            List<Article> mlRecommendedArticles = callPythonRecommendationScript(this.username);

            // Step 3: Fetch articles from preferred categories
            if (!preferredCategories.isEmpty()) {
                String query = "SELECT title, description, content, category, url FROM Articles WHERE category IN (" +
                        String.join(",", preferredCategories.stream().map(c -> "?").toArray(String[]::new)) +
                        ") LIMIT 10";

                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    for (int i = 0; i < preferredCategories.size(); i++) {
                        stmt.setString(i + 1, preferredCategories.get(i));
                    }
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        recommendedArticles.add(new Article(
                                rs.getString("title"),
                                rs.getString("description"),
                                rs.getString("category"),
                                rs.getString("url") // Added URL here
                        ));
                    }
                }
            }

            // Combine ML recommendations with category-based recommendations
            recommendedArticles.addAll(mlRecommendedArticles);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return recommendedArticles;
    }

    private List<Article> callPythonRecommendationScript(String username) {
        List<Article> articles = new ArrayList<>();
        try {
            // Absolute path to the Python script
            String scriptPath = new File("/Users/ehansagajanayake/Documents/CM2601_OOP/courseworkOOPPython").getAbsolutePath();

            // Use python3 if needed
            ProcessBuilder pb = new ProcessBuilder("python3", scriptPath, username);
            pb.redirectErrorStream(true);

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length == 4) { // Ensure the script provides the URL as well
                    articles.add(new Article(
                            details[0], // Title
                            details[1], // Description
                            details[2], // Category
                            details[3]  // URL
                    ));
                }
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }
}
