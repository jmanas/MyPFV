package json;

public interface JsonAction {
    /**
     * @param node
     * @return true to continue tree building; false to skip node from tree.
     */
    boolean apply(JsonNode node);
}
