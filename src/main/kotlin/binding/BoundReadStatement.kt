package binding

import Type

data class ReadToIdentifier(val type: Type, val name: String)
class BoundReadStatement(val identifiers: List<ReadToIdentifier>) : BoundStatement()