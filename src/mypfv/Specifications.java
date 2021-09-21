package mypfv;

import json.JSON;
import json.JsonNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Specifications {
    private static File specfile;

    private static int logLevel;

    private static int frequency = 1;
    private static int limitN = -1;
    private static int limitM = -1;
    private static int limitH = -1;
    private static int limitD = -1;
    private static int limitW = -1;
    //    private static int limitT = -1;
    private static final List<String> excludeList = new ArrayList<>();

    private static final List<DirSpec> dirSpecList = new ArrayList<>();

    public static void load(File specfile)
            throws IOException {
        Specifications.specfile = specfile;
        String jsontext = new String(Files.readAllBytes(specfile.toPath()));
        JsonNode node = JSON.loadString(jsontext);
        frequency = node.getInt("frequency", 1);
        logLevel = node.getInt("log", 1);
        limitN = node.getInt("limitN", 5);
        limitM = node.getInt("limitM", limitN);
        limitH = node.getInt("limitH", limitN);
        limitD = node.getInt("limitD", limitN);
        limitW = node.getInt("limitW", limitN);
//        limitT = node.getInt("limitT", -1);
        loadExclude(node.getArray("exclude"));
        loadDirSpec(node.getArray("directories"));
    }

    private static void loadExclude(List array) {
        if (array == null)
            return;
        for (Object object : array) {
            if (object instanceof String)
                excludeList.add((String) object);
        }
    }

    private static void loadDirSpec(List array) {
        if (array == null)
            return;
        for (Object object : array) {
            try {
                JsonNode child = (JsonNode) object;
                DirSpec spec = DirSpec.mk(child);
                if (spec != null)
                    dirSpecList.add(spec);
            } catch (Exception ignored) {
            }
        }
    }

    public static File getSpecfile() {
        return specfile;
    }

    public static int getLogLevel() {
        return logLevel;
    }

    public static int getFrequency() {
        return frequency;
    }

    public static int getlimitN() {
        return limitN;
    }

    public static int getlimitM() {
        return limitM;
    }

    public static int getlimitH() {
        return limitH;
    }

    public static int getlimitD() {
        return limitD;
    }

    public static int getlimitW() {
        return limitW;
    }

//    public static int getlimitT() {
//        return limitT;
//    }

    public static List<String> getExcluded() {
        return excludeList;
    }

    public static int size() {
        return dirSpecList.size();
    }

    public static List<DirSpec> getDirSpecList() {
        return dirSpecList;
    }
}
