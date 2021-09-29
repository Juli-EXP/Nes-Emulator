package mappers

abstract class Mapper(
    protected val prgBanks: Int,
    protected val chrBanks: Int
) {
    //The methods return null if the operation is not permitted

    open fun cpuRead(address: Int): Int? {
        return null
    }

    open fun cpuWrite(address: Int): Int? {
        return null
    }

    open fun ppuRead(address: Int): Int? {
        return null
    }

    open fun ppuWrite(address: Int): Int? {
        return null
    }
}