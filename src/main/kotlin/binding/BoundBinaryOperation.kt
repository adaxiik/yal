package binding

import Type

enum class BinaryOperationKind {
    Add,
    Sub,
    Mul,
    Div,
    Mod,
    And,
    Or,
    Lt,
    Gt,
    Eq,
    NotEq,
    Concat,
}

class InvalidBinaryDeduction(val left: Type, val right: Type, val op: BinaryOperationKind) : Exception()

class BoundBinaryOperation(
    left: BoundExpression,
    right: BoundExpression,
    kind: BinaryOperationKind
) : BoundExpression(deduceType(left.type, right.type, kind)) {
    companion object {

        private val Type.isInt get()    = this == Type.Int
        private val Type.isFloat get()  = this == Type.Float
        private val Type.isBool get()   = this == Type.Bool
        private val Type.isString get() = this == Type.String

        private fun swappable(left: Type, right: Type, firstType: Type, secondType: Type) = when {
            left == firstType && right == secondType -> true
            right == firstType && left == secondType -> true
            else -> false
        }

        private fun arithmeticOp(left: Type, right: Type) = when {
            left.isInt && right.isInt -> Type.Int
            left.isFloat && right.isFloat -> Type.Float
            swappable(left, right, Type.Int, Type.Float) -> Type.Float
            else -> null
        }

        private fun mustBeTarget(left: Type, right: Type, target: Type) = when {
            left == right && right == target -> target
            else -> null
        }

        private fun relationalOp(left:Type, right: Type) = when {
            swappable(left, right, Type.Int, Type.Float) -> Type.Bool
            left != right -> null
            left.isFloat -> Type.Bool
            left.isInt -> Type.Bool
            else -> null
        }

        private fun comparsionOp(left:Type, right: Type) = when {
            swappable(left, right, Type.Int, Type.Float) -> Type.Bool
            left != right -> null
            left.isFloat -> Type.Bool
            left.isInt -> Type.Bool
            left.isString -> Type.Bool
            else -> null
        }

        private fun booleanOp(left: Type, right: Type) = when {
            left.isBool and right.isBool -> Type.Bool
            else -> null
        }

        private fun deduceType(left: Type, right: Type, op: BinaryOperationKind) : Type = when (op) {
            BinaryOperationKind.Add -> arithmeticOp(left, right)
            BinaryOperationKind.Sub -> arithmeticOp(left, right)
            BinaryOperationKind.Mul -> arithmeticOp(left, right)
            BinaryOperationKind.Div -> arithmeticOp(left, right)
            BinaryOperationKind.Mod -> mustBeTarget(left, right, Type.Int)
            BinaryOperationKind.Concat -> mustBeTarget(left, right, Type.String)
            BinaryOperationKind.Lt -> relationalOp(left, right)
            BinaryOperationKind.Gt -> relationalOp(left, right)
            BinaryOperationKind.Eq -> comparsionOp(left, right)
            BinaryOperationKind.NotEq -> comparsionOp(left, right)
            BinaryOperationKind.And -> booleanOp(left, right)
            BinaryOperationKind.Or -> booleanOp(left, right)
        } ?: throw InvalidBinaryDeduction(left, right, op)
    }
}