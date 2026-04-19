module javafxproject {
    requires javafx.controls;
    requires javafx.fxml;

    opens controller to javafx.fxml;
    opens app to javafx.graphics;
    opens model to javafx.fxml;
    opens model.data;
}
