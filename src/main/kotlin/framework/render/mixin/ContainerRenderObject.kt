package framework.render.mixin

import framework.RenderPipeline
import framework.render.RenderObject

interface ContainerRenderObject<ChildType : RenderObject> {
    val children: MutableList<ChildType>
    val thisRef: RenderObject

    fun insert(child: ChildType) {
        thisRef.adoptChild(child)
        children.add(child)
    }

    fun visitChildren(visitor: RenderObjectVisitor) {
        for (child in children) {
            visitor(child)
        }
    }

    fun attachChildren(owner: RenderPipeline) {
        for (child in children) {
            child.attach(owner)
        }
    }

    fun redepthChildren(callback: (child: RenderObject) -> Unit) {
        for (child in children) {
            child.let(callback)
        }
    }
}
