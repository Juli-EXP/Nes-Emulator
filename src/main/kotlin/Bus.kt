import cpu.CPU

@ExperimentalUnsignedTypes
class Bus(
    cpu: CPU
) {

    //fake ram
    private val ram: UByteArray


    fun read(addr: UShort): UByte {
        return if (addr >= 0u && addr <= 0xFFFF.toUByte())
            ram[addr.toInt()]
        else
            0u
    }

    fun write(addr: UShort, data: UByte) {
        if (addr >= 0u && addr <= 0xFFFF.toUByte())
            ram[addr.toInt()] = data
    }

    init {
        //connect the cpu to the bus
        cpu.connectBus(this)

        ram = UByteArray(0x10000)
    }
}