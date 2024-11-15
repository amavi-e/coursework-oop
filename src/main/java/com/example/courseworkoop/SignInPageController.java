package com.example.courseworkoop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SignInPageController {
    @FXML
    public Button signInButtonFinal;
    @FXML
    public Button signUpLoginPageButton;
    @FXML
    public TextField usernameSignInPage;
    @FXML
    public PasswordField passwordSignInPage;
    @FXML
    public Button forgotPasswordButton;

    // Inside SignInPageController.java
    public void OnSignInButtonFinalClick(ActionEvent actionEvent) throws IOException {
        String username = usernameSignInPage.getText();
        String password = passwordSignInPage.getText();

        // Check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.");
            return;
        }

        // Validate the username and password with the database
        DatabaseManager dbManager = new DatabaseManager();
        boolean validUser = dbManager.validateUser(username, password);

        if (validUser) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
            Parent root = loader.load();

            // Pass the username to UserViewController
            UserViewController userController = loader.getController();
            userController.setUsername(username);

            Stage stage = (Stage) signInButtonFinal.getScene().getWindow();
            stage.setScene(new Scene(root, 648, 356));
            stage.show();
        }
        else {
            showAlert("Error", "Invalid username or password.");
        }
    }

    // Helper method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void onSignUpLoginPageButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage)this.signUpLoginPageButton.getScene().getWindow();
        Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("sign-up-page.fxml"));
        previousStage.setScene(new Scene(root, 516, 400));
        previousStage.show();
    }

    public void onForgotPasswordButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage)this.forgotPasswordButton.getScene().getWindow();
        Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("reset-password.fxml"));
        previousStage.setScene(new Scene(root, 482, 400));
        previousStage.show();
    }
}
