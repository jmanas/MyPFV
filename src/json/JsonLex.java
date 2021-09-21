package json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;

public class JsonLex {
    private final BufferedReader reader;
    private final Stack<Character> stack = new Stack<>();

    JsonLex(BufferedReader reader) {
        this.reader = reader;
    }

    public JToken next()
            throws IOException {
        int n = readChar();
        if (n < 0)
            return null;
        char ch = (char) n;
        if (Character.isWhitespace(ch))
            return next();
        if (ch == ',')
            return new JToken(JToken.COMMA);
        if (ch == ':')
            return new JToken(JToken.COLON);
        if (ch == '{')
            return new JToken(JToken.LEFT_BRACE);
        if (ch == '}')
            return new JToken(JToken.RIGHT_BRACE);
        if (ch == '[')
            return new JToken(JToken.OPEN_SQUARE_BRACKETS);
        if (ch == ']')
            return new JToken(JToken.CLOSE_SQUARE_BRACKETS);
        if (ch == '"')
            return JToken.mkString(mkString());
        if (ch == 'f')
            return tryKeyword("false");
        if (ch == 'n')
            return tryKeyword("null");
        if (ch == 't')
            return tryKeyword("true");
        if (ch >= '0' && ch <= '9')
            return mkNumber(ch);
        if (ch == '-')
            return mkNumber(ch);
//            Debug.print2("JsonLex.next()", ch);
        return JToken.mkString("");
    }

    private int readChar()
            throws IOException {
        if (stack.size() > 0)
            return stack.pop();
        return reader.read();
    }

    private JToken tryKeyword(String word)
            throws IOException {
        for (int at = 1; at < word.length(); at++) {
            char ch = (char) readChar();
            if (ch != word.charAt(at))
                return JToken.mkString(word.substring(0, at));
        }
        if (word.equalsIgnoreCase("null"))
            return JToken.mkNull();
        if (word.equalsIgnoreCase("true"))
            return JToken.mkBool(true);
        if (word.equalsIgnoreCase("false"))
            return JToken.mkBool(false);
        return JToken.mkString(word);
    }

    private String mkString()
            throws IOException {
        String s = "";
        for (; ; ) {
            int n = readChar();
            if (n < 0)
                return s;
            char ch = (char) n;
            if (ch == '"')
                return s;
            if (ch == '\\')
                s += getEscChar();
            else
                s += ch;
        }
    }

    private char getEscChar()
            throws IOException {
        int n2 = readChar();
        if (n2 < 0)
            throw new IOException("EOF");
        char ch2 = (char) n2;
        if (ch2 == '"')
            return ch2;
        if (ch2 == '\\')
            return ch2;
        if (ch2 == '/')
            return ch2;
        if (ch2 == 'b')
            return ' ';
        if (ch2 == 'n')
            return '\n';
        if (ch2 == 'r')
            return '\r';
        if (ch2 == 't')
            return '\t';
        if (ch2 == 'u') {
            String hex = "";
            for (int i = 0; i < 4; i++)
                hex += readChar();
            return (char) Integer.parseInt(hex, 16);
        }
        return ch2;
    }

    private JToken mkNumber(char ch)
            throws IOException {
        String s = String.valueOf(ch);
        for (; ; ) {
            int n = readChar();
            if (n < 0)
                throw new IOException("EOF");
            ch = (char) n;
            if (ch >= '0' && ch <= '9')
                s += ch;
            else
                break;
        }
        if (ch == '.') {
            s += ch;
            for (; ; ) {
                int n = readChar();
                if (n < 0)
                    throw new IOException("EOF");
                ch = (char) n;
                if (ch >= '0' && ch <= '9')
                    s += ch;
                else
                    break;
            }
        }
        stack.push(ch);
        try {
            return JToken.mkInt(Integer.parseInt(s));
        } catch (NumberFormatException ignored) {
        }
        try {
            return JToken.mkDouble(Double.parseDouble(s));
        } catch (NumberFormatException ignored) {
        }
        return JToken.mkString(s);
    }

    static class JToken {
        //        static final int STRING = 0;
//        static final int NUMBER = 1;
        static final int COMMA = 2;
        static final int COLON = 3;
        static final int LEFT_BRACE = 4;
        static final int RIGHT_BRACE = 5;
        static final int OPEN_SQUARE_BRACKETS = 6;
        static final int CLOSE_SQUARE_BRACKETS = 7;

        static final int NULL = 10;
        static final int BOOL = 11;
        static final int INT = 12;
        static final int DOUBLE = 13;
        static final int STRING = 14;

        final int type;
        Object value;

        JToken(int type) {
            this.type = type;
        }

        static JToken mkNull() {
            JToken token = new JToken(NULL);
            token.value = null;
            return token;
        }

        static JToken mkBool(boolean v) {
            JToken token = new JToken(BOOL);
            token.value = v;
            return token;
        }

        static JToken mkInt(int v) {
            JToken token = new JToken(INT);
            token.value = v;
            return token;
        }

        static JToken mkDouble(double v) {
            JToken token = new JToken(DOUBLE);
            token.value = v;
            return token;
        }

        static JToken mkString(String v) {
            JToken token = new JToken(STRING);
            token.value = v;
            return token;
        }

        public String toString() {
            if (type == NULL)
                return "null";
            if (value == null)
                return String.valueOf(type);
            return String.valueOf(value);
        }

        public boolean isObject() {
            return type == LEFT_BRACE;
        }

        public boolean isObjectEnd() {
            return type == RIGHT_BRACE;
        }

        public boolean isNull() {
            return type == NULL;
        }

        public boolean isBool() {
            return type == BOOL;
        }

        public boolean isInt() {
            return type == INT;
        }

        public boolean isDouble() {
            return type == DOUBLE;
        }

        public boolean isString() {
            return type == STRING;
        }

        public boolean isValue() {
            return type == NULL || type == BOOL || type == INT || type == DOUBLE || type == STRING;
        }

        public Object getValue() {
            return value;
        }

        public boolean isArray() {
            return type == OPEN_SQUARE_BRACKETS;
        }

        public boolean isArrayEnd() {
            return type == CLOSE_SQUARE_BRACKETS;
        }
    }

    public static void main(String[] args)
            throws IOException {
        String text = "[\"juan\", 1234, 12.34, true, \"2\\\"+\\\"2\" ]";
        BufferedReader reader = new BufferedReader(new StringReader(text));
        JsonLex lex = new JsonLex(reader);
        for (; ; ) {
            JToken token = lex.next();
            if (token == null)
                return;
            System.out.println(token);
        }
    }
}
