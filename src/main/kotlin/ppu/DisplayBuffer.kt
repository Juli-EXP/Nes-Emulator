package ppu

import common.Constants

class DisplayBuffer {
    var pixels = Array(Constants.NTSC_DISPLAY_HEIGHT) { IntArray(Constants.NTSC_DISPLAY_WIDTH) }
        private set

    fun clear() {
        pixels = Array(Constants.NTSC_DISPLAY_HEIGHT) { IntArray(Constants.NTSC_DISPLAY_WIDTH) }
    }

    fun getPixel(row: Int, column: Int): Int {
        return pixels[row][column]
    }

    fun setPixel(row: Int, column: Int, color: Int){
        val colorValue = Palette2C02.paletteHex[color]

        pixels[row][column] = (0xFF shl 24) or (colorValue and 0x00FFFFFF)
    }
}