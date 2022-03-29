package com.animal_shelter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class BookingController implements Initializable
{
    private final int DATE_LIST_LENGTH = 56;

    private final HashMap<Integer, String> SIZES = new HashMap<>();
    private final Database DB = new Database();

    // FXML variables
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
    @FXML
    private Button proceedButton;

    private String selectedDate;
    private String selectedStartDate;
    private String selectedEndDate;

    private int selectedCageID;
    private String selectedCageSize;
    private double selectedCagePrice;

    private double totalPrice;

    private final ArrayList<Long> epochDates = getDates(DATE_LIST_LENGTH);
    private final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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
        // Disable proceed button
        proceedButton.setDisable(true);
    }

    @FXML
    private void resetBooking()
    {
        SceneController.switchTo("booking");
    }
    @FXML
    private void switchToRegistration()
    {
        SceneController.switchTo("registration");
    }

    // Formats an epoch long to a date string with the format yyyy-MM-dd
    private String formatEpochToDate(long epoch)
    {
        return sdf.format(new Date(epoch * 1000));
    }

    // Parses a string date with the format yyyy-MM-dd to an epoch long
    private long parseDateToEpoch(String date)
    {
        try {return sdf.parse(date).getTime() / 1000;}
        catch (Exception e) {return 0;}
    }
    /**
     * Creates an arraylist of epoch seconds, each element represents a day in the future since today
     * @param length The length of the arraylist
     * @return An arraylist of longs representing the epoch seconds of the future days
     */
    private ArrayList<Long> getDates(int length)
    {
        ArrayList<Long> temp = new ArrayList<>();
        // Get the current date with an SQL query
        long currentDay = Long.parseLong(DB.returns("get_seconds_since_epoch").get(0));
        // Add the epoch seconds of the future days to the arraylist by incrementing the current day
        for (int i = 0; i <= length; i++) {temp.add(currentDay + ((long) i *60*60*24));}
        return temp;
    }

    // Sets the values of the list view to the values of the database
    private void setCageList()
    {
        cageListView.getItems().clear();
        for (String s : DB.returns("select fld_Cage_ID from tbl_cages"))
        {
            cageListView.getItems().add(String.format("Cage no. %s", s));
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
            // Convert the epoch date to a readable date and add it to the list view
            datesListView.getItems().add(formatEpochToDate(i));
        }
        // Remove booked dates
        for (String s:DB.returns("select fld_booking_start, fld_booking_end from tbl_bookings where fld_Cage_id = " + cageID))
        {
            int startIndex;
            int endIndex;
            // Get the index of the start date
            for (startIndex = 0; startIndex < epochDates.size(); startIndex++)
            {
                // If the formatted epoch is equal to the selected start date, break
                if (formatEpochToDate(epochDates.get(startIndex)).equals(s.split(" ")[0])) {break;}
            }
            // Get the index of the end date
            for (endIndex = 0; endIndex < epochDates.size(); endIndex++)
            {
                // If the formatted epoch is equal to the selected end date, break
                if (formatEpochToDate(epochDates.get(endIndex)).equals(s.split(" ")[1])) {break;}
            }
            // Remove the dates between the start and end date
            for (int i = startIndex; i <= endIndex; i++)
            {
                datesListView.getItems().remove(formatEpochToDate(epochDates.get(i)));
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
                selectedDate = newValue;
            }
        });
    }

    // Sets the listener for the start date buttons
    private void setDateButtonListener()
    {
        // Set the listener for the start date button
        selectStartButton.setOnAction(event ->
        {
            if (selectedDate != null)
            {
                // Set the start date
                selectedStartDate = selectedDate;
                // Disable the button
                selectStartButton.setDisable(true);
                // Enable the end button
                selectEndButton.setDisable(false);
                // Set the start text
                startText.setText(String.format("Start date: %s", selectedStartDate));
                // If the date is before the start date, remove it
                datesListView.getItems().removeIf(s -> s.compareTo(selectedStartDate) < 0);
            }
        });
        // Set the listener for the end date button
        selectEndButton.setOnAction(event ->
        {
            if (selectedDate != null)
            {
                // Set the end date
                selectedEndDate = selectedDate;
                // Disable the button
                selectEndButton.setDisable(true);
                // Set the end text
                endText.setText(String.format("End date: %s", selectedEndDate));
                // Parse the dates to epoch
                long startEpoch = parseDateToEpoch(selectedStartDate);
                long endEpoch = parseDateToEpoch(selectedEndDate);
                // Calculate the total price
                totalPrice = ((endEpoch - startEpoch) / 86400 +1)* selectedCagePrice;
                // Set the total price text
                priceText.setText(String.format("Total price: %.2f", totalPrice));
                // Enable the proceed button
                proceedButton.setDisable(false);
            }
        });
    }
}