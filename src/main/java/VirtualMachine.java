import java.util.*;

public class VirtualMachine {
/*    private final List<Quadruple> quadruples;
    private int ip = 0; // Instruction pointer
    private final Scanner scanner = new Scanner(System.in);
    private final Map<Integer, Object> globalMemory = new HashMap<>();
    private final Stack<Map<Integer, Object>> localMemoryStack = new Stack<>();
    private final Stack<Integer> returnAddressStack = new Stack<>();
    private Map<Integer, Object> currentMemory = globalMemory;
    private final VirtualMemoryManager memoryManager;
    private final Map<String, Integer> functionDirectory = new HashMap<>();

    public VirtualMachine(List<Quadruple> quads, Map<String, Integer> funcDir, Map<Integer, Object> constantValues, VirtualMemoryManager memoryManager) {
        this.quadruples = quads;
        this.memoryManager = memoryManager;
        this.functionDirectory.putAll(funcDir);
        globalMemory.putAll(constantValues);
    }

    public void run() {
        while (ip < quadruples.size()) {
            Quadruple quad = quadruples.get(ip);
            String op = quad.operator;
            int next = ip + 1;

            switch (op) {
                case "+" -> setValue(quad.result,
                        toNumber(getValue(quad.leftOperand)) + toNumber(getValue(quad.rightOperand)));
                case "-" -> {
                    Object left = getValue(quad.leftOperand);
                    Object right = getValue(quad.rightOperand);
                    double result = toNumber(left) - toNumber(right);
                    System.out.printf("[RESTA] %s (%s) - %s (%s) = %s\n", quad.leftOperand, left, quad.rightOperand, right, result);
                    setValue(quad.result, result);
                }
                case "*" -> setValue(quad.result,
                        toNumber(getValue(quad.leftOperand)) * toNumber(getValue(quad.rightOperand)));
                case "/" -> setValue(quad.result,
                        toNumber(getValue(quad.leftOperand)) / toNumber(getValue(quad.rightOperand)));

                case "=" -> setValue(quad.result, getValue(quad.leftOperand));

                case "==" -> setValue(quad.result,
                        getValue(quad.leftOperand).equals(getValue(quad.rightOperand)) ? 1 : 0);

                case "!=" -> setValue(quad.result,
                        !getValue(quad.leftOperand).equals(getValue(quad.rightOperand)) ? 1 : 0);

                case "<" -> setValue(quad.result,
                        toNumber(getValue(quad.leftOperand)) < toNumber(getValue(quad.rightOperand)) ? 1 : 0);

                case "<=" -> setValue(quad.result,
                        toNumber(getValue(quad.leftOperand)) <= toNumber(getValue(quad.rightOperand)) ? 1 : 0);

                case ">" -> setValue(quad.result,
                        toNumber(getValue(quad.leftOperand)) > toNumber(getValue(quad.rightOperand)) ? 1 : 0);

                case ">=" -> setValue(quad.result,
                        toNumber(getValue(quad.leftOperand)) >= toNumber(getValue(quad.rightOperand)) ? 1 : 0);

                case "&&" -> {
                    boolean a = toBool(getValue(quad.leftOperand));
                    boolean b = toBool(getValue(quad.rightOperand));
                    setValue(quad.result, (a && b) ? 1 : 0);
                }

                case "||" -> {
                    boolean a = toBool(getValue(quad.leftOperand));
                    boolean b = toBool(getValue(quad.rightOperand));
                    setValue(quad.result, (a || b) ? 1 : 0);
                }

                case "!" -> {
                    boolean val = toBool(getValue(quad.leftOperand));
                    setValue(quad.result, !val ? 1 : 0);
                }

                case "PRINT" -> System.out.println(getValue(quad.leftOperand));

                case "READ" -> {
                    System.out.print("> ");
                    String input = scanner.nextLine();
                    setValue(quad.result, tryParse(input));
                }

                case "GOTO" -> next = toInt(quad.result);

                case "GOTOF" -> {
                    boolean cond = toBool(getValue(quad.leftOperand));
                    if (!cond) next = toInt(quad.result);
                }

                case "PARAM" -> {
                    int dest = toInt(quad.result);
                    Object val = getValue(quad.leftOperand);
                    currentMemory.put(dest, val);
                    System.out.printf("[PARAM] Copiando %s a %d\n", val, dest);
                }

                case "ERA" -> {
                    currentMemory = new HashMap<>();
                }

                case "RETURN" -> {
                    int retAddr = toInt(quad.result);
                    Object value = getValue(quad.leftOperand);

                    globalMemory.put(retAddr, value); // Siempre guardar en global
                    System.out.printf("[RETURN] guardando %s en %d\n", value, retAddr);
                }

                case "ENDFUNC" -> {
                    if (!localMemoryStack.isEmpty()) {
                        currentMemory = localMemoryStack.pop();
                    } else {
                        currentMemory = globalMemory;
                    }

                    if (!returnAddressStack.isEmpty()) {
                        ip = returnAddressStack.pop();
                    } else {
                        break;
                    }
                    continue;
                }

                case "GOSUB" -> {
                    localMemoryStack.push(currentMemory);
                    returnAddressStack.push(ip + 1);
                    ip = functionDirectory.get(quad.leftOperand);
                    continue;
                }

                default -> throw new RuntimeException("Operación no soportada: " + op);
            }

            ip = next;
        }
    }

    private Object getValue(String operand) {
        int addr = toInt(operand);

        if (currentMemory.containsKey(addr)) return currentMemory.get(addr);
        if (!localMemoryStack.isEmpty() && localMemoryStack.peek().containsKey(addr)) {
            return localMemoryStack.peek().get(addr);
        }
        if (globalMemory.containsKey(addr)) return globalMemory.get(addr);

        throw new RuntimeException("Dirección no inicializada: " + addr);
    }

    private void setValue(String addrStr, Object value) {
        int addr = toInt(addrStr);
        currentMemory.put(addr, value);
    }

    private int toInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            Integer addr = memoryManager.get(s);
            if (addr == null) {
                throw new RuntimeException("No se encontró dirección para símbolo: " + s);
            }
            return addr;
        }
    }

    private double toNumber(Object val) {
        if (val instanceof Integer) return ((Integer) val).doubleValue();
        if (val instanceof Float) return ((Float) val).doubleValue();
        if (val instanceof Double) return (Double) val;
        throw new RuntimeException("Valor no numérico: " + val);
    }

    private boolean toBool(Object val) {
        if (val instanceof Boolean) return (Boolean) val;
        if (val instanceof Integer) return ((Integer) val) != 0;
        throw new RuntimeException("No se puede convertir a booleano: " + val);
    }

    private Object tryParse(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e1) {
            try {
                return Float.parseFloat(input);
            } catch (NumberFormatException e2) {
                return input;
            }
        }
    } */
}
