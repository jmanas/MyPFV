package json;

import java.io.*;
import java.util.Set;

public class JSON {

    public static JsonNode loadFile(String filename)
            throws IOException {
        return loadFile(new File(filename));
    }

    public static JsonNode loadFile(File file)
            throws IOException {
        return loadFile(file, null);
    }

    public static JsonNode loadFile(File file, Set<JsonRule> ruleSet)
            throws IOException {
        InputStream is = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        return load(reader, ruleSet);
    }

    public static JsonNode loadString(String text) {
        BufferedReader reader = new BufferedReader(new StringReader(text));
        return load(reader, null);
    }

    public static JsonNode load(BufferedReader reader, Set<JsonRule> ruleSet) {
        JsonLex lex = new JsonLex(reader);
        JsonParse parse = new JsonParse(lex, ruleSet);
        return parse.getRoot();
    }
}
