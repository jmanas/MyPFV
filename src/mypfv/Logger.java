package mypfv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private static PrintWriter writer;

    private static PrintWriter getWriter()
            throws IOException {
        if (writer == null) {
            File specFile = Specifications.getSpecfile();
            String specName = specFile.getName();
            int ext = specName.lastIndexOf('.');
            String prfx = ext < 0 ? specName : specName.substring(0, ext);
            String logName = prfx + ".log";
            File logFile = new File(specFile.getParentFile(), logName);
//            FileWriter fileWriter = new FileWriter(logFile, true);
            FileWriter fileWriter = new FileWriter(logFile);
            writer = new PrintWriter(fileWriter, true);
        }
        return writer;
    }

    public static void log(String msg) {
        try {
            LocalDateTime now = LocalDateTime.now();
            String label = DATE_FORMATTER.format(now);
            getWriter().println(label + " " + msg);
        } catch (Exception ignored) {
        }
    }
}
