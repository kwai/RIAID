package com.kuaishou.riaid.render.pb.layout;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.layout.ButtonLayoutNode;
import com.kuaishou.riaid.render.pb.layout.base.AbsLayoutPbParser;
import com.kuaishou.riaid.render.util.Pb2Model;

/**
 * 这个是负责把pb的button的model对象，转换成渲染用的model对象
 */
public class ButtonLayoutPbParser
    extends AbsLayoutPbParser<ButtonLayoutNode.ButtonAttrs, ButtonLayoutNode> {

  @Override
  protected int getParseClassType() {
    return Node.CLASS_TYPE_LAYOUT_BUTTON;
  }

  @NonNull
  @Override
  protected ButtonLayoutNode.ButtonAttrs createUINodeAttributes(
      @NonNull UIModel.NodeContext context, @NonNull IServiceContainer serviceContainer,
      @NonNull ButtonLayoutNode.ButtonAttrs attrsUi, @Nullable Attributes attrsPb,
      @NonNull Map<Integer, AbsObjectNode<?>> nodeCacheMap) {
    if (attrsPb != null && attrsPb.button != null) {
      Pb2Model.transformButtonAttrs(context.realContext,
          context.decorSize, serviceContainer, attrsUi, attrsPb.button, nodeCacheMap);
    }
    return super.createUINodeAttributes(context, serviceContainer, attrsUi, attrsPb, nodeCacheMap);
  }

  @NonNull
  @Override
  protected ButtonLayoutNode createUINode(
      @NonNull AbsObjectNode.NodeInfo<ButtonLayoutNode.ButtonAttrs> nodeInfo) {
    return new ButtonLayoutNode(nodeInfo);
  }

  @NonNull
  @Override
  protected ButtonLayoutNode.ButtonAttrs createUIAttrs() {
    return new ButtonLayoutNode.ButtonAttrs();
  }
}
