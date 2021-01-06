package ppu

import cpu.CPUBus

class PPU {
    //variables---------------------------------------------------------------------------------------------------------
    private lateinit var cpuBus: CPUBus


    //Communication with the bus----------------------------------------------------------------------------------------

    //Connects the CPU to the bus
    fun connectBus(cpuBus: CPUBus) {
        this.cpuBus = cpuBus
    }
}