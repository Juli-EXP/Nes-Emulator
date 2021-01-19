import cartridge.Cartridge
import cartridge.RomHeader
import cpu.*
import ext.toByteArrayFromHex
import ppu.Ppu
import util.parseLog
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

private var logFile = "logs/error.txt"

private var cpu = Cpu()
private var ppu = Ppu()
private var ram = Ram()
private var cartridge = Cartridge("roms/nestest.nes")

private var cpuBus = CpuBus(cpu, ppu, ram)

fun main() {
    cpuBus.connectCartridge(cartridge)

    cpu.registers.pc = 0xC000
    cpu.debug = true

    Files.deleteIfExists(Paths.get(logFile))
    Files.createFile(Paths.get(logFile))


    do {
        cpu.clock()
        if (cpu.cycles == 0) {
            if (cpuBus.read(0x0200) != 0) {
                //println(String.format("0x0200: 0x%02X", ram.read(0x0200)))
                Files.write(
                    Paths.get(logFile),
                    String.format("0x0200: 0x%02X at: %d\n", ram.read(0x0200), cpu.totalClockCount).toByteArray(),
                    StandardOpenOption.APPEND
                )
            }
            if (cpuBus.read(0x0300) != 0) {
                //println(String.format("0x0300: 0x%02X", ram.read(0x0300)))
                Files.write(
                    Paths.get(logFile),
                    String.format("0x0300: 0x%02X at: %d\n", ram.read(0x0300), cpu.totalClockCount).toByteArray(),
                    StandardOpenOption.APPEND
                )
            }
            if (cpu.totalClockCount == 26548) {
                println(ram.toString())
                //readLine()
            }
        }
    } while (cpu.totalClockCount <= 26554)

    println("parsing log")
    parseLog()
}

