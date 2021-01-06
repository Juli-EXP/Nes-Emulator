import cpu.CPUBus

class Ram(
    size: Int
) {
    //variables---------------------------------------------------------------------------------------------------------
    private lateinit var cpuBus: CPUBus
    private val data = IntArray(size)


    //Communication with the bus----------------------------------------------------------------------------------------

    //Connects the CPU to the bus
    fun connectBus(cpuBus: CPUBus) {
        this.cpuBus = cpuBus
    }

    //reads from the bus
    fun read(addr: Int): Int {
        return this.data[addr]
    }

    //writes to the bus
    fun write(addr: Int, data: Int) {
        this.data[addr] = data
    }


}