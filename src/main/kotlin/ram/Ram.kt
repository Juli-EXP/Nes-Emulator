package ram

class Ram(
    size: Int
) {
    //variables---------------------------------------------------------------------------------------------------------
    private val data = IntArray(size)

    //reads from RAM
    fun read(addr: Int): Int {
        return this.data[addr]
    }

    //writes to RAM
    fun write(addr: Int, data: Int) {
        this.data[addr] = data
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