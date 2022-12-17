package framework.element

import framework.widget.Widget

interface BuildContext {
    val widget: Widget
    val owner: BuildOwner?
}
