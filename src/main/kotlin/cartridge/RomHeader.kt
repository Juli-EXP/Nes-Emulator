package cartridge

import ext.toBoolean
import ext.toHexString

class RomHeader(private val headerBytes: ByteArray) {
    val headerConstant = headerBytes.copyOfRange(0, 3)
    val prgSize = headerBytes[4].toInt() * 0x4000   //times 16KB
    val chrSize = headerBytes[5].toInt() * 0x2000   //times 8 KB
    val flag6 = headerBytes[6].toInt()
    val flag7 = headerBytes[7].toInt()
    val flag8 = headerBytes[8].toInt()
    val flag9 = headerBytes[9].toInt()
    val flag10 = headerBytes[10].toInt()
    val unused = headerBytes.copyOfRange(11, 15)

    val size: Int
        get() = headerBytes.size

    val isValid: Boolean
        get() = headerBytes.toHexString().toUpperCase() == iNesConstant

    val trainerPresent: Boolean
        get() = (flag6 and 0x4).toBoolean()

    val mirroring: Mirroring
        get() {
            return if((flag6 and 0x4).toBoolean()){
                Mirroring.VERTICAL
            }else{
                Mirroring.HORIZONTAL
            }
        }

    val mapper: Int
        get() = (flag7 and 0xFF00) or ((flag6 shr 4) and 0xFF)

    companion object {
        const val iNesConstant = "4E45531A"
    }
}