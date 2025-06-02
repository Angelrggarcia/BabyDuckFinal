import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class VariableTable {
    private Stack<Map<String, String>> scopes = new Stack<>();
    private VirtualMemoryManager memory; // <-- agrega esto

    public VariableTable(VirtualMemoryManager memory) {
        scopes.push(new HashMap<>());
        this.memory = memory;
    }

    public void enterScope(String name) {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        scopes.pop();
    }

    public void addVariable(String name, String type, String scopeType) {
        scopes.peek().put(name, type);
        String realScope = scopeType == null ? "global" : scopeType;
        // No dupliques memoria si ya existe (por ejemplo para parámetros)
        if (memory.get(name) == null) {
            int addr = memory.allocate(realScope, type);
            memory.set(name, addr);
        }
    }

    public boolean exists(String name) {
        for (int i = scopes.size()-1; i>=0; i--) {
            if (scopes.get(i).containsKey(name)) return true;
        }
        return false;
    }

    public String getType(String name) {
        for (int i = scopes.size()-1; i>=0; i--) {
            if (scopes.get(i).containsKey(name)) return scopes.get(i).get(name);
        }
        return null;
    }
}
