package photos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import photos.model.User;
import photos.model.UserManager;

/**
 * @author Charles Eshelman and Ryan Lilly
 */
public class LoginController {

    @FXML private TextField usernameField;

    /**
     * Handles login button.
     * Trims and lowercases the username, looks it up in
     * {@link UserManager}, and loads the scene that fits based on those results.
     * Displays an {@link Alert} if the username is blank or not found.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim().toLowerCase();

        if (username.isBlank()) {
            showAlert("Please enter a username.");
            return;
        }

        UserManager manager = UserManager.getInstance();

        if (username.equals("admin")) {
            loadScene("/photos/view/admin.fxml", null);
            return;
        }

        User user = manager.getUser(username);
        if (user == null) {
            showAlert("User '" + username + "' not found.");
            return;
        }

        loadScene("/photos/view/albumList.fxml", user);
    }

    /**
     * Loads a new scene.
     * If the destination controller is an {@link AlbumListController},
     *
     * @param fxmlPath the classpath-relative path to the FXML resource
     * @param user     the authenticated User, or {@code null} for admin
     */
    private void loadScene(String fxmlPath, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (user != null) {
                Object controller = loader.getController();
                if (controller instanceof AlbumListController) {
                    ((AlbumListController) controller).setUser(user);
                }
            }

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Photos");
            stage.show();

        } catch (Exception e) {
            showAlert("Failed to load scene: " + fxmlPath);
        }
    }

    /**
     * Displays an error {@link Alert} with the message parameter.
     * @param message the error text to display
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
