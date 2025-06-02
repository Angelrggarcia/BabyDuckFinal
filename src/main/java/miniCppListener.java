// Generated from miniCpp.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link miniCppParser}.
 */
public interface miniCppListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link miniCppParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(miniCppParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(miniCppParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#functionDecl}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDecl(miniCppParser.FunctionDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#functionDecl}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDecl(miniCppParser.FunctionDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#paramList}.
	 * @param ctx the parse tree
	 */
	void enterParamList(miniCppParser.ParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#paramList}.
	 * @param ctx the parse tree
	 */
	void exitParamList(miniCppParser.ParamListContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(miniCppParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(miniCppParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterVarDecl(miniCppParser.VarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitVarDecl(miniCppParser.VarDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(miniCppParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(miniCppParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#body}.
	 * @param ctx the parse tree
	 */
	void enterBody(miniCppParser.BodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#body}.
	 * @param ctx the parse tree
	 */
	void exitBody(miniCppParser.BodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(miniCppParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(miniCppParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#lhs}.
	 * @param ctx the parse tree
	 */
	void enterLhs(miniCppParser.LhsContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#lhs}.
	 * @param ctx the parse tree
	 */
	void exitLhs(miniCppParser.LhsContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(miniCppParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(miniCppParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#printStatement}.
	 * @param ctx the parse tree
	 */
	void enterPrintStatement(miniCppParser.PrintStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#printStatement}.
	 * @param ctx the parse tree
	 */
	void exitPrintStatement(miniCppParser.PrintStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#functionCallStmt}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCallStmt(miniCppParser.FunctionCallStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#functionCallStmt}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCallStmt(miniCppParser.FunctionCallStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(miniCppParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(miniCppParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(miniCppParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(miniCppParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#forStatement}.
	 * @param ctx the parse tree
	 */
	void enterForStatement(miniCppParser.ForStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#forStatement}.
	 * @param ctx the parse tree
	 */
	void exitForStatement(miniCppParser.ForStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#forInit}.
	 * @param ctx the parse tree
	 */
	void enterForInit(miniCppParser.ForInitContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#forInit}.
	 * @param ctx the parse tree
	 */
	void exitForInit(miniCppParser.ForInitContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#forCond}.
	 * @param ctx the parse tree
	 */
	void enterForCond(miniCppParser.ForCondContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#forCond}.
	 * @param ctx the parse tree
	 */
	void exitForCond(miniCppParser.ForCondContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void enterForUpdate(miniCppParser.ForUpdateContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void exitForUpdate(miniCppParser.ForUpdateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PrimaryCallExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryCallExpr(miniCppParser.PrimaryCallExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PrimaryCallExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryCallExpr(miniCppParser.PrimaryCallExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LogicalOrExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOrExpr(miniCppParser.LogicalOrExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LogicalOrExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOrExpr(miniCppParser.LogicalOrExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MultiplicativeExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpr(miniCppParser.MultiplicativeExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MultiplicativeExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpr(miniCppParser.MultiplicativeExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AdditiveExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpr(miniCppParser.AdditiveExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AdditiveExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpr(miniCppParser.AdditiveExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LogicalNotExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLogicalNotExpr(miniCppParser.LogicalNotExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LogicalNotExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLogicalNotExpr(miniCppParser.LogicalNotExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RelationalExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpr(miniCppParser.RelationalExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RelationalExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpr(miniCppParser.RelationalExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code InExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterInExpr(miniCppParser.InExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code InExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitInExpr(miniCppParser.InExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LogicalAndExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLogicalAndExpr(miniCppParser.LogicalAndExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LogicalAndExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLogicalAndExpr(miniCppParser.LogicalAndExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IndexExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterIndexExpr(miniCppParser.IndexExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IndexExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitIndexExpr(miniCppParser.IndexExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterParenExpr(miniCppParser.ParenExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitParenExpr(miniCppParser.ParenExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionCallExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCallExpr(miniCppParser.FunctionCallExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionCallExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCallExpr(miniCppParser.FunctionCallExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LambdaCallExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterLambdaCallExpr(miniCppParser.LambdaCallExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LambdaCallExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitLambdaCallExpr(miniCppParser.LambdaCallExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code InputCallExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterInputCallExpr(miniCppParser.InputCallExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code InputCallExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitInputCallExpr(miniCppParser.InputCallExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterLiteralExpr(miniCppParser.LiteralExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitLiteralExpr(miniCppParser.LiteralExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(miniCppParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(miniCppParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VariableExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterVariableExpr(miniCppParser.VariableExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VariableExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitVariableExpr(miniCppParser.VariableExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(miniCppParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(miniCppParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#callArgs}.
	 * @param ctx the parse tree
	 */
	void enterCallArgs(miniCppParser.CallArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#callArgs}.
	 * @param ctx the parse tree
	 */
	void exitCallArgs(miniCppParser.CallArgsContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#inputExpr}.
	 * @param ctx the parse tree
	 */
	void enterInputExpr(miniCppParser.InputExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#inputExpr}.
	 * @param ctx the parse tree
	 */
	void exitInputExpr(miniCppParser.InputExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#lambdaExpr}.
	 * @param ctx the parse tree
	 */
	void enterLambdaExpr(miniCppParser.LambdaExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#lambdaExpr}.
	 * @param ctx the parse tree
	 */
	void exitLambdaExpr(miniCppParser.LambdaExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(miniCppParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(miniCppParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#listLiteral}.
	 * @param ctx the parse tree
	 */
	void enterListLiteral(miniCppParser.ListLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#listLiteral}.
	 * @param ctx the parse tree
	 */
	void exitListLiteral(miniCppParser.ListLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#dictLiteral}.
	 * @param ctx the parse tree
	 */
	void enterDictLiteral(miniCppParser.DictLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#dictLiteral}.
	 * @param ctx the parse tree
	 */
	void exitDictLiteral(miniCppParser.DictLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#keyValue}.
	 * @param ctx the parse tree
	 */
	void enterKeyValue(miniCppParser.KeyValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#keyValue}.
	 * @param ctx the parse tree
	 */
	void exitKeyValue(miniCppParser.KeyValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(miniCppParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(miniCppParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#baseType}.
	 * @param ctx the parse tree
	 */
	void enterBaseType(miniCppParser.BaseTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#baseType}.
	 * @param ctx the parse tree
	 */
	void exitBaseType(miniCppParser.BaseTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCppParser#genericType}.
	 * @param ctx the parse tree
	 */
	void enterGenericType(miniCppParser.GenericTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCppParser#genericType}.
	 * @param ctx the parse tree
	 */
	void exitGenericType(miniCppParser.GenericTypeContext ctx);
}