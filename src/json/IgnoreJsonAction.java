package json;

public class IgnoreJsonAction
        implements JsonAction {
    @Override
    public boolean apply(JsonNode node) {
        return false;
    }
}
