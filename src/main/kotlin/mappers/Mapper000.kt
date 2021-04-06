package mappers

class Mapper000(
    prgBanks: Int,
    chrBanks: Int
) : Mapper(prgBanks, chrBanks) {
    //TODO check for ram
    override fun cpuRead(addr: Int): Int {
        return if (prgBanks > 1) {
            addr and 0x7FFF
        } else {
            addr and 0x3FFF
        }
    }

    override fun cpuWrite(addr: Int): Int {
        return if (prgBanks > 1) {
            addr and 0x7FFF
        } else {
            addr and 0x3FFF
        }
    }

    override fun ppuRead(addr: Int): Int {
        return  addr
    }

    override fun ppuWrite(addr: Int): Int {
        return addr
    }
}