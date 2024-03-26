
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer

class ErrorListener : BaseErrorListener() {
    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String?,
        e: RecognitionException?
    ) {
        recognizer as Parser
        val ruleStackStr = recognizer.ruleInvocationStack.reversed().joinToString(" ")
        println("rule stack: $ruleStackStr")
        println("$line:$charPositionInLine  $msg")
    }
}

