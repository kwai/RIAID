package com.kuaishou.riaid.render.pb.item;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.item.VideoItemNode;
import com.kuaishou.riaid.render.pb.item.base.AbsItemPbParser;
import com.kuaishou.riaid.render.service.base.IDataBindingService;
import com.kuaishou.riaid.render.util.Pb2Model;

/**
 * 这个是负责把pb的video的model对象，转换成渲染用的model对象
 */
public class VideoItemPbParser extends AbsItemPbParser<VideoItemNode.VideoAttrs, VideoItemNode> {

  @Override
  protected int getParseClassType() {
    return Node.CLASS_TYPE_ITEM_VIDEO;
  }

  @NonNull
  @Override
  protected VideoItemNode.VideoAttrs createUINodeAttributes(@NonNull UIModel.NodeContext context,
      @NonNull IServiceContainer serviceContainer, @NonNull VideoItemNode.VideoAttrs attrsUi,
      @Nullable Attributes attrsPb, @NonNull Map<Integer, AbsObjectNode<?>> nodeCacheMap) {
    if (attrsPb != null && attrsPb.video != null) {
      IDataBindingService service = serviceContainer.getService(IDataBindingService.class);
      Pb2Model.transformVideoAttrs(context.realContext, service, attrsUi, attrsPb.video);
    }
    return super.createUINodeAttributes(context, serviceContainer, attrsUi, attrsPb, nodeCacheMap);
  }

  @NonNull
  @Override
  protected VideoItemNode createUINode(
      @NonNull AbsObjectNode.NodeInfo<VideoItemNode.VideoAttrs> nodeInfo) {
    return new VideoItemNode(nodeInfo);
  }

  @NonNull
  @Override
  protected VideoItemNode.VideoAttrs createUIAttrs() {
    return new VideoItemNode.VideoAttrs();
  }
}
