package labo4.gonin_stadlin.dai23_labo4;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import labo4.gonin_stadlin.dai23_labo4.helpers.Popups;
//import org.apache.commons.lang3.math.NumberUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class App extends Application {
    final String SERVER_ADDRESS = "localhost";
    final int SERVER_PORT = 4242;

    public final static String MSG_EXPRESSION_HANDLER = "Client: exception : ";


    @FXML
    private Tab t_calculate;

    @FXML
    private TabPane tp;

    @FXML
    private TextField txt_op1;

    @FXML
    private TextField txt_op2;

    @FXML
    private ComboBox<String> cbx_op;

    @FXML
    private Label l_result;

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

    @FXML
    private void run() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream());) {
            Worker wrk = new Worker(out, in);

            FileReaderWriter frw = new FileReaderWriter();

            File f_emails = new File("../../../../../../emails.txt"),
                 f_content = new File("../../../../../../content.txt");

            if(!f_emails.exists() || f_emails.isDirectory() || !f_emails.canRead() ||
               !f_content.exists() || f_content.isDirectory() || !f_content.canRead()) throw new RuntimeException();

            String emails = frw.readFile(f_emails, StandardCharsets.UTF_8),
                   content = frw.readFile(f_content, StandardCharsets.UTF_8);

            /*
            String op1 = txt_op1.getText();
            String op2 = txt_op2.getText();
            if(op1.charAt(0) != '+' || op1.charAt(0) != '-') {
                op1 = '+' + op1;
            }
            if(op2.charAt(0) != '+' || op2.charAt(0) != '-') {
                op2 = '+' + op2;
            }

            switch (cbx_op.getSelectionModel().getSelectedItem()) {
                default:
                    return;
                case "+":
                    wrk.add(op1, op2);
                    break;
                case "-":
                    wrk.sub(op1, op2);
                    break;
                case "*":
                    wrk.mul(op1, op2);
                    break;
                case "/":
                    wrk.div(op1, op2);
            }
            */

            String data = wrk.read();/*
            if (NumberUtils.isParsable(data)) {
                l_result.setText("Result : " + data);
            } else {
                Popups.warn("Something went wrong !", MSG_EXPRESSION_HANDLER + data);
            }*/

        } catch (IOException ex) {
           Popups.error("Error happened", MSG_EXPRESSION_HANDLER + ex);
        }
    }

    public void onConnectButtonClick(ActionEvent actionEvent) {
        cbx_op.getItems().add("+");
        cbx_op.getItems().add("-");
        cbx_op.getItems().add("/");
        cbx_op.getItems().add("*");
        cbx_op.getSelectionModel().select("+");

        t_calculate.setDisable(false);
        tp.getSelectionModel().select(t_calculate);
    }
}