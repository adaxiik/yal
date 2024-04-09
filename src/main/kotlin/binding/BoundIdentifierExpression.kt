package binding

import Type

class BoundIdentifierExpression(type:Type, val name: String) : BoundExpression(type)