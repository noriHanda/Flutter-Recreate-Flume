package framework.render

import common.Offset
import framework.PaintingContext
import framework.geometrics.BoxConstraints

class RenderConstrainedBox(
    private val additionalConstraints: BoxConstraints,
) : RenderProxyBox() {

    override fun layout(constraints: BoxConstraints) {
        size = if (child != null) {
            child!!.layout(additionalConstraints.enforce(constraints))
            child!!.size
        } else {
            additionalConstraints.enforce(constraints).constrain(size)
        }
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        child?.paint(context, offset)
    }
}