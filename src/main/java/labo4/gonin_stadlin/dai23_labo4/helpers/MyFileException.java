package labo4.gonin_stadlin.dai23_labo4.helpers;

/**
 * Custom exception for files, linked to FileManager
 * @author Guillaume Gonin
 * @version 1.1
 * @since 30.02.2020
 */
public class MyFileException extends Exception {
    private final boolean open;

    /**
     * @param msg  class and method name or other information
     * @param open error during the opening time (true if yes)
     */
    public MyFileException(String msg, boolean open) {
        super("ERROR in: " + msg);
        this.open = open;
    }

    @Override
    public String toString() {
        return super.toString() + message();
    }

    @Override
    public String getMessage() {
        return super.getMessage() + message();
    }

    private String message() {
        return open ? "\nFile problem at the opening time" : "\nFile problem at the reading/writing time";
    }

}