package framework.render

import common.Offset
import common.Size
import framework.PaintingContext
import framework.RenderPipeline
import framework.geometrics.*
import framework.render.mixin.ContainerRenderObject
import kotlin.math.max

class RenderFlex(
    direction: Axis = Axis.Vertical,
    mainAxisAlignment: MainAxisAlignment = MainAxisAlignment.Start,
    mainAxisSize: MainAxisSize = MainAxisSize.Max,
    crossAxisAlignment: CrossAxisAlignment = CrossAxisAlignment.Center,
    verticalDirection: VerticalDirection = VerticalDirection.Down,
) : RenderBox(), ContainerRenderObject<RenderBox> {
    var direction: Axis by MarkLayoutProperty(direction)
    var mainAxisAlignment: MainAxisAlignment by MarkLayoutProperty(mainAxisAlignment)
    var mainAxisSize: MainAxisSize by MarkLayoutProperty(mainAxisSize)
    var crossAxisAlignment: CrossAxisAlignment by MarkLayoutProperty(crossAxisAlignment)
    var verticalDirection: VerticalDirection by MarkLayoutProperty(verticalDirection)
    
    override val children: MutableList<RenderBox> = mutableListOf()
    override val thisRef: RenderObject = this

    private fun getMainSize(size: Size): Double {
        return when (direction) {
            Axis.Horizontal -> size.width
            Axis.Vertical -> size.height
        }
    }

    private fun getCrossSize(size: Size): Double {
        return when (direction) {
            Axis.Horizontal -> size.height
            Axis.Vertical -> size.width
        }
    }

    private fun startIsTopLeft(direction: Axis, verticalDirection: VerticalDirection): Boolean? {
        return when (direction) {
            Axis.Horizontal -> null
            Axis.Vertical -> {
                when (verticalDirection) {
                    VerticalDirection.Down -> true
                    VerticalDirection.Up -> false
                }
            }
        }
    }

    override fun performLayout() {
        val maxMainSize =
            if (direction == Axis.Horizontal) constraints.maxWidth else constraints.maxHeight
        val canFlex = maxMainSize < Double.POSITIVE_INFINITY
        var crossSize = 0.0 // ?????????????????????????????????????????????
        var allocatedSize = 0.0 // ??????????????????????????????????????????
        for (child in children) {
            val innerConstraints =
                if (crossAxisAlignment == CrossAxisAlignment.Stretch) { // stretch ????????????????????????????????????????????????
                    when (direction) {
                        Axis.Horizontal -> BoxConstraints.tightFor(height = constraints.maxHeight, width = null)
                        Axis.Vertical -> BoxConstraints.tightFor(width = constraints.maxWidth, height = null)
                    }
                } else { // ??????????????????????????????????????????????????????
                    when (direction) {
                        Axis.Horizontal -> BoxConstraints(maxHeight = constraints.maxHeight)
                        Axis.Vertical -> BoxConstraints(maxWidth = constraints.maxWidth)
                    }
                }
            child.layout(innerConstraints, parentUsesSize = true)
            val childSize = child.size
            allocatedSize += getMainSize(childSize)
            crossSize = max(crossSize, getCrossSize(childSize))
        } // MainAxisSize???Max ?????? ??????????????????????????????????????????????????????
        // Min????????????????????????
        var idealMainSize =
            if (canFlex && mainAxisSize == MainAxisSize.Max) maxMainSize else allocatedSize

        // ?????????????????????????????????
        when (direction) {
            Axis.Horizontal -> {
                size = constraints.constrain(Size(idealMainSize, crossSize))
                idealMainSize = size.width
                crossSize = size.height
            }

            Axis.Vertical -> {
                size = constraints.constrain(Size(crossSize, idealMainSize))
                idealMainSize = size.height
                crossSize = size.width
            }
        }

        // ?????????????????????
        val remainingSpace = max(0.0, idealMainSize - allocatedSize)
        val leadingSpace: Double
        val betweenSpace: Double
        when (mainAxisAlignment) {
            MainAxisAlignment.Start -> {
                leadingSpace = 0.0
                betweenSpace = 0.0
            }

            MainAxisAlignment.End -> {
                leadingSpace = remainingSpace
                betweenSpace = 0.0
            }

            MainAxisAlignment.Center -> {
                leadingSpace = remainingSpace / 2.0
                betweenSpace = 0.0
            }

            MainAxisAlignment.SpaceBetween -> {
                leadingSpace = 0.0
                betweenSpace = if (children.size > 1) remainingSpace / (children.size - 1) else 0.0
            }

            MainAxisAlignment.SpaceAround -> {
                betweenSpace = if (children.size > 0) remainingSpace / children.size else 0.0
                leadingSpace = betweenSpace / 2
            }

            MainAxisAlignment.SpaceEvenly -> {
                betweenSpace = if (children.size > 0) remainingSpace / (children.size + 1) else 0.0
                leadingSpace = betweenSpace
            }
        }

        val flipMainAxis = !(startIsTopLeft(direction, verticalDirection) ?: true)
        var childMainPosition = if (flipMainAxis) idealMainSize - leadingSpace else leadingSpace
        for (child in children) {
            val childParentData = child.parentData as BoxParentData
            val childCrossPosition = when (crossAxisAlignment) {
                CrossAxisAlignment.Start -> {
                    if (startIsTopLeft(
                            direction.flip(),
                            verticalDirection
                        ) != false
                    ) 0.0 else crossSize - getCrossSize(child.size)
                }

                CrossAxisAlignment.End -> {
                    if (startIsTopLeft(
                            direction.flip(),
                            verticalDirection
                        ) == false
                    ) 0.0 else crossSize - getCrossSize(child.size)
                }

                CrossAxisAlignment.Center -> {
                    crossSize / 2.0 - getCrossSize(child.size) / 2.0
                }

                CrossAxisAlignment.Stretch -> 0.0
                CrossAxisAlignment.Baseline -> {
                    throw NotImplementedError()
                }
            }
            if (flipMainAxis) {
                childMainPosition -= getMainSize(child.size)
            }

            // ??????Offset?????????
            childParentData.offset = when (direction) {
                Axis.Horizontal -> Offset(childMainPosition, childCrossPosition)
                Axis.Vertical -> Offset(childCrossPosition, childMainPosition)
            }

            // ?????????????????????????????????
            if (flipMainAxis) {
                childMainPosition -= betweenSpace
            } else {
                childMainPosition += getMainSize(child.size) + betweenSpace
            }
        }
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        for (child in children) {
            val childParentData = child.parentData as BoxParentData
            context.paintChild(child, childParentData.offset + offset)
        }
    }

    override fun attach(owner: RenderPipeline) {
        super.attach(owner)
        attachChildren(owner)
    }

    override fun detach() {
        super.detach()
        detachChildren()
    }

    override fun visitChildren(visitor: RenderObjectVisitor) {
        super<ContainerRenderObject>.visitChildren(visitor)
    }

    override fun redepthChildren() {
        super<ContainerRenderObject>.redepthChildren { redepthChild(it) }
    }
}