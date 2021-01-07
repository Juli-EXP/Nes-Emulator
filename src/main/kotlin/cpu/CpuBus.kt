package cpu

import Ram
import ppu.Ppu

class CpuBus(
    cpu: Cpu,
    val ppu: Ppu,
    val ram: Ram
) {


    fun read(addr: Int): Int {
        return when(addr){
            //reads from ram
            in 0x0000..0x17FF -> ram.read(addr % 0x800) and 0xFF
            //reads from ppu
            in 0x2000..0x3FFF -> ppu.read(addr % 0x8) and 0xFF
            else -> 0
        }
    }

    fun write(addr: Int, data: Int) {
        when(addr){
            //writes to ram
            in 0x0000..0x17FF -> ram.write(addr % 0x800, data and 0xFF)
            //writes to ppu
            in 0x2000..0x3FFF -> ppu.write(addr % 0x8, data and 0xFF)
        }

        //
        if (addr in 0..0xFFFF)
            ram.write(addr, data and 0xFF)
    }

    //connect the all devices to the bus
    init {
        cpu.connectBus(this)
        ppu.connectBus(this)
        ram.connectBus(this)
    }
}