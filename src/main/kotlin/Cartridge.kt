import ppu.Ppu

class Cartridge {


    //Communication with the bus----------------------------------------------------------------------------------------

    //Reads from the CPU bus
    fun cpuRead(addr: Int): Int {
        return 0
    }

    //Writes to the CPU bus
    fun cpuWrite(addr: Int, data: Int) {
    }

    //Reads from the PPU bus
    fun ppuRead(addr: Int): Int {
        return 0
    }

    //Writes to the PPU bus
    fun ppuWrite(addr: Int, data: Int) {
    }
}