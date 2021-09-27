package util

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

fun main() {
    val list = createList("logs/palette.txt")

    var code = ""

    for (l in list) {
        val temp = l.split(" ")
        val values: ArrayList<String> = ArrayList()

        for (v in temp) {
            if (v != "") {
                values.add(v)
            }
        }

        var i = 0
        while (i < values.size) {
            code += "color(${values[i].toInt()} / 255.0, ${values[i + 1].toInt()} / 255.0, ${values[i + 2].toInt()} / 255.0), "
            i += 3
        }
        code += "\n"
    }

    Files.write(
        Paths.get("logs/palette_code.txt"),
        code.toByteArray(),
        StandardOpenOption.CREATE
    )
}