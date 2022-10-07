package framework.element

import framework.render.RenderObject
import framework.widget.RenderObjectWidget
import framework.widget.Widget

abstract class RenderObjectElement(
    widget: RenderObjectWidget
) : Element(widget) {
    override var widget: Widget? = super.widget as RenderObjectWidget

    override var renderObject: RenderObject? = null

    override fun mount(parent: Element?) {
        super.mount(parent)
        renderObject = (widget as RenderObjectWidget).createRenderObject()
        attachRenderObject()
    }

    override fun attachRenderObject() {
        val ancestorRenderObjectElement = findAncestorRenderObjectElement()
        ancestorRenderObjectElement?.insertRenderObjectChild(renderObject!!)
    }

    fun findAncestorRenderObjectElement(): RenderObjectElement? {
        var ancestor = parent
        while (ancestor != null && ancestor !is RenderObjectElement) {
            ancestor = ancestor.parent
        }
        return ancestor as RenderObjectElement?
    }

    open fun insertRenderObjectChild(child: RenderObject) {

    }

}