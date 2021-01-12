package cartridge

import ext.toHexString
import mappers.Mapper
import mappers.Mapper000
import java.io.File

class Cartridge(
    private val romFile: File
) {
    private val programMemory: ByteArray
    private val characterMemory: ByteArray
    private val mapper: Mapper?
    private val mirroring: Mirroring


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

    //

    //Prepare data
    init {
        //Read in all data
        var romData = romFile.inputStream().readBytes()

        //Construct header
        val header = RomHeader(romData.copyOfRange(0, 15))
        //Remove header from romData
        romData = romData.copyOfRange(header.size, romData.size - 1)

        if (header.trainerPresent) {
            //Read trainer and ignore it
            val trainer = romData.copyOfRange(0, 511)
            romData = romData.copyOfRange(512, romData.size - 1)
        }

        //Read program memory
        programMemory = romData.copyOfRange(0, header.prgSize - 1)
        romData = romData.copyOfRange(header.prgSize, romData.size - 1)

        //Read character memory
        characterMemory = romData.copyOfRange(0, header.chrSize - 1)
        romData = romData.copyOfRange(header.chrSize, romData.size - 1)

        //TODO add INST-ROM and PROM

        mirroring = header.mirroring

        mapper = when (header.mapper) {
            0 -> Mapper000()
            else -> null
        }
    }
}