package com.example.courseworkoop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminSignInPageController {
    @FXML
    public TextField usernameSignInPage;
    @FXML
    public Button signInButtonFinal;
    @FXML
    public Button signUpLoginPageButton;
    @FXML
    public PasswordField passwordSignInPage;
    @FXML
    public Button forgotPasswordButton;

    // Admin sign-in action
    public void OnSignInButtonFinalClick(ActionEvent actionEvent) throws IOException {
        String username = usernameSignInPage.getText();
        String password = passwordSignInPage.getText();

        // Check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.");
            return;
        }

        // Validate the admin username and password with the database
        DatabaseManager dbManager = new DatabaseManager();
        boolean validAdmin = dbManager.validateAdmin(username, password);

        if (validAdmin) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-dashboard.fxml"));
            Parent root = loader.load();

            // Pass the username to UserViewController
            AdminDashboardController adminDashboardController = loader.getController();
            adminDashboardController.setUsername(username);

            Stage stage = (Stage) signInButtonFinal.getScene().getWindow();
            stage.setScene(new Scene(root, 743, 495));
            stage.show();
        } else {
            showAlert("Error", "Invalid username or password.");
        }
    }

    // Helper method to show alerts
    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Redirect to sign-up page for admins
    public void onSignUpLoginPageButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage) this.signUpLoginPageButton.getScene().getWindow();
        Parent root = FXMLLoader.load(this.getClass().getResource("admin-sign-up.fxml"));
        previousStage.setScene(new Scene(root, 516, 400));
        previousStage.show();
    }

    // Forgot password functionality (redirect to reset password page)
    public void onForgotPasswordButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage) this.forgotPasswordButton.getScene().getWindow();
        Parent root = FXMLLoader.load(this.getClass().getResource("admin-reset-password.fxml"));
        previousStage.setScene(new Scene(root, 482, 400));
        previousStage.show();
    }
}
