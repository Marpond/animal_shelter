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
    private ListView<String> servicesListView;
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
    private Text serviceText;
    @FXML
    private Button selectStartButton;
    @FXML
    private Button selectEndButton;
    @FXML
    private Button proceedButton;
    @FXML
    private Button addServiceButton;
    @FXML
    private Button removeServiceButton;

    private String selectedDate;
    private int selectedServiceID;
    private double totalServicePrice = 0;

    public static String selectedStartDate;
    public static String selectedEndDate;
    public static int selectedCageID;
    public static String selectedCageSize;
    public static double selectedCagePrice;
    public static double totalPrice;
    public static int totalDays;

    public static ArrayList<Integer> selectedServiceIDs = new ArrayList<>();

    private final ArrayList<Long> EPOCH_DATES = getDates(DATE_LIST_LENGTH);
    private final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final ArrayList<String> SERVICE_NAMES = new ArrayList<>();

    /**
     * Initializes the controller class, this method is automatically called after the fxml file has been loaded
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        // Reset static variables
        selectedStartDate = "";
        selectedEndDate = "";
        selectedCageID = 0;
        selectedCageSize = "";
        selectedCagePrice = 0;
        totalPrice = 0;
        totalDays = 0;
        selectedServiceIDs.clear();
        // Set the size values of the hash map
        SIZES.put(1, "Small");
        SIZES.put(2, "Medium");
        SIZES.put(3, "Large");
        // Set the listeners
        setCageListView();
        setServiceListView();
        setCageListListener();
        setDatesListListener();
        setServiceListListener();
        // Disable the buttons
        proceedButton.setDisable(true);
        selectStartButton.setDisable(true);
        selectEndButton.setDisable(true);
        addServiceButton.setDisable(true);
        removeServiceButton.setDisable(true);
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
        System.out.println("Selected Cage ID: "     + selectedCageID);
        System.out.println("Selected Cage Size: "   + selectedCageSize);
        System.out.println("Selected Cage Price: "  + selectedCagePrice);
        System.out.println("Selected Start Date: "  + selectedStartDate);
        System.out.println("Selected End Date: "    + selectedEndDate);
        System.out.println("Total Price: "          + totalPrice);
        System.out.println("Total Days: "           + totalDays);
        System.out.println("Selected Service IDs: " + selectedServiceIDs);

    }

    @FXML
    private void selectStartDate()
    {
        if (selectedDate != null)
        {
            // Disable the cage list view
            cageListView.setDisable(true);
            // Enable the select end button
            selectEndButton.setDisable(false);
            // Set the start date
            selectedStartDate = selectedDate;
            // Set the start text
            startText.setText(String.format("Start date: %s", selectedStartDate));
            // If the date is before the start date, remove it
            datesListView.getItems().removeIf(s -> s.compareTo(selectedStartDate) < 0);
        }
    }

    @FXML
    private void selectEndDate()
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
        totalPrice = totalDays * selectedCagePrice + totalServicePrice;
        // Set the total days text
        totalDaysText.setText(String.format("Days: %d", totalDays));
        // Set the total price text
        priceText.setText(String.format("Total price\tkr. %.2f", totalPrice));
        // Enable the proceed button
        proceedButton.setDisable(false);
        // Disable the select start button and select end button
        selectStartButton.setDisable(true);
        selectEndButton.setDisable(true);
    }

    @FXML
    private void selectService()
    {
        // Disable the add service button
        addServiceButton.setDisable(true);
        // Enable the remove service button
        removeServiceButton.setDisable(false);
        // Add the selected service to the list
        selectedServiceIDs.add(selectedServiceID);
        // Get the price of the selected service
        String query = String.format("select fld_service_price from " +
                                    "tbl_extra_services where fld_service_id = %d", selectedServiceID);
        double currenServicePrice = Double.parseDouble(DB.returns(query).get(0));
        totalServicePrice += currenServicePrice;
        // Add the price to the total price
        BookingController.totalPrice += currenServicePrice;
        // Set the service text
        StringBuilder temp = new StringBuilder("Services:\n");
        for (int i: selectedServiceIDs)
        {
            temp.append(SERVICE_NAMES.get(i-1)).append("\n");
        }
        serviceText.setText(temp.toString());
        // Set the total price text
        priceText.setText(String.format("Total price\tkr. %.2f", BookingController.totalPrice));
    }

    @FXML
    private void removeService()
    {
        // Disable the remove service button
        removeServiceButton.setDisable(true);
        // Enable the add service button
        addServiceButton.setDisable(false);
        // Remove the service from the list
        selectedServiceIDs.remove((Integer) selectedServiceID);
        // Get the price of the selected service
        String query = String.format("select fld_service_price from " +
                                    "tbl_extra_services where fld_service_id = %d", selectedServiceID);
        double currentServicePrice = Double.parseDouble(DB.returns(query).get(0));
        totalServicePrice -= currentServicePrice;
        // Subtract the price from the total price
        totalPrice -= currentServicePrice;
        // Set the service text
        StringBuilder temp = new StringBuilder("Services:\n");
        for (int i: selectedServiceIDs)
        {
            temp.append(SERVICE_NAMES.get(i-1)).append("\n");
        }
        serviceText.setText(temp.toString());
        // Set the total price text
        priceText.setText(String.format("Total price\tkr. %.2f", totalPrice));
    }

    /**
     * Formats an epoch long to a date string in the format yyyy-MM-dd
     * @param epoch The epoch long to format
     * @return The formatted date string
     */
    private String formatEpochToDate(long epoch)
    {
        return DATE_FORMAT.format(new Date(epoch * 1000));
    }

    /**
     * Parses a date string with the format yyyy-MM-dd to an epoch long
     * @param date The date string to parse
     * @return The epoch long of the date
     */
    private long parseDateToEpoch(String date)
    {
        try {return DATE_FORMAT.parse(date).getTime() / 1000;}
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

    private void setServiceListView()
    {
        // Get the services from the database
        ArrayList<String> services = DB.returns("select * from tbl_extra_services");
        // Add the services to the serviceListView
        for (String s: services)
        {
            SERVICE_NAMES.add(s.split(DB.getDELIMITER())[1]);
            servicesListView.getItems().add(String.format("%s\tkr. %s",
                                            s.split(DB.getDELIMITER())[1],
                                            s.split(DB.getDELIMITER())[2]));
        }
    }

    // Sets the values of the list view to the values of the database
    private void setCageListView()
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
    private void setDatesListView(int cageID)
    {
        datesListView.getItems().clear();
        // Adds the dates to the list view
        for (long i: EPOCH_DATES)
        {
            // Convert the epoch date to a readable date and add it to the list view
            datesListView.getItems().add(formatEpochToDate(i));
        }
        // Remove booked dates
        String query = String.format("select fld_booking_start, fld_booking_end from " +
                                    "tbl_bookings where fld_Cage_id = %d", cageID);
        for (String s:DB.returns(query))
        {
            int startIndex;
            int endIndex;
            // Get the index of the start date
            for (startIndex = 0; startIndex < EPOCH_DATES.size(); startIndex++)
            {
                // If the formatted epoch is equal to the selected start date, break
                if (formatEpochToDate(EPOCH_DATES.get(startIndex)).equals(s.split(DB.getDELIMITER())[0])) {break;}
            }
            // Get the index of the end date
            for (endIndex = 0; endIndex < EPOCH_DATES.size(); endIndex++)
            {
                // If the formatted epoch is equal to the selected end date, break
                if (formatEpochToDate(EPOCH_DATES.get(endIndex)).equals(s.split(DB.getDELIMITER())[1])) {break;}
            }
            // Remove the dates between the start and end date
            for (int i = startIndex; i <= endIndex; i++)
            {
                // Remove the date from the list view
                try {datesListView.getItems().remove(formatEpochToDate(EPOCH_DATES.get(i)));}
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
                selectedCageID = cageListView.getSelectionModel().getSelectedIndex() + 1;
                // Set the dates list view with the selected cage ID
                setDatesListView(selectedCageID);
                // Get the cage price
                String query = String.format("select fld_cage_price_per_day from " +
                                            "tbl_cages where fld_cage_id = %d",selectedCageID);
                selectedCagePrice = Double.parseDouble(DB.returns(query).get(0));
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
            // If a date is selected, set the selected date
            if (newValue != null) selectedDate = newValue;
        });
    }

    // Sets the listener for the service list view
    private void setServiceListListener()
    {
        servicesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            // Get the selected service ID
            selectedServiceID = servicesListView.getSelectionModel().getSelectedIndex()+1;
            // Enable the add service button
            addServiceButton.setDisable(selectedServiceIDs.contains(selectedServiceID));
            removeServiceButton.setDisable(!selectedServiceIDs.contains(selectedServiceID));
        });
    }
}