package com.kuaishou.riaid.render.pb.item;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.item.ImageItemNode;
import com.kuaishou.riaid.render.pb.item.base.AbsItemPbParser;
import com.kuaishou.riaid.render.util.Pb2Model;

/**
 * 这个是负责把pb的image的model对象，转换成渲染用的model对象
 */
public class ImageItemPbParser
    extends AbsItemPbParser<ImageItemNode.ImageAttrs, ImageItemNode> {

  @Override
  protected int getParseClassType() {
    return Node.CLASS_TYPE_ITEM_IMAGE;
  }

  @NonNull
  @Override
  protected ImageItemNode.ImageAttrs createUINodeAttributes(@NonNull UIModel.NodeContext context,
      @NonNull IServiceContainer serviceContainer, @NonNull ImageItemNode.ImageAttrs attrsUi,
      @Nullable Attributes attrsPb, @NonNull Map<Integer, AbsObjectNode<?>> nodeCacheMap) {
    if (attrsPb != null && attrsPb.image != null) {
      Pb2Model.transformImageAttrs(context.realContext, serviceContainer, attrsUi, attrsPb.image);
    }
    return super.createUINodeAttributes(context, serviceContainer, attrsUi, attrsPb, nodeCacheMap);
  }

  @NonNull
  @Override
  protected ImageItemNode createUINode(
      @NonNull AbsObjectNode.NodeInfo<ImageItemNode.ImageAttrs> nodeInfo) {
    return new ImageItemNode(nodeInfo);
  }

  @NonNull
  @Override
  protected ImageItemNode.ImageAttrs createUIAttrs() {
    return new ImageItemNode.ImageAttrs();
  }
}
