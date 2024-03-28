package binding

import Type

class BoundDeclarationStatement(val type: Type, val declarationNames: List<String>) : BoundStatement() {

}