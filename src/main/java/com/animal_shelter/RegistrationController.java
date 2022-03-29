package com.animal_shelter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.util.ArrayList;

//TODO:
// Needed functions: addCustomer, addPet
// Finishing touch: receipt.
public class RegistrationController implements Initializable
{
    private final Database DB = new Database();

    @FXML
    private ListView<String> customerDetailsListView;
    @FXML
    private TextField customerPhoneNumberTextField;
    @FXML
    private Button searchCustomerButton;
    @FXML
    private ListView<String> customerPetsListView;
    @FXML
    private Button addPetButton;
    @FXML
    private Text customerSelectionConfirmationText;
    @FXML
    private Text petSelectionConfirmationText;

    public static int selectedCustomerID;
    public static int selectedPetID;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources)
    {
        // Default values
        searchCustomerButton.setDisable(true);
        customerDetailsListView.setDisable(true);
        // Set the listeners for the customerPhoneNoTextField
        setCustomerPhoneNoTextFieldListener();
        setCustomerPetsListViewListener();
    }

    @FXML
    private void switchToBooking()
    {
        SceneController.load("booking");
    }

    @FXML
    private void addCustomer()
    {
        SceneController.popup("addCustomer");
    }

    // Sets the listener for the customerPetsListView
    @FXML
    private void setCustomerPetsListViewListener()
    {
        customerPetsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            try
            {
                // Set the selectedPetID to the ID of the selected pet
                selectedPetID = Integer.parseInt(newValue.split(" ")[0]);
                // Set the petSelectionConfirmationText to the name of the selected pet
                petSelectionConfirmationText.setText(String.format("%s selected.", newValue.split(" ")[1]));
            }
            // If the selected item is null, do nothing
            catch (Exception ignored) {}
        });
    }
    // Searches for a customer in the database with the phone number in the customerPhoneNoTextField
    // Sets the value of the selectedCustomerID
    @FXML
    private void searchCustomerAndPet()
    {
        // Search for the customer by phone number and the pets by the owner's ID
        // If the customer exists, display the customer details and the pets
        String phoneNo = customerPhoneNumberTextField.getText();
        try
        {
            // Get the customer ID
            // At this point, the customer has already been selected
            selectedCustomerID = Integer.parseInt(
                    DB.returns("select fld_customer_id " +
                                "from tbl_customers where fld_customer_phone_number = '" + phoneNo + "'").get(0));
            // Set the text of the customerSelectionConfirmationText
            customerSelectionConfirmationText.setText("Customer selected");
            // Get the details of the customer
            ArrayList<String> selectedCustomerDetails =
                    DB.returns("select fld_customer_name, fld_customer_phone_number, fld_customer_address" +
                                " from tbl_customers where fld_customer_phone_number = '" + phoneNo + "'");
            customerDetailsListView.getItems().clear();
            // Add the details to the customerDetailsListView
            for (String s:selectedCustomerDetails.get(0).split(" "))
            {
                customerDetailsListView.getItems().add(s);
            }
            // Get the pets of the customer
            ArrayList<String> selectedCustomerPets =
                    DB.returns("select fld_animal_id,fld_animal_name,fld_animal_species,fld_animal_description " +
                                "from tbl_animals where fld_customer_id =" + selectedCustomerID);
            customerPetsListView.getItems().clear();
            for (String s:selectedCustomerPets)
            {
                customerPetsListView.getItems().add(s);
            }
            // Enable the selectPetButton and addPetButton
            addPetButton.setDisable(false);
        }
        // If the customer does not exist, display an error message on both the customerDetailsListView and the customerPetsListView
        catch (Exception e)
        {
            addPetButton.setDisable(true);
            customerDetailsListView.getItems().clear();
            customerPetsListView.getItems().clear();
            customerDetailsListView.getItems().add("No customer found");
            customerPetsListView.getItems().add("No pets found");
        }
    }

    // Sets the listener for the customerPhoneNoTextField
    private void setCustomerPhoneNoTextFieldListener()
    {

        // If the value of customerPhoneNoTextField is empty, disable the searchCustomerButton
        // Otherwise, enable it
        customerPhoneNumberTextField.textProperty().addListener((observable, oldValue, newValue) ->
                searchCustomerButton.setDisable(newValue.trim().isEmpty()));
    }
}
