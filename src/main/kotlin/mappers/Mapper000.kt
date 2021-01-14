package mappers

class Mapper000(
    private val prgBanks: Int,
    private val chrBanks: Int
) : Mapper() {

    override fun cpuRead(addr: Int): Int {
        return if (prgBanks == 0) {
            addr and 0x3FFF
        } else {
            addr and 0x7FFF
        }
    }

    override fun cpuWrite(addr: Int): Int {
        return cpuRead(addr)
    }

    override fun ppuRead(addr: Int): Int {
        usePpuRam = false
        return addr
    }

    override fun ppuWrite(addr: Int): Int {
        usePpuRam = chrBanks == 0
        return addr
    }
}