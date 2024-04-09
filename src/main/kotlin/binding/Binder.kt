package binding

import Type
import YALBaseVisitor
import YALParser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode

// got slightly inspired here: https://github.com/fpeterek/tilscript/blob/master/interpreter/src/main/kotlin/astprocessing/AntlrVisitor.kt
// and here https://github.com/adaxiik/sea-code-control/tree/main/src/binding
class Binder(
): YALBaseVisitor<BoundNode>() {

    private val reservedKeyword = setOf(
        "while",
        "if",
        "else",
        "read",
        "write",
        "string",
        "int",
        "float",
        "bool",
        "true",
        "false"
    )

    private val variables = mutableMapOf<String, Type>()
    public val errors = mutableListOf<BindingError>()

    private val Token.position get() = SourcePosition(line, charPositionInLine)
    private val TerminalNode.position get() = symbol.position
    private val ParserRuleContext.position get() = start.position


    override fun visit(tree: ParseTree?): BoundNode = when (tree) {
        null -> throw Unreachable(SourcePosition(0, 0))
        !is YALParser.ProgramContext -> throw Unreachable(SourcePosition(0, 0))
        else -> visitProgram(tree)
    }

    override fun visitProgram(ctx: YALParser.ProgramContext) = BoundBlockStatement(
        ctx
            .statement()
            .mapNotNull{
                try {
                    visitStatement(it)
                }
                catch (e: BindingError){
                    errors.add(e)
                    null
                }
            }
    )

//    override fun visitDoStatement(ctx: YALParser.DoStatementContext): BoundDoStatement = BoundDoStatement(
//        visitExpression(ctx.expression()).mustBe(Type.Bool) ?: throw UnexpectedType(ctx.position, Type.Bool),
//        visitStatement(ctx.statement())
//    )
    override fun visitStatement(ctx: YALParser.StatementContext): BoundStatement = when {
        ctx.blockStatement()       != null -> visitBlockStatement(ctx.blockStatement())
        ctx.declarationStatement() != null -> visitDeclarationStatement(ctx.declarationStatement())
        ctx.expressionStatement()  != null -> visitExpressionStatement(ctx.expressionStatement())
        ctx.writeStatement()       != null -> visitWriteStatement(ctx.writeStatement())
        ctx.ifStatement()          != null -> visitIfStatement(ctx.ifStatement())
        ctx.whileStatement()       != null -> visitWhileStatement(ctx.whileStatement())
        ctx.readStatement()        != null -> visitReadStatement(ctx.readStatement())
        ctx.emptyStatement()       != null -> BoundEmptyStatement()
//        ctx.doStatement()          != null -> visitDoStatement(ctx.doStatement())
        else -> throw Unreachable(ctx.position)
    }

    private fun BoundExpression.mustBe(target: Type) = when (type) {
        target -> this
        else -> null
    }

    override fun visitIfStatement(ctx: YALParser.IfStatementContext) = when (ctx.statement().size) {
        1 -> BoundIfStatement(
            visitExpression(ctx.expression()).mustBe(Type.Bool) ?: throw UnexpectedType(ctx.position, Type.Bool),
            visitStatement(ctx.statement(0)),
            null
        )

        2 -> BoundIfStatement(
            visitExpression(ctx.expression()).mustBe(Type.Bool) ?: throw UnexpectedType(ctx.position, Type.Bool),
            visitStatement(ctx.statement(0)),
            visitStatement(ctx.statement(1)),
        )

        else -> throw Unreachable(ctx.position)
    }

    override fun visitWhileStatement(ctx: YALParser.WhileStatementContext) = BoundWhileStatement(
        visitExpression(ctx.expression()).mustBe(Type.Bool) ?: throw UnexpectedType(ctx.position, Type.Bool),
        visitStatement(ctx.statement())
    )

    override fun visitBlockStatement(ctx: YALParser.BlockStatementContext) = BoundBlockStatement(
        ctx.statement().mapNotNull(::visitStatement)
    )

    private fun TerminalNode.toType(): Type = when (text) {
        "float"  -> Type.Float
        "string" -> Type.String
        "bool"   -> Type.Bool
        "int"    -> Type.Int
        else     -> throw Unreachable(SourcePosition(0, 0))
    }

    override fun visitDeclarationStatement(ctx: YALParser.DeclarationStatementContext) = BoundDeclarationStatement(
        ctx.TypeSpecifier().toType(),
        ctx.Identificator()
            .map { it.text }
            .map {
                when (it) {
                    in reservedKeyword -> throw IdentifierIsKeyword(ctx.position, it)
                    in variables -> throw IdentifierAlreadyExists(ctx.position, it)
                    else -> it.let {
                        variables[it] = ctx.TypeSpecifier().toType()
                        it
                    }
                }
            }
    )

    override fun visitLiteralExpression(ctx: YALParser.LiteralExpressionContext) = when {
        ctx.BoolLiteral()   != null -> BoundLiteralExpression.BoolLiteral(ctx.BoolLiteral().text == "true")
        ctx.FloatLiteral()  != null -> BoundLiteralExpression.FloatLiteral(ctx.FloatLiteral().text.toFloat())
        ctx.IntLiteral()    != null -> BoundLiteralExpression.IntLiteral(ctx.IntLiteral().text.toInt())
        ctx.StringLiteral() != null -> BoundLiteralExpression.StringLiteral(ctx.StringLiteral().text.drop(1).dropLast(1))
        else -> throw Unreachable(ctx.position)
    }

    override fun visitBinaryAddSub(ctx: YALParser.BinaryAddSubContext) = BoundBinaryExpression(
        visitExpression(ctx.expression(0)),
        visitExpression(ctx.expression(1)),
        when (ctx.op.text) {
            "+" -> BinaryOperationKind.Add
            "-" -> BinaryOperationKind.Sub
            "." -> BinaryOperationKind.Concat
            else -> throw Unreachable(ctx.position)
        }
    )

    override fun visitBinaryMulDivMod(ctx: YALParser.BinaryMulDivModContext) = BoundBinaryExpression(
        visitExpression(ctx.expression(0)),
        visitExpression(ctx.expression(1)),
        when (ctx.op.text) {
            "*" -> BinaryOperationKind.Mul
            "/" -> BinaryOperationKind.Div
            "%" -> BinaryOperationKind.Mod
            else -> throw Unreachable(ctx.position)
        }
    )

    override fun visitLiteralExpressionInExpression(ctx: YALParser.LiteralExpressionInExpressionContext?)
        = throw Unreachable(ctx!!.position)

    override fun visitAssign(ctx: YALParser.AssignContext) = ctx.Identificator().text.let {
        val variableType = variables[it] ?: throw VariableDoesNotExist(ctx.position, it)
        BoundAssignmentExpression(
            variableType,
            it,
            visitExpression(ctx.expression()).let { expr ->
                when {
                    expr.type == variableType -> expr
                    expr.type == Type.Int && variableType == Type.Float -> expr
                    else -> throw CantAssign(ctx.position, it, variableType, expr.type)
                }
            }
        )
    }
    override fun visitParenthesized(ctx: YALParser.ParenthesizedContext) = visitExpression(ctx.expression())

    override fun visitBinaryAnd(ctx: YALParser.BinaryAndContext) = BoundBinaryExpression(
        visitExpression(ctx.expression(0)),
        visitExpression(ctx.expression(1)),
        BinaryOperationKind.And
    )

    override fun visitBinaryOr(ctx: YALParser.BinaryOrContext) = BoundBinaryExpression(
        visitExpression(ctx.expression(0)),
        visitExpression(ctx.expression(1)),
        BinaryOperationKind.Or
    )

    override fun visitBinaryLtGt(ctx: YALParser.BinaryLtGtContext) = BoundBinaryExpression(
        visitExpression(ctx.expression(0)),
        visitExpression(ctx.expression(1)),
        when (ctx.op.text) {
            "<" -> BinaryOperationKind.Lt
            ">" -> BinaryOperationKind.Gt
            else -> throw Unreachable(ctx.position)
        }
    )

    override fun visitBinaryEqNotEq(ctx: YALParser.BinaryEqNotEqContext) = BoundBinaryExpression(
        visitExpression(ctx.expression(0)),
        visitExpression(ctx.expression(1)),
        when (ctx.op.text) {
            "==" -> BinaryOperationKind.Eq
            "!=" -> BinaryOperationKind.NotEq
            else -> throw Unreachable(ctx.position)
        }
    )

    override fun visitUnaryNot(ctx: YALParser.UnaryNotContext) = BoundUnaryExpression(
        visitExpression(ctx.expression()),
        UnaryOperationKind.Not
    )

    override fun visitUnaryMinus(ctx: YALParser.UnaryMinusContext) = BoundUnaryExpression(
        visitExpression(ctx.expression()),
        UnaryOperationKind.Minus
    )
    private fun visitExpression(ctx: YALParser.ExpressionContext): BoundExpression = try {
        when (ctx) {
            is YALParser.BinaryAddSubContext                  -> visitBinaryAddSub(ctx)
            is YALParser.BinaryMulDivModContext               -> visitBinaryMulDivMod(ctx)
            is YALParser.LiteralExpressionInExpressionContext -> visitLiteralExpression(ctx.literalExpression())
            is YALParser.IdentifierExpressionContext          -> visitIdentifierExpression(ctx)
            is YALParser.UnaryMinusContext                    -> visitUnaryMinus(ctx)
            is YALParser.UnaryNotContext                      -> visitUnaryNot(ctx)
            is YALParser.BinaryAndContext                     -> visitBinaryAnd(ctx)
            is YALParser.BinaryOrContext                      -> visitBinaryOr(ctx)
            is YALParser.BinaryEqNotEqContext                 -> visitBinaryEqNotEq(ctx)
            is YALParser.BinaryLtGtContext                    -> visitBinaryLtGt(ctx)
            is YALParser.AssignContext                        -> visitAssign(ctx)
            is YALParser.ParenthesizedContext                 -> visitParenthesized(ctx)
            else                                              -> throw Unreachable(ctx.position)
        }
    } catch (deductionError: InvalidBinaryDeduction) {
        throw InvalidBinaryOperatorTypes(
            ctx.position,
            deductionError.left,
            deductionError.right,
            deductionError.op
        )
    } catch (deductionError: InvalidUnaryDeduction) {
        throw InvalidUnaryOperatorTypes(
            ctx.position,
            deductionError.exprType,
            deductionError.op
        )
    }

    override fun visitIdentifierExpression(ctx: YALParser.IdentifierExpressionContext) = when {
        ctx.Identificator() == null -> throw Unreachable(ctx.position)
        ctx.Identificator().text !in variables -> throw VariableDoesNotExist(ctx.position, ctx.Identificator().text)
        else -> BoundIdentifierExpression(
            variables[ctx.Identificator().text]!!,
            ctx.Identificator().text
        )
    }
    override fun visitExpressionStatement(ctx: YALParser.ExpressionStatementContext) = BoundExpressionStatement(ctx
        .expression()
        ?.let { visitExpression(it) }
        ?: throw Unreachable(ctx.position)
    )

    override fun visitWriteStatement(ctx: YALParser.WriteStatementContext) = BoundWriteStatement(
        ctx.expression().map(::visitExpression)
    )

    override fun visitReadStatement(ctx: YALParser.ReadStatementContext) = BoundReadStatement(
        ctx.Identificator()
            .map { it.text }
            .map {name ->
                variables[name]?.let {
                    type -> ReadToIdentifier(type, name)
                } ?: throw VariableDoesNotExist(ctx.position, name)
            }
    )

}
