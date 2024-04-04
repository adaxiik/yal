package binding
import Type

abstract class BindingError(val position: SourcePosition) : Exception() {
    override fun toString(): String {
        return "${super.toString()} $position"
    }
}

class Unreachable(position: SourcePosition) : BindingError(position) {
    override fun toString(): String = "Unreachable($position.toString())"
}
class UnexpectedType(position: SourcePosition, val expectedType: Type) : BindingError(position)
class IdentifierIsKeyword(position: SourcePosition, val identifier: String) : BindingError(position)
class IdentifierAlreadyExists(position: SourcePosition, val identifier: String) : BindingError(position)
class VariableDoesNotExist(position: SourcePosition, val identifier: String) : BindingError(position)

class InvalidBinaryOperatorTypes(
    position: SourcePosition,
    lefType: Type,
    right: Type,
    operator: BinaryOperationKind
) : BindingError(position)

class InvalidUnaryOperatorTypes(
    position: SourcePosition,
    exprType: Type,
    operator: UnaryOperationKind
) : BindingError(position)


class CantAssign(
    position: SourcePosition,
    name: String,
    leftType: Type,
    right: Type
) : BindingError(position)