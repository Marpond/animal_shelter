package com.animal_shelter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable
{

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Database db = new Database();
        try {
            ArrayList<String> asd = db.select("SELECT fld_cage_price_per_day FROM tbl_Cages");
            System.out.println(asd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}