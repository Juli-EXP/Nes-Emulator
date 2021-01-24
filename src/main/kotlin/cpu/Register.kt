package cpu

import ext.toBoolean
import ext.toInt

class Register {
    var a = 0       //Accumulator register
    var x = 0       //X register
    var y = 0       //Y register
    var sp = 0xFD   //Stack pointer
    var pc = 0      //Program counter

    var c = false   //Carry
    var z = false   //Zero
    var i = true    //Disable interrupts
    var d = false   //Decimal mode
    var b = false   //Break
    var u = true    //Unused
    var v = false   //Overflow
    var n = false   //Negative

    var p: Int //Status register as one byte
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

    fun resetStatus() {
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
        return "a: $a, x: $x, y: $y\n" +
                "${String.format("sp: 0x%04X", sp)}, " +
                "${String.format("pc: 0x%04X", pc)}\n" +
                "c: $c, z: $z, i: $i\n" +
                "d: $d, b: $b, u: $u\n" +
                "v: $v, b: $b"
    }


}