package mappers

class Mapper000(
    private val prgBanks: Int,
    private val chrBanks: Int
) : Mapper() {

    override fun useCartridgeRam(addr: Int): Boolean {
        //True is for Family Basic only
        return false
    }

    override fun usePpuRam(addr: Int): Boolean {
        TODO()
    }

    override fun cpuRead(addr: Int): Int {
        return if (prgBanks > 1) {
            addr and 0x7FFF
        } else {
            addr and 0x3FFF
        }
    }

    override fun cpuWrite(addr: Int): Int {
        return cpuRead(addr)
    }

    override fun ppuRead(addr: Int): Int {
        TODO()
    }

    override fun ppuWrite(addr: Int): Int {
        TODO()
    }
}