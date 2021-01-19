package util

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

fun main() {
    println("Ready")

    when (readLine()!!.toInt()) {
        0 -> parseLog()
        1 -> parseGoldenLog()
        2 -> {
            parseLog()
            parseGoldenLog()
        }
    }

    println("Finished")
}

fun parseLog() {
    val list = createList("logs/log.txt")
    var entry = ""

    for (l in list) {
        entry = entry + l.subSequence(6, 10) + "  "     //PC
        entry = entry + l.subSequence(18, 20) + "  "    //Opcode
        //entry = entry + l.subSequence(30, 34) + "  "    //Address
        entry = entry + l.subSequence(43, 46) + "  "    //Instruction
        entry += l.subSequence(70, l.length)            //Registers
        entry += "\n"
    }

    //println(entry)

    Files.write(
        Paths.get("logs/log2.txt"),
        entry.toByteArray(),
        StandardOpenOption.CREATE
    )
}

fun parseGoldenLog() {
    val list = createList("logs/goldenlog.txt")
    var entry = ""

    for (l in list) {
        entry = entry + l.subSequence(0, 4) + "  "              //PC
        entry = entry + l.subSequence(6, 8) + "  "              //Opcode
        //entry = entry + l.subSequence(12, 14) + ""              //Address
        //entry = entry + l.subSequence(9, 11) + "  "             //Address
        entry = entry + l.subSequence(16, 19) + "  "            //Instruction
        entry = entry + "A: " + l.subSequence(50, 52) + "  "    //A
        entry = entry + "X: " + l.subSequence(55, 57) + "  "    //X
        entry = entry + "Y: " + l.subSequence(60, 62) + "  "    //Y
        entry = entry + "P: " + l.subSequence(65, 67) + "  "    //P
        entry = entry + "SP: " + l.subSequence(71, 73) + "  "   //SP
        entry = entry + "CYC: " + l.subSequence(90, l.length)   //CYC
        entry += "\n"
    }

    //println(entry)

    Files.write(
        Paths.get("logs/goldenlog2.txt"),
        entry.toByteArray(),
        StandardOpenOption.CREATE
    )
}

fun createList(path: String): List<String> {
    val inputStream = File(path).inputStream()
    val list = mutableListOf<String>()

    inputStream.bufferedReader().useLines { lines -> lines.forEach { list.add(it) } }

    return list
}