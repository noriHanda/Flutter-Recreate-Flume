package framework.widget

import StatefulElement
import framework.element.BuildContext
import framework.element.Element

abstract class StatefulWidget : Widget() {
    override fun createElement(): Element = StatefulElement(this)

    abstract fun createState(): State<*>
}

abstract class State<T : StatefulWidget> {
    val widget: T
        get() = widgetInternal!!
    var widgetInternal: T? = null

    var element: StatefulElement? = null
    val context: BuildContext
        get() = element!!

    open fun initState() {}

    open fun didUpdateWidget(oldWidget: T) {}

    open fun setState(func: () -> Unit) {
        func()
        element!!.markNeedsBuild()
    }

    open fun didChangeDependencies() {

    }

    abstract fun build(context: BuildContext): Widget
}
