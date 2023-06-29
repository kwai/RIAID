package com.kuaishou.riaid.render.pb.layout;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.layout.VScrollLayoutNode;
import com.kuaishou.riaid.render.pb.layout.base.AbsLayoutPbParser;
import com.kuaishou.riaid.render.util.Pb2Model;

public class VScrollLayoutPbParser
    extends AbsLayoutPbParser<UIModel.ScrollAttrs, VScrollLayoutNode> {

  @Override
  protected int getParseClassType() {
    return Node.CLASS_TYPE_LAYOUT_V_SCROLL;
  }

  @NonNull
  @Override
  protected UIModel.ScrollAttrs createUINodeAttributes(@NonNull UIModel.NodeContext context,
      @NonNull IServiceContainer serviceContainer, @NonNull UIModel.ScrollAttrs attrsUi,
      @Nullable Attributes attrsPb, @NonNull Map<Integer, AbsObjectNode<?>> nodeCacheMap) {
    if (attrsPb != null && attrsPb.scroll != null) {
      Pb2Model.transformScrollAttrs(context.realContext, attrsUi, attrsPb.scroll);
    }
    return super.createUINodeAttributes(context, serviceContainer, attrsUi, attrsPb, nodeCacheMap);
  }

  @NonNull
  @Override
  protected VScrollLayoutNode createUINode(
      @NonNull AbsObjectNode.NodeInfo<UIModel.ScrollAttrs> nodeInfo) {
    return new VScrollLayoutNode(nodeInfo);
  }

  @NonNull
  @Override
  protected UIModel.ScrollAttrs createUIAttrs() {
    return new UIModel.ScrollAttrs();
  }
}
