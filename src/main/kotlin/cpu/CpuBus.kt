package cpu

import cartridge.Cartridge
import Ram
import ppu.Ppu

class CpuBus(
    private val cpu: Cpu,
    private val ppu: Ppu,
    private val ram: Ram
) {
    //variables---------------------------------------------------------------------------------------------------------

    private lateinit var cartridge: Cartridge
    private var totalClockCount: Int = 0

    //Communication-----------------------------------------------------------------------------------------------------

    fun read(addr: Int): Int {
        return when (addr) {
            //Reads from ram
            in 0x0000..0x17FF -> ram.read(addr % 0x800) and 0xFF
            //Reads from ppu
            in 0x2000..0x3FFF -> ppu.cpuRead(addr % 0x8) and 0xFF
            //Family Basic only
            in 0x6000..0x7FFF -> 0
            //Reads from Cartridge
            in 0x8000..0xFFFF -> cartridge.cpuRead(addr) and 0xFF
            else -> 0
        }
    }

    fun write(addr: Int, data: Int) {
        when (addr) {
            //Writes to ram
            in 0x0000..0x17FF -> ram.write(addr % 0x800, data and 0xFF)
            //Writes to ppu
            in 0x2000..0x3FFF -> ppu.cpuWrite(addr % 0x8, data and 0xFF)
            //Family Basic only
            in 0x6000..0x7FFF -> println("Illegal write operation at ${String.format("0x%04X")}")
            //Tries to write to Cartridge
            in 0x8000..0xFFFF -> cartridge.cpuWrite(addr, data and 0xFF)
        }
    }

    //Connects the the CPU to the bus
    init {
        cpu.connectBus(this)
    }


    //Functionality-----------------------------------------------------------------------------------------------------

    //Connects the cartridge to the CPU and PPU bus
    fun connectCartridge(cartridge: Cartridge) {
        this.cartridge = cartridge
        ppu.connectCartridge(cartridge)
    }

    //Reset the console
    fun reset() {
        totalClockCount = 0
        cpu.reset()
        ppu.reset()
    }

    //Performs one clock cycle
    fun clock() {
        cpu.clock()
        ppu.clock()
    }
}