package labo4.gonin_stadlin.dai23_labo4;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import labo4.gonin_stadlin.dai23_labo4.helpers.FileManager;
import labo4.gonin_stadlin.dai23_labo4.helpers.MyFileException;
import labo4.gonin_stadlin.dai23_labo4.helpers.Popups;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import static labo4.gonin_stadlin.dai23_labo4.helpers.Constants.APP_TITLE;
import static labo4.gonin_stadlin.dai23_labo4.helpers.Constants.FXML_PATH;
import static labo4.gonin_stadlin.dai23_labo4.helpers.Popups.getOptions;
import static labo4.gonin_stadlin.dai23_labo4.helpers.Popups.EXTENSION_FILTER_LIST;
import static labo4.gonin_stadlin.dai23_labo4.helpers.Popups.USER_HOME;

public class App extends Application {

    //APP implementation
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println(App.class.getResource(FXML_PATH));
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(FXML_PATH));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(APP_TITLE);
        //scene.getStylesheets().add(URL_CSS_SHEET);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    //CONTROLLER part
    //FXML attributes
    @FXML
    private TabPane tp;

    @FXML
    private Tab t_prepare;

    @FXML
    private ListView<String> lv_victims;

    @FXML
    private ListView<String> lv_messages;

    @FXML
    private Label l_nbGroups;

    @FXML
    private TextArea txa_victim;

    @FXML
    private TextArea txa_message;

    private ArrayList<String> victims, messages;

    private Integer nbGroups;

    //onButtonClick functs
    @FXML
    private void onSendButtonClick() {
        String serverIp = Popups.askText("Server IP", "Give the server IP", "Give the server IP to which you want to connect to send this.");
        String serverPort = Popups.askText("Server Port", "Give the server port", "Give the server port to which you want to connect to send this.");

        if (!NumberUtils.isParsable(serverPort)) {
            return;
        }

        int port = Integer.parseInt(serverPort);
        SocketManager socketManager = null;

        try {
            socketManager = new SocketManager(serverIp, port);
        } catch (RuntimeException ex) {
            Popups.error("Error append", "Impossible to connect to the server.");
        }

        if (socketManager != null && socketManager.sendSpam(victims, messages, nbGroups)) {
            Popups.info("Spam done !", "It seems that it works. The spams should be send and received soon.");
        } else {
            Popups.warn("Something went wrong !", "It's seems that something went wrong on the sending of spam. May check all is alright or try again.");
        }
    }

    @FXML
    public void onConfigButtonClick(ActionEvent actionEvent) {
        //victims
        File directory = new File(USER_HOME.getAbsolutePath() + "\\Documents\\");

        File fVictims = Popups.askFile("Victims list", "Indicate the file containing the list of e-mails addresses who describe the victim list.",
                getOptions("Victims list file", directory, EXTENSION_FILTER_LIST.subList(0, 2), 1));

        if (fVictims == null) Popups.info("null", "null returned");
        if (!validateVictims(fVictims)) {
            Popups.error("Something went wrong !", "The file indicated seems invalid, please retry");
            return;
        }

        try {
            victims = fileToList(fVictims);
        } catch (MyFileException e) {
            Popups.error("Error", "An error occurred while reading the victims email list file");
        }

        //Messages
        File fMessages = Popups.askFile("Messages list", "Indicate the file containing the list of subjects and body to send.",
                getOptions("Messages list file", directory, EXTENSION_FILTER_LIST.subList(0, 2), 1));
        if (!validateMessages(fMessages)) {
            Popups.error("Something went wrong !", "The file indicated seems invalid, please retry");
            return;
        }

        try {
            messages = fileToList(fMessages);
        } catch (MyFileException e) {
            Popups.error("Error", "An error occurred while reading the emails content file");
        }

        //nbOfGroups
        nbGroups = Popups.askElInAList("Choose numbers of groups.", "Choose the number of groups you needs.", "One groups is composed of 2 to 5 victims who one is the sender of the email and the others the recievers.",
                new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
        if (nbGroups == null) {
            Popups.error("Something went wrong !", "The value got seems invalid, please retry");
            return;
        }

        initTabPrepare(victims, messages, nbGroups);

        //display tab prepare
        t_prepare.setDisable(false);
        tp.getSelectionModel().select(t_prepare);
    }

    /**
     * Method that allow use to test if a victims file is valid (an email per line (email need to contain one and only one @ and at least one '.' after the @)
     *
     * @param fileToValidate the file to validate
     * @return true if valid, else false
     */
    private boolean validateVictims(File fileToValidate) {
        if (fileToValidate == null) return false;
        boolean isValid = true;

        try {
            FileManager fm = new FileManager(fileToValidate.getAbsolutePath());

            boolean isEmailsFileEmpty = true;
            ArrayList<String> line;
            int i = 0;
            while (!(line = fm.reads(i, 1)).isEmpty() && isValid){
                isEmailsFileEmpty = false;
                i++;

                String[] at = line.get(0).split("@");
                if (at.length != 2){
                    isValid = false;
                    break;
                }

                String[] point = at[1].split("\\.");
                if (point.length < 2) isValid = false;
            }

            return !isEmailsFileEmpty && isValid;
        } catch (Exception ex) {
            return false;
        }
    }
    /**
     * Method that allow use to test if a messages file is valid (subject first line, then body, then empty line and so on)
     *
     * @param fileToValidate the file to validate
     * @return true if valid, else false
     */
    private boolean validateMessages(File fileToValidate) {
        if (fileToValidate == null) return false;
        boolean isValid = true;

        try {
            FileManager fm = new FileManager(fileToValidate.getAbsolutePath());

            int i = 0;
            ArrayList<String> message;
            while (!(message = fm.reads(i, 3)).isEmpty() && isValid){
                i += 3;
                String subject = message.get(0);
                String body = message.size() > 1 ? message.get(1) : null;
                String emptyLine = message.size() > 2 ? message.get(2) : null;

                if (subject == null || body == null || emptyLine == null || !emptyLine.isEmpty()) isValid = false;
            }
            return isValid;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Method that return the content of the file into an ArrayList
     * NB: Probably move it to file manager
     *
     * @param fileToConvert the file to convert
     * @return the data on an ArrayList or null
     */
    private ArrayList<String> fileToList(File fileToConvert) throws MyFileException {
        return new FileManager(fileToConvert.getAbsolutePath()).readAll();
    }

    /**
     * Method that set the contents of UI of the tab prepare (the ListViews, the events, the labels, ...)
     *
     * @param victims  the list of victims got to display
     * @param messages the list of messages got to display
     * @param nbGroups the number of groups chosen by the user
     */
    private void initTabPrepare(ArrayList<String> victims, ArrayList<String> messages, Integer nbGroups) {
        lv_victims.setOnMouseClicked(mouseEvent -> txa_victim.setText(lv_victims.getSelectionModel().getSelectedItem()));
        lv_messages.setOnMouseClicked(mouseEvent -> txa_message.setText(lv_messages.getSelectionModel().getSelectedItem()));

        lv_victims.getItems().addAll(victims);
        lv_messages.getItems().addAll(messages);

        l_nbGroups.setText("Number of groups : " + nbGroups);
    }
}