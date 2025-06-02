import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class VariableTable {
    private Stack<Map<String, String>> scopes = new Stack<>();

    public VariableTable() {
        scopes.push(new HashMap<>()); // global scope
    }

    public void enterScope(String name) {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        scopes.pop();
    }

    public void addVariable(String name, String type, String scopeType) {
        scopes.peek().put(name, type);
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
