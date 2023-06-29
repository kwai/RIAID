package com.kuaishou.riaid.render.pb.layout;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.layout.AbsoluteLayoutNode;
import com.kuaishou.riaid.render.pb.layout.base.AbsLayoutPbParser;

/**
 * 这个是负责把pb的absolute的model对象，转换成渲染用的model对象
 */
public class AbsoluteLayoutPbParser
    extends AbsLayoutPbParser<UIModel.Attrs, AbsoluteLayoutNode> {

  @Override
  protected int getParseClassType() {
    return Node.CLASS_TYPE_LAYOUT_ABSOLUTE;
  }

  @NonNull
  @Override
  protected AbsoluteLayoutNode createUINode(
      @NonNull AbsObjectNode.NodeInfo<UIModel.Attrs> nodeInfo) {
    return new AbsoluteLayoutNode(nodeInfo);
  }

  @NonNull
  @Override
  protected UIModel.Attrs createUIAttrs() {
    return new UIModel.Attrs();
  }

}
