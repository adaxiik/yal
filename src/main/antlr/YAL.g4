grammar YAL;

/** The start rule; begin parsing here. */
//prog: (expr ';')+;
//
//expr: expr op=('*'|'/') expr    # mul
//    | expr op=('+'|'-') expr    # add
//    | INT                       # int
//    | OCT                       # oct
//    | HEXA                      # hexa
//    | '(' expr ')'              # par
//    ;
//



program : statement*;

//statement : typeSpecifier BoolLiteral?;
statement : declarationStatement
          | blockStatement
          | writeStatement
          | expressionStatement
          | whileStatement
          | ifStatement
          | emptyStatement
          ;


declarationStatement : TypeSpecifier Identificator (',' Identificator)* ';';
emptyStatement : ';' ;
blockStatement : '{' statement* '}' ;
writeStatement : 'write' expression (',' expression)* ';' ;
expressionStatement : expression ';' ;
whileStatement : 'while' '(' expression ')' statement ;
ifStatement : 'if' '(' expression ')' statement ('else' statement)? ;

// todo: jen promenne
// readStatement : 'read' expression (',' expression)*;

expression : literalExpression ;

literalExpression : StringLiteral
                  | FloatLiteral
                  | IntLiteral
                  | BoolLiteral
                  ;

TypeSpecifier
    : 'string'
    | 'bool'
    | 'float'
    | 'int'
    ;

//BoolLiteral: 'true' | 'false';


StringLiteral: '"' StringCharacters? '"' ;
fragment StringCharacters: StringCharacter+ ;
fragment StringCharacter: ~["\\\r\n] ;

FloatLiteral : Digits '.' Digits? ;
fragment Digits: [0-9] ;

BoolLiteral : 'true' | 'false' ;

IntLiteral : NegativeIntLiteral | PositiveIntLiteral ;
fragment NegativeIntLiteral : '-' PositiveIntLiteral ;
fragment PositiveIntLiteral : [1-9][0-9]* ;

Whitespace: [ \t]+ -> channel(HIDDEN) ;

Newline : ('\r' '\n'? | '\n') -> channel(HIDDEN) ;
Identificator : [a-zA-Z]+[a-zA-Z0-9]* ;

//
//
//ID : [a-zA-Z]+ ;        // match identifiers
//OCT : '0'[0-7]* ;
//HEXA : '0x'[0-9a-fA-F]+ ;
