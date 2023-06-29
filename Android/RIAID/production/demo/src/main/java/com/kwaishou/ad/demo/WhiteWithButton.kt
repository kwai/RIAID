package com.kwaishou.ad.demo

import com.kuaishou.riaid.proto.*
import com.kuaishou.riaid.proto.nano.RIAIDConstants
import com.kwaishou.riaid.production.nodes.AttributesCreator
import com.kwaishou.riaid.production.nodes.BaseNumCreator
import com.kwaishou.riaid.production.nodes.INodeCreator
import com.kwaishou.riaid.production.nodes.LayoutCreator
import com.kwaishou.riaid.production.nodes.NodeCreator
import extension.dataBinding

/**
 * 基础卡片，白色背景
 */
class WhiteWithButton() : INodeCreator {
  override fun createNode(): Node {
    val common = Attributes.newBuilder()
      .setCommon(
        CommonAttributes.newBuilder()
          .setShapeType(CommonAttributes.ShapeType.SHAPE_TYPE_RECTANGLE)
          .setBackgroundColor("#FFFFFF")
          .setCornerRadius(AttributesCreator.createCornerRadius(8.0f))
      )
    val newBuilder = Node.newBuilder()
    newBuilder.classType = Node.ClassType.CLASS_TYPE_LAYOUT_ABSOLUTE
    newBuilder.layout =
      LayoutCreator.createLayout(width = 270f, height = LayoutCreator.WRAP_CONTENT)
    newBuilder.attributes = common.build()
    newBuilder.addChildren(createChildrenContent())
    newBuilder.addChildren(createRightImageNode("http://s16.kwai.net/bs2/ad-i18n-dsp/icon_gray_24.png"))
    return newBuilder.build()
  }

  private fun createChildrenContent(): Node {
    val newBuilder = Node.newBuilder()
    newBuilder.classType = Node.ClassType.CLASS_TYPE_LAYOUT_VERTICAL
    newBuilder.layout = LayoutCreator.createLayout(
      width = LayoutCreator.MATCH_PARENT, height = LayoutCreator.WRAP_CONTENT,
      padding = LayoutCreator.createEdge(start = 16f, end = 16f, top = 12f, bottom = 12f)
    )
    newBuilder.addChildren(createChildrenContent_TOP())
    newBuilder.addChildren(createButtonContainer())
    return newBuilder.build()
  }

  private fun createChildrenContent_TOP(): Node {
    val newBuilder = Node.newBuilder()
    newBuilder.classType = Node.ClassType.CLASS_TYPE_LAYOUT_HORIZONTAL
    newBuilder.layout = LayoutCreator.createLayout(width = LayoutCreator.MATCH_PARENT, height = 74f)
    newBuilder.addChildren(createChildrenContent_TOP_Image())
    newBuilder.addChildren(createChildrenContent_TOP_Info())
    return newBuilder.build()
  }

  private fun createChildrenContent_TOP_Image(): Node {
    val createSquareImageNodeBuilder = NodeCreator.createSquareImageNodeBuilder(
      LayoutCreator.MATCH_PARENT,
      LayoutCreator.MATCH_PARENT,
      imageUrl = RIAIDConstants.DataBinding.ICON_URL.dataBinding(),
      radius = 6.0f
    )
    return createSquareImageNodeBuilder.build()
  }

  private fun createChildrenContent_TOP_Info(): Node {
    var desc = RIAIDConstants.DataBinding.DESCRIPTION.dataBinding() + "[tag]"
    val newBuilder = Node.newBuilder()
    newBuilder.classType = Node.ClassType.CLASS_TYPE_LAYOUT_VERTICAL
    newBuilder.layout = LayoutCreator.createLayout(
      width = LayoutCreator.MATCH_PARENT, height = LayoutCreator.MATCH_PARENT,
      margin = LayoutCreator.createEdge(start = 8f)
    )
    newBuilder
      .addChildren(createTitleTextNode((RIAIDConstants.DataBinding.TITLE.dataBinding())))
      .addChildren(createDescTextNode(desc))
    return newBuilder.build()
  }

  private fun createTitleTextNode(titleText: String): Node {
    val titleTextAttributes = TextAttributes.newBuilder()
      .setText(titleText)
      .setFontSize(BaseNumCreator.createFloatValue(15F))
      .setFontColor("#E0000000")
      .setBold(BaseNumCreator.createBoolValue(true))
      .setMaxLines(BaseNumCreator.createInt32Value(1))
      .setEllipsize(TextAttributes.Ellipsize.ELLIPSIZE_END)
      .build()
    return Node.newBuilder()
      .setClassType(Node.ClassType.CLASS_TYPE_ITEM_TEXT)
      .setLayout(
        LayoutCreator.createLayout(
          LayoutCreator.MATCH_PARENT,
          LayoutCreator.WRAP_CONTENT,
          margin = LayoutCreator.createEdge(top = 2f, end = 28f)
        )
      )
      .setAttributes(Attributes.newBuilder().setText(titleTextAttributes).build())
      .build()
  }


  private fun createDescTextNode(desc: String): Node {
    val richNode = Node.newBuilder()
      .setClassType(Node.ClassType.CLASS_TYPE_LAYOUT_ABSOLUTE)
      .setLayout(
        LayoutCreator.createLayout(
          RIAIDConstants.Render.WRAP_CONTENT.toFloat(),
          RIAIDConstants.Render.WRAP_CONTENT.toFloat(),
          padding = LayoutCreator.createEdge(start = 4f)
        )
      )
      .addChildren(
        Node.newBuilder()
          .setClassType(Node.ClassType.CLASS_TYPE_ITEM_TEXT)
          .setLayout(LayoutCreator.createLayout(14f, 12f))
          .setAttributes(
            Attributes.newBuilder()
              .setCommon(
                CommonAttributes.newBuilder()
                  .setShapeType(CommonAttributes.ShapeType.SHAPE_TYPE_RECTANGLE)
                  .setCornerRadius(
                    AttributesCreator.createCornerRadius(2.0f)
                  )
                  .setBackgroundColor("#40222222")
              )
              .setText(
                TextAttributes.newBuilder()
                  .setFontColor("#FFFFFF")
                  .setFontSize(FloatValue.newBuilder().setValue(8.0f))
                  .setText(RIAIDConstants.DataBinding.AD_TAG.dataBinding())
                  .setAlign(
                    TextAttributes.Align.newBuilder()
                      .setVertical(TextAttributes.Align.Vertical.VERTICAL_CENTER)
                      .setHorizontal(TextAttributes.Align.Horizontal.HORIZONTAL_CENTER)
                  )
              )
          )
      )
      .build()

    val build =
      TextAttributes.RichText.newBuilder().setPlaceHolder("[tag]").setContent(richNode).build()

    val titleTextAttributes = TextAttributes.newBuilder()
      .setText(desc)
      .setFontSize(BaseNumCreator.createFloatValue(12f))
      .setFontColor("#A3000000")
      .setMaxLines(BaseNumCreator.createInt32Value(3))
      .setEllipsize(TextAttributes.Ellipsize.ELLIPSIZE_END)
    // 不在按钮上的，描述才会有ad标
    titleTextAttributes.addRichList(build)
    return Node.newBuilder()
      .setClassType(Node.ClassType.CLASS_TYPE_ITEM_TEXT)
      .setLayout(
        LayoutCreator.createLayout(
          LayoutCreator.MATCH_PARENT,
          LayoutCreator.WRAP_CONTENT,
          margin = LayoutCreator.createEdge(top = 2f, end = 28f)
        )
      )
      .setAttributes(Attributes.newBuilder().setText(titleTextAttributes).build())
      .build()
  }

  private fun createButtonContainer(): Node {
    return createCta()
  }

  private fun createCta(): Node {
    var weight = 0
    val ctaTextAttributes = AttributesCreator.createButtonTextAttributes(
      RIAIDConstants.DataBinding.CTA.dataBinding(), 15F
    )
    val node: Node = NodeCreator.createButtonWithPressBuilder(
      LayoutCreator.MATCH_PARENT,
      LayoutCreator.MATCH_PARENT, "#168FFF",
      6F, ctaTextAttributes, weight = weight
    )
      .build()
    val builder = Node.newBuilder()
      .setClassType(Node.ClassType.CLASS_TYPE_LAYOUT_ABSOLUTE)
      .setLayout(
        LayoutCreator.createLayout(
          width = LayoutCreator.MATCH_PARENT, height = 36f, weight = weight,
          margin = LayoutCreator.createEdge(top = 12f)
        )
      )
      .addChildren(node)

    return builder.build()

  }

  private fun createRightImageNode(imageUrl: String): Node {
    val node =
      NodeCreator.createSquareImageNodeBuilder(width = 24f, height = 24f, imageUrl = imageUrl)
    return Node.newBuilder()
      .setClassType(Node.ClassType.CLASS_TYPE_LAYOUT_HORIZONTAL)
      .setLayout(
        LayoutCreator.createLayout(
          width = LayoutCreator.MATCH_PARENT,
          height = 24F,
          margin = LayoutCreator.createEdge(top = 8f, end = 8f)
        )
      )
      .addChildren(NodeCreator.createSpaceWeightNodeBuilder().build())
      .addChildren(node).build()
  }
}