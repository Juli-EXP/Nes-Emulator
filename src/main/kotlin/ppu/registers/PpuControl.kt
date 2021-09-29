package ppu.registers

import ext.toBoolean

class PpuControl(var value: Int) {
    val enableNmi: Boolean
        get() = (value and 0x80).toBoolean()

    val slaveMode: Boolean
        get() = (value and 0x40).toBoolean()

    val spriteSize: Boolean
        get() = (value and 0x20).toBoolean()

    val backgroundPatternAddress: Boolean
        get() = (value and 0x10).toBoolean()

    val spritePatternAddress: Boolean
        get() = (value and 0x8).toBoolean()

    val incrementMode: Boolean
        get() = (value and 0x4).toBoolean()

    val nametableY: Boolean
        get() = (value and 0x2).toBoolean()

    val nametableX: Boolean
        get() = (value and 0x1).toBoolean()
}