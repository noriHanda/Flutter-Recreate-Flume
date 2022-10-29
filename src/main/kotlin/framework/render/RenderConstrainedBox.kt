package framework.render

import framework.geometrics.BoxConstraints

class RenderConstrainedBox(
    private val additionalConstraints: BoxConstraints,
) : RenderProxyBox() {

    override fun performLayout() {
        size = if (child != null) {
            child!!.layout(additionalConstraints.enforce(constraints))
            child!!.size
        } else {
            additionalConstraints.enforce(constraints).constrain(size)
        }
    }
}
