package com.example.courseworkoop;

import javafx.scene.control.ListCell;
import javafx.scene.text.Text;

public class ArticleListCell extends ListCell<Article> {
    @Override
    public void updateItem(Article article, boolean empty) {
        super.updateItem(article, empty);

        if (empty || article == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Display only the title
            Text titleText = new Text(article.getTitle());
            titleText.getStyleClass().add("article-title");

            setGraphic(titleText); // Set only the title as the graphic
        }
    }
}
