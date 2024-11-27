package com.example.courseworkoop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class ArticleViewController extends BaseViewController {
    @FXML
    public Button backButton;

    @FXML
    private Label titleLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label contentLabel;

    private String username; // Store username

    // Method to set article details along with username
    public void setArticleDetails(String title, String description, String content, String username) {
        super.setUsername(username); // Set username using the inherited method
        titleLabel.setText(title);
        descriptionLabel.setText("Description: " + description);
        contentLabel.setText(content);
        this.username = username; // Store the username
    }

    // Action when back button is clicked
    public void onBackButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage) this.backButton.getScene().getWindow();

        // Load the user-view.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
        Parent root = loader.load();

        // Get the controller for UserViewController and pass the username
        UserViewController userViewController = loader.getController();
        userViewController.setUsername(this.username); // Ensure the username is passed to the UserViewController

        // Set the scene and show the previous stage
        previousStage.setScene(new Scene(root, 743, 495));
        previousStage.show();
    }

}
