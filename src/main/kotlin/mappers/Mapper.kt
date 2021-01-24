package mappers

abstract class Mapper(
    protected val prgBanks: Int,
    protected val chrBanks: Int
) {

    open fun usePrgRam(addr: Int): Boolean {
        return false
    }

    open fun useChrRam(addr: Int): Boolean {
        return false
    }

    open fun cpuRead(addr: Int): Int {
        return addr
    }

    open fun cpuWrite(addr: Int): Int {
        return addr
    }

    open fun ppuRead(addr: Int): Int {
        return addr
    }

    open fun ppuWrite(addr: Int): Int {
        return addr
    }
}