
import ppu.Display
import ppu.Palete2C02
import tornadofx.View
import tornadofx.borderpane


class GUITest : View("Test"){
    override val root = borderpane{
        this.prefWidth = 800.0
        this.prefHeight = 500.0

        val display = Display(prefWidth, prefHeight)
        val c = Palete2C02.palette

        this.center = display


        for(i in 0..100){
            display.draw(i,3 , c[1].red, c[1].green, c[1].blue)
        }
    }

}
