
import binding.Binder
import binding.BoundBlockStatement
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    val inputStream = CharStreams.fromFileName("yal-sources/test1.yal")
    val lexer = YALLexer(inputStream)
    val tokens = CommonTokenStream(lexer)
    val parser = YALParser(tokens)

    parser.addErrorListener(ErrorListener())
    val tree = parser.program()

    if (parser.numberOfSyntaxErrors != 0) {
        exitProcess(1)
    }

    val program = Binder().visit(tree) as BoundBlockStatement
    program.statements.forEach(::println)
}