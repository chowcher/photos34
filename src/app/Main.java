package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.DataManager;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        DataManager.loadUsers(); // initializes UserManager.instance before any controller runs
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Photos");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
