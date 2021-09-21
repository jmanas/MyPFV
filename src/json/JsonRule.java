package json;

public class JsonRule {
    private final JsonPath path;
    private final JsonAction action;

    public JsonRule(JsonPath path, JsonAction action) {
        this.path = path;
        this.action = action;
    }

    public JsonPath getPath() {
        return path;
    }

    public JsonAction getAction() {
        return action;
    }
}
