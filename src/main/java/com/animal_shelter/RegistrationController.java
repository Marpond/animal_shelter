package com.animal_shelter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.util.ArrayList;

//TODO:
// Needed functions: addPet, receipt.
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
    private Button paymentButton;
    @FXML
    private Text customerSelectionConfirmationText;
    @FXML
    private Text petSelectionConfirmationText;

    public static String selectedCustomerName;
    public static int selectedCustomerID;
    public static int selectedPetID;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources)
    {
        // Default values
        searchCustomerButton.setDisable(true);
        customerDetailsListView.setDisable(true);
        addPetButton.setDisable(true);
        paymentButton.setDisable(true);
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
    private void popupConfirmPayment()
    {
        SceneController.popup("confirmPayment");
    }
    @FXML
    private void popupAddCustomer()
    {
        SceneController.popup("addCustomer");
    }

    @FXML
    private void popupAddPet()
    {
        SceneController.popup("addPet");
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
                // Enable the paymentButton
                paymentButton.setDisable(false);
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
            // Disable the paymentButton
            paymentButton.setDisable(true);
            // Get the customer ID and name
            String query = String.format("select fld_customer_id, fld_customer_name from " +
                                        "tbl_customers where fld_customer_phone_number = '%s'", phoneNo);
            System.out.println(query);
            ArrayList<String> customerIDAndName = DB.returns(query);
            // Set the customer ID and name
            selectedCustomerID = Integer.parseInt(customerIDAndName.get(0).split(DB.getDELIMITER())[0]);
            selectedCustomerName = customerIDAndName.get(0).split(DB.getDELIMITER())[1];
            // At this point, the customer has already been selected
            // Set the text of the customerSelectionConfirmationText
            customerSelectionConfirmationText.setText(String.format("Customer %s selected.", selectedCustomerName));
            setCustomerDetailsListView(phoneNo);
            setCustomerPetsListView();
            // Enable the selectPetButton and addPetButton
            addPetButton.setDisable(false);
        }
        // If the customer does not exist, display an error message on both the customerDetailsListView and the customerPetsListView
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            addPetButton.setDisable(true);
            customerDetailsListView.getItems().clear();
            customerPetsListView.getItems().clear();
            customerDetailsListView.getItems().add("No customer found");
            customerPetsListView.getItems().add("No pets found");
        }
    }

    private void setCustomerPetsListView() {
        // Get the pets of the customer
        String query = String.format("select fld_animal_id, fld_animal_name, fld_animal_species, fld_animal_description " +
                                    "from tbl_animals where fld_customer_id = %d", selectedCustomerID);
        ArrayList<String> selectedCustomerPets = DB.returns(query);
        customerPetsListView.getItems().clear();
        // Add the pets to the customerPetsListView
        for (String s:selectedCustomerPets)
        {
            customerPetsListView.getItems().add(s.replace(DB.getDELIMITER(), " "));
        }
    }

    /**
     * Sets the customer details list view
     * @param phoneNo The phone number of the customer
     */
    private void setCustomerDetailsListView(String phoneNo) {
        // Get the details of the customer
        String query = String.format("select fld_customer_name, fld_customer_phone_number, fld_customer_address from " +
                                    "tbl_customers where fld_customer_phone_number = '%s'", phoneNo);
        ArrayList<String> selectedCustomerDetails =
                DB.returns(query);
        customerDetailsListView.getItems().clear();
        // Add the details to the customerDetailsListView
        for (String s:selectedCustomerDetails.get(0).split(DB.getDELIMITER()))
        {
            customerDetailsListView.getItems().add(s);
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
