import cpu.*


private var bus: Bus? = null
private var cpu: CPU? = null

fun main() {
    init()


}

private fun init() {
    cpu = CPU()
    bus = Bus(cpu!!)


}
