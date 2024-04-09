package binding

import Type

open class BoundLiteralExpression private constructor(type: Type) : BoundExpression(type) {
    class FloatLiteral (val value: Float)   : BoundLiteralExpression(Type.Float)
    class StringLiteral(val value: String)  : BoundLiteralExpression(Type.String)
    class BoolLiteral  (val value: Boolean) : BoundLiteralExpression(Type.Bool)
    class IntLiteral   (val value: Int)     : BoundLiteralExpression(Type.Int)
}