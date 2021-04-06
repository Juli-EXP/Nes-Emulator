import cartridge.Cartridge
import cpu.*
import ext.toggleBit
import ppu.Ppu
import ppu.registers.PpuStatus
import ram.Ram
import util.parseLog
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

private var logFile = "logs/error.txt"

private var cpu = Cpu()
private var ppu = Ppu()
private var ram = Ram(0x800)
private var cartridge = Cartridge("roms/nestest.nes")

private var cpuBus = CpuBus(cpu, ppu, ram)

fun main() {
    //nestestTest()
    test2()
}

//Test nesttest.nes rom
fun nestestTest() {
    cpuBus.connectCartridge(cartridge)

    cpu.registers.pc = 0xC000   //Start of the headless program
    cpu.debug = true

    Files.deleteIfExists(Paths.get(logFile))
    Files.createFile(Paths.get(logFile))


    do {
        cpu.clock()
        if (cpu.cycleComplete) {
            //Read error codes from specific locations
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
        }
    } while (cpu.totalClockCount <= 26554)

    println("parsing log")
    parseLog()
}

fun test2() {
    var ppuStatus = PpuStatus(205)
    println(ppuStatus)
    ppuStatus = PpuStatus(ppuStatus.value.toggleBit(0))
    println(ppuStatus)
}