import common.KeyEvent
import common.Offset
import common.Size
import engine.runFlume
import framework.WidgetsFlumeBinding
import framework.element.BuildContext
import framework.geometrics.Axis
import framework.geometrics.MainAxisSize
import framework.painting.BorderRadius
import framework.render.TextSpan
import framework.render.clip.CustomClipper
import framework.runApp
import framework.widget.*
import framework.widget.paint.ClipOval
import framework.widget.paint.ClipPath
import framework.widget.paint.ClipRRect
import org.jetbrains.skia.Path
import org.jetbrains.skia.paragraph.TextStyle

fun main() {
    runFlume(appMain = { appMain() })
}

fun appMain() {
    val propagator = Propagator<KeyEvent>()
    runApp(MyPage(propagator))
    WidgetsFlumeBinding.setOnKeyEventCallback {
        propagator.fire(it)
    }
}

class Propagator<T> {
    var listener: ((T) -> Unit)? = null

    fun fire(data: T) {
        listener?.invoke(data)
    }
}

class MyPage(private val propagator: Propagator<KeyEvent>) : StatelessWidget() {
    override fun build(context: BuildContext): Widget {
        return MyStatefulView(propagator)
    }
}

class MyStatefulView(val propagator: Propagator<KeyEvent>) : StatefulWidget() {
    override fun createState(): State<*> = MyState()
}

class MyState : State<MyStatefulView>() {
    private val darken = 0xFF9E9E9E.toInt()
    private var phase: LightPhase = LightPhase.All

    override fun didChangeDependencies() {
        super.didChangeDependencies()
        widget.propagator.listener = {
            setState {
                phase = when (it.character) {
                    "r" -> LightPhase.Red
                    "y" -> LightPhase.Yellow
                    "g" -> LightPhase.Green
                    "a" -> LightPhase.All
                    else -> phase
                }
            }
        }
    }

    override fun build(context: BuildContext): Widget {
        return Align(
            child = Flex(
                mainAxisSize = MainAxisSize.Min,
                direction = Axis.Vertical,
                children = listOf(
                    ClipPath(
                        clipper = ArcClipper(),
                        child = SizedBox(
                            width = 100.0, height = 100.0,
                            child = ColoredBox(
                                color = if (phase in listOf(LightPhase.Red, LightPhase.All)) 0xFFF44336.toInt()
                                else darken
                            )
                        )
                    ),
                    ClipRRect(
                        borderRadius = BorderRadius.circular(20.0),
                        child = SizedBox(
                            width = 100.0, height = 100.0,
                            child = ColoredBox(
                                color = if (phase in listOf(LightPhase.Yellow, LightPhase.All)) 0xFFFFEB3B.toInt()
                                else darken
                            )
                        )
                    ),
                    ClipOval(
                        child = SizedBox(
                            width = 100.0, height = 100.0,
                            child = ColoredBox(
                                color = if (phase in listOf(LightPhase.Green, LightPhase.All)) 0xFF4CAF50.toInt()
                                else darken
                            )
                        )
                    ),
                    RichText(
                        TextSpan(
                            "???????????????",
                            textStyle = TextStyle().apply {
                                color = 0xFFFF0000.toInt()
                                fontSize = 50f
                            }
                        )
                    )
                )
            )
        )
    }
}


enum class LightPhase {
    Red, Green, Yellow, All
}


class ArcClipper : CustomClipper<Path>() {
    override fun getClip(size: Size): Path {
        return Path().apply {
            lineTo(0f, size.height.toFloat() - 30f)

            val firstControlPoint = Offset(size.width / 4, size.height)
            val firstPoint = Offset(size.width / 2, size.height)
            quadTo(
                firstControlPoint.dx.toFloat(),
                firstControlPoint.dy.toFloat(),
                firstPoint.dx.toFloat(),
                firstPoint.dy.toFloat()
            )

            val secondControlPoint = Offset(size.width - size.width / 4, size.height)
            val secondPoint = Offset(size.width, size.height - 30)
            quadTo(
                secondControlPoint.dx.toFloat(),
                secondControlPoint.dy.toFloat(),
                secondPoint.dx.toFloat(),
                secondPoint.dy.toFloat()
            )
            lineTo(size.width.toFloat(), 0f)
            closePath()
        }
    }

    override fun shouldReclip(oldClipper: CustomClipper<Path>): Boolean = false
}
