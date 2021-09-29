package ppu

import ram.Ram
import cartridge.Cartridge
import ext.changeBit
import ext.clearBit
import ext.setBit
import ext.toInt
import ppu.registers.PpuControl
import ppu.registers.PpuMask
import ppu.registers.PpuStatus

class Ppu {
    //Variables---------------------------------------------------------------------------------------------------------

    // Memory
    private lateinit var cartridge: Cartridge           // Connected cartridge
    private val nametables: Ram = Ram(0x800)       // Stores the layout of the sprites (VRAM)
    private val oam: Ram = Ram(0x100)              // Object attribute memory
    private val paletteRam: Ram = Ram(0x20)        // Stores the color palette

    private val paletteTable = Palette2C02.paletteHex

    // Displaying stuff
    private var scanline: Int = 0                       // Row
    private var scanLineCycle: Int = 0                  // Column
    private var frameComplete = false                   // Indicates if a full frame is complete

    // Registers
    private var ppuControl = PpuControl(0)
    private var ppuStatus = PpuStatus(0)
    private var ppuMask = PpuMask(0)

    // Other variables
    private var ppuAddress = 0
    private var ppuTempAddress = 0
    private var oamAddress = 0
    private var addressLatch = 0
    private var ppuDataBuffer = 0

    var nonMaskableInterrupt = false


    @Suppress("SpellCheckingInspection")
    companion object {
        const val PPUCTRL = 0x0
        const val PPUMASK = 0X1
        const val PPUSTATUS = 0x2
        const val OAMADDR = 0x3
        const val OAMDATA = 0x4
        const val PPUSCROLL = 0x5
        const val PPUADDR = 0x6
        const val PPUDATA = 0x7
        const val OAMDMA = 0x4014
    }


    // Communication with the bus---------------------------------------------------------------------------------------

    // Reads data from the PPU registers
    fun cpuRead(address: Int): Int {
        var data = 0
        when (address) {
            PPUSTATUS -> {
                data = (ppuStatus.value and 0xE0) or (ppuDataBuffer and 0x1F)
                ppuStatus.value.clearBit(7)     // Clear vertical blank
                addressLatch = 0
            }
            OAMDATA -> {
                data = oam.read(oamAddress)
            }
            PPUDATA -> {
                data = ppuDataBuffer
                ppuDataBuffer = ppuRead(ppuAddress)

                // Immediate read if the address is in the palette range
                if (ppuAddress in 0x3F00..0x3FFF) {
                    data = ppuDataBuffer
                }

                ppuAddress += if (ppuControl.incrementMode) 32 else 1
            }
        }

        return data
    }

    // Writes data to the PPU registers
    fun cpuWrite(address: Int, data: Int) {
        when (address) {
            PPUCTRL -> {
                ppuControl.value = data
                ppuTempAddress.changeBit(0, ppuControl.nametableX.toInt())
                ppuTempAddress.changeBit(1, ppuControl.nametableY.toInt())
            }
            PPUMASK -> {
                ppuMask.value = data
            }
            OAMADDR -> {
                oamAddress = data
            }
            OAMDATA -> {
                oam.write(oamAddress, data)
            }
            PPUSCROLL -> {
                TODO()
            }
            PPUADDR -> {
                if (addressLatch == 0) {
                    ppuTempAddress = ((data and 0x3F) shl 8) or (ppuTempAddress and 0xFF)
                    addressLatch = 1
                } else {
                    ppuTempAddress = (ppuTempAddress and 0xFF00) or data
                    ppuAddress = ppuTempAddress
                    addressLatch = 0
                }
            }
            PPUDATA -> {
                ppuWrite(ppuAddress, data)
                ppuAddress += if (ppuControl.incrementMode) 32 else 1
            }
        }
    }

    // Reads from the PPU bus
    private fun ppuRead(address: Int): Int {
        when (address) {
            // Pattern Table
            in 0x0000..0x1FFF -> {
                return cartridge.ppuRead(address) and 0xFF
            }
            // Nametables
            in 0x2000..0x3EFF -> {
                TODO("Check for dynamic mirroring")
            }
            // Palette
            in 0x3F00..0x3FFF -> {
                var newAddress = address and 0x1F

                // Mirroring for the palette
                when (newAddress) {
                    0x10 -> newAddress = 0x00
                    0x14 -> newAddress = 0x04
                    0x18 -> newAddress = 0x08
                    0x1C -> newAddress = 0x0C
                }
                return paletteRam.read(newAddress) and 0xFF
            }
            else -> return 0
        }
    }

    // Writes to the PPU bus
    private fun ppuWrite(address: Int, data: Int) {
        when (address) {
            // Pattern Table
            in 0x0000..0x1FFF -> {
                cartridge.ppuWrite(address, data and 0xFF)
            }
            // Nametables
            in 0x2000..0x3EFF -> {
                TODO("Nametables")
            }
            // Palette
            in 0x3F00..0x3FFF -> {
                var newAddress = address and 0x1F

                // Mirroring for the palette
                when (newAddress) {
                    0x10 -> newAddress = 0x00
                    0x14 -> newAddress = 0x04
                    0x18 -> newAddress = 0x08
                    0x1C -> newAddress = 0x0C
                }
                paletteRam.write(newAddress, data and 0xFF)
            }
        }
    }

    // Connects the cartridge to the PPU bus
    fun connectCartridge(cartridge: Cartridge) {
        this.cartridge = cartridge
    }

    fun reset() {
        TODO()
    }

    fun clock() {
        ++scanLineCycle
        if (scanLineCycle >= 341) {
            scanLineCycle = 0
            ++scanline
            if (scanline >= 261) {
                scanline = -1
                frameComplete = true
            }
        }


    }

}