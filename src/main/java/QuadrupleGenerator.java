import java.util.*;

public class QuadrupleGenerator {
    public Stack<String> operands = new Stack<>();
    public Stack<String> types = new Stack<>();
    public Stack<String> operators = new Stack<>();
    public List<Quadruple> quadruples = new ArrayList<>();
    private int tempCounter = 0;

    private final VirtualMemoryManager memory = new VirtualMemoryManager();
    private final FunctionDirectory funcDir;
    private final Map<String, Integer> functionDirectory = new HashMap<>();

    public void addFunctionStart(String name, int startIndex) {
        functionDirectory.put(name, startIndex);
    }

    public Map<String, Integer> getFunctionDirectory() {
        return functionDirectory;
    }

    public Map<Integer, Object> getConstantValues() {
        Map<Integer, Object> constants = new HashMap<>();

        for (Map.Entry<String, Integer> entry : memory.getSymbolTable().entrySet()) {
            String key = entry.getKey();
            Integer addr = entry.getValue();

            // key tiene formato "valor:tipo"
            if (key.contains(":")) {
                String[] parts = key.split(":");
                String value = parts[0];
                String type = parts[1];

                Object parsed;
                switch (type) {
                    case "int" -> parsed = Integer.parseInt(value);
                    case "float" -> parsed = Float.parseFloat(value);
                    case "bool" -> parsed = value.equals("true") || value.equals("1");
                    case "string" -> parsed = value;
                    default -> throw new RuntimeException("Tipo de constante no reconocido: " + type);
                }

                constants.put(addr, parsed);
            }
        }

        return constants;
    }

    public QuadrupleGenerator(FunctionDirectory funcDir) {
        this.funcDir = funcDir;
    }


    public String newTemp() {
        return "t" + tempCounter++;
    }

    public int addQuadruple(String op, String left, String right, String res) {
        Quadruple q = new Quadruple(op, left, right, res);
        quadruples.add(q);
        return quadruples.size() - 1; // Retorna el índice donde lo agregó
    }

    public void printQuadruples() {
        System.out.println("Cuádruplos generados:");
        for (int i = 0; i < quadruples.size(); i++) {
            Quadruple q = quadruples.get(i);
            System.out.printf("%02d: (%s, %s, %s, %s)\n", i, q.operator, q.leftOperand, q.rightOperand, q.result);
        }
    }

    public void printQuadruplesWithAddresses() {
        System.out.println("---- Cuádruplos con Direcciones Virtuales ----");
        for (int i = 0; i < quadruples.size(); i++) {
            Quadruple q = quadruples.get(i);
            System.out.printf(
                    "%02d: (%s, %s, %s, %s)%n",
                    i,
                    q.operator,
                    getAddr(q.leftOperand, memory),
                    getAddr(q.rightOperand, memory),
                    getAddr(q.result, memory)
            );
        }
    }

    private String getAddr(String symbol, VirtualMemoryManager mem) {
        if (symbol == null || symbol.isBlank()) return "";

        try {
            Integer addr = mem.get(symbol);
            if (addr != null) return String.valueOf(addr);

            // Constantes
            if (symbol.matches("[0-9]+")) return String.valueOf(mem.getOrAddConstant(symbol, "int"));
            if (symbol.matches("[0-9]+\\.[0-9]+")) return String.valueOf(mem.getOrAddConstant(symbol, "float"));
            if (symbol.startsWith("\"")) return String.valueOf(mem.getOrAddConstant(symbol, "string"));

            // Temporales
            if (symbol.matches("t[0-9]+")) {
                int tempAddr = mem.allocate("temp", "int"); // Default a int
                mem.set(symbol, tempAddr);
                return String.valueOf(tempAddr);
            }

            if (isFunctionName(symbol)) {
                return symbol;  // se deja como está
            }

            throw new RuntimeException("Símbolo sin dirección virtual: '" + symbol + "'");
        } catch (Exception e) {
            return symbol;  // fallback final
        }
    }

    private boolean isFunctionName(String symbol) {
        return funcDir.getFunction(symbol) != null;
    }


    public VirtualMemoryManager getMemoryManager() {
        return memory;
    }

    public List<Quadruple> getQuadruples() {
        return quadruples;
    }

    public List<Quadruple> getQuadruplesWithVirtualAddresses() {
        List<Quadruple> translated = new ArrayList<>();

        for (Quadruple quad : quadruples) {
            String left = translate(quad.leftOperand);
            String right = translate(quad.rightOperand);
            String result = translate(quad.result);

            translated.add(new Quadruple(quad.operator, left, right, result));
        }

        return translated;
    }

    private String translate(String operand) {
        if (operand == null || operand.isBlank()) return "";

        if (operand.matches("[0-9]+")) return operand;

        // Si es nombre de una función, se deja sin traducir
        if (funcDir.getFunction(operand) != null) {
            return operand;
        }

        Integer addr = memory.get(operand);
        if (addr != null) return String.valueOf(addr);

        if (operand.matches("-?[0-9]+")) {
            return String.valueOf(memory.getOrAddConstant(operand, "int"));
        }

        if (operand.matches("-?[0-9]+\\.[0-9]+")) {
            return String.valueOf(memory.getOrAddConstant(operand, "float"));
        }

        if (operand.startsWith("\"")) {
            return String.valueOf(memory.getOrAddConstant(operand, "string"));
        }

        throw new RuntimeException("Símbolo sin dirección virtual: '" + operand + "'");
    }



}