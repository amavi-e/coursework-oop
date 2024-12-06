package com.example.courseworkoop;

public class Article {
    private String title;
    private String description;
    private String category;
    private String url;

    public Article(String title, String description, String url, String category) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.category = category;

    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


    public String getCategory() {
        return category;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return title + " - " + description; // Display title and description in ListView
    }
}
