package com.example.courseworkoop;

public class Article {
    private String title;
    private String description;
    private String category; // New field to store the category of the article
    private String url;

    public Article(String title, String description, String url, String category) {
        this.title = title;
        this.description = description;
        this.url = url; // Initialize the URL
        this.category = category; // Initialize the category

    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


    public String getCategory() { // Getter for category
        return category;
    }

    public String getUrl() { // Getter for URL
        return url;
    }

    @Override
    public String toString() {
        return title + " - " + description; // Display title and description in ListView
    }
}
