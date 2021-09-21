package mypfv;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class MyPFV {
    public static void main(String[] args)
            throws IOException {
        help(args);
        version(args);

        File spec = new File(args[0]);
        Specifications.load(spec);
        if (Specifications.size() > 0)
            start();
    }

    private static boolean help(String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("-h")) {
            System.out.println("MyPFV spec.json");
            System.out.println("MyPFV -v  -- print version");
            System.out.println("MyPFV -h  -- print help");
            return true;
        }
        return false;
    }

    private static boolean version(String[] args) {
        if (args[0].equalsIgnoreCase("-h")) {
            System.out.println("MyPFV: " + Version.VERSION);
            return true;
        }
        return false;
    }

    private static void start() {
        if (Specifications.getLogLevel() > 0)
            Logger.log(String.format("start %s: %s",
                    Specifications.getSpecfile().getAbsolutePath(),
                    Version.VERSION));
        do {
            try {
                long now = System.currentTimeMillis();
                for (DirSpec spec : Specifications.getDirSpecList())
                    spec.doit(now);
                Thread.sleep(60 * 1000);    // 1 min
            } catch (InterruptedException ignored) {
            }
        } while (true);
    }
}
