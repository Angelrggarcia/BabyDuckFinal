import java.util.HashMap;
import java.util.Map;

public class SemanticCube {
    private static final Map<String, Map<String, Map<String, String>>> cube = new HashMap<>();

    static {
        init();
    }

    private static void init() {
        // Operadores aritméticos
        add("int", "+", "int", "int");
        add("int", "-", "int", "int");
        add("int", "*", "int", "int");
        add("int", "/", "int", "int");

        add("float", "+", "float", "float");
        add("float", "-", "float", "float");
        add("float", "*", "float", "float");
        add("float", "/", "float", "float");

        add("int", "+", "float", "float");
        add("float", "+", "int", "float");
        add("int", "-", "float", "float");
        add("float", "-", "int", "float");
        add("int", "*", "float", "float");
        add("float", "*", "int", "float");
        add("int", "/", "float", "float");
        add("float", "/", "int", "float");

        // Operadores relacionales
        for (String t : new String[]{"int", "float", "bool"}) {
            add(t, "==", t, "bool");
            add(t, "!=", t, "bool");
            add(t, "<", t, "bool");
            add(t, "<=", t, "bool");
            add(t, ">", t, "bool");
            add(t, ">=", t, "bool");
        }

        // Operadores lógicos
        add("bool", "&&", "bool", "bool");
        add("bool", "||", "bool", "bool");
        add("bool", "!", "", "bool");

        // Operadores con string
        add("string", "+", "string", "string");
        add("string", "==", "string", "bool");
        add("string", "!=", "string", "bool");

        // in operator (ejemplo simple con listas o strings)
        add("int", "in", "list", "bool");
        add("string", "in", "string", "bool");

        // Asignación directa
        add("int", "=", "int", "ok");
        add("float", "=", "float", "ok");
        add("bool", "=", "bool", "ok");
        add("string", "=", "string", "ok");

        // Asignación con coerción implícita permitida
        add("float", "=", "int", "ok");
        add("float", "=", "string", "ok");
        add("string", "=", "int", "ok");
        add("string", "=", "float", "ok");

        // Relacionales entre int y float (y viceversa)
        add("int", "==", "float", "bool");
        add("float", "==", "int", "bool");
        add("int", "!=", "float", "bool");
        add("float", "!=", "int", "bool");
        add("int", "<",  "float", "bool");
        add("float", "<", "int", "bool");
        add("int", "<=", "float", "bool");
        add("float", "<=", "int", "bool");
        add("int", ">",  "float", "bool");
        add("float", ">", "int", "bool");
        add("int", ">=", "float", "bool");
        add("float", ">=", "int", "bool");

    }

    private static void add(String left, String op, String right, String result) {
        cube.computeIfAbsent(left, k -> new HashMap<>())
                .computeIfAbsent(op, k -> new HashMap<>())
                .put(right, result);
    }

    public static String get(String left, String op, String right) {
        return cube.getOrDefault(left, Map.of())
                .getOrDefault(op, Map.of())
                .getOrDefault(right, "error");
    }
}
