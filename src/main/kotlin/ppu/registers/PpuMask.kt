package ppu.registers

import ext.toBoolean

inline class PpuMask(val value: Int) {
    val emphasizeBlue: Boolean
        get() = (value and 0x80).toBoolean()

    val emphasizeGreen: Boolean
        get() = (value and 0x40).toBoolean()

    val emphasizeRed: Boolean
        get() = (value and 0x20).toBoolean()

    val showSprites: Boolean
        get() = (value and 0x10).toBoolean()

    val showBackground: Boolean
        get() = (value and 0x8).toBoolean()

    val showSpriteLeft: Boolean
        get() = (value and 0x4).toBoolean()

    val showBackgroundLeft: Boolean
        get() = (value and 0x2).toBoolean()

    val greyscale: Boolean
        get() = (value and 0x1).toBoolean()
}