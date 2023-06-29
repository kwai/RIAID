package com.kwaishou.riaid.production.nodes

import com.kuaishou.riaid.proto.*

/**
 * 这个是通用的属性构造器
 */
object AttributesCreator {

    /**
     * 创建按钮的属性携带Content
     */
    fun createButtonAttrWithContent(node: Node): Attributes.Builder {
        return Attributes.newBuilder()
                .setButton(
                        ButtonAttributes.newBuilder()
                                .setContent(node)
                )
    }

    /**
     * 创建圆角属性兑现，四个圆角的大小是一致的
     */
    fun createCornerRadius(radius: Float): CornerRadius {
        return CornerRadius.newBuilder()
                .setTopEnd(radius)
                .setTopStart(radius)
                .setBottomEnd(radius)
                .setBottomStart(radius)
                .build()
    }

    /**
     * 创建圆角属性兑现，四个圆角的大小可以分别自定义
     */
    fun createCornerRadius(topStart: Float = 0F, topEnd: Float = 0F, bottomStart: Float = 0F, bottomEnd: Float = 0F): CornerRadius {
        return CornerRadius.newBuilder()
                .setTopEnd(topEnd)
                .setTopStart(topStart)
                .setBottomEnd(bottomEnd)
                .setBottomStart(bottomStart)
                .build()
    }

    /**
     * 创建背景，可以定义背景的圆角和颜色，因为这个最常用
     */
    fun createBackgroundDrawable(backgroundColor: String, radius: Float = 0f, strokeWidth: Float = 0f, strokeColor: String = ""): Attributes {
        return Attributes.newBuilder().setCommon(createBackgroundDrawableCommon(backgroundColor, radius, strokeColor, strokeWidth)).build()
    }

    fun createBackgroundDrawableRadius(backgroundColor: String, radius: CornerRadius = createCornerRadius(0f), strokeWidth: Float = 0f, strokeColor: String = ""): Attributes {
        return Attributes.newBuilder().setCommon(createBackgroundDrawableCommonRadius(backgroundColor, radius, strokeColor, strokeWidth)).build()
    }

    fun createBackgroundDrawableCommonRadius(backgroundColor: String, radius: CornerRadius = createCornerRadius(0f), strokeColor: String? = "", strokeWidth: Float = 0f): CommonAttributes {
        val cornerRadius = CommonAttributes.newBuilder()
                .setShapeType(CommonAttributes.ShapeType.SHAPE_TYPE_RECTANGLE)
                .setBackgroundColor(backgroundColor)
                .setCornerRadius(radius)
        if (!strokeColor.isNullOrEmpty() && strokeWidth > 0) {
            cornerRadius.setStroke(Stroke.newBuilder()
                    .setColor(strokeColor)
                    .setWidth(strokeWidth))
        }
        return cornerRadius.build();
    }

    fun createBackgroundDrawableCommon(backgroundColor: String, radius: Float, strokeColor: String? = "", strokeWidth: Float = 0f): CommonAttributes {
        val cornerRadius = CommonAttributes.newBuilder()
                .setShapeType(CommonAttributes.ShapeType.SHAPE_TYPE_RECTANGLE)
                .setBackgroundColor(backgroundColor)
                .setCornerRadius(createCornerRadius(radius))
        if (!strokeColor.isNullOrEmpty() && strokeWidth > 0) {
            cornerRadius.setStroke(Stroke.newBuilder()
                    .setColor(strokeColor)
                    .setWidth(strokeWidth))
        }
        return cornerRadius.build();
    }

    /**
     * 创建背景，渐变的
     */
    fun createGradientDrawable(angle: Int, colorList: List<String>, radius: Float = 0f): Attributes {
        val gradient = Gradient.newBuilder()
                .setType(Gradient.GradientType.GRADIENT_TYPE_LINEAR)
                .setAngleValue(angle)
                .addAllColors(colorList)
                .build()
        val commonAttributes = CommonAttributes.newBuilder()
                .setShapeType(CommonAttributes.ShapeType.SHAPE_TYPE_RECTANGLE)
                .setGradient(gradient)
                .setCornerRadius(createCornerRadius(radius = radius))
                .build()
        return Attributes.newBuilder().setCommon(commonAttributes).build()
    }

    /**
     * 创建基础属性
     */
    fun createCommonAttributes(backgroundColor: String, radius: Float): CommonAttributes {
        return CommonAttributes.newBuilder()
                .setShapeType(CommonAttributes.ShapeType.SHAPE_TYPE_RECTANGLE)
                .setBackgroundColor(backgroundColor)
                .setCornerRadius(createCornerRadius(radius))
                .build()
    }

    fun createCommonAttributesBuild(backgroundColor: String, radius: Float): CommonAttributes.Builder {
        return CommonAttributes.newBuilder()
                .setShapeType(CommonAttributes.ShapeType.SHAPE_TYPE_RECTANGLE)
                .setBackgroundColor(backgroundColor)
                .setCornerRadius(createCornerRadius(radius))
    }

    fun createCommonAttributeStroke(backgroundColor: String, radius: Float, strokeColor: String, strokeWidth: Float): CommonAttributes {
        return CommonAttributes.newBuilder()
                .setShapeType(CommonAttributes.ShapeType.SHAPE_TYPE_RECTANGLE)
                .setBackgroundColor(backgroundColor)
                .setCornerRadius(createCornerRadius(radius))
                .setStroke(Stroke.newBuilder()
                        .setWidth(strokeWidth)
                        .setColor(strokeColor))
                .build()
    }

    /**
     * 创建居中属性
     */
    fun createTextAlinDefaultCenter(
            hMode: TextAttributes.Align.Horizontal = TextAttributes.Align.Horizontal.HORIZONTAL_CENTER,
            vMode: TextAttributes.Align.Vertical = TextAttributes.Align.Vertical.VERTICAL_CENTER): TextAttributes.Align {
        return TextAttributes.Align.newBuilder()
                .setHorizontal(hMode)
                .setVertical(vMode)
                .build()
    }

    /**
     * 创建按钮的ButtonText的属性
     */
    @JvmStatic
    fun createButtonTextAttributes(text: String, fontSize: Float, fontColor: String = "#FFFFFF", richList: List<TextAttributes.RichText>? = null): TextAttributes {
        return TextAttributes.newBuilder()
                .setText(text)
                .setFontColor(fontColor)
                .setFontSize(BaseNumCreator.createFloatValue(fontSize))
                .setBold(BaseNumCreator.createBoolValue(true))
                .setMaxLines(BaseNumCreator.createInt32Value(1))
                .setEllipsize(TextAttributes.Ellipsize.ELLIPSIZE_END)
                .setAlign(createTextAlinDefaultCenter())
                .addAllRichList(richList ?: listOf())
                .build()
    }

    fun createTextAttributes(text: String, fontSize: Float, fontColor: String = "#FFFFFF", bold: Boolean = false): TextAttributes {
        return TextAttributes.newBuilder()
                .setText(text)
                .setFontColor(fontColor)
                .setFontSize(BaseNumCreator.createFloatValue(fontSize))
                .setBold(BaseNumCreator.createBoolValue(true))
                .setMaxLines(BaseNumCreator.createInt32Value(1))
                .setEllipsize(TextAttributes.Ellipsize.ELLIPSIZE_END)
                .setBold(BaseNumCreator.createBoolValue(bold))
                .setAlign(createTextAlinDefaultCenter())
                .build()
    }
    fun createTextAttributesBuilder(text: String, fontSize: Float, fontColor: String = "#FFFFFF", bold: Boolean = false): TextAttributes.Builder {
        return TextAttributes.newBuilder()
                .setText(text)
                .setFontColor(fontColor)
                .setFontSize(BaseNumCreator.createFloatValue(fontSize))
                .setBold(BaseNumCreator.createBoolValue(true))
                .setMaxLines(BaseNumCreator.createInt32Value(1))
                .setBold(BaseNumCreator.createBoolValue(bold))
                .setAlign(createTextAlinDefaultCenter())
    }
}