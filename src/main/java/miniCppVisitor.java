// Generated from miniCpp.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link miniCppParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface miniCppVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link miniCppParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(miniCppParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#functionDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDecl(miniCppParser.FunctionDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamList(miniCppParser.ParamListContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(miniCppParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecl(miniCppParser.VarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(miniCppParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBody(miniCppParser.BodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(miniCppParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#lhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLhs(miniCppParser.LhsContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(miniCppParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#printStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintStatement(miniCppParser.PrintStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#functionCallStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCallStmt(miniCppParser.FunctionCallStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(miniCppParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#whileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(miniCppParser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#forStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStatement(miniCppParser.ForStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#forInit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForInit(miniCppParser.ForInitContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#forCond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForCond(miniCppParser.ForCondContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#forUpdate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForUpdate(miniCppParser.ForUpdateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PrimaryCallExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryCallExpr(miniCppParser.PrimaryCallExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalOrExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalOrExpr(miniCppParser.LogicalOrExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MultiplicativeExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeExpr(miniCppParser.MultiplicativeExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AdditiveExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveExpr(miniCppParser.AdditiveExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalNotExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalNotExpr(miniCppParser.LogicalNotExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RelationalExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpr(miniCppParser.RelationalExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code InExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInExpr(miniCppParser.InExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalAndExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalAndExpr(miniCppParser.LogicalAndExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IndexExpr}
	 * labeled alternative in {@link miniCppParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndexExpr(miniCppParser.IndexExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExpr(miniCppParser.ParenExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionCallExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCallExpr(miniCppParser.FunctionCallExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LambdaCallExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLambdaCallExpr(miniCppParser.LambdaCallExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code InputCallExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputCallExpr(miniCppParser.InputCallExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LiteralExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralExpr(miniCppParser.LiteralExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(miniCppParser.StringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VariableExpr}
	 * labeled alternative in {@link miniCppParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableExpr(miniCppParser.VariableExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(miniCppParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#callArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallArgs(miniCppParser.CallArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#inputExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputExpr(miniCppParser.InputExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#lambdaExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLambdaExpr(miniCppParser.LambdaExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IntLiteral}
	 * labeled alternative in {@link miniCppParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntLiteral(miniCppParser.IntLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FloatLiteral}
	 * labeled alternative in {@link miniCppParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatLiteral(miniCppParser.FloatLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BoolLiteral}
	 * labeled alternative in {@link miniCppParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolLiteral(miniCppParser.BoolLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ListCallLiteral}
	 * labeled alternative in {@link miniCppParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListCallLiteral(miniCppParser.ListCallLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DictCallLiteral}
	 * labeled alternative in {@link miniCppParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDictCallLiteral(miniCppParser.DictCallLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#listLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListLiteral(miniCppParser.ListLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#dictLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDictLiteral(miniCppParser.DictLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#keyValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeyValue(miniCppParser.KeyValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(miniCppParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#baseType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBaseType(miniCppParser.BaseTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCppParser#genericType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericType(miniCppParser.GenericTypeContext ctx);
}