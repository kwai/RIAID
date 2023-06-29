package com.kuaishou.riaid.render.pb.item;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.item.TextItemNode;
import com.kuaishou.riaid.render.pb.item.base.AbsItemPbParser;
import com.kuaishou.riaid.render.util.Pb2Model;

/**
 * 这个是负责把pb的text的model对象，转换成渲染用的model对象
 */
public class TextItemPbParser
    extends AbsItemPbParser<TextItemNode.TextAttrs, TextItemNode> {

  @Override
  protected int getParseClassType() {
    return Node.CLASS_TYPE_ITEM_TEXT;
  }

  @NonNull
  @Override
  protected TextItemNode.TextAttrs createUINodeAttributes(@NonNull UIModel.NodeContext context,
      @NonNull IServiceContainer serviceContainer, @NonNull TextItemNode.TextAttrs attrsUi,
      @Nullable Attributes attrsPb, @NonNull Map<Integer, AbsObjectNode<?>> nodeCacheMap) {
    if (attrsPb != null && attrsPb.text != null) {
      Pb2Model.transformTextAttrs(context.realContext, serviceContainer, context.decorSize,
          attrsUi, attrsPb.text);
    }
    return super.createUINodeAttributes(context, serviceContainer, attrsUi, attrsPb, nodeCacheMap);
  }

  @NonNull
  @Override
  protected TextItemNode createUINode(
      @NonNull AbsObjectNode.NodeInfo<TextItemNode.TextAttrs> nodeInfo) {
    return new TextItemNode(nodeInfo);
  }

  @NonNull
  @Override
  protected TextItemNode.TextAttrs createUIAttrs() {
    return new TextItemNode.TextAttrs();
  }
}
