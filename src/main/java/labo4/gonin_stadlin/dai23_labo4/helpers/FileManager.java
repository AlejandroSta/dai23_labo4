package labo4.gonin_stadlin.dai23_labo4.helpers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Java file manager doing standards operations
 *
 * @author Guillaume Gonin
 * @version 2.5
 * @since 30.02.2020
 */
public class FileManager {

    //path to the file
    private final String stringFile;

    //is this file a binary file?
    private final boolean binary;

    //the name of the file (for example the name of "myFile.txt" is "myFile")
    private final String name;

    //the extension type of the file (txt, jar, obj, ...)
    private final String extension;

    /**
     * Constructor, by default it is supposed that it's not a binary file.
     * If it's not what you want, use the other constructor.
     *
     * @param stringFile the path to the file
     */
    public FileManager(String stringFile) {
        this(stringFile, false);
    }

    /**
     * Constructor
     *
     * @param stringFile the path to the file
     * @param binary     is this file a binary file?
     */
    public FileManager(String stringFile, boolean binary) {
        this.stringFile = stringFile;
        this.binary = binary;
        int index = stringFile.indexOf(".");
        name = stringFile.substring(0, index);
        extension = stringFile.substring(index + 1);
    }

    /**
     * Method that return the name of the file
     *
     * @return the name of the file
     */
    public String getName() {
        return name;
    }

    /**
     * Method that return the extension of the file
     *
     * @return the extension of the file
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Method that create the file
     *
     * @return true if the file exist, else false
     * @throws MyFileException thrown only if the method who creat the file can't do it (security exception for example)
     */
    public boolean create() throws MyFileException {
        File file = new File(stringFile);
        try {
            file.createNewFile();
        } catch (SecurityException ex) {
            throw new MyFileException("FileManager.create()\n" + ex.getMessage(), true);
        } catch (IOException ex) {
            throw new MyFileException("FileManager.create()\n" + ex.getMessage(), false);
        }
        return file.exists();
    }

    /**
     * Method that add a boolean value in the file if binary
     *
     * @param value              the boolean to write
     * @param writeInExistedFile if true append at the end of the file, else at the beginning
     * @return true if it succeeded to write else false (for example: not a binary file)
     * @throws MyFileException when an error append throw this one instead
     */
    public boolean addBool(boolean value, boolean writeInExistedFile) throws MyFileException {
        if (binary) {
            try (DataOutputStream out = new DataOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(stringFile, writeInExistedFile)))) {
                out.writeBoolean(value);
                return true;
            } catch (FileNotFoundException ex) {
                throw new MyFileException("FileManager.addBool()\n" + ex.getMessage(), true);
            } catch (IOException ex) {
                throw new MyFileException("FileManager.addBool()\n" + ex.getMessage(), false);
            }
        }
        return false;
    }

    /**
     * Method that add a double value in the file if binary
     *
     * @param value              the double to write
     * @param writeInExistedFile if true append at the end of the file, else at the beginning
     * @return true if it succeeded to write else false (for example: not a binary file)
     * @throws MyFileException when an error append throw this one instead
     */
    public boolean addDouble(double value, boolean writeInExistedFile) throws MyFileException {
        if (binary) {
            try (DataOutputStream out = new DataOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(stringFile, writeInExistedFile)))) {
                out.writeDouble(value);
                return true;
            } catch (FileNotFoundException ex) {
                throw new MyFileException("FileManager.addDouble()\n" + ex.getMessage(), true);
            } catch (IOException ex) {
                throw new MyFileException("FileManager.addDouble()\n" + ex.getMessage(), false);
            }
        }
        return false;
    }

    /**
     * Method that add an int value in the file if binary
     *
     * @param value              the int to write
     * @param writeInExistedFile if true append at the end of the file, else at the beginning
     * @return true if it succeeded to write else false (for example: not a binary file)
     * @throws MyFileException when an error append throw this one instead
     */
    public boolean addInt(int value, boolean writeInExistedFile) throws MyFileException {
        if (binary) {
            try (DataOutputStream out = new DataOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(stringFile, writeInExistedFile)))) {
                out.writeInt(value);
                return true;
            } catch (FileNotFoundException ex) {
                throw new MyFileException("FileManager.addInt()\n" + ex.getMessage(), true);
            } catch (IOException ex) {
                throw new MyFileException("FileManager.addInt()\n" + ex.getMessage(), false);
            }
        }
        return false;
    }

    /**
     * Method that add an object value (typically a String) in the file
     *
     * @param value              the object to write (if not a String, write the .toString, except on a .obj file where it will write the object itself)
     * @param writeInExistedFile if true append at the end of the file, else at the beginning
     * @throws MyFileException when an error append throw this one instead
     */
    public <T> void add(T value, boolean writeInExistedFile) throws MyFileException {
        if (extension.equals("obj")) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(stringFile, writeInExistedFile))) {
                if (value != null)
                    objectOutputStream.writeObject(value);
            } catch (IOException ex) {
                throw new MyFileException("FileManager.add()\n" + ex.getMessage(), false);
            }
        } else if (binary) {
            try (DataOutputStream out = new DataOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(stringFile, writeInExistedFile)))) {
                if (value != null) {
                    out.writeUTF(value.toString());
                }
            } catch (FileNotFoundException ex) {
                throw new MyFileException("FileManager.add()\n" + ex.getMessage(), true);
            } catch (IOException ex) {
                throw new MyFileException("FileManager.add()\n" + ex.getMessage(), false);
            }
        } else {
            try (PrintWriter pw = new PrintWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(stringFile, writeInExistedFile), StandardCharsets.UTF_8))) {
                if (value != null) {
                    pw.println(value);
                }
            } catch (FileNotFoundException ex) {
                throw new MyFileException("FileManager.add()\n" + ex.getMessage(), true);
            }
        }
    }

    /**
     * Method that add an object value (typically a String) in the file
     *
     * @param values             the object to write (if not a String, write the .toString, except on a .obj file where it will write the object itself)
     * @param writeInExistedFile if true append at the end of the file, else at the beginning
     * @throws MyFileException when an error append throw this one instead
     */
    public <T> void addAll(Collection<T> values, boolean writeInExistedFile) throws MyFileException {
        if (extension.equals("obj")) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(stringFile, writeInExistedFile))) {
                if (values != null) {
                    for (T value : values) {
                        if (value == null) continue;
                        objectOutputStream.writeObject(value);
                    }
                } else {
                    throw new MyFileException("FileManager.addAll()\n Values is null, can't write children.", false);
                }
            } catch (IOException ex) {
                throw new MyFileException("FileManager.addAll()\n" + ex.getMessage(), false);
            }
        } else if (binary) {
            try (DataOutputStream out = new DataOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(stringFile, writeInExistedFile)))) {
                if (values != null) {
                    for (T value : values) {
                        if (value == null) continue;
                        out.writeUTF(value.toString());
                    }
                } else {
                    throw new MyFileException("FileManager.addAll\n Values is null, can't write children.", false);
                }
            } catch (FileNotFoundException ex) {
                throw new MyFileException("FileManager.addAll()\n" + ex.getMessage(), true);
            } catch (IOException ex) {
                throw new MyFileException("FileManager.addAll()\n" + ex.getMessage(), false);
            }
        } else {
            try (PrintWriter pw = new PrintWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(stringFile, writeInExistedFile), StandardCharsets.UTF_8));) {
                if (values != null) {
                    for (T value : values) {
                        if (value == null) continue;
                        pw.println(value);
                    }
                } else {
                    throw new MyFileException("FileManager.addAll()\n Values is null, can't write children.", false);
                }
            } catch (FileNotFoundException ex) {
                throw new MyFileException("FileManager.addAll()\n" + ex.getMessage(), true);
            }
        }
    }

    /**
     * Method that read a boolean (opposite of addBool() method) from the file
     *
     * @return the boolean ridden
     * @throws MyFileException when an error append throw this one instead
     */
    public boolean readBool() throws MyFileException {
        if (binary) {
            try (FileInputStream fis = new FileInputStream(stringFile); DataInputStream dis = new DataInputStream(fis)) {
                if (fis.available() > 0) {
                    return dis.readBoolean();
                }
            } catch (FileNotFoundException ex) {
                throw new MyFileException("FileManager.readBool()\n" + ex.getMessage(), true);
            } catch (IOException ex) {
                throw new MyFileException("FileManager.readBool()\n" + ex.getMessage(), false);
            }
        }
        throw new MyFileException("FileManager.readBool()\n Not a binary file ! Use Read() for other type of file.", false);
    }

    /**
     * Method that read a double (opposite of addDouble() method) from the file
     *
     * @return the double ridden
     * @throws MyFileException when an error append throw this one instead
     */
    public double readDouble() throws MyFileException {
        if (binary) {
            try (FileInputStream fis = new FileInputStream(stringFile); DataInputStream dis = new DataInputStream(fis)) {
                if (fis.available() > 0) {
                    return dis.readDouble();
                }
            } catch (FileNotFoundException ex) {
                throw new MyFileException("FileManager.readDouble()\n" + ex.getMessage(), true);
            } catch (IOException ex) {
                throw new MyFileException("FileManager.readDouble()\n" + ex.getMessage(), false);
            }
        }
        throw new MyFileException("FileManager.readDouble()\n Not a binary file ! Use Read() for other type of file.", false);
    }

    /**
     * Method that read an int (opposite of addInt() method) from the file
     *
     * @return the int ridden
     * @throws MyFileException when an error append throw this one instead
     */
    public int readInt() throws MyFileException {
        if (binary) {
            try (FileInputStream fis = new FileInputStream(stringFile); DataInputStream dis = new DataInputStream(fis)) {
                if (fis.available() > 0) {
                    return dis.readInt();
                }
            } catch (FileNotFoundException ex) {
                throw new MyFileException("FileManager.readInt()\n" + ex.getMessage(), true);
            } catch (IOException ex) {
                throw new MyFileException("FileManager.readInt()\n" + ex.getMessage(), false);
            }
        }
        throw new MyFileException("FileManager.readInt()\n Not a binary file ! Use Read() for other type of file.", false);
    }

    /**
     * Method that read an object (opposite of add() method) from the file
     *
     * @return the object ridden or null
     * @throws MyFileException when an error append throw this one instead
     */
    public <T> T read() throws MyFileException {
        if (extension.equals("obj")) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(stringFile));) {
                Object line;
                if ((line = objectInputStream.readObject()) != null) {
                    return (T) line;
                }
            } catch (FileNotFoundException ex) {
                throw new MyFileException("FileManager.read()\n" + ex.getMessage(), true);
            } catch (IOException | ClassNotFoundException ex) {
                throw new MyFileException("FileManager.read()\n" + ex.getMessage(), false);
            }
        } else if (binary) {
            try (FileInputStream fis = new FileInputStream(stringFile); DataInputStream dis = new DataInputStream(fis)) {
                if (fis.available() > 0) {
                    return (T) dis.readUTF();
                }
            } catch (FileNotFoundException ex) {
                throw new MyFileException("FileManager.read()\n" + ex.getMessage(), true);
            } catch (IOException ex) {
                throw new MyFileException("FileManager.read()\n" + ex.getMessage(), false);
            }
        } else {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(stringFile),
                            StandardCharsets.UTF_8))) {
                String line;
                if ((line = br.readLine()) != null) {
                    return (T) line;
                }
            } catch (FileNotFoundException ex) {
                throw new MyFileException("FileManager.read()\n" + ex.getMessage(), true);
            } catch (IOException ex) {
                throw new MyFileException("FileManager.read()\n" + ex.getMessage(), false);
            }
        }

        return null;
    }

    /**
     * Method that read n object from the line o file
     *
     * @param o offset (will read the line number o as if it was the first line)
     * @param n number of object to read (at max)
     * @return an ArrayList of n object (or less) ridden from the file
     * @throws MyFileException when an error append throw this one instead
     */
    public <T> ArrayList<T> reads(int o, int n) throws MyFileException {
        ArrayList<T> values = new ArrayList<>();
        if (extension.equals("obj")) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(stringFile));) {
                Object line;
                int i = 0;
                while ((line = objectInputStream.readObject()) != null && values.size() < n) {
                    if (i >= o) values.add((T) line);
                    else ++i;
                }
            } catch (IOException | ClassNotFoundException ex) {
                throw new MyFileException("FileManager.reads()\n" + ex.getMessage(), true);
            }
        } else if (binary) {
            try {
                FileInputStream fis;
                try {
                    fis = new FileInputStream(stringFile);
                } catch (FileNotFoundException ex) {
                    throw new MyFileException("FileManager.reads()\n" + ex.getMessage(), false);
                }
                DataInputStream dis = new DataInputStream(fis);
                int i = 0;
                while (fis.available() > 0 && values.size() < n) {
                    if (i >= o) values.add((T) dis.readUTF());
                    else i++;
                }
            } catch (IOException ex) {
                throw new MyFileException("FileManager.reads()\n" + ex.getMessage(), true);
            }
        } else {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(stringFile),
                            StandardCharsets.UTF_8))) {
                String line;
                int i = 0;
                while ((line = br.readLine()) != null && i < n) {
                    if (i >= o) values.add((T) line);
                    else ++i;
                }
            } catch (FileNotFoundException ex) {
                throw new MyFileException("FileManager.reads()\n" + ex.getMessage(), true);
            } catch (IOException ex) {
                throw new MyFileException("FileManager.reads()\n" + ex.getMessage(), false);
            }
        }

        return values;
    }

    /**
     * Method that read n object from the beginning of the file
     *
     * @param n number of object to read (at max)
     * @return an ArrayList of n object (or less) ridden from the file
     * @throws MyFileException when an error append throw this one instead
     */
    public <T> ArrayList<T> reads(int n) throws MyFileException {
        return reads(0, n);
    }

    /**
     * Method that read all line from the file as an object <T>
     *
     * @return an ArrayList of object (or empty) ridden from the file
     * @throws MyFileException when an error append throw this one instead
     */
    public <T> ArrayList<T> readAll() throws MyFileException {
        ArrayList<T> values = new ArrayList<>();
        if (extension.equals("obj")) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(stringFile));) {
                Object line;
                while ((line = objectInputStream.readObject()) != null) {
                    values.add((T) line);
                }
            } catch (IOException | ClassNotFoundException ex) {
                throw new MyFileException("FileManager.readAll()\n" + ex.getMessage(), true);
            }
        } else if (binary) {
            try {
                FileInputStream fis;
                try {
                    fis = new FileInputStream(stringFile);
                } catch (FileNotFoundException ex) {
                    throw new MyFileException("FileManager.readAll()\n" + ex.getMessage(), false);
                }
                DataInputStream dis = new DataInputStream(fis);
                while (fis.available() > 0) {
                    values.add((T) dis.readUTF());
                }
            } catch (IOException ex) {
                throw new MyFileException("FileManager.readAll()\n" + ex.getMessage(), true);
            }
        } else {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(stringFile),
                            StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    values.add((T) line);
                }
            } catch (FileNotFoundException ex) {
                throw new MyFileException("FileManager.readAll()\n" + ex.getMessage(), true);
            } catch (IOException ex) {
                throw new MyFileException("FileManager.readAll()\n" + ex.getMessage(), false);
            }
        }

        return values;
    }

    /**
     * Method who (try to) delete the file
     *
     * @return true if the file doesn't exist
     * @throws MyFileException if a SecurityException append
     */
    public boolean delete() throws MyFileException {
        File file = new File(stringFile);
        try {
            file.delete();
        } catch (SecurityException ex) {
            throw new MyFileException("FileManager.delete()\n" + ex.getMessage(), true);
        }
        return !file.exists();
    }
}
