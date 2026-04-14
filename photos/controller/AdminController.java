package photos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import photos.model.DataManager;
import photos.model.User;
import photos.model.UserManager;

import java.util.Optional;

/**
 * @author Charles Eshelman and Ryan Lilly
 */
public class AdminController {

    @FXML private ListView<User> userListView;

    private UserManager manager;
    private ObservableList<User> observableUsers;

    /**
     * Initializes the controller after the FXML is loaded.
     * Retrieves the {@link UserManager} instance and populates
     * the user {@link ListView} with all registered users.
     */
    @FXML
    public void initialize() {
        manager = UserManager.getInstance();
        observableUsers = FXCollections.observableArrayList(manager.getUsers());
        userListView.setItems(observableUsers);
    }

    /**
     * Handles Create User button.
     * Prompts admin for new username with {@link TextInputDialog}.
     * Doesn't allow blank names, admin, stock, or duplicates.
     */
    @FXML
    private void handleCreateUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create User");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter new username:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(username -> {
            if (username.isBlank()) {
                showAlert("Username cannot be blank.");
                return;
            }
            User created = manager.addUser(username.trim());
            if (created == null) {
                showAlert("Username '" + username + "' is already taken or reserved.");
            } else {
                observableUsers.setAll(manager.getUsers());
                DataManager.saveUsers(manager);
            }
        });
    }

    /**
     * Handles the Delete User button.
     * Confirms with {@link Alert}.
     */
    @FXML
    private void handleDeleteUser() {
        User selected = userListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a user to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Delete user '" + selected.getUsername() + "'? This cannot be undone.");

        Optional<ButtonType> response = confirm.showAndWait();
        if (response.isPresent() && response.get() == ButtonType.OK) {
            boolean removed = manager.removeUser(selected.getUsername());
            if (!removed) {
                showAlert("Could not delete '" + selected.getUsername() + "'.");
            } else {
                observableUsers.setAll(manager.getUsers());
                DataManager.saveUsers(manager);
            }
        }
    }

    /**
     * Handles logout button, saves data to disk, returns to login screen.
     */
    @FXML
    private void handleLogout() {
        DataManager.saveUsers(manager);
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/photos/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) userListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Photos — Login");
            stage.show();
        } catch (Exception e) {
            showAlert("Failed to return to login screen.");
        }
    }

    /**
     * Displays an error {@link Alert} with the given message.
     * @param message the error text to display
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Admin Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
