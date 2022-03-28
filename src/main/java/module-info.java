module com.animal_shelter {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;


    opens com.animal_shelter to javafx.fxml;
    exports com.animal_shelter;
}