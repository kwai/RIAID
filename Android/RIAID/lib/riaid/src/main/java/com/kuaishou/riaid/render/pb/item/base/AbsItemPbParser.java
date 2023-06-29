package com.kuaishou.riaid.render.pb.item.base;

import java.util.Map;

import android.content.Context;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.item.base.AbsItemNode;
import com.kuaishou.riaid.render.pb.base.AbsObjectPbParser;

/**
 * 这个是item-pb-parser的单一组件基类
 *
 * @param <T> 这个attrs的泛型
 */
public abstract class AbsItemPbParser<T extends UIModel.Attrs, R extends AbsItemNode<T>>
    extends AbsObjectPbParser<T, R> {


  /**
   * 不需要重写此方法
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
    ADRenderLogger.d("key = " + node.key + " item transformSpecificPbData执行");
  }
}
