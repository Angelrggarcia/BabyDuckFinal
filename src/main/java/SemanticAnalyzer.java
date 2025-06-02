import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public class SemanticAnalyzer extends miniCppBaseVisitor<Object> {

    // Declare instance variables for all components
    private final VariableTable variableTable;
    private final FunctionDirectory functionDirectory;
    private final SemanticCube semanticCube;
    private final VirtualMemoryManager virtualMemoryManager;
    private final Stack<String> functionReturnTypeStack;
    private final QuadrupleGenerator quadGen;
    private final Stack<String> functionNameStack = new Stack<>();

    public SemanticAnalyzer(FunctionDirectory funcDir) {
        this.functionDirectory = funcDir;
        virtualMemoryManager = new VirtualMemoryManager();
        this.variableTable = new VariableTable(this.virtualMemoryManager);
        this.quadGen = new QuadrupleGenerator(funcDir, variableTable, virtualMemoryManager);
        semanticCube = new SemanticCube();
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

        System.out.println("VirtualMemoryManager de SemanticAnalyzer: " + System.identityHashCode(virtualMemoryManager));
        System.out.println("VirtualMemoryManager de QuadrupleGenerator: " + System.identityHashCode(quadGen.getMemoryManager()));

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
            String paramName = functionInfo.paramNames.get(i);  // <-- usa el nombre real
            if (!args.get(i).type.equals(paramType)) {
                throw new RuntimeException("Argument type mismatch at position " + (i + 1) + " for function '" +
                        functionName + "'. Expected '" + paramType + "', found '" + args.get(i).type + "'.");
            }
            quadGen.addQuadruple("PARAM", args.get(i).value, "", paramName);
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

        // Usa el nombre actual de la función para saber a dónde retornar
        String functionName = functionNameStack.isEmpty() ? null : functionNameStack.peek();

        // Si la función es void, puedes poner RETURN sin destino
        if (functionName != null && !"void".equals(returnType)) {
            quadGen.addQuadruple("RETURN", exprRes.value, "", functionName + "_ret");
        } else {
            quadGen.addQuadruple("RETURN", "", "", "");
        }
        return null;
    }

    @Override
    public Object visitFunctionCallStmt(miniCppParser.FunctionCallStmtContext ctx) {
        visit(ctx.functionCall());
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

        // --------- Agrega variable virtual para el return de la función -----------
        String returnVarName = functionName + "_ret";
        // En visitFunctionDecl, al crear la variable de retorno:
        int retAddr = virtualMemoryManager.allocate("local", returnType); // <-- LOCAL, no global
        virtualMemoryManager.set(returnVarName, retAddr);
        variableTable.addVariable(returnVarName, returnType, "local");

        // --- LÍNEA CLAVE: Guarda el índice actual de cuádruplo donde inicia la función ---
        quadGen.addFunctionStart(functionName, quadGen.getQuadruples().size());

        // New scope for variables
        variableTable.enterScope(functionName);

        // Agrega los parámetros como variables locales Y les asigna dirección virtual
        for (int i = 0; i < paramNames.size(); i++) {
            variableTable.addVariable(paramNames.get(i), paramTypes.get(i), "local");
            int addr = virtualMemoryManager.allocate("local", paramTypes.get(i));
            virtualMemoryManager.set(paramNames.get(i), addr);
        }

        // Analiza el cuerpo de la función
        functionNameStack.push(functionName);   // <---- PUSH
        functionReturnTypeStack.push(returnType);
        visit(ctx.body());
        functionReturnTypeStack.pop();
        functionNameStack.pop();                // <---- POP
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

        // --- DETERMINA SCOPE ACTUAL ---
        // Aquí puedes tener una variable booleana o método, por ejemplo:
        // boolean isGlobalScope = (functionReturnTypeStack.isEmpty());
        // O mejor, mantén un flag de scope actual en tu SemanticAnalyzer.
        String scope = functionReturnTypeStack.isEmpty() ? "global" : "local";

        // --- ASIGNA DIRECCIÓN VIRTUAL ---
        int addr = virtualMemoryManager.allocate(scope, varType);
        virtualMemoryManager.set(varName, addr);

        // --- AGREGA VARIABLE A LA TABLA DE VARIABLES LOGICA (opcional, si sigues usando variableTable) ---
        variableTable.addVariable(varName, varType, scope);

        if (ctx.expr() != null) {
            SemanticAnalyzer.ExprResult exprRes = (SemanticAnalyzer.ExprResult) visit(ctx.expr());
            System.out.println("Initializer expression type for " + varName + ": " + exprRes.type);
            String resultType = semanticCube.get(varType, "=", exprRes.type);
            if (resultType.equals("error")) {
                throw new RuntimeException("Incompatible types in initialization of '" + varName + "' (" + varType + " = " + exprRes.type + ")");
            }
            // Puedes agregar el cuádruplo de asignación (inicialización) aquí si quieres
            // quadGen.addQuadruple("=", exprRes.value, "", varName); // o mejor, con direcciones
            quadGen.addQuadruple("=", exprRes.value, "", varName); // exprRes.value ya debe ser nombre temporal/constante
        }

        return null;
    }

    //----------------------------------- Asignacion -----------------------------------------------------------
    @Override
    public Object visitAssignment(miniCppParser.AssignmentContext ctx) {
        String varName = ctx.lhs().ID().getText();      // nombre de variable a la izquierda
        String varType = variableTable.getType(varName); // tipo declarado de la variable

        if (varType == null) {
            throw new RuntimeException("Variable '" + varName + "' not declared.");
        }

        ExprResult exprRes = (ExprResult) visit(ctx.expr());  // valor y tipo de la expresión derecha

        // Chequeo de tipos
        String resultType = semanticCube.get(varType, "=", exprRes.type);
        if (resultType.equals("error")) {
            throw new RuntimeException("Incompatible types in assignment: '" + varType + "' = '" + exprRes.type + "'");
        }

        // --- Aquí es donde asocias nombre->dirección ---
        Integer addrLeft = virtualMemoryManager.get(varName);
        if (addrLeft == null) {
            throw new RuntimeException("No virtual address found for variable '" + varName + "'");
        }

        // El lado derecho puede ser una constante, variable, o temporal; su value es el nombre/constante
        String rightOperand = exprRes.value; // Usualmente lo dejas así, la impresión de cuádruplo hará la traducción

        // Puedes guardar el cuádruplo con nombres (mejor práctica), y luego traducir a direcciones en la impresión
        quadGen.addQuadruple("=", rightOperand, "", varName);

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
        // Evalúa la condición
        ExprResult cond = (ExprResult) visit(ctx.expr());
        if (!cond.type.equals("bool")) {
            throw new RuntimeException("Condition of 'if' statement must be boolean, but found '" + cond.type + "'");
        }

        // Genera el cuádruplo GOTOF (deja pendiente el destino)
        int gotofIndex = quadGen.addQuadruple("GOTOF", cond.value, "", ""); // El result se llenará después

        // Visita el cuerpo del if
        visit(ctx.statement(0));

        if (ctx.statement(1) != null) { // Hay else
            // Genera GOTO para saltar fuera del else
            int gotoEndIfElse = quadGen.addQuadruple("GOTO", "", "", ""); // también pendiente

            // Ahora sí, rellena el destino del GOTOF para saltar al else
            quadGen.getQuadruples().get(gotofIndex).setResult(String.valueOf(quadGen.getQuadruples().size()));

            // Visita el cuerpo del else
            visit(ctx.statement(1));

            // Rellena el destino del GOTO para saltar después del else
            quadGen.getQuadruples().get(gotoEndIfElse).setResult(String.valueOf(quadGen.getQuadruples().size()));
        } else {
            // Rellena el destino del GOTOF para saltar después del if
            quadGen.getQuadruples().get(gotofIndex).setResult(String.valueOf(quadGen.getQuadruples().size()));
        }

        return null;
    }


    @Override
    public Object visitWhileStatement(miniCppParser.WhileStatementContext ctx) {
        // Marca el inicio del ciclo (donde se evalúa la condición)
        int conditionQuadIndex = quadGen.getQuadruples().size();

        // Evalúa la condición
        ExprResult cond = (ExprResult) visit(ctx.expr());
        if (!cond.type.equals("bool")) {
            throw new RuntimeException("Condition of 'while' statement must be boolean, but found '" + cond.type + "'");
        }

        // Cuádruplo GOTOF (pendiente de destino)
        int gotofIndex = quadGen.addQuadruple("GOTOF", cond.value, "", "");

        // Cuerpo del ciclo
        visit(ctx.statement());

        // Regresa al inicio de la condición
        quadGen.addQuadruple("GOTO", "", "", String.valueOf(conditionQuadIndex));

        // Rellena el destino del GOTOF (fin del ciclo)
        quadGen.getQuadruples().get(gotofIndex).setResult(String.valueOf(quadGen.getQuadruples().size()));

        return null;
    }

    @Override
    public Object visitForStatement(miniCppParser.ForStatementContext ctx) {
        // Inicialización (ejemplo: int i = 0;)
        visit(ctx.init);

        // Marca el inicio de la condición
        int conditionQuadIndex = quadGen.getQuadruples().size();

        // Evalúa condición
        ExprResult cond = (ExprResult) visit(ctx.cond);
        if (cond != null && !cond.type.equals("bool")) {
            throw new RuntimeException("Condition of 'for' statement must be boolean, but found '" + cond.type + "'");
        }

        // Cuádruplo GOTOF (pendiente de destino)
        int gotofIndex = quadGen.addQuadruple("GOTOF", cond != null ? cond.value : "", "", "");

        // Cuerpo del for
        visit(ctx.stmt);

        // Actualización (ejemplo: i++)
        visit(ctx.update);

        // Regresa a la condición
        quadGen.addQuadruple("GOTO", "", "", String.valueOf(conditionQuadIndex));

        // Rellena el destino del GOTOF (fin del for)
        quadGen.getQuadruples().get(gotofIndex).setResult(String.valueOf(quadGen.getQuadruples().size()));

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
