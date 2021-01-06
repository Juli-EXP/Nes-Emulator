package ext

fun String.toByteArrayFromHex(): ByteArray {
    val data = ByteArray(this.length / 2)

    var i = 0
    while (i < this.length) {
        data[i / 2] = Integer.decode("0x${this[i]}${this[i + 1]}").toByte()
        i += 2
    }
    return data
}