package cpu

@ExperimentalUnsignedTypes
data class Instruction(
    val instruction: () -> UByte,
    val addressingMode: () -> UByte,
    val cycles: UByte = 0u
){
    private fun toInstructionString(function: () -> UByte):String{
        return function
            .toString()
            .substringAfter("CPU.")
            .substring(0, 3)
            .toUpperCase()
    }

    override fun toString(): String {
        return "Instr.: ${toInstructionString(instruction)}, Addr.: ${toInstructionString(addressingMode)}, cycles: $cycles"
    }
}
