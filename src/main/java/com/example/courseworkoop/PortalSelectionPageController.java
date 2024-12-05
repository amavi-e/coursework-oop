package com.example.courseworkoop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class PortalSelectionPageController {
    @FXML
    public Button userButton;
    @FXML
    public Button adminButton;

    public void onUserButtonClick(ActionEvent actionEvent) throws IOException { //open user welcome page if user is clicked
        Stage previousStage = (Stage)this.userButton.getScene().getWindow();
        Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("welcome-page.fxml"));
        previousStage.setScene(new Scene(root, 488, 250));
        previousStage.show();
    }

    public void onAdminButtonClick(ActionEvent actionEvent) throws IOException { //open admin welcome page if user is clicked
        Stage previousStage = (Stage)this.adminButton.getScene().getWindow();
        Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("admin-welcome-page.fxml"));
        previousStage.setScene(new Scene(root, 488, 250));
        previousStage.show();
    }
}
