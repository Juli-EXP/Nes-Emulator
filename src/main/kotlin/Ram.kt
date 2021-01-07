import cpu.CpuBus

class Ram(
    size: Int = 0x800   //2KB
) {
    //variables---------------------------------------------------------------------------------------------------------
    private lateinit var cpuBus: CpuBus
    private val data = IntArray(size)


    //Connects the RAM to the bus
    fun connectBus(cpuBus: CpuBus) {
        this.cpuBus = cpuBus
    }

    //reads from RAM
    fun read(addr: Int): Int {
        return this.data[addr]
    }

    //writes to RAM
    fun write(addr: Int, data: Int) {
        this.data[addr] = data
    }


}