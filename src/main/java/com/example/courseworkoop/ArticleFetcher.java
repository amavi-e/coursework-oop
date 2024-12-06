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
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ArticleFetcher {

    private static final String API_URL = "https://newsapi.org/v2/everything?q=technology&apiKey=bb56ed4e802444cbadfedca280c8c487";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/personalizedArticles";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static final Map<String, String> CATEGORY_KEYWORDS = new HashMap<>();

    //article categories and their key words
    static {
        CATEGORY_KEYWORDS.put("AI & Machine Learning", "artificial intelligence AI machine learning neural networks deep learning automation robotics algorithms");
        CATEGORY_KEYWORDS.put("Social Media", "social media Facebook Instagram Twitter TikTok influencers content viral posts trends engagement hashtags");
        CATEGORY_KEYWORDS.put("Crime", "crime police investigation murder theft robbery fraud hacking cybercrime illegal activities");
        CATEGORY_KEYWORDS.put("Sales", "sales discounts promotions deals ecommerce marketing strategy revenue profits");
        CATEGORY_KEYWORDS.put("Shopping", "shopping retail ecommerce online store marketplace customers brands");
        CATEGORY_KEYWORDS.put("Healthcare", "healthcare hospitals clinics medical treatment patients insurance health management doctors nurses");
        CATEGORY_KEYWORDS.put("Hospital", "hospital emergency surgery ICU healthcare facility treatment doctors nurses");
        CATEGORY_KEYWORDS.put("Agriculture", "agriculture farming crops livestock soil irrigation sustainable farming pesticides fertilizers");
        CATEGORY_KEYWORDS.put("Geological Research", "geology earth research tectonics seismology minerals rocks natural disasters volcanoes earthquakes");
        CATEGORY_KEYWORDS.put("Entertainment", "movies music celebrities Hollywood Bollywood TV shows streaming platforms comics theater");
        CATEGORY_KEYWORDS.put("Education", "education learning school university online courses training knowledge exams skills");
        CATEGORY_KEYWORDS.put("Business & Finance", "business finance economy stocks investing startup entrepreneur market banking trading cryptocurrency");
        CATEGORY_KEYWORDS.put("Technology & Gadgets", "technology gadgets software hardware cloud computing cybersecurity smartphones apps");
        CATEGORY_KEYWORDS.put("Science & Research", "science space research biology physics chemistry genetics exploration innovation experiments NASA");
        CATEGORY_KEYWORDS.put("Health & Wellness", "health medicine fitness nutrition mental health wellness pandemic COVID vaccine disease hospital");
        CATEGORY_KEYWORDS.put("Sports", "sports football basketball cricket tennis Olympics athletics soccer baseball hockey");
        CATEGORY_KEYWORDS.put("Politics", "politics elections government policies democracy parliament congress laws leaders diplomacy");
        CATEGORY_KEYWORDS.put("Environment & Sustainability", "environment climate pollution recycling sustainability global warming wildlife conservation energy");
        CATEGORY_KEYWORDS.put("Travel & Tourism", "travel tourism destinations hotels flights sightseeing adventure backpacking itineraries vacations trips");
        CATEGORY_KEYWORDS.put("Food & Cooking", "food cooking recipes cuisine dining restaurants kitchen culinary baking chefs meals");
        CATEGORY_KEYWORDS.put("Lifestyle", "lifestyle habits wellness mindfulness personal development happiness trends routines");
        CATEGORY_KEYWORDS.put("Fashion & Beauty", "fashion beauty skincare makeup trends style clothing accessories runway grooming");
        CATEGORY_KEYWORDS.put("Automotive", "automotive cars vehicles electric vehicles engines road trips mechanics driving car reviews");
        CATEGORY_KEYWORDS.put("Real Estate", "real estate property housing market apartments homes land development mortgages construction");
        CATEGORY_KEYWORDS.put("Gaming", "gaming video games esports consoles PC gaming mobile gaming virtual reality online games");
        CATEGORY_KEYWORDS.put("History", "history ancient modern events wars civilizations archaeology historical leaders discoveries");
        CATEGORY_KEYWORDS.put("World News", "world news international events diplomacy global conflicts economy trade culture current events");
        CATEGORY_KEYWORDS.put("Arts & Culture", "arts culture painting sculpture museums galleries photography heritage creativity exhibits festivals");
        CATEGORY_KEYWORDS.put("Startups & Entrepreneurship", "startups entrepreneurship innovation business growth funding investors ventures bootstrapping");
        CATEGORY_KEYWORDS.put("Books & Literature", "books literature novels poetry authors writing publishing reading libraries reviews");
        CATEGORY_KEYWORDS.put("Religion & Spirituality", "religion spirituality beliefs practices faith meditation rituals culture philosophy theology");
        CATEGORY_KEYWORDS.put("Parenting & Family", "parenting family children education motherhood fatherhood relationships parenting tips childhood development");
        CATEGORY_KEYWORDS.put("DIY & Home Improvement", "DIY home improvement repairs renovation furniture decoration tools projects gardening household");
    }

    public void fetchAndStoreArticles() {
        try {
            //fetch articles from API
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            //reads the API response line by line using a BufferedReader and appends it to a StringBuilder
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            //converts the response string into a JSON object.
            JSONObject jsonResponse = new JSONObject(content.toString());

            // Extract total number of articles
            int totalArticles = jsonResponse.getInt("totalResults");
            System.out.println("Total number of articles available: " + totalArticles);

            JSONArray articles = jsonResponse.getJSONArray("articles");

            //handling missing fields
            for (int i = 0; i < articles.length(); i++) {
                JSONObject article = articles.getJSONObject(i);
                String title = article.optString("title", "No Title");
                String description = article.optString("description", "No Description");
                String contentText = article.optString("content", "No Content");
                String urlToArticle = article.optString("url", "No URL");

                //skip articles with "[Removed]" in title, description, content or url
                if (title.equals("[Removed]") || description.equals("[Removed]") || contentText.equals("[Removed]") || urlToArticle.equals("[Removed]")) {
                    System.out.println("Skipping article with removed content.");
                    continue;
                }

                //categorize the article using NLP
                String category = categorizeArticleUsingNLP(title + " " + description + " " + contentText);

                //retry categorization if the category is "General"
                if (category.equals("General")) {
                    System.out.println("Retrying categorization for article: " + title);
                    category = retryCategorization(title + " " + description + " " + contentText);
                }

                //store the article in the database
                storeArticleInDatabase(title, description, contentText, urlToArticle, category);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //cleans and tokenizes article text.
    //computes similarity scores with predefined categories using TF-IDF and cosine similarity.
    public String categorizeArticleUsingNLP(String text) {
        Map<String, Double> similarityScores = new HashMap<>();
        String cleanedText = preprocessText(text);

        Map<String, Integer> articleWordCount = calculateWordFrequency(cleanedText);

        for (Map.Entry<String, String> categoryEntry : CATEGORY_KEYWORDS.entrySet()) {
            String category = categoryEntry.getKey();
            String keywords = preprocessText(categoryEntry.getValue());

            //create TF-IDF for category keywords
            Map<String, Integer> categoryWordCount = calculateWordFrequency(keywords);

            //calculate cosine similarity
            double similarity = calculateCosineSimilarity(articleWordCount, categoryWordCount);
            similarityScores.put(category, similarity);
        }

        //find the category with the highest similarity score
        return similarityScores.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .filter(entry -> entry.getValue() > 0.02)
                .map(Map.Entry::getKey)
                .orElse("General");
    }

    public String preprocessText(String text) {
        text = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        String[] words = text.split("\\s+");
        StringBuilder processedText = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                processedText.append(stem(word)).append(" ");
            }
        }
        return processedText.toString().trim();
    }

    public String stem(String word) {
        if (word.length() > 3) {
            if (word.endsWith("ing") || word.endsWith("ed")) {
                word = word.substring(0, word.length() - 3);
            } else if (word.endsWith("ly") || word.endsWith("es") || word.endsWith("s")) {
                word = word.substring(0, word.length() - 2);
            }
        }
        return word;
    }

    public String retryCategorization(String text) {
        System.out.println("Retrying categorization with adjusted thresholds...");
        return categorizeArticleUsingNLP(text);
    }

    public Map<String, Integer> calculateWordFrequency(String text) {
        Map<String, Integer> wordCount = new HashMap<>();
        String[] words = text.split("\\W+");
        for (String word : words) {
            word = word.toLowerCase();
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }
        return wordCount;
    }

    public double calculateCosineSimilarity(Map<String, Integer> vec1, Map<String, Integer> vec2) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (String key : vec1.keySet()) {
            dotProduct += vec1.getOrDefault(key, 0) * vec2.getOrDefault(key, 0);
            normA += Math.pow(vec1.get(key), 2);
        }

        for (String key : vec2.keySet()) {
            normB += Math.pow(vec2.get(key), 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public void storeArticleInDatabase(String title, String description, String content, String url, String category) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO Articles (title, description, content, url, category) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, title);
                statement.setString(2, description);
                statement.setString(3, content);
                statement.setString(4, url);
                statement.setString(5, category);
                statement.executeUpdate();
                System.out.println("Stored article: " + title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ArticleFetcher articleFetcher = new ArticleFetcher();
        articleFetcher.fetchAndStoreArticles();
    }
}





