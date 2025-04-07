module com.example.pt2025_30422_larisa_pasca_assignment_2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.pt2025_30422_larisa_pasca_assignment_2 to javafx.fxml;
    exports com.example.pt2025_30422_larisa_pasca_assignment_2;
    exports GUI;
    exports businessLogic;
    exports dataModel;
}