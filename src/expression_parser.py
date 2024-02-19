from typing import Optional, Tuple, Union
from enum import Enum
from dataclasses import dataclass
from parser_combinators import ApplyParser
from expression_lexer import Token, TokenKind

class OpKind(Enum):
    ADD = '+'
    SUB = '-'
    MUL = '*'
    DIV = '/'



Expression = Union["NumberExpression", "BinaryExpression"]

@dataclass
class NumberExpression():
    value: int

@dataclass
class BinaryExpression():
    left: Expression
    right: Expression
    op: OpKind

class ParserData():
    def __init__(self, tokens) -> None:
        self.tokens: list[Token] = tokens
        self.index: int = 0

    @property
    def peek(self):
        return self.tokens[self.index]

    @property
    def has_next(self):
        return self.index < len(self.tokens)

    def check_next(self, token_kind: TokenKind) -> bool:
        if self.has_next:
            return self.peek.token_kind == token_kind
        else:
            return False

    def consume(self):
        self.index += 1

def parse(tokens: list[Token]) ->Optional[Expression]:
    return parse_plus_minus(ParserData(tokens))

def parse_plus_minus(data: ParserData) ->Optional[Expression]:
    left = parse_mul_div(data)
    while data.check_next(TokenKind.PLUS) or data.check_next(TokenKind.MINUS):
        op = OpKind.ADD if data.peek.token_kind == TokenKind.PLUS else OpKind.SUB
        data.consume()
        right = parse_mul_div(data)
        left = BinaryExpression(left, right, op)
    return left

def parse_mul_div(data: ParserData) ->Optional[Expression]:
    left = parse_number(data)
    while data.check_next(TokenKind.MUL) or data.check_next(TokenKind.DIV):
        op = OpKind.MUL if data.peek.token_kind == TokenKind.MUL else OpKind.DIV
        data.consume()
        right = parse_number(data)
        left = BinaryExpression(left, right, op)
    return left

def parse_number(data: ParserData) ->Optional[Expression]:
    if data.check_next(TokenKind.NUMBER):
        value = int(data.peek.value)
        data.consume()
        return NumberExpression(value)

    elif data.check_next(TokenKind.LPAREN):
        data.consume()
        expression = parse_plus_minus(data)
        if data.check_next(TokenKind.RPAREN):
            data.consume()
            return expression

    return None
