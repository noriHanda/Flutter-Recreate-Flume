package framework.widget

import framework.render.RenderParagraph
import framework.render.TextSpan

class RichText(
    val text: TextSpan,
) : SingleChildRenderObjectWidget<RenderParagraph>(child = null) {
    override fun createRenderObject(): RenderParagraph {
        return RenderParagraph(
            text
        )
    }

    override fun updateRenderObject(renderObject: RenderParagraph) {
        renderObject.text = text
    }
}