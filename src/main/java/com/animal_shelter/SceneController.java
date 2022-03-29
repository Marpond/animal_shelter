package com.animal_shelter;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Objects;

public class SceneController
{
    /**
     * Public method to load a scene.
     * @param fxmlName the name of the fxml file to be loaded
     */
    public static void switchTo(String fxmlName)
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
        catch (IOException e){e.printStackTrace();}
    }
}