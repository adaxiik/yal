class TypeCheckerVisitor: YALBaseVisitor<Type?>() {
    override fun visitTypeSpecifier(ctx: YALParser.TypeSpecifierContext?): Type? = when (ctx!!.text) {
        "float"  -> Type.Float
        "string" -> Type.String
        "bool"   -> Type.Bool
        "int"    -> Type.Int
        else -> null
    }

    override fun visitProgram(ctx: YALParser.ProgramContext?): Type? {
        ctx!!.command().forEach { visit(it) }
        return null
    }
}