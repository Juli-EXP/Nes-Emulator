package mappers

abstract class Mapper {

    open fun useCartridgeRam(addr: Int): Boolean {
        return false
    }

    open fun usePpuRam(addr: Int): Boolean {
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