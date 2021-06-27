import display.Display
import ppu.Palette2C02

import tornadofx.View
import tornadofx.borderpane


class GUITest : View("Test") {
    override val root = borderpane {
        this.prefWidth = (Display.NTSC_DISPLAY_HEIGHT * Display.DEFAULT_DISPLAY_SCALE).toDouble()
        this.prefHeight = (Display.NTSC_DISPLAY_WIDTH * Display.DEFAULT_DISPLAY_SCALE).toDouble()

        val d = Display()
        this.center = d

        for (i in 0 until 100) {
            d.setPixel(i, 5, Palette2C02.paletteFx[0])
        }
    }

}
