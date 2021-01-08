import cpu.CpuBus

class Ram(
    size: Int = 0x800   //2KB
) {
    //variables---------------------------------------------------------------------------------------------------------
    private val data = IntArray(size)

    //reads from RAM
    fun read(addr: Int): Int {
        return this.data[addr]
    }

    //writes to RAM
    fun write(addr: Int, data: Int) {
        this.data[addr] = data
    }


}