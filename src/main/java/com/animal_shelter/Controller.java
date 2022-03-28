package com.animal_shelter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Controller implements Initializable
{

    private final Database DB = new Database();
    @FXML
    private ListView cageListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        createCalendar();
    }

    // Creates the calendar by adding a year worth of days to the current day since epoch
    private void createCalendar()
    {
        ArrayList<Integer> calendar = new ArrayList<>();
        int currentDay = Integer.parseInt(DB.returns("get_days_since_epoch").get(0));
        for (int i = 0; i <= 365; i++)
        {
            calendar.add(currentDay + i);
        }
        System.out.println(calendar);
    }

    // Sets the values of the list view to the values of the database
    private void setListView()
    {
        cageListView.getItems().clear();
        cageListView.getItems().addAll(Arrays.asList(DB.returns("select * from tbl_cages").get(0).split(" ")));
    }
}