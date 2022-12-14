package framework

import common.ContainerLayer
import common.KeyEvent
import framework.element.BuildOwner
import framework.element.Element
import framework.element.RenderObjectToWidgetElement
import framework.render.RenderView
import framework.widget.RenderObjectToWidgetAdapter
import framework.widget.Widget

object WidgetsFlumeBinding : WidgetsBinding {
    lateinit var pipeline: RenderPipeline
    lateinit var engine: Engine
    var renderViewElement: Element? = null
    var initialized = false
    var engineConnected = false
    var buildOwner: BuildOwner = BuildOwner { handleBuildScheduled() }

    private fun handleBuildScheduled() {
        ensureVisualUpdate()
    }

    override fun connectToEngine(engine: Engine) {
        this.engine = engine
        engineConnected = true
    }

    fun ensureInitialized() {
        if (!engineConnected) {
            throw Exception("tried to initialize before connecting to engine.")
        }
        if (initialized) return
        initialized = true
        val configuration = engine.viewConfiguration
        pipeline = RenderPipeline(onNeedVisualUpdate = {
            ensureVisualUpdate()
        }).apply {
            renderView = RenderView(configuration)
            renderView!!.prepareInitialFrame()
        }
    }

    fun ensureVisualUpdate() {
        engine.scheduleFrame()
    }

    fun attachRootWidget(rootWidget: Widget) {
        val isBootstrapFrame = renderViewElement == null
        renderViewElement =
            RenderObjectToWidgetAdapter(rootWidget, pipeline.renderView!!).attachToRenderTree(
                buildOwner,
                renderViewElement as RenderObjectToWidgetElement<*>?
            )
        if (isBootstrapFrame) {
            ensureVisualUpdate()
        }
    }

    override fun beginFrame() {
        if (!initialized) return
        drawFrame()
    }


    fun drawFrame() {
        // WidgetsBinding.drawFrame
        if (renderViewElement != null) {
            buildOwner.buildScope()
        }

        // RendererBinding.drawFrame
        pipeline.flushLayout()
        pipeline.flushPaint()
        engine.render(pipeline.renderView!!.layer as ContainerLayer)
    }


    var keyEventListener: ((KeyEvent) -> Unit)? = null
    fun setOnKeyEventCallback(listener: (KeyEvent) -> Unit) {
        keyEventListener = listener
    }

    override fun handleKeyEvent(event: KeyEvent) {
        keyEventListener?.invoke(event)
    }
}
