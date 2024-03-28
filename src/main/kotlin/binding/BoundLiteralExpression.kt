package binding

import Type

open class BoundLiteralExpression private constructor(type: Type) : BoundExpression(type) {
    class FloatLiteral (value: Float)   : BoundLiteralExpression(Type.Float)
    class StringLiteral(value: String)  : BoundLiteralExpression(Type.String)
    class BoolLiteral  (value: Boolean) : BoundLiteralExpression(Type.Bool)
    class IntLiteral   (value: Int)     : BoundLiteralExpression(Type.Int)
}