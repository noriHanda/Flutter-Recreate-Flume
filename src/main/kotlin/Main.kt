import org.jetbrains.skia.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11

fun main(args: Array<String>) {
    val width = 640
    val height = 480

    glfwInit()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
    val windowHandle = glfwCreateWindow(width, height, "Flume", 0, 0)
    glfwMakeContextCurrent(windowHandle)
    glfwSwapInterval(1)
    glfwShowWindow(windowHandle)


    GL.createCapabilities()

    val context = DirectContext.makeGL()

    val fbId = GL11.glGetInteger(0x8CA6)
    val renderTarget =
        BackendRenderTarget.makeGL(width, height, 0, 8, fbId, FramebufferFormat.GR_GL_RGBA8)

    val surface = Surface.makeFromBackendRenderTarget(context,
        renderTarget,
        SurfaceOrigin.BOTTOM_LEFT,
        SurfaceColorFormat.RGBA_8888,
        ColorSpace.sRGB)!!

    val canvas = surface.canvas

    while (!glfwWindowShouldClose(windowHandle)) {

        val paintGreen = Paint().apply { color = 0xFF8BC34A.toInt() }
        canvas.drawCircle(100f, 100f, 40f, paintGreen)

        val paintYellow = Paint().apply { color = 0xFFFFEB3B.toInt() }
        canvas.drawCircle(200f, 100f, 40f, paintYellow)

        val paintRed = Paint().apply { color = 0xFFF44336.toInt() }
        canvas.drawCircle(300f, 100f, 40f, paintRed)

        context.flush()
        glfwSwapBuffers(windowHandle)
        glfwPollEvents()
    }

}