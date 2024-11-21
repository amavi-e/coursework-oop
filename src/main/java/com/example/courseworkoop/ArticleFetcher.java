package com.example.courseworkoop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

public class ArticleFetcher {

    private static final String API_URL = "https://newsapi.org/v2/everything?q=technology&apiKey=bb56ed4e802444cbadfedca280c8c487";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/personalizedArticles";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public void fetchAndStoreArticles() {
        try {
            // Step 1: Fetch articles from API
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(content.toString());

            // Extract total number of articles
            int totalArticles = jsonResponse.getInt("totalResults");
            System.out.println("Total number of articles available: " + totalArticles);

            JSONArray articles = jsonResponse.getJSONArray("articles");

            // Step 2: Categorize and store articles
            for (int i = 0; i < articles.length(); i++) {
                JSONObject article = articles.getJSONObject(i);
                String title = article.optString("title", "No Title");
                String description = article.optString("description", "No Description");
                String contentText = article.optString("content", "No Content");

                // Skip articles with "[removed]" in title, description, or content
                if (title.equals("[Removed]") || description.equals("[Removed]") || contentText.equals("[Removed]")) {
                    System.out.println("Skipping article with removed content.");
                    continue;
                }

                // Categorize the article
                String category = categorizeArticle(title + " " + description + " " + contentText);

                // If uncategorized, mark as "General"
                if (category.equals("Uncategorized")) {
                    category = "General";
                }

                // Store the article in the database
                storeArticleInDatabase(title, description, contentText, category);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Categorize an article based on simple keyword matching
    private String categorizeArticle(String text) {
        Map<String, String> keywordToCategory = new HashMap<>();

        // Technology-related keywords
        keywordToCategory.put("technology", "Technology");
        keywordToCategory.put("programming", "Technology");
        keywordToCategory.put("software", "Technology");
        keywordToCategory.put("hardware", "Technology");
        keywordToCategory.put("gadgets", "Technology");
        keywordToCategory.put("AI", "Artificial Intelligence");
        keywordToCategory.put("artificial intelligence", "Artificial Intelligence");
        keywordToCategory.put("machine learning", "Artificial Intelligence");
        keywordToCategory.put("cloud computing", "Technology");
        keywordToCategory.put("cybersecurity", "Technology");

        // Health-related keywords
        keywordToCategory.put("health", "Health");
        keywordToCategory.put("medicine", "Health");
        keywordToCategory.put("fitness", "Health");
        keywordToCategory.put("nutrition", "Health");
        keywordToCategory.put("mental health", "Health");
        keywordToCategory.put("wellness", "Health");
        keywordToCategory.put("pandemic", "Health");
        keywordToCategory.put("COVID", "Health");
        keywordToCategory.put("vaccine", "Health");

        // Sports-related keywords
        keywordToCategory.put("sports", "Sports");
        keywordToCategory.put("football", "Sports");
        keywordToCategory.put("basketball", "Sports");
        keywordToCategory.put("cricket", "Sports");
        keywordToCategory.put("tennis", "Sports");
        keywordToCategory.put("Olympics", "Sports");
        keywordToCategory.put("athletics", "Sports");
        keywordToCategory.put("soccer", "Sports");

        // Business-related keywords
        keywordToCategory.put("business", "Business");
        keywordToCategory.put("finance", "Business");
        keywordToCategory.put("economy", "Business");
        keywordToCategory.put("stocks", "Business");
        keywordToCategory.put("investing", "Business");
        keywordToCategory.put("startup", "Business");
        keywordToCategory.put("entrepreneur", "Business");

        // Science-related keywords
        keywordToCategory.put("science", "Science");
        keywordToCategory.put("space", "Science");
        keywordToCategory.put("NASA", "Science");
        keywordToCategory.put("research", "Science");
        keywordToCategory.put("biology", "Science");
        keywordToCategory.put("physics", "Science");
        keywordToCategory.put("chemistry", "Science");
        keywordToCategory.put("genetics", "Science");

        // Entertainment-related keywords
        keywordToCategory.put("entertainment", "Entertainment");
        keywordToCategory.put("movies", "Entertainment");
        keywordToCategory.put("music", "Entertainment");
        keywordToCategory.put("celebrities", "Entertainment");
        keywordToCategory.put("Hollywood", "Entertainment");
        keywordToCategory.put("Bollywood", "Entertainment");
        keywordToCategory.put("TV shows", "Entertainment");
        keywordToCategory.put("games", "Entertainment");
        keywordToCategory.put("comics", "Entertainment");

        // Education-related keywords
        keywordToCategory.put("education", "Education");
        keywordToCategory.put("learning", "Education");
        keywordToCategory.put("school", "Education");
        keywordToCategory.put("university", "Education");
        keywordToCategory.put("online courses", "Education");
        keywordToCategory.put("training", "Education");

        // Environment-related keywords
        keywordToCategory.put("environment", "Environment");
        keywordToCategory.put("climate", "Environment");
        keywordToCategory.put("pollution", "Environment");
        keywordToCategory.put("recycling", "Environment");
        keywordToCategory.put("sustainability", "Environment");
        keywordToCategory.put("global warming", "Environment");
        keywordToCategory.put("wildlife", "Environment");

        // Default category
        String category = "Uncategorized";

        // Simple keyword matching
        for (Map.Entry<String, String> entry : keywordToCategory.entrySet()) {
            if (text.toLowerCase().contains(entry.getKey().toLowerCase())) {
                category = entry.getValue();
                break;
            }
        }
        return category;
    }

    // Store the article in the database
    private void storeArticleInDatabase(String title, String description, String content, String category) {
        String insertQuery = "INSERT INTO Articles (title, description, content, category) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, content);
            stmt.setString(4, category);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ArticleFetcher fetcher = new ArticleFetcher();
        fetcher.fetchAndStoreArticles();
    }
}
