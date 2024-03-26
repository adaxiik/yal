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



program: (command ';')+;

command : typeSpecifier BoolLiteral?;

typeSpecifier
    : 'string'
    | 'bool'
    | 'int'
    | 'float';

BoolLiteral: 'true' | 'false';

StringLiteral: '"' StringCharacters? '"' ;
fragment StringCharacters: StringCharacter+ ;
fragment StringCharacter: ~["\\\r\n] ;

FloatLiteral : Digits '.' Digits? ;
fragment Digits: [0-9] ;

IntLiteral : NegativeIntLiteral | PositiveIntLiteral ;
fragment NegativeIntLiteral : '-' PositiveIntLiteral ;
fragment PositiveIntLiteral : [1-9][0-9]* ;

Whitespace: [ \t]+ -> channel(HIDDEN) ;

Newline : ('\r' '\n'? | '\n') -> channel(HIDDEN) ;

//
//FLOAT_TYPE_SPECIFIER : 'float';
//INT_TYPE_SPECIFIER : 'int';
//BOOL_TYPE_SPECIFIER : 'bool';
//STRING_TYPE_SPECIFIER : 'string';
//
//
//ID : [a-zA-Z]+ ;        // match identifiers
//OCT : '0'[0-7]* ;
//HEXA : '0x'[0-9a-fA-F]+ ;
