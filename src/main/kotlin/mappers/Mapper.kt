package mappers

abstract class Mapper(
    protected val prgBanks: Int,
    protected val chrBanks: Int
) {
    //All methods return null if the operation is not permited

    open fun cpuRead(addr: Int): Int? {
        return null
    }

    open fun cpuWrite(addr: Int): Int? {
        return null
    }

    open fun ppuRead(addr: Int): Int? {
        return null
    }

    open fun ppuWrite(addr: Int): Int? {
        return null
    }
}