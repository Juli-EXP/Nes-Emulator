package cpu

class Flag {
    private var c: Boolean = false      //Carry
    private var z: Boolean = false      //Zero
    private var i: Boolean = false      //Disable interrputs
    private var d: Boolean = false      //Decimal mode
    private var b: Boolean = false      //Break
    private var u: Boolean = false      //Unused
    private var v: Boolean = false      //Overflow
    private var n: Boolean = false      //Negative

    fun getFlag(flag: Flags): Boolean {
        return when (flag) {
            Flags.C -> c
            Flags.Z -> z
            Flags.I -> i
            Flags.D -> d
            Flags.B -> b
            Flags.U -> u
            Flags.V -> v
            Flags.N -> n
        }
    }

    fun setFlag(flag: Flags, value: Boolean) {
        when (flag) {
            Flags.C -> c = value
            Flags.Z -> z = value
            Flags.I -> i = value
            Flags.D -> d = value
            Flags.B -> b = value
            Flags.U -> u = value
            Flags.V -> v = value
            Flags.N -> n = value
        }
    }

    fun reset() {
        c = false
        z = false
        i = false
        d = false
        b = false
        b = false
        u = false
        v = false
        n = false
    }
}

enum class Flags {
    C, Z, I, D, B, U, V, N
}
