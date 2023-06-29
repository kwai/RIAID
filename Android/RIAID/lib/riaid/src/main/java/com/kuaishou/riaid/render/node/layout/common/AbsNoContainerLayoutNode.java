package com.kuaishou.riaid.render.node.layout.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.layout.base.AbsLayoutNode;
import com.kuaishou.riaid.render.util.LayoutPerformer;
import com.kuaishou.riaid.render.widget.ShadowView;

/**
 * 这个是没有容器的盒子的通用基类
 * 适用于水平，垂直等布局，如果布局中存在背景drawable，会自动帮你创建一个FrameLayout容器，此容器的
 * 子组件（比如TextView，ImageView），直接放在这个容器即可，不需要单独特殊创建，所以叫NoContainer，而且这个容器
 * 的实现是在AbsNoContainerLayoutNode中加的，所以我们还可以做一些通用操作，比如加背景，设置透明度等。
 */
public abstract class AbsNoContainerLayoutNode<T extends UIModel.Attrs> extends AbsLayoutNode<T> {

  /**
   * 用来展示背景的
   */
  protected FrameLayout contentDecor = null;

  public AbsNoContainerLayoutNode(@NonNull NodeInfo<T> nodeInfo) {
    super(nodeInfo);
  }

  protected void loadLayoutBefore() {
    if (contentDecor != null) {
      LayoutPerformer.setPadding(getRealView(), mNodeInfo.layout.padding);
    }
  }

  @Override
  public void onLayoutParams() {
    super.onLayoutParams();
    UIModel.Shadow shadow = mNodeInfo.attrs.shadow;
    Context context = mNodeInfo.context.realContext;
    Drawable backgroundDrawable = mNodeInfo.attrs.backgroundDrawable;
    if (backgroundDrawable != null) {
      // 先准备好content容器对象
      contentDecor = contentDecor == null ? new FrameLayout(context) : contentDecor;
      if (shadow != null && shadowView != null) {
        // 配置好属性
        int xOffset = ShadowView.getXPadding(shadow);
        int yOffset = ShadowView.getYPadding(shadow);
        LayoutPerformer.setMatchLayoutParams(contentDecor);
        LayoutPerformer.setMargins(contentDecor, xOffset, xOffset, yOffset, yOffset);
      } else {
        // 没有shadow，content即可
        LayoutPerformer.setFixedLayoutParams(contentDecor, size.width, size.height);
      }
      // 配置background属性
      contentDecor.setBackground(backgroundDrawable);
      contentDecor.setAlpha(mNodeInfo.attrs.alpha);
    } else {
      ADRenderLogger.d("key = " + mNodeInfo.context.key + " 当前控件没有背景，不需要创建内容容器");
    }
    isDecor = getRealView() != null;
    LayoutPerformer.requestLayoutByAbsolutePos(getRealView(), absolutePosition, shadow);
    // 修改可见性
    setVisibility();
    // 遍历子View改变布局参数
    for (AbsObjectNode<?> childView : childList) {
      childView.onLayoutParams();
    }

  }

  @Override
  protected void drawBackground(@NonNull ViewGroup decor) {
    super.drawBackground(decor);
    UIModel.Shadow shadow = mNodeInfo.attrs.shadow;
    Drawable backgroundDrawable = mNodeInfo.attrs.backgroundDrawable;
    if (backgroundDrawable != null) {
      if (shadow != null && shadowView != null) {
        LayoutPerformer.addView(shadowDecor, contentDecor);
      } else {
        // 不用把content添加到shadow的容器中
        ADRenderLogger.d("key = " + mNodeInfo.context.key + " 当前控件无shadow有背景，不需要把背景添加到shadow容器");
      }
    } else {
      ADRenderLogger.d("key = " + mNodeInfo.context.key + " 当前控件没有背景，不需要考虑是否加入到shadow容器了");
    }
    // 把背景容器添加
    LayoutPerformer.addView(decor, getRealView());
  }

  @Nullable
  @Override
  public View getRealView() {
    return getDecorView();
  }

  @Nullable
  protected ViewGroup getDecorView() {
    return shadowDecor != null ? shadowDecor : contentDecor;
  }

  @Override
  protected void refreshPressUI(@Nullable UIModel.Attrs attrs) {
    if (contentDecor != null && attrs != null) {
      Drawable backgroundDrawable = attrs.backgroundDrawable;
      contentDecor.setAlpha(attrs.alpha);
      if (backgroundDrawable != null) {
        contentDecor.setBackground(backgroundDrawable);
      }
    }
  }

}
