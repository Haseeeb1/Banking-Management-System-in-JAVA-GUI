module com.example.banking_managemen_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.banking_managemen_system to javafx.fxml;
    exports com.example.banking_managemen_system;
}