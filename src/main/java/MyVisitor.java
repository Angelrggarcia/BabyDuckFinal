import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;

import java.util.*;

public class MyVisitor extends miniCppBaseVisitor<Object> {
    // Ámbito de variables
    private final Stack<Map<String, Object>> scopes = new Stack<>();
    private final Map<String, FunctionDefinition> functions = new HashMap<>();

    private static class ReturnValue extends RuntimeException {
        final Object value;

        public ReturnValue(Object value) {
            this.value = value;
        }
    }

    private boolean isTrue(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value) != 0;
        }
        return value != null;
    }

    // Definición de función
    private static class FunctionDefinition {
        miniCppParser.FunctionDeclContext context;
        Map<String, Object> defaultValues = new HashMap<>();

        public FunctionDefinition(miniCppParser.FunctionDeclContext ctx) {
            this.context = ctx;
            if (ctx != null && ctx.paramList() != null) {
                for (miniCppParser.ParamContext param : ctx.paramList().param()) {
                    if (param.expr() != null) {
                        defaultValues.put(param.ID().getText(), new MyVisitor().visit(param.expr()));
                    }
                }
            }
        }

        public Object invoke(List<Object> args, MyVisitor visitor) {
            if (context == null) {
                throw new UnsupportedOperationException("Built-in function must override invoke method.");
            }

            visitor.pushScope();
            try {
                if (context.paramList() != null) {
                    List<miniCppParser.ParamContext> params = context.paramList().param();
                    for (int i = 0; i < params.size(); i++) {
                        String paramName = params.get(i).ID().getText();
                        Object value = i < args.size() ? args.get(i) :
                                defaultValues.getOrDefault(paramName, null);
                        if (value == null) {
                            throw new RuntimeException("Parámetro faltante: " + paramName);
                        }
                        visitor.declareVar(paramName, value);
                    }
                }

                try {
                    return visitor.visit(context.body());
                } catch (ReturnValue ret) {
                    return ret.value;
                }
            } finally {
                visitor.popScope();
            }
        }
    }

    public MyVisitor() {
        scopes.push(new HashMap<>()); // Ámbito global
        initBuiltins();
    }

    private void initBuiltins() {
        // Funciones built-in
        functions.put("print", new FunctionDefinition(null) {
            @Override
            public Object invoke(List<Object> args, MyVisitor visitor) {
                for (Object arg : args) {
                    if (arg instanceof Double) {
                        // Redondear a 3 decimales
                        System.out.print(String.format("%.3f", (Double) arg));
                    } else {
                        System.out.print(arg != null ? arg.toString() : "null");
                    }
                }
                return null;
            }
        });


        functions.put("println", new FunctionDefinition(null) {
            @Override
            public Object invoke(List<Object> args, MyVisitor visitor) {
                functions.get("print").invoke(args, visitor);
                System.out.println(); // Solo añade salto de línea
                return null;
            }
        });

        functions.put("input", new FunctionDefinition(null) {
            @Override
            public Object invoke(List<Object> args, MyVisitor visitor) {
                if (!args.isEmpty()) {
                    System.out.print(args.get(0));
                }
                return new Scanner(System.in).nextLine();
            }
        });
    }

    @Override
    public Object visitPrintStatement(miniCppParser.PrintStatementContext ctx) {
        List<Object> args = new ArrayList<>();
        if (ctx.callArgs() != null) {
            Object result = visit(ctx.callArgs());
            if (result instanceof List<?>) {
                args = (List<Object>) result;
            } else {
                args.add(result); // En caso de que haya un solo argumento no envuelto en lista
            }
        }
        return functions.get("print").invoke(args, this);
    }

    // Helpers
    private void pushScope() {
        scopes.push(new HashMap<>());
    }

    private void popScope() {
        scopes.pop();
    }

    private void declareVar(String name, Object value) {
        scopes.peek().put(name, value);
    }



    private void assignVar(String name, Object value) {
        // Buscar la variable en los ámbitos desde el más interno al más externo
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                scopes.get(i).put(name, value);
                return;
            }
        }
        // Si no existe, lanzar excepción (mejor que crearla automáticamente)
        throw new RuntimeException("Variable no declarada: " + name);
    }

    private Object lookupVar(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return scopes.get(i).get(name);
            }
        }
        throw new RuntimeException("Variable no definida: " + name);
    }

    // Visitor methods
    @Override
    public Object visitProgram(miniCppParser.ProgramContext ctx) {
        ctx.children.forEach(this::visit);
        return null;
    }

    @Override
    public Object visitFunctionDecl(miniCppParser.FunctionDeclContext ctx) {
        functions.put(ctx.ID().getText(), new FunctionDefinition(ctx));
        return null;
    }

    @Override
    public Object visitVarDecl(miniCppParser.VarDeclContext ctx) {
        String varName = ctx.ID().getText();
        Object value;

        if (ctx.expr() != null) {
            value = visit(ctx.expr());
        } else {
            // Asigna valores por defecto según el tipo
            String type = ctx.type().getText();
            value = switch (type) {
                case "int" -> 0;
                case "float" -> 0.0f;
                case "bool" -> false;
                case "string" -> "";
                default -> throw new RuntimeException("Tipo no soportado: " + type);
            };
        }

        declareVar(varName, value);
        return null;
    }

    @Override
    public Object visitAssignment(miniCppParser.AssignmentContext ctx) {
        // Obtener el lado izquierdo de la asignación
        miniCppParser.LhsContext lhs = ctx.lhs();
        Object value = visit(ctx.expr()); // Valor a asignar

        if (lhs.ID() != null && lhs.expr() == null) {
            // Caso 1: Asignación simple a variable (var = valor)
            String varName = lhs.ID().getText();

            try {
                assignVar(varName, value);
            } catch (RuntimeException e) {
                declareVar(varName, value);
            }
        }
        else if (lhs.ID() != null && lhs.expr() != null) {
            // Caso 2: Asignación a elemento de array (array[índice] = valor)
            String arrayName = lhs.ID().getText();
            Object indexObj = visit(lhs.expr());

            // Verificar tipos
            if (!(indexObj instanceof Number)) {
                throw new RuntimeException("Índice de array debe ser numérico");
            }
            int index = ((Number)indexObj).intValue();

            Object array = lookupVar(arrayName);
            if (array == null) {
                throw new RuntimeException("Array '" + arrayName + "' no definido");
            }

            if (array instanceof List) {
                List<Object> list = (List<Object>)array;
                // Si el índice está fuera de rango, expandir la lista con nulls
                while (list.size() <= index) {
                    list.add(null);
                }
                list.set(index, value);
            }
            else if (array instanceof Object[]) {
                Object[] arr = (Object[])array;
                if (index >= 0 && index < arr.length) {
                    arr[index] = value;
                } else {
                    throw new RuntimeException("Índice " + index + " fuera de rango para array " + arrayName);
                }
            }
            else {
                throw new RuntimeException("Variable '" + arrayName + "' no es un array");
            }
        }

        return null;
    }

    @Override
    public Object visitCallArgs(miniCppParser.CallArgsContext ctx) {
        List<Object> args = new ArrayList<>();
        for (miniCppParser.ExprContext expr : ctx.expr()) {
            args.add(visit(expr));
        }
        return args;
    }

    @Override
    public Object visitFunctionCall(miniCppParser.FunctionCallContext ctx) {
        String funcName = ctx.ID().getText();
        List<Object> args = Collections.emptyList();

        if (ctx.callArgs() != null) {
            args = (List<Object>) visit(ctx.callArgs());
        }

        if (functions.containsKey(funcName)) {
            return functions.get(funcName).invoke(args, this);
        }

        throw new RuntimeException("Función no definida: " + funcName);
    }

    @Override
    public Object visitReturnStatement(miniCppParser.ReturnStatementContext ctx) {
        Object value = ctx.expr() != null ? visit(ctx.expr()) : null;
        throw new ReturnValue(value);
    }

    @Override
    public Object visitIfStatement(miniCppParser.IfStatementContext ctx) {
        if (isTrue(visit(ctx.expr()))) {
            return visit(ctx.statement(0));
        } else if (ctx.statement().size() > 1) {
            return visit(ctx.statement(1));
        }
        return null;
    }

    @Override
    public Object visitWhileStatement(miniCppParser.WhileStatementContext ctx) {
        while (isTrue(visit(ctx.expr()))) {
            Object result = visit(ctx.statement());
            if (result instanceof ReturnValue) {
                return result;
            }
        }
        return null;
    }

    @Override
    public Object visitForStatement(miniCppParser.ForStatementContext ctx) {
        pushScope();
        try {
            // Init
            if (ctx.init != null && !ctx.init.getText().equals(";")) {
                visit(ctx.init);
            }

            // Loop
            while (ctx.cond == null || isTrue(visit(ctx.cond))) {
                Object result = visit(ctx.stmt);
                if (result instanceof ReturnValue) {
                    return result;
                }

                // Update
                if (ctx.update != null) {
                    visit(ctx.update);
                }
            }
        } finally {
            popScope();
        }
        return null;
    }


    // Expressions
    @Override
    public Object visitAdditiveExpr(miniCppParser.AdditiveExprContext ctx) {
        Object left = visit(ctx.expr(0));
        Object right = visit(ctx.expr(1));
        String op = ctx.op.getText();

        // Caso 1: Ambos operandos son enteros
        if (left instanceof Integer && right instanceof Integer) {
            int l = (Integer) left;
            int r = (Integer) right;
            switch (op) {
                case "+": return l + r;
                case "-": return l - r;
                default: throw unsupportedOperator(op, ctx);
            }
        }

        // Caso 2: Al menos un operando es numérico (double/float)
        if (left instanceof Number && right instanceof Number) {
            double l = ((Number) left).doubleValue();
            double r = ((Number) right).doubleValue();
            switch (op) {
                case "+": return l + r;
                case "-": return l - r;
                default: throw unsupportedOperator(op, ctx);
            }
        }

        // Caso 3: Concatenación de strings (solo para +)
        if (op.equals("+")) {
            return String.valueOf(left) + String.valueOf(right);
        }

        // Caso 4: Operación no soportada
        throw new RuntimeException(String.format(
                "Operación no soportada: %s %s %s (tipos: %s y %s)",
                left, op, right,
                left != null ? left.getClass().getSimpleName() : "null",
                right != null ? right.getClass().getSimpleName() : "null"
        ));
    }

    private RuntimeException unsupportedOperator(String op, ParserRuleContext ctx) {
        return new RuntimeException(String.format(
                "Operador '%s' no soportado en línea %d:%d",
                op,
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine()
        ));
    }

    @Override
    public Object visitRelationalExpr(miniCppParser.RelationalExprContext ctx) {
        Object left = visit(ctx.expr(0));
        Object right = visit(ctx.expr(1));
        String op = ctx.op.getText();

        if (left instanceof Number && right instanceof Number) {
            double l = ((Number) left).doubleValue();
            double r = ((Number) right).doubleValue();

            switch (op) {
                case "==": return Math.abs(l - r) < 0.000001;
                case "!=": return Math.abs(l - r) >= 0.000001;
                case "<": return l < r;
                case ">": return l > r;
                case "<=": return l <= r;
                case ">=": return l >= r;
            }
        }

        if (left instanceof String && right instanceof String) {
            int cmp = ((String) left).compareTo((String) right);
            switch (op) {
                case "==": return cmp == 0;
                case "!=": return cmp != 0;
                case "<": return cmp < 0;
                case ">": return cmp > 0;
                case "<=": return cmp <= 0;
                case ">=": return cmp >= 0;
            }
        }

        if (op.equals("==")) return left == right;
        if (op.equals("!=")) return left != right;

        throw new RuntimeException("Invalid comparison: " + left + " " + op + " " + right);
    }

    @Override
    public Object visitLogicalAndExpr(miniCppParser.LogicalAndExprContext ctx) {
        return isTrue(visit(ctx.expr(0))) && isTrue(visit(ctx.expr(1)));
    }

    @Override
    public Object visitLogicalOrExpr(miniCppParser.LogicalOrExprContext ctx) {
        return isTrue(visit(ctx.expr(0))) || isTrue(visit(ctx.expr(1)));
    }

    @Override
    public Object visitLogicalNotExpr(miniCppParser.LogicalNotExprContext ctx) {
        return !isTrue(visit(ctx.expr()));
    }

    @Override
    public Object visitInExpr(miniCppParser.InExprContext ctx) {
        Object left = visit(ctx.expr(0));
        Object right = visit(ctx.expr(1));

        if (right instanceof List) {
            return ((List<?>) right).contains(left);
        }
        if (right instanceof String && left instanceof String) {
            return ((String) right).contains((String) left);
        }

        throw new RuntimeException("'in' operation not supported for these types");
    }

    @Override
    public Object visitIndexExpr(miniCppParser.IndexExprContext ctx) {
        Object array = visit(ctx.expr(0));
        Object index = visit(ctx.expr(1));

        if (array instanceof List && index instanceof Number) {
            return ((List<?>) array).get(((Number) index).intValue());
        }
        if (array instanceof String && index instanceof Number) {
            return String.valueOf(((String) array).charAt(((Number) index).intValue()));
        }

        throw new RuntimeException("Invalid array/index types");
    }

    @Override
    public Object visitListLiteral(miniCppParser.ListLiteralContext ctx) {
        if (ctx.callArgs() == null) {
            return new ArrayList<>();
        }
        Object args = visit(ctx.callArgs());
        return args instanceof List ? new ArrayList<>((List<?>) args) : new ArrayList<>(Collections.singletonList(args));
    }

    @Override
    public Object visitDictLiteral(miniCppParser.DictLiteralContext ctx) {
        Map<Object, Object> dict = new HashMap<>();
        for (miniCppParser.KeyValueContext kv : ctx.keyValue()) {
            Object key = visit(kv.expr(0));
            Object value = visit(kv.expr(1));
            dict.put(key, value);
        }
        return dict;
    }

    @Override
    public Object visitLambdaExpr(miniCppParser.LambdaExprContext ctx) {
        return new FunctionDefinition(null) {
            @Override
            public Object invoke(List<Object> args, MyVisitor visitor) {
                visitor.pushScope();
                try {
                    if (ctx.paramList() != null) {
                        List<miniCppParser.ParamContext> params = ctx.paramList().param();
                        if (args.size() != params.size()) {
                            throw new RuntimeException("Argument count mismatch");
                        }
                        for (int i = 0; i < params.size(); i++) {
                            visitor.declareVar(params.get(i).ID().getText(), args.get(i));
                        }
                    }
                    return visitor.visit(ctx.expr());
                } finally {
                    visitor.popScope();
                }
            }
        };
    }

    @Override
    public Object visitIntLiteral(miniCppParser.IntLiteralContext ctx) {
        return Integer.parseInt(ctx.INT().getText());
    }

    @Override
    public Object visitFloatLiteral(miniCppParser.FloatLiteralContext ctx) {
        return Double.parseDouble(ctx.FLOAT().getText());
    }

    @Override
    public Object visitBoolLiteral(miniCppParser.BoolLiteralContext ctx) {
        return Boolean.parseBoolean(ctx.BOOL().getText());
    }

    @Override
    public Object visitStringLiteral(miniCppParser.StringLiteralContext ctx) {
        String text = ctx.STRING().getText();
        return text.substring(1, text.length() - 1)
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\"", "\"");
    }

    @Override
    public Object visitVariableExpr(miniCppParser.VariableExprContext ctx) {
        return lookupVar(ctx.ID().getText());
    }

    @Override
    public Object visitParenExpr(miniCppParser.ParenExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Object visitFunctionCallStmt(miniCppParser.FunctionCallStmtContext ctx) {
        return visit(ctx.functionCall());
    }


    @Override
    public Object visitMultiplicativeExpr(miniCppParser.MultiplicativeExprContext ctx) {
        Object left = visit(ctx.expr(0));
        Object right = visit(ctx.expr(1));
        String op = ctx.op.getText();

        if (left instanceof Number && right instanceof Number) {
            if (left instanceof Double || right instanceof Double) {
                double l = ((Number) left).doubleValue();
                double r = ((Number) right).doubleValue();
                return op.equals("*") ? l * r : l / r;
            } else {
                int l = ((Number) left).intValue();
                int r = ((Number) right).intValue();
                return op.equals("*") ? l * r : l / r;
            }
        }

        throw new RuntimeException("Operación no válida: " + left.getClass() + " " + op + " " + right.getClass());
    }

    @Override
    public Object visitPrimaryCallExpr(miniCppParser.PrimaryCallExprContext ctx) {
        return visit(ctx.primaryExpr());
    }

    @Override
    public Object visitFunctionCallExpr(miniCppParser.FunctionCallExprContext ctx) {
        return visit(ctx.functionCall());
    }

    @Override
    public Object visitLambdaCallExpr(miniCppParser.LambdaCallExprContext ctx) {
        return visit(ctx.lambdaExpr());
    }

    @Override
    public Object visitInputCallExpr(miniCppParser.InputCallExprContext ctx) {
        return visit(ctx.inputExpr());
    }

    @Override
    public Object visitLiteralExpr(miniCppParser.LiteralExprContext ctx) {
        return visit(ctx.literal());
    }

    @Override
    public Object visitListCallLiteral(miniCppParser.ListCallLiteralContext ctx) {
        return visit(ctx.listLiteral());
    }

    @Override
    public Object visitDictCallLiteral(miniCppParser.DictCallLiteralContext ctx) {
        return visit(ctx.dictLiteral());
    }

    // Type handling
    @Override
    public Object visitType(miniCppParser.TypeContext ctx) {
        // In a full implementation, we might want to track type information
        return ctx.getText();
    }

    // Additional helper methods
    private Number performNumericOperation(Number left, Number right, String op) {
        if (left instanceof Double || right instanceof Double) {
            double l = left.doubleValue();
            double r = right.doubleValue();
            return switch (op) {
                case "+" -> l + r;
                case "-" -> l - r;
                case "*" -> l * r;
                case "/" -> l / r;
                default -> throw new RuntimeException("Operador no soportado: " + op);
            };
        } else {
            int l = left.intValue();
            int r = right.intValue();
            return switch (op) {
                case "+" -> l + r;
                case "-" -> l - r;
                case "*" -> l * r;
                case "/" -> l / r;
                default -> throw new RuntimeException("Operador no soportado: " + op);
            };
        }
    }


    // Error handling
    @Override
    public Object visitErrorNode(ErrorNode node) {
        throw new RuntimeException("Error de sintaxis: " + node.getText());
    }

    @Override
    public Object visitInputExpr(miniCppParser.InputExprContext ctx) {
        // Input no debería tener argumentos según tu gramática
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        // Simple conversión de tipos (podrías mejorarlo)
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e2) {
                return input; // Devuelve como string si no es número
            }
        }
    }

}