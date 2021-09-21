package json;

import java.util.Stack;

public class JsonPath {
    private final String[] path;

    public JsonPath(String... path) {
        this.path = path;
    }

    public boolean match(Stack<String> stack) {
        if (stack.size() < path.length)
            return false;
        for (int i = 0; i < path.length; i++) {
            int stackAt = stack.size() - i - 1;
            int pathAt = path.length - i - 1;
            if (!stack.get(stackAt).equals(path[pathAt]))
                return false;
        }
        return true;
    }
}
