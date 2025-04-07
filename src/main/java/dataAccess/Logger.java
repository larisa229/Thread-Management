package dataAccess;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    private static BufferedWriter writer;

    public static void init(String filename) {
        try {
            writer = new BufferedWriter(new FileWriter(filename));
        } catch (IOException e) {
            System.err.println("Could not initialize log file: " + e.getMessage());
        }
    }

    public static void log(String message) {
        try {
            System.out.print(message);
            if (writer != null) {
                writer.write(message);
                writer.flush();
            }
        } catch (IOException e) {
            System.err.println("Error writing log: " + e.getMessage());
        }
    }

    public static void close() {
        try {
            if (writer != null) writer.close();
        } catch (IOException e) {
            System.err.println("Error closing log file: " + e.getMessage());
        }
    }
}