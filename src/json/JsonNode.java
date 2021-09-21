package json;

// Copyright (c) 2005-2019 Centro Criptologico Nacional
// Copyright (c) 2005-2019 Centro Nacional de Inteligencia
// Copyright (c) 2005-2019 A.L.H. J. Manas S.L.

import java.util.*;

public class JsonNode {
    private MyDictionary dictionary;
    private List array;

    private JsonNode() {
    }

    public static JsonNode mkObject() {
        JsonNode node = new JsonNode();
        node.dictionary = new MyDictionary();
        return node;
    }

    public static JsonNode mkArray() {
        JsonNode node = new JsonNode();
        node.array = new ArrayList();
        return node;
    }

    public void put(String key, int val) {
        if (dictionary == null)
            return;
        dictionary.put(key, val);
    }

    public void put(String key, double val) {
        if (dictionary == null)
            return;
//        dictionary.put(key, Double.toString(val));
        dictionary.put(key, val);
    }

    public void put(String key, Object val) {
        if (dictionary == null)
            return;
//        if (val == null)
//            val = "null";
        dictionary.put(key, val);
    }

    public Object del(String key) {
        if (dictionary == null)
            return null;
        return dictionary.del(key);
    }

    public void add(Object val) {
        if (array == null)
            return;
        array.add(val);
    }

    public int size() {
        if (dictionary != null)
            return dictionary.size();
        if (array != null)
            return array.size();
        return 0;
    }

    public Collection<String> getKeySet() {
        return dictionary.keySet();
    }

    public Object get(String key) {
        return dictionary.get(key);
    }

    public Object get(int idx) {
        return array.get(idx);
    }

    public String getString(String key) {
        return getStringFrom(dictionary.get(key), null);
    }

    public String getString(int idx) {
        return getStringFrom(array.get(idx), null);
    }

    public String getString(String key, String def) {
        return getStringFrom(dictionary.get(key), def);
    }

    public String getString(int idx, String def) {
        return getStringFrom(array.get(idx), def);
    }

    private String getStringFrom(Object object, String def) {
        if (object == null)
            return def;
        try {
//            if (object == null)
//                return "null";
            if (object instanceof Boolean) {
                Boolean b = (Boolean) object;
                return String.valueOf(b);
            }
            if (object instanceof Integer) {
                Integer v = (Integer) object;
                return String.valueOf(v);
            }
            if (object instanceof Double) {
                Double v = (Double) object;
                return String.valueOf(v);
            }
            if (object instanceof String)
                return (String) object;
        } catch (Exception ignored) {
        }
        return def;
    }

    public boolean getBoolean(String key, boolean def) {
        return getBooleanFrom(dictionary.get(key), def);
    }

    public boolean getBoolean(int idx, boolean def) {
        return getBooleanFrom(array.get(idx), def);
    }

    private boolean getBooleanFrom(Object object, boolean def) {
        if (object == null)
            return def;
        try {
            if (object instanceof Boolean)
                return (Boolean) object;
            if (object instanceof Integer)
                return def;
            if (object instanceof Double)
                return def;
            if (object instanceof String) {
                String s = (String) object;
                return getBoolean0(s.toLowerCase(), def);
            }
        } catch (Exception ignored) {
        }
        return def;
    }

    private boolean getBoolean0(String s, boolean def) {
        if (s.startsWith("t"))
            return true;
        if (s.startsWith("f"))
            return false;
        if (s.startsWith("y"))
            return true;
        if (s.startsWith("n"))
            return false;
        if (s.startsWith("on"))
            return true;
        if (s.startsWith("of"))
            return false;
        if (s.startsWith("1"))
            return true;
        if (s.startsWith("0"))
            return false;
        return def;
    }

    public int getInt(String key, int def) {
        return getIntFrom(dictionary.get(key), def);
    }

    public int getInt(int idx, int def) {
        return getIntFrom(array.get(idx), def);
    }

    private int getIntFrom(Object object, int def) {
        if (object == null)
            return def;
        try {
            if (object instanceof Boolean)
                return (Boolean) object ? 1 : 0;
            if (object instanceof Integer)
                return (Integer) object;
            if (object instanceof Double) {
                Double v = (Double) object;
                return v.intValue();
            }
            if (object instanceof String)
                return Integer.parseInt((String) object);
        } catch (Exception ignored) {
        }
        return def;
    }

    public double getDouble(String key, double def) {
        return getDoubleFrom(dictionary.get(key), def);
    }

    public double getDouble(int idx, double def) {
        return getDoubleFrom(array.get(idx), def);
    }

    private double getDoubleFrom(Object object, double def) {
        if (object == null)
            return def;
        try {
            if (object instanceof Boolean)
                return (Boolean) object ? 1.0 : 0.0;
            if (object instanceof Integer)
                return (Integer) object;
            if (object instanceof Double)
                return (Double) object;
            if (object instanceof String)
                return Double.parseDouble((String) object);
        } catch (Exception ignored) {
        }
        return def;
    }

    public JsonNode getNode(String key) {
        try {
            return (JsonNode) dictionary.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public JsonNode getNode(int idx) {
        try {
            return (JsonNode) array.get(idx);
        } catch (Exception e) {
            return null;
        }
    }

    public String getStringByPath(String... path) {
        return getStringByPathFrom(0, path);
    }

    private String getStringByPathFrom(int from, String... path) {
        try {
            String key = path[from];
            if (key.equals("[]"))
                return getStringByPathFrom(from + 1, path);
            if (key.equals("{}"))
                return getStringByPathFrom(from + 1, path);
            if (array != null) {
                Object object = array.get(0);
                JsonNode child = (JsonNode) object;
                return child.getStringByPathFrom(from, path);
            }
            if (from == path.length - 1)
                return getString(key);
            JsonNode child = (JsonNode) dictionary.get(key);
            return child.getStringByPathFrom(from + 1, path);
        } catch (Exception e) {
            return null;
        }
    }

    public JsonNode getNodeByPath(String... path) {
        return getNodeByPathFrom(0, path);
    }

    private JsonNode getNodeByPathFrom(int from, String... path) {
        try {
            String key = path[from];
            if (key.equals("[]"))
                return getNodeByPathFrom(from + 1, path);
            if (key.equals("{}"))
                return getNodeByPathFrom(from + 1, path);
            if (array != null) {
                Object object = array.get(0);
                JsonNode child = (JsonNode) object;
                return child.getNodeByPathFrom(from, path);
            }
            if (from == path.length - 1)
                return getNode(key);
            JsonNode child = (JsonNode) dictionary.get(key);
            return child.getNodeByPathFrom(from + 1, path);
        } catch (Exception e) {
            return null;
        }
    }

    public String toString() {
        if (dictionary != null)
            return dictionary.toString();
        if (array != null)
            return array.toString();
        return "{}";
    }

    public String toJson() {
        StringBuilder builder = new StringBuilder();
        toJson(builder);
        return builder.toString();
    }

    private void toJson(StringBuilder builder) {
        int n = 0;
        if (dictionary != null) {
            builder.append("{");
            for (String key : dictionary.keyList) {
                if (n++ > 0)
                    builder.append(",");
                builder.append("\"").append(key).append("\":");
                Object object = dictionary.get(key);
                String s = objectToString(object);
                if (s != null) {
                    builder.append(s);
                } else if (object instanceof JsonNode) {
                    JsonNode child = (JsonNode) object;
                    child.toJson(builder);
                } else
                    toString(builder, object.toString());
            }
            builder.append("}");
        } else if (array != null) {
            builder.append("[");
            for (Object object : array) {
                if (n++ > 0)
                    builder.append(",");
                String s = objectToString(object);
                if (s != null) {
                    builder.append(s);
                } else if (object instanceof JsonNode) {
                    JsonNode child = (JsonNode) object;
                    child.toJson(builder);
                } else
                    toString(builder, object.toString());
            }
            builder.append("]");
        }
    }

    static String objectToString(Object object) {
        if (object == null)
            return "null";
        if (object instanceof Boolean) {
            Boolean b = (Boolean) object;
            return b ? "true" : "false";
        }
        if (object instanceof Integer) {
            Integer v = (Integer) object;
            return String.valueOf((v));
        }
        if (object instanceof Double) {
            Double v = (Double) object;
            return String.valueOf((v));
        }
        return null;
    }

    static void toString(StringBuilder builder, String s) {
        if (s == null) {
            builder.append("null");
            return;
        }

        builder.append('"');
        for (char c : s.toCharArray()) {
            switch (c) {
                case '\\':
                case '"':
                    builder.append('\\').append(c);
                    break;
                default:
                    if (c < ' ') {
                        String t = "000" + Integer.toHexString(c);
                        builder.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        builder.append(c);
                    }
            }
        }
        builder.append('"');
    }

    public List<String> getStringList(String key) {
        try {
            JsonNode child = (JsonNode) dictionary.get(key);
            List<String> list = new ArrayList<>();
            for (Object x : child.array)
                list.add(getStringFrom(x, "null"));
            return list;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Set<String> getStringSet(String key) {
        try {
            JsonNode child = (JsonNode) dictionary.get(key);
            Set<String> set = new HashSet<>();
            for (Object x : child.array)
                set.add(getStringFrom(x, "null"));
            return set;
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    public List<JsonNode> getNodeList(String key) {
        try {
            JsonNode child = (JsonNode) dictionary.get(key);
            List<JsonNode> list = new ArrayList<>();
            for (Object x : child.array) {
                if (x instanceof JsonNode)
                    list.add((JsonNode) x);
            }
            return list;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public boolean hasKey(String key) {
        if (dictionary == null)
            return false;
        return dictionary.containsKey(key);
    }

    public MyDictionary getDictionary(String key) {
        try {
            JsonNode child = (JsonNode) dictionary.get(key);
            return child.dictionary;
        } catch (Exception e) {
            return null;
        }
    }

    public MyDictionary getDictionary() {
        return dictionary;
    }

    public List getArray() {
        return array;
    }

    public List getArray(String key) {
        try {
            JsonNode child = (JsonNode) dictionary.get(key);
            return child.array;
        } catch (Exception e) {
            return null;
        }
    }

    public void putLog(String label, String key, String val) {
        if (dictionary == null)
            return;
        if (key == null && val == null)
            return;
        JsonNode array = getLogArray(label + ".array");
        JsonNode node = JsonNode.mkObject();
        array.add(node);
        if (key != null)
            node.put("key", key);
        node.put("message", val);
    }

    private JsonNode getLogArray(String s) {
        try {
            JsonNode node = (JsonNode) dictionary.get(s);
            if (node.array != null)
                return node;
        } catch (Exception ignored) {
        }
        JsonNode node = JsonNode.mkArray();
        dictionary.put(s, node);
        return node;
    }

    public void putStringList(String key, List<String> list) {
        JsonNode array = mkArray();
        for (String s : list)
            array.add(s);
        put(key, array);
    }

    public static void main(String[] args) {
        test1();
    }

    private static void test1() {
        JsonNode node = JsonNode.mkObject();
        node.put("null", null);
        node.put("bool", true);
        node.put("int", 3);
        node.put("double", 3.1416);
        node.put("three", "tres");
        node.put("four", "2\"+\"2");
        node.put("five", "José A. Mañas");
        node.put("four", "2\"+\"3");
        String s = node.toJson();
        System.out.println(s);

        JsonNode node2 = JSON.loadString(s);
        String s2 = node2.toJson();
        System.out.println(s2);
    }

    public JsonNode son(String id) {
        try {
            return (JsonNode) dictionary.get(id);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static class MyDictionary {
        private final List<String> keyList = new ArrayList<>();
        private final List<Object> valList = new ArrayList<>();

        public void put(String key, Object object) {
            int idx = keyList.indexOf(key);
            if (idx < 0) {
                keyList.add(key);
                valList.add(object);
            } else {
                valList.set(idx, object);
            }
        }

        public int size() {
            return keyList.size();
        }

        public Collection<String> keySet() {
            return keyList;
        }

        public Object get(String key) {
            try {
                int idx = keyList.indexOf(key);
                return valList.get(idx);
            } catch (Exception e) {
                return null;
            }
        }

        public boolean containsKey(String key) {
            return keyList.contains(key);
        }

        public Object del(String key) {
            int idx = keyList.indexOf(key);
            if (idx < 0)
                return null;
            Object object = valList.get(idx);
            keyList.remove(idx);
            valList.remove(idx);
            return object;
        }
    }
}
