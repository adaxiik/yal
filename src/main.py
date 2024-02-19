#!/usr/bin/env python3
from expression_evaluator import evaluate
from expression_lexer import tokenize
from expression_parser import parse


inputs = int(input())
for _ in range(inputs):
    cleaned = input().strip().replace(' ', '')
    tokens = tokenize(cleaned)
    if not tokens:
        print("Tokenization error")
        continue

    expression = parse(tokens)
    if not expression:
        print("Parsing error")
        continue

    result = evaluate(expression)
    if result is None:
        print("Evaluation error")
        continue

    print(result)
