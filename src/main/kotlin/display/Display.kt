package display

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.PixelWriter
import javafx.scene.paint.Color

class Display(
    private val width: Int = NTSC_DISPLAY_WIDTH,
    private val height: Int = NTSC_DISPLAY_HEIGHT
) : Canvas(width.toDouble(), height.toDouble()) {
    companion object {
        const val NTSC_DISPLAY_WIDTH = 256
        const val NTSC_DISPLAY_HEIGHT = 240
        const val DEFAULT_DISPLAY_SCALE = 2
    }

    private val gc: GraphicsContext = graphicsContext2D
    private val pw: PixelWriter = gc.pixelWriter
    var scale = DEFAULT_DISPLAY_SCALE
        set(value) {
            field = value
            setWidth((NTSC_DISPLAY_WIDTH * scale).toDouble())
            setHeight((NTSC_DISPLAY_HEIGHT * scale).toDouble())
        }


    fun setPixel(row: Int, column: Int, color: Color) {
        for (i in 0 until scale) {       // Width scale
            for (j in 0 until scale) {   // Height scale
                pw.setColor(row + i, column + j, color)
            }
        }
    }
}