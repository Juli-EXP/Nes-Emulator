package mappers

class Mapper000(
    prgBanks: Int,
    chrBanks: Int
) : Mapper(prgBanks, chrBanks) {

    override fun cpuRead(address: Int): Int {
        return if (prgBanks > 1) {
            address and 0x7FFF
        } else {
            address and 0x3FFF
        }
    }

    override fun cpuWrite(address: Int): Int {
        return if (prgBanks > 1) {
            address and 0x7FFF
        } else {
            address and 0x3FFF
        }
    }

    override fun ppuRead(address: Int): Int {
        return address
    }
}