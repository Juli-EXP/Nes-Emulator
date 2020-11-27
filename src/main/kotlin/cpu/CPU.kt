package cpu

import Bus

class CPU {
    //Other devices
    private lateinit var bus: Bus

    //CPU.CPU registers
    private var a: UByte = 0u           //Accumulator register
    private var y: UByte = 0u           //X register
    private var x: UByte = 0u           //Y register
    private var sp: UByte = 0u          //Stack pointer
    private var pc: UShort = 0u         //Program counter
    private var status: Flag = Flag()   //Status Register

    private var opcode: UByte = 0u          //Current opcode
    private var fetched: UByte = 0u         //Fetched value
    private var cycles: UByte = 0u          //How many cycles are left
    private var absoluteAddress: UShort = 0u //absolute address
    private val relativeAddress: UShort = 0u //relative address


    //Communication with the bus----------------------------------------------------------------------------------------

    //connects the CPU.CPU to the bus
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

    //External event functions------------------------------------------------------------------------------------------

    //Performs one clock cycle
    fun clockCycle() {

    }

    //Forces CPU into known state
    fun resetInterrupt() {

    }

    //Executes an instruction at a specific location
    fun interruptRequest() {

    }

    //Executes an instruction at a specific location, but it cannot be disabled
    fun nonMaskableInterrupt() {

    }

    //fetch etc.--------------------------------------------------------------------------------------------------------


    //fill instruction list---------------------------------------------------------------------------------------------
    val instructionTable = mapOf(
            0x00 to Instruction("", this::ABS, this::ABS, 3u)
    )

    //Addressing modes--------------------------------------------------------------------------------------------------

    //Accumulator
    private fun ACC(): UByte {
        println("Servus: $a")
        return 0u
    }

    //Implicit
    private fun IMP(): UByte {
        return 0u
    }

    //Immediate
    private fun IMM(): UByte {
        return 0u
    }

    //Zero page
    private fun ZPG(): UByte {
        return 0u
    }

    //Absolute
    private fun ABS(): UByte {
        return 0u
    }

    //Relative
    private fun REL(): UByte {
        return 0u
    }

    //Indirect
    private fun IND(): UByte {
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

    //Absolute x
    private fun ABX(): UByte {
        return 0u
    }

    //Absolute y
    private fun ABY(): UByte {
        return 0u
    }

    //Indirect x
    private fun INX(): UByte {
        return 0u
    }

    //Indirect y
    private fun INY(): UByte {
        return 0u
    }

    //Opcodes-----------------------------------------------------------------------------------------------------------

    //Undefined
    private fun XXXx(): UByte {
        return 0u
    }

    //
    fun ADC() : UByte{
        return 0u
    }

    //
    fun AND() : UByte{
        return 0u
    }

    //
    fun ASL() : UByte{
        return 0u
    }

    //
    fun BCC() : UByte{
        return 0u
    }

    //
    fun BCS() : UByte{
        return 0u
    }

    //
    fun BEQ() : UByte{
        return 0u
    }

    //
    fun BIT() : UByte{
        return 0u
    }

    //
    fun BMI() : UByte{
        return 0u
    }

    //
    fun BNE() : UByte{
        return 0u
    }

    //
    fun BPL() : UByte{
        return 0u
    }

    //
    fun BRK() : UByte{
        return 0u
    }

    //
    fun BVC() : UByte{
        return 0u
    }

    //
    fun CLC() : UByte{
        return 0u
    }

    //
    fun CLD() : UByte{
        return 0u
    }

    //
    fun CLI() : UByte{
        return 0u
    }

    //
    fun CLV() : UByte{
        return 0u
    }

    //
    fun CMP() : UByte{
        return 0u
    }

    //
    fun CPX() : UByte{
        return 0u
    }

    //
    fun CPY() : UByte{
        return 0u
    }

    //
    fun DEC() : UByte{
        return 0u
    }

    /*
    fun DEX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

    //
    fun XXX() : UByte{
        return 0u
    }

     */

}