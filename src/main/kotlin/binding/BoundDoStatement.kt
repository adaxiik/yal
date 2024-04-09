package binding


class BoundDoStatement(val condition: BoundExpression, body: BoundStatement) : BoundStatement() {
}