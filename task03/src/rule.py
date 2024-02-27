
from typing import List
from grammar_symbol import Epsilon, NonTerminal, Symbol


class Rule():
    def __init__(self,nonterminal: NonTerminal, symbols: List[Symbol]) -> None:
        self.nonterminal: NonTerminal = nonterminal
        self.symbols: List[Symbol] = symbols

    def __repr__(self) -> str:
        return ' '.join([str(x.name) for x in self.symbols])
