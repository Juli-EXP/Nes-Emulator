package ppu

import Ram
import cartridge.Cartridge

class Ppu {
    //Variables---------------------------------------------------------------------------------------------------------

    private lateinit var cartridge: Cartridge            //Connected cartridge
    private val vRam: Ram = Ram(0x200)              //Nametable RAM / VRAM
    private val patternRam: Ram = Ram(0x2000)       //Pattern RAM
    private val paletteRam = Ram(0x20)              //Palette RAM

    private val colorPalette = Palete2C02.palette

    private var scanline: Int = 0       //Row
    private var lineCycle: Int = 0      //Collumn
    private var frameComplete = false   //Indicates if a full frame is complete

    private var oamAddress = 0x00
    private val oamData: Nothing = TODO()

    private val registers = IntArray(0x08)

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
        return when (addr) {
            PPUSTATUS -> {
                TODO()
            }
            OAMDATA -> {
                TODO()
            }
            PPUDATA -> {
                TODO()
            }
            else -> 0
        }

    }

    //Writes to the CPU bus
    fun cpuWrite(addr: Int, data: Int) {
        when (addr) {
            PPUCTRL -> {
                registers[PPUCTRL] = data
                TODO("Bits 0 and 1")
            }
            PPUMASK -> {
                registers[PPUMASK] = data
            }
            OAMADDR -> {
                oamAddress = data
            }
            OAMDATA -> {
                TODO()
            }
            PPUSCROLL -> {
                TODO()
            }
            PPUADDR -> {
                TODO()
            }
            PPUDATA -> {
                TODO()
            }
        }
    }

    //Reads from the PPU bus
    fun ppuRead(addr: Int): Int {
        //if cartridge TODO(Cartridge)
        when (addr) {
            //Pattern Table
            in 0x0000..0x1FFF -> {
                return vRam.read(addr) and 0xFF
            }
            //Nametables
            in 0x2000..0x3EFF -> {
                TODO()
            }
            //Palette
            in 0x3F00..0x3FFF -> {
                var newAddr = addr and 0x1F

                when (newAddr) {
                    0x10 -> newAddr = 0x00
                    0x14 -> newAddr = 0x04
                    0x18 -> newAddr = 0x08
                    0x1C -> newAddr = 0x0C
                }
                return paletteRam.read(newAddr) and 0xFF
            }
            else -> return 0
        }
    }

    //Writes to the PPU bus
    fun ppuWrite(addr: Int, data: Int) {
        //if cartridge
        when (addr) {
            //Pattern Table
            in 0x0000..0x1FFF -> {
                vRam.write(addr, data and 0xFF)
            }
            //Nametables
            in 0x2000..0x3EFF -> {
                TODO("Nametables")
            }
            //Palette
            in 0x3F00..0x3FFF -> {
                var newAddr = addr and 0x1F

                when (newAddr) {
                    0x10 -> newAddr = 0x00
                    0x14 -> newAddr = 0x04
                    0x18 -> newAddr = 0x08
                    0x1C -> newAddr = 0x0C
                }
                paletteRam.write(newAddr, data and 0xFF)
            }
        }
    }

    //Connects the cartridge to the PPU bus
    fun connectCartridge(cartridge: Cartridge) {
        this.cartridge = cartridge
    }

    fun reset() {

    }

    fun clock() {
        ++lineCycle
        if (lineCycle >= 341) {
            lineCycle = 0
            ++scanline
            if (scanline >= 261) {
                scanline = -1
                frameComplete = true
            }
        }


    }


    //Graphics stuff----------------------------------------------------------------------------------------------------
}