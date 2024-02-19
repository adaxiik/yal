

from typing import Optional
from expression_parser import Expression, BinaryExpression, NumberExpression, OpKind


def evaluate(expression: Expression) -> Optional[int]:
    if isinstance(expression, NumberExpression):
        return expression.value
    elif isinstance(expression, BinaryExpression):
        left = evaluate(expression.left)
        right = evaluate(expression.right)
        if expression.op == OpKind.ADD:
            return left + right
        elif expression.op == OpKind.SUB:
            return left - right
        elif expression.op == OpKind.MUL:
            return left * right
        elif expression.op == OpKind.DIV:
            return left / right

    return None