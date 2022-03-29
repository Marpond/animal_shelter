package com.animal_shelter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application
{
    public static Stage stage;
    @Override
    public void start(Stage primaryStage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("booking.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = primaryStage;
        stage.setTitle("booking");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}