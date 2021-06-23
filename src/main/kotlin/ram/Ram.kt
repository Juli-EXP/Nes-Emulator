package ram

class Ram(
    size: Int
) {
    private val data = IntArray(size)

    // Reads data from RAM
    fun read(address: Int): Int {
        return this.data[address]
    }

    // Writes data to RAM
    fun write(address: Int, data: Int) {
        this.data[address] = data
    }

    override fun toString(): String {
        var hexData = "XXXX"

        for (i in 0x0..0xF) {
            hexData += String.format(" %02X", i)
        }
        hexData += "\n"

        val rows = if (data.size % 0x10 == 0) {
            data.size / 0x10 - 1
        } else {
            data.size / 0x10
        }

        for (i in 0x0..rows) {
            hexData += String.format("%03X0", i)
            for (j in 0x0..0xF) {
                hexData += String.format(" %02X", data[(i shl 4) or j])
            }
            hexData += "\n"
        }

        return hexData
    }
}