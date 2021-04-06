package ext


fun Int.toBoolean(): Boolean {
    return this != 0
}

fun Int.setBit(pos: Int): Int {
    return this or (1 shl pos)
}

fun Int.clearBit(pos: Int): Int {
    return this and (1 shl pos).inv()
}

fun Int.toggleBit(pos: Int): Int {
    return this xor (1 shl pos)
}

fun Int.changeBit(pos: Int, value: Int): Int {
    return this xor ((-value xor this) and (1 shl pos))
}