package ppu

import Ram
import cartridge.Cartridge

class Ppu {
    //Variables---------------------------------------------------------------------------------------------------------

    private lateinit var cartridge: Cartridge            //Connected cartridge
    private val vRam: Ram = Ram()                        //Nametable RAM
    private val patternRam: Ram = Ram(0x2000)       //Pattern RAM
    private val paletteRam = Palete2C02.palette          //Palette RAM


    private var scanline: Int = 0       //row
    private var cycle: Int = 0          //collumn
    private var frameComplete = false   //Indicates if a full frame is complete


    companion object {
        const val PPUCTRL = 0x0
        const val PPUMASK = 0X1
        const val PPUSTATUS = 0x2
        const val OAMADDR = 0x3
        const val OAMDATA = 0x4
        const val PPUSCROLL = 0x5
        const val PPUADDR = 0x6
        const val PPUDATA = 0x7
    }


    //Communication with the bus----------------------------------------------------------------------------------------

    //Reads from the CPU bus
    fun cpuRead(addr: Int): Int {
        when (addr) {
            PPUSTATUS -> {
            }
            OAMDATA -> {
            }
            PPUDATA -> {
            }
        }

        return 0
    }

    //Writes to the CPU bus
    fun cpuWrite(addr: Int, data: Int) {
        when (addr) {
            PPUCTRL -> {
            }
            PPUMASK -> {
            }
            OAMADDR -> {
            }
            OAMDATA -> {
            }
            PPUSCROLL -> {
            }
            PPUADDR -> {
            }
            PPUDATA -> {
            }
        }
    }

    //Reads from the PPU bus
    fun ppuRead(addr: Int): Int {
        //if cartridge
        return when (addr) {
            in 0x0000..0x1FFF -> TODO("Pattern Table/Cart")
            in 0x2000..0x3EFF -> TODO("Nametables")
            in 0x3F00..0x3FFF -> TODO("Palette RAM")
            else -> 0
        }
    }

    //Writes to the PPU bus
    fun ppuWrite(addr: Int, data: Int) {
        //if cartridge
        when (addr) {
            in 0x0000..0x1FFF -> TODO("Pattern Table/Cart")
            in 0x2000..0x3EFF -> TODO("Nametables")
            in 0x3F00..0x3FFF -> TODO("Palette RAM")
        }
    }

    //Connects the cartridge to the PPU bus
    fun connectCartridge(cartridge: Cartridge) {
        this.cartridge = cartridge
    }

    fun reset() {

    }

    fun clock() {
        ++cycle
        if (cycle >= 341) {
            cycle = 0
            ++scanline
            if (scanline >= 261) {
                scanline = -1
                frameComplete = true
            }
        }

    }


    //Graphics stuff----------------------------------------------------------------------------------------------------
}