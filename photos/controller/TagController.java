package photos.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import photos.model.Album;
import photos.model.DataManager;
import photos.model.Photo;
import photos.model.Tag;
import photos.model.User;
import photos.model.UserManager;

import java.util.Optional;

/**
 * @author Charles Eshelman and Ryan Lilly
 */
public class TagController {

    @FXML private ComboBox<String> tagTypeComboBox;
    @FXML private TextField        tagValueField;
    @FXML private ListView<Tag>    tagListView;

    private User  currentUser;
    private Album currentAlbum;
    private Photo currentPhoto;

    private static final String ADD_NEW_TYPE = "+ Add new tag type...";

    /**
     * @param user  the currently logged in User
     * @param album the currently viewed Album
     * @param photo the photo whose tags are being focused on
     */
    public void setContext(User user, Album album, Photo photo) {
        this.currentUser  = user;
        this.currentAlbum = album;
        this.currentPhoto = photo;

        refreshTagTypes();
        refreshTagList();

        tagTypeComboBox.setOnAction(e -> {
            if (ADD_NEW_TYPE.equals(tagTypeComboBox.getValue())) {
                handleAddNewTagType();
            }
        });
    }

    /**
     * Rebuilds tag type {@link ComboBox} from the current tag list.
     * Always append s {@link #ADD_NEW_TYPE} as the final item.
     */
    private void refreshTagTypes() {
        tagTypeComboBox.getItems().setAll(currentUser.getTagTypes());
        tagTypeComboBox.getItems().add(ADD_NEW_TYPE);
        tagTypeComboBox.setValue(currentUser.getTagTypes().isEmpty() ? null : currentUser.getTagTypes().get(0));
    }

    /**
     * Rebuilds the tag {@link ListView} from the photo's tag list.
     * Each cell displays tag from {@link Tag#toString()}
     */
    private void refreshTagList() {
        tagListView.setItems(
                FXCollections.observableArrayList(currentPhoto.getTags()));
    }

    /**
     * Handles add Tag button.
     * Reads type from ComboBox and value from text field.
     */
    @FXML
    private void handleAddTag() {
        String type  = tagTypeComboBox.getValue();
        String value = tagValueField.getText().trim();

        if (type == null || type.equals(ADD_NEW_TYPE)) {
            showAlert("Please select a valid tag type.");
            return;
        }
        if (value.isBlank()) {
            showAlert("Please enter a tag value.");
            return;
        }

        boolean added = currentPhoto.addTag(
                new Tag(type, value),
                currentUser.getSingleValueTagTypes());

        if (!added) {
            showAlert("This tag already exists on the photo, " + "or this type only allows one value per photo.");
        } else {
            tagValueField.clear();
            refreshTagList();
            DataManager.saveUsers(UserManager.getInstance());
        }
    }

    /**
     * Handles delete tag button.
     * Asks before removing tags from photo.
     */
    @FXML
    private void handleDeleteTag() {
        Tag selected = tagListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a tag to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Tag");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete tag '" + selected + "'?");

        Optional<ButtonType> response = confirm.showAndWait();
        if (response.isPresent() && response.get() == ButtonType.OK) {
            currentPhoto.removeTag(selected);
            refreshTagList();
            DataManager.saveUsers(UserManager.getInstance());
        }
    }

    /**
     * Gives prompt to get new tag.
     * Asks for name of the typ e using {@link TextInputDialog}
     * Asks for tag multiplicty from {@link Alert}
     */
    private void handleAddNewTagType() {
        // Step 1: get the type name
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("New Tag Type");
        nameDialog.setHeaderText(null);
        nameDialog.setContentText("Enter new tag type name:");

        Optional<String> nameResult = nameDialog.showAndWait();
        if (nameResult.isEmpty() || nameResult.get().isBlank()) {
            resetComboBoxSelection();
            return;
        }

        String newType = nameResult.get().trim();

        // Step 2: single-value or multi-value?
        Alert singleValueAlert = new Alert(Alert.AlertType.CONFIRMATION);
        singleValueAlert.setTitle("Tag Value Policy");
        singleValueAlert.setHeaderText(null);
        singleValueAlert.setContentText(
                "Should '" + newType + "' allow only one value per photo?\n" +
                "e.g. 'location' is single-value; 'person' is multi-value.");
        singleValueAlert.getButtonTypes().setAll(
                new ButtonType("Single value"),
                new ButtonType("Multiple values"),
                ButtonType.CANCEL);

        Optional<ButtonType> response = singleValueAlert.showAndWait();
        if (response.isEmpty() || response.get() == ButtonType.CANCEL) {
            resetComboBoxSelection();
            return;
        }

        boolean isSingle = response.get().getText().equals("Single value");
        boolean added = currentUser.addTagType(newType, isSingle);

        if (!added) {
            showAlert("Tag type '" + newType + "' already exists.");
            resetComboBoxSelection();
        } else {
            DataManager.saveUsers(UserManager.getInstance());
            refreshTagTypes();
            tagTypeComboBox.setValue(newType);
        }
    }

    private void resetComboBoxSelection() {
        tagTypeComboBox.setValue(currentUser.getTagTypes().isEmpty() ? null : currentUser.getTagTypes().get(0));
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/album.fxml"));
            Parent root = loader.load();

            AlbumController controller = loader.getController();
            controller.setContext(currentUser, currentAlbum);

            Stage stage = (Stage) tagListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(currentAlbum.getName());
            stage.show();
        } catch (Exception e) {
            showAlert("Failed to return to album.");
        }
    }

    /**
     * Displays an {@link Alert} with a message.
     * @param message The error text to display
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Tag Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
