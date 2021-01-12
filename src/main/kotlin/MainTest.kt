import cartridge.RomHeader
import cpu.*
import ext.toByteArrayFromHex
import ppu.Ppu
import java.nio.file.Files
import java.nio.file.Paths


private var cpu = Cpu()
private var ppu = Ppu()
private var ram = Ram(0xFFFF)
private var cpuBus = CpuBus(cpu, ppu, ram)

fun main() {
    val romBytes = Files.readAllBytes(Paths.get("roms/nestest.nes"))
    val romData = romBytes.copyOfRange(0x10, 0x4000)

    var i = 0x8000
    for(b in romData){
        ram.write(i, b.toInt())
        ++i
    }

    i = 0xC000
    for(b in romData){
        ram.write(i, b.toInt())
        ++i
    }

    cpu.registers.pc = 0xC000
    cpu.debug = true

    while(true){
        cpu.clock()
        cpu.printDebug()
        println(cpu.fetched)
        println(cpu.totalClockCount)
    }



}
