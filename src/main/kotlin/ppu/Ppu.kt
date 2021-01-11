package ppu

import cartridge.Cartridge

class Ppu {
    //Variables---------------------------------------------------------------------------------------------------------

    private lateinit var cartridge: Cartridge

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
        return 0
    }

    //Writes to the PPU bus
    fun ppuWrite(addr: Int, data: Int) {

    }

    //Connects the cartridge to the PPU bus
    fun connectCartridge(cartridge: Cartridge) {
        this.cartridge = cartridge
    }

    fun reset(){

    }

    fun clock(){

    }


    //Graphics stuff----------------------------------------------------------------------------------------------------
}