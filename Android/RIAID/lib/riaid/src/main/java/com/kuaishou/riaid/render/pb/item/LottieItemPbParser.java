package com.kuaishou.riaid.render.pb.item;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.item.LottieItemNode;
import com.kuaishou.riaid.render.pb.item.base.AbsItemPbParser;
import com.kuaishou.riaid.render.service.base.IDataBindingService;
import com.kuaishou.riaid.render.util.Pb2Model;

/**
 * 这个是负责把pb的lottie的model对象，转换成渲染用的model对象
 */
public class LottieItemPbParser
    extends AbsItemPbParser<LottieItemNode.LottieAttrs, LottieItemNode> {

  @Override
  protected int getParseClassType() {
    return Node.CLASS_TYPE_ITEM_LOTTIE;
  }

  @NonNull
  @Override
  protected LottieItemNode.LottieAttrs createUINodeAttributes(@NonNull UIModel.NodeContext context,
      @NonNull IServiceContainer serviceContainer, @NonNull LottieItemNode.LottieAttrs attrsUi,
      @Nullable Attributes attrsPb, @NonNull Map<Integer, AbsObjectNode<?>> nodeCacheMap) {
    if (attrsPb != null && attrsPb.lottie != null) {
      IDataBindingService service = serviceContainer.getService(IDataBindingService.class);
      Pb2Model.transformLottieAttrs(context.realContext, service, attrsUi, attrsPb.lottie);
    }
    return super.createUINodeAttributes(context, serviceContainer, attrsUi, attrsPb, nodeCacheMap);
  }

  @NonNull
  @Override
  protected LottieItemNode createUINode(
      @NonNull AbsObjectNode.NodeInfo<LottieItemNode.LottieAttrs> nodeInfo) {
    return new LottieItemNode(nodeInfo);
  }

  @NonNull
  @Override
  protected LottieItemNode.LottieAttrs createUIAttrs() {
    return new LottieItemNode.LottieAttrs();
  }
}
