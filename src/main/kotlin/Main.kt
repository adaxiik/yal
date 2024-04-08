
import binding.Binder
import binding.BoundBlockStatement
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    val inputStream = try {
        CharStreams.fromFileName("yal-sources/PLC_errors.in")
    } catch (e: Exception) {
        println(e)
        exitProcess(1)
    }
    val lexer = YALLexer(inputStream)
    val tokens = CommonTokenStream(lexer)
    val parser = YALParser(tokens)

    parser.addErrorListener(ErrorListener())
    val tree = parser.program()

    if (parser.numberOfSyntaxErrors != 0) {
        exitProcess(1)
    }

    val binder = Binder()
    val program = binder.visit(tree) as BoundBlockStatement
    when (binder.errors.size) {
        0 -> program.statements.forEach(::println)
        else -> binder.errors.forEach(::println)
    }

}