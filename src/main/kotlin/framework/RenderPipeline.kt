package framework

import framework.render.RenderObject
import framework.render.RenderView

class RenderPipeline(private val onNeedVisualUpdate: () -> Unit) {
    val nodesNeedingPaint: MutableList<RenderObject> = mutableListOf()
    val nodesNeedingLayout: MutableList<RenderObject> = mutableListOf()
    var renderView: RenderView? = null
        set(value) {
            value?.attach(this)
            field = value
        }

    fun flushLayout() {
        while (nodesNeedingLayout.isNotEmpty()) {
            // ツリーの上の方を先にやる
            val dirtyNodes = nodesNeedingLayout.sortedBy { it.depth }
            nodesNeedingLayout.clear()
            for (node in dirtyNodes) {
                if (node.needsLayout && node.owner == this) {
                    node.layoutWithoutResize()
                }
            }
        }
    }

    fun flushPaint() {
        val dirtyNodes = nodesNeedingPaint.sortedBy { it.depth }
        nodesNeedingPaint.clear()
        for (node in dirtyNodes) {
            if (node.needsPaint && node.owner == this) {
                PaintingContext.repaintCompositedChild(node)
            }
        }
    }

    fun requestVisualUpdate() {
        onNeedVisualUpdate()
    }
}