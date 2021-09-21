package json;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Stack;

/**
 * Produces JSON files.
 *
 * @author Jose A. Manas
 * @version 9.12.2017
 */
public class JsonWriter {
    private final PrintWriter writer;
    private final Stack<OAN> stack = new Stack<>();
    private int outmost;
    private final StringBuilder buffer = new StringBuilder();

    private boolean opening = false;

    public JsonWriter(File file)
            throws FileNotFoundException, UnsupportedEncodingException {
        writer = new PrintWriter(file, StandardCharsets.UTF_8.name());
//        CharEncoding.writeBOM(writer, "UTF-8");
        writer.print("{");
    }

    public JsonWriter(PrintWriter writer) {
        this.writer = writer;
//        CharEncoding.writeBOM(writer, "UTF-8");
        writer.print("{");
    }

    private JsonWriter(OutputStream os) {
        writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
//        CharEncoding.writeBOM(writer, encoding);
        this.writer.print("{");
    }

    public static String mkFileName(String... parts) {
        if (parts == null || parts.length == 0)
            return "file.json";
        StringBuilder builder = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            builder.append('_');
            for (char ch : parts[i].toCharArray())
                builder.append(sanitize(ch));
        }
        return builder + ".json";
    }

    private static char sanitize(char ch) {
        if (Character.isLetterOrDigit(ch))
            return ch;
        if (ch == '_' || ch == '-' || ch == '.' || ch == ' ')
            return ch;
        return '_';
    }

    public void close() {
        writer.println("}");
        writer.close();
    }

    public void openTag(String tag) {
        closeOpenTag();
        separatingComma();
        OAN current = getCurrent();
        if (current == null || current.jsonArray == null)
            buffer.append("\"").append(tag).append("\": ");

        stack.push(new OAN(tag, null));
        buffer.append("{");
        opening = true;
    }

    private void separatingComma() {
        OAN current = getCurrent();
        if (current == null) {
            if (outmost++ > 0)
                writer.print(", ");
        } else {
            if (current.count++ > 0)
                writer.print(", ");
        }
    }

    private OAN getCurrent() {
        try {
            return stack.peek();
        } catch (Exception ignored) {
            return null;
        }
    }

    private void closeOpenTag() {
        if (opening) {
//            buffer.append("},");
            writer.print(buffer);
            clear();
        }
        opening = false;
    }

    public void closeTag() {
        closeTag(null);
    }

    public void closeTag(String tag) {
        if (stack.isEmpty())
            throw new IllegalArgumentException("empy stack while writing " + tag);
        OAN current = stack.pop();
        buffer.append("}");
        writer.print(buffer);
        clear();
        if (tag != null && !tag.equals(current.jsonObject))
            writeTag("JSON writer", tag + " closing " + current);
        opening = false;
    }

    public void openArray(String tag) {
        separatingComma();

        stack.push(new OAN(null, tag));
        buffer.append("\"").append(tag).append("\": [");
        opening = true;
    }

    public void closeArray() {
        closeArray(null);
    }

    public void closeArray(String tag) {
        if (stack.isEmpty())
            throw new IllegalArgumentException("empy stack while writing " + tag);
        OAN current = stack.pop();
        buffer.append("]");
        writer.print(buffer);
        clear();
        if (tag != null && !tag.equals(current.jsonArray))
            writeTag("JSON writer", tag + " closing " + current);
        opening = false;
    }

    public void attribute(String name, boolean value) {
        writeTag("-" + name, value);
    }

    public void attribute(String name, int value) {
        writeTag("-" + name, value);
    }

    public void attribute(String name, long value) {
        writeTag("-" + name, value);
    }

    public void attribute(String name, double value) {
        writeTag("-" + name, value);
    }

    public void attribute(String name, String format, double value) {
        writeTag("-" + name, String.format(format, value));
    }

    public void attribute(String name, String value) {
        writeTag("-" + name, value);
    }

    public void attribute(String name, Object x) {
        closeOpenTag();
        separatingComma();

        OAN current = getCurrent();
        if (current == null || current.jsonArray == null)
            buffer.append("\"").append(name).append("\": ");
        String s = JsonNode.objectToString(x);
        if (s != null)
            buffer.append(s);
        else
            JsonNode.toString(buffer, x.toString());
        writer.print(buffer);
        clear();
    }

    public void write(String text) {
        writeTag("#text", text);
    }

    public void writeTag(String tag, boolean b) {
        closeOpenTag();
        separatingComma();

        OAN current = getCurrent();
        if (current == null || current.jsonArray == null)
            buffer.append("\"").append(tag).append("\": ");
        buffer.append(b ? "true" : "false");
        writer.print(buffer);
        clear();
    }

    /**
     * Prints &lt;tag&gt;text&lt;/tag%gt.
     *
     * @param tag  label.
     * @param text contents.
     */
    public void writeTag(String tag, String text) {
        closeOpenTag();
        separatingComma();

        OAN current = getCurrent();
        if (current == null || current.jsonArray == null)
            buffer.append("\"").append(tag).append("\": ");

        JsonNode.toString(buffer, text);
        writer.print(buffer);
        clear();
    }

    void writeTag(String tag, int n) {
        closeOpenTag();
        separatingComma();

        OAN current = getCurrent();
        if (current == null || current.jsonArray == null)
            buffer.append("\"").append(tag).append("\": ");
        buffer.append(n);
        writer.print(buffer);
        clear();
    }

    void writeTag(String tag, long n) {
        closeOpenTag();
        separatingComma();

        OAN current = getCurrent();
        if (current == null || current.jsonArray == null)
            buffer.append("\"").append(tag).append("\": ");
        buffer.append(n);
        writer.print(buffer);
        clear();
    }

    public void writeTag(String tag, double v) {
        closeOpenTag();
        separatingComma();

        OAN current = getCurrent();
        if (current == null || current.jsonArray == null)
            buffer.append("\"").append(tag).append("\": ");
        buffer.append(minimal(v));
        writer.print(buffer);
        clear();
    }

    private void clear() {
        buffer.delete(0, buffer.length());
    }

    public void closeAllTags() {
        closeOpenTag();
        while (!stack.empty())
            closeTag();
    }

    public static void main(String[] args) {
//        test0();
//        test1();
        test2();
        test3();
    }

    private static void test0() {
        JsonWriter writer = new JsonWriter(System.out);
        writer.openTag("test");
        writer.writeTag("null", null);
        writer.writeTag("bool", true);
        writer.writeTag("int", 3);
        writer.writeTag("double", 3.1416);
        writer.writeTag("three", "tres");
        writer.writeTag("four", "2\"+\"2");
        writer.writeTag("five", "José A. Mañas");
        writer.writeTag("four", "2\"+\"3");

        writer.closeTag();
        writer.close();
    }

    private static void test1() {
        JsonWriter writer = new JsonWriter(System.out);
        writer.openTag("anagrafica");
        writer.openTag("testdata");
        writer.openTag("nomemercato");
        writer.attribute("id", "007");
        writer.write("Mercato di \"test\"");
        writer.closeTag();
        writer.writeTag("data", "Giovedi 18 dicembre 2003 16.05.29");
        writer.closeTag("testdata");

        writer.openArray("record");
        writer.openTag("record");
        writer.writeTag("codice_cliente", 5);
        writer.writeTag("rag_soc", "Miami American Cafe");
        writer.writeTag("codice_fiscale", "IT07654930130");
        writer.openTag("indirizzo");
        writer.attribute("tipo", "casa");
        writer.write("Viale Carlo Espinasse 5, Como");
        writer.closeTag();
        writer.writeTag("num_prodotti", 13);
        writer.closeTag("record");
        writer.closeArray("record");

        writer.closeTag("anagrafica");
        writer.close();
    }

    private static void test2() {
        JsonWriter writer = new JsonWriter(System.out);
        writer.writeTag("id", 1);
        writer.writeTag("name", "Foo");
        writer.writeTag("price", 123.50);
        writer.openArray("tags");
        writer.writeTag("tags", "Bar");
        writer.writeTag("tags", "Eek");
        writer.closeArray();
        writer.openTag("stock");
        writer.writeTag("warehouse", 300);
        writer.writeTag("retail", 20);
        writer.writeTag("boolean", false);
        writer.writeTag("nop", null);
        writer.closeTag();
        writer.close();
    }

    private static void test3() {
        JsonWriter writer = new JsonWriter(System.out);
        writer.openArray("");
        writer.openTag("");
        writer.writeTag("userid", 1);
        writer.writeTag("id", 1);
        writer.writeTag("title", "delectus aut autem");
        writer.writeTag("completed", false);
        writer.closeTag();
        writer.openTag("");
        writer.writeTag("userid", 2);
        writer.writeTag("id", 2);
        writer.writeTag("title", "quis ut nam facilis et officia qui");
        writer.writeTag("completed", true);
        writer.closeTag();
        writer.closeArray();
        writer.close();
    }

    private static class OAN {
        String jsonObject;
        String jsonArray;
        int count;

        OAN(String objectTag, String arrayTag) {
            jsonObject = objectTag;
            jsonArray = arrayTag;
        }

        @Override
        public String toString() {
            if (jsonObject != null)
                return jsonObject;
            if (jsonArray != null)
                return jsonArray;
            return "...";
        }
    }

    static String minimal(double n) {
        if (n == 0)
            return "0";

        double m = Math.abs(n);
        double factor = 1;
        while (m < 199.5) {
            factor *= 10;
            m *= 10;
        }
        while (m > 1999.5) {
            factor /= 10;
            m /= 10;
        }
        m = Math.floor(m + 0.5) / factor;
        if (n < 0)
            n = -m;
        else
            n = m;

        return reduceDigits(String.format((Locale) null, "%.20f", n));
    }

    private static String reduceDigits(String s) {
        StringBuilder builder = new StringBuilder();

        int state = 0;
        int neededDigits = 100;
        for (int at = 0; at < s.length(); at++) {
            char c = s.charAt(at);
            switch (state) {
                case 0: // starting
                    if (c == '.') {
                        if (neededDigits <= 0)
                            return builder.toString();
                        if (builder.length() == 0)
                            builder.append('0');
                        builder.append('.');
                        state = 10;
                    } else if (c == '0') {
                        builder.append('0');
                        neededDigits--;
                    } else if (c == '1') {
                        if (neededDigits > 10)
                            neededDigits = 4;
                        if (neededDigits > 0)
                            builder.append(c);
                        else
                            builder.append('0');
                        neededDigits--;
                    } else {
                        if (neededDigits > 10)
                            neededDigits = 3;
                        if (neededDigits > 0)
                            builder.append(c);
                        else
                            builder.append('0');
                        neededDigits--;
                    }
                    break;
                case 10:
                    if (neededDigits <= 0)
                        return noTail(builder);
                    neededDigits--;
                    builder.append(c);
                    if (neededDigits > 10) {
                        if (c == '1')
                            neededDigits = 3;
                        else if (c > '1')
                            neededDigits = 2;
                    }
            }
        }
        return builder.toString();
    }

    private static String noTail(StringBuilder builder) {
        int last = builder.length() - 1;
        while (builder.charAt(last) == '0')
            last--;
        if (builder.charAt(last) == '.')
            last--;
        return builder.substring(0, last + 1);
    }
}
