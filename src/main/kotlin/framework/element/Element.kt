package framework.element

import framework.render.RenderObject
import framework.widget.Widget

abstract class Element(
    open var widget: Widget?,
) {
    var parent: Element? = null

    open fun visitChildren(visitor: ElementVisitor) {

    }

    open fun mount(parent: Element?) {
        this.parent = parent
    }

    protected fun updateChild(child: Element?, newWidget: Widget?): Element? {
        // とりあえずchildが来る場合は考えない
        assert(child == null)
        if (newWidget == null) return null
        return inflateWidget(newWidget)
    }

    protected fun inflateWidget(newWidget: Widget): Element {
        val newChild = newWidget.createElement()
        newChild.mount(this)
        return newChild
    }

    protected open fun attachRenderObject() {

    }

    open var renderObject: RenderObject? = null
        get() {
            var result: RenderObject? = null
            fun visit(element: Element) {
                if (element is RenderObjectElement) {
                    result = element.renderObject
                } else {
                    element.visitChildren { visit(it) }
                }
            }
            visit(this)
            return result
        }
        protected set
}

typealias ElementVisitor = (child: Element) -> Unit
