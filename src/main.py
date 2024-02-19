#!/usr/bin/env python3
from expression_lexer import tokenize
from expression_parser import parse

tokens = tokenize("(1+2)*3")
print(tokens)

print('========')

expression = parse(tokens)
print(expression)

# inputs = int(input())
# for _ in range(inputs):
    # print(evaluate(input().strip().replace(' ', '')))
