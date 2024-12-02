package com.example.courseworkoop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminArticleViewController {
    @FXML
    public Label usernameLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label urlLabel; // URL label to display the article URL
    @FXML
    private Button backButton;

    private String title;
    private String description;
    private String url;

    private String username;

    // Set username and populate articles when set
    public void setUsername(String username) {
        this.username = username;
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        }
    }

    // Method to set the article details
    public void setArticleDetails(String title, String description, String url, String username) {
        this.title = title;
        this.description = description;
        this.url = url;

        titleLabel.setText(title);
        this.username = username;
        descriptionLabel.setText("Description: " + description);
        urlLabel.setText("URL: " + url); // Display URL

        setUsername(username);

        // Add click event on URL
        urlLabel.setOnMouseClicked(this::openArticleUrl);
    }

    // Method to go back to the previous screen
    @FXML
    public void onBackButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage) backButton.getScene().getWindow();

        // Load the admin articles management screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-manage-articles.fxml"));
        Parent root = loader.load();

        AdminManageArticlesController adminManageArticlesController = loader.getController();
        // Set the username or pass any data if needed
        adminManageArticlesController.setUsername("Admin");

        // Set the scene and show the stage
        previousStage.setScene(new Scene(root, 743, 495));
        previousStage.show();
    }

    // Method to open the article URL in a browser
    private void openArticleUrl(MouseEvent event) {
        if (url != null && !url.isEmpty()) {
            try {
                // Open the URL in the default browser
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
