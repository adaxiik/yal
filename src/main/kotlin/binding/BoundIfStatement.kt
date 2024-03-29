package binding

class BoundIfStatement(
    val condition: BoundExpression,
    val body: BoundStatement,
    val elseBody: BoundStatement?
) : BoundStatement()