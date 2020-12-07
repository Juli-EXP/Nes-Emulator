package cpu

import Bus
import java.util.stream.IntStream

@ExperimentalUnsignedTypes
class CPU {
    //Other devices
    private lateinit var bus: Bus

    //CPU.CPU registers
    private var a: UByte = 0u           //Accumulator register
    private var x: UByte = 0u           //X register
    private var y: UByte = 0u           //Y register
    private var sp: UByte = 0u          //Stack pointer
    private var pc: UShort = 0u         //Program counter
    private var status: Flag = Flag()   //Status Register

    private var opcode: UByte = 0u          //Current opcode
    private var fetched: UByte = 0u         //Fetched value
    private var cycles: UByte = 0u          //How many cycles are left
    private var absoluteAddress: UShort = 0u //Absolute address
    private val relativeAddress: UShort = 0u //Relative address
    private val clockCount: UInt = 0u       //Total clock count


    //Communication with the bus----------------------------------------------------------------------------------------

    //Connects the CPU.CPU to the bus
    fun connectBus(bus: Bus) {
        this.bus = bus
    }

    //reads from the bus
    private fun read(addr: UShort): UByte {
        return bus.read(addr)
    }

    //writes to the bus
    private fun write(addr: UShort, data: UByte) {
        bus.write(addr, data)
    }


    //CPU functions-----------------------------------------------------------------------------------------------------

    //Resets the CPU
    private fun reset() {

    }

    //Performs one clock cycle
    private fun clockCycle() {

    }

    //Executes an instruction at a specific location
    private fun interruptRequest() {

    }

    //Executes an instruction at a specific location, but it cannot be disabled
    private fun nonMaskableInterrupt() {

    }

    //Gets data depending on the current addressing mode
    private fun fetch(): UByte {
        return 0u
    }


    //Fill instruction list---------------------------------------------------------------------------------------------
    private val instructionTable = mapOf(
        0x00 to Instruction(this::brk, this::imp, 7u),
        0x01 to Instruction(this::ora, this::idx, 6u),
        0x05 to Instruction(this::ora, this::zpg, 3u),
        0x06 to Instruction(this::asl, this::zpg, 5u),
        0x08 to Instruction(this::php, this::imp, 3u),
        0x09 to Instruction(this::ora, this::imm, 2u),
        0x0A to Instruction(this::asl, this::imp, 2u),
        0x0D to Instruction(this::ora, this::abs, 4u),
        0x0E to Instruction(this::asl, this::abs, 6u),

        0x10 to Instruction(this::bpl, this::rel, 2u),
        0x11 to Instruction(this::ora, this::idy, 5u),
        0x15 to Instruction(this::ora, this::zpx, 4u),
        0x16 to Instruction(this::asl, this::zpx, 6u),
        0x18 to Instruction(this::clc, this::imp, 2u),
        0x19 to Instruction(this::ora, this::aby, 4u),
        0x1D to Instruction(this::ora, this::abx),
        0x1E to Instruction(this::asl, this::abx),

        0x20 to Instruction(this::jsr, this::abs),
        0x21 to Instruction(this::and, this::idx),
        0x24 to Instruction(this::bit, this::zpg),
        0x25 to Instruction(this::and, this::zpg),
        0x26 to Instruction(this::rol, this::zpg),
        0x28 to Instruction(this::plp, this::imp),
        0x29 to Instruction(this::and, this::imm),
        0x2A to Instruction(this::rol, this::imp),
        0x2C to Instruction(this::bit, this::abs),
        0x2D to Instruction(this::and, this::abs),
        0x2E to Instruction(this::rol, this::abs),

        0x30 to Instruction(this::bmi, this::rel),
        0x31 to Instruction(this::and, this::idy),
        0x35 to Instruction(this::and, this::zpx),
        0x36 to Instruction(this::rol, this::zpx),
        0x38 to Instruction(this::sec, this::imp),
        0x39 to Instruction(this::and, this::aby),
        0x3D to Instruction(this::and, this::abx),
        0x3E to Instruction(this::rol, this::abx),

        0x40 to Instruction(this::rti, this::imp),
        0x41 to Instruction(this::eor, this::ind),
        0x45 to Instruction(this::eor, this::zpg),
        0x46 to Instruction(this::lsr, this::zpg),
        0x48 to Instruction(this::pha, this::imp),
        0x49 to Instruction(this::eor, this::imm),
        0x4A to Instruction(this::lsr, this::imp),
        0x4C to Instruction(this::jmp, this::abs),
        0x4D to Instruction(this::eor, this::abs),
        0x4E to Instruction(this::lsr, this::abs),

        0x50 to Instruction(this::bvc, this::rel),
        0x51 to Instruction(this::eor, this::idy),
        0x55 to Instruction(this::eor, this::zpx),
        0x56 to Instruction(this::lsr, this::zpx),
        0x58 to Instruction(this::cli, this::imp),
        0x59 to Instruction(this::eor, this::aby),
        0x5D to Instruction(this::eor, this::abx),
        0x5E to Instruction(this::lsr, this::abx),

        0x60 to Instruction(this::rts, this::imp),
        0x61 to Instruction(this::adc, this::idx),
        0x65 to Instruction(this::adc, this::zpg),
        0x66 to Instruction(this::ror, this::zpg),
        0x68 to Instruction(this::pla, this::imp),
        0x69 to Instruction(this::adc, this::imm),
        0x6A to Instruction(this::ror, this::imp),
        0x6C to Instruction(this::jmp, this::ind),
        0x6D to Instruction(this::adc, this::abs),
        0x6E to Instruction(this::ror, this::abs),

        0x70 to Instruction(this::bvs, this::rel),
        0x71 to Instruction(this::adc, this::idy),
        0x75 to Instruction(this::adc, this::zpx),
        0x76 to Instruction(this::ror, this::zpx),
        0x78 to Instruction(this::sei, this::imp),
        0x7D to Instruction(this::adc, this::abx),
        0x7E to Instruction(this::ror, this::abx),

        0x81 to Instruction(this::sta, this::ind),
        0x84 to Instruction(this::sty, this::zpg),
        0x85 to Instruction(this::sta, this::zpg),
        0x86 to Instruction(this::stx, this::zpg),
        0x88 to Instruction(this::dey, this::imp),
        0x8A to Instruction(this::txa, this::imp),
        0x8C to Instruction(this::sty, this::abs),
        0x8D to Instruction(this::sta, this::abs),
        0x8E to Instruction(this::stx, this::abs),

        0x90 to Instruction(this::bcc, this::rel),
        0x91 to Instruction(this::sta, this::idy),
        0x94 to Instruction(this::sty, this::zpx),
        0x95 to Instruction(this::sta, this::zpx),
        0x96 to Instruction(this::stx, this::zpy),
        0x98 to Instruction(this::tya, this::imp),
        0x99 to Instruction(this::sta, this::aby),
        0x9A to Instruction(this::txs, this::imp),
        0x9D to Instruction(this::sta, this::abx),

        0xA0 to Instruction(this::ldy, this::imm),
        0xA1 to Instruction(this::lda, this::idx),
        0xA2 to Instruction(this::ldx, this::imm),
        0xA4 to Instruction(this::ldy, this::zpg),
        0xA5 to Instruction(this::lda, this::zpg),
        0xA6 to Instruction(this::ldx, this::zpg),
        0xA8 to Instruction(this::tay, this::imp),
        0xA9 to Instruction(this::lda, this::imm),
        0xAA to Instruction(this::tax, this::imp),
        0xAC to Instruction(this::ldy, this::abs),
        0xAD to Instruction(this::lda, this::abs),
        0xAE to Instruction(this::ldx, this::abs),

        0xB0 to Instruction(this::bcs, this::rel),
        0xB1 to Instruction(this::lda, this::idy),
        0xB4 to Instruction(this::ldy, this::zpx),
        0xB5 to Instruction(this::lda, this::zpx),
        0xB6 to Instruction(this::ldx, this::zpy),
        0xB8 to Instruction(this::clv, this::imp),
        0xB9 to Instruction(this::lda, this::aby),
        0xBA to Instruction(this::tsx, this::imp),
        0xBC to Instruction(this::ldy, this::abx),
        0xBD to Instruction(this::lda, this::abx),
        0xBE to Instruction(this::ldx, this::aby),

        0xC0 to Instruction(this::cpy, this::imm),
        0xC1 to Instruction(this::cmp, this::idx),
        0xC4 to Instruction(this::cpy, this::zpg),
        0xC5 to Instruction(this::cmp, this::zpg),
        0xC6 to Instruction(this::dec, this::zpg),
        0xC8 to Instruction(this::iny, this::imp),
        0xC9 to Instruction(this::cmp, this::imm),
        0xCA to Instruction(this::dex, this::imp),
        0xCC to Instruction(this::cpy, this::abs),
        0xCD to Instruction(this::cmp, this::abs),
        0xCE to Instruction(this::dec, this::abs),

        0xD0 to Instruction(this::bne, this::rel),
        0xD1 to Instruction(this::cmp, this::idy),
        0xD5 to Instruction(this::cmp, this::zpx),
        0xD6 to Instruction(this::dec, this::zpx),
        0xD8 to Instruction(this::cld, this::imp),
        0xD9 to Instruction(this::cmp, this::aby),
        0xDD to Instruction(this::cmp, this::abx),
        0xDE to Instruction(this::dec, this::abx),

        0xE0 to Instruction(this::cpx, this::imm),
        0xE1 to Instruction(this::sbc, this::ind),
        0xe4 to Instruction(this::cpx, this::zpg),
        0xE5 to Instruction(this::sbc, this::zpg),
        0xE6 to Instruction(this::inc, this::zpg),
        0xE8 to Instruction(this::inx, this::imp),
        0xE9 to Instruction(this::sbc, this::imm),
        0xEA to Instruction(this::nop, this::imp),
        0xEC to Instruction(this::cpx, this::abs),
        0xED to Instruction(this::sbc, this::abs),
        0xEE to Instruction(this::inc,this::abs),

        0xF0 to Instruction(this::beq, this::rel),
        0xF1 to Instruction(this::sbc, this::idy),
        0xF5 to Instruction(this::sbc, this::zpx),
        0xF6 to Instruction(this::inc, this::zpx),
        0xF8 to Instruction(this::sed, this::imp),
        0xF9 to Instruction(this::sbc, this::aby),
        0xFD to Instruction(this::sbc, this::abx),
        0xFE to Instruction(this::inc, this::abx)
        )


    //Here is a usefull link with all the addressing modes and opcodes:
    //https://www.masswerk.at/6502/6502_instruction_set.html
    //Addressing modes--------------------------------------------------------------------------------------------------

    //Accumulator
    fun acc(): UByte {
        println("Called ACC")
        return 0u
    }

    //Absolute
    private fun abs(): UByte {
        return 0u
    }

    //Absolute x
    private fun abx(): UByte {
        return 0u
    }

    //Absolute y
    private fun aby(): UByte {
        return 0u
    }

    //Immediate
    private fun imm(): UByte {
        return 0u
    }

    //Implicit
    private fun imp(): UByte {
        return 0u
    }

    //Indirect
    private fun ind(): UByte {
        return 0u
    }

    //Indirect x
    private fun idx(): UByte {
        return 0u
    }

    //Indirect y
    private fun idy(): UByte {
        return 0u
    }

    //Relative
    private fun rel(): UByte {
        return 0u
    }

    //Zero page
    private fun zpg(): UByte {
        return 0u
    }

    //Zero page x
    private fun zpx(): UByte {
        return 0u
    }

    //Zero page y
    private fun zpy(): UByte {
        return 0u
    }


    //Opcodes-----------------------------------------------------------------------------------------------------------

    //Undefined
    private fun xxx(): UByte {
        return 0u
    }

    //And with carry
    private fun adc(): UByte {
        return 0u
    }

    //And (with accumulator)
    private fun and(): UByte {
        return 0u
    }

    //Arithmetic shift left
    private fun asl(): UByte {
        return 0u
    }

    //Branch on carry clear
    private fun bcc(): UByte {
        return 0u
    }

    //Branch on carry set
    private fun bcs(): UByte {
        return 0u
    }

    //Branch on equal (zero set)
    private fun beq(): UByte {
        return 0u
    }

    //Bit test
    private fun bit(): UByte {
        return 0u
    }

    //Branch on minus (negative set)
    private fun bmi(): UByte {
        return 0u
    }

    //Branch on not equal (zero set)
    private fun bne(): UByte {
        return 0u
    }

    //Branch on plus (negative clear)
    private fun bpl(): UByte {
        return 0u
    }

    //Break / Interrupt
    private fun brk(): UByte {
        return 0u
    }

    //Branch on overflow clear
    private fun bvc(): UByte {
        return 0u
    }

    //Branch on overflow set
    private fun bvs(): UByte {
        return 0u
    }

    //Clear carry
    private fun clc(): UByte {
        return 0u
    }

    //Clear decimal
    private fun cld(): UByte {
        return 0u
    }

    //Clear interrupt disable
    private fun cli(): UByte {
        return 0u
    }

    //Clear overflow
    private fun clv(): UByte {
        return 0u
    }

    //Compare (with accumulator)
    private fun cmp(): UByte {
        return 0u
    }

    //Compare with x
    private fun cpx(): UByte {
        return 0u
    }

    //Compare with y
    private fun cpy(): UByte {
        return 0u
    }

    //Decrement
    private fun dec(): UByte {
        return 0u
    }

    //Decrement x
    private fun dex(): UByte {
        return 0u
    }

    //Decrement y
    private fun dey(): UByte {
        return 0u
    }

    //Exclusive or (with accumulator)
    private fun eor(): UByte {
        return 0u
    }

    //Increment
    private fun inc(): UByte {
        return 0u
    }

    //Increment x
    private fun inx(): UByte {
        return 0u
    }

    //Increment y
    private fun iny(): UByte {
        return 0u
    }

    //Jump
    private fun jmp(): UByte {
        return 0u
    }

    //Jump to subroutine
    private fun jsr(): UByte {
        return 0u
    }

    //Load accumulator
    private fun lda(): UByte {
        return 0u
    }

    //Load x
    private fun ldx(): UByte {
        return 0u
    }

    //Load y
    private fun ldy(): UByte {
        return 0u
    }

    //Logical shift right
    private fun lsr(): UByte {
        return 0u
    }

    //No operation
    private fun nop(): UByte {
        return 0u
    }

    //Or with accumulator
    private fun ora(): UByte {
        return 0u
    }

    //Push accumulator
    private fun pha(): UByte {
        return 0u
    }

    //Push processor status
    private fun php(): UByte {
        return 0u
    }

    //Pull accumulator
    private fun pla(): UByte {
        return 0u
    }

    //Pull processor status
    private fun plp(): UByte {
        return 0u
    }

    //Rotate left
    private fun rol(): UByte {
        return 0u
    }

    //Rotate right
    private fun ror(): UByte {
        return 0u
    }

    //Return from interrupt
    private fun rti(): UByte {
        return 0u
    }

    //Return from subroutine
    private fun rts(): UByte {
        return 0u
    }

    //Subtract with carry
    private fun sbc(): UByte {
        return 0u
    }

    //Set carry
    private fun sec(): UByte {
        return 0u
    }

    //Set decimal
    private fun sed(): UByte {
        return 0u
    }

    //Set interrupt disable
    private fun sei(): UByte {
        return 0u
    }

    //Store accumulator
    private fun sta(): UByte {
        return 0u
    }

    //Store x
    private fun stx(): UByte {
        return 0u
    }

    //Store y
    private fun sty(): UByte {
        return 0u
    }

    //Transfer accumulator to X
    private fun tax(): UByte {
        return 0u
    }

    //Transfer accumulator to y
    private fun tay(): UByte {
        return 0u
    }

    //Transfer stack pointer to x
    private fun tsx(): UByte {
        return 0u
    }

    //Transfer X to accumulator
    private fun txa(): UByte {
        return 0u
    }

    //Transfer X to stack pointer
    private fun txs(): UByte {
        return 0u
    }

    //Transfer Y to accumulator
    private fun tya(): UByte {
        return 0u
    }

}