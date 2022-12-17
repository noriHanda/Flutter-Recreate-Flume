package framework.element

import framework.render.RenderObject
import framework.widget.Widget

abstract class Element(
    widget: Widget,
) : Comparable<Element>, BuildContext {
    var parent: Element? = null
    var depth: Int = 0
    override var owner: BuildOwner? = null
    var dirty: Boolean = true
    var inDirtyList: Boolean = false

    final override var widget: Widget = widget
        private set

    open fun visitChildren(visitor: ElementVisitor) {

    }

    open fun mount(parent: Element?) {
        this.parent = parent
        depth = if (parent != null) parent.depth + 1 else 1
        parent?.let {
            owner = it.owner
        }
    }

    fun updateChild(child: Element?, newWidget: Widget?): Element? {
        if (newWidget == null) {
            if (child != null) {
                deactivateChild(child)
            }
            return null
        }
        if (child != null) {
            val newChild: Element
            if (child.widget == newWidget) {
                newChild = child
            } else if (Widget.canUpdate(child.widget, newWidget)) {
                child.update(newWidget)
                newChild = child
            } else {
                deactivateChild(child)
                newChild = inflateWidget(newWidget)
            }
            return newChild
        } else {
            return inflateWidget(newWidget)
        }
    }

    open fun update(newWidget: Widget) {
        widget = newWidget
    }

    protected fun inflateWidget(newWidget: Widget): Element {
        val newChild = newWidget.createElement()
        newChild.mount(this)
        return newChild
    }

    open fun attachRenderObject() {}

    open fun detachRenderObject() {
        visitChildren {
            it.detachRenderObject()
        }
    }

    protected fun deactivateChild(child: Element) {
        child.parent = null
        child.detachRenderObject()
    }

    open var renderObject: RenderObject? = null
        get() {
            var result: RenderObject? = null
            fun visit(element: Element) {
                if (element is RenderObjectElement<*>) {
                    result = element.renderObject
                } else {
                    element.visitChildren { visit(it) }
                }
            }
            visit(this)
            return result
        }
        protected set

    override operator fun compareTo(other: Element): Int {
        when {
            depth < other.depth -> return -1
            other.depth < depth -> return 1
            !dirty && other.dirty -> return -1
            dirty && !other.dirty -> return 1
        }
        return 0
    }

    open fun didChangeDependencies() {
        markNeedsBuild()
    }

    fun markNeedsBuild() {
        if (dirty) return
        dirty = true
        owner!!.scheduleBuildFor(this)
    }

    fun rebuild() {
        performRebuild()
    }

    abstract fun performRebuild()
}

typealias ElementVisitor = (child: Element) -> Unit
