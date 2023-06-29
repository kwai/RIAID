package com.kuaishou.riaid.render.pb.layout;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.layout.VerticalLayoutNode;
import com.kuaishou.riaid.render.pb.layout.base.AbsLayoutPbParser;

/**
 * 这个是负责把pb的vertical的model对象，转换成渲染用的model对象
 */
public class VerticalLayoutPbParser
    extends AbsLayoutPbParser<UIModel.Attrs, VerticalLayoutNode> {

  @Override
  protected int getParseClassType() {
    return Node.CLASS_TYPE_LAYOUT_VERTICAL;
  }

  @NonNull
  @Override
  protected VerticalLayoutNode createUINode(
      @NonNull AbsObjectNode.NodeInfo<UIModel.Attrs> nodeInfo) {
    return new VerticalLayoutNode(nodeInfo);
  }

  @NonNull
  @Override
  protected UIModel.Attrs createUIAttrs() {
    return new UIModel.Attrs();
  }
}
