import cpu.*
import ext.toByteArrayFromHex
import ppu.PPU
import java.nio.file.Files
import java.nio.file.Paths


private var cpuBus: CPUBus? = null
private var cpu: CPU? = null
private var ppu: PPU? = null
private var ram: Ram? = null

fun main() {


}

fun loadRom(path: String){
    val data = Files.readAllBytes(Paths.get(path))

    for((i, bytes) in data.withIndex()){
        cpuBus!!.ram.write(i, bytes.toInt() and 0xFF)
    }
}

fun olcTest(){
    cpu = CPU()
    ram = Ram(0xFFFF)
    cpuBus = CPUBus(cpu!!, ppu!!, ram!!)

    val code = "A20A8E0000A2038E0100AC0000A900186D010088D0FA8D0200EAEAEA"
    val data = code.toByteArrayFromHex()

    var offset = 0x8000
    for(byte in data){
        cpuBus!!.ram.write(offset, byte.toInt() and 0xFF)
        ++offset
    }

    cpuBus!!.ram.write(0xFFFC, 0x00)
    cpuBus!!.ram.write(0xFFFD, 0x80)

    cpu!!.reset()

    while(true){
        println("Press any key")
        when(readLine()){
            "g" -> {
                do{
                    cpu!!.clockCycle()
                }while(cpu!!.cycles != 0)
            }


        }

        cpu!!.printDebug()
    }
}
