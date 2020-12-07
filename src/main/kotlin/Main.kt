import cpu.CPU
import kotlin.jvm.JvmStatic

@ExperimentalUnsignedTypes
object Main {
    private var bus: Bus? = null
    private var cpu: CPU? = null

    @JvmStatic
    fun main(args: Array<String>) {
        init()
    }

    private fun init() {
        cpu = CPU()
        bus = Bus(cpu!!)
    }
}