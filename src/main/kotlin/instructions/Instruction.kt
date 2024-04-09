package instructions

import Type

interface Instruction {}

class AddInstruction() : Instruction {
    override fun toString() = "add"
}

class SubInstruction() : Instruction {
    override fun toString() = "sub"
}

class MulInstruction() : Instruction {
    override fun toString() = "mul"
}

class DivInstruction() : Instruction {
    override fun toString() = "div"
}

class ModInstruction() : Instruction {
    override fun toString() = "mod"
}

class UnaryMinusInstruction() : Instruction {
    override fun toString() = "unaryMinus"
}

class ConcatInstruction() : Instruction {
    override fun toString() = "concat"
}

class AndInstruction() : Instruction {
    override fun toString() = "and"
}

class OrInstruction() : Instruction {
    override fun toString() = "or"
}

class GtInstruction() : Instruction {
    override fun toString() = "gt"
}

class LtInstruction() : Instruction {
    override fun toString() = "lt"
}

class EqInstruction() : Instruction {
    override fun toString() = "eq"
}

class NotInstruction() : Instruction {
    override fun toString() = "not"
}

class IntToFloatInstruction() : Instruction {
    override fun toString() = "intToFloat"
}

class PushInstruction(val type: Type, val value: Any) : Instruction {
    override fun toString() = "push $type $value"
}

class PopInstruction() : Instruction {
    override fun toString() = "pop"
}

class LoadInstruction(val name: String) : Instruction {
    override fun toString() = "load $name"
}

class SaveInstruction(val name: String) : Instruction {
    override fun toString() = "save $name"
}

class LabelInstruction(val label: Int) : Instruction {
    override fun toString() = "label $label"
}

class JmpInstruction(val label: Int) : Instruction {
    override fun toString() = "jmp $label"
}

class FalseJmpInstruction(val label: Int) : Instruction {
    override fun toString() = "fjmp $label"
}

class PrintInstruction(val n: Int) : Instruction {
    override fun toString() = "print $n"
}

class ReadInstruction(val type: Type) : Instruction {
    override fun toString() = "read $type"
}