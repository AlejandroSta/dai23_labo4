package labo4.gonin_stadlin.dai23_labo4;

//import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Worker {
    private final PrintWriter out;
    private final BufferedReader in;

    public Worker(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    void add(String x, String y) {
        out.println("ADD " + x + " " + y);
        out.flush();
    }

    void sub(String x, String y) {
        out.println("SUB " + x + " " + y);
        out.flush();
    }

    void mul(String x, String y) {
        out.println("MUL " + x + " " + y);
        out.flush();
    }

    void div(String x, String y) {
        out.println("DIV " + x + " " + y);
        out.flush();
    }

    String read() throws IOException {
        String data;
        if ((data = in.readLine()) != null) {
            if (data.split(" ")[0].equals("INVALID")) {
                return errnumString(Integer.parseInt(data.split(" ")[1]));
            }
            /*if (NumberUtils.isParsable(data)) {
                return data;
            }*/
        }
        return "not a number : The result is not parsable";
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
