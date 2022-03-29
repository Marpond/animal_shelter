package com.animal_shelter;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneController
{
    /**
     * Public method to load a scene.
     * @param fxmlName the name of the fxml file to be loaded
     */
    public static void load(String fxmlName)
    {
        try
        {
            // Load fxml
            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneController.class.getResource(fxmlName+".fxml")));
            // Change to new scene
            Main.stage.setScene(new Scene(root));
            // Request focus so stuff works
            root.requestFocus();
            // Set title
            Main.stage.setTitle(fxmlName);
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public static void popup(String fxmlName)
    {
        try
        {
            // Load fxml
            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneController.class.getResource(fxmlName+".fxml")));
            // Change to new scene
            Stage popup = new Stage();
            popup.setScene(new Scene(root));
            // Make it non-collapsable and disable other windows
            popup.initModality(Modality.APPLICATION_MODAL);
            // Request focus so stuff works
            root.requestFocus();
            popup.setTitle(fxmlName);
            popup.setResizable(false);
            popup.show();
        }
        catch (IOException e) {e.printStackTrace();}
    }
}