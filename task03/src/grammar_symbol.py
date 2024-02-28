
from typing import List, Union
from abc import ABC, abstractmethod

class Epsilon():
    def __eq__(self, o: object) -> bool:
        return isinstance(o, Epsilon)

    def __repr__(self) -> str:
        return '{e}'

    def __hash__(self) -> int:
        return hash('e')

class Symbol():
    def __init__(self, name: Union[str, Epsilon]) -> None:
        self.name: Union[str, Epsilon] = name if name else Epsilon()

    @property
    def is_epsilon(self) -> bool:
        return isinstance(self.name, Epsilon)

    def __eq__(self, o: object) -> bool:
        return self.name == o.name

    def __repr__(self) -> str:
        return str(self.name)

    def __hash__(self) -> int:
        return hash(self.name)

class Terminal(Symbol):
    def __init__(self, name: str) -> None:
        super().__init__(name)

class NonTerminal(Symbol):

    def __init__(self, name: str, rules: List['Rule']) -> None:
        super().__init__(name)
        self.rules: List['Rule'] = rules

    def add_rule(self, rule: 'Rule') -> 'Rule':
        self.rules.append(rule)
        return rule

    def __repr__(self) -> str:
        return f'{self.name}'
