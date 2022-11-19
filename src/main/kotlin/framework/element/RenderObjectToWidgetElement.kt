package framework.element

import framework.render.RenderObject
import framework.render.mixin.RenderObjectWithChild
import framework.widget.RenderObjectToWidgetAdapter
import framework.widget.RenderObjectWidget

class RenderObjectToWidgetElement<T : RenderObject>(widget: RenderObjectWidget<T>) : RenderObjectElement<T>(widget) {
    private var child: Element? = null
    override fun mount(parent: Element?) {
        super.mount(parent)
        rebuild()
    }

    private fun rebuild() {
        child = updateChild(child, (widget as RenderObjectToWidgetAdapter).child)
    }

    override fun insertRenderObjectChild(child: RenderObject) {
        (renderObject as RenderObjectWithChild<RenderObject>).child = child
    }
}
