package ppu

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class Display(
    width: Double = 0.0,
    height: Double = 0.0
) : Canvas(width, height) {
    private val gc: GraphicsContext = graphicsContext2D
    private val pw = gc.pixelWriter

    init {
        gc.fill = Color.BLACK
        gc.fillRect(0.0, 0.0, width, height)
    }

    fun draw(x: Int, y: Int, r: Int, g: Int, b: Int) {
        val color = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
        pw.setArgb(x, y, color)
    }



}