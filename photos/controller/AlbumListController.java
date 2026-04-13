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
import photos.model.Album;
import photos.model.DataManager;
import photos.model.User;
import photos.model.UserManager;

import java.util.Optional;


public class AlbumListController {

    @FXML private ListView<Album> albumListView;

    private User currentUser;
    private ObservableList<Album> observableAlbums;

    
    public void setUser(User user) {
        this.currentUser = user;
        observableAlbums = FXCollections.observableArrayList(currentUser.getAlbums());
        albumListView.setItems(observableAlbums);
    }

   
    private void refreshList() {
        observableAlbums.setAll(currentUser.getAlbums());
    }

    
    @FXML
    private void handleCreateAlbum() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Album");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter album name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (name.isBlank()) {
                showAlert("Album name cannot be blank.");
                return;
            }
            boolean added = currentUser.addAlbum(new Album(name.trim()));
            if (!added) {
                showAlert("An album named '" + name + "' already exists.");
            } else {
                refreshList();
                DataManager.saveUsers(UserManager.getInstance());
            }
        });
    }

    
    @FXML
    private void handleDeleteAlbum() {
        Album selected = albumListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an album to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Album");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete album '" + selected.getName() + "'?");

        Optional<ButtonType> response = confirm.showAndWait();
        if (response.isPresent() && response.get() == ButtonType.OK) {
            currentUser.removeAlbum(selected);
            refreshList();
            DataManager.saveUsers(UserManager.getInstance());
        }
    }

   
    @FXML
    private void handleRenameAlbum() {
        Album selected = albumListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an album to rename.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getName());
        dialog.setTitle("Rename Album");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter new name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            if (newName.isBlank()) {
                showAlert("Album name cannot be blank.");
                return;
            }
            boolean renamed = currentUser.renameAlbum(selected.getName(), newName.trim());
            if (!renamed) {
                showAlert("An album named '" + newName + "' already exists.");
            } else {
                refreshList();
                DataManager.saveUsers(UserManager.getInstance());
            }
        });
    }

    
    @FXML
    private void handleOpenAlbum() {
        Album selected = albumListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an album to open.");
            return;
        }
        loadAlbumScene(selected);
    }

   
    @FXML
    private void handleAlbumDoubleClick() {
        Album selected = albumListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            loadAlbumScene(selected);
        }
    }

   
    @FXML
    private void handleSearch() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/photos/view/search.fxml"));
            Parent root = loader.load();

            SearchController controller = loader.getController();
            controller.setUser(currentUser);

            Stage stage = (Stage) albumListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Failed to open search.");
        }
    }

    
    @FXML
    private void handleLogout() {
        DataManager.saveUsers(UserManager.getInstance());
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/photos/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) albumListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Photos — Login");
            stage.show();
        } catch (Exception e) {
            showAlert("Failed to return to login screen.");
        }
    }

    
    private void loadAlbumScene(Album album) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/photos/view/album.fxml"));
            Parent root = loader.load();

            AlbumController controller = loader.getController();
            controller.setContext(currentUser, album);

            Stage stage = (Stage) albumListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(album.getName());
            stage.show();
        } catch (Exception e) {
            showAlert("Failed to open album.");
        }
    }

    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}