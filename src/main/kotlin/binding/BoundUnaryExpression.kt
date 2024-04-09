package binding

import Type

enum class UnaryOperationKind {
    Not,
    Minus
}
class InvalidUnaryDeduction(val exprType: Type, val op: UnaryOperationKind) : Exception()

class BoundUnaryExpression(
    val expression: BoundExpression,
    val operationKind: UnaryOperationKind
) : BoundExpression(deduceType(expression.type, operationKind)) {

    companion object {
        private fun unaryMinus(exprType: Type) = when (exprType) {
            Type.Float -> Type.Float
            Type.Int-> Type.Int
            else -> null
        }

        private fun unaryNot(exprType: Type) = when (exprType) {
            Type.Bool -> Type.Bool
            else -> null
        }


        private fun deduceType(exprType: Type, op: UnaryOperationKind) : Type = when (op) {
            UnaryOperationKind.Minus -> unaryMinus(exprType)
            UnaryOperationKind.Not -> unaryNot(exprType)
        } ?: throw InvalidUnaryDeduction(exprType, op)

    }
}