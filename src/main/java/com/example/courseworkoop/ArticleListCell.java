package com.example.courseworkoop;

import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ArticleListCell extends ListCell<Article> {
    @Override
    protected void updateItem(Article article, boolean empty) {
        super.updateItem(article, empty);

        if (empty || article == null) {
            setText(null);
            setGraphic(null);
        } else {
            Text titleText = new Text(article.getTitle());
            titleText.getStyleClass().add("article-title");

            Text descriptionText = new Text(article.getDescription());
            descriptionText.getStyleClass().add("article-description");

            VBox articleBox = new VBox(5); // Add spacing between title and description
            articleBox.getChildren().addAll(titleText, descriptionText);

            setGraphic(articleBox);
        }
    }
}
