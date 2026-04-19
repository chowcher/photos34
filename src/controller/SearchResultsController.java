package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Album;
import model.DataManager;
import model.Photo;
import model.User;
import model.UserManager;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * @author Charles Eshelman and Ryan Lilly
 */
public class SearchResultsController {

    @FXML private ListView<Photo> resultsListView;
    @FXML private Label resultCountLabel;

    private User currentUser;
    private List<Photo> results;

    /**
     * Sets context.
     * Called by {@link SearchController}.
     * @param user the logged-in {@link User}
     * @param results the list of photos matching the search criteria
     */
    public void setContext(User user, List<Photo> results) {
        this.currentUser = user;
        this.results     = results;

        resultCountLabel.setText(results.size() + " photo"
                + (results.size() == 1 ? "" : "s") + " found");

        resultsListView.setItems(FXCollections.observableArrayList(results));
        setupCellFactory();
    }

    /**
     * Makes cell factory render a 100×100 thumbnail, like in {@link AlbumController}.
     */
    private void setupCellFactory() {
        resultsListView.setCellFactory(lv -> new ListCell<>() {
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
     * Handles save as new album button.
     * Asks for album name with {@link TextInputDialog} and creates
     * new {@link Album} containing references to result photos.
     */
    @FXML
    private void handleSaveAsAlbum() {
        if (results.isEmpty()) {
            showAlert("No photos to save — the search returned no results.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save as Album");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter a name for the new album:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (name.isBlank()) {
                showAlert("Album name cannot be blank.");
                return;
            }

            Album newAlbum = new Album(name.trim());
            boolean created = currentUser.addAlbum(newAlbum);
            if (!created) {
                showAlert("An album named '" + name + "' already exists.");
                return;
            }

            for (Photo photo : results) {
                newAlbum.addPhoto(photo);
            }

            DataManager.saveUsers(UserManager.getInstance());

            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Album Created");
            info.setHeaderText(null);
            info.setContentText("Album '" + name.trim() + "' created with "
                    + results.size() + " photo"
                    + (results.size() == 1 ? "" : "s") + ".");
            info.showAndWait();
        });
    }

    /**
     * Handles the back button.
     * Returns to the album list screen for the current user.
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/albumList.fxml"));
            Parent root = loader.load();

            AlbumListController controller = loader.getController();
            controller.setUser(currentUser);

            Stage stage = (Stage) resultsListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Photos");
            stage.show();
        } catch (Exception e) {
            showAlert("Failed to return to album list.");
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
