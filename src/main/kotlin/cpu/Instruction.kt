package cpu

class Instruction(
    val instruction: () -> Int,
    val addressingMode: () -> Int,
    val cycles: Int = 0
){
    private fun toInstructionString(function: () -> Int):String{
        return function
            .toString()
            .substringAfter("Cpu.")
            .substring(0, 3)
            .toUpperCase()
    }

    override fun toString(): String {
        return "INSTR: ${toInstructionString(instruction)}, MODE: ${toInstructionString(addressingMode)}, CYCLES: $cycles"
    }
}
