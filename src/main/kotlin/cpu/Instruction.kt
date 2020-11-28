package cpu

@ExperimentalUnsignedTypes
data class Instruction(
        val name: String,
        val opcode: () -> UByte,
        val addressingMode: () -> UByte,
        val cycles: UByte
)

