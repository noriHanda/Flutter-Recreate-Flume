package framework.render.mixin

import framework.RenderPipeline
import framework.render.RenderObject
import kotlin.reflect.KProperty

interface RenderObjectWithChild<ChildType : RenderObject> {
    var child: ChildType?

    fun attachChild(owner: RenderPipeline) {
        child?.attach(owner)
    }

    fun detachChild() {
        child?.detach()
    }

    class ChildDelegate<ChildType : RenderObject> {
        var child: ChildType? = null
        operator fun getValue(thisRef: RenderObject, property: KProperty<*>): ChildType? {
            return child
        }

        operator fun setValue(thisRef: RenderObject, property: KProperty<*>, value: ChildType?) {
            if (child != null) {
                thisRef.dropChild(child!!)
            }
            child = value
            child?.let {
                thisRef.adoptChild(it)
            }
        }
    }

    /**
     * Implement先の[RenderObject.visitChildren]で必ず呼ぶ
     */
    fun visitChildren(visitor: RenderObjectVisitor) {
        child?.let(visitor)
    }

    fun redepthChildren(callback: (child: RenderObject) -> Unit) {
        child?.let(callback)
    }
}

typealias RenderObjectVisitor = (child: RenderObject) -> Unit
