import tornadofx.*

class Main : View("Nes-Emulator") {
    override val root = borderpane {
        top<TopView>()
        bottom<BottomView>()
    }
}

class TopView: View() {
    override val root = label("Top View")
}

class BottomView: View() {
    override val root = label("Bottom View")
}