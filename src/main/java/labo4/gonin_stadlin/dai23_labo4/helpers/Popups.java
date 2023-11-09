package labo4.gonin_stadlin.dai23_labo4.helpers;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * JavaFx popups handler, inspired by <a href="https://code.makery.ch/blog/javafx-dialogs-official/">https://code.makery.ch/blog/javafx-dialogs-official/</a>
 *
 * @author Guillaume Gonin
 * @version 1.0
 * @since 04.11.2023
 */
public class Popups {
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
     * Popup with a text input handling
     *
     * @param title  the title of the window
     * @param header the content of the header (if null, no header handled)
     * @param text   the content
     * @return the content of the text input after pressed OK button
     */
    public static String askText(String title, String header, String text) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(text);

        Optional<String> result = dialog.showAndWait();
        return result.get();
    }

    /**
     * Popup with a text input handling
     *
     * @param title       the title of the window
     * @param header      the content of the header (if null, no header handled)
     * @param text        the content
     * @param placeholder initial content of the text input
     * @return the content of the text input after pressed OK button
     */
    public static String askText(String title, String header, String text, String placeholder) {
        TextInputDialog dialog = new TextInputDialog(placeholder);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(text);

        Optional<String> result = dialog.showAndWait();
        return result.get();
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
