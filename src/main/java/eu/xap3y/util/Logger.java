package eu.xap3y.util;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
public class Logger {
    private static Logger instance;
    private PrintWriter fileWriter;
    private static final String LOG_PATH = "logs";
    private static final String LOG_FILE = "l.txt";

    public enum LogType {
        INFO, WARN, ERROR, DEBUG
    }

    private Logger() {
        try {
            File logFile = new File(LOG_PATH);
            logFile.mkdirs();
            this.fileWriter = new PrintWriter(new FileWriter(LOG_PATH + "/" + LOG_FILE, true));
            info("=============START=============");
        } catch (IOException e) {
            System.err.println("Logger: Failed to enable file logging: " + e.getMessage());
            this.fileWriter = null;
        }
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void info(String message) {
        log(LogType.INFO, message);
    }

    public void ok(String message) {
        info("✅ " + message);
    }

    public void ok(String message, Object... args) {
        info("✅ " + message, args);
    }

    public void info(String message, Object... args) {
        log(LogType.INFO, format(message, args));
    }

    public void warn(String message) {
        log(LogType.WARN, message);
    }

    public void warn(String message, Object... args) {
        log(LogType.WARN, format(message, args));
    }


    public void err(String message) {
        log(LogType.ERROR, message);
    }

    public void err(String message, Object... args) {
        log(LogType.ERROR, format(message, args));
    }

    public void debug(String message) {
        log(LogType.DEBUG, message);
    }

    public void debug(String message, Object... args) {
        log(LogType.DEBUG, format(message, args));
    }

    // Aby slo napr .ingo("Test message: {} {}", "arg1", "arg2"), napodobuje log4j styl
    private String format(String message, Object... args) {
        if (args == null || args.length == 0) return message;
        StringBuilder sb = new StringBuilder();
        int argIndex = 0;
        int last = 0;
        for (int i = 0; i < message.length(); i++) {
            if (i + 1 < message.length() && message.charAt(i) == '{' && message.charAt(i + 1) == '}') {
                sb.append(message, last, i);
                if (argIndex < args.length) {
                    sb.append(args[argIndex] != null ? args[argIndex].toString() : "null");
                } else {
                    sb.append("{}");
                }
                argIndex++;
                i++;
                last = i + 1;
            }
        }
        sb.append(message.substring(last));
        return sb.toString();
    }


    private void log(LogType type, String message) {
        String ts = "[" + LocalDateTime.now() + "] " + type.name() + ": " + message;
        if (type == LogType.ERROR) {
            System.err.println(ts);
        } else {
            System.out.println(ts);
        }
        if (fileWriter != null) {
            fileWriter.println(ts);
            fileWriter.flush();
        }
    }

    public void close() {
        if (fileWriter != null) {
            info("Logger: Closing file writer.");
            fileWriter.close();
        }
    }
}
