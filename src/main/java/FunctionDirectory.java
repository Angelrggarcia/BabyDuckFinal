import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class FunctionDirectory {

    private Map<String, FunctionInfo> functions = new HashMap<>();

    public void addFunction(String name, String returnType, List<String> paramTypes, List<String> paramNames) {
        if (functions.containsKey(name)) {
            throw new RuntimeException("Función '" + name + "' ya declarada.");
        }
        functions.put(name, new FunctionInfo(name, returnType, paramTypes, paramNames));
    }

    public FunctionInfo getFunction(String name) {
        return functions.get(name);
    }

    public boolean exists(String name) {
        return functions.containsKey(name);
    }

    // Clase interna para almacenar la información de las funciones
    public static class FunctionInfo {
        public String name;
        public String returnType;
        public List<String> paramTypes;
        public List<String> paramNames;

        public FunctionInfo(String name, String returnType, List<String> paramTypes, List<String> paramNames) {
            this.name = name;
            this.returnType = returnType;
            this.paramTypes = paramTypes;
            this.paramNames = paramNames;
        }
    }
}

