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

        if (isFloat(addr)) {
            if (val instanceof Integer) val = ((Integer) val).doubleValue();
            if (val instanceof String) val = Double.parseDouble((String) val);
        }

        if (isGlobal(addr)) globalMemory.put(addr, val);
        else if (isConstant(addr)) throw new RuntimeException("No se puede escribir en memoria constante");
        else if (isLocal(addr)) localMemoryStack.peek().put(addr, val);
        else if (isTemp(addr)) tempMemoryStack.peek().put(addr, val);
        else throw new RuntimeException("Dirección desconocida: " + addr);
    }


    private boolean isGlobal(int addr)    { return addr >= 1000 && addr < 5000; }
    private boolean isLocal(int addr)     { return addr >= 5000 && addr < 9000; }
    private boolean isTemp(int addr)      { return addr >= 9000 && addr < 13000; }
    private boolean isConstant(int addr)  { return addr >= 13000 && addr < 17000; }

    public void execute() {
        // Inicializa la memoria global y las constantes
        globalMemory.putAll(constantMemory);

        // Inicializa el stack para main
        localMemoryStack.push(new HashMap<>());
        tempMemoryStack.push(new HashMap<>());

        while (ip < quadruples.size()) {
            Quadruple q = quadruples.get(ip);

            switch (q.operator) {
                case "+": case "-": case "*": case "/": {
                    Object leftVal = getValue(q.leftOperand);
                    Object rightVal = getValue(q.rightOperand);

                    if (leftVal instanceof String || rightVal instanceof String) {
                        if (q.operator.equals("+") && leftVal instanceof String && rightVal instanceof String) {
                            // Concatenación de strings
                            setValue(q.result, (String) leftVal + (String) rightVal);
                        } else {
                            throw new RuntimeException("Operación inválida con strings: " + q.operator);
                        }
                    } else {
                        Number left = toNumber(leftVal);
                        Number right = toNumber(rightVal);
                        Object res;
                        boolean isFloat = left instanceof Float || left instanceof Double || right instanceof Float || right instanceof Double;
                        res = switch (q.operator) {
                            case "+" -> isFloat ? left.doubleValue() + right.doubleValue() : left.intValue() + right.intValue();
                            case "-" -> isFloat ? left.doubleValue() - right.doubleValue() : left.intValue() - right.intValue();
                            case "*" -> isFloat ? left.doubleValue() * right.doubleValue() : left.intValue() * right.intValue();
                            case "/" -> isFloat ? left.doubleValue() / right.doubleValue() : left.intValue() / right.intValue();
                            default -> throw new RuntimeException("Operador aritmético desconocido: " + q.operator);
                        };
                        setValue(q.result, res);
                    }
                    break;
                }
                case "<=":
                case ">=":
                case "<":
                case ">":
                case "==":
                case "!=": {
                    Object leftVal = getValue(q.leftOperand);
                    Object rightVal = getValue(q.rightOperand);

                    boolean res;
                    // Comparación de strings
                    if (leftVal instanceof String && rightVal instanceof String) {
                        if (q.operator.equals("=="))
                            res = leftVal.equals(rightVal);
                        else if (q.operator.equals("!="))
                            res = !leftVal.equals(rightVal);
                        else
                            throw new RuntimeException("Operador relacional inválido para strings: " + q.operator);
                    }
                    // Comparación de números
                    else if (leftVal instanceof Number && rightVal instanceof Number) {
                        Number left = toNumber(leftVal);
                        Number right = toNumber(rightVal);
                        switch (q.operator) {
                            case "<=": res = left.doubleValue() <= right.doubleValue(); break;
                            case ">=": res = left.doubleValue() >= right.doubleValue(); break;
                            case "<":  res = left.doubleValue() < right.doubleValue(); break;
                            case ">":  res = left.doubleValue() > right.doubleValue(); break;
                            case "==": res = left.doubleValue() == right.doubleValue(); break;
                            case "!=": res = left.doubleValue() != right.doubleValue(); break;
                            default: throw new RuntimeException("Operador relacional no soportado: " + q.operator);
                        }
                    }
                    // Comparación de bool
                    else if (leftVal instanceof Boolean && rightVal instanceof Boolean) {
                        boolean l = (Boolean) leftVal, r = (Boolean) rightVal;
                        if (q.operator.equals("==")) res = l == r;
                        else if (q.operator.equals("!=")) res = l != r;
                        else throw new RuntimeException("Operador relacional inválido para bool: " + q.operator);
                    }
                    // Cualquier otra cosa no soportada
                    else {
                        throw new RuntimeException("No se puede comparar valores de distinto tipo: "
                                + leftVal + " (" + leftVal.getClass().getSimpleName() + "), "
                                + rightVal + " (" + rightVal.getClass().getSimpleName() + ")");
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
                case "PRINT": {
                    Object val = getValue(q.leftOperand);
                    if (val instanceof Integer) {
                        System.out.print((Integer) val);
                    } else if (val instanceof Float) {
                        System.out.print(String.format("%.7f", (Float) val).replaceAll("\\.?0+$", ""));
                    } else if (val instanceof Double) {
                        System.out.print(String.format("%.7f", (Double) val).replaceAll("\\.?0+$", ""));
                    } else if (val instanceof String) {
                        String s = (String) val;
                        // Remover comillas al inicio y al final si existen
                        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
                            s = s.substring(1, s.length() - 1);
                        }
                        System.out.print(s);
                    } else {
                        System.out.print(val);
                    }
                    break;
                }

                case "PARAM":
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
                        Object retVal = getValue(q.leftOperand);
                        setValue(q.result, retVal);
                    }

                    if (!returnIpStack.isEmpty()) {
                        int retAddr = Integer.parseInt(q.result);
                        Object retVal = localMemoryStack.peek().get(retAddr);
                        localMemoryStack.pop();
                        tempMemoryStack.pop();
                        if (!localMemoryStack.isEmpty())
                            localMemoryStack.peek().put(retAddr, retVal);
                        ip = returnIpStack.pop();
                        continue;
                    } else {
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

    private boolean isFloat(int addr) {
        return (addr >= 2000 && addr < 3000)     // global_float
                || (addr >= 6000 && addr < 7000)    // local_float
                || (addr >= 10000 && addr < 11000)  // temp_float
                || (addr >= 14000 && addr < 15000); // const_float
    }

    private boolean toBool(Object obj) {
        if (obj instanceof Boolean) return (Boolean) obj;
        if (obj instanceof Integer) return (Integer) obj != 0;
        if (obj instanceof Double) return (Double) obj != 0.0;
        if (obj instanceof String) return Boolean.parseBoolean((String) obj);
        throw new RuntimeException("No se puede convertir a boolean: " + obj);
    }
}
