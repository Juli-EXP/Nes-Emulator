package cartridge

import mappers.Mapper
import mappers.Mapper000
import mappers.MapperException
import java.nio.file.Files
import java.nio.file.Paths


class Cartridge(
    romFilePath: String
) {
    private val prgMemory: ByteArray
    private val chrMemory: ByteArray
    private val mapper: Mapper
    private val mirroring: Mirroring

    // Prepare data
    init {
        // Read in all data from the file
        var romData = Files.readAllBytes(Paths.get(romFilePath))

        // Construct header
        val header = RomHeader(romData.copyOfRange(0, 16))

        // Remove header from romData
        romData = romData.copyOfRange(header.size, romData.size)

        if (header.trainerPresent) {
            // Read trainer and ignore it
            val trainer = romData.copyOfRange(0, 512)
            romData = romData.copyOfRange(512, romData.size)
        }

        // Read program memory
        prgMemory = romData.copyOfRange(0, header.prgSize)
        romData = romData.copyOfRange(header.prgSize, romData.size)

        // Read character memory
        chrMemory = romData.copyOfRange(0, header.chrSize)
        romData = romData.copyOfRange(header.chrSize, romData.size)

        // Placeholder for INST-ROM and PROM

        mirroring = header.mirroring

        mapper = when (header.mapper) {
            0 -> Mapper000(header.prgBanks, header.chrBanks)
            else -> throw MapperException()
        }
    }


    // Returns read data from the Cartridge to the CPU bus
    fun cpuRead(address: Int): Int {
        val mappedAddress = mapper.cpuRead(address)
        return if (mappedAddress != null) {
            prgMemory[mappedAddress].toInt()
        } else {
            0
        }
    }

    // Writes data to the Cartridge, if possible
    fun cpuWrite(address: Int, data: Int) {
        val mappedAddress = mapper.cpuWrite(address)
        if (mappedAddress != null) {
            prgMemory[mappedAddress] = data.toByte()
        }
    }

    // Returns read data from the Cartridge to the PPU bus
    fun ppuRead(address: Int): Int {
        val mappedAddress = mapper.ppuRead(address)
        return if (mappedAddress != null) {
            chrMemory[mappedAddress].toInt()
        } else {
            0
        }
    }

    // Writes data to the Cartridge, if possible
    fun ppuWrite(address: Int, data: Int) {
        val mappedAddress = mapper.ppuWrite(address)
        if (mappedAddress != null) {
            chrMemory[mappedAddress] = data.toByte()
        }
    }
}