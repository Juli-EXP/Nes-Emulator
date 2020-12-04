package cpu

import Bus

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
        0x00 to Instruction("", this::BRK, this::IMP, 7u),
        0x01 to Instruction("", this::ORA, this::IDX, 6u),
        0x05 to Instruction("", this::ORA, this::ZPG, 3u),
        0x06 to Instruction("", this::ASL, this::ZPG, 5u),
        0x08 to Instruction("", this::PHP, this::IMP, 3u),
        0x09 to Instruction("", this::ORA, this::IMM, 2u),
        0x0A to Instruction("", this::ASL, this::IMP, 2u),

    )


    //Here is a usefull link with all the addressing modes and opcodes:
    //https://www.masswerk.at/6502/6502_instruction_set.html
    //Addressing modes--------------------------------------------------------------------------------------------------

    //Accumulator
    private fun ACC(): UByte {
        //TODO check later
        return 0u
    }

    //Absolute
    private fun ABS(): UByte {

        return 0u
    }

    //Absolute x
    private fun ABX(): UByte {
        return 0u
    }

    //Absolute y
    private fun ABY(): UByte {
        return 0u
    }

    //Immediate
    private fun IMM(): UByte {
        return 0u
    }

    //Implicit
    private fun IMP(): UByte {
        return 0u
    }

    //Indirect
    private fun IND(): UByte {
        return 0u
    }

    //Indirect x
    private fun IDX(): UByte {
        return 0u
    }

    //Indirect y
    private fun IDY(): UByte {
        return 0u
    }

    //Relative
    private fun REL(): UByte {
        return 0u
    }

    //Zero page
    private fun ZPG(): UByte {
        return 0u
    }

    //Zero page x
    private fun ZPX(): UByte {
        return 0u
    }

    //Zero page y
    private fun ZPY(): UByte {
        return 0u
    }


    //Opcodes-----------------------------------------------------------------------------------------------------------

    //Undefined
    private fun XXXx(): UByte {
        return 0u
    }

    //And with carry
    private fun ADC(): UByte {
        return 0u
    }

    //And (with accumulator)
    private fun AND(): UByte {
        return 0u
    }

    //Arithmetic shift left
    private fun ASL(): UByte {
        return 0u
    }

    //Branch on carry clear
    private fun BCC(): UByte {
        return 0u
    }

    //Branch on carry set
    private fun BCS(): UByte {
        return 0u
    }

    //Branch on equal (zero set)
    private fun BEQ(): UByte {
        return 0u
    }

    //Bit test
    private fun BIT(): UByte {
        return 0u
    }

    //Branch on minus (negative set)
    private fun BMI(): UByte {
        return 0u
    }

    //Branch on not equal (zero set)
    private fun BNE(): UByte {
        return 0u
    }

    //Branch on plus (negative clear)
    private fun BPL(): UByte {
        return 0u
    }

    //Break / Interrupt
    private fun BRK(): UByte {
        return 0u
    }

    //Branch on overflow clear
    private fun BVC(): UByte {
        return 0u
    }

    //Clear carry
    private fun CLC(): UByte {
        return 0u
    }

    //Clear decimal
    private fun CLD(): UByte {
        return 0u
    }

    //Clear interrupt disable
    private fun CLI(): UByte {
        return 0u
    }

    //Clear overflow
    private fun CLV(): UByte {
        return 0u
    }

    //Compare (with accumulator)
    private fun CMP(): UByte {
        return 0u
    }

    //Compare with x
    private fun CPX(): UByte {
        return 0u
    }

    //Compare with y
    private fun CPY(): UByte {
        return 0u
    }

    //Decrement
    private fun DEC(): UByte {
        return 0u
    }

    //Decrement x
    private fun DEX(): UByte {
        return 0u
    }

    //Decrement y
    private fun DEY(): UByte {
        return 0u
    }

    //Exclusive or (with accumulator)
    private fun EOR(): UByte {
        return 0u
    }

    //Increment
    private fun INC(): UByte {
        return 0u
    }

    //Increment x
    private fun INX(): UByte {
        return 0u
    }

    //Increment y
    private fun INY(): UByte {
        return 0u
    }

    //Jump
    private fun JMP(): UByte {
        return 0u
    }

    //Jump to subroutine
    private fun JSR(): UByte {
        return 0u
    }

    //Load accumulator
    private fun LDA(): UByte {
        return 0u
    }

    //Load x
    private fun LDX(): UByte {
        return 0u
    }

    //Load y
    private fun LDY(): UByte {
        return 0u
    }

    //Logical shift right
    private fun LSR(): UByte {
        return 0u
    }

    //No operation
    private fun NOP(): UByte {
        return 0u
    }

    //Or with accumulator
    private fun ORA(): UByte {
        return 0u
    }

    //Push accumulator
    private fun PHA(): UByte {
        return 0u
    }

    //Push processor status
    private fun PHP(): UByte {
        return 0u
    }

    //Pull accumulator
    private fun PLA(): UByte {
        return 0u
    }

    //Pull processor status
    private fun PLP(): UByte {
        return 0u
    }

    //Rotate left
    private fun ROL(): UByte {
        return 0u
    }

    //Rotate right
    private fun ROR(): UByte {
        return 0u
    }

    //Return from interrupt
    private fun RTI(): UByte {
        return 0u
    }

    //Return from subroutine
    private fun RTS(): UByte {
        return 0u
    }

    //Subtract with carry
    private fun SBC(): UByte {
        return 0u
    }

    //Set carry
    private fun SEC(): UByte {
        return 0u
    }

    //Set decimal
    private fun SED(): UByte {
        return 0u
    }

    //Set interrupt disable
    private fun SEI(): UByte {
        return 0u
    }

    //Store accumulator
    private fun STA(): UByte {
        return 0u
    }

    //Store x
    private fun STX(): UByte {
        return 0u
    }

    //Store y
    private fun STY(): UByte {
        return 0u
    }

    //Transfer accumulator to X
    private fun TAX(): UByte {
        return 0u
    }

    //Transfer accumulator to y
    private fun TAY(): UByte {
        return 0u
    }

    //Transfer stack pointer to x
    private fun TSX(): UByte {
        return 0u
    }

    //Transfer X to accumulator
    private fun TXA(): UByte {
        return 0u
    }

    //Transfer X to stack pointer
    private fun TXS(): UByte {
        return 0u
    }

    //Transfer Y to accumulator
    private fun TYA(): UByte {
        return 0u
    }

}