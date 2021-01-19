package cpu

import ext.toBoolean
import ext.toInt
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


class Cpu {
    //Variables---------------------------------------------------------------------------------------------------------
    private lateinit var cpuBus: CpuBus

    var registers = Register()

    var opcode: Int = 0                     //Current opcode
    var instruction: Instruction? = null    //Current instruction
    var fetched: Int = 0                    //Fetched value
    var cycles: Int = 0                     //How many cycles are left
    var address: Int = 0                    //Absolute address

    var totalClockCount: Int = 7            //Total clock count
    var debug: Boolean = false


    init {
        //TODO change startup states
        registers.sp = 0xFD

        Files.deleteIfExists(Paths.get("logs/log.txt"))
        Files.createFile(Paths.get("logs/log.txt"))

    }

    //Communication with the bus----------------------------------------------------------------------------------------

    //Connects the CPU to the bus
    fun connectBus(cpuBus: CpuBus) {
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
    fun clock() {
        if (cycles == 0) {
            if (debug) {
                Files.write(
                    Paths.get("logs/log.txt"),
                    String.format("PC: 0x%04X  ", registers.pc).toByteArray(),
                    StandardOpenOption.APPEND
                )
            }

            opcode = read(registers.pc++)
            instruction = instructionTable[opcode]

            if (instruction == null) {
                instruction = instructionTable[0xEA]    //NOP
            }

            cycles = instruction!!.cycles

            val additionalCycle1 = instruction!!.addressingMode()

            if (debug) {
                Files.write(
                    Paths.get("logs/log.txt"),
                    (String.format("OP: 0x%02X  ", opcode) +
                            String.format("ADDR: 0x%04X  ", address) +
                            "$instruction" +
                            "  " +
                            String.format(
                                "A: %02X  X: %02X  Y: %02X  P: %02X  SP: %02X  CYC: %d\n",
                                registers.a,
                                registers.x,
                                registers.y,
                                registers.status,
                                registers.sp,
                                totalClockCount
                            )).toByteArray(),
                    StandardOpenOption.APPEND
                )
            }

            val additionalCycle2 = instruction!!.instruction()

            cycles += (additionalCycle1 and additionalCycle2)
        }

        ++totalClockCount

        --cycles
    }

    //Prints the current state of the CPU
    fun printDebug(msg: String = "") {
        var debug = ""

        if (msg.isNotEmpty())
            debug = "$msg\n"

        debug += String.format("op: 0x%02X", opcode) + ", ${instruction}\n"
        debug += "Current status:\n"
        debug += String.format("addr: 0x%04X", address) + "\n"
        debug += registers.toString() + "\n"
        print(debug)
    }

    //Resets the CPU
    fun reset() {
        //reset clock count
        totalClockCount = 0

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
        if (instruction!!.addressingMode != this::imp)
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
        0x4A to Instruction(this::lsr, this::imp, 2),
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
        0xE4 to Instruction(this::cpx, this::zpg, 3),
        0xE5 to Instruction(this::sbc, this::zpg, 3),
        0xE6 to Instruction(this::inc, this::zpg, 5),
        0xE8 to Instruction(this::inx, this::imp, 2),
        0xE9 to Instruction(this::sbc, this::imm, 2),
        0xEA to Instruction(this::nop, this::noa, 2),
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
        0xFE to Instruction(this::inc, this::abx, 7),

        //Unoffical opcodes
        0xA3 to Instruction(this::lax, this::idx, 6),
        0xA7 to Instruction(this::lax, this::zpg, 3),
        0xAF to Instruction(this::lax, this::abs, 4),
        0xB3 to Instruction(this::lax, this::idy, 5),
        0xB7 to Instruction(this::lax, this::zpy, 4),
        0xBF to Instruction(this::lax, this::aby, 4),

        0x83 to Instruction(this::sax, this::idx, 6),
        0x87 to Instruction(this::sax, this::zpg, 3),
        0x8F to Instruction(this::sax, this::abs, 4),
        0x97 to Instruction(this::sax, this::zpy, 4),

        0xEB to Instruction(this::sbc, this::imm, 2),

        0xC3 to Instruction(this::dcp, this::idx, 8),
        0xC7 to Instruction(this::dcp, this::zpg, 5),
        0xCF to Instruction(this::dcp, this::abs, 6),
        0xD3 to Instruction(this::dcp, this::idy, 8),
        0xD7 to Instruction(this::dcp, this::zpx, 6),
        0xDB to Instruction(this::dcp, this::aby, 7),
        0xDF to Instruction(this::dcp, this::abx, 7),

        0xE3 to Instruction(this::isc, this::idx, 8),
        0xE7 to Instruction(this::isc, this::zpg, 5),
        0xEF to Instruction(this::isc, this::abs, 6),
        0xF3 to Instruction(this::isc, this::idy, 8),
        0xF7 to Instruction(this::isc, this::zpx, 6),
        0xFB to Instruction(this::isc, this::aby, 7),
        0xFF to Instruction(this::isc, this::abx, 7),

        0x23 to Instruction(this::rla, this::idx, 8),
        0x27 to Instruction(this::rla, this::zpg, 5),
        0x2F to Instruction(this::rla, this::abs, 6),
        0x33 to Instruction(this::rla, this::idy, 8),
        0x37 to Instruction(this::rla, this::zpx, 6),
        0x3B to Instruction(this::rla, this::aby, 7),
        0x3F to Instruction(this::rla, this::abx, 7),

        0x63 to Instruction(this::rra, this::idx, 8),
        0x67 to Instruction(this::rra, this::zpg, 5),
        0x6F to Instruction(this::rra, this::abs, 6),
        0x73 to Instruction(this::rra, this::idy, 8),
        0x77 to Instruction(this::rra, this::zpx, 6),
        0x7B to Instruction(this::rra, this::aby, 7),
        0x7F to Instruction(this::rra, this::abx, 7),

        0x03 to Instruction(this::slo, this::idx, 8),
        0x07 to Instruction(this::slo, this::zpg, 5),
        0x0F to Instruction(this::slo, this::abs, 6),
        0x13 to Instruction(this::slo, this::idy, 8),
        0x17 to Instruction(this::slo, this::zpx, 6),
        0x1B to Instruction(this::slo, this::aby, 7),
        0x1F to Instruction(this::slo, this::abx, 7),

        0x43 to Instruction(this::sre, this::idx, 8),
        0x47 to Instruction(this::sre, this::zpg, 5),
        0x4F to Instruction(this::sre, this::abs, 6),
        0x53 to Instruction(this::sre, this::idy, 8),
        0x57 to Instruction(this::sre, this::zpx, 6),
        0x5B to Instruction(this::sre, this::aby, 7),
        0x5F to Instruction(this::sre, this::abx, 7)
    )


    //Here is a useful link with all the addressing modes and opcodes:
    //https://www.masswerk.at/6502/6502_instruction_set.html

    //The addressing modes return 1 if an additional clock cycle is needed
    //Addressing modes--------------------------------------------------------------------------------------------------

    //Accumulator
    fun acc(): Int {
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
        address = registers.pc++ and 0xFFFF
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

        val temp = ((hi shl 8) or lo)

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

        address = ((hi shl 8) or lo)
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
        var offset = read(registers.pc++)

        //checks if number is bigger than 127
        if ((offset and 0x80).toBoolean()) {
            offset -= 0x100
        }

        address = (registers.pc + offset) and 0xFFFF

        return if ((address and 0xFF00) != (registers.pc and 0xFF00)) {
            1
        } else {
            0
        }
    }

    //Zero page
    private fun zpg(): Int {
        address = read(registers.pc++)
        return 0
    }

    //Zero page x
    private fun zpx(): Int {
        address = (read(registers.pc++) + registers.x) and 0xFF
        return 0
    }

    //Zero page y
    private fun zpy(): Int {
        address = (read(registers.pc++) + registers.y) and 0xFF
        return 0
    }

    //Address mode for all NOPs
    private fun noa(): Int {
        return when (opcode) {
            0x1A, 0x3A, 0x5A, 0x7A, 0xDA, 0xEA, 0xFA -> imp()
            0x80, 0x82, 0x89, 0xC2, 0xE2 -> imm()
            0x0C -> abs()
            0x1C, 0x3C, 0x5C, 0x7C, 0xDC, 0xFC -> abx()
            0x04, 0x44, 0x64 -> zpg()
            0x14, 0x34, 0x54, 0x74, 0xD4, 0xF4 -> zpx()
            else -> 0
        }
    }

    //Opcodes-----------------------------------------------------------------------------------------------------------

    private fun branch() {
        ++cycles
        registers.pc = address
    }

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
        val temp = if (instruction!!.addressingMode == this::imp) {
            registers.a
        } else {
            fetched
        }

        val result = temp shl 1

        registers.c = (result and 0xFF00).toBoolean()
        registers.z = (result and 0xFF) == 0
        registers.n = (result and 0x80).toBoolean()

        if (instruction!!.addressingMode == this::imp) {
            registers.a = result and 0xFF
        } else {
            write(address, result)
        }
        return 0
    }

    //Branch on carry clear
    private fun bcc(): Int {
        if (!registers.c) {
            branch()
            return 1
        }
        return 0
    }

    //Branch on carry set
    private fun bcs(): Int {
        if (registers.c) {
            branch()
            return 1
        }
        return 0
    }

    //Branch on equal (zero set)
    private fun beq(): Int {
        if (registers.z) {
            branch()
            return 1
        }
        return 0
    }

    //Bit test
    private fun bit(): Int {
        fetch()
        val result = registers.a and fetched

        registers.z = (result and 0xFF) == 0
        registers.n = (fetched and (1 shl 7)).toBoolean()
        registers.v = (fetched and (1 shl 6)).toBoolean()
        return 0
    }

    //Branch on minus (negative set)
    private fun bmi(): Int {
        if (registers.n) {
            branch()
            return 1
        }
        return 0
    }

    //Branch on not equal (zero clear)
    private fun bne(): Int {
        if (!registers.z) {
            branch()
            return 1
        }
        return 0
    }

    //Branch on plus (negative clear)
    private fun bpl(): Int {
        if (!registers.n) {
            branch()
            return 1
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
            branch()
            return 1
        }
        return 0
    }

    //Branch on overflow set
    private fun bvs(): Int {
        if (registers.v) {
            branch()
            return 1
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
        return 1
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
        registers.x = registers.x and 0xFF

        registers.z = registers.x == 0
        registers.n = (registers.x and 0x80).toBoolean()
        return 0
    }

    //Decrement y
    private fun dey(): Int {
        --registers.y and 0xFF
        registers.y = registers.y and 0xFF

        registers.z = registers.y == 0
        registers.n = (registers.y and 0x80).toBoolean()
        return 0
    }

    //Exclusive or (with accumulator)
    private fun eor(): Int {
        fetch()
        registers.a = registers.a xor fetched

        registers.z = registers.a == 0
        registers.n = (registers.a and 0x80).toBoolean()
        return 1
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
        registers.x = registers.x and 0xFF

        registers.z = registers.x == 0
        registers.n = (registers.x and 0x80).toBoolean()
        return 0
    }

    //Increment y
    private fun iny(): Int {
        ++registers.y and 0xFF
        registers.y = registers.y and 0xFF

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
        return 1
    }

    //Load x
    private fun ldx(): Int {
        fetch()
        registers.x = fetched

        registers.z = registers.x == 0
        registers.n = (registers.x and 0x80).toBoolean()
        return 1
    }

    //Load y
    private fun ldy(): Int {
        fetch()
        registers.y = fetched

        registers.z = registers.y == 0
        registers.n = (registers.y and 0x80).toBoolean()
        return 1
    }

    //Logical shift right
    private fun lsr(): Int {
        fetch()
        val temp = if (instruction!!.addressingMode == this::imp) {
            registers.a
        } else {
            fetched
        }

        val result = temp shr 1

        registers.c = (fetched and 0x01).toBoolean()
        registers.z = (result and 0xFF) == 0
        registers.n = (result and 0x80).toBoolean()

        if (instruction!!.addressingMode == this::imp) {
            registers.a = result and 0xFF
        } else {
            write(address, result)
        }
        return 0
    }

    //No operation
    private fun nop(): Int {
        when (opcode) {
            0x1A, 0x3A, 0x5A, 0x7A, 0xDA, 0xEA, 0xFA -> cycles
            0x80, 0x82, 0x89, 0xC2, 0xE2 -> cycles
            0x0C -> cycles += 2
            0x1C, 0x3C, 0x5C, 0x7C, 0xDC, 0xFC -> {
                cycles += 2
                return 1
            }
            0x04, 0x44, 0x64 -> ++cycles
            0x14, 0x34, 0x54, 0x74, 0xD4, 0xF4 -> cycles += 2
        }
        return 0
    }

    //Or with accumulator
    private fun ora(): Int {
        fetch()
        registers.a = registers.a or fetched

        registers.z = registers.a == 0
        registers.n = (registers.a and 0x80).toBoolean()
        return 1
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
        registers.z = (registers.a and 0xFF) == 0
        registers.n = (registers.a and 0x80).toBoolean()
        return 0
    }

    //Pull processor status
    private fun plp(): Int {
        registers.status = pop()
        registers.u = true
        registers.b = false
        return 0
    }

    //Rotate left
    private fun rol(): Int {
        fetch()
        val temp = if (instruction!!.addressingMode == this::imp) {
            registers.a
        } else {
            fetched
        }

        val result = (temp shl 1) or registers.c.toInt()
        registers.c = (temp and 0x80).toBoolean()
        registers.z = (result and 0xFF) == 0
        registers.n = (result and 0x80).toBoolean()

        if (instruction!!.addressingMode == this::imp) {
            registers.a = result and 0xFF
        } else {
            write(address, result)
        }
        return 0
    }

    //Rotate right
    private fun ror(): Int {
        fetch()
        val temp = if (instruction!!.addressingMode == this::imp) {
            registers.a
        } else {
            fetched
        }

        val result = (registers.c.toInt() shl 7) or (temp shr 1)

        registers.c = (temp and 0x01).toBoolean()
        registers.z = (result and 0xFF) == 0
        registers.n = (result and 0x80).toBoolean()

        if (instruction!!.addressingMode == this::imp) {
            registers.a = result and 0xFF
        } else {
            write(address, result)
        }
        return 0
    }

    //Return from interrupt
    private fun rti(): Int {
        registers.status = pop()
        registers.u = true
        registers.pc = pop() or (pop() shl 8)
        return 0
    }

    //Return from subroutine
    private fun rts(): Int {
        registers.pc = pop() or (pop() shl 8)
        ++registers.pc
        return 0
    }

    //Subtract with carry
    private fun sbc(): Int {
        fetch()
        val result = registers.a - fetched - (!registers.c).toInt()

        registers.c = result >= 0
        registers.z = (result and 0xFF) == 0
        registers.v = ((result xor registers.a) and (registers.a xor fetched) and 0x80).toBoolean()
        registers.n = (result and 0x80).toBoolean()
        registers.a = result and 0xFF
        return 1
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
        return 1
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
        registers.z = registers.a == 0
        registers.n = (registers.a and 0x80).toBoolean()
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
        registers.z = registers.a == 0
        registers.n = (registers.a and 0x80).toBoolean()
        return 0
    }

    //Unofficial opcodes------------------------------------------------------------------------------------------------

    //LDA then TAX
    private fun lax(): Int {
        fetch()
        registers.a = fetched
        registers.x = registers.a

        registers.z = registers.a == 0
        registers.n = (registers.a and 0x80).toBoolean()
        return 1
    }

    //Store and of A and X
    private fun sax(): Int {
        val result = registers.a and registers.x
        write(address, result)
        return 0
    }

    //DEC then CMP
    private fun dcp(): Int {
        fetch()
        var result = fetched - 1
        write(address, result)

        fetch()
        result = registers.a - fetched

        registers.c = registers.a >= fetched
        registers.z = (result and 0xFF) == 0
        registers.n = (result and 0x80).toBoolean()
        return 0
    }

    //INC the SBC
    private fun isc(): Int {
        fetch()
        var result = (fetched + 1) and 0xFF
        write(address, result)

        fetch()     //Get the altered value back
        result = registers.a - fetched - (!registers.c).toInt()

        registers.c = result >= 0
        registers.z = (result and 0xFF) == 0
        registers.v = ((result xor registers.a) and (registers.a xor fetched) and 0x80).toBoolean()
        registers.n = (result and 0x80).toBoolean()
        registers.a = result and 0xFF
        return 0
    }

    //ROL the AND
    private fun rla(): Int {
        fetch()
        val result = (fetched shl 1) or registers.c.toInt()
        registers.c = (fetched and 0x80).toBoolean()
        registers.z = (result and 0xFF) == 0
        registers.n = (result and 0x80).toBoolean()

        write(address, result)

        registers.a = (registers.a and result) and 0xFF
        return 0
    }

    //ROR then ADC
    private fun rra(): Int {
        fetch()
        var result = (registers.c.toInt() shl 7) or (fetched shr 1)
        registers.c = (fetched and 0x01).toBoolean()
        write(address, result)

        fetch()     //Get the altered value back
        result = registers.a + fetched + registers.c.toInt()

        registers.c = result > 0xFF
        registers.z = (result and 0xFF) == 0
        registers.v = (((registers.a xor fetched).inv() and (registers.a xor result)) and 0x80).toBoolean()
        registers.n = (result and 0x80).toBoolean()
        registers.a = result and 0xFF
        return 0
    }

    //ASL the ORA
    private fun slo(): Int {
        fetch()
        val result = fetched shl 1

        registers.a = (registers.a or result) and 0xFF

        registers.c = (result and 0xFF00).toBoolean()
        registers.z = registers.a == 0
        registers.n = (registers.a and 0x80).toBoolean()

        write(address, result)
        return 0
    }

    //LSR then EOR
    private fun sre(): Int {
        fetch()
        val result = fetched shr 1

        registers.a = (registers.a xor result) and 0xFF

        registers.c = (fetched and 0x01).toBoolean()
        registers.z = (registers.a and 0xFF) == 0
        registers.n = (registers.a and 0x80).toBoolean()

        write(address, result)
        return 0
    }
}