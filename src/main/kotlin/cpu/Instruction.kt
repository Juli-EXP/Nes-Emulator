package cpu

import java.util.*

class Instruction(
    val instruction: () -> Int,
    val addressingMode: () -> Int,
    val cycles: Int = 0
) {
    private val instructionString: String = toInstructionString(instruction)
    private val addressingString: String = toInstructionString(addressingMode)

    private fun toInstructionString(function: () -> Int): String {
        return function
            .toString()
            .substringAfter("Cpu.")
            .substring(0, 3)
            .uppercase(Locale.getDefault())
    }

    override fun toString(): String {
        return "INSTR: $instructionString, MODE: $addressingString, CYCLES: $cycles"
    }
}
