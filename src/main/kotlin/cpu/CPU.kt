package cpu

import Bus


class CPU {
    //Other devices
    private lateinit var bus: Bus

    //CPU registers
    private var a: Int = 0           //Accumulator register
    private var x: Int = 0           //X register
    private var y: Int = 0           //Y register
    private var sp: Int = 0          //Stack pointer
    private var pc: Int = 0         //Program counter
    private var status: Flag = Flag()   //Status Register

    private var opcode: Int = 0          //Current opcode
    private var fetched: Int = 0         //Fetched value
    private var cycles: Int = 0          //How many cycles are left
    private var absoluteAddress: Int = 0 //Absolute address
    private var relativeAddress: Int = 0 //Relative address
    private var clockCount: Int = 0        //Total clock count


    //Communication with the bus----------------------------------------------------------------------------------------

    //Connects the CPU.CPU to the bus
    fun connectBus(bus: Bus) {
        this.bus = bus
    }

    //reads from the bus
    private fun read(addr: Int): Int {
        return bus.read(addr)
    }

    //writes to the bus
    private fun write(addr: Int, data: Int) {
        bus.write(addr, data)
    }


    //CPU functions-----------------------------------------------------------------------------------------------------

    //Resets the CPU
    private fun reset() {
        //Set program counter
        absoluteAddress = 0xFFFC

        val lo = read(absoluteAddress + 0)
        val hi = read(absoluteAddress + 1)

        pc = (hi shl 8) or lo

        //Reset registers
        a = 0
        x = 0
        y = 0
        sp = 0xFD
        status.reset()

        //Reset any other var
        relativeAddress = 0
        absoluteAddress = 0
        fetched = 0
        //clockCount = 0

        cycles = 8
    }

    //Performs one clock cycle
    private fun clockCycle() {
        if (cycles.compareTo(0) == 0) {
            opcode = read(pc++)

            //status.setFlag(Flags.U, true)

            cycles = instructionTable[opcode]!!.cycles

            val additionalCycle1 = instructionTable[opcode]!!.addressingMode()
            val additionalCycle2 = instructionTable[opcode]!!.instruction()

            cycles += (additionalCycle1 and additionalCycle2)

            //status.setFlag(Flags.U, true)
        }

        ++clockCount

        --cycles
    }

    //Executes an instruction at a specific location
    private fun interruptRequest() {

    }

    //Executes an instruction at a specific location, but it cannot be disabled
    private fun nonMaskableInterrupt() {

    }

    //Gets data depending on the current addressing mode
    private fun fetch() {
        if (instructionTable[opcode]!!.addressingMode != this::imp)
            fetched = read(absoluteAddress)
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
    //Addressing modes--------------------------------------------------------------------------------------------------

    //Accumulator
    fun acc(): Int {
        println("Called ACC")
        return 0
    }

    //Absolute
    //Uses a 16-bit address
    private fun abs(): Int {
        val lo = read(pc++)
        val hi = read(pc++)

        absoluteAddress = (hi shl 8) or lo

        return 0
    }

    //Absolute x
    //Absolute with a offset of x
    private fun abx(): Int {
        val lo = read(pc++)
        val hi = read(pc++)

        absoluteAddress = (hi shl 8) or lo
        absoluteAddress += x

        if ((absoluteAddress and 0xFF00) != (hi shl 8)) {
            return 1
        }

        return 0
    }

    //Absolute y
    //Absolute with a offset of yf
    private fun aby(): Int {
        val lo = read(pc++)
        val hi = read(pc++)

        absoluteAddress = (hi shl 8) or lo
        absoluteAddress = (absoluteAddress + y)

        if ((absoluteAddress and 0xFF00) != (hi shl 8)) {
            return 1
        }

        return 0
    }

    //Immediate
    private fun imm(): Int {
        absoluteAddress = pc++

        return 0
    }

    //Implied
    private fun imp(): Int {
        fetched = a

        return 0
    }

    //Indirect
    private fun ind(): Int {
        val loPointer = read(pc++)
        val hiPointer = read(pc++)

        val pointer = ((hiPointer shl 8) or loPointer)

        //simulating a hardware bug
        absoluteAddress = if (loPointer == 0x00FF) {
            (read(pointer and 0xFF00) shl 8) or read(pointer + 0)
        } else {
            (read(pointer + 1) shl 8) or read(pointer + 0)
        }

        return 0
    }

    //Indirect x
    private fun idx(): Int {
        val temp = read(pc++)

        val lo = read((temp + x) and 0x00FF)
        val hi = read((temp + x + 1) and 0x00FF)

        absoluteAddress = ((hi shl 8) or lo)

        return 0
    }

    //Indirect y
    private fun idy(): Int {
        val temp = read(pc++)

        val lo = read(temp and 0x00FF)
        val hi = read((temp + 1) and 0x00FF)

        absoluteAddress = ((hi shl 8) or lo)
        absoluteAddress += y

        if ((absoluteAddress and 0xFF00) != (hi shl 8)) {
            return 1
        }

        return 0
    }

    //Relative
    private fun rel(): Int {
        relativeAddress = read(pc++)

        //checks if number is bigger than 127
        if (relativeAddress and 0x80 != 0) {
            relativeAddress = relativeAddress or 0xFF00
        }

        return 0
    }

    //Zero page
    private fun zpg(): Int {
        absoluteAddress = read(pc++)
        absoluteAddress = absoluteAddress and 0x00FF

        return 0
    }

    //Zero page x
    private fun zpx(): Int {
        absoluteAddress = read(pc++) + x
        absoluteAddress = absoluteAddress and 0x00FF

        return 0
    }

    //Zero page y
    private fun zpy(): Int {
        absoluteAddress = read(pc++) + x
        absoluteAddress = absoluteAddress and 0x00FF

        return 0
    }


    //Opcodes-----------------------------------------------------------------------------------------------------------

    //Undefined
    private fun xxx(): Int {
        return 0
    }

    //And with carry
    private fun adc(): Int {
        fetch()



        return 0
    }

    //And (with accumulator)
    private fun and(): Int {
        return 0
    }

    //Arithmetic shift left
    private fun asl(): Int {
        return 0
    }

    //Branch on carry clear
    private fun bcc(): Int {
        return 0
    }

    //Branch on carry set
    private fun bcs(): Int {
        return 0
    }

    //Branch on equal (zero set)
    private fun beq(): Int {
        return 0
    }

    //Bit test
    private fun bit(): Int {
        return 0
    }

    //Branch on minus (negative set)
    private fun bmi(): Int {
        return 0
    }

    //Branch on not equal (zero set)
    private fun bne(): Int {
        return 0
    }

    //Branch on plus (negative clear)
    private fun bpl(): Int {
        return 0
    }

    //Break / Interrupt
    private fun brk(): Int {
        return 0
    }

    //Branch on overflow clear
    private fun bvc(): Int {
        return 0
    }

    //Branch on overflow set
    private fun bvs(): Int {
        return 0
    }

    //Clear carry
    private fun clc(): Int {
        return 0
    }

    //Clear decimal
    private fun cld(): Int {
        return 0
    }

    //Clear interrupt disable
    private fun cli(): Int {
        return 0
    }

    //Clear overflow
    private fun clv(): Int {
        return 0
    }

    //Compare (with accumulator)
    private fun cmp(): Int {
        return 0
    }

    //Compare with x
    private fun cpx(): Int {
        return 0
    }

    //Compare with y
    private fun cpy(): Int {
        return 0
    }

    //Decrement
    private fun dec(): Int {
        return 0
    }

    //Decrement x
    private fun dex(): Int {
        return 0
    }

    //Decrement y
    private fun dey(): Int {
        return 0
    }

    //Exclusive or (with accumulator)
    private fun eor(): Int {
        return 0
    }

    //Increment
    private fun inc(): Int {
        return 0
    }

    //Increment x
    private fun inx(): Int {
        return 0
    }

    //Increment y
    private fun iny(): Int {
        return 0
    }

    //Jump
    private fun jmp(): Int {
        return 0
    }

    //Jump to subroutine
    private fun jsr(): Int {
        return 0
    }

    //Load accumulator
    private fun lda(): Int {
        return 0
    }

    //Load x
    private fun ldx(): Int {
        return 0
    }

    //Load y
    private fun ldy(): Int {
        return 0
    }

    //Logical shift right
    private fun lsr(): Int {
        return 0
    }

    //No operation
    private fun nop(): Int {
        return 0
    }

    //Or with accumulator
    private fun ora(): Int {
        return 0
    }

    //Push accumulator
    private fun pha(): Int {
        return 0
    }

    //Push processor status
    private fun php(): Int {
        return 0
    }

    //Pull accumulator
    private fun pla(): Int {
        return 0
    }

    //Pull processor status
    private fun plp(): Int {
        return 0
    }

    //Rotate left
    private fun rol(): Int {
        return 0
    }

    //Rotate right
    private fun ror(): Int {
        return 0
    }

    //Return from interrupt
    private fun rti(): Int {
        return 0
    }

    //Return from subroutine
    private fun rts(): Int {
        return 0
    }

    //Subtract with carry
    private fun sbc(): Int {
        return 0
    }

    //Set carry
    private fun sec(): Int {
        return 0
    }

    //Set decimal
    private fun sed(): Int {
        return 0
    }

    //Set interrupt disable
    private fun sei(): Int {
        return 0
    }

    //Store accumulator
    private fun sta(): Int {
        return 0
    }

    //Store x
    private fun stx(): Int {
        return 0
    }

    //Store y
    private fun sty(): Int {
        return 0
    }

    //Transfer accumulator to X
    private fun tax(): Int {
        return 0
    }

    //Transfer accumulator to y
    private fun tay(): Int {
        return 0
    }

    //Transfer stack pointer to x
    private fun tsx(): Int {
        return 0
    }

    //Transfer X to accumulator
    private fun txa(): Int {
        return 0
    }

    //Transfer X to stack pointer
    private fun txs(): Int {
        return 0
    }

    //Transfer Y to accumulator
    private fun tya(): Int {
        return 0
    }

}