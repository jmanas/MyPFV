package json;

// Copyright (c) 2005-2019 Centro Criptologico Nacional
// Copyright (c) 2005-2019 Centro Nacional de Inteligencia
// Copyright (c) 2005-2019 A.L.H. J. Manas S.L.

import java.io.IOException;
import java.util.Set;
import java.util.Stack;

public class JsonParse {
    private final JsonLex lex;
    private final Set<JsonRule> ruleSet;

    private final Stack<String> stack = new Stack<>();
    private JsonNode root;

    JsonParse(JsonLex lex) {
        this(lex, null);
    }

    JsonParse(JsonLex lex, Set<JsonRule> ruleSet) {
        this.lex = lex;
        this.ruleSet = ruleSet;
    }

    public JsonNode getRoot() {
        try {
            if (root == null)
                root = mkValue(lex.next());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    private JsonNode mkValue(JsonLex.JToken token)
            throws IOException {
        if (token.isValue()) {
            JsonNode node = JsonNode.mkArray();
            node.add(token.getValue());
            return node;
        }
        if (token.isObject()) {
            JsonNode node = JsonNode.mkObject();
            stack.push("{}");
            token = lex.next();
            for (; ; ) {
                if (token.isObjectEnd()) {
                    stack.pop();
                    return node;
                }
                if (token.isString()) {
                    String key = token.toString();
                    stack.push(key);
                    token = lex.next();
                    if (token.type == JsonLex.JToken.COLON)
                        token = lex.next();
                    if (token.isValue())
                        node.put(key, token.getValue());
                    else {
                        JsonNode n2 = mkValue(token);
                        if (n2 == null)
                            return null;
                        if (applyRules(n2))
                            node.put(key, n2);
                    }
                    stack.pop();
                }
                token = lex.next();
                if (token == null)
                    return node;
                if (token.type == JsonLex.JToken.COMMA)
                    token = lex.next();
            }
        }
        if (token.isArray()) {
            JsonNode node = JsonNode.mkArray();
            stack.push("[]");
            token = lex.next();
            for (; ; ) {
                if (token.isArrayEnd()) {
                    stack.pop();
                    return node;
                }
                if (token.isValue())
                    node.add(token.getValue());
                else {
                    JsonNode n2 = mkValue(token);
                    if (n2 == null)
                        return null;
                    if (applyRules(n2))
                        node.add(n2);
                }
                token = lex.next();
                if (token == null)
                    break;
                if (token.type == JsonLex.JToken.COMMA)
                    token = lex.next();
            }
        }
        JsonNode errorNode = JsonNode.mkObject();
        errorNode.put("error", token);
        return errorNode;
    }

    private boolean applyRules(JsonNode node) {
        if (ruleSet == null)
            return true;
        for (JsonRule rule : ruleSet) {
            if (rule.getPath().match(stack))
                return rule.getAction().apply(node);
        }
        return true;
    }

    private void check(JsonLex.JToken token, int type) {
        if (token.type != type)
            System.err.println("JsonParse.check() " + token);
    }

    public static void main(String[] args) {
        test("[\"juan\", 1234, 12.34, true ]");
        test("[\"juan\" , 1234 , 12.34 true, ]");
        test("{\"name\": \"juan\", \"n1\": 1234, \"n2\": 12.34,  \"bool\": true }");
        test("{\"name\" : \"juan\" , \"n1\" : 1234 , \"n2\" : 12.34 \"bool\" : true, }");
        test("{\n" +
                "\"employees\":[\n" +
                "  {\"firstName\":\"John\", \"lastName\":\"Doe\"}, \n" +
                "  {\"firstName\":\"Anna\", \"lastName\":\"Smith\"},\n" +
                "  {\"firstName\":\"Peter\", \"lastName\":\"Jones\"}\n" +
                "]\n" +
                "}");
        test("[\n" +
                "  {\"firstName\":\"John\", \"lastName\":\"Doe\"}, \n" +
                "  {\"firstName\":\"Anna\", \"lastName\":\"Smith\"},\n" +
                "  {\"firstName\":\"Peter\", \"lastName\":\"Jones\"}\n" +
                "]");
    }

    private static void test(String text) {
        System.out.println(text);
//        BufferedReader reader = new BufferedReader(new StringReader(text));
//        JsonLex lex = new JsonLex(reader);
//        JsonParse parse= new JsonParse(lex);
//        JsonNode root = parse.getRoot();
        JsonNode root = JSON.loadString(text);
        System.out.println(root);
        System.out.println();
    }

}
