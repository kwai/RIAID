package com.kuaishou.riaid.render.node.layout.common;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.layout.base.AbsLayoutNode;
import com.kuaishou.riaid.render.util.LayoutPerformer;
import com.kuaishou.riaid.render.widget.ShadowView;

/**
 * 这个是有容器的盒子的通用基类
 * 适用于button，滚动容器等，如果按照默认做法，如果有背景background，那么就会帮你创建一个FrameLayout-A，
 * 但是这个FrameLayout可能不能满足你的诉求，你需要滚动，所以你又创建了一个ScrollView，此时所有的子组件需要放在
 * ScrollView里面，然后这个ScrollView仍然需要加在A中，造成了层级加深，而且，这个ScrollView父类是感知不到的，是子
 * 类的具体实现，所以AbsWithContainerLayoutNode也没有办法做一些通用操作，比如加背景（因为背景需要加在具
 * 体的容器中），所以这个基类不会创建通用容器，是子类自己定制容器。
 */
public abstract class AbsWithContainerLayoutNode<T extends UIModel.Attrs> extends AbsLayoutNode<T> {

  public AbsWithContainerLayoutNode(@NonNull NodeInfo<T> nodeInfo) {
    super(nodeInfo);
  }

  @Override
  protected boolean isDecor() {
    return true;
  }

  @Override
  public void onLayoutParams() {
    super.onLayoutParams();
    Drawable backgroundDrawable = mNodeInfo.attrs.backgroundDrawable;
    UIModel.Shadow shadow = mNodeInfo.attrs.shadow;
    ViewGroup container = getRealContainer();
    container.setAlpha(mNodeInfo.attrs.alpha);
    if (backgroundDrawable != null) {
      container.setBackground(backgroundDrawable);
    }
    if (shadow != null && shadowView != null) {
      // 证明是有Shadow,把容器加入到Shadow的容器
      int xOffset = ShadowView.getXPadding(shadow);
      int yOffset = ShadowView.getYPadding(shadow);
      LayoutPerformer.setMatchLayoutParams(container);
      LayoutPerformer.setMargins(container, xOffset, xOffset, yOffset, yOffset);
    } else {
      // 没有shadow的话，直接就是容器作为根容器
      LayoutPerformer.setFixedLayoutParams(container, size.width, size.height);
    }
    layoutGroupParams();
    // 坐标转换
    LayoutPerformer.requestLayoutByAbsolutePos(getRealView(), absolutePosition, shadow);
    setVisibility();
    // 遍历子View改变布局参数
    for (AbsObjectNode<?> childView : childList) {
      childView.onLayoutParams();
    }
  }

  /**
   * 这里子组件可以书写自己的布局参数
   */
  protected void layoutGroupParams() {

  }

  @Override
  protected void drawBackground(@NonNull ViewGroup decor) {
    super.drawBackground(decor);
    UIModel.Shadow shadow = mNodeInfo.attrs.shadow;
    ViewGroup container = getDecorView();
    if (shadow != null && shadowView != null) {
      // 证明是有Shadow,把容器加入到Shadow的容器
      LayoutPerformer.addView(shadowDecor, container);
    } else {
      // 没有shadow的话，直接就是容器作为根容器
      ADRenderLogger.d("key = " + mNodeInfo.context.key + " 没有shadow的话，直接就是内容容器作为根容器");
    }
    LayoutPerformer.addView(decor, getRealView());
  }

  @Nullable
  @Override
  public View getRealView() {
    return shadowDecor;
  }

  @NonNull
  @Override
  protected ViewGroup getDecorView() {
    return getRealContainer();
  }

  /**
   * 获取真实的容器，不能多次创建
   *
   * @return 返回容器
   */
  @NonNull
  protected abstract ViewGroup getRealContainer();

  @Override
  protected void refreshPressUI(@Nullable UIModel.Attrs attrs) {
    View realContainer = getRealContainer();
    if (attrs != null) {
      Drawable backgroundDrawable = attrs.backgroundDrawable;
      realContainer.setAlpha(attrs.alpha);
      if (backgroundDrawable != null) {
        realContainer.setBackground(backgroundDrawable);
      }
    }
  }
}
