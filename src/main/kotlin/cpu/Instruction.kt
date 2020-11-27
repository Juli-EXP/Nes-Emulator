package cpu

data class Instruction(
        val name: String,
        val opcode: () -> UByte,
        val addressingMode: () -> UByte,
        val cycles: UByte
)

fun main(){
        print("Servus")
        Thread.sleep(5000)
}