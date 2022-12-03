package framework.render

import framework.geometrics.BoxConstraints

class RenderConstrainedBox(
    additionalConstraints: BoxConstraints,
) : RenderProxyBox() {
    var additionalConstraints: BoxConstraints by MarkLayoutProperty(additionalConstraints)
    override fun performLayout() {
        size = if (child != null) {
            child!!.layout(additionalConstraints.enforce(constraints), parentUsesSize = true)
            child!!.size
        } else {
            additionalConstraints.enforce(constraints).constrain(size)
        }
    }
}
