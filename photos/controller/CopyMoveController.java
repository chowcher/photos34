package photos.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import photos.model.Album;
import photos.model.DataManager;
import photos.model.Photo;
import photos.model.User;
import photos.model.UserManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Charles Eshelman and Ryan Lilly
 */
public class CopyMoveController {

    @FXML private Label           titleLabel;
    @FXML private ListView<Album> albumListView;

    private User    currentUser;
    private Album   sourceAlbum;
    private Photo   targetPhoto;
    private boolean isMove;

    /**
     * populates destination album list.
     * Removes source album from list so a photo doesn't copy to
     * its own album location
     *
     * @param user   the logged-in {@link User}
     * @param source the {@link Album} the photo belongs to
     * @param photo  the {@link Photo} to copy or move
     * @param isMove {@code true} for a move operation, {@code false} for copy
     */
    public void setContext(User user, Album source, Photo photo, boolean isMove) {
        this.currentUser = user;
        this.sourceAlbum = source;
        this.targetPhoto = photo;
        this.isMove      = isMove;
        titleLabel.setText(isMove ? "Move photo to..." : "Copy photo to...");
        List<Album> eligible = user.getAlbums().stream()
                .filter(a -> !a.getName().equalsIgnoreCase(source.getName()))
                .collect(Collectors.toList());
        albumListView.setItems(FXCollections.observableArrayList(eligible));
    }

    /**
     * Handles confirm button.
     * Adds the photo to the selected target album.
     */
    @FXML
    private void handleConfirm() {
        Album target = albumListView.getSelectionModel().getSelectedItem();
        if (target == null) {
            showAlert("Please select a destination album.");
            return;
        }

        boolean added = target.addPhoto(targetPhoto);
        if (!added) {
            showAlert("That photo is already in '" + target.getName() + "'.");
            return;
        }

        if (isMove) {
            sourceAlbum.removePhoto(targetPhoto);
        }

        DataManager.saveUsers(UserManager.getInstance());
        returnToAlbum();
    }

    /**
     * Handles the cancel button.
     * Returns to source album with no changes.
     */
    @FXML
    private void handleCancel() {
        returnToAlbum();
    }

    /**
     * Goes back to the source album screen.
     */
    private void returnToAlbum() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/photos/view/album.fxml"));
            Parent root = loader.load();

            AlbumController controller = loader.getController();
            controller.setContext(currentUser, sourceAlbum);

            Stage stage = (Stage) albumListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(sourceAlbum.getName());
            stage.show();
        } catch (Exception e) {
            showAlert("Failed to return to album.");
        }
    }

    /**
     * Displays an error {@link Alert} with the message parameter,.
     * @param message the error text to display
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
