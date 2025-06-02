import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public class SemanticAnalyzer extends miniCppBaseVisitor<Object> {

    // Declare instance variables for all components
    private VariableTable variableTable;
    private final FunctionDirectory functionDirectory;
    private SemanticCube semanticCube;
    private VirtualMemoryManager virtualMemoryManager;
    private Stack<String> functionReturnTypeStack;
    private final QuadrupleGenerator quadGen;

    public SemanticAnalyzer(FunctionDirectory funcDir) {
        this.functionDirectory = funcDir;
        this.quadGen = new QuadrupleGenerator(funcDir);
        variableTable = new VariableTable();
        semanticCube = new SemanticCube();
        virtualMemoryManager = new VirtualMemoryManager();
        functionReturnTypeStack = new Stack<>();
    }

    // -----------------------------------------Auxiliares-------------------------------------------------
    public String currentFunctionReturnType() {
        if (functionReturnTypeStack.isEmpty()) {
            throw new RuntimeException("No function is currently being analyzed.");
        }
        return functionReturnTypeStack.peek();  // Get the return type of the current function
    }

    public static class ExprResult {
        public String value; // nombre: puede ser literal, variable, temporal
        public String type;  // tipo: int, float, bool, etc.

        public ExprResult(String value, String type) {
            this.value = value;
            this.type = type;
        }
    }

    public QuadrupleGenerator getQuadrupleGenerator() {
        return this.quadGen;
    }

    // -----------------------------------------Program-------------------------------------------------
    @Override
    public Object visitProgram(miniCppParser.ProgramContext ctx) {
        // 1. Procesa primero todas las declaraciones de variables globales
        for (miniCppParser.VarDeclContext varDecl : ctx.varDecl()) {
            visit(varDecl);
        }

        // 2. Luego, genera cuádruplo de GOTO a main (reservar espacio, se rellenará después)
        int gotoMainQuad = quadGen.addQuadruple("GOTO", "", "", "");

        // 3. Analiza todas las funciones (para llenar el FunctionDirectory y saber la dirección de main)
        for (miniCppParser.FunctionDeclContext functionDecl : ctx.functionDecl()) {
            visit(functionDecl);
        }

        // 4. Obtiene el índice real de main en los cuádruplos
        Integer mainIndex = quadGen.getFunctionDirectory().get("main");
        if (mainIndex == null) {
            throw new RuntimeException("No se encontró función 'main'.");
        }

        // 5. Rellena el cuádruplo GOTO para apuntar al inicio de main
        quadGen.getQuadruples().get(gotoMainQuad).setResult(mainIndex.toString());

        // 6. Procesa statements sueltos, si tu lenguaje lo permite
        for (miniCppParser.StatementContext statement : ctx.statement()) {
            visit(statement);
        }

        return null;
    }

    // ----------------------------------Funciones----------------------------------------------
    @Override
    public Object visitFunctionCall(miniCppParser.FunctionCallContext ctx) {
        String functionName = ctx.ID().getText();
        if (!functionDirectory.exists(functionName)) {
            throw new RuntimeException("Function '" + functionName + "' is not defined.");
        }

        // Get function info
        FunctionDirectory.FunctionInfo functionInfo = functionDirectory.getFunction(functionName);

        // Evalúa y chequea argumentos
        List<ExprResult> args = new ArrayList<>();
        if (ctx.callArgs() != null) {
            for (miniCppParser.ExprContext exprContext : ctx.callArgs().expr()) {
                args.add((ExprResult) visit(exprContext));
            }
        }

        if (args.size() != functionInfo.paramTypes.size()) {
            throw new RuntimeException("Argument count mismatch for function '" + functionName + "'. Expected " +
                    functionInfo.paramTypes.size() + " but got " + args.size());
        }

        // Chequeo de tipos y generación de PARAM
        for (int i = 0; i < args.size(); i++) {
            String paramType = functionInfo.paramTypes.get(i);
            if (!args.get(i).type.equals(paramType)) {
                throw new RuntimeException("Argument type mismatch at position " + (i + 1) + " for function '" +
                        functionName + "'. Expected '" + paramType + "', found '" + args.get(i).type + "'.");
            }
            quadGen.addQuadruple("PARAM", args.get(i).value, "", "param" + i); // o usa el nombre real del param
        }

        // GOSUB cuádruplo
        quadGen.addQuadruple("GOSUB", functionName, "", "");

        // Si retorna algo, crea temporal y genera cuádruplo ASSIGN
        if (!functionInfo.returnType.equals("void")) {
            String temp = quadGen.newTemp();
            variableTable.addVariable(temp, functionInfo.returnType, "temp");
            quadGen.addQuadruple("=", functionName + "_ret", "", temp);
            return new ExprResult(temp, functionInfo.returnType);
        }
        return null;
    }

    @Override
    public Object visitReturnStatement(miniCppParser.ReturnStatementContext ctx) {
        String returnType = currentFunctionReturnType();
        ExprResult exprRes = (ExprResult) visit(ctx.expr());

        if (!returnType.equals(exprRes.type)) {
            throw new RuntimeException("Return type mismatch: Expected '" + returnType + "', found '" + exprRes.type + "'.");
        }

        quadGen.addQuadruple("RETURN", exprRes.value, "", "");
        return null;
    }

    @Override
    public Object visitFunctionCallStmt(miniCppParser.FunctionCallStmtContext ctx) {
        String functionName = ctx.functionCall().ID().getText();  // Get the function name
        if (!functionDirectory.exists(functionName)) {
            throw new RuntimeException("Function '" + functionName + "' is not defined.");
        }

        // Get the function info (parameter types) from the function directory
        FunctionDirectory.FunctionInfo functionInfo = functionDirectory.getFunction(functionName);

        // Process arguments (if any)
        miniCppParser.CallArgsContext argsContext = ctx.functionCall().callArgs();
        List<String> argTypes = new ArrayList<>();
        for (miniCppParser.ExprContext exprContext : argsContext.expr()) {
            argTypes.add((String) visit(exprContext));  // Get the type of each argument
        }

        // Check if the number of arguments matches
        if (argTypes.size() != functionInfo.paramTypes.size()) {
            throw new RuntimeException("Argument count mismatch in function '" + functionName + "'. Expected " +
                    functionInfo.paramTypes.size() + " but got " + argTypes.size());
        }

        // Check if the argument types match the expected parameter types
        for (int i = 0; i < argTypes.size(); i++) {
            if (!argTypes.get(i).equals(functionInfo.paramTypes.get(i))) {
                throw new RuntimeException("Argument type mismatch at position " + (i + 1) + " for function '" +
                        functionName + "'. Expected '" + functionInfo.paramTypes.get(i) + "', found '" + argTypes.get(i) + "'.");
            }
        }

        return null;
    }

    @Override
    public Object visitFunctionDecl(miniCppParser.FunctionDeclContext ctx) {
        String returnType = ctx.type().getText();
        String functionName = ctx.ID().getText();

        // Prepare parameter types and names
        List<String> paramTypes = new ArrayList<>();
        List<String> paramNames = new ArrayList<>();

        if (ctx.paramList() != null) {
            for (miniCppParser.ParamContext param : ctx.paramList().param()) {
                String paramType = param.type().getText();
                String paramName = param.ID().getText();

                if (paramNames.contains(paramName)) {
                    throw new RuntimeException("Duplicate parameter '" + paramName + "' in function '" + functionName + "'");
                }

                paramTypes.add(paramType);
                paramNames.add(paramName);
            }
        }

        // Añade la función al FunctionDirectory (tipo, params, etc)
        if (functionDirectory.exists(functionName)) {
            throw new RuntimeException("Function '" + functionName + "' already declared.");
        }
        functionDirectory.addFunction(functionName, returnType, paramTypes, paramNames);

        // --- LÍNEA CLAVE: Guarda el índice actual de cuádruplo donde inicia la función ---
        quadGen.addFunctionStart(functionName, quadGen.getQuadruples().size());

        // New scope for variables
        variableTable.enterScope(functionName);

        // Agrega los parámetros como variables locales
        for (int i = 0; i < paramNames.size(); i++) {
            variableTable.addVariable(paramNames.get(i), paramTypes.get(i), "local");
        }

        // Analiza el cuerpo de la función
        functionReturnTypeStack.push(returnType);
        visit(ctx.body());
        functionReturnTypeStack.pop();

        variableTable.exitScope();

        return null;
    }

    private Object addParam(miniCppParser.ParamContext ctx, List<String> paramTypes, List<String> paramNames) {
        String paramType = ctx.type().getText();  // Get the parameter's type
        String paramName = ctx.ID().getText();    // Get the parameter's name

        // Check if the parameter has already been declared in the function's scope
        if (paramNames.contains(paramName)) {
            throw new RuntimeException("Parameter '" + paramName + "' already declared.");
        }

        // Add parameter type and name to the lists
        paramTypes.add(paramType);
        paramNames.add(paramName);

        return null;
    }

    // -------------------------------------Variables-------------------------------------
    @Override
    public Object visitVarDecl(miniCppParser.VarDeclContext ctx) {
        String varType = ctx.type().getText();
        String varName = ctx.ID().getText();

        System.out.println("Declaring variable: " + varName + " of type " + varType);

        if (variableTable.exists(varName)) {
            throw new RuntimeException("Variable '" + varName + "' already declared.");
        }

        variableTable.addVariable(varName, varType, null);

        if (ctx.expr() != null) {
            SemanticAnalyzer.ExprResult exprRes = (SemanticAnalyzer.ExprResult) visit(ctx.expr());
            System.out.println("Initializer expression type for " + varName + ": " + exprRes.type);
            String resultType = semanticCube.get(varType, "=", exprRes.type);
            if (resultType.equals("error")) {
                throw new RuntimeException("Incompatible types in initialization of '" + varName + "' (" + varType + " = " + exprRes.type + ")");
            }
        }

        return null;
    }

    //----------------------------------- Asignacion -----------------------------------------------------------
    @Override
    public Object visitAssignment(miniCppParser.AssignmentContext ctx) {
        String varName = ctx.lhs().ID().getText();  // nombre de variable a la izquierda
        String varType = variableTable.getType(varName);  // tipo declarado de la variable

        if (varType == null) {
            throw new RuntimeException("Variable '" + varName + "' not declared.");
        }

        ExprResult exprRes = (ExprResult) visit(ctx.expr());  // valor y tipo de la expresión derecha

        // Chequeo de tipos
        String resultType = semanticCube.get(varType, "=", exprRes.type);
        if (resultType.equals("error")) {
            throw new RuntimeException("Incompatible types in assignment: '" + varType + "' = '" + exprRes.type + "'");
        }

        // Generar cuádruplo de asignación
        quadGen.addQuadruple("=", exprRes.value, "", varName);

        return null;
    }

    // ---------------------------------------------Expresiones-----------------------------------------------------
    @Override
    public Object visitLiteralExpr(miniCppParser.LiteralExprContext ctx) {
        String value = ctx.getText();
        String type;
        if (ctx.literal().INT() != null) type = "int";
        else if (ctx.literal().FLOAT() != null) type = "float";
        else if (ctx.literal().BOOL() != null) type = "bool";
        else if (ctx.literal().STRING() != null) type = "string";
        else throw new RuntimeException("Unknown literal type: " + value);
        return new ExprResult(value, type);
    }

    @Override
    public Object visitVariableExpr(miniCppParser.VariableExprContext ctx) {
        String varName = ctx.ID().getText();
        String type = variableTable.getType(varName);
        if (type == null) throw new RuntimeException("Variable '" + varName + "' not declared.");
        return new ExprResult(varName, type);
    }

    // ----------------------------------- Operadores ----------------------------------------
    @Override
    public Object visitAdditiveExpr(miniCppParser.AdditiveExprContext ctx) {
        ExprResult left = (ExprResult) visit(ctx.expr(0));
        ExprResult right = (ExprResult) visit(ctx.expr(1));
        String op = ctx.op.getText();

        String resultType = semanticCube.get(left.type, op, right.type);
        if ("error".equals(resultType)) {
            throw new RuntimeException("Type error: " + left.type + " " + op + " " + right.type);
        }

        String temp = quadGen.newTemp();
        quadGen.addQuadruple(op, left.value, right.value, temp);
        variableTable.addVariable(temp, resultType, "temp");
        return new ExprResult(temp, resultType);
    }

    @Override
    public Object visitMultiplicativeExpr(miniCppParser.MultiplicativeExprContext ctx) {
        ExprResult left = (ExprResult) visit(ctx.expr(0));
        ExprResult right = (ExprResult) visit(ctx.expr(1));
        String op = ctx.op.getText();

        String resultType = semanticCube.get(left.type, op, right.type);
        if ("error".equals(resultType)) {
            throw new RuntimeException("Type error: " + left.type + " " + op + " " + right.type);
        }

        String temp = quadGen.newTemp();
        quadGen.addQuadruple(op, left.value, right.value, temp);
        variableTable.addVariable(temp, resultType, "temp");
        return new ExprResult(temp, resultType);
    }

    @Override
    public Object visitRelationalExpr(miniCppParser.RelationalExprContext ctx) {
        ExprResult left = (ExprResult) visit(ctx.expr(0));
        ExprResult right = (ExprResult) visit(ctx.expr(1));
        String op = ctx.op.getText();

        String resultType = semanticCube.get(left.type, op, right.type);
        if ("error".equals(resultType)) {
            throw new RuntimeException("Type error: " + left.type + " " + op + " " + right.type);
        }

        String temp = quadGen.newTemp();
        quadGen.addQuadruple(op, left.value, right.value, temp);
        variableTable.addVariable(temp, resultType, "temp");
        return new ExprResult(temp, resultType); // resultType será "bool"
    }

    @Override
    public Object visitLogicalAndExpr(miniCppParser.LogicalAndExprContext ctx) {
        ExprResult left = (ExprResult) visit(ctx.expr(0));
        ExprResult right = (ExprResult) visit(ctx.expr(1));
        String op = ctx.op.getText();

        // Ensure both expressions are boolean
        if (!left.type.equals("bool") || !right.type.equals("bool")) {
            throw new RuntimeException("Both operands must be boolean for logical AND: '" + left.type + "' && '" + right.type + "'");
        }

        String temp = quadGen.newTemp();
        quadGen.addQuadruple(op, left.value, right.value, temp);
        variableTable.addVariable(temp, "bool", "temp");
        return new ExprResult(temp, "bool");
    }

    @Override
    public Object visitLogicalOrExpr(miniCppParser.LogicalOrExprContext ctx) {
        ExprResult left = (ExprResult) visit(ctx.expr(0));
        ExprResult right = (ExprResult) visit(ctx.expr(1));
        String op = ctx.op.getText();

        if (!left.type.equals("bool") || !right.type.equals("bool")) {
            throw new RuntimeException("Both operands must be boolean for logical OR: '" + left.type + "' || '" + right.type + "'");
        }

        String temp = quadGen.newTemp();
        quadGen.addQuadruple(op, left.value, right.value, temp);
        variableTable.addVariable(temp, "bool", "temp");
        return new ExprResult(temp, "bool");
    }

    @Override
    public Object visitLogicalNotExpr(miniCppParser.LogicalNotExprContext ctx) {
        ExprResult expr = (ExprResult) visit(ctx.expr());
        if (!expr.type.equals("bool")) {
            throw new RuntimeException("Operand must be boolean for logical NOT: '" + expr.type + "'");
        }

        String temp = quadGen.newTemp();
        quadGen.addQuadruple("!", expr.value, "", temp);
        variableTable.addVariable(temp, "bool", "temp");
        return new ExprResult(temp, "bool");
    }

    // ------------------------------ Ciclos -------------------------------------------------
    @Override
    public Object visitIfStatement(miniCppParser.IfStatementContext ctx) {
        ExprResult cond = (ExprResult) visit(ctx.expr());  // NO String, sino ExprResult

        if (!cond.type.equals("bool")) {
            throw new RuntimeException("Condition of 'if' statement must be boolean, but found '" + cond.type + "'");
        }

        visit(ctx.statement(0));  // Visit the 'if' body
        if (ctx.statement(1) != null) {
            visit(ctx.statement(1));  // Visit the 'else' body, if it exists
        }

        return null;
    }

    @Override
    public Object visitWhileStatement(miniCppParser.WhileStatementContext ctx) {
        ExprResult cond = (ExprResult) visit(ctx.expr());

        if (!cond.type.equals("bool")) {
            throw new RuntimeException("Condition of 'while' statement must be boolean, but found '" + cond.type + "'");
        }

        visit(ctx.statement());
        return null;
    }

    @Override
    public Object visitForStatement(miniCppParser.ForStatementContext ctx) {
        // Visit the initialization
        visit(ctx.init);

        // Checa la condición
        ExprResult cond = (ExprResult) visit(ctx.cond);

        if (cond != null && !cond.type.equals("bool")) {
            throw new RuntimeException("Condition of 'for' statement must be boolean, but found '" + cond.type + "'");
        }

        visit(ctx.stmt);  // Visit the body of the loop

        return null;
    }

    // --------------------------------- Print y Input ----------------------------------------------------
    @Override
    public Object visitPrintStatement(miniCppParser.PrintStatementContext ctx) {
        if (ctx.callArgs() != null) {
            for (miniCppParser.ExprContext expr : ctx.callArgs().expr()) {
                ExprResult arg = (ExprResult) visit(expr);  // get value & type
                quadGen.addQuadruple("PRINT", arg.value, "", "");
            }
        }
        return null;
    }

    @Override
    public Object visitInputExpr(miniCppParser.InputExprContext ctx) {
        // Puedes modelar input como un temporal, si lo usas en expresiones
        String temp = quadGen.newTemp();
        quadGen.addQuadruple("INPUT", "", "", temp);
        variableTable.addVariable(temp, "string", "temp");
        return new ExprResult(temp, "string");
    }


}
