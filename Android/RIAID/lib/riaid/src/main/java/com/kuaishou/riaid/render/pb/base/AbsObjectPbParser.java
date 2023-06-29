package com.kuaishou.riaid.render.pb.base;

import java.util.Map;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.proto.nano.Handler;
import com.kuaishou.riaid.proto.nano.Layout;
import com.kuaishou.riaid.proto.nano.LottieHandler;
import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.proto.nano.VideoHandler;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.util.Pb2Model;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * 这个是PB解析的基类
 */
public abstract class AbsObjectPbParser<T extends UIModel.Attrs, R extends AbsObjectNode<T>> {

  /**
   * 这个是解析的class，当前节点的类型
   *
   * @return 返回解析的class，可以理解为PB的class
   */
  protected abstract int getParseClassType();

  /**
   * 当前是不是节点类型是不是匹配的上
   */
  public boolean canParse(int classType) {
    return classType == getParseClassType();
  }

  /**
   * 把Pb解析成视图渲染的node
   *
   * @param context          这个context是用来创建node的，应为Parser对象是会被长期持有的，
   *                         如果把context当成一个内部成员变量，存在内存泄露的风险，避免这个风险
   * @param serviceContainer 这个是用来提供能力的容器，注入到各个node，在构造函数时候传入，
   *                         如果想获取能力，直接在里面获取即可
   * @param decorSize        这个是外界传递的画布的大小，对于node的尺寸的约束，即画布的尺寸
   * @param node             pb的model中的node对象
   * @param nodeCacheMap     这个map是用来映射key和node的
   * @return 返回转换解析好的node
   */
  @NonNull
  public R transformUIModelNode(@NonNull Context context,
      @NonNull IServiceContainer serviceContainer, @NonNull UIModel.Size decorSize,
      @NonNull Node node, @NonNull Map<Integer, AbsObjectNode<?>> nodeCacheMap) {
    // 准备好构建nodeInfo的参数
    UIModel.NodeContext uiNodeContext =
        createUINodeContext(context, node, decorSize, node.key);
    UIModel.Layout uiNodeLayout = createUINodeLayout(context, node.layout);
    T uiNodeAttributes =
        createUINodeAttributes(uiNodeContext, serviceContainer, createUIAttrs(),
            node.attributes, nodeCacheMap);
    // 构建nodeInfo
    AbsObjectNode.NodeInfo<T> nodeInfo = new AbsObjectNode.NodeInfo<>(
        uiNodeAttributes, uiNodeLayout, uiNodeContext, serviceContainer);
    nodeInfo.handler = createUINodeHandler(context, node.handler);
    nodeInfo.videoHandler = createUINodeVideoHandler(context, node.videoHandler);
    nodeInfo.lottieHandler = createUINodeLottieHandler(context, node.lottieHandler);
    // 创建渲染节点
    R uiNode = createUINode(nodeInfo);
    // 添加map
    ToolHelper.addKeyNode(uiNodeContext.key, uiNode, nodeCacheMap);
    // 上下文对象
    nodeInfo.context.viewWrapper = uiNode;
    // 解析 子节点
    transformSpecificPbData(context, serviceContainer, decorSize, node, uiNode, nodeCacheMap);
    return uiNode;
  }

  /**
   * 创建Context
   *
   * @param context   context
   * @param node      当前节点的数据model
   * @param decorSize 这个是外界的约束大小
   * @param key       node的标识
   */
  @NonNull
  private UIModel.NodeContext createUINodeContext(@NonNull Context context,
      @NonNull Node node, @NonNull UIModel.Size decorSize, int key) {
    UIModel.NodeContext nodeContext =
        new UIModel.NodeContext(node, context, decorSize);
    nodeContext.key = key;
    return nodeContext;
  }

  /**
   * 这个是用来解析属性的，映射成目标属性对象
   *
   * @param context          这个是node用的context包装类，存放着一些通用的必要的信息
   * @param serviceContainer 这个是外界提供的service服务容器
   * @param attrsUi          这个是目标属性对象，用来渲染的
   * @param attrsPb          这个是源属性对象，是Pb直接转换的model
   * @param nodeCacheMap     这个map是用来映射key和node的
   * @return 把转换好的UI的attrs对象返回
   */
  @NonNull
  protected T createUINodeAttributes(@NonNull UIModel.NodeContext context,
      @NonNull IServiceContainer serviceContainer, @NonNull T attrsUi, @Nullable Attributes attrsPb,
      @NonNull Map<Integer, AbsObjectNode<?>> nodeCacheMap) {
    // 这里解析通用属性
    if (attrsPb != null && attrsPb.common != null) {
      Pb2Model.copyAttrs(serviceContainer, context.realContext, attrsUi, attrsPb.common);
    }
    return attrsUi;
  }

  /**
   * 把Pb的layout属性转换成UIModel的layout属性对象
   *
   * @param context context
   * @param layout  pb的源layout属性对象
   * @return 把转换好的UI的layout对象返回
   */
  @NonNull
  protected UIModel.Layout createUINodeLayout(@NonNull Context context, @Nullable Layout layout) {
    if (layout != null) {
      return Pb2Model.transformLayout(context, layout);
    }
    return new UIModel.Layout();
  }

  /**
   * 把Pb的action属性转换成UIModel的action属性对象
   *
   * @param context context
   * @param handler pb的源handler属性对象
   * @return 把转换好的UI的handler对象返回
   */
  @Nullable
  protected UIModel.Handler createUINodeHandler(@NonNull Context context,
      @Nullable Handler handler) {
    if (handler != null) {
      return Pb2Model.transformHandler(handler);
    }
    return null;
  }

  /**
   * 把Pb的视频关键事件属性转换成UIModel的VideoHandler
   *
   * @param context context
   * @param handler pb的源handler属性对象
   * @return 把转换好的UI的handler对象返回
   */
  @Nullable
  protected UIModel.VideoHandler createUINodeVideoHandler(@NonNull Context context,
      @Nullable VideoHandler handler) {
    if (handler != null) {
      return Pb2Model.transformVideoHandler(handler);
    }
    return null;
  }


  /**
   * 把Pb的Lottie关键事件属性转换成UIModel的LottieHandler
   *
   * @param context context
   * @param handler pb的源handler属性对象
   * @return 把转换好的UI的handler对象返回
   */
  @Nullable
  protected UIModel.LottieHandler createUINodeLottieHandler(@NonNull Context context,
      @Nullable LottieHandler handler) {
    if (handler != null) {
      return Pb2Model.transformLottieHandler(handler);
    }
    return null;
  }

  /**
   * 创建具体的ui-node对象
   *
   * @return 创建完成并返回
   */
  @NonNull
  protected abstract R createUINode(@NonNull AbsObjectNode.NodeInfo<T> nodeInfo);

  /**
   * 创建具体的ui-attrs对象
   *
   * @return 创建完成并返回
   */
  @NonNull
  protected abstract T createUIAttrs();

  /**
   * 解析子组件
   *
   * @param context          context
   * @param serviceContainer 外界提供的service服务容器
   * @param decorSize        这个是外界传递的画布的大小，对于node的尺寸的约束
   * @param node             这个Pb数据的model对象
   * @param uiNode           这个是要渲染到视图用的数据node
   * @param nodeCacheMap     这个map是用来映射key和node的
   */
  protected abstract void transformSpecificPbData(@NonNull Context context,
      @NonNull IServiceContainer serviceContainer, @NonNull UIModel.Size decorSize,
      @NonNull Node node, @NonNull R uiNode, @NonNull Map<Integer, AbsObjectNode<?>> nodeCacheMap);

}
