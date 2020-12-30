package cpu

import Bus
import org.junit.jupiter.api.*

class CPUTest {
    var bus: Bus? = null
    var cpu: CPU? = null

    fun init() {
        cpu = CPU()
        bus = Bus(cpu!!)
    }

}