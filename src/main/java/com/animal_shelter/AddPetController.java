package com.animal_shelter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class AddPetController implements Initializable
{
    private final Database DB = new Database();
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField speciesTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private Button addPetButton;
    @FXML
    private Text addConfirmationText;

    private String name;
    private String species;
    private String description;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        addPetButton.setDisable(true);
        setListeners();
    }

    @FXML
    private void addPet()
    {
        System.out.printf("insert_animal %d, '%s', '%s', '%s'\n",
                RegistrationController.selectedCustomerID, name, species, description);
        // Add pet to database
        DB.executes(String.format("insert_animal %d, '%s', '%s', '%s'",
                RegistrationController.selectedCustomerID, name, species, description));
        // Confirmation text
        addConfirmationText.setText("Pet added!");
    }

    // Set listeners for the text fields
    // If any of the text fields are empty, or the thresholds are exceeded, disable the add button
    private void setListeners()
    {
        nameTextField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            addPetButton.setDisable(newValue.length()>30 || areTextFieldsEmpty() || RegistrationController.checkForIllegalCharacters(newValue));
            name = newValue;
        });
        speciesTextField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            addPetButton.setDisable(newValue.length()>15 || areTextFieldsEmpty() || RegistrationController.checkForIllegalCharacters(newValue));
            species = newValue;
        });
        descriptionTextField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            addPetButton.setDisable(areTextFieldsEmpty() || RegistrationController.checkForIllegalCharacters(newValue));
            description = newValue;
        });
    }

    // Check if any of the text fields are empty
    private boolean areTextFieldsEmpty()
    {
        return nameTextField.getText().isEmpty() ||
               speciesTextField.getText().isEmpty() ||
               descriptionTextField.getText().isEmpty();
    }
}
