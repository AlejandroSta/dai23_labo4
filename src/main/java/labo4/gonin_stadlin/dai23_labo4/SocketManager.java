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
import java.util.List;

import static labo4.gonin_stadlin.dai23_labo4.helpers.Constants.*;

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

    boolean isReadFine(){
        String data;
        do {
            try {
                data = in.readLine();
                if(data.charAt(0) != '2') return false;
            }catch(IOException e){
                return false;
            }
        }while(data.charAt(3) != ' ');
        return true;
    }

    boolean sendSpam(ArrayList<String> victims, ArrayList<String> messages, int nbGroups) {
        if(victims.size() == 1) return false;
        int groupSize = victims.size() / nbGroups;
        if(groupSize < 2){
            Popups.warn("Mauvais ratio nombre d'emails / nombre de groupes", "Le nombre d'emails donnés est trop petit comparé au nombre de groupes demandés (il faut au minimum deux emails par groupe)");
            boolean c = Popups.ask("Continuer ?", "Voulez-vous continuer ?", "Nous pouvons réduire le nombre de groupes automatiquement si vous le souhaitez");
            if(!c) return false;
        }
        while(groupSize < 2){
            groupSize = victims.size() / (--nbGroups);
        }
        for(int i = 0; i < nbGroups; ++i){
            if (!sendMail(victims.subList(i * groupSize, (i + 1) * groupSize), messages.get(i))) return false;
        }
        return true;
    }

    boolean sendMail(List<String> victims, String content){
        try {
            connect();
            if(!isReadFine()) return false;
            out.println("ehlo " + srvAddr);
            out.flush();
            if(!isReadFine()) return false;
            out.println("mail from:<" + victims.get(0) + ">");
            out.flush();
            if(!isReadFine()) return false;
            for(int i = 1; i < victims.size(); ++i){
                out.println("rcpt to: <" + victims.get(i) + ">");
                out.flush();
                if(!isReadFine()) return false;
            }
            out.println("data");
            out.flush();
            if(in.readLine().charAt(0) != '3') return false;
            out.println("From: <shakira@music.com>");
            StringBuilder to = new StringBuilder().append("To: ");
            for(int i = 1; i < victims.size(); ++i){
                to.append("<").append(victims.get(i)).append(">,");
            }
            to.setLength(to.length() - 1);
            out.println(to);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            int day = Integer.parseInt(DateTimeFormatter.ofPattern("dd").format(LocalDateTime.now()));
            String daySuffix = switch(day % 10){
                case 1 -> (day == 11 ? "th" : "st");
                case 2 -> (day == 12 ? "th" : "nd");
                case 3 -> (day == 13 ? "th" : "rd");
                default -> "th";
            };
            out.println("Date : " + dtf.format(LocalDateTime.now()).replace(",", daySuffix + ","));
            out.flush();
            out.println("Subject: April Joke" + RN);
            out.flush();
            out.println(content);
            out.flush();
            out.println(RN + ".");
            out.flush();
            if(!isReadFine()) return false;
            out.println("quit");
            out.flush();
            if(!isReadFine()) return false;
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
