package cpu

import ext.toBoolean
import ext.toInt

class Register {
    var a = 0       // Accumulator register
    var x = 0       // X register
    var y = 0       // Y register
    var sp = 0xFD   // Stack pointer
    var pc = 0      // Program counter

    var c = false   // Carry
    var z = false   // Zero
    var i = true    // Disable interrupts
    var d = false   // Decimal mode
    var b = false   // Break            //Maybe set this to true TODO
    var u = true    // Unused
    var v = false   // Overflow
    var n = false   // Negative

    var p: Int  // Status register as one byte
        get() = (c.toInt()) or
                (z.toInt() shl 1) or
                (i.toInt() shl 2) or
                (d.toInt() shl 3) or
                (b.toInt() shl 4) or
                (u.toInt() shl 5) or
                (v.toInt() shl 6) or
                (n.toInt() shl 7)
        set(value){
            c = (value and 0x01).toBoolean()
            z = (value and 0x02).toBoolean()
            i = (value and 0x04).toBoolean()
            d = (value and 0x08).toBoolean()
            b = (value and 0x10).toBoolean()
            u = (value and 0x20).toBoolean()
            v = (value and 0x40).toBoolean()
            n = (value and 0x80).toBoolean()
        }

    fun reset() {
        a = 0
        x = 0
        y = 0
        sp = 0xFD

        c = false
        z = false
        i = true
        d = false
        b = false
        u = true
        v = false
        n = false
    }

    override fun toString(): String {
        return "A: $a, X: $x, Y: $y, ${String.format("P: 0x%02X", p)}\n" +
                "${String.format("SP: 0x%04X", sp)}, " +
                "${String.format("PC: 0x%04X", pc)}\n" +
                "C: $c, Z: $z, I: $i\n" +
                "D: $d, B: $b, U: $u\n" +
                "V: $v, N: $n"
    }


}