module com.example.trafficsimulator {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.trafficsimulator to javafx.fxml;
    exports com.example.trafficsimulator;
    exports com.example.trafficsimulator.Controller;
    exports com.example.trafficsimulator.Model;
    opens com.example.trafficsimulator.Controller to javafx.fxml;
}