package mypfv;

import json.JsonNode;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DirSpec {
    private static final String PFV = "pfv";

    private final File from;
    private int frequency;
    private int logLevel;
    //    private int limitN = 3;
    private int limitM;
    private int limitH;
    private int limitD;
    private int limitW;
    //    private int limitT = 1;
    private final List<String> excluded;

    private long next;

    public static DirSpec mk(JsonNode node) {
        if (!node.hasKey("from"))
            return null;
        File from = new File(node.getString("from"));
        if (!from.exists() || !from.isDirectory())
            return null;
        DirSpec spec = new DirSpec(from);
        spec.frequency = node.getInt("frequency", Specifications.getFrequency());
        spec.logLevel = node.getInt("log", Specifications.getLogLevel());
//        spec.limitN = node.getInt("log", Specifications.getlimitN());
        spec.limitM = node.getInt("limitM", Specifications.getlimitM());
        spec.limitH = node.getInt("limitH", Specifications.getlimitH());
        spec.limitD = node.getInt("limitD", Specifications.getlimitD());
        spec.limitW = node.getInt("limitW", Specifications.getlimitW());
//        spec.limitT = node.getInt("limitT", Specifications.getlimitT());
        List toExclude = node.getArray("exclude");
        if (toExclude != null) {
            for (Object x: toExclude) {
                if (x instanceof String)
                    spec.excluded.add((String)x);
            }
        }
        return spec;
    }

    public DirSpec(File from) {
        this.from = from;
        excluded = Specifications.getExcluded();
    }

    public void doit(long now) {
        if (now < next)
            return;
        if (logLevel > 3)
            Logger.log("run " + from.getAbsolutePath());
        next = now + (long) frequency * 60 * 1000;
        doDirectory(from);
    }

    private void doDirectory(File file) {
        try {
            File to = new File(file, PFV);
            for (File srcFile : file.listFiles()) {
                if (srcFile.getName().charAt(0) == '.')
                    continue;
                if (srcFile.equals(to))
                    continue;
                if (tbe(srcFile))
                    continue;
                if (srcFile.isDirectory()) {
                    doDirectory(srcFile);
                    continue;
                }

                if (!to.exists())
                    to.mkdir();

                ImageLists images = ImageLists.mk(to, srcFile);
                Image1 newImg = null;
                Image1 last = images.last();
                if (last == null) {
                    newImg = Image1.mkNew(to, srcFile);
                } else {
                    long srcTime = srcFile.lastModified();
                    long imgTime = last.getImg().lastModified();
                    if (imgTime < srcTime)
                        newImg = Image1.mkNew(to, srcFile);
                }
                if (newImg == null)
                    continue;
                if (logLevel > 1)
                    Logger.log("new " + newImg);
                images.push(newImg);
                images.purge(logLevel, limitM, limitH, limitD, limitW);

//                if (limitT > 0) {
//                    LocalDateTime now = LocalDateTime.now();
//                    for (int i = 0; i < images.size() - 1; i++) {
//                        Image1 x = images.get(i);
//                        if (DAYS.between(x.getDate(), now) > limitT)
//                            x.getImg().delete();
//                    }
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // to be excluded
    private boolean tbe(File srcFile) {
        if (excluded == null || excluded.isEmpty())
            return false;
        String filename = srcFile.getName().toLowerCase();
        for (String pat : excluded) {
            try {
                char chS = pat.charAt(0);
                char chE = pat.charAt(pat.length() - 1);
                if (chS == '*' && chE == '*') {
                    if (filename.contains(pat.substring(1, pat.length() - 1)))
                        return true;
                }
                if (chS == '*') {
                    if (filename.endsWith(pat.substring(1)))
                        return true;
                }
                if (chE == '*') {
                    if (filename.startsWith(pat.substring(0, pat.length() - 1)))
                        return true;
                }
                if (chS != '*' && chE != '*') {
                    if (filename.equals(pat))
                        return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    public static void main(String[] args) {
        test1();
    }


    private static void test1() {
        DirSpec spec = new DirSpec(new File("C:/tmpp/example"));
        spec.doit(System.currentTimeMillis());
    }
}
