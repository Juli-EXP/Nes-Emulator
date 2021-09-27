import common.Constants
import display.Display
import ppu.DisplayBuffer
import ppu.Palette2C02

import tornadofx.View
import tornadofx.borderpane


class GUITest : View("Test") {
    override val root = borderpane {
        this.prefWidth = Constants.NTSC_DISPLAY_HEIGHT.toDouble()
        this.prefHeight = Constants.NTSC_DISPLAY_WIDTH.toDouble()

        val d = Display()
        val db = DisplayBuffer()
        this.center = d


        for (i in 0 until 100) {
            for(j in 15 until 30){
                db.setPixel(i, j, 0x1)
            }
        }

        d.draw(db)
    }

}


