package com.example.courseworkoop;

public class Article {
    private String title;
    private String description;
    private String content;
    private String category; // New field to store the category of the article

    public Article(String title, String description, String content, String category) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.category = category; // Initialize the category
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() { // Getter for category
        return category;
    }

    @Override
    public String toString() {
        return title + " - " + description; // Display title and description in ListView
    }
}
