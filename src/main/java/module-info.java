module com.mycompany.parkingsimulation {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.parkingsimulation to javafx.fxml;
    exports com.mycompany.parkingsimulation;
}
