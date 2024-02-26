from enum import Enum
from dataclasses import dataclass
from typing import Optional, Union

from parser_combinators import ApplyParser, Parser

class TokenKind(Enum):
    NUMBER     = 0
    LPAREN     = 1
    RPAREN     = 2
    OP         = 3
    SEMICOLON  = 4
    IDENTIFIER = 5
    DIV        = 6
    MOD        = 7
    COMMENT    = 8

@dataclass
class Token():
    token_kind: TokenKind
    value: str

def tokenize(input_str: str) -> Optional[list[Token]]:
    rest: str = input_str
    tokens: list[Token] = []

    def apply(parser: Parser, token_kind: TokenKind) -> bool:
        nonlocal tokens, rest

        result = parser(rest)
        if result.has_value:
            tokens.append(Token(token_kind, result.value))
            rest = result.rest
            return True

        return False


    while True:
        if rest.startswith(' '):
            rest = rest[1:]
            continue

        if not rest:
            return [x for x in tokens if x.token_kind != TokenKind.COMMENT]

        if apply(ApplyParser.comment,TokenKind.COMMENT):
            continue

        if apply(ApplyParser.uint, TokenKind.NUMBER):
            continue

        if apply(ApplyParser.lparen, TokenKind.LPAREN):
            continue

        if apply(ApplyParser.rparen, TokenKind.RPAREN):
            continue

        if apply(ApplyParser.op, TokenKind.OP):
            continue

        if apply(ApplyParser.div, TokenKind.DIV):
            continue

        if apply(ApplyParser.mod, TokenKind.MOD):
            continue

        if apply(ApplyParser.semicolon, TokenKind.SEMICOLON):
            continue

        if apply(ApplyParser.identifier, TokenKind.IDENTIFIER):
            continue

        if not rest.strip():
            rest = rest.strip()
            continue

        print('Unable to parse:', rest)
        return None
