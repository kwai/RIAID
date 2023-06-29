package com.kuaishou.riaid.render.node.base;

import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.proto.nano.ButtonAttributes;
import com.kuaishou.riaid.render.impl.touch.gesture.GestureDetector;
import com.kuaishou.riaid.render.interf.IRealViewWrapper;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.layout.base.AbsLayoutNode;
import com.kuaishou.riaid.render.util.LayoutPerformer;
import com.kuaishou.riaid.render.util.ToolHelper;
import com.kuaishou.riaid.render.widget.ShadowView;

/**
 * 这个是所有渲染的基类，控制组件的UI样式层面
 * 正确的流程  loadAttributes -- loadLayout -- onMeasure -- onLayout -- onLayoutParams -- onDraw
 */
public abstract class AbsObjectNode<T extends UIModel.Attrs>
    implements GestureDetector.IPressListener, IRealViewWrapper {

  public AbsObjectNode(@NonNull NodeInfo<T> nodeInfo) {
    this.mNodeInfo = nodeInfo;
  }

  /**
   * 当前View的绝对坐标
   */
  @NonNull
  public final Rect absolutePosition = new Rect();

  /**
   * 当前View的宽高
   */
  @NonNull
  public final UIModel.Size size = new UIModel.Size();

  /**
   * 这个是Render需要的基本信息
   */
  @NonNull
  public final NodeInfo<T> mNodeInfo;

  /**
   * 当前View的parent
   */
  @Nullable
  public AbsLayoutNode<?> parentView = null;

  /**
   * 渲染View绑定样式数据,具体的数据源是在T中
   */
  public abstract void loadAttributes();

  /**
   * 将要变化的目标属性
   */
  @Nullable
  public IViewInfo showingViewInfo = null;

  /**
   * shadow的view
   */
  @Nullable
  public ShadowView shadowView = null;

  /**
   * 用来展示shadow的
   */
  protected FrameLayout shadowDecor = null;

  /**
   * 绑定布局属性
   */
  public abstract void loadLayout();

  /**
   * View的自己测量，算出自己的宽高
   * 如果是子View，需要在VG的约束下测量出来
   * 如果是VG，那么有可能是服务端指定好的，也有可能是通过子View反哺的
   *
   * @param widthSpec  这个是父View的最大宽度边界
   * @param heightSpec 这个是父View的最大高度边界
   */
  public abstract void onMeasure(int widthSpec, int heightSpec);

  /**
   * 排布子View
   * 只有VG才会用的到
   */
  public abstract void onLayout();

  /**
   * 绑定布局参数，这样，在onDraw的时候添加View，确保布局参数已经设置到每一个View上
   */
  @CallSuper
  public void onLayoutParams() {
    // 坐标转换，确定相对真实父布局的内部偏移量
    LayoutPerformer.transformPos(this, size, absolutePosition);
    // 确定Decor的布局参数
    UIModel.Shadow shadow = mNodeInfo.attrs.shadow;
    Context context = mNodeInfo.context.realContext;
    // 改变shadow的尺寸
    if (shadow != null) {
      // 保证值创建一次，也能延迟创建的作用，美滋滋，我ZTM机智
      shadowDecor = shadowDecor == null ? new FrameLayout(context) : shadowDecor;
      shadowView = shadowView == null ? new ShadowView(context) : shadowView;
      int shadowWidth = size.width + ShadowView.getXPadding(shadow) * 2;
      int shadowHeight = size.height + ShadowView.getYPadding(shadow) * 2;
      // 确定根容器的尺寸
      LayoutPerformer.setFixedLayoutParams(shadowDecor, shadowWidth, shadowHeight);
      // 设置shadowView布局撑满
      LayoutPerformer.setMatchLayoutParams(shadowView);
      // 设置shadow
      shadowView.setShadow(shadow);
    } else {
      ADRenderLogger.d("key = " + mNodeInfo.context.key + " 当前控件没有shadow，不需要创建shadow容器");
    }
  }

  /**
   * 这个是绘制的具体流程
   * 1 --- 坐标转换计算出绝对坐标
   * 2 --- 绘制背景
   * 3 --- 绘制自身，包括自己的孩子
   * 4 --- 绘制前景
   *
   * @param decor 根View
   */
  public final void onDraw(@NonNull ViewGroup decor) {
    // 绘制背景
    drawBackground(decor);
    // 绘制自己
    draw(decor);
    // 绘制前景
    drawForeground(decor);
  }

  /**
   * 绘制背景
   *
   * @param decor 根View
   */
  @CallSuper
  protected void drawBackground(@NonNull ViewGroup decor) {
    if (shadowView != null) {
      // 证明shadow的根容器已经创建，添加shadowView
      LayoutPerformer.addView(shadowDecor, shadowView);
    }
  }

  /**
   * 绘制自身，包括绘制先绘制自己，在绘制孩子
   * 这个方式是必须要实现的
   *
   * @param decor 根View
   */
  public abstract void draw(@NonNull ViewGroup decor);

  /**
   * 绘制前景
   *
   * @param decor 根View
   */
  protected void drawForeground(@NonNull ViewGroup decor) {

  }

  /**
   * 填充按压属性,
   * 目前主要是Button填充
   */
  public void inflatePressAttrs(@NonNull List<ButtonAttributes.HighlightState> pressStateList) {}

  /**
   * 获取真实的视图View
   */
  @Nullable
  @Override
  public View getRealView() {
    return null;
  }

  @NonNull
  @Override
  public IViewInfo getCurrentViewInfo() {

    return new IViewInfo() {

      @NonNull
      @Override
      public UIModel.Size getRealViewSize() {
        return ToolHelper.formatSize(size, mNodeInfo.attrs.shadow);
      }

      @Override
      public float getRealViewAlpha() {
        return mNodeInfo.attrs.alpha;
      }

      @NonNull
      @Override
      public Rect getRealViewPosition() {
        return ToolHelper.formatAbsolutePosition(absolutePosition, mNodeInfo.attrs.shadow);
      }
    };
  }

  @CallSuper
  @Override
  public void updateViewInfo(@Nullable IViewInfo showingViewInfo) {
    if (showingViewInfo != null) {
      mNodeInfo.attrs.alpha = showingViewInfo.getRealViewAlpha();
      UIModel.Size realViewSize = showingViewInfo.getRealViewSize();
      Rect realViewPosition = showingViewInfo.getRealViewPosition();
      size.width = realViewSize.width;
      size.height = realViewSize.height;
      absolutePosition.left = realViewPosition.left;
      absolutePosition.top = realViewPosition.top;
      absolutePosition.right = realViewPosition.right;
      absolutePosition.bottom = realViewPosition.bottom;
    }
    this.showingViewInfo = null;
  }

  @Nullable
  @Override
  public IViewInfo getShowingViewInfo() {
    return showingViewInfo;
  }

  /**
   * 判断当前render的key是不是匹配的上
   *
   * @param keyList 这个是render的key的集合，用来找合适的render
   * @return 返回true代表匹配的上，false代表匹配不上
   */
  protected final boolean canMatchRenderKey(@Nullable List<Integer> keyList) {
    if (ToolHelper.isListValid(keyList)) {
      for (Integer key : keyList) {
        if (key == mNodeInfo.context.key) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @param eventType  场景值，比如下载场景
   * @param keyList    这个是render的key的集合，用来找合适的render,如果为空(list=null或者list=empty)，
   *                   直接默认匹配，如果不为空，只会匹配list中存在的key
   * @param attributes 属性json，需要修改什么属性
   * @return 返回处理的结果，是不是有组件有效消费了
   */
  public boolean dispatchEvent(@NonNull String eventType, @Nullable List<Integer> keyList,
      @Nullable Attributes attributes) {
    return false;
  }

  /**
   * 获取需要的Service服务
   *
   * @param <Service> service的class类型
   * @return 返回容器中的具体service
   */
  protected <Service> Service getService(@NonNull Class<Service> clazz) {
    return mNodeInfo.serviceContainer.getService(clazz);
  }

  /**
   * 设置当前View的透明度
   */
  protected void setVisibility() {
    View realView = getRealView();
    if (realView != null) {
      boolean hidden = mNodeInfo.attrs.hidden;
      realView.setVisibility(hidden ? View.INVISIBLE : View.VISIBLE);
    }
  }

  /**
   * 这个Render需要的数据
   *
   * @param <T> 属性attrs的泛型
   */
  public static class NodeInfo<T extends UIModel.Attrs> {

    // 用于渲染自身的属性
    @NonNull
    public T attrs;
    // 这个是按压的状态，默认为空
    @Nullable
    public T pressAttrs;
    // 具体的交互响应行为
    @Nullable
    public UIModel.Handler handler;
    // 具体的视频响应行为
    @Nullable
    public UIModel.VideoHandler videoHandler;
    // 具体的Lottie响应行为
    @Nullable
    public UIModel.LottieHandler lottieHandler;
    // 自身的布局属性
    @NonNull
    public UIModel.Layout layout;
    // render的上下文context
    @NonNull
    public UIModel.NodeContext context;
    // 组件需要的服务
    @NonNull
    public IServiceContainer serviceContainer;

    /**
     * 构造函数，非空属性，通过构造函数来约束
     *
     * @param attrs            render渲染需要的样式属性
     * @param layout           render的布局属性
     * @param context          render的context，存放一些render的重要信息，方便外界使用
     * @param serviceContainer 外界提供的service服务的容器
     */
    public NodeInfo(@NonNull T attrs, @NonNull UIModel.Layout layout,
        @NonNull UIModel.NodeContext context, @NonNull IServiceContainer serviceContainer) {
      this.attrs = attrs;
      this.layout = layout;
      this.context = context;
      this.serviceContainer = serviceContainer;
    }
  }
}
