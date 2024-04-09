package virtualMachine

import Type
import instructions.*
import java.util.*

class VirtualMachine(private val instructions: List<Instruction>) {
    class Unreachable() : RuntimeException()

    private val jumpTable = instructions
        .mapIndexed { idx, instruction ->
            Pair(idx, instruction)
        }
        .filter { it.second is LabelInstruction }
        .associate { (idx, instruction) ->
            instruction as LabelInstruction
            instruction.label to idx
        }

    private val variables = mutableMapOf<String, Any>()

    private val Any?.unit get() = Unit

    private val valueStack = Stack<Any>()
    private var instructionPointer = 0

    private fun interpret(ins: AddInstruction) {
        val left = valueStack.pop()
        val right = valueStack.pop()
        when (left) {
            is Int -> valueStack.push(left + right as Int)
            is Float -> valueStack.push(left + right as Float)
            else -> Unreachable()
        }
    }

    private fun interpret(ins: SubInstruction) {
        val left = valueStack.pop()
        val right = valueStack.pop()
        when (left) {
            is Int -> valueStack.push(left - right as Int)
            is Float -> valueStack.push(left - right as Float)
            else -> Unreachable()
        }
    }

    private fun interpret(ins: MulInstruction) {
        val left = valueStack.pop()
        val right = valueStack.pop()
        when (left) {
            is Int -> valueStack.push(left * right as Int)
            is Float -> valueStack.push(left * right as Float)
            else -> Unreachable()
        }
    }

    private fun interpret(ins: DivInstruction) {
        val left = valueStack.pop()
        val right = valueStack.pop()
        when (left) {
            is Int -> valueStack.push(left / right as Int)
            is Float -> valueStack.push(left / right as Float)
            else -> Unreachable()
        }
    }

    private fun interpret(ins: ModInstruction)  {
        val left = valueStack.pop() as Int
        val right = valueStack.pop() as Int
        valueStack.push(left % right)
    }

    private fun interpret(ins: ConcatInstruction)  {
        val left = valueStack.pop() as String
        val right = valueStack.pop() as String
        valueStack.push(left + right)
    }

    private fun interpret(ins: AndInstruction) {
        val left = valueStack.pop() as Boolean
        val right = valueStack.pop() as Boolean
        valueStack.push(left && right)
    }

    private fun interpret(ins: OrInstruction) {
        val left = valueStack.pop() as Boolean
        val right = valueStack.pop() as Boolean
        valueStack.push(left || right)
    }


    private fun interpret(ins: GtInstruction) {
        val left = valueStack.pop()
        val right = valueStack.pop()
        when (left) {
            is Int -> valueStack.push(left > right as Int)
            is Float -> valueStack.push(left > right as Float)
            else -> Unreachable()
        }
    }

    private fun interpret(ins: LtInstruction) {
        val left = valueStack.pop()
        val right = valueStack.pop()
        when (left) {
            is Int -> valueStack.push(left < right as Int)
            is Float -> valueStack.push(left < right as Float)
            else -> Unreachable()
        }
    }

    private fun interpret(ins: EqInstruction) {
        val left = valueStack.pop()
        val right = valueStack.pop()
        when (left) {
            is Int -> valueStack.push(left == right as Int)
            is Float -> valueStack.push(left == right as Float)
            is String -> valueStack.push(left == right as String)
            else -> Unreachable()
        }
    }

    private fun interpret(ins: UnaryMinusInstruction) : Unit = valueStack.pop().let {
        when (it) {
            is Int   -> valueStack.push(-it)
            is Float -> valueStack.push(-it)
            else     -> throw Unreachable()
        }
    }

    private fun interpret(ins: NotInstruction) : Unit = valueStack.push(!(valueStack.pop() as Boolean)).unit

    private fun interpret(ins: IntToFloatInstruction) : Unit = valueStack.push((valueStack.pop() as Int).toFloat()).unit

    private fun interpret(ins: PopInstruction) : Unit = valueStack.pop().unit

    private fun interpret(ins: LoadInstruction) = valueStack.push(variables[ins.name]!!).unit

    private fun interpret(ins: SaveInstruction) { variables[ins.name] = valueStack.peek()}

    private fun interpret(ins: LabelInstruction) : Unit = Unit

    private fun interpret(ins: JmpInstruction) { instructionPointer = jumpTable[ins.label]!! }

    private fun interpret(ins: FalseJmpInstruction) : Unit = when (valueStack.pop()) {
        false -> instructionPointer = jumpTable[ins.label]!!
        else ->  Unit
    }

    private fun interpret(ins: PushInstruction) : Unit = valueStack.push(ins.value).unit

    private fun interpret(ins: PrintInstruction) : Unit = (1..ins.n)
        .map { valueStack.pop() }
        .joinToString(" ")
        .let(::println)

    private fun interpret(ins: ReadInstruction) = try {
        val line = readln()
        when (ins.type) {
            Type.Bool   -> valueStack.push(line.toBoolean())
            Type.String -> valueStack.push(line)
            Type.Float  -> valueStack.push(line.toFloat())
            Type.Int    -> valueStack.push(line.toInt())
        }.unit
    }
    catch (e: Exception) {
        println("ERROR $e")
    }

    private fun interpret(ins : Instruction) : Unit = when (ins) {
        is AddInstruction        -> interpret(ins)
        is SubInstruction        -> interpret(ins)
        is MulInstruction        -> interpret(ins)
        is DivInstruction        -> interpret(ins)
        is ModInstruction        -> interpret(ins)
        is UnaryMinusInstruction -> interpret(ins)
        is ConcatInstruction     -> interpret(ins)
        is AndInstruction        -> interpret(ins)
        is OrInstruction         -> interpret(ins)
        is GtInstruction         -> interpret(ins)
        is LtInstruction         -> interpret(ins)
        is EqInstruction         -> interpret(ins)
        is NotInstruction        -> interpret(ins)
        is IntToFloatInstruction -> interpret(ins)
        is PushInstruction       -> interpret(ins)
        is PopInstruction        -> interpret(ins)
        is LoadInstruction       -> interpret(ins)
        is SaveInstruction       -> interpret(ins)
        is LabelInstruction      -> interpret(ins)
        is JmpInstruction        -> interpret(ins)
        is FalseJmpInstruction   -> interpret(ins)
        is PrintInstruction      -> interpret(ins)
        is ReadInstruction       -> interpret(ins)
        else -> throw Unreachable()
    }

    fun interpret() {
        while (instructionPointer < instructions.size) {
            interpret(instructions[instructionPointer])
            instructionPointer++;
        }
    }
}