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
    private Text totalDaysText;
    @FXML
    private Button selectStartButton;
    @FXML
    private Button selectEndButton;
    @FXML
    private Button proceedButton;

    private String selectedDate;

    public static String selectedStartDate;
    public static String selectedEndDate;
    public static int selectedCageID;
    public static String selectedCageSize;
    public static double selectedCagePrice;
    public static double totalPrice;
    public static int totalDays;

    private final ArrayList<Long> epochDates = getDates(DATE_LIST_LENGTH);
    private final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Initializes the controller class, this method is automatically called after the fxml file has been loaded
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized
     */
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
        // Disable the buttons
        proceedButton.setDisable(true);
        selectStartButton.setDisable(true);
        selectEndButton.setDisable(true);
    }

    @FXML
    private void resetBooking()
    {
        SceneController.load("booking");
    }

    @FXML
    private void switchToRegistration()
    {
        SceneController.load("registration");
        // Print out the stuff for debugging
        System.out.println("Selected Cage ID: " + selectedCageID);
        System.out.println("Selected Cage Size: " + selectedCageSize);
        System.out.println("Selected Cage Price: " + selectedCagePrice);
        System.out.println("Selected Start Date: " + selectedStartDate);
        System.out.println("Selected End Date: " + selectedEndDate);
        System.out.println("Total Price: " + totalPrice);
        System.out.println("Total Days: " + totalDays);

    }

    /**
     * Formats an epoch long to a date string in the format yyyy-MM-dd
     * @param epoch The epoch long to format
     * @return The formatted date string
     */
    private String formatEpochToDate(long epoch)
    {
        return sdf.format(new Date(epoch * 1000));
    }

    /**
     * Parses a date string with the format yyyy-MM-dd to an epoch long
     * @param date The date string to parse
     * @return The epoch long of the date
     */
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
            // Add the cage ID to the list view
            cageListView.getItems().add(String.format("Cage no. %s", s));
        }
    }

    /**
     * Sets the values of the dates list view according to the selected cage
     * @param cageID The ID of the cage
     */
    private void setDatesList(int cageID)
    {
        datesListView.getItems().clear();
        // Adds the dates to the list view
        for (long i:epochDates)
        {
            // Convert the epoch date to a readable date and add it to the list view
            datesListView.getItems().add(formatEpochToDate(i));
        }
        // Remove booked dates
        for (String s:DB.returns("select fld_booking_start, fld_booking_end " +
                                "from tbl_bookings where fld_Cage_id = " + cageID))
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
                // Remove the date from the list view
                try {datesListView.getItems().remove(formatEpochToDate(epochDates.get(i)));}
                // If the date is not in the list view, do nothing
                catch (Exception ignored) {}
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
                // Get the cage ID
                selectedCageID = Integer.parseInt(newValue.split(" ")[2]);
                // Set the dates list view with the selected cage ID
                setDatesList(selectedCageID);
                // Get the cage price
                selectedCagePrice = Double.parseDouble(
                        DB.returns("select fld_cage_price_per_day " +
                                    "from tbl_cages where fld_cage_id = " + selectedCageID).get(0));
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
            // If a date is selected, enable the start button and the end button
            // Otherwise disable them
            selectStartButton.setDisable(newValue == null);
            selectEndButton.setDisable(newValue == null);
            // If a date is selected, set the selected date
            if (newValue != null) {selectedDate = newValue;}
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
                // Disable the cage list view
                cageListView.setDisable(true);
                // Set the start date
                selectedStartDate = selectedDate;
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
                // Disable the cage list view
                cageListView.setDisable(true);
                // Set the end date
                selectedEndDate = selectedDate;
                // Set the end text
                endText.setText(String.format("End date: %s", selectedEndDate));
                // Parse the dates to epoch
                long startEpoch = parseDateToEpoch(selectedStartDate);
                long endEpoch = parseDateToEpoch(selectedEndDate);
                // Calculate the total price
                totalDays =(int) (endEpoch - startEpoch) / 86400 + 1;
                totalPrice = totalDays * selectedCagePrice;
                // Set the total days text
                totalDaysText.setText(String.format("Days: %d", totalDays));
                // Set the total price text
                priceText.setText(String.format("Total price: %.2f", totalPrice));
                // Enable the proceed button
                proceedButton.setDisable(false);
            }
        });
    }
}