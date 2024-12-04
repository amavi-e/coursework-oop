package com.example.courseworkoop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminManageUsersController {
    @FXML
    public Label usernameLabel;
    @FXML
    public Label titleLabel;
    @FXML
    public ListView<String> usersListView; // Update to ListView<String> for proper display
    @FXML
    public Button deleteAllUsersButton;
    @FXML
    public Button backButton;

    private String username;

    public void initialize() {
        setUsername(username);
        populateUsers();
    }

    public void setUsername(String username) {
        this.username = username;
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome " + username + "!");
        }
    }

    private void populateUsers() {
        ObservableList<String> usersList = FXCollections.observableArrayList();
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";

        String query = "SELECT Username FROM UserDetails";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String username = resultSet.getString("Username");
                usersList.add(username); // Display only usernames
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        usersListView.setItems(usersList);
        usersListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click to view user history
                String selectedUser = usersListView.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    redirectToUserView(selectedUser);
                }
            }
        });
    }

    private void redirectToUserView(String selectedUser) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-user-view.fxml"));
            Parent root = loader.load();

            AdminUserViewController controller = loader.getController();
            controller.setUser(selectedUser); // Pass the selected user

            // Pass the username to AdminUserViewController
            AdminUserViewController adminUserViewController= loader.getController();
            adminUserViewController.setUsername(username);


            Stage stage = (Stage) usersListView.getScene().getWindow();
            stage.setScene(new Scene(root, 743, 495));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDeleteAllUsersButton(ActionEvent actionEvent) {
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";

        String deleteQuery = "DELETE FROM UserDetails";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(deleteQuery)) {

            int rowsDeleted = statement.executeUpdate();
            System.out.println("Deleted " + rowsDeleted + " users from the database.");

            usersListView.getItems().clear();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onBackButtonClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-dashboard.fxml"));
        Parent root = loader.load();

        // Pass the username to UserViewController
        AdminDashboardController adminDashboardController = loader.getController();
        adminDashboardController.setUsername(username);

        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root, 743, 165));
        stage.show();
    }
}
