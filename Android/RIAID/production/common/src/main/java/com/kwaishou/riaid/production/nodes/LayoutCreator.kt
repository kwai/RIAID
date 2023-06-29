package com.kwaishou.riaid.production.nodes

import com.kuaishou.riaid.proto.Layout
import com.kuaishou.riaid.proto.nano.RIAIDConstants

/**
 * 这个是通用的布局构造器
 */
object LayoutCreator {

    const val MATCH_PARENT = RIAIDConstants.Render.MATCH_PARENT.toFloat()

    const val WRAP_CONTENT = RIAIDConstants.Render.WRAP_CONTENT.toFloat()

    /**
     * 创建四个方向都等于value的edge
     */
    fun createEdge(value: Float): Layout.Edge {
        return createEdge(value, value, value, value)
    }

    /**
     * 创建margin或者padding
     */
    fun createEdge(start: Float = 0F, end: Float = 0F, top: Float = 0F, bottom: Float = 0F): Layout.Edge {
        return Layout.Edge.newBuilder()
                .setBottom(bottom)
                .setTop(top)
                .setStart(start)
                .setEnd(end)
                .build()
    }

    /**
     * 更具宽高，以及padding和margin创建Layout属性对象
     */
    fun createLayout(width: Float, height: Float, weight: Int = 0, priority: Int = 0, margin: Layout.Edge? = null, padding: Layout.Edge? = null): Layout {
        val builder = Layout.newBuilder().setWidth(width).setHeight(height).setWeight(weight).setPriority(priority)
        padding?.let { builder.setPadding(it) }
        margin?.let { builder.setMargin(it) }
        return builder.build()
    }

    fun createLayoutBuilder(width: Float, height: Float, weight: Int = 0, priority: Int = 0, margin: Layout.Edge? = null, padding: Layout.Edge? = null): Layout.Builder {
        val builder = Layout.newBuilder().setWidth(width).setHeight(height).setWeight(weight).setPriority(priority)
        padding?.let { builder.setPadding(it) }
        margin?.let { builder.setMargin(it) }
        return builder
    }

}