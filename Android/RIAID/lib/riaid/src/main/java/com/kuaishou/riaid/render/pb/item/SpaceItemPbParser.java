package com.kuaishou.riaid.render.pb.item;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.item.SpaceItemNode;
import com.kuaishou.riaid.render.pb.item.base.AbsItemPbParser;

/**
 * 这个是负责把pb的space的model对象，转换成渲染用的model对象
 */
public class SpaceItemPbParser extends AbsItemPbParser<UIModel.Attrs, SpaceItemNode> {

  @Override
  protected int getParseClassType() {
    return Node.CLASS_TYPE_ITEM_SPACE;
  }

  @NonNull
  @Override
  protected SpaceItemNode createUINode(
      @NonNull AbsObjectNode.NodeInfo<UIModel.Attrs> nodeInfo) {
    return new SpaceItemNode(nodeInfo);
  }

  @NonNull
  @Override
  protected UIModel.Attrs createUIAttrs() {
    return new UIModel.Attrs();
  }
}
