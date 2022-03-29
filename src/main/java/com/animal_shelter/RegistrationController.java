package com.animal_shelter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.ArrayList;

//TODO: Customer is already selected in searchCustomerAndPet, create an indicator for that -also for the selected pet-
// Needed functions: selectPet, addCustomer, addPet
// Finishing touch: receipt.
public class RegistrationController implements Initializable
{
    private final Database DB = new Database();

    @FXML
    private ListView<String> customerDetailsListView;
    @FXML
    private TextField customerPhoneNoTextField;
    @FXML
    private Button searchCustomerButton;
    @FXML
    private Button addCustomerButton;
    @FXML
    private ListView<String> customerPetsListView;
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
        searchCustomerButton.setDisable(true);
        selectPetButton.setDisable(true);
        addPetButton.setDisable(true);
        // Set the listeners for the customerPhoneNoTextField
        setCustomerPhoneNoTextFieldListener();
    }

    @FXML
    private void switchToBooking()
    {
        SceneController.switchTo("booking");
    }

    // Searches for a customer in the database with the phone number in the customerPhoneNoTextField
    // Sets the value of the selectedCustomerID
    @FXML
    private void searchCustomerAndPet()
    {
        // Search for the customer by phone number and the pets by the owner's ID
        // If the customer exists, display the customer details and the pets
        // If the customer does not exist, display an error message on both the customerDetailsListView and the customerPetsListView
        String phoneNo = customerPhoneNoTextField.getText();
        try
        {
            // Get the customer ID
            selectedCustomerID = Integer.parseInt(
                    DB.returns("select fld_customer_id from tbl_customers where fld_customer_phone_number = '" + phoneNo +
                            "'").get(0));
            // Get the details of the customer
            ArrayList<String> selectedCustomerDetails =
                    DB.returns("select fld_customer_name, fld_customer_phone_number, fld_customer_address from tbl_customers where " +
                            "fld_customer_phone_number = '" + phoneNo + "'");
            // Clear the customer list view
            customerDetailsListView.getItems().clear();
            // Add the details to the customerDetailsListView
            for (String s:selectedCustomerDetails.get(0).split(" "))
            {
                customerDetailsListView.getItems().add(s);
            }
            // Get the pets of the customer
            ArrayList<String> selectedCustomerPets =
                    DB.returns("select fld_animal_id fld_animal_name, fld_animal_species, fld_animal_description from tbl_animals where " +
                            "fld_customer_id = " + selectedCustomerID);
            // Clear the customer pets list view
            customerPetsListView.getItems().clear();
            for (String s:selectedCustomerPets)
            {
                customerPetsListView.getItems().add(s);
            }
            // Enable the selectPetButton and addPetButton
            selectPetButton.setDisable(false);
            addPetButton.setDisable(false);
        }
        catch (Exception e)
        {
            // Disable the selectPetButton and addPetButton
            selectPetButton.setDisable(true);
            addPetButton.setDisable(true);
            // Clear the customerDetailsListView
            customerDetailsListView.getItems().clear();
            // Clear the customerPetsListView
            customerPetsListView.getItems().clear();
            // Display an error message on the customerDetailsListView
            customerDetailsListView.getItems().add("No customer found");
            // Display an error message on the customerPetsListView
            customerPetsListView.getItems().add("No pets found");
        }
    }

    // Sets the listener for the customerPhoneNoTextField
    private void setCustomerPhoneNoTextFieldListener()
    {

        // If the value of customerPhoneNoTextField is empty, disable the searchCustomerButton
        // Otherwise, enable it
        customerPhoneNoTextField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            searchCustomerButton.setDisable(newValue.trim().isEmpty());
        });
    }
}
