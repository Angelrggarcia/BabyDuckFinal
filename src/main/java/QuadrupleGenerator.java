import java.util.*;

public class QuadrupleGenerator {
    public List<Quadruple> quadruples = new ArrayList<>();
    private int tempCounter = 0;

    private final VirtualMemoryManager memory;
    private final FunctionDirectory funcDir;
    private final Map<String, Integer> functionDirectory = new HashMap<>();
    private final VariableTable variableTable;


    public void addFunctionStart(String name, int startIndex) {
        functionDirectory.put(name, startIndex);
    }

    public Map<String, Integer> getFunctionDirectory() {
        return functionDirectory;
    }

    public QuadrupleGenerator(FunctionDirectory funcDir, VariableTable variableTable, VirtualMemoryManager memory) {
        this.funcDir = funcDir;
        this.variableTable = variableTable;
        this.memory = memory;
    }


    public String newTemp() {
        return "t" + tempCounter++;
    }

    public int addQuadruple(String op, String left, String right, String res) {
        Quadruple q = new Quadruple(op, left, right, res);
        quadruples.add(q);
        return quadruples.size() - 1;
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

            if (q.operator.equals("GOTO") || q.operator.equals("GOTOF") || q.operator.equals("GOSUB")) {
                System.out.printf(
                        "%02d: (%s, %s, %s, %s)%n",
                        i,
                        q.operator,
                        getAddr(q.leftOperand, memory),
                        getAddr(q.rightOperand, memory),
                        q.result
                );
            } else {
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
    }


    private String getAddr(String symbol, VirtualMemoryManager mem) {
        if (symbol == null || symbol.isBlank()) return "";

        // Constantes literales
        if (symbol.matches("-?[0-9]+")) return String.valueOf(mem.getOrAddConstant(symbol, "int"));
        if (symbol.matches("-?[0-9]+\\.[0-9]+")) return String.valueOf(mem.getOrAddConstant(symbol, "float"));
        if ("true".equals(symbol) || "false".equals(symbol)) return String.valueOf(mem.getOrAddConstant(symbol, "bool"));
        if (symbol.startsWith("\"")) return String.valueOf(mem.getOrAddConstant(symbol, "string"));

        // Busca en la tabla de símbolos
        Integer addr = mem.get(symbol);
        if (addr != null) return String.valueOf(addr);

        // Si es un temporal no registrado
        if (symbol.matches("t[0-9]+")) {

            String type = variableTable.getType(symbol);
            if (type == null) type = "int";
            int tAddr = mem.allocate("temp", type);
            mem.set(symbol, tAddr);
            return String.valueOf(tAddr);
        }

        if (isFunctionName(symbol)) return symbol;

        throw new RuntimeException("No virtual address found for symbol: '" + symbol + "'");
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
            String result;

            if (quad.operator.equals("GOTO") || quad.operator.equals("GOTOF") || quad.operator.equals("GOSUB")) {
                result = quad.result;
            } else {
                result = translate(quad.result);
            }

            translated.add(new Quadruple(quad.operator, left, right, result));
        }

        return translated;
    }

    private String translate(String operand) {
        if (operand == null || operand.isBlank()) return "";

        // Si es literal booleano
        if (operand.equals("true") || operand.equals("false")) {
            return String.valueOf(memory.getOrAddConstant(operand, "bool"));
        }

        // Si es nombre de función
        if (funcDir.getFunction(operand) != null) {
            return operand;
        }
        Integer addr = memory.get(operand);
        if (addr != null) return String.valueOf(addr);

        // Si es un número entero
        if (operand.matches("-?[0-9]+")) {
            return String.valueOf(memory.getOrAddConstant(operand, "int"));
        }
        // Si es un número flotante
        if (operand.matches("-?[0-9]+\\.[0-9]+")) {
            return String.valueOf(memory.getOrAddConstant(operand, "float"));
        }
        // Si es string
        if (operand.startsWith("\"")) {
            return String.valueOf(memory.getOrAddConstant(operand, "string"));
        }
        throw new RuntimeException("Símbolo sin dirección virtual: '" + operand + "'");
    }


}