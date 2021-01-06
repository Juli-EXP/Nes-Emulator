package ext

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }