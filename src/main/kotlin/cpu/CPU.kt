package cpu

import ext.toBoolean
import ext.toInt


class CPU {
    //variables---------------------------------------------------------------------------------------------------------
    private lateinit var cpuBus: CPUBus

    var registers = Register()

    var opcode: Int = 0             //Current opcode
    var fetched: Int = 0            //Fetched value
    var cycles: Int = 0             //How many cycles are left
    var address: Int = 0            //Absolute address
    var offsetAddress: Int = 0    //Relative address

    var clockCount: Int = 0         //Total clock count


    //Communication with the bus----------------------------------------------------------------------------------------

    //Connects the CPU to the bus
    fun connectBus(cpuBus: CPUBus) {
        this.cpuBus = cpuBus
    }

    //reads from the bus
    private fun read(addr: Int): Int {
        return cpuBus.read(addr)
    }

    //writes to the bus
    private fun write(addr: Int, data: Int) {
        cpuBus.write(addr, data)
    }


    //CPU functions-----------------------------------------------------------------------------------------------------

    //Performs one clock cycle
    fun clockCycle() {
        if (cycles == 0) {
            opcode = read(registers.pc++)

            cycles = instructionTable[opcode]!!.cycles

            val additionalCycle1 = instructionTable[opcode]!!.addressingMode()
            val additionalCycle2 = instructionTable[opcode]!!.instruction()

            cycles += (additionalCycle1 and additionalCycle2)
        }

        ++clockCount

        --cycles
    }

    //prints the current state of the CPU
    fun printDebug(msg: String = "") {
        var debug = ""

        if (msg.isNotEmpty())
            debug = "$msg\n"

        debug += "Last instruction:\n"
        debug += String.format("op: 0x%02X", opcode) + ", ${instructionTable[opcode].toString()}\n"
        debug += "Current status:\n"
        debug += String.format("addr: 0x%04X", address) + "\n"
        debug += registers.toString() + "\n"
        print(debug)
    }

    //Resets the CPU
    fun reset() {
        //reset clock count
        clockCount = 0

        //Set program counter
        address = 0xFFFC

        val lo = read(address + 0)
        val hi = read(address + 1)

        registers.pc = (hi shl 8) or lo

        //Reset registers
        registers.a = 0
        registers.x = 0
        registers.y = 0
        registers.sp = 0xFD
        registers.resetStatus()

        //Reset any other var
        offsetAddress = 0
        address = 0
        fetched = 0

        cycles = 8
    }

    //Interrupt request
    fun interruptRequest() {
        if (!registers.i) {
            //Save current program counter to the stack
            push((registers.pc shr 8))
            push(registers.pc)

            //Push status register to
            registers.b = false
            registers.u = true
            registers.i = true
            push(registers.status)

            //Set program counter to tha value of stored at the absolute address
            address = 0xFFFE
            val lo = read(address)
            val hi = read(address + 1)
            registers.pc = (hi shl 8) or lo

            //uses 8 clock cycles
            cycles = 7
        }
    }

    //Interrupt request, but it cannot be disabled
    fun nonMaskableInterrupt() {
        //Save current program counter to the stack
        push((registers.pc shr 8))
        push(registers.pc)

        //Push status register to
        registers.b = false
        registers.u = true
        registers.i = true
        push(registers.status)

        //Set program counter to tha value of stored at the absolute address
        address = 0xFFFA
        val lo = read(address)
        val hi = read(address + 1)
        registers.pc = (hi shl 8) or lo

        //uses 8 clock cycles
        cycles = 8
    }

    //Gets data depending on the current addressing mode
    private fun fetch() {
        if (instructionTable[opcode]!!.addressingMode != this::imp)
            fetched = read(address)
    }

    private fun push(data: Int) {
        write(0x0100 + registers.sp--, data)
    }

    private fun pop(): Int {
        return read(0x0100 + ++registers.sp)
    }


    //Fill instruction list---------------------------------------------------------------------------------------------
    private val instructionTable = mapOf(
        0x00 to Instruction(this::brk, this::imp, 7),
        0x01 to Instruction(this::ora, this::idx, 6),
        0x05 to Instruction(this::ora, this::zpg, 3),
        0x06 to Instruction(this::asl, this::zpg, 5),
        0x08 to Instruction(this::php, this::imp, 3),
        0x09 to Instruction(this::ora, this::imm, 2),
        0x0A to Instruction(this::asl, this::imp, 2),
        0x0D to Instruction(this::ora, this::abs, 4),
        0x0E to Instruction(this::asl, this::abs, 6),

        0x10 to Instruction(this::bpl, this::rel, 2),
        0x11 to Instruction(this::ora, this::idy, 5),
        0x15 to Instruction(this::ora, this::zpx, 4),
        0x16 to Instruction(this::asl, this::zpx, 6),
        0x18 to Instruction(this::clc, this::imp, 2),
        0x19 to Instruction(this::ora, this::aby, 4),
        0x1D to Instruction(this::ora, this::abx, 4),
        0x1E to Instruction(this::asl, this::abx, 7),

        0x20 to Instruction(this::jsr, this::abs, 6),
        0x21 to Instruction(this::and, this::idx, 6),
        0x24 to Instruction(this::bit, this::zpg, 3),
        0x25 to Instruction(this::and, this::zpg, 3),
        0x26 to Instruction(this::rol, this::zpg, 5),
        0x28 to Instruction(this::plp, this::imp, 4),
        0x29 to Instruction(this::and, this::imm, 2),
        0x2A to Instruction(this::rol, this::imp, 2),
        0x2C to Instruction(this::bit, this::abs, 4),
        0x2D to Instruction(this::and, this::abs, 4),
        0x2E to Instruction(this::rol, this::abs, 6),

        0x30 to Instruction(this::bmi, this::rel, 2),
        0x31 to Instruction(this::and, this::idy, 5),
        0x35 to Instruction(this::and, this::zpx, 4),
        0x36 to Instruction(this::rol, this::zpx, 6),
        0x38 to Instruction(this::sec, this::imp, 2),
        0x39 to Instruction(this::and, this::aby, 4),
        0x3D to Instruction(this::and, this::abx, 4),
        0x3E to Instruction(this::rol, this::abx, 7),

        0x40 to Instruction(this::rti, this::imp, 6),
        0x41 to Instruction(this::eor, this::idx, 6),
        0x45 to Instruction(this::eor, this::zpg, 3),
        0x46 to Instruction(this::lsr, this::zpg, 5),
        0x48 to Instruction(this::pha, this::imp, 3),
        0x49 to Instruction(this::eor, this::imm, 2),
        0x4A to Instruction(this::lsr, this::imp, 4),
        0x4C to Instruction(this::jmp, this::abs, 3),
        0x4D to Instruction(this::eor, this::abs, 4),
        0x4E to Instruction(this::lsr, this::abs, 6),

        0x50 to Instruction(this::bvc, this::rel, 2),
        0x51 to Instruction(this::eor, this::idy, 5),
        0x55 to Instruction(this::eor, this::zpx, 4),
        0x56 to Instruction(this::lsr, this::zpx, 6),
        0x58 to Instruction(this::cli, this::imp, 2),
        0x59 to Instruction(this::eor, this::aby, 4),
        0x5D to Instruction(this::eor, this::abx, 4),
        0x5E to Instruction(this::lsr, this::abx, 7),

        0x60 to Instruction(this::rts, this::imp, 6),
        0x61 to Instruction(this::adc, this::idx, 6),
        0x65 to Instruction(this::adc, this::zpg, 3),
        0x66 to Instruction(this::ror, this::zpg, 5),
        0x68 to Instruction(this::pla, this::imp, 4),
        0x69 to Instruction(this::adc, this::imm, 2),
        0x6A to Instruction(this::ror, this::imp, 2),
        0x6C to Instruction(this::jmp, this::ind, 5),
        0x6D to Instruction(this::adc, this::abs, 4),
        0x6E to Instruction(this::ror, this::abs, 6),

        0x70 to Instruction(this::bvs, this::rel, 2),
        0x71 to Instruction(this::adc, this::idy, 5),
        0x75 to Instruction(this::adc, this::zpx, 4),
        0x76 to Instruction(this::ror, this::zpx, 6),
        0x78 to Instruction(this::sei, this::imp, 2),
        0x79 to Instruction(this::adc, this::aby, 4),
        0x7D to Instruction(this::adc, this::abx, 4),
        0x7E to Instruction(this::ror, this::abx, 7),

        0x81 to Instruction(this::sta, this::idx, 6),
        0x84 to Instruction(this::sty, this::zpg, 3),
        0x85 to Instruction(this::sta, this::zpg, 3),
        0x86 to Instruction(this::stx, this::zpg, 3),
        0x88 to Instruction(this::dey, this::imp, 2),
        0x8A to Instruction(this::txa, this::imp, 2),
        0x8C to Instruction(this::sty, this::abs, 4),
        0x8D to Instruction(this::sta, this::abs, 4),
        0x8E to Instruction(this::stx, this::abs, 4),

        0x90 to Instruction(this::bcc, this::rel, 2),
        0x91 to Instruction(this::sta, this::idy, 6),
        0x94 to Instruction(this::sty, this::zpx, 4),
        0x95 to Instruction(this::sta, this::zpx, 4),
        0x96 to Instruction(this::stx, this::zpy, 4),
        0x98 to Instruction(this::tya, this::imp, 2),
        0x99 to Instruction(this::sta, this::aby, 5),
        0x9A to Instruction(this::txs, this::imp, 2),
        0x9D to Instruction(this::sta, this::abx, 5),

        0xA0 to Instruction(this::ldy, this::imm, 2),
        0xA1 to Instruction(this::lda, this::idx, 6),
        0xA2 to Instruction(this::ldx, this::imm, 2),
        0xA4 to Instruction(this::ldy, this::zpg, 3),
        0xA5 to Instruction(this::lda, this::zpg, 3),
        0xA6 to Instruction(this::ldx, this::zpg, 3),
        0xA8 to Instruction(this::tay, this::imp, 2),
        0xA9 to Instruction(this::lda, this::imm, 2),
        0xAA to Instruction(this::tax, this::imp, 2),
        0xAC to Instruction(this::ldy, this::abs, 4),
        0xAD to Instruction(this::lda, this::abs, 4),
        0xAE to Instruction(this::ldx, this::abs, 4),

        0xB0 to Instruction(this::bcs, this::rel, 2),
        0xB1 to Instruction(this::lda, this::idy, 5),
        0xB4 to Instruction(this::ldy, this::zpx, 4),
        0xB5 to Instruction(this::lda, this::zpx, 4),
        0xB6 to Instruction(this::ldx, this::zpy, 4),
        0xB8 to Instruction(this::clv, this::imp, 2),
        0xB9 to Instruction(this::lda, this::aby, 4),
        0xBA to Instruction(this::tsx, this::imp, 2),
        0xBC to Instruction(this::ldy, this::abx, 4),
        0xBD to Instruction(this::lda, this::abx, 4),
        0xBE to Instruction(this::ldx, this::aby, 4),

        0xC0 to Instruction(this::cpy, this::imm, 2),
        0xC1 to Instruction(this::cmp, this::idx, 6),
        0xC4 to Instruction(this::cpy, this::zpg, 3),
        0xC5 to Instruction(this::cmp, this::zpg, 3),
        0xC6 to Instruction(this::dec, this::zpg, 5),
        0xC8 to Instruction(this::iny, this::imp, 2),
        0xC9 to Instruction(this::cmp, this::imm, 2),
        0xCA to Instruction(this::dex, this::imp, 2),
        0xCC to Instruction(this::cpy, this::abs, 4),
        0xCD to Instruction(this::cmp, this::abs, 4),
        0xCE to Instruction(this::dec, this::abs, 6),

        0xD0 to Instruction(this::bne, this::rel, 2),
        0xD1 to Instruction(this::cmp, this::idy, 5),
        0xD5 to Instruction(this::cmp, this::zpx, 4),
        0xD6 to Instruction(this::dec, this::zpx, 6),
        0xD8 to Instruction(this::cld, this::imp, 2),
        0xD9 to Instruction(this::cmp, this::aby, 4),
        0xDD to Instruction(this::cmp, this::abx, 4),
        0xDE to Instruction(this::dec, this::abx, 7),

        0xE0 to Instruction(this::cpx, this::imm, 2),
        0xE1 to Instruction(this::sbc, this::idx, 6),
        0xe4 to Instruction(this::cpx, this::zpg, 3),
        0xE5 to Instruction(this::sbc, this::zpg, 3),
        0xE6 to Instruction(this::inc, this::zpg, 5),
        0xE8 to Instruction(this::inx, this::imp, 2),
        0xE9 to Instruction(this::sbc, this::imm, 2),
        0xEA to Instruction(this::nop, this::imp, 2),
        0xEC to Instruction(this::cpx, this::abs, 4),
        0xED to Instruction(this::sbc, this::abs, 4),
        0xEE to Instruction(this::inc, this::abs, 6),

        0xF0 to Instruction(this::beq, this::rel, 2),
        0xF1 to Instruction(this::sbc, this::idy, 5),
        0xF5 to Instruction(this::sbc, this::zpx, 4),
        0xF6 to Instruction(this::inc, this::zpx, 6),
        0xF8 to Instruction(this::sed, this::imp, 2),
        0xF9 to Instruction(this::sbc, this::aby, 4),
        0xFD to Instruction(this::sbc, this::abx, 4),
        0xFE to Instruction(this::inc, this::abx, 7)
    )


    //Here is a usefull link with all the addressing modes and opcodes:
    //https://www.masswerk.at/6502/6502_instruction_set.html

    //The addressing modes return 1 if an additional clock cycle is needed
    //Addressing modes--------------------------------------------------------------------------------------------------

    //Accumulator
    fun acc(): Int {
        println("Called ACC")
        return 0
    }

    //Absolute
    //Uses a 16-bit address
    private fun abs(): Int {
        val lo = read(registers.pc++)
        val hi = read(registers.pc++)

        address = ((hi shl 8) or lo)
        return 0
    }

    //Absolute x
    //Absolute with a offset of x
    private fun abx(): Int {
        val lo = read(registers.pc++)
        val hi = read(registers.pc++)

        address = (((hi shl 8) or lo) + registers.x) and 0xFFFF

        return if ((address and 0xFF00) != (hi shl 8)) {
            1
        } else {
            0
        }
    }

    //Absolute y
    //Absolute with a offset of y
    private fun aby(): Int {
        val lo = read(registers.pc++)
        val hi = read(registers.pc++)

        address = (((hi shl 8) or lo) + registers.y) and 0xFFFF

        return if ((address and 0xFF00) != (hi shl 8)) {
            1
        } else {
            0
        }
    }

    //Immediate
    private fun imm(): Int {
        address = registers.pc++
        return 0
    }

    //Implied
    private fun imp(): Int {
        return 0
    }

    //Indirect
    private fun ind(): Int {
        val lo = read(registers.pc++)
        val hi = read(registers.pc++)

        val temp = ((hi shl 8) or lo) and 0xFFFF

        //simulating a hardware bug
        address = if (lo == 0xFF) {
            (read(temp and 0xFF00) shl 8) or read(temp)
        } else {
            (read(temp + 1) shl 8) or read(temp)
        }
        return 0
    }

    //Indirect x
    private fun idx(): Int {
        val temp = read(registers.pc++)

        val lo = read((temp + registers.x) and 0xFF)
        val hi = read((temp + registers.x + 1) and 0xFF)

        address = ((hi shl 8) or lo) and 0xFFFF
        return 0
    }

    //Indirect y
    private fun idy(): Int {
        val temp = read(registers.pc++)

        val lo = read(temp and 0xFF)
        val hi = read((temp + 1) and 0xFF)

        address = (((hi shl 8) or lo) + registers.y) and 0xFFFF

        return if ((address and 0xFF00) != (hi shl 8)) {
            1
        } else {
            0
        }
    }

    //Relative
    private fun rel(): Int {
        offsetAddress = read(registers.pc++)

        //checks if number is bigger than 127
        if (offsetAddress > 0x80) {
            offsetAddress -= 0x100
        }

        return 0
    }

    //Zero page
    private fun zpg(): Int {
        address = read(registers.pc++)
        return 0
    }

    //Zero page x
    private fun zpx(): Int {
        address = read(registers.pc++) + registers.x
        return 0
    }

    //Zero page y
    private fun zpy(): Int {
        address = read(registers.pc++) + registers.x
        return 0
    }


    //Opcodes-----------------------------------------------------------------------------------------------------------

    //And with carry
    private fun adc(): Int {
        fetch()
        val result = registers.a + fetched + registers.c.toInt()

        registers.c = result > 255
        registers.z = (result and 0xFF) == 0
        registers.v = (((registers.a xor fetched).inv() and (registers.a xor result)) and 0x80).toBoolean()
        registers.n = (result and 0x80).toBoolean()

        registers.a = result and 0xFF
        return 1
    }

    //And (with accumulator)
    private fun and(): Int {
        fetch()
        registers.a = registers.a and fetched

        registers.z = (registers.a and 0xFF) == 0
        registers.n = (registers.a and 0x80).toBoolean()
        return 1
    }

    //Arithmetic shift left
    private fun asl(): Int {
        fetch()
        val temp = if (instructionTable[opcode]!!.addressingMode == this::imp) {
            registers.a
        } else {
            fetched
        }

        val result = temp shl 1

        registers.c = (result and 0xFF00).toBoolean()
        registers.z = (result and 0xFF) == 0
        registers.n = (result and 0x80).toBoolean()

        if (instructionTable[opcode]!!.addressingMode == this::imp) {
            registers.a = result and 0xFF
        } else {
            write(address, result)
        }
        return 0
    }

    //Branch on carry clear
    private fun bcc(): Int {
        if (!registers.c) {
            ++cycles

            address = registers.pc + offsetAddress

            if ((address and 0xFF0) != (registers.pc and 0xFF00)) {
                ++cycles
            }

            registers.pc = address
        }
        return 0
    }

    //Branch on carry set
    private fun bcs(): Int {
        if (registers.c) {
            ++cycles

            address = registers.pc + offsetAddress

            if ((address and 0xFF0) != (registers.pc and 0xFF00)) {
                ++cycles
            }

            registers.pc = address
        }
        return 0
    }

    //Branch on equal (zero set)
    private fun beq(): Int {
        if (registers.z) {
            ++cycles

            address = registers.pc + offsetAddress

            if ((address and 0xFF0) != (registers.pc and 0xFF00)) {
                ++cycles
            }

            registers.pc = address
        }
        return 0
    }

    //Bit test
    private fun bit(): Int {
        fetch()
        val result = registers.a and fetched

        registers.z = (result and 0xFF) == 0
        registers.v = (fetched and (1 shl 7)).toBoolean()
        registers.n = (fetched and (1 shl 6)).toBoolean()
        return 0
    }

    //Branch on minus (negative set)
    private fun bmi(): Int {
        if (registers.n) {
            ++cycles

            address = registers.pc + offsetAddress

            if ((address and 0xFF0) != (registers.pc and 0xFF00)) {
                ++cycles
            }

            registers.pc = address
        }
        return 0
    }

    //Branch on not equal (zero set)
    private fun bne(): Int {
        if (!registers.z) {
            ++cycles

            address = registers.pc + offsetAddress

            if ((address and 0xFF0) != (registers.pc and 0xFF00)) {
                ++cycles
            }

            registers.pc = address
        }
        return 0
    }

    //Branch on plus (negative clear)
    private fun bpl(): Int {
        if (!registers.n) {
            ++cycles

            address = registers.pc + offsetAddress

            if ((address and 0xFF00) != (registers.pc and 0xFF00)) {
                ++cycles
            }

            registers.pc = address
        }
        return 0
    }

    //Break / Interrupt
    private fun brk(): Int {
        ++registers.pc

        push((registers.pc shr 8))
        push(registers.pc)

        registers.b = true

        push(registers.status)

        registers.b = false
        registers.i = true

        registers.pc = read(0xFFFE) or (read(0xFFFF) shl 8)
        return 0
    }

    //Branch on overflow clear
    private fun bvc(): Int {
        if (!registers.v) {
            ++cycles

            address = registers.pc + offsetAddress

            if ((address and 0xFF00) != (registers.pc and 0xFF00)) {
                ++cycles
            }

            registers.pc = address
        }
        return 0
    }

    //Branch on overflow set
    private fun bvs(): Int {
        if (registers.v) {
            ++cycles

            address = registers.pc + offsetAddress

            if ((address and 0xFF00) != (registers.pc and 0xFF00)) {
                ++cycles
            }

            registers.pc = address
        }
        return 0
    }

    //Clear carry
    private fun clc(): Int {
        registers.c = false
        return 0
    }

    //Clear decimal
    private fun cld(): Int {
        registers.d = false
        return 0
    }

    //Clear interrupt disable
    private fun cli(): Int {
        registers.i = false
        return 0
    }

    //Clear overflow
    private fun clv(): Int {
        registers.v = false
        return 0
    }

    //Compare (with accumulator)
    private fun cmp(): Int {
        fetch()
        val result = registers.a - fetched
        registers.c = registers.a >= fetched
        registers.z = (result and 0xFF) == 0
        registers.n = (result and 0x80).toBoolean()
        return 0
    }

    //Compare with x
    private fun cpx(): Int {
        fetch()
        val result = registers.x - fetched
        registers.c = registers.x >= fetched
        registers.z = (result and 0xFF) == 0
        registers.n = (result and 0x80).toBoolean()
        return 0
    }

    //Compare with y
    private fun cpy(): Int {
        fetch()
        val result = registers.y - fetched

        registers.c = registers.y >= fetched
        registers.z = (result and 0xFF) == 0
        registers.n = (result and 0x80).toBoolean()
        return 0
    }

    //Decrement
    private fun dec(): Int {
        fetch()
        val result = fetched - 1
        write(address, result)

        registers.z = (result and 0xFF) == 0
        registers.n = (result and 0x80).toBoolean()
        return 0
    }

    //Decrement x
    private fun dex(): Int {
        --registers.x

        registers.z = registers.x == 0
        registers.n = (registers.x and 0x80).toBoolean()
        return 0
    }

    //Decrement y
    private fun dey(): Int {
        --registers.y

        registers.z = registers.y == 0
        registers.n = (registers.y and 0x80).toBoolean()
        return 0
    }

    //Exclusive or (with accumulator)
    private fun eor(): Int {
        fetch()
        registers.a = registers.a xor fetched

        registers.z = registers.x == 0
        registers.n = (registers.x and 0x80).toBoolean()
        return 0
    }

    //Increment
    private fun inc(): Int {
        fetch()
        val result = fetched + 1
        write(address, result)

        registers.z = (result and 0xFF) == 0
        registers.n = (result and 0x80).toBoolean()
        return 0
    }

    //Increment x
    private fun inx(): Int {
        ++registers.x

        registers.z = registers.x == 0
        registers.n = (registers.x and 0x80).toBoolean()
        return 0
    }

    //Increment y
    private fun iny(): Int {
        ++registers.y

        registers.z = registers.y == 0
        registers.n = (registers.y and 0x80).toBoolean()
        return 0
    }

    //Jump
    private fun jmp(): Int {
        registers.pc = address
        return 0
    }

    //Jump to subroutine
    private fun jsr(): Int {
        --registers.pc
        push((registers.pc shr 8))
        push(registers.pc)

        registers.pc = address
        return 0
    }

    //Load accumulator
    private fun lda(): Int {
        fetch()
        registers.a = fetched

        registers.z = registers.a == 0
        registers.n = (registers.a and 0x80).toBoolean()
        return 0
    }

    //Load x
    private fun ldx(): Int {
        fetch()
        registers.x = fetched

        registers.z = registers.x == 0
        registers.n = (registers.x and 0x80).toBoolean()
        return 0
    }

    //Load y
    private fun ldy(): Int {
        fetch()
        registers.y = fetched

        registers.z = registers.y == 0
        registers.n = (registers.y and 0x80).toBoolean()
        return 0
    }

    //Logical shift right
    private fun lsr(): Int {
        fetch()
        val temp = if (instructionTable[opcode]!!.addressingMode == this::imp) {
            registers.a
        } else {
            fetched
        }

        val result = temp shr 1

        registers.z = (result and 0xFF) == 0
        registers.n = (result and 0x80).toBoolean()

        if (instructionTable[opcode]!!.addressingMode == this::imp) {
            registers.a = result and 0xFF
        } else {
            write(address, result)
        }
        return 0
    }

    //No operation
    private fun nop(): Int {
        return 0
    }

    //Or with accumulator
    private fun ora(): Int {
        fetch()
        registers.a = registers.a or fetched

        registers.z = registers.x == 0
        registers.n = (registers.x and 0x80).toBoolean()
        return 0
    }

    //Push accumulator
    private fun pha(): Int {
        push(registers.a)
        return 0
    }

    //Push processor status
    private fun php(): Int {
        registers.b = true
        push(registers.status)
        registers.b = false
        return 0
    }

    //Pull accumulator
    private fun pla(): Int {
        registers.a = pop()
        return 0
    }

    //Pull processor status
    private fun plp(): Int {
        registers.status = pop()
        registers.u = true
        return 0
    }

    //Rotate left
    private fun rol(): Int {
        fetch()
        val temp = if (instructionTable[opcode]!!.addressingMode == this::imp) {
            registers.a
        } else {
            fetched
        }

        val result = (temp shl 1) or registers.c.toInt()
        registers.c = (result and 0xFF00).toBoolean()
        registers.z = (result and 0xFF) == 0
        registers.n = (result and 0x80).toBoolean()

        if (instructionTable[opcode]!!.addressingMode == this::imp) {
            registers.a = result and 0xFF
        } else {
            write(address, result)
        }
        return 0
    }

    //Rotate right
    private fun ror(): Int {
        fetch()
        val temp = if (instructionTable[opcode]!!.addressingMode == this::imp) {
            registers.a
        } else {
            fetched
        }

        val result = (registers.c.toInt() shl 7) or (temp shr 1)
        registers.c = (result and 0x1).toBoolean()
        registers.z = (result and 0xFF) == 0
        registers.n = (result and 0x80).toBoolean()

        if (instructionTable[opcode]!!.addressingMode == this::imp) {
            registers.a = result and 0xFF
        } else {
            write(address, result)
        }
        return 0
    }

    //Return from interrupt
    private fun rti(): Int {
        registers.status = pop()
        registers.pc = pop() or (pop() shl 8)
        return 0
    }

    //Return from subroutine
    private fun rts(): Int {
        registers.pc = pop() or (pop() shl 8)
        return 0
    }

    //Subtract with carry
    private fun sbc(): Int {
        fetch()
        val result = registers.a - fetched - (!registers.c).toInt()

        registers.c = (result and 0xFF00).toBoolean()
        registers.z = (result and 0xFF) == 0
        registers.v = ((result xor registers.a) and (registers.a xor fetched) and 0x80).toBoolean()
        registers.n = (result and 0x80).toBoolean()
        return 0
    }

    //Set carry
    private fun sec(): Int {
        registers.c = true
        return 0
    }

    //Set decimal
    private fun sed(): Int {
        registers.d = true
        return 0
    }

    //Set interrupt disable
    private fun sei(): Int {
        registers.i = true
        return 0
    }

    //Store accumulator
    private fun sta(): Int {
        write(address, registers.a)
        return 0
    }

    //Store x
    private fun stx(): Int {
        write(address, registers.x)
        return 0
    }

    //Store y
    private fun sty(): Int {
        write(address, registers.y)
        return 0
    }

    //Transfer accumulator to X
    private fun tax(): Int {
        registers.x = registers.a
        registers.z = registers.x == 0
        registers.n = (registers.x and 0x80).toBoolean()
        return 0
    }

    //Transfer accumulator to y
    private fun tay(): Int {
        registers.y = registers.a
        registers.z = registers.y == 0
        registers.n = (registers.y and 0x80).toBoolean()
        return 0
    }

    //Transfer stack pointer to x
    private fun tsx(): Int {
        registers.x = registers.sp
        registers.z = registers.x == 0
        registers.n = (registers.x and 0x80).toBoolean()
        return 0
    }

    //Transfer X to accumulator
    private fun txa(): Int {
        registers.a = registers.x
        registers.z = registers.x == 0
        registers.n = (registers.x and 0x80).toBoolean()
        return 0
    }

    //Transfer X to stack pointer
    private fun txs(): Int {
        registers.sp = registers.x
        return 0
    }

    //Transfer Y to accumulator
    private fun tya(): Int {
        registers.a = registers.y
        registers.z = registers.y == 0
        registers.n = (registers.y and 0x80).toBoolean()
        return 0
    }

}