package framework.widget.paint

import common.Clip
import framework.painting.BorderRadius
import framework.render.RenderObject
import framework.render.clip.CustomClipper
import framework.render.clip.RenderClipRRect
import framework.widget.SingleChildRenderObjectWidget
import framework.widget.Widget
import org.jetbrains.skia.RRect

class ClipRRect(
    val clipper: CustomClipper<RRect>? = null,
    val clipBehavior: Clip = Clip.AntiAlias,
    val borderRadius: BorderRadius = BorderRadius.zero,
    child: Widget? = null,
) : SingleChildRenderObjectWidget(child) {
    override fun createRenderObject(): RenderObject {
        return RenderClipRRect(clipper = clipper, clipBehavior = clipBehavior, borderRadius = borderRadius)
    }
}