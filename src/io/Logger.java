package io;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final String filename = "log.txt";
    private static Logger instance = null;
    private static PrintWriter out;
    private static DateTimeFormatter formatter;

    private Logger() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        try {
            out = new PrintWriter(new FileWriter(filename, true), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void log(String owner, String msg) {
        if (instance == null) {
            instance = new Logger();
        }
        out.println("[" + LocalDateTime.now().format(formatter) + "] " + owner.toUpperCase() + ": " + msg);
    }

}
