import common.*
import engine.GLView
import engine.TaskRunner
import engine.TaskRunners
import framework.RenderPipeline
import framework.render.RenderView
import framework.render.clip.CustomClipper
import framework.widget.*
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Path
import org.jetbrains.skia.PictureRecorder
import org.jetbrains.skia.Rect
import org.lwjgl.glfw.GLFW.GLFW_KEY_M
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import kotlin.random.Random

fun main() {
    val width = 640
    val height = 480

    val taskRunners = TaskRunners(rasterTaskRunner = TaskRunner(), uiTaskRunner = TaskRunner())

    val glView = GLView(width, height)

    val renderPipeline = RenderPipeline().apply {
        renderView = RenderView(width.toDouble(), height.toDouble())
    }

    val shell = Shell(taskRunners, glView, null, renderPipeline, width, height)

    shell.initRasterThread()

    shell.drawFrame()

    var keyPressed = false

    glView.setKeyCallback { _, key, _, action, _ ->
        if (key == GLFW_KEY_M && action == GLFW_PRESS) {
            keyPressed = true
        }
    }

    while (!shell.glView.windowShouldClose()) {
        if (keyPressed) {
            keyPressed = false
            RenderObjectToWidgetAdapter(
                createWidgetTree(),
                renderPipeline.renderView!!
            ).attachToRenderTree()
            shell.drawFrame()
        }
        shell.glView.pollEvents()
    }
    shell.taskRunners.terminateAll()
}

fun createWidgetTree(): Widget {
    return Align(
        child = SizedBox(
            width = 100.0, height = 100.0,
            child = ColoredBox(
                color = 0xFFFF0000.toInt()
            )
        )
    )
}

fun createRandomTree(width: Float, height: Float): LayerTree {
    val root = ContainerLayer()
    val rect = Rect.makeXYWH(0f, 0f, width, height)
    val leaf = PictureLayer()
    val recorder = PictureRecorder()
    val canvas = recorder.beginRecording(rect)

    val paint = Paint().apply { color = 0xFFFF0000.toInt() }

    val randomX = Random.nextFloat() * width
    val randomY = Random.nextFloat() * height

    canvas.drawCircle(randomX, randomY, 40f, paint)

    leaf.picture = recorder.finishRecordingAsPicture()
    root.children.add(leaf)

    val opacity = OpacityLayer(alpha = 150)

    val opacityPicture = PictureLayer()
    val opacityRecorder = PictureRecorder()
    val opacityCanvas = opacityRecorder.beginRecording(rect)
    opacityCanvas.drawCircle(0f, 0f, 60f, paint)
    opacityPicture.picture = opacityRecorder.finishRecordingAsPicture()
    opacity.children.add(opacityPicture)

    root.children.add(opacity)

    return LayerTree().apply {
        rootLayer = root
    }
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
