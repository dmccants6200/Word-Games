module com.wordgames {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.wordgames to javafx.fxml;
    exports com.wordgames;
}