package json;

import java.io.IOException;

public class JsonTest {
    public static void main(String[] args)
            throws IOException {
        JsonNode root = JSON.loadFile(args[0]);
        System.out.println(root.toJson());
    }
}
