package cpu

data class Instruction(
    val instruction: () -> Int,
    val addressingMode: () -> Int,
    val cycles: Int = 0
){
    private fun toInstructionString(function: () -> Int):String{
        return function
            .toString()
            .substringAfter("CPU.")
            .substring(0, 3)
            .toUpperCase()
    }

    override fun toString(): String {
        return "instr: ${toInstructionString(instruction)}, mode: ${toInstructionString(addressingMode)}, cycles: $cycles"
    }
}
