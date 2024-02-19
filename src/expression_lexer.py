from enum import Enum
from dataclasses import dataclass
from typing import Optional, Union

from parser_combinators import ApplyParser, Parser

class TokenKind(Enum):
    NUMBER = 0
    LPAREN = 1
    RPAREN = 2
    PLUS   = 3
    MINUS  = 4
    MUL    = 5
    DIV    = 6

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
        if not rest:
            return tokens

        if apply(ApplyParser.uint, TokenKind.NUMBER):
            continue

        if apply(ApplyParser.lparen, TokenKind.LPAREN):
            continue

        if apply(ApplyParser.rparen, TokenKind.RPAREN):
            continue

        if apply(ApplyParser.plus, TokenKind.PLUS):
            continue

        if apply(ApplyParser.minus, TokenKind.MINUS):
            continue

        if apply(ApplyParser.mul, TokenKind.MUL):
            continue

        if apply(ApplyParser.div, TokenKind.DIV):
            continue

        return None
