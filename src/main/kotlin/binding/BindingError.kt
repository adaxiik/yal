package binding
import Type

abstract class BindingError : Exception()

class Unreachable() : BindingError()
class UnexpectedType(val expectedType: Type) : BindingError()
