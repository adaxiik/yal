
from string import ascii_letters
from typing import Callable


class ParseResult:
    def __init__(self, value, rest):
        self.value = value
        self.rest = rest

    def __repr__(self):
        return f"ParseResult({self.value}, {self.rest})"

    @staticmethod
    def invalid() -> 'ParseResult':
        return ParseResult(None, None)

    @property
    def is_valid(self) -> bool:
        return self.value is not None and self.rest is not None

    @property
    def is_invalid(self) -> bool:
        return not self.is_valid

    @property
    def has_value(self) -> bool:
        return self.value is not None


Parser = Callable[[str], ParseResult]

class ParseCombinator:

    @staticmethod
    def create_char_parser(input_char: str) -> Parser:
        def parser(input_str) -> ParseResult:
            if input_str.startswith(input_char):
                return ParseResult(input_char, input_str[1:])
            else:
                return ParseResult.invalid()
        return parser

    @staticmethod
    def create_string_parser(input_str: str) -> Parser:
        return ParseCombinator.create_sequence_parser([ParseCombinator.create_char_parser(c) for c in input_str])

    @staticmethod
    def create_optional_parser(parser: Parser) -> Parser:
        def optional_parser(input_str) -> ParseResult:
            result = parser(input_str)
            if result.is_invalid:
                return ParseResult("", input_str)
            else:
                return result
        return optional_parser

    @staticmethod
    def create_alternative_parser(parsers: list[Parser]) -> Parser:
        def alternative_parser(input_str) -> ParseResult:
            for parser in parsers:
                result = parser(input_str)
                if result.is_valid:
                    return result
            return ParseResult.invalid()
        return alternative_parser

    @staticmethod
    def create_repeat_parser(parser: Parser, min_count: int = 0) -> Parser:
        def repeat_parser(input_str) -> ParseResult:
            values = []
            rest = input_str
            while True:
                result = parser(rest)
                if result.is_invalid:
                    break
                else:
                    values.append(result.value)
                    rest = result.rest
            if len(values) < min_count:
                return ParseResult.invalid()
            else:
                return ParseResult("".join(values), rest)
        return repeat_parser

    @staticmethod
    def create_sequence_parser(parsers: list[Parser]) -> Parser:
        def sequence_parser(string) -> ParseResult:
            values = []
            rest = string
            for parser in parsers:
                result = parser(rest)
                if result.is_invalid:
                    return ParseResult.invalid()
                else:
                    values.append(result.value)
                    rest = result.rest
            return ParseResult("".join(values), rest)
        return sequence_parser

class ApplyParser:
    digit = ParseCombinator.create_alternative_parser([ParseCombinator.create_char_parser(str(i)) for i in range(10)])
    printable = ParseCombinator.create_alternative_parser([ParseCombinator.create_char_parser(x) for x in ascii_letters])
    uint = ParseCombinator.create_repeat_parser(digit, min_count=1)
    lparen = ParseCombinator.create_char_parser('(')
    rparen = ParseCombinator.create_char_parser(')')
    whitespace = ParseCombinator.create_repeat_parser(ParseCombinator.create_alternative_parser([ParseCombinator.create_char_parser(' '), ParseCombinator.create_char_parser('\t')]), 1)
    newline = ParseCombinator.create_char_parser('\n')
    op =  ParseCombinator.create_alternative_parser(
        [
            ParseCombinator.create_char_parser('+'),
            ParseCombinator.create_char_parser('-'),
            ParseCombinator.create_char_parser('*'),
            ParseCombinator.create_char_parser('/')
        ]
    )

    div = ParseCombinator.create_string_parser('div')
    mod = ParseCombinator.create_string_parser('mod')
    comment = ParseCombinator.create_sequence_parser(
        [
            ParseCombinator.create_string_parser('//'),
            ParseCombinator.create_repeat_parser(
                ParseCombinator.create_alternative_parser([printable, whitespace])
                , 1
            ),
            newline
        ]
    )

    semicolon = ParseCombinator.create_char_parser(';')
    identifier = ParseCombinator.create_repeat_parser(
        printable
        , 1
    )
