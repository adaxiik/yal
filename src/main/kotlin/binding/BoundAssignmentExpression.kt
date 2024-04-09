package binding
import Type

class BoundAssignmentExpression(type: Type, val name: String, val expression: BoundExpression) : BoundExpression(type)