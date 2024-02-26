#!/usr/bin/env python3
from expression_lexer import tokenize

# 1+1mod 4;
inputs = int(input())
for _ in range(inputs):
    cleaned = input() + '\n'
    tokens = tokenize(cleaned)
    if not tokens:
        print("Tokenization error")
        continue

    # print(tokens)

    for token in tokens:
        print(f'{token.token_kind}: {token.value}')
