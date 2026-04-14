package photos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import photos.model.Album;
import photos.model.Photo;
import photos.model.User;

import java.io.File;
import java.util.List;

/**
 * @author Charles Eshelman and Ryan Lilly
 */
public class SlideshowController {

    @FXML private ImageView photoImageView;
    @FXML private Label captionLabel;
    @FXML private Label indexLabel;
    @FXML private Button prevButton;
    @FXML private Button nextButton;

    private User currentUser;
    private Album currentAlbum;
    private List<Photo> photos;
    private int currentIndex;

    /**
     * @param user the logged-in {@link User}
     * @param album the {@link Album} being shown
     * @param startIndex the index of the first displayed photo
     */
    public void setContext(User user, Album album, int startIndex) {
        this.currentUser = user;
        this.currentAlbum = album;
        this.photos = album.getPhotos();
        this.currentIndex = startIndex;
        displayCurrentPhoto();
    }

    /**
     * Displays the photo at {@link #currentIndex} and updates UI.
     * Disables the prev button when it's at index 0 and the next button
     * when at the final photo.
     */
    private void displayCurrentPhoto() {
        Photo photo = photos.get(currentIndex);

        try {
            File f = new File(photo.getFilePath());
            photoImageView.setImage(new Image(f.toURI().toString()));
            photoImageView.setPreserveRatio(true);
        } catch (Exception e) {
            photoImageView.setImage(null);
        }

        captionLabel.setText(photo.getCaption().isBlank() ? "(no caption)" : photo.getCaption());

        indexLabel.setText((currentIndex + 1) + " / " + photos.size());

        prevButton.setDisable(currentIndex == 0);
        nextButton.setDisable(currentIndex == photos.size() - 1);
    }

    /**
     * Handles the prev button.
     * Lowers {@link #currentIndex} and changes display accordingly.
     */
    @FXML
    private void handlePrev() {
        if (currentIndex > 0) {
            currentIndex--;
            displayCurrentPhoto();
        }
    }

    /**
     * Handles the next button.
     * Increases {@link #currentIndex} and changes display accordingly.
     */
    @FXML
    private void handleNext() {
        if (currentIndex < photos.size() - 1) {
            currentIndex++;
            displayCurrentPhoto();
        }
    }

    /**
     * Handles back button.
     * Returns to the album view screen for current album.
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/album.fxml"));
            Parent root = loader.load();

            AlbumController controller = loader.getController();
            controller.setContext(currentUser, currentAlbum);

            Stage stage = (Stage) photoImageView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(currentAlbum.getName());
            stage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to return to album.");
            alert.showAndWait();
        }
    }
}
