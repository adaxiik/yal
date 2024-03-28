package binding

import Type
import YALBaseVisitor
import YALParser
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode

class Binder: YALBaseVisitor<BoundNode>() {
    override fun visit(tree: ParseTree?): BoundNode = when (tree) {
        null -> throw Unreachable()
        !is YALParser.ProgramContext -> throw Unreachable()
        else -> visitProgram(tree)
    }

    override fun visitProgram(ctx: YALParser.ProgramContext) = BoundBlockStatement(
        ctx.statement().mapNotNull(::visitStatement)
    )

    override fun visitStatement(ctx: YALParser.StatementContext): BoundStatement = when {
        ctx.blockStatement()       != null -> visitBlockStatement(ctx.blockStatement())
        ctx.declarationStatement() != null -> visitDeclarationStatement(ctx.declarationStatement())
        ctx.expressionStatement()  != null -> visitExpressionStatement(ctx.expressionStatement())
        ctx.writeStatement()       != null -> visitWriteStatement(ctx.writeStatement())
        ctx.ifStatement()          != null -> visitIfStatement(ctx.ifStatement())
        ctx.whileStatement()       != null -> visitWhileStatement(ctx.whileStatement())
        ctx.emptyStatement()       != null -> BoundEmptyStatement()
        else -> throw Unreachable()
    }

    private fun BoundExpression.mustBe(target: Type) = when (type) {
        target -> this
        else -> null
    }

    override fun visitIfStatement(ctx: YALParser.IfStatementContext) : BoundStatement
    {
        return when (ctx.statement().size) {
            1 -> BoundIfStatement(
                visitExpression(ctx.expression()).mustBe(Type.Bool) ?: throw UnexpectedType(Type.Bool),
                visitStatement(ctx.statement(0)),
                null
            )

            2 -> BoundIfStatement(
                visitExpression(ctx.expression()).mustBe(Type.Bool) ?: throw UnexpectedType(Type.Bool),
                visitStatement(ctx.statement(0)),
                visitStatement(ctx.statement(1)),
            )

            else -> throw Unreachable()
        }
    }
    override fun visitWhileStatement(ctx: YALParser.WhileStatementContext) : BoundStatement
    {
        return BoundWhileStatement(
            visitExpression(ctx.expression()).mustBe(Type.Bool) ?: throw UnexpectedType(Type.Bool),
            visitStatement(ctx.statement())
        )
    }

    override fun visitBlockStatement(ctx: YALParser.BlockStatementContext) = BoundBlockStatement(
        ctx.statement().mapNotNull(::visitStatement)
    )

    private fun TerminalNode.toType(): Type = when (text) {
        "float" -> Type.Float
        "string" -> Type.String
        "bool" -> Type.Bool
        "int" -> Type.Int
        else -> throw Unreachable()
    }

    override fun visitDeclarationStatement(ctx: YALParser.DeclarationStatementContext) = BoundDeclarationStatement(
        ctx.TypeSpecifier().toType(),
        ctx.Identificator().map { it.text }
    )

    override fun visitLiteralExpression(ctx: YALParser.LiteralExpressionContext) = when {
        ctx.BoolLiteral() != null -> BoundLiteralExpression.BoolLiteral(ctx.BoolLiteral().text == "true")
        ctx.FloatLiteral() != null -> BoundLiteralExpression.FloatLiteral(ctx.FloatLiteral().text.toFloat())
        ctx.IntLiteral() != null -> BoundLiteralExpression.IntLiteral(ctx.IntLiteral().text.toInt())
        ctx.StringLiteral() != null -> BoundLiteralExpression.StringLiteral(ctx.StringLiteral().text)
        else -> throw Unreachable()
    }

    override fun visitExpression(ctx: YALParser.ExpressionContext) = when {
        ctx.literalExpression() != null -> visitLiteralExpression(ctx.literalExpression())
        else -> throw Unreachable()
    }

    override fun visitExpressionStatement(ctx: YALParser.ExpressionStatementContext) = BoundExpressionStatement(ctx
        .expression()
        ?.let { visitExpression(it) }
        ?: throw Unreachable()
    )

    override fun visitWriteStatement(ctx: YALParser.WriteStatementContext) = BoundWriteStatement(
        ctx.expression().map(::visitExpression)
    )

}