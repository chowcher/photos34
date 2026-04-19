package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Album;
import model.DataManager;
import model.Photo;
import model.User;
import model.UserManager;

import java.io.File;
import java.util.Optional;

/**
 * @author Charles Eshelman and Ryan Lilly
 */
public class AlbumController {

    @FXML private ListView<Photo> photoListView;
    @FXML private Label albumNameLabel;

    private User  currentUser;
    private Album currentAlbum;
    private ObservableList<Photo> observablePhotos;

    /**
     * Sets the user and album and loads the photo list.
     * @param user  the logged-in {@link User}
     * @param album the {@link Album} being viewed
     */
    public void setContext(User user, Album album) {
        this.currentUser  = user;
        this.currentAlbum = album;
        albumNameLabel.setText(album.getName());
        observablePhotos = FXCollections.observableArrayList(album.getPhotos());
        photoListView.setItems(observablePhotos);
        setupCellFactory();
    }

    /**
     * Add javadocs comment
     */
    private void setupCellFactory() {
        photoListView.setCellFactory(lv -> new ListCell<>() {
            private final ImageView thumbnail = new ImageView();
            private final Label     caption   = new Label();
            private final VBox      cell      = new VBox(4, thumbnail, caption);

            {
                thumbnail.setFitWidth(100);
                thumbnail.setFitHeight(100);
                thumbnail.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(Photo photo, boolean empty) {
                super.updateItem(photo, empty);
                if (empty || photo == null) {
                    setGraphic(null);
                } else {
                    try {
                        File f = new File(photo.getFilePath());
                        thumbnail.setImage(
                                new Image(f.toURI().toString(), 100, 100, true, true));
                    } catch (Exception e) {
                        thumbnail.setImage(null);
                    }
                    caption.setText(photo.getCaption().isBlank()
                            ? "(no caption)" : photo.getCaption());
                    setGraphic(cell);
                }
            }
        });
    }

    /**
     * Syncs photo list with the album.
     */
    private void refreshList() {
        observablePhotos.setAll(currentAlbum.getPhotos());
    }

    /**
     * Handles the Add Photo button.
     * Opens a {@link FileChooser}.
     * Creates a {@link Photo} from the file and adds it to the album.
     */
    @FXML
    private void handleAddPhoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Photo");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp"));

        File file = chooser.showOpenDialog(photoListView.getScene().getWindow());
        if (file == null) return;

        try {
            Photo photo = new Photo(file.getAbsolutePath());
            boolean added = currentAlbum.addPhoto(photo);
            if (!added) {
                showAlert("That photo is already in this album.");
            } else {
                refreshList();
                DataManager.saveUsers(UserManager.getInstance());
            }
        } catch (IllegalArgumentException e) {
            showAlert("Could not load photo: " + e.getMessage());
        }
    }

    /**
     * Handles the Remove Photo button.
     * The {@link Photo} object stays in other
     * albums that reference it.
     */
    @FXML
    private void handleRemovePhoto() {
        Photo selected = photoListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a photo to remove.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Photo");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Remove this photo from the album? " +
                "It will not be deleted from other albums.");

        Optional<ButtonType> response = confirm.showAndWait();
        if (response.isPresent() && response.get() == ButtonType.OK) {
            currentAlbum.removePhoto(selected);
            refreshList();
            DataManager.saveUsers(UserManager.getInstance());
        }
    }

    /**
     * Handles caption button.
     */
    @FXML
    private void handleCaption() {
        Photo selected = photoListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a photo to caption.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getCaption());
        dialog.setTitle("Caption Photo");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter caption:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(caption -> {
            selected.setCaption(caption);
            refreshList();
            DataManager.saveUsers(UserManager.getInstance());
        });
    }

    /**
     * Handles display button.
     */
    @FXML
    private void handleDisplayPhoto() {
        Photo selected = photoListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a photo to display.");
            return;
        }
        loadPhotoDisplayScene(selected);
    }

    /**
     * Opens the photo display screen based on double-click.
     *
     * @param event the mouse event fired by the ListView
     */
    @FXML
    private void handlePhotoDoubleClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            Photo selected = photoListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                loadPhotoDisplayScene(selected);
            }
        }
    }

    /**
     * Handles the Add Tag button.
     */
    @FXML
    private void handleAddTag() {
        Photo selected = photoListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a photo to tag.");
            return;
        }
        loadTagScene(selected);
    }

    /**
     * Handles "delete tag" button.
     * Goes to the tag editor screen (which handles both add and delete)
     */
    @FXML
    private void handleDeleteTag() {
        Photo selected = photoListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a photo to manage tags.");
            return;
        }
        loadTagScene(selected);
    }

    /**
     * Handles the copy button.
     * Goes to the copy/move screen in copy mode for the selected photo.
     */
    @FXML
    private void handleCopyPhoto() {
        Photo selected = photoListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a photo to copy.");
            return;
        }
        loadCopyMoveScene(selected, false);
    }

    /**
     * Handles move button.
     * Navigates to copy/move screen for the selected photo.
     */
    @FXML
    private void handleMovePhoto() {
        Photo selected = photoListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a photo to move.");
            return;
        }
        loadCopyMoveScene(selected, true);
    }

    /**
     * Handles the slideshow button.
     * Opens the slideshow screen starting at the currently selected photo (or first photo if none selected.)
     * Shows an error if there are no photos.
     */
    @FXML
    private void handleSlideshow() {
        if (currentAlbum.getPhotoCount() == 0) {
            showAlert("This album has no photos to show.");
            return;
        }

        int startIndex = photoListView.getSelectionModel().getSelectedIndex();
        if (startIndex < 0) startIndex = 0;

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/slideshow.fxml"));
            Parent root = loader.load();

            SlideshowController controller = loader.getController();
            controller.setContext(currentUser, currentAlbum, startIndex);

            Stage stage = (Stage) photoListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Slideshow — " + currentAlbum.getName());
            stage.show();
        } catch (Exception e) {
            showAlert("Failed to open slideshow.");
        }
    }

    /**
     * Handles back button and saves data, returning to the album list screen
     */
    @FXML
    private void handleBack() {
        DataManager.saveUsers(UserManager.getInstance());
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/albumList.fxml"));
            Parent root = loader.load();

            AlbumListController controller = loader.getController();
            controller.setUser(currentUser);

            Stage stage = (Stage) photoListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Photos");
            stage.show();
        } catch (Exception e) {
            showAlert("Failed to return to album list.");
        }
    }

    /**
     * Loads the photo display scene.
     * @param photo the {@link Photo} to display
     */
    private void loadPhotoDisplayScene(Photo photo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/photoDisplay.fxml"));
            Parent root = loader.load();

            PhotoDisplayController controller = loader.getController();
            controller.setContext(currentUser, currentAlbum, photo);

            Stage stage = (Stage) photoListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Photo — " + (photo.getCaption().isBlank()
                    ? "Untitled" : photo.getCaption()));
            stage.show();
        } catch (Exception e) {
            showAlert("Failed to open photo display.");
        }
    }

    /**
     * Loads the tag editor scene.
     * @param photo the {@link Photo} whose tags will be edited
     */
    private void loadTagScene(Photo photo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/tagEditor.fxml"));
            Parent root = loader.load();

            TagController controller = loader.getController();
            controller.setContext(currentUser, currentAlbum, photo);

            Stage stage = (Stage) photoListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tags — " + (photo.getCaption().isBlank()
                    ? "Photo" : photo.getCaption()));
            stage.show();
        } catch (Exception e) {
            showAlert("Failed to open tag editor.");
        }
    }

    /**
     * Loads the copy/move scene.
     * @param photo  the {@link Photo} to copy or move
     * @param isMove {@code true} to move, {@code false} to copy
     */
    private void loadCopyMoveScene(Photo photo, boolean isMove) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/copyMove.fxml"));
            Parent root = loader.load();

            CopyMoveController controller = loader.getController();
            controller.setContext(currentUser, currentAlbum, photo, isMove);

            Stage stage = (Stage) photoListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(isMove ? "Move Photo" : "Copy Photo");
            stage.show();
        } catch (Exception e) {
            showAlert("Failed to open copy/move dialog.");
        }
    }

    /**
     * Displays an error {@link Alert} with the given message.
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
