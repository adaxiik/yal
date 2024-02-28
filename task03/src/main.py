#!/usr/bin/env python3

from reader import GrammarParser
from grammar import GrammarOps

g = GrammarParser.grammar_from_file("task03/G1.txt")
gops = GrammarOps(g)

# print(gops.empty_nonterminals)
print(gops.first_set)