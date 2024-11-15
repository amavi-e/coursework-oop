package com.example.courseworkoop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ResetPasswordController {
    @FXML
    public TextField usernameResetPasswordField;
    @FXML
    public PasswordField enterNewPasswordField;
    @FXML
    public PasswordField confirmNewPasswordField;
    @FXML
    public Button resetPasswordButton;

    public void onResetPasswordButtonClick(ActionEvent actionEvent) {
        String username = usernameResetPasswordField.getText();
        String newPassword = enterNewPasswordField.getText();
        String confirmPassword = confirmNewPasswordField.getText();

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        // Validate username and update password
        DatabaseManager dbManager = new DatabaseManager();
        if (dbManager.usernameExists(username)) {
            boolean isUpdated = dbManager.updatePassword(username, newPassword);
            if (isUpdated) {
                showAlert("Success", "Password updated successfully.");
            } else {
                showAlert("Error", "Failed to update password. Please try again.");
            }
        } else {
            showAlert("Error", "Username not found.");
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
}
