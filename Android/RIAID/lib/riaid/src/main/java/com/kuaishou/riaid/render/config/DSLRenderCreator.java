package com.kuaishou.riaid.render.config;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.proto.nano.RIAIDConstants;
import com.kuaishou.riaid.render.impl.inner.FindNodeByKeyImpl;
import com.kuaishou.riaid.render.interf.IRealViewWrapper;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.service.ServiceContainer;
import com.kuaishou.riaid.render.service.base.IDataBindingService;
import com.kuaishou.riaid.render.service.base.ILoadImageService;
import com.kuaishou.riaid.render.service.base.IMediaPlayerService;
import com.kuaishou.riaid.render.service.base.IRIAIDLogReportService;
import com.kuaishou.riaid.render.service.base.IResumeActionService;
import com.kuaishou.riaid.render.service.base.render.IRenderService;
import com.kuaishou.riaid.render.service.inner.IFindNodeByKeyService;
import com.kuaishou.riaid.render.service.inner.NodeDurationService;
import com.kuaishou.riaid.render.util.DefaultHelper;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * 这个是外界构建使用的入口
 */
public class DSLRenderCreator {

  private DSLRenderCreator() {}

  /**
   * 原始Json数据源
   */
  @Nullable
  private Node mData;

  /**
   * 渲染好的虚拟树的头节点,这个属性暴露出来
   */
  @Nullable
  public AbsObjectNode<?> rootRender;

  /**
   * 这个是宿主约束的宽度，如果宿主有约束的话，默认是不给任何约束的，
   * 默认值参考{@link Builder#mMaxWidth}类的定义，尺寸相关的都是pixel
   */
  private int mMaxWidth;

  /**
   * 这个是宿主约束的高度，如果宿主有约束的话，默认是不给任何约束的，
   * 默认值参考{@link Builder#mMaxHeight}类的定义，尺寸相关的都是pixel
   */
  private int mMaxHeight;

  /**
   * 外界提供的service容器，里面有render需要的各种service
   */
  @NonNull
  private final IServiceContainer mServiceContainer = new ServiceContainer();

  /**
   * 开始构建虚拟树
   */
  @Nullable
  public View render(@NonNull Context context) {
    // 如果data都没有，或者service都么有，还渲染个锤锤呀
    if (mData == null) {
      ADRenderLogger.w("mData == null，上层传递的数据有问题");
      return null;
    }
    // 外界的尺寸约束
    UIModel.Size decorSize = new UIModel.Size();
    decorSize.width = mMaxWidth;
    decorSize.height = mMaxHeight;

    long currentTimeMillis = System.currentTimeMillis();
    // 创建容器
    HashMap<Integer, AbsObjectNode<?>> nodeCacheMap = new HashMap<>();
    // 解析
    rootRender = DSLRenderCore.createInstance()
        .parsePbSourceData(context, mServiceContainer, decorSize, mData, nodeCacheMap);
    // 注册内部服务
    mServiceContainer
        .registerService(IFindNodeByKeyService.class, new FindNodeByKeyImpl(nodeCacheMap));
    mServiceContainer.registerService(NodeDurationService.class, new NodeDurationService());
    long duration = System.currentTimeMillis() - currentTimeMillis;
    // 上报Node构建时长埋点
    ToolHelper.reportStandardDuration(
        RIAIDConstants.Standard.RENDER_BUILD_DURATION,
        mServiceContainer.getService(IRIAIDLogReportService.class),
        duration);
    ToolHelper.renderTotalDuration(rootRender, duration);
    // 渲染
    return DSLRenderCore.createInstance().renderRootView(context, rootRender);
  }

  /**
   * 刷新整体卡片的布局
   */
  public void requestLayout() {
    // 如果顶层的容器或者节点是空的，证明是无效的
    if (rootRender != null) {
      DSLRenderCore.createInstance().requestLayout(rootRender);
    }
  }

  /**
   * 场景过渡动画复用
   *
   * @param context context
   * @param render  将要展示的render对象
   * @return 如果所有的组件都是复用的，就分会要动画的ViewWrapper集合，
   * 如果不合法，不符合复用组件过渡动画，直接返回null，或者emptyList（这里判断是否合法，
   * 就是两个render的组件是不是完全一致，数量和key都是一致的）
   */
  @Nullable
  public List<IRealViewWrapper> diffRender(@NonNull Context context, @NonNull Node render) {
    // 外界的尺寸约束
    UIModel.Size decorSize = new UIModel.Size();
    decorSize.width = mMaxWidth;
    decorSize.height = mMaxHeight;

    // 创建新的容器
    // 创建容器
    HashMap<Integer, AbsObjectNode<?>> showingNodeCacheMap = new HashMap<>();
    // 解析构建tree
    AbsObjectNode<?> showingRootRender = DSLRenderCore.createInstance()
        .parsePbSourceData(context, mServiceContainer, decorSize, render, showingNodeCacheMap);
    // 进行构建，测量尺寸位置
    DSLRenderCore.createInstance().renderRootView(context, showingRootRender);
    // 获取到之前的nodeCacheMap
    IFindNodeByKeyService service = rootRender == null ? null
        : rootRender.mNodeInfo.serviceContainer.getService(IFindNodeByKeyService.class);
    return ToolHelper
        .diffRenderTree(service == null ? null : service.getNodeCacheMap(), showingNodeCacheMap);
  }

  /**
   * 更新render的tree
   */
  public void updateRenderTree() {
    if (rootRender != null) {
      rootRender.updateViewInfo(rootRender.showingViewInfo);
    }
  }

  public static class Builder {

    @Nullable
    private Node data;
    @NonNull
    private final IRenderService mRenderService;
    private int mMaxWidth = DefaultHelper.UNSPECIFIED;
    private int mMaxHeight = DefaultHelper.UNSPECIFIED;

    public Builder(@NonNull IRenderService renderService) {
      this.mRenderService = renderService;
    }

    public Builder withPbData(Node data) {
      this.data = data;
      return this;
    }

    public Builder withMaxWidth(int maxWidth) {
      this.mMaxWidth = maxWidth;
      return this;
    }

    public Builder withMaxHeight(int maxHeight) {
      this.mMaxHeight = maxHeight;
      return this;
    }

    public DSLRenderCreator build() {
      DSLRenderCreator dslRender = new DSLRenderCreator();
      dslRender.mData = data;
      dslRender.mMaxWidth = mMaxWidth;
      dslRender.mMaxHeight = mMaxHeight;
      dslRender.mServiceContainer
          .registerService(IResumeActionService.class, mRenderService.getResumeActionService());
      dslRender.mServiceContainer
          .registerService(IDataBindingService.class, mRenderService.getDataBindingService());
      dslRender.mServiceContainer
          .registerService(ILoadImageService.class, mRenderService.getLoadImageService());
      dslRender.mServiceContainer
          .registerService(IRIAIDLogReportService.class, mRenderService.getRIAIDLogReportService());
      dslRender.mServiceContainer
          .registerService(IMediaPlayerService.class, mRenderService.getMediaService());
      return dslRender;
    }
  }
}
