package com.example.courseworkoop;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ArticleViewController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label contentLabel;

    public void setArticleDetails(String title, String category, String content) {
        titleLabel.setText(title);
        categoryLabel.setText("Category: " + category);
        contentLabel.setText(content);
    }
}
