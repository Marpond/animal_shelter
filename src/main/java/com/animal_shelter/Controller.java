package com.animal_shelter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    private final HashMap<Integer, String> sizes = new HashMap<>();
    private final Database DB = new Database();

    @FXML
    private ListView<String> cageListView;
    @FXML
    private ListView<String> datesListView;
    @FXML
    private Text selectedCageText;

    private int selectedCageID;
    private String selectedCageSize;
    private double selectedCagePrice;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        // Set the size values of the hash map
        sizes.put(1, "Small");
        sizes.put(2, "Medium");
        sizes.put(3, "Large");
        // Set the listeners
        setCageList();
        setCageListListener();
    }

    // Creates a calendar by adding a year worth of days to the current day since epoch
    private ArrayList<Integer> createCalendar()
    {
        ArrayList<Integer> calendarDates = new ArrayList<>();
        int currentDay = Integer.parseInt(DB.returns("get_days_since_epoch").get(0));
        for (int i = 0; i <= 100; i++)
        {
            calendarDates.add(currentDay + i);
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
        // Creates a calendar
        createCalendar();
        // Clears the list view
        datesListView.getItems().clear();
        // Adds the dates to the list view
        for (int i:createCalendar())
        {
            datesListView.getItems().add(String.valueOf(i));
        }
        // Remove booked dates
        for (String s:DB.returns("select fld_booking_start, fld_booking_end from tbl_bookings where fld_Cage_id = " + cageID))
        {
            for (int i = Integer.parseInt(s.split(" ")[0]); i <= Integer.parseInt(s.split(" ")[1]); i++)
            {
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
                // Get the cage ID
                selectedCageID = Integer.parseInt(newValue.split(" ")[2]);
                // Set the dates list view with the selected cage ID
                setDatesList(selectedCageID);
                // Get the cage price
                selectedCagePrice = Double.parseDouble(
                        DB.returns("select fld_cage_price_per_day from tbl_cages where fld_cage_id = " + selectedCageID).get(0));
                // Get the cage size
                selectedCageSize = sizes.get(Integer.parseInt(newValue.split(" ")[2]));
                // Set the selected cage text
                selectedCageText.setText(String.format(
                        """
                        Selected cage: %s
                        Size: %s
                        Pricing: %.2f
                        """,
                        selectedCageID,
                        selectedCageSize,
                        selectedCagePrice));
            }
        });
    }
}