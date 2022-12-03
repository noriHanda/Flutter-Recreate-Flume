import engine.runFlume
import framework.WidgetsFlumeBinding
import framework.runApp
import framework.widget.Align
import framework.widget.ColoredBox
import framework.widget.SizedBox
import framework.widget.Widget

fun main() {
    runFlume(appMain = { appMain() })
}

fun appMain() {
    runApp(createWidgetTree())
    WidgetsFlumeBinding.setOnKeyEventCallback {
        when (it.character) {
            "r" -> runApp(createWidgetTree(LightPhase.Red))
            "y" -> runApp(createWidgetTree(LightPhase.Yellow))
            "g" -> runApp(createWidgetTree(LightPhase.Green))
            "a" -> runApp(createWidgetTree(LightPhase.All))
        }
    }
}

enum class LightPhase {
    Red, Green, Yellow, All
}

fun createWidgetTree(phase: LightPhase = LightPhase.All): Widget {
    val darken = 0xFF9E9E9E.toInt()
    return Align(
        child = SizedBox(
            width = 300.0, height = 300.0,
            child = ColoredBox(
                color = if (phase in listOf(LightPhase.Red, LightPhase.All)) 0xFFF44336.toInt() else darken
            )
        )
    )
}