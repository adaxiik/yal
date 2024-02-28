
from collections import defaultdict
from typing import Dict, List, Optional, Set, Tuple
from grammar_symbol import NonTerminal, Symbol, Terminal


class Grammar():
    def __init__(self) -> None:
        self.nonterminals: List[NonTerminal] = []
        self.terminals: List[Terminal] = []

    def _get_symbol(self, symbol: Symbol) -> Optional[Symbol]:
        if isinstance(symbol, Terminal):
            return next((x for x in self.terminals if x == symbol), None)
        else:
            return next((x for x in self.nonterminals if x == symbol), None)

    def add_terminal(self, terminal: Terminal) -> Terminal:
        existing = self._get_symbol(terminal)
        if existing:
            return existing
        self.terminals.append(terminal)
        return terminal

    def add_nonterminal(self, nonterminal: NonTerminal) -> NonTerminal:
        existing = self._get_symbol(nonterminal)
        if existing:
            return existing
        self.nonterminals.append(nonterminal)
        return nonterminal

    def add_symbol(self, symbol: Symbol) -> Symbol:
        if isinstance(symbol, Terminal):
            return self.add_terminal(symbol)
        elif isinstance(symbol, NonTerminal):
            return self.add_nonterminal(symbol)

        raise ValueError("Unknown symbol type")

    def __repr__(self) -> str:
        return "\n".join([str(x) for x in self.nonterminals])


class GrammarOps():
    def __init__(self, grammar: Grammar) -> None:
        self.grammar: Grammar = grammar
        self._empty_nonterminals: Set[NonTerminal] = GrammarOps.calculate_empty_nonterminals(self.grammar)
        self._first_set = GrammarOps.calculate_first_set(self.grammar, self._empty_nonterminals)
        # self._follow_set = GrammarOps.calculate_follow_set(self.grammar, self._first_set, self._empty_nonterminals)

    @staticmethod
    def calculate_empty_nonterminals(grammar: Grammar) -> Set[NonTerminal]:
        result: Set[NonTerminal] = set()
        last_size = -1
        while last_size != len(result):
            last_size = len(result)
            for nonterminal in grammar.nonterminals:
                if nonterminal in result:
                    continue

                for rule in nonterminal.rules:
                    if all([x.is_epsilon for x in rule.symbols]):
                        result.add(nonterminal)
                        break
                    if all([x in result for x in rule.symbols]):
                        result.add(nonterminal)
                        break
        return result

    @property
    def empty_nonterminals(self):
        return self._empty_nonterminals

    @staticmethod
    def calculate_first_set(grammar: Grammar, empty_nonterminals: Set[NonTerminal]) -> Dict[Symbol, Set[Symbol]]:
        result: Dict[Symbol, Set[Symbol]] = {x: set() for x in grammar.nonterminals}
        last_size = -1
        while last_size != sum([len(x) for x in result.values()]):
            last_size = sum([len(x) for x in result.values()])
            # for terminal in grammar.terminals:
            #     result[terminal].add(terminal)
            for nonterminal in grammar.nonterminals:
                for rule in nonterminal.rules:
                    for symbol in rule.symbols:
                        if isinstance(symbol, Terminal):
                            result[nonterminal].add(symbol)
                            break
                        result[nonterminal] = result[nonterminal].union(result[symbol])
                        if not symbol in empty_nonterminals:
                            break
        return result

    @property
    def first_set(self):
        return self._first_set

    @property
    def first_set_by_rule(self):
        return self._first_set_by_rule