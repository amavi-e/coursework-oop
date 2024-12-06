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
    @FXML
    public Button manageUsersButton;
    @FXML
    public Button signInButton;
    @FXML
    public Button signUpButton;
    @FXML
    public Button logOutButton;

    private String username;

    //set username and populate articles when set
    public void setUsername(String username) {
        this.username = username;
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        }
    }

    public void onManageArticlesButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-manage-articles.fxml"));
        Parent root = loader.load();

        AdminManageArticlesController adminManageArticlesController = loader.getController();
        adminManageArticlesController.setUsername(username);

        Stage stage = (Stage) manageArticlesButton.getScene().getWindow();
        stage.setScene(new Scene(root, 743, 495));
        stage.show();
    }

    public void onManageUsersButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-manage-users.fxml"));
        Parent root = loader.load();

        AdminManageUsersController adminManageUsersController = loader.getController();
        adminManageUsersController.setUsername(username);

        Stage stage = (Stage) manageArticlesButton.getScene().getWindow();
        stage.setScene(new Scene(root, 743, 495));
        stage.show();
    }

    public void onSignInButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage)this.signInButton.getScene().getWindow();
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("admin-sign-in.fxml"));
        previousStage.setScene(new Scene(root, 331, 400));
        previousStage.show();
    }

    public void onSignUpButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage)this.signUpButton.getScene().getWindow();
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("admin-sign-up.fxml"));
        previousStage.setScene(new Scene(root, 516, 400));
        previousStage.show();
    }

    public void onLogOutButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage)this.logOutButton.getScene().getWindow();
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("portal-selection-page.fxml"));
        previousStage.setScene(new Scene(root, 476, 167));
        previousStage.show();
    }
}
