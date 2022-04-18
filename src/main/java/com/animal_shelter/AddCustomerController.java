package com.animal_shelter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class AddCustomerController implements Initializable {
    private final Database DB = new Database();
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField phoneNumberTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private Button addCustomerButton;
    @FXML
    private Text addConfirmationText;

    private String name;
    private String phoneNumber;
    private String address;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addCustomerButton.setDisable(true);
        setListeners();
    }

    @FXML
    private void addCustomer() {
        // Add customer to database
        DB.executes(String.format("insert_customer '%s', '%s', '%s'",
                name, phoneNumber, address));
        // Confirmation text
        addConfirmationText.setText("Customer added!");
    }

    // Set listeners for the text fields
    // If any of the text fields are empty, or the thresholds are exceeded, disable the add button
    private void setListeners() {
        nameTextField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            addCustomerButton.setDisable(newValue.length() > 30 || areTextFieldsEmpty() || RegistrationController.checkForIllegalCharacters(newValue));
            name = newValue;
        });
        phoneNumberTextField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            addCustomerButton.setDisable(newValue.length() > 20 || areTextFieldsEmpty() || RegistrationController.checkForIllegalCharacters(newValue));
            phoneNumber = newValue;
        });
        addressTextField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            addCustomerButton.setDisable(areTextFieldsEmpty() || RegistrationController.checkForIllegalCharacters(newValue));
            address = newValue;
        });
    }

    // Check if any of the text fields are empty
    private boolean areTextFieldsEmpty() {
        return nameTextField.getText().isEmpty() ||
                phoneNumberTextField.getText().isEmpty() ||
                addressTextField.getText().isEmpty();
    }
}
