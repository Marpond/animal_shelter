package com.animal_shelter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class RegistrationController implements Initializable
{
    private final Database DB = new Database();

    @FXML
    private Button backButton;
    @FXML
    private ListView<String> customerDetailsListView;
    @FXML
    private Button selectCustomerButton;
    @FXML
    private TextField customerPhoneNoTextField;
    @FXML
    private Button searchCustomerButton;
    @FXML
    private Button addCustomerButton;
    @FXML
    private ListView customerPetsListView;
    @FXML
    private Button selectPetButton;
    @FXML
    private Button addPetButton;

    private int selectedCustomerID;
    private int selectedPetID;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources)
    {
        // Default values
        selectCustomerButton.setDisable(true);
        // Set the listeners for the customerPhoneNoTextField
        setCustomerPhoneNoTextFieldListener();
    }

    @FXML
    private void switchToBooking()
    {
        SceneController.switchTo("booking");
    }

    // Searches for a customer in the database with the phone number in the customerPhoneNoTextField
    @FXML
    private void searchCustomer()
    {
        // Search for the customer by phone number
        // If the customer exists, display the customer details
        // If the customer does not exist, display an error message
        String phoneNo = customerPhoneNoTextField.getText();
        try
        {
            selectedCustomerID = Integer.parseInt(
                    DB.returns("select fld_customer_id from tbl_customers where fld_customer_phone_number = '" + phoneNo +
                            "'").get(0));
            ArrayList<String> selectedCustomer =
                    DB.returns("select fld_customer_name, fld_customer_phone_number, fld_customer_address from tbl_customers where " +
                            "fld_customer_phone_number = '" + phoneNo + "'");
            for (String s:selectedCustomer.get(0).split(" "))
            {
                customerDetailsListView.getItems().add(s);
            }
        }
        catch (Exception e)
        {
            // Clear the customerDetailsListView
            customerDetailsListView.getItems().clear();
            // Display an error message
            customerDetailsListView.getItems().add("No customer found");
        }

    }

    // Sets the listener for the customerPhoneNoTextField
    private void setCustomerPhoneNoTextFieldListener()
    {
        // If the value of customerPhoneNoTextField is not empty, enable the selectCustomerButton
        // If the value of customerPhoneNoTextField is empty, disable the selectCustomerButton
        customerPhoneNoTextField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            selectCustomerButton.setDisable(newValue.trim().isEmpty());
        });
    }
}
