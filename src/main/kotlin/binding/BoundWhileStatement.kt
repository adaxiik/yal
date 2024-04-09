package binding

class BoundWhileStatement(val condition: BoundExpression, val body: BoundStatement) : BoundStatement() {
}