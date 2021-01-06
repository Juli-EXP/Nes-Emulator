package cpu

import Ram
import ppu.PPU

class CPUBus(
    cpu: CPU,
    ppu: PPU,
    val ram: Ram
) {


    fun read(addr: Int): Int {
        return if (addr in 0..0xFFFF)
            ram.read(addr) and 0xFF
        else
            0
    }

    fun write(addr: Int, data: Int) {
        if (addr in 0..0xFFFF)
            ram.write(addr, data and 0xFF)
    }

    init {
        //connect the cpu to the bus
        cpu.connectBus(this)
        ppu.connectBus(this)
        ram.connectBus(this)
    }
}