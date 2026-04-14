package photos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import photos.model.Photo;
import photos.model.User;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Charles Eshelman and Ryan Lilly
 */
public class SearchController {

    @FXML private RadioButton    dateRangeRadio;
    @FXML private RadioButton    tagSearchRadio;
    @FXML private ToggleGroup    searchModeGroup;

    @FXML private DatePicker     startDatePicker;
    @FXML private DatePicker     endDatePicker;

    @FXML private ComboBox<String> tagType1ComboBox;
    @FXML private TextField        tagValue1Field;
    @FXML private ComboBox<String> conjunctionComboBox;
    @FXML private ComboBox<String> tagType2ComboBox;
    @FXML private TextField        tagValue2Field;

    private User currentUser;

    /**
     * Initializes conjunction ComboBox
     */
    @FXML
    public void initialize() {
        conjunctionComboBox.getItems().addAll("AND", "OR");

        // Date range controls are disabled when tag search is selected
        startDatePicker.disableProperty().bind(tagSearchRadio.selectedProperty());
        endDatePicker.disableProperty().bind(tagSearchRadio.selectedProperty());

        // Tag controls are disabled when date range is selected
        tagType1ComboBox.disableProperty().bind(dateRangeRadio.selectedProperty());
        tagValue1Field.disableProperty().bind(dateRangeRadio.selectedProperty());
        conjunctionComboBox.disableProperty().bind(dateRangeRadio.selectedProperty());
        tagType2ComboBox.disableProperty().bind(dateRangeRadio.selectedProperty());
        tagValue2Field.disableProperty().bind(dateRangeRadio.selectedProperty());
    }

    /**
     * Sets current user and tag-type ComboBoxes
     * from the user's registered tag types.
     *
     * @param user the logged-in {@link User}
     */
    public void setUser(User user) {
        this.currentUser = user;
        tagType1ComboBox.getItems().setAll(user.getTagTypes());
        tagType2ComboBox.getItems().setAll(user.getTagTypes());
        if (!user.getTagTypes().isEmpty()) {
            tagType1ComboBox.setValue(user.getTagTypes().get(0));
            tagType2ComboBox.setValue(user.getTagTypes().get(0));
        }
    }

    /**
     * Handles the Search button.
     * Returns {@code null} if validation fails
     */
    @FXML
    private void handleSearch() {
        List<Photo> results;

        if (dateRangeRadio.isSelected()) {
            results = searchByDateRange();
        } else {
            results = searchByTags();
        }

        if (results == null) return;

        loadResultsScene(results);
    }

    /**
     * Searches by the date range specified, inclusive of all photos taken within that day
     * @return list of matching {@link Photo} objects, or {@code null} on validation failure
     */
    private List<Photo> searchByDateRange() {
        LocalDate startLocal = startDatePicker.getValue();
        LocalDate endLocal   = endDatePicker.getValue();

        if (startLocal == null || endLocal == null) {
            showAlert("Please select both a start and end date.");
            return null;
        }
        if (endLocal.isBefore(startLocal)) {
            showAlert("End date cannot be before start date.");
            return null;
        }

        Calendar start = localDateToCalendar(startLocal);

        Calendar end = localDateToCalendar(endLocal);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 0);

        List<Photo> results  = new ArrayList<>();
        Set<Photo>  allPhotos = currentUser.getAllPhotos();

        for (Photo photo : allPhotos) {
            Calendar date = photo.getCaptureDate();
            if (!date.before(start) && !date.after(end)) {
                results.add(photo);
            }
        }
        return results;
    }

    /**
     * Searches all of the user's photos using one or two tag type-value pairs.
     * A second tag is considered if a conjunction and a value are provided.
     *
     * @return list of matching {@link Photo} objects, or {@code null} on failure
     */
    private List<Photo> searchByTags() {
        String type1  = tagType1ComboBox.getValue();
        String value1 = tagValue1Field.getText().trim();

        if (type1 == null || value1.isBlank()) {
            showAlert("Please enter at least one tag type and value.");
            return null;
        }

        String  conjunction = conjunctionComboBox.getValue();
        String  type2       = tagType2ComboBox.getValue();
        String  value2      = tagValue2Field.getText().trim();
        boolean hasTwoTags  = conjunction != null
                              && type2 != null
                              && !value2.isBlank();

        List<Photo> results   = new ArrayList<>();
        Set<Photo>  allPhotos = currentUser.getAllPhotos();

        for (Photo photo : allPhotos) {
            boolean match1 = photo.hasTag(type1, value1);

            if (!hasTwoTags) {
                if (match1) results.add(photo);
            } else {
                boolean match2 = photo.hasTag(type2, value2);
                if ("AND".equals(conjunction) && match1 && match2) {
                    results.add(photo);
                } else if ("OR".equals(conjunction) && (match1 || match2)) {
                    results.add(photo);
                }
            }
        }
        return results;
    }

    /**
     * Converts a {@link LocalDate} to a {@link Calendar} with the time set
     * to midnight.
     *
     * @param date the date to convert
     * @return an equivalent {@link Calendar} instance
     */
    private Calendar localDateToCalendar(LocalDate date) {
        Date utilDate = Date.from(
                date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar cal = Calendar.getInstance();
        cal.setTime(utilDate);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /**
     * Handles the back button.
     * Returns to the album list screen for the current user.
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/photos/view/albumList.fxml"));
            Parent root = loader.load();

            AlbumListController controller = loader.getController();
            controller.setUser(currentUser);

            Stage stage = (Stage) startDatePicker.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Photos");
            stage.show();
        } catch (Exception e) {
            showAlert("Failed to return to album list.");
        }
    }

    /**
     * Loads the search results scene.
     * @param results the list of photos matching the search criteria
     */
    private void loadResultsScene(List<Photo> results) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/photos/view/searchResults.fxml"));
            Parent root = loader.load();

            SearchResultsController controller = loader.getController();
            controller.setContext(currentUser, results);

            Stage stage = (Stage) startDatePicker.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Search Results (" + results.size() + " photo"
                    + (results.size() == 1 ? "" : "s") + ")");
            stage.show();
        } catch (Exception e) {
            showAlert("Failed to load search results.");
        }
    }

    /**
     * Displays an error {@link Alert} with a gven message.
     * @param message the error text to display
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Search Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
