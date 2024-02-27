from grammar import Grammar
from rule import Rule
from grammar_symbol import NonTerminal, Symbol, Terminal

def slurp_file(path: str) -> list[str]:
    with open(path, 'r') as f:
        return [x.strip() for x in f.readlines()]


class GrammarParser():

    @staticmethod
    def consume_comment(line: str) -> str:
        if not line.startswith('{'):
            return line


        while True:
            if line.startswith('}'):
                return line[1:]
            line = line[1:]

    @staticmethod
    def grammar_from_lines(lines: list[str]) -> Grammar:
        grammar: Grammar = Grammar()

        def create_symbol(symbol: str) -> Symbol:
            if not symbol:
                return grammar.add_terminal(Terminal(None))

            if symbol.islower():
                return grammar.add_terminal(Terminal(symbol))

            return grammar.add_nonterminal(NonTerminal(symbol, []))

        for line in lines:
            line = GrammarParser.consume_comment(line).removesuffix(';')

            if not line:
                continue

            nonterminal, rest = line.split(':')
            nonterminal: NonTerminal = create_symbol(nonterminal.strip())

            rules_str = [x.strip() for x in rest.split('|')]
            for rule_str in rules_str:
                symbols: list[Symbol] = [create_symbol(GrammarParser.consume_comment(x)) for x in rule_str.split(' ')]
                nonterminal.add_rule(Rule(nonterminal, symbols))

        return grammar

    @staticmethod
    def grammar_from_file(path: str) -> Grammar:
        return GrammarParser.grammar_from_lines(slurp_file(path))