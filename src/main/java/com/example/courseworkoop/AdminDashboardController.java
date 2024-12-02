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

public class AdminDashboardController {

    @FXML
    public Label usernameLabel;
    @FXML
    public Button manageArticlesButton;

    private String username;

    // Set username and populate articles when set
    public void setUsername(String username) {
        this.username = username;
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        }
    }

    public void onManageArticlesButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-manage-articles.fxml"));
        Parent root = loader.load();

        // Pass the username to UserViewController
        AdminManageArticlesController adminManageArticlesController = loader.getController();
        adminManageArticlesController.setUsername(username);

        Stage stage = (Stage) manageArticlesButton.getScene().getWindow();
        stage.setScene(new Scene(root, 743, 495));
        stage.show();
    }
}
