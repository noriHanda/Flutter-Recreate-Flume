package framework.widget

import framework.geometrics.*
import framework.render.RenderFlex

class Flex(
    children: List<Widget> = listOf(),
    val direction: Axis,
    val mainAxisAlignment: MainAxisAlignment = MainAxisAlignment.Start,
    val mainAxisSize: MainAxisSize = MainAxisSize.Max,
    val crossAxisAlignment: CrossAxisAlignment = CrossAxisAlignment.Center,
    val verticalDirection: VerticalDirection = VerticalDirection.Down,
) : MultiChildRenderObjectWidget<RenderFlex>(children) {
    override fun createRenderObject(): RenderFlex {
        return RenderFlex(
            direction,
            mainAxisAlignment,
            mainAxisSize,
            crossAxisAlignment,
            verticalDirection
        )
    }

    override fun updateRenderObject(renderObject: RenderFlex) {
        renderObject.let {
            it.direction = direction
            it.mainAxisAlignment = mainAxisAlignment
            it.mainAxisSize = mainAxisSize
            it.crossAxisAlignment = crossAxisAlignment
            it.verticalDirection = verticalDirection
        }
    }
}
