package labo4.gonin_stadlin.dai23_labo4.helpers;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * JavaFx popups handler, inspired by <a href="https://code.makery.ch/blog/javafx-dialogs-official/">https://code.makery.ch/blog/javafx-dialogs-official/</a>
 *
 * @author Guillaume Gonin
 * @version 1.9
 * @since 04.11.2023
 */
public class Popups {
    /**
     * Extension filter list proposed (contain a default "All Files", "Test Files", "Image Files", "Video Files")
     */
    public final static List<FileChooser.ExtensionFilter> EXTENSION_FILTER_LIST = Arrays.asList(
            new FileChooser.ExtensionFilter("All Files", "*.*"),
            new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.md", "*.xmlx", "*.html", "*.xml", "*.csv"),
            new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif", "*.jpeg"),
            new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mkv", "*.mts"));

    /**
     * User home directory
     */
    public final static File USER_HOME = new File(System.getProperty("user.home"));

    /**
     * Popup with an ok/cancel button handling
     *
     * @param title  the title of the window
     * @param header the content of the header (if null, no header handled)
     * @param text   the content
     * @return true if OK pressed, else false
     */
    public static boolean ask(String title, String header, String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    /**
     * Popup with a list of input handling
     *
     * @param title  the title of the window
     * @param header the content of the header (if null, no header handled)
     * @param text   the content
     * @param values the values that the list will take (the first one is selected)
     * @return the selected element chosen by the user or null
     */
    public static <T> T askElInAList(String title, String header, String text, ArrayList<T> values) {
        ChoiceDialog<T> dialog = new ChoiceDialog<>(values.get(0), values);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(text);

        Optional<T> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Popup with a button open who ask to choose a file and then return it
     *
     * @param title  the title of the window
     * @param header the content of the header (if null, no header handled)
     * @return the File object chosen or null
     */
    public static File askFile(String title, String header) {
        return askFile(title, header, null);
    }

    /**
     * get option hashmap easily for ask file
     *
     * @param title   the title set a custom title
     * @param directory  set a custom directory opened with the window
     * @param extensionsFilter a custom extension filter list
     * @param SelectedExtensionFilter the index of the extensionFilter to be selected by default
     *
     * @return the File object chosen or placeholder
     */
    public static HashMap<String, Object> getOptions(String title, File directory, Collection<FileChooser.ExtensionFilter> extensionsFilter, Integer SelectedExtensionFilter) {
        HashMap<String, Object> options = new HashMap<>();
        options.put("Title", title);
        if (directory.exists())
            options.put("Initial Directory", directory);
        options.put("Extension Filter List", extensionsFilter);
        options.put("Selected Extension Filter", SelectedExtensionFilter);
        return options;
    }

    /**
     * Popup with a button open who ask to choose a file and then return it
     *
     * @param title   the title of the window
     * @param header  the content of the header (if null, no header handled)
     * @param options customise the file window. See getOptions() documentation.
     * @return the File object chosen or placeholder
     */
    public static File askFile(String title, String header, HashMap<String, Object> options) {
        Dialog<File> dialog = new Dialog<>();
        File file = null;

        // Set the button types.
        ButtonType openButtonType = new ButtonType("Open", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(openButtonType);


        dialog.setTitle(title);
        dialog.setHeaderText(header);

        // Get the Stage.
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == openButtonType) {
                FileChooser fileChooser = new FileChooser();
                if (options != null && options.containsKey("Title") && options.get("Title") instanceof String)
                    fileChooser.setTitle((String) options.get("Title"));
                else
                    fileChooser.setTitle("Open File");
                if (options != null && options.containsKey("Initial File Name") && options.get("Initial File Name") instanceof String)
                    fileChooser.setInitialFileName((String) options.get("Initial File Name"));
                if (options != null && options.containsKey("Initial Directory") && options.get("Initial Directory") instanceof File)
                    fileChooser.setInitialDirectory((File) options.get("Initial Directory"));
                if (options != null && options.containsKey("Extension Filter List") && options.get("Extension Filter List") instanceof Collection<?>) {
                    for (Object e : (Collection<?>) options.get("Extension Filter List")) {
                        if (e instanceof FileChooser.ExtensionFilter)
                            fileChooser.getExtensionFilters().add((FileChooser.ExtensionFilter) e);
                    }

                    if (options.containsKey("Selected Extension Filter") && options.get("Selected Extension Filter") instanceof Integer)
                        fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get((Integer) options.get("Selected Extension Filter")));
                }
                return fileChooser.showOpenDialog(stage);
            }
            return null;
        });

        Optional<File> result = dialog.showAndWait();


        return result.orElse(null);
    }


    /**
     * Popup with a text input handling
     *
     * @param title  the title of the window
     * @param header the content of the header (if null, no header handled)
     * @param text   the content
     * @return the content of the text input after pressed OK button
     */
    public static String askText(String title, String header, String text) {
        return askText(title, header, text, null);
    }

    /**
     * Popup with a text input handling
     *
     * @param title       the title of the window
     * @param header      the content of the header (if null, no header handled)
     * @param text        the content
     * @param placeholder initial content of the text input
     * @return the content of the text input after pressed OK button or null
     */
    public static String askText(String title, String header, String text, String placeholder) {
        TextInputDialog dialog = placeholder == null
                ? new TextInputDialog()
                : new TextInputDialog(placeholder);

        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(text);

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Popup info handling
     *
     * @param header the content of the header (if null, no header handled)
     * @param text   the content
     */
    public static void info(String header, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(header);
        alert.setContentText(text);

        alert.showAndWait();
    }

    /**
     * Popup warn handling
     *
     * @param header the content of the header (if null, no header handled)
     * @param text   the content
     */
    public static void warn(String header, String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText(header);
        alert.setContentText(text);

        alert.showAndWait();
    }

    /**
     * Popup error handling
     *
     * @param header the content of the header (if null, no header handled)
     * @param text   the content
     */
    public static void error(String header, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(header);
        alert.setContentText(text);

        alert.showAndWait();
    }

    /**
     * Complex popup handling an exception with the stack trace on a scrollable element
     *
     * @param header the content of the header (if null, no header handled)
     * @param ex     the exception to handle
     */
    public static void exceptionHandle(String header, Exception ex) {
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception Dialog");
            alert.setHeaderText(header);
            alert.setContentText(ex.getMessage());

            // Create expandable Exception.
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);

            alert.showAndWait();
        }
    }
}
