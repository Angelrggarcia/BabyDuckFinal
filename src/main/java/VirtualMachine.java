import java.util.*;

public class VirtualMachine {
    private final List<Quadruple> quadruples;
    private final Map<Integer, Object> globalMemory = new HashMap<>();
    private final Map<Integer, Object> constantMemory;
    private final Stack<Map<Integer, Object>> localMemoryStack = new Stack<>();
    private final Stack<Map<Integer, Object>> tempMemoryStack = new Stack<>();
    private final Stack<Integer> returnIpStack = new Stack<>();
    private final Map<Integer, Object> paramStaging = new HashMap<>(); // área de paso de parámetros

    private int ip = 0;

    private final Map<String, Integer> funcDir;

    public VirtualMachine(List<Quadruple> quadruples, Map<Integer, Object> constantValues, Map<String, Integer> funcDir) {
        this.quadruples = quadruples;
        this.constantMemory = constantValues;
        this.funcDir = funcDir;
    }

    private int buscarInicioFuncion(String funcName) {
        Integer idx = funcDir.get(funcName);
        if (idx == null) throw new RuntimeException("No se encontró la función: " + funcName);
        return idx;
    }

    private Object getValue(String sAddr) {
        if (sAddr == null || sAddr.isBlank()) return null;
        int addr = Integer.parseInt(sAddr);
        if (isConstant(addr)) return constantMemory.get(addr);
        if (isGlobal(addr)) return globalMemory.get(addr);
        if (isLocal(addr)) return localMemoryStack.peek().get(addr);
        if (isTemp(addr)) return tempMemoryStack.peek().get(addr);
        throw new RuntimeException("Dirección desconocida: " + addr);
    }
    private void setValue(String sAddr, Object val) {
        int addr = Integer.parseInt(sAddr);
        if (isGlobal(addr)) globalMemory.put(addr, val);
        else if (isConstant(addr)) throw new RuntimeException("No se puede escribir en memoria constante");
        else if (isLocal(addr)) localMemoryStack.peek().put(addr, val);
        else if (isTemp(addr)) tempMemoryStack.peek().put(addr, val);
        else throw new RuntimeException("Dirección desconocida: " + addr);
    }

    // Define los rangos de tus segmentos según tus convenciones
    private boolean isGlobal(int addr)    { return addr >= 1000 && addr < 5000; }
    private boolean isLocal(int addr)     { return addr >= 5000 && addr < 9000; }
    private boolean isTemp(int addr)      { return addr >= 9000 && addr < 13000; }
    private boolean isConstant(int addr)  { return addr >= 13000 && addr < 17000; }

    public void execute() {
        // Inicializa la memoria global y las constantes
        globalMemory.putAll(constantMemory);

        // Inicializa el stack para main (un contexto inicial, aunque main no es función)
        localMemoryStack.push(new HashMap<>());
        tempMemoryStack.push(new HashMap<>());

        while (ip < quadruples.size()) {
            Quadruple q = quadruples.get(ip);

            switch (q.operator) {
                case "+":
                case "-":
                case "*":
                case "/": {
                    Number left = toNumber(getValue(q.leftOperand));
                    Number right = toNumber(getValue(q.rightOperand));
                    Object res = null;
                    switch (q.operator) {
                        case "+": res = (left instanceof Double || right instanceof Double)
                                ? left.doubleValue() + right.doubleValue()
                                : left.intValue() + right.intValue();
                            break;
                        case "-": res = (left instanceof Double || right instanceof Double)
                                ? left.doubleValue() - right.doubleValue()
                                : left.intValue() - right.intValue();
                            break;
                        case "*": res = (left instanceof Double || right instanceof Double)
                                ? left.doubleValue() * right.doubleValue()
                                : left.intValue() * right.intValue();
                            break;
                        case "/": res = (left instanceof Double || right instanceof Double)
                                ? left.doubleValue() / right.doubleValue()
                                : left.intValue() / right.intValue();
                            break;
                    }
                    setValue(q.result, res);
                    break;
                }
                case "<=":
                case ">=":
                case "<":
                case ">":
                case "==":
                case "!=": {
                    Number left = toNumber(getValue(q.leftOperand));
                    Number right = toNumber(getValue(q.rightOperand));
                    boolean res = false;
                    switch (q.operator) {
                        case "<=": res = left.doubleValue() <= right.doubleValue(); break;
                        case ">=": res = left.doubleValue() >= right.doubleValue(); break;
                        case "<":  res = left.doubleValue() < right.doubleValue(); break;
                        case ">":  res = left.doubleValue() > right.doubleValue(); break;
                        case "==": res = left.doubleValue() == right.doubleValue(); break;
                        case "!=": res = left.doubleValue() != right.doubleValue(); break;
                    }
                    setValue(q.result, res);
                    break;
                }
                case "&&":
                case "||": {
                    boolean left = toBool(getValue(q.leftOperand));
                    boolean right = toBool(getValue(q.rightOperand));
                    boolean res = false;
                    switch (q.operator) {
                        case "&&": res = left && right; break;
                        case "||": res = left || right; break;
                    }
                    setValue(q.result, res);
                    break;
                }
                case "!": {
                    boolean val = toBool(getValue(q.leftOperand));
                    setValue(q.result, !val);
                    break;
                }
                case "=":
                    setValue(q.result, getValue(q.leftOperand));
                    break;
                case "PRINT":
                    System.out.println(getValue(q.leftOperand));
                    break;
                case "PARAM":
                    // Guarda en staging area, será copiado a locals al hacer GOSUB
                    paramStaging.put(Integer.parseInt(q.result), getValue(q.leftOperand));
                    break;
                case "GOSUB":
                    // Guarda return IP
                    returnIpStack.push(ip + 1);
                    // Nuevas memorias locales/temporales
                    localMemoryStack.push(new HashMap<>());
                    tempMemoryStack.push(new HashMap<>());
                    // Copia parámetros a memoria local nueva
                    for (Map.Entry<Integer, Object> e : paramStaging.entrySet())
                        localMemoryStack.peek().put(e.getKey(), e.getValue());
                    paramStaging.clear();
                    // Salta al inicio de función (busca en tu directorio de funciones)
                    ip = buscarInicioFuncion(q.leftOperand);
                    continue;
                case "RETURN":
                    // Si hay valor de retorno y result
                    if (q.leftOperand != null && !q.leftOperand.isBlank() && q.result != null && !q.result.isBlank()) {
                        Object retVal = getValue(q.leftOperand); // lee desde local actual
                        setValue(q.result, retVal);              // escribe en local actual
                    }

                    // --- COPIA EL RETORNO ANTES DE HACER POP ---
                    if (!returnIpStack.isEmpty()) {
                        // COPIA el valor de retorno al contexto anterior (debajo en el stack)
                        int retAddr = Integer.parseInt(q.result);
                        Object retVal = localMemoryStack.peek().get(retAddr);
                        // Sale de este contexto, entra al anterior
                        localMemoryStack.pop();
                        tempMemoryStack.pop();
                        // Copia el valor al slot de retorno en el nuevo tope
                        if (!localMemoryStack.isEmpty())
                            localMemoryStack.peek().put(retAddr, retVal);
                        // Regresa
                        ip = returnIpStack.pop();
                        continue;
                    } else {
                        // main terminó
                        return;
                    }
                case "GOTO":
                    ip = Integer.parseInt(q.result);
                    continue;
                case "GOTOF":
                    if (!toBool(getValue(q.leftOperand))) {
                        ip = Integer.parseInt(q.result);
                        continue;
                    }
                    break;
                default:
                    throw new RuntimeException("Operador no soportado: " + q.operator);
            }
            ip++;
        }
    }

    private Number toNumber(Object obj) {
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Double) return (Double) obj;
        if (obj instanceof Float) return ((Float) obj).doubleValue();
        if (obj instanceof Boolean) return ((Boolean) obj) ? 1 : 0;
        if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                try {
                    return Double.parseDouble((String) obj);
                } catch (NumberFormatException ee) {
                    throw new RuntimeException("No se puede convertir a número: " + obj);
                }
            }
        }
        throw new RuntimeException("No se puede convertir a número: " + obj);
    }

    private boolean toBool(Object obj) {
        if (obj instanceof Boolean) return (Boolean) obj;
        if (obj instanceof Integer) return (Integer) obj != 0;
        if (obj instanceof Double) return (Double) obj != 0.0;
        if (obj instanceof String) return Boolean.parseBoolean((String) obj);
        throw new RuntimeException("No se puede convertir a boolean: " + obj);
    }
}
