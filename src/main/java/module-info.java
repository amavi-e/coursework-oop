module com.example.courseworkoop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;


    opens com.example.courseworkoop to javafx.fxml;
    exports com.example.courseworkoop;
}