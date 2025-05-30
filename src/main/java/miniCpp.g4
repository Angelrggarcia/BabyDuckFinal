grammar miniCpp;

// ------------------- Programa -------------------
program: (functionDecl | varDecl | statement)* EOF;

// ------------------- Declaraciones -------------------
functionDecl: type ID '(' paramList? ')' body;
paramList: param (',' param)*;
param: type ID ('=' expr)?;

// ------------------- Variables -------------------
varDecl: type ID ('[' INT ']')? ('=' expr)? ';';

// ------------------- Sentencias -------------------
statement
    : body
    | varDecl
    | assignment
    | ifStatement
    | whileStatement
    | forStatement
    | returnStatement
    | printStatement
    | functionCallStmt
    ;

body: '{' statement* '}';

assignment: lhs '=' expr ';';
lhs: ID | ID '[' expr ']';  // Para distinguir acceso a array

returnStatement: 'return' expr? ';';
printStatement: 'print' '(' callArgs? ')' ';';
functionCallStmt: functionCall ';';  // Llamadas como sentencias

// ------------------- Control -------------------
ifStatement: 'if' '(' expr ')' statement ('else' statement)?;
whileStatement: 'while' '(' expr ')' statement;

forStatement
    : 'for' '(' init=forInit cond=forCond ';' update=forUpdate ')' stmt=statement
    ;

forInit: varDecl | assignment | ';';
forCond: expr?;
forUpdate: assignment | expr | ;

// ------------------- Expresiones con Precedencia -------------------
expr
    : expr op='||' expr           # LogicalOrExpr
    | expr op='&&' expr           # LogicalAndExpr
    | '!' expr                                  # LogicalNotExpr
    | expr op=('=='|'!='|'<'|'>'|'<='|'>=') expr # RelationalExpr
    | expr 'in' expr                            # InExpr
    | expr op=('+'|'-') expr                    # AdditiveExpr
    | expr op=('*'|'/') expr                    # MultiplicativeExpr
    | expr '[' expr ']'                         # IndexExpr
    | primaryExpr                               # PrimaryCallExpr
    ;

primaryExpr
    : '(' expr ')'                              # ParenExpr
    | functionCall                              # FunctionCallExpr
    | lambdaExpr                                # LambdaCallExpr
    | inputExpr                                 # InputCallExpr
    | literal                                   # LiteralExpr
    | STRING                                    # StringLiteral
    | ID                                        # VariableExpr
    ;

// ------------------- Llamadas a funciones -------------------
functionCall: ID '(' callArgs? ')';
callArgs: expr (',' expr)*;
inputExpr: 'input' '(' ')';

// ------------------- Funciones lambda -------------------
lambdaExpr: '[' paramList? ']' '->' expr;

// ------------------- Literales -------------------
literal
    : INT        # IntLiteral
    | FLOAT      # FloatLiteral
    | BOOL       # BoolLiteral
    | listLiteral # ListCallLiteral
    | dictLiteral # DictCallLiteral
    ;

listLiteral: '[' callArgs? ']';
dictLiteral: '{' keyValue (',' keyValue)* '}';
keyValue: (ID | STRING | INT | expr) ':' expr;

// ------------------- Tipos -------------------
type: baseType genericType? | 'var';
baseType: 'int' | 'float' | 'double' | 'bool' | 'char' | 'void' | 'string' | 'list' | 'dict' | ID;
genericType: '<' type (',' type)* '>';

// ------------------- Tokens -------------------
ID: [a-zA-Z_][a-zA-Z_0-9]*;
INT: [0-9]+;
FLOAT: [0-9]+ '.' [0-9]+;
BOOL: 'true' | 'false';
STRING: '"' (ESC | ~["\\\r\n])* '"';
fragment ESC: '\\' ["\\/bfnrt];

WS: [ \t\r\n]+ -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;
BLOCK_COMMENT: '/*' .*? '*/' -> skip;
