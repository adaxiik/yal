package lowering

import Type
import binding.*
import instructions.*

class Lowerer {

    class Unreachable() : RuntimeException()

    private var currentLabel = 0

    private fun createLabel() = currentLabel++

    // list concatenation isn't probably the best idea.. some instruction buffer would be better I guess
    // but I dont really care for these smoll programs

    private fun List<Instruction>.add(instructions: List<Instruction>) = this + instructions
    private fun List<Instruction>.add(instruction: Instruction) = this + instruction

    private fun lower(node: BoundLiteralExpression) = listOf(
        when (node) {
            is BoundLiteralExpression.IntLiteral    -> PushInstruction(Type.Int, node.value)
            is BoundLiteralExpression.FloatLiteral  -> PushInstruction(Type.Float, node.value)
            is BoundLiteralExpression.BoolLiteral   -> PushInstruction(Type.Bool, node.value)
            is BoundLiteralExpression.StringLiteral -> PushInstruction(Type.String, node.value)
            else -> throw Unreachable()
        }
    )

    private fun lower(node: BoundUnaryExpression) = lower(node.expression) + when (node.operationKind) {
        UnaryOperationKind.Not   -> NotInstruction()
        UnaryOperationKind.Minus -> UnaryMinusInstruction()
    }

    private fun lower(node: BoundAssignmentExpression) = lower(node.expression)
        .add(if (node.type == Type.Float && node.expression.type != Type.Float) listOf(IntToFloatInstruction()) else emptyList<Instruction>())
        .add(SaveInstruction(node.name))
        .add(LoadInstruction(node.name))

    private fun lower(node: BoundExpression) : List<Instruction> = when (node) {
        is BoundLiteralExpression    -> lower(node)
        is BoundUnaryExpression      -> lower(node)
        is BoundBinaryExpression     -> lower(node)
        is BoundIdentifierExpression -> lower(node)
        is BoundAssignmentExpression -> lower(node)
        else -> throw Unreachable()
    }

    private fun lower(node: BoundIdentifierExpression) = listOf(LoadInstruction(node.name))

    private fun shouldCastToFloat(targetType: Type, left: Type, right: Type, lookingOnRight: Boolean) = when (targetType) {
        Type.Float -> left != Type.Float && !lookingOnRight || right != Type.Float && lookingOnRight
        Type.Bool -> when (left) {
            Type.Float -> right == Type.Int && lookingOnRight
            Type.Int -> right == Type.Float && !lookingOnRight
            else -> false
        }
        else -> false
    }
    private fun lower(node: BoundBinaryExpression) = lower(node.left)
        .add(
            if (shouldCastToFloat(node.type, node.left.type, node.right.type, false))
                listOf(IntToFloatInstruction())
            else emptyList()
        )
        .add(lower(node.right))
        .add(
            if (shouldCastToFloat(node.type, node.left.type, node.right.type, true))
                listOf(IntToFloatInstruction())
            else
                emptyList()
        )
        .add(
            when (node.kind) {
                BinaryOperationKind.Add    -> listOf(AddInstruction())
                BinaryOperationKind.Sub    -> listOf(SubInstruction())
                BinaryOperationKind.Mul    -> listOf(MulInstruction())
                BinaryOperationKind.Div    -> listOf(DivInstruction())
                BinaryOperationKind.Mod    -> listOf(ModInstruction())
                BinaryOperationKind.And    -> listOf(AndInstruction())
                BinaryOperationKind.Or     -> listOf(OrInstruction())
                BinaryOperationKind.Lt     -> listOf(LtInstruction())
                BinaryOperationKind.Gt     -> listOf(GtInstruction())
                BinaryOperationKind.Eq     -> listOf(EqInstruction())
                BinaryOperationKind.NotEq  -> listOf(EqInstruction(), NotInstruction())
                BinaryOperationKind.Concat -> listOf(ConcatInstruction())
            }
        )

    private fun lower(node: BoundWriteStatement) = node
        .expressions
        .asReversed()
        .flatMap { lower(it) }
        .add(PrintInstruction(node.expressions.size))

    private fun lower(node: BoundDeclarationStatement) = node.declarationNames.flatMap {
        listOf(
            when (node.type) {
                Type.Int -> PushInstruction(Type.Int, 0)
                Type.Float -> PushInstruction(Type.Float, 0.0f)
                Type.String -> PushInstruction(Type.String, String())
                Type.Bool -> PushInstruction(Type.Bool, false)
            },
            SaveInstruction(it)
        )
    }

    private fun lower(node: BoundReadStatement) = node
        .identifiers
        .flatMap {
            listOf(
                ReadInstruction(it.type),
                SaveInstruction(it.name)
            )
        }

    private fun lowerSingleIf(node: BoundIfStatement) : List<Instruction> = lower(node.condition) +
        let {
            val endLabel = createLabel()
            listOf(FalseJmpInstruction(endLabel)) + lower(node.body) + LabelInstruction(endLabel)
        }

    private fun lowerElseIf(node: BoundIfStatement) : List<Instruction> = lower(node.condition) +
            let {
                val elseLabel = createLabel()
                val endLabel = createLabel()
                listOf(FalseJmpInstruction(elseLabel))
                    .add(lower(node.body))
                    .add(JmpInstruction(endLabel))
                    .add(LabelInstruction(elseLabel))
                    .add(lower(node.elseBody!!))
                    .add(LabelInstruction(endLabel))
            }

    private fun lower(node: BoundIfStatement) = when (node.elseBody) {
        null -> lowerSingleIf(node)
        else -> lowerElseIf(node)
    }

    private fun lower(node: BoundWhileStatement) : List<Instruction> {
        val continueLabel = createLabel()
        val breakLabel = createLabel()

        return listOf(LabelInstruction(continueLabel))
            .add(lower(node.condition))
            .add(FalseJmpInstruction(breakLabel))
            .add(lower(node.body))
            .add(JmpInstruction(continueLabel))
            .add(LabelInstruction(breakLabel))
    }


    private fun lower(node: BoundStatement) = when (node) {
        is BoundWriteStatement       -> lower(node)
        is BoundExpressionStatement  -> lower(node.expression) + PopInstruction()
        is BoundBlockStatement       -> lower(node)
        is BoundEmptyStatement       -> listOf()
        is BoundDeclarationStatement -> lower(node)
        is BoundReadStatement        -> lower(node)
        is BoundIfStatement          -> lower(node)
        is BoundWhileStatement       -> lower(node)
        else -> throw Unreachable()
    }

    fun lower(node: BoundBlockStatement) : List<Instruction> = node
        .statements
        .flatMap { lower(it) }

}