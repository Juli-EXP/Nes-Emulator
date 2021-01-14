import cartridge.RomHeader
import cpu.*
import ext.toByteArrayFromHex
import ppu.Ppu
import java.nio.file.Files
import java.nio.file.Paths


private var cpu = Cpu()
private var ppu = Ppu()
private var ram = Ram(0x10000)
private var cpuBus = CpuBus(cpu, ppu, ram)

fun main() {
    val romBytes = Files.readAllBytes(Paths.get("roms/nestest.nes"))
    val romData = romBytes.copyOfRange(0x10, 0x4000)
    println(RomHeader(romBytes.copyOfRange(0, 0xF)).mapper)

    var i = 0x8000
    for (b in romData) {
        ram.write(i, b.toInt())
        ++i
    }

    i = 0xC000
    for (b in romData) {
        ram.write(i, b.toInt())
        ++i
    }

    cpu.registers.pc = 0xC000
    cpu.debug = true


    /*
    while (true) {
        cpu.clock()
        if(ram.read(0x02) != 0) println("0x02: " + ram.read(0x02))
        if(ram.read(0x03) != 0) println("0x03: " + ram.read(0x03))
        if(ram.read(0x0200) != 0) println("0x0200: " + ram.read(0x0200))
        if(ram.read(0x0300) != 0) println("0x0300: " + ram.read(0x0300))
    }

     */


}
