import java.util.HashMap;
import java.util.Map;

public class VirtualMemoryManager {
    private final Map<String, MemorySegment> segments = new HashMap<>();
    private final Map<String, Integer> symbolTable = new HashMap<>();
    private final Map<Integer, Object> constantValues = new HashMap<>();

    public VirtualMemoryManager() {
        initSegments();
    }

    public Map<String, Integer> getSymbolTable() {
        return symbolTable;
    }

    public Map<Integer, Object> getConstantValues() {
        return constantValues;
    }


    private Object parseValue(String value, String type) {
        return switch (type) {
            case "int" -> Integer.parseInt(value);
            case "float" -> Float.parseFloat(value);
            case "bool" -> value.equals("1") || value.equalsIgnoreCase("true");
            case "string" -> value;
            default -> throw new RuntimeException("Tipo no soportado: " + type);
        };
    }

    private void initSegments() {
        // Global
        segments.put("global_int", new MemorySegment(1000, 2000));
        segments.put("global_float", new MemorySegment(2000, 3000));
        segments.put("global_bool", new MemorySegment(3000, 4000));
        segments.put("global_string", new MemorySegment(4000, 5000));

        // Local
        segments.put("local_int", new MemorySegment(5000, 6000));
        segments.put("local_float", new MemorySegment(6000, 7000));
        segments.put("local_bool", new MemorySegment(7000, 8000));
        segments.put("local_string", new MemorySegment(8000, 9000));

        // Temporal
        segments.put("temp_int", new MemorySegment(9000, 10000));
        segments.put("temp_float", new MemorySegment(10000, 11000));
        segments.put("temp_bool", new MemorySegment(11000, 12000));
        segments.put("temp_string", new MemorySegment(12000, 13000));

        // Constantes
        segments.put("const_int", new MemorySegment(13000, 14000));
        segments.put("const_float", new MemorySegment(14000, 15000));
        segments.put("const_bool", new MemorySegment(15000, 16000));
        segments.put("const_string", new MemorySegment(16000, 17000));
    }

    public int allocate(String scope, String type) {
        String key = scope + "_" + type;
        MemorySegment segment = segments.get(key);
        if (segment == null) {
            throw new RuntimeException("Segmento de memoria no encontrado: " + key);
        }
        return segment.allocate();
    }

    public int getOrAddConstant(String value, String type) {
        String key = value + ":" + type;
        if (symbolTable.containsKey(key)) {
            return symbolTable.get(key);
        } else {
            int addr = allocate("const", type);
            symbolTable.put(key, addr);
            constantValues.put(addr, parseValue(value, type));
            return addr;
        }
    }


    public void set(String symbol, int addr) {
        symbolTable.put(symbol, addr);
    }

    public Integer get(String symbol) {
        return symbolTable.get(symbol);
    }

}
