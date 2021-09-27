package display

import common.Constants
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritablePixelFormat
import ppu.DisplayBuffer
import java.nio.IntBuffer

class Display(
    width: Int = Constants.NTSC_DISPLAY_WIDTH,
    height: Int = Constants.NTSC_DISPLAY_HEIGHT
) : Canvas(width.toDouble(), height.toDouble()) {

    private val gc: GraphicsContext = graphicsContext2D
    private val pw: PixelWriter = gc.pixelWriter
    private val format: WritablePixelFormat<IntBuffer> = WritablePixelFormat.getIntArgbInstance()

    var scale = Constants.DEFAULT_DISPLAY_SCALE
        set(value) {
            field = value
            width = (Constants.NTSC_DISPLAY_WIDTH * value).toDouble()
            height = ((Constants.NTSC_DISPLAY_HEIGHT * value).toDouble())
        }

    fun draw(displayBuffer: DisplayBuffer) {
        //TODO scaling

        //Create 1D array out of an 2D array
        val pixels = IntArray((width * height).toInt())

        var index = 0
        for (row in 0 until height.toInt()) {
            for (column in 0 until width.toInt()) {
                pixels[index++] = displayBuffer.getPixel(row, column)
            }
        }

        pw.setPixels(0, 0, width.toInt(), height.toInt(), format, pixels, 0, width.toInt())
    }
}