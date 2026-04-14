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
 * Controller for the login screen (login.fxml).
 * Validates the username and routes the user to the appropriate
 * subsystem: admin panel or the album list view.
 *
 * @author Charles Eshelman
 * @author Ryan Lilly
 */
public class LoginController {

    @FXML private TextField usernameField;

    /**
     * Handles the Login button action.
     * Reads the username field, looks up the user in UserManager,
     * and loads the appropriate scene. Shows an Alert on failure.
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
     * Loads a new scene into the primary stage.
     * Passes the logged-in User to the destination controller
     * if the controller implements user injection via setUser().
     *
     * @param fxmlPath path to the FXML resource
     * @param user     the User to pass, or null for admin
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
     * Displays an error Alert with the given message.
     *
     * @param message the error message to display
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}