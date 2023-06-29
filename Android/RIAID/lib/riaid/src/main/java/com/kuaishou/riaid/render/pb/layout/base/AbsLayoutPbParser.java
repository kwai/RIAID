package com.kuaishou.riaid.render.pb.layout.base;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.layout.base.AbsLayoutNode;
import com.kuaishou.riaid.render.pb.PbParser;
import com.kuaishou.riaid.render.pb.base.AbsObjectPbParser;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * 这个是layout-pb-parser盒子基类
 *
 * @param <T> 这个attrs的泛型
 */
public abstract class AbsLayoutPbParser<T extends UIModel.Attrs, R extends AbsLayoutNode<T>>
    extends AbsObjectPbParser<T, R> {


  /**
   * 目前用来解析子组件
   *
   * @param context          context
   * @param serviceContainer 外界提供的service服务容器
   * @param decorSize        这个是外界传递的画布的大小，对于node的尺寸的约束
   * @param node             这个Pb数据的model对象
   * @param uiNode           这个是要渲染到视图用的数据node
   * @param nodeCacheMap     这个map是用来映射key和node的
   */
  @Override
  protected void transformSpecificPbData(@NonNull Context context,
      @NonNull IServiceContainer serviceContainer, @NonNull UIModel.Size decorSize,
      @NonNull Node node, @NonNull R uiNode, @NonNull Map<Integer, AbsObjectNode<?>> nodeCacheMap) {
    if (node.children != null && node.children.length > 0) {
      List<AbsObjectNode<?>> childRenderList =
          PbParser.getInstance().parseChildrenPbModel(context, serviceContainer, decorSize,
              Arrays.asList(node.children), nodeCacheMap);
      if (ToolHelper.isListValid(childRenderList)) {
        uiNode.addAllViews(childRenderList);
      }
    }
  }
}
