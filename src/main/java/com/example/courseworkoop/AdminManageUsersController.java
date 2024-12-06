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
    public ListView<String> usersListView;
    @FXML
    public Button deleteAllUsersButton;
    @FXML
    public Button backButton;
    @FXML
    public Button signInButton;
    @FXML
    public Button signUpButton;
    @FXML
    public Button logOutButton;

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

    public void populateUsers() {
        ObservableList<String> usersList = FXCollections.observableArrayList();
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";

        String query = "SELECT FullName FROM UserDetails";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String fullName = resultSet.getString("FullName"); //fetch the full name
                usersList.add(fullName); //display full names
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        usersListView.setItems(usersList);
        usersListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { //double-click to view user history
                String selectedUser = usersListView.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    redirectToUserView(selectedUser); //pass the full name
                }
            }
        });
    }

    public void redirectToUserView(String selectedUserFullName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-user-view.fxml"));
            Parent root = loader.load();

            AdminUserViewController controller = loader.getController();
            controller.setUser(selectedUserFullName);

            //pass the admin username to AdminUserViewController
            AdminUserViewController adminUserViewController = loader.getController();
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

        //pass the admin's username to UserViewController
        AdminDashboardController adminDashboardController = loader.getController();
        adminDashboardController.setUsername(username);

        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root, 743, 165));
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
