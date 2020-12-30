import cpu.CPU

class Bus(
    cpu: CPU
) {

    //fake ram
    val ram: IntArray


    fun read(addr: Int): Int {
        return if (addr in 0..0xFFFF)
            ram[addr] and 0xFF
        else
            0
    }

    fun write(addr: Int, data: Int) {
        if (addr in 0..0xFFFF)
            ram[addr] = data and 0xFF
    }

    init {
        //connect the cpu to the bus
        cpu.connectBus(this)

        ram = IntArray(0x10000)
    }
}