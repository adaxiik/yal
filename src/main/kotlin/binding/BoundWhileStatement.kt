package binding

class BoundWhileStatement(val condition: BoundExpression, body: BoundStatement) : BoundStatement() {
}