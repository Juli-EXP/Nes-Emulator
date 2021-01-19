package cartridge

import mappers.Mapper
import mappers.Mapper000
import java.nio.file.Files
import java.nio.file.Paths

class Cartridge(
    private val romFilePath: String
) {
    private val programMemory: ByteArray
    private val characterMemory: ByteArray
    private val mapper: Mapper?
    private val mirroring: Mirroring


    //Communication with the bus----------------------------------------------------------------------------------------

    //Returns read data from the Cartridge to the CPU bus
    fun cpuRead(addr: Int): Int {
        return programMemory[mapper!!.cpuRead(addr)].toInt()
    }

    //Writes data to the Cartridge, if possible
    fun cpuWrite(addr: Int, data: Int) {
        if (mapper!!.useCartridgeRam(addr)) {
            TODO()
        }
    }

    //Returns read data from the Cartridge to the PPU bus
    fun ppuRead(addr: Int): Int {
        return 0
    }

    //Writes data to the Cartridge, if possible
    fun ppuWrite(addr: Int, data: Int) {
    }


    //Prepare data
    init {
        //Read in all data
        var romData = Files.readAllBytes(Paths.get(romFilePath))

        //Construct header
        val header = RomHeader(romData.copyOfRange(0, 16))

        //Remove header from romData
        romData = romData.copyOfRange(header.size, romData.size)

        if (header.trainerPresent) {
            //Read trainer and ignore it
            val trainer = romData.copyOfRange(0, 512)
            romData = romData.copyOfRange(512, romData.size)
        }

        //Read program memory
        programMemory = romData.copyOfRange(0, header.prgSize)
        romData = romData.copyOfRange(header.prgSize, romData.size)

        //Read character memory
        characterMemory = romData.copyOfRange(0, header.chrSize)
        romData = romData.copyOfRange(header.chrSize, romData.size)

        //Placeholder for INST-ROM and PROM

        mirroring = header.mirroring

        mapper = when (header.mapper) {
            0 -> Mapper000(header.prgBanks, header.chrBanks)
            else -> null
        }
    }
}