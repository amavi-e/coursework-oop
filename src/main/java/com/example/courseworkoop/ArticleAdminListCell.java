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
    public void updateItem(Article article, boolean empty) {
        super.updateItem(article, empty);

        if (empty || article == null || "No Articles Available".equals(article.getTitle())) {
            setText("No Articles Available");
            setGraphic(null);
        } else {
            setText(article.getTitle() + "\n" + article.getDescription());
            setGraphic(deleteButton);
        }
    }

}

