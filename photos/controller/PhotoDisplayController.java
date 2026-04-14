package photos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import photos.model.Album;
import photos.model.Photo;
import photos.model.Tag;
import photos.model.User;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * @author Charles Eshelman and Ryan Lilly
 */
public class PhotoDisplayController {

    @FXML private ImageView     photoImageView;
    @FXML private Label         captionLabel;
    @FXML private Label         dateLabel;
    @FXML private ListView<Tag> tagListView;

    private User  currentUser;
    private Album currentAlbum;
    private Photo currentPhoto;

    /**
     * Updates all UI fields for the given photo.
     * @param user  the logged-in {@link User}
     * @param album the {@link Album} this photo was opened from
     * @param photo the {@link Photo} to display
     */
    public void setContext(User user, Album album, Photo photo) {
        this.currentUser  = user;
        this.currentAlbum = album;
        this.currentPhoto = photo;

        try {
            File f = new File(photo.getFilePath());
            photoImageView.setImage(new Image(f.toURI().toString()));
            photoImageView.setPreserveRatio(true);
        } catch (Exception e) {
            captionLabel.setText("Image could not be loaded.");
        }

        captionLabel.setText(photo.getCaption().isBlank()
                ? "(no caption)" : photo.getCaption());

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy  hh:mm a");
        dateLabel.setText(sdf.format(photo.getCaptureDate().getTime()));

        tagListView.getItems().setAll(photo.getTags());
    }

    @FXML 
    private void handleBack() { //Handles back button
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/photos/view/album.fxml"));
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
