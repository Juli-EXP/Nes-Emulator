package ppu.registers

import ext.toBoolean

inline class PpuStatus(val value: Int) {
    val verticalBlank: Boolean
        get() = (value and 0x80).toBoolean()

    val spriteZeroHit: Boolean
        get() = (value and 0x40).toBoolean()

    val spriteOverflow: Boolean
        get() = (value and 0x20).toBoolean()

    val lastWritten: Boolean
        get() = (value and 0x1F).toBoolean()
}