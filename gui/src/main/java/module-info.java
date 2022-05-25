module com.gui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.gui to javafx.fxml;
    exports com.gui;
}