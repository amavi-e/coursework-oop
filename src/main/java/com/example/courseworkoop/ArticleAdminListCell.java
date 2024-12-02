package com.example.courseworkoop;

import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.function.Consumer;

public class ArticleAdminListCell extends ListCell<Article> {
    private final HBox content;
    private final Text titleText;
    private final Button deleteButton;

    public ArticleAdminListCell(Consumer<Article> onDelete) {
        super();
        titleText = new Text();
        deleteButton = new Button("Delete");

        deleteButton.setOnAction(event -> {
            Article article = getItem();
            if (article != null) {
                onDelete.accept(article);
            }
        });

        content = new HBox(10, titleText, deleteButton);
    }

    @Override
    protected void updateItem(Article article, boolean empty) {
        super.updateItem(article, empty);

        if (empty || article == null || "No Articles Available".equals(article.getTitle())) {
            setText("No Articles Available");
            setGraphic(null); // Remove the button or any additional graphics
        } else {
            // Existing rendering logic for articles with the "Delete" button
            setText(article.getTitle() + "\n" + article.getDescription());
            setGraphic(deleteButton); // Assume deleteButton is your Delete button
        }
    }

}

