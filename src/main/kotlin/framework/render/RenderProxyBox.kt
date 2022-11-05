package framework.render

import common.Offset
import framework.PaintingContext
import framework.RenderPipeline
import framework.render.mixin.RenderObjectWithChild

abstract class RenderProxyBox : RenderBox(), RenderObjectWithChild<RenderBox> {
    override var child: RenderBox? by RenderObjectWithChild.ChildDelegate()
    override fun performLayout() {
        size = if (child != null) {
            child!!.layout(constraints, parentUsesSize = true)
            child!!.size
        } else {
            constraints.smallest
        }
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        child?.let { context.paintChild(it, offset) }
    }

    override fun attach(owner: RenderPipeline) {
        super.attach(owner)
        attachChild(owner)
    }

    override fun visitChildren(visitor: RenderObjectVisitor) {
        super<RenderObjectWithChild>.visitChildren(visitor)
    }

    override fun redepthChildren() {
        super<RenderObjectWithChild>.redepthChildren { redepthChild(it) }
    }
}
