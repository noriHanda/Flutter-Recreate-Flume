package framework.element

import ComponentElement
import framework.widget.StatelessWidget
import framework.widget.Widget

class StatelessElement(widget: StatelessWidget) : ComponentElement(widget) {
    override fun build(): Widget = (widget as StatelessWidget).build(this)

    override fun update(newWidget: Widget) {
        super.update(newWidget)
        dirty = true
        rebuild()
    }
}
