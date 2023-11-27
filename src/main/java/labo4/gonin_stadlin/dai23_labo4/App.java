package labo4.gonin_stadlin.dai23_labo4;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import labo4.gonin_stadlin.dai23_labo4.helpers.FileManager;
import labo4.gonin_stadlin.dai23_labo4.helpers.MyFileException;
import labo4.gonin_stadlin.dai23_labo4.helpers.Popups;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class App extends Application {

    //APP implementation
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println(App.class.getResource("view.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
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
    private Tab t_home;

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

    private File fVictims, fMessages;
    private ArrayList<String> victims, messages;

    private Integer nbGroups;

    //onButtonClick functs
    @FXML
    private void onSendButtonClick() {
        //TODO or adapt
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
        File directory = new File(System.getProperty("user.home") + "\\Documents\\");
        HashMap<String, Object> options = new HashMap<>();
        options.put("Title", "Victims list file");
        //options.put("Initial File Name", /*directory.getAbsolutePath()+"\\*/"README.txt"); seems not to work
        if (directory.exists())
            options.put("Initial Directory", directory);
        List<FileChooser.ExtensionFilter> extensionsFilter = Arrays.asList(new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.docx", "*.xmlx", "*.doc", "*.xml", "*.pdf"),
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif", "*.jpeg"),
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mkv", "*.mts"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        options.put("Extension Filter List", extensionsFilter);
        options.put("Selected Extension Filter", 3);

        fVictims = Popups.askFile("Victims list", "Indicate the file containing the list of e-mails addresses who describe the victim list.", options);
        if (fVictims == null) Popups.info("null", "null returned");
        if (!validateFile(fVictims)) {
            Popups.error("Something went wrong !", "The file indicated seems invalid, please retry");
            return;
        }

        try {
            victims = fileToList(fVictims);
        } catch (MyFileException e) {
            Popups.error("Error", "An error occurred while reading the victims email list file");
        }

        //Messages
        fMessages = Popups.askFile("Messages list", "Indicate the file containing the list of subjects and body to send.");
        if (!validateFile(fMessages)) {
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

        //Populate with tests values
        //victims = new ArrayList<>(Arrays.asList("myEmail@.test.com", "myEmail2@.test.com", "myEmail3@.test.com", "yourEmail@.test.com", "yourEmail2@.test.com", "yourEmail3@.test.com", "ourEmail@.test.com", "ourEmail2@.test.com", "ourEmail3@.test.com", "myHyperLongEmail@theLongestDomainNameEverInTheWorld.com"));
        //messages = new ArrayList<>(Arrays.asList("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis maximus ipsum velit, vitae fringilla mauris maximus sit amet. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Vivamus finibus nisi ex, ac vehicula nunc bibendum eget. Mauris rhoncus metus in ullamcorper convallis. Suspendisse consectetur ut tellus ac mattis. Aenean ultricies urna ut risus placerat viverra. Mauris dictum dui vitae dolor tincidunt, mollis commodo leo viverra. Phasellus varius ipsum mi. Aenean imperdiet sed odio id efficitur. Integer id elit a ex congue pharetra non ac quam. Fusce a orci sed ante tempor faucibus. Quisque at turpis ornare, convallis lorem eget, congue ex. Maecenas quis dolor dui. Donec bibendum sollicitudin neque non sagittis. Duis faucibus diam id malesuada commodo.", "Praesent ut justo tristique libero rhoncus ultricies. Etiam semper lectus vitae orci hendrerit pellentesque. Aliquam bibendum, mi eu molestie rutrum, turpis ipsum hendrerit orci, posuere accumsan quam odio eget nulla. Cras id tincidunt quam. Etiam ac urna eget lorem vehicula vestibulum id eget eros. Vivamus suscipit laoreet lorem, in sagittis turpis hendrerit eu. Cras mattis velit augue, sed cursus sem porta sed. Nam vitae commodo arcu, in accumsan nibh. Phasellus non lacus venenatis, faucibus est ut, vulputate diam. Fusce dignissim neque tristique, rutrum ipsum cursus, mattis justo. Duis ac sollicitudin dolor, sit amet accumsan magna. In hac habitasse platea dictumst.", "Maecenas sit amet finibus dui. Maecenas elementum rutrum purus vitae sollicitudin. Suspendisse varius magna mollis, feugiat leo et, pellentesque purus. Donec mollis sit amet tortor quis posuere. Integer velit lectus, sodales lobortis interdum id, sollicitudin lobortis urna. Integer porttitor sapien efficitur viverra porttitor. Mauris posuere, turpis et pellentesque ultricies, lectus turpis convallis arcu, eu molestie mauris metus eu elit. Nunc lorem augue, aliquam vel lacinia eu, vulputate eget sapien. Pellentesque id justo a elit sodales efficitur sit amet et metus.", "Nunc accumsan hendrerit mauris. Nulla eu nisl rutrum, condimentum libero in, vestibulum urna. Praesent in diam quis orci fringilla faucibus. Nam vulputate non neque eget ullamcorper. Cras sodales, nulla vitae viverra scelerisque, metus tellus mollis enim, vel feugiat dui lacus vulputate nunc. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Quisque ut tristique sapien. Mauris vitae dolor sed nunc mattis commodo eu non eros. Etiam imperdiet tellus vitae augue sagittis, ut eleifend nisi pellentesque. Vestibulum nec dolor ut lacus accumsan gravida sed sit amet nisl. Vestibulum nec metus nec risus accumsan scelerisque. Sed ullamcorper, nulla eget rhoncus feugiat, justo nisi fringilla ipsum, nec bibendum mi massa quis augue. Ut auctor lectus in dictum tempor.", "Aenean semper elementum tellus. Nunc pharetra dictum orci eu tempus. Morbi pharetra lacus in ante condimentum, et rutrum arcu semper. In placerat magna sed nisi suscipit efficitur. Vivamus congue tincidunt ligula, sed dictum nulla ultricies non. Aenean tincidunt a ante id ullamcorper. Proin facilisis luctus pulvinar. Aenean tincidunt mi sit amet lacinia pharetra. Phasellus vestibulum mattis semper. Morbi aliquam placerat turpis, tristique vestibulum quam porta vel. Curabitur non ullamcorper mi. Pellentesque quis purus tincidunt, convallis eros sit amet, iaculis augue. Duis elit dolor, efficitur vitae volutpat et, varius a nisl. Donec risus dolor, ornare non aliquet in, efficitur non odio. In consectetur nisi a ipsum dignissim dictum. Morbi id metus erat.", "Proin id orci turpis. Donec semper semper quam non semper. Donec placerat sem id magna elementum ullamcorper. Integer eget nisl vitae ante pharetra posuere. Sed et pretium enim, nec mattis odio. Vivamus vitae tellus ac ipsum malesuada commodo nec et orci. Curabitur quis ultricies dolor. Cras pharetra pellentesque ex.", "Integer efficitur hendrerit purus. Etiam laoreet ullamcorper porta. Vivamus euismod, odio ut imperdiet ultricies, mi dui consectetur odio, sit amet viverra orci justo nec odio. Aenean eget urna eu orci cursus vehicula laoreet id magna. Ut at neque hendrerit justo bibendum accumsan quis a leo. Praesent sem urna, sollicitudin in arcu sit amet, pulvinar auctor erat. Ut tellus justo, fringilla sed efficitur sed, pulvinar at sapien. Pellentesque porttitor interdum urna ac accumsan. Nulla facilisi. Nulla sit amet ex cursus, egestas lacus eu, hendrerit augue. Praesent vel enim est.", "Pellentesque eu massa quam. Donec eu mattis neque, vel iaculis augue. Vivamus elementum massa id risus porta, eu sagittis risus ultrices. Vivamus eu nibh et ipsum congue pretium ut at lacus. Praesent at maximus est. Nulla facilisi. Curabitur nec lorem vel risus scelerisque sollicitudin. Duis imperdiet, ligula eget ullamcorper accumsan, velit augue sodales tellus, a lacinia neque nibh et libero. Cras tempus efficitur dui, vel vehicula ipsum pharetra rhoncus.", "Pellentesque ligula tellus, cursus sit amet sem pretium, sodales porta felis. In ac lobortis turpis. Nulla eget varius leo, egestas bibendum neque. Donec tristique libero ut suscipit laoreet. Nulla dignissim posuere accumsan. Integer id viverra odio. Integer id libero eget felis eleifend rhoncus ut vel orci. Phasellus ut massa convallis, eleifend lectus non, vulputate est. Pellentesque sed risus vehicula, vulputate diam ut, molestie sapien. Nam sem sapien, sodales sed elit pharetra, blandit viverra justo. Donec eget dui sit amet nulla egestas mattis id non lectus. Interdum et malesuada fames ac ante ipsum primis in faucibus. Nullam finibus felis dolor, id commodo erat consectetur vel. Aenean mi nunc, auctor in ligula pharetra, scelerisque egestas dui.", "Nullam hendrerit sed dui sed dapibus. Sed vel urna suscipit, fermentum metus ac, ultrices justo. Phasellus nisi lectus, rhoncus et luctus eget, fermentum et augue. Nam euismod, ligula sed suscipit aliquam, mauris nisl consectetur neque, non sodales leo ante non ligula. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Vivamus ut placerat risus. Nullam leo lacus, bibendum sit amet mollis eget, bibendum in ligula. Nunc laoreet consequat porttitor. Cras bibendum quis velit id iaculis. Aliquam molestie leo augue, vitae luctus lacus bibendum at. Aenean pellentesque nulla non blandit aliquet. Nunc mattis, lorem vitae convallis hendrerit, urna turpis gravida urna, vel dapibus enim neque sit amet nunc. Aliquam sit amet ligula in quam eleifend suscipit vel quis est. Mauris ornare libero in purus molestie, a aliquet ante vulputate. Nam nec ex semper ante mollis rutrum vel quis orci."));
        initTabPrepare(victims, messages, nbGroups);

        //display tab prepare
        t_prepare.setDisable(false);
        tp.getSelectionModel().select(t_prepare);
    }

    /**
     * Method that allow use to test if a file is valid
     * NB: may move it to file manager, may split it in validateVictims, validateMessages. TO SEE
     *
     * @param fileToValidate the file to validate
     * @return true if valid, else false
     */
    private boolean validateFile(File fileToValidate) {
        return fileToValidate != null; //TODO
    }

    /**
     * Method that return the content of the file into an ArrayList
     * NB: Probably move it to file manager
     *
     * @param fileToConvert the file to convert
     * @return the data on an ArrayList or null
     */
    private ArrayList<String> fileToList(File fileToConvert) throws MyFileException {
        return new FileManager(fileToConvert.getAbsolutePath()).readAll(); //TODO
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