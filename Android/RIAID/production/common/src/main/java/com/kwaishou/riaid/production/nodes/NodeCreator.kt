package com.kwaishou.riaid.production.nodes

import com.kuaishou.riaid.proto.*

/**
 * 创建简单的Node
 */
object NodeCreator {

    /**
     * 横向的node居右的
     */
    fun createHorWeightEndNode(node: Node): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_LAYOUT_HORIZONTAL)
                .setLayout(
                  LayoutCreator.createLayout(
                    LayoutCreator.MATCH_PARENT,
                    LayoutCreator.WRAP_CONTENT
                  )
                )
                .addChildren(createSpaceWeightNodeBuilder())
                .addChildren(node)
    }

    /**
     * 横向的node居中的
     */
    fun createHorWeightCenterNode(node: Node): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_LAYOUT_HORIZONTAL)
                .setLayout(
                  LayoutCreator.createLayout(
                    LayoutCreator.MATCH_PARENT,
                    LayoutCreator.WRAP_CONTENT
                  )
                )
                .addChildren(createSpaceWeightNodeBuilder())
                .addChildren(node)
                .addChildren(createSpaceWeightNodeBuilder())
    }

    /**
     * 纵向的node居中的
     */
    fun createVerWeightCenterNode(node: Node, weight: Int = 0): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_LAYOUT_VERTICAL)
                .setLayout(
                  LayoutCreator.createLayout(
                    LayoutCreator.WRAP_CONTENT,
                    LayoutCreator.MATCH_PARENT,
                    weight = weight
                  )
                )
                .addChildren(createSpaceWeightNodeBuilder())
                .addChildren(node)
                .addChildren(createSpaceWeightNodeBuilder())
    }

    fun createVerWeightCenterNode2(node: Node, weight: Int = 0): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_LAYOUT_VERTICAL)
                .setLayout(
                  LayoutCreator.createLayout(
                    0f,
                    LayoutCreator.MATCH_PARENT,
                    weight = weight
                  )
                )
                .addChildren(createSpaceWeightNodeBuilder())
                .addChildren(node)
                .addChildren(createSpaceWeightNodeBuilder())
    }

    /**
     * 纵向的node居下的
     */
    fun createVerWeightBottomNode(node: Node): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_LAYOUT_VERTICAL)
                .setLayout(
                  LayoutCreator.createLayout(
                    LayoutCreator.WRAP_CONTENT,
                    LayoutCreator.MATCH_PARENT
                  )
                )
                .addChildren(createSpaceWeightNodeBuilder())
                .addChildren(node)
    }

    /**
     * 创建一个正方形盒子组件
     */
    fun createSquareNodeBuilder(node: Node, width: Float, height: Float): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_LAYOUT_SQUARE)
                .setLayout(LayoutCreator.createLayout(width, height))
                .addChildren(node)
    }

    fun createSquareImageNodeBuilder(width: Float, height: Float, imageUrl: String, radius: Float? = null, handler: Handler = Handler.getDefaultInstance(), imageViewKey: Int = 0, margin: Layout.Edge? = null): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_LAYOUT_SQUARE)
                .setLayout(LayoutCreator.createLayout(width, height, margin = margin))
                .addChildren(createImageNodeBuilder(width, height, imageUrl, radius, handler = handler, key = imageViewKey).build())
    }


    fun createImageNodeBuilder(width: Float, height: Float, imageUrl: String, radius: Float? = null, margin: Layout.Edge? = null, padding: Layout.Edge? = null, key: Int = 0, handler: Handler = Handler.getDefaultInstance()): Node.Builder {
        val imageAttributes = ImageAttributes.newBuilder().setUrl(imageUrl).build()
        val attributesBuilder = Attributes.newBuilder()
        radius?.let {
            attributesBuilder.setCommon(CommonAttributes.newBuilder().setCornerRadius(
              AttributesCreator.createCornerRadius(it)
            ).build())
        }
        return Node.newBuilder()
                .setKey(key)
                .setClassType(Node.ClassType.CLASS_TYPE_ITEM_IMAGE)
                .setLayout(
                  LayoutCreator.createLayout(
                    width,
                    height,
                    margin = margin,
                    padding = padding
                  )
                )
                .setHandler(handler)
                .setAttributes(attributesBuilder.setImage(imageAttributes).build())
    }

    /**
     * 创建一个消费点击事件的Node*
     */
    fun createInvalidClickNode(width: Float = LayoutCreator.MATCH_PARENT, height: Float = LayoutCreator.MATCH_PARENT, handler: Handler): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_ITEM_TEXT)
                .setLayout(LayoutCreator.createLayout(width, height))
                .setAttributes(AttributesCreator.createBackgroundDrawable("#00FFFFFF", 0F))
                .setHandler(handler)
    }

    /**
     * 创建一个报货按钮
     */
    fun createButtonWithNodeBuilder(btnKey: Int = 0, layout: Layout, node: Node, handler: Handler): Node.Builder {
        // 设置默认状态的TextNode
        return Node.newBuilder()
                .setKey(btnKey)
                .setHandler(handler)
                .setClassType(Node.ClassType.CLASS_TYPE_LAYOUT_BUTTON)
                .setLayout(layout)
                .setAttributes(AttributesCreator.createButtonAttrWithContent(node))
    }

    /**
     * 创建一个有按压态的按钮
     */
    fun createButtonWithPressBuilder(width: Float, height: Float, bgColor: String, radius: Float, textAttributes: TextAttributes, margin: Layout.Edge? = null, weight: Int = 0, btnViewKey: Int = 0): Node.Builder {
        // 设置默认状态的TextNode
        val commonAttributes = AttributesCreator.createCommonAttributes(bgColor, radius)
        val textNode = Node.newBuilder()
                .setKey(1234)
                .setClassType(Node.ClassType.CLASS_TYPE_ITEM_TEXT)
                .setLayout(LayoutCreator.createLayout(width, height))
                .setAttributes(Attributes.newBuilder().setCommon(commonAttributes).setText(textAttributes).build())
                .build()
        // 设置按压态的属性
        val pressAttributes = Attributes.newBuilder().setCommon(CommonAttributes.newBuilder().setAlpha(
          BaseNumCreator.createFloatValue(0.6F)
        ).build()).build()
        val highlightState = ButtonAttributes.HighlightState.newBuilder()
                .setKey(1234)
                .setAttributes(pressAttributes)
                .build()
        // 添加按压列表
        val buttonAttributes = ButtonAttributes.newBuilder()
                .setContent(textNode)
                .addHighlightStateList(highlightState)
                .build()
        return Node.newBuilder()
                .setKey(btnViewKey)
                .setClassType(Node.ClassType.CLASS_TYPE_LAYOUT_BUTTON)
                .setLayout(
                  LayoutCreator.createLayout(
                    width,
                    height,
                    margin = margin,
                    weight = weight
                  )
                )
                .setAttributes(Attributes.newBuilder().setButton(buttonAttributes).build())
    }

    /**
     * 创建一个有按压态的按钮
     */
    fun createStrokeButtonWithPressBuilder(width: Float, height: Float, bgColor: String, radius: Float, strokeColor: String, strokeWidth: Float, textAttributes: TextAttributes, margin: Layout.Edge? = null, weight: Int = 0): Node.Builder {
        // 设置默认状态的TextNode
        val commonAttributes =
          AttributesCreator.createCommonAttributeStroke(bgColor, radius, strokeColor, strokeWidth)
        val textNode = Node.newBuilder()
                .setKey(1234)
                .setClassType(Node.ClassType.CLASS_TYPE_ITEM_TEXT)
                .setLayout(LayoutCreator.createLayout(width, height))
                .setAttributes(Attributes.newBuilder().setCommon(commonAttributes).setText(textAttributes).build())
                .build()
        // 设置按压态的属性
        val pressAttributes = Attributes.newBuilder().setCommon(CommonAttributes.newBuilder().setAlpha(
          BaseNumCreator.createFloatValue(0.6F)
        ).build()).build()
        val highlightState = ButtonAttributes.HighlightState.newBuilder()
                .setKey(1234)
                .setAttributes(pressAttributes)
                .build()
        // 添加按压列表
        val buttonAttributes = ButtonAttributes.newBuilder()
                .setContent(textNode)
                .addHighlightStateList(highlightState)
                .build()
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_LAYOUT_BUTTON)
                .setLayout(
                  LayoutCreator.createLayout(
                    width,
                    height,
                    weight = weight,
                    margin = margin
                  )
                )
                .setAttributes(Attributes.newBuilder().setButton(buttonAttributes).build())
    }

    /**
     * 创建撑满的弹簧
     */
    fun createSpaceWeightNodeBuilder(weight: Int = 1): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_ITEM_SPACE)
                .setLayout(Layout.newBuilder().setWeight(weight).build())
    }

    /**
     * 创建指定大小的弹簧
     */
    fun createSpaceSizeNodeBuilder(width: Float, height: Float): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_ITEM_SPACE)
                .setLayout(LayoutCreator.createLayout(width, height))
    }


    /**
     * 创建指定大小的弹簧
     */
    fun createSpaceSizeNodeBuilder(width: Float, height: Float, weight: Int): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_ITEM_SPACE)
                .setLayout(LayoutCreator.createLayout(width, height, weight = weight))
    }

    /**
     * 水平组件容器，让Node在水平居中，可以定义容器的高度，和margin
     */
    fun createCenterHorizontalContainerBuilder(node: Node, height: Float, margin: Layout.Edge? = null, padding: Layout.Edge? = null): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_LAYOUT_HORIZONTAL)
                .setLayout(
                  LayoutCreator.createLayout(
                    LayoutCreator.MATCH_PARENT,
                    height,
                    margin = margin,
                    padding = padding
                  )
                )
                .addChildren(createSpaceWeightNodeBuilder().build())
                .addChildren(node)
                .addChildren(createSpaceWeightNodeBuilder().build())
    }

    /**
     * 垂直组件容器，让Node在垂直居中，可以定义容器的高度，和margin
     */
    fun createCenterVerticalContainerBuilder(node: Node, width: Float, margin: Layout.Edge? = null, padding: Layout.Edge? = null): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_LAYOUT_VERTICAL)
                .setLayout(
                  LayoutCreator.createLayout(
                    width,
                    LayoutCreator.MATCH_PARENT,
                    margin = margin,
                    padding = padding
                  )
                )
                .addChildren(createSpaceWeightNodeBuilder().build())
                .addChildren(node)
                .addChildren(createSpaceWeightNodeBuilder().build())
    }

    /**
     * 创建一个分割线
     */
    fun createDivideBuilder(width: Float, height: Float, bgColor: String = "#FFFFFF", radius: Float = 0F, weight: Int = 0): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_ITEM_TEXT)
                .setLayout(LayoutCreator.createLayout(width, height, weight = weight))
                .setAttributes(AttributesCreator.createBackgroundDrawable(bgColor, radius))
    }

    fun createSolidBuilder(size: Float, bgColor: String = "#FFFFFF"): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_ITEM_TEXT)
                .setLayout(LayoutCreator.createLayout(size, size))
                .setAttributes(AttributesCreator.createBackgroundDrawable(bgColor, size / 2f))
    }

    fun createTextNodeBuilder(width: Float, height: Float, commonAttributes: CommonAttributes = CommonAttributes.newBuilder().build(), textAttributes: TextAttributes): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_ITEM_TEXT)
                .setLayout(LayoutCreator.createLayout(width, height))
                .setAttributes(
                        Attributes.newBuilder()
                                .setCommon(commonAttributes)
                                .setText(textAttributes)
                )


    }
    fun createTextNodeBuilder(layout: Layout, commonAttributes: CommonAttributes = CommonAttributes.newBuilder().build(), textAttributes: TextAttributes): Node.Builder {
        return Node.newBuilder()
                .setClassType(Node.ClassType.CLASS_TYPE_ITEM_TEXT)
                .setLayout(layout)
                .setAttributes(
                        Attributes.newBuilder()
                                .setCommon(commonAttributes)
                                .setText(textAttributes)
                )


    }
}