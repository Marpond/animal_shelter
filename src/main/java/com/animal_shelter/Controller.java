package com.animal_shelter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    private final HashMap<Integer, String> SIZES = new HashMap<>();
    private final Database DB = new Database();

    @FXML
    private ListView<String> cageListView;
    @FXML
    private ListView<String> datesListView;
    @FXML
    private Text cageText;
    @FXML
    private Text startText;
    @FXML
    private Text endText;
    @FXML
    private Text priceText;
    @FXML
    private Button selectStartButton;
    @FXML
    private Button selectEndButton;

    private int selectedDate = 0;
    private int selectedStartDate = 0;
    private int selectedEndDate = 0;

    private final int DATE_LIST_LENGTH = 100;

    private int selectedCageID;
    private String selectedCageSize;
    private double selectedCagePrice;

    private double totalPrice;

    private final ArrayList<Long> epochDates = getDates(DATE_LIST_LENGTH);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        // Set the size values of the hash map
        SIZES.put(1, "Small");
        SIZES.put(2, "Medium");
        SIZES.put(3, "Large");
        // Set the listeners
        setCageList();
        setCageListListener();
        setDatesListListener();
        setDateButtonListener();
    }

    /**
     * Creates an arraylist of epoch seconds, each element represents a day in the future since today
     * @param length The length of the arraylist
     * @return An arraylist of integers
     */
    private ArrayList<Long> getDates(int length)
    {
        ArrayList<Long> calendarDates = new ArrayList<>();
        long currentDay = Long.parseLong(DB.returns("get_seconds_since_epoch").get(0));
        for (int i = 0; i <= length; i++)
        {
            calendarDates.add(currentDay + ((long) i *60*60*24));
        }
        return calendarDates;
    }

    // Sets the values of the list view to the values of the database
    private void setCageList()
    {
        cageListView.getItems().clear();
        for (String s : DB.returns("select * from tbl_cages"))
        {
            cageListView.getItems().add(String.format("Cage no. %s", s.split(" ")[0]));
        }
    }

    /**
     * Sets the values of the dates list view according to the selected cage
     * @param cageID The ID of the cage
     */
    private void setDatesList(int cageID)
    {
        // Clears the list view
        datesListView.getItems().clear();
        // Adds the dates to the list view
        for (long i:epochDates)
        {
            // Convert the epoch date to a readable date
            Date d = new Date( i * 1000 );
            datesListView.getItems().add(String.valueOf(d));
        }
        // Remove booked dates
        for (String s:DB.returns("select fld_booking_start, fld_booking_end from tbl_bookings where fld_Cage_id = " + cageID))
        {
            for (int i = Integer.parseInt(s.split(" ")[0]); i <= Integer.parseInt(s.split(" ")[1]); i++)
            {
                /*
                TODO: Fix tbl_Bookings epoch dates, they are not correct (to long!!!)
                 This tries to delete an epoch value from a humanly readable date list
                 We have to convert the deletable epoch values to humanly readable dates
                 Preferably we should format the dates this way: Day Month Daynumber Year,
                 Then just get the string.valueOf(date) and remove it from the list
                 */

                datesListView.getItems().remove(String.valueOf(i));
            }
        }
    }

    // Sets the listener for the cage list view
    private void setCageListListener()
    {
        cageListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue != null)
            {
                // Enable start button
                selectStartButton.setDisable(false);
                // Disable end button
                selectEndButton.setDisable(true);
                // Get the cage ID
                selectedCageID = Integer.parseInt(newValue.split(" ")[2]);
                // Set the dates list view with the selected cage ID
                setDatesList(selectedCageID);
                // Get the cage price
                selectedCagePrice = Double.parseDouble(
                        DB.returns("select fld_cage_price_per_day from tbl_cages where fld_cage_id = " + selectedCageID).get(0));
                // Get the cage size
                selectedCageSize = SIZES.get(Integer.parseInt(newValue.split(" ")[2]));
                // Set the selected cage text
                cageText.setText(String.format(
                        """
                        Selected cage: %s
                        Size: %s
                        Pricing: %.2f /day
                        """,
                        selectedCageID,
                        selectedCageSize,
                        selectedCagePrice));
            }
        });
    }

    // Sets the listener for the dates list view
    private void setDatesListListener()
    {
        datesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue != null)
            {
                selectedDate = Integer.parseInt(newValue);
            }
        });
    }

    // Sets the listener for the start date buttons
    private void setDateButtonListener()
    {
        // Set the listener for the start date button
        selectStartButton.setOnAction(event ->
        {
            if (selectedDate != 0)
            {
                // Set the start date
                selectedStartDate = selectedDate;
                // Disable the button
                selectStartButton.setDisable(true);
                // Enable the end button
                selectEndButton.setDisable(false);
                // Set the start text
                startText.setText(String.format("Start date: %s", selectedStartDate));
                for (int i = Integer.parseInt(DB.returns("get_days_since_epoch").get(0)); i < selectedStartDate; i++)
                {
                    // Remove the date from the list view
                    datesListView.getItems().remove(String.valueOf(i));
                }
            }
        });
        // Set the listener for the end date button
        selectEndButton.setOnAction(event ->
        {
            if (selectedDate != 0)
            {
                // Set the end date
                selectedEndDate = selectedDate;
                // Disable the button
                selectEndButton.setDisable(true);
                // Set the end text
                endText.setText(String.format("End date: %s", selectedEndDate));
                // Calculate the total price
                totalPrice = (selectedEndDate - selectedStartDate) * selectedCagePrice;
                // Set the total price text
                priceText.setText(String.format("Total price: %.2f", totalPrice));
            }
        });
    }
}