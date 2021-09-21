package mypfv;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class MyPFV {
    public static void main(String[] args)
            throws IOException {
        Preferences preferences = Preferences.userRoot().node("mypfv");

        File spec = null;
        if (args.length == 0) {
            String last = preferences.get("root", null);
            if (last != null)
                spec = new File(last);
        } else {
            spec = new File(args[0]);
        }
        if (spec == null)
            return;
        preferences.put("root", spec.getCanonicalPath());
        Specifications.load(spec);
        if (Specifications.size() > 0)
            start();
    }

    private static void start() {
        if (Specifications.getLogLevel() > 0)
            Logger.log("start " + Specifications.getSpecfile().getAbsolutePath());
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
