package atm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class GuiController {

    private ATM atm;
    @FXML
    private TextField nameField;
    @FXML
    private TextField pinField;
    @FXML
    private TextField balanceField;
    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField loginPinField;

    @FXML
    private Label AtmNumber;
    public void setATM(ATM atm) {
        this.atm = atm;
    }
    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            if (atm == null) {
                showAlert("System Error", "ATM instance is not initialized.");
                return;
            }

            String cardNumber = cardNumberField.getText();
            String pin = loginPinField.getText();

            if (cardNumber.isEmpty() || pin.isEmpty()) {
                showAlert("Missing Information", "Please fill out both fields: Card Number and PIN.");
                return;
            }

            boolean loginSuccess = atm.login(cardNumber, pin);
            if (loginSuccess) {
                showAlert("Login Success", "Welcome back!");
                // Proceed to another scene or functionality
            } else {
                showAlert("Login Failed", "Invalid Card Number or PIN.");
            }

        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }
    @FXML
    private void generateCardNumber(ActionEvent event) {
        try {
            if (atm == null) {
                showAlert("System Error", "ATM instance is not initialized.");
                return;
            }

            String name = nameField.getText();
            String pin = pinField.getText();
            if (name.isEmpty() || pin.isEmpty()) {
                showAlert("Missing Information", "Please fill out all fields.");
                return;
            }

            double balance = Double.parseDouble(balanceField.getText());
            String cardNumber = atm.createUser(name, pin, balance);
            showAlert("Success", "User created successfully with card number: " + cardNumber);
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid numeric balance.");
        }
    }

    @FXML
    private void switchToLogin(ActionEvent event) {
        try {
            // Load the FXML file for the login scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));

            // Set the new scene
            Parent root = loader.load(); // Load the new layout

            // Get the new controller instance and set the ATM instance
            GuiController newController = loader.getController();
            newController.setATM(this.atm); // Pass the current ATM instance

            // Get the current stage from the event source (button clicked)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            // Set the new scene on the stage
            stage.setScene(scene);
            stage.setTitle("Login Screen");
            stage.show();
            switchToAtm(event);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to switch scenes: " + e.getMessage());
        }
    }

    @FXML
    private void switchToSignup(ActionEvent event) {
        try {
            // Load the FXML file for the login scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/signup.fxml"));

            // Set the new scene
            Parent root = loader.load(); // Load the new layout

            // Get the new controller instance and set the ATM instance
            GuiController newController = loader.getController();
            newController.setATM(this.atm); // Pass the current ATM instance

            // Get the current stage from the event source (button clicked)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            // Set the new scene on the stage
            stage.setScene(scene);
            stage.setTitle("Sign up Screen");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to switch scenes: " + e.getMessage());
        }
    }

    @FXML
    private void switchToAtm(ActionEvent event) {
        try {
            // Load the FXML file for the login scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/atm.fxml"));

            // Set the new scene
            Parent root = loader.load(); // Load the new layout

            // Get the new controller instance and set the ATM instance
            GuiController newController = loader.getController();
            newController.setATM(this.atm); // Pass the current ATM instance

            // Get the current stage from the event source (button clicked)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            // Set the new scene on the stage
            stage.setScene(scene);
            stage.setTitle("ATM");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to switch scenes: " + e.getMessage());
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
