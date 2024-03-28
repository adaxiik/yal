package binding

class BoundExpressionStatement(val expression: BoundExpression) : BoundStatement() {

    override fun toString(): String {
        return expression.toString()
    }
}