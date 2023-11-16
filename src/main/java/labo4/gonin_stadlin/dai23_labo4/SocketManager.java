package labo4.gonin_stadlin.dai23_labo4;

import labo4.gonin_stadlin.dai23_labo4.helpers.Popups;

import org.apache.commons.lang3.math.NumberUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import static labo4.gonin_stadlin.dai23_labo4.helpers.Constances.*;

public class SocketManager {
    private final String srvAddr;
    private final int srvPort;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public SocketManager(String srvAddr, int srvPort) throws RuntimeException {
        this.srvAddr = srvAddr;
        this.srvPort = srvPort;

        try {
            connect();
            //TODO call hello, or another test method to the server to validate the connection
            disconnect();
        } catch (IOException ex) {
            if (isConnected())
                try {
                    disconnect();
                } catch (IOException e) {
                    socket = null;
                    in = null;
                    out = null;
                }//nothing else to do than hope garbage collector will destroy it soon
            Popups.error("Error happened", MSG_EXPRESSION_HANDLER + ex);
            throw new RuntimeException();
        }
    }

    private void connect() throws IOException {
        socket = new Socket(srvAddr, srvPort);

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());
    }

    private void disconnect() throws IOException {
        in.close();
        in = null;
        out.flush();
        out.close();
        out = null;

        socket.close();
        socket = null;
    }

    private boolean isConnected() {
        return socket != null && in != null && out != null;
    }

    String read() throws IOException {
        String data;
        if ((data = in.readLine()) != null) {
            if (data.split(" ")[0].equals("INVALID")) {
                return errnumString(Integer.parseInt(data.split(" ")[1]));
            }
            if (NumberUtils.isParsable(data)) {
                return data;
            }
        }
        return "not a number : The result is not parsable";
    }

    boolean sendSpam(ArrayList<String> victims, ArrayList<String> messages, int nbGroups) {
        out.println("ADD " + victims.size() + " " + nbGroups);
        out.flush();
        return false;
    }

    boolean sendMail(ArrayList<String> vicims, String content){
        try {
            connect();
            out.println("ehlo " + srvAddr);
            out.flush();
            out.println("mail from:<" + vicims.get(0) + ">");
            out.flush();
            for(int i = 1; i < vicims.size(); ++i){
                out.println("rcpt to: <" + vicims.get(i) + ">");
                out.flush();
            }
            out.println("data");
            out.flush();
            out.println("From: <shakira@music.com>");
            StringBuilder to = new StringBuilder().append("To: ");
            for(int i = 1; i < vicims.size(); ++i){
                to.append("<").append(vicims.get(i)).append(">,");
            }
            to.setLength(to.length() - 1);
            out.println(to);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM dd, yyyy");
            out.println("Date : " + dtf.format(LocalDateTime.now()));
            out.flush();
            disconnect();
        }catch (IOException e){
            return false;
        }
        return true;
    }

    private String errnumString(int errnum) {
        return switch (errnum) {
            default -> "not specified : various unknowns errors";
            case 1 -> "not an operation : the operation ask by the client is not valid.";
            case 2 -> "Illegal amount of parameter : Too least or too many parameters given.";
            case 3 ->
                    "not a number : At least one of the parameter who need to be convertible to double is not convertible.";
            case 4 -> "Illegal move : division by 0, square root of a negative number, ...";
            case 5 ->
                    "Internal errors : various errors that happened on the server side, as an example : the result is too big to be stock in a double.";
        };
    }
}
