package ppu

import cpu.CpuBus

class Ppu {
    //variables---------------------------------------------------------------------------------------------------------
    private lateinit var cpuBus: CpuBus

    companion object{
        const val PPUCTRL = 0x0
        const val PPUMAKS = 0X1
        const val PPUSATUS = 0x2
        const val OAMADDR = 0x3
        const val OAMDATA = 0x4
        const val PPUSCROLL = 0x5
        const val PPUADDR = 0x6
        const val PPUDATA = 0x7
    }


    //Communication with the bus----------------------------------------------------------------------------------------

    //Connects the PPU to the CPU bus
    fun connectBus(cpuBus: CpuBus) {
        this.cpuBus = cpuBus
    }

    //reads from the CPU bus
    fun read(addr: Int): Int{
        when(addr){

        }

        return 0
    }

    //writes to the CPU bus
    fun write(addr: Int, data: Int){
        when(addr){

        }
    }


}