package com.kuaishou.riaid.render.node.item.base;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.util.LayoutPerformer;
import com.kuaishou.riaid.render.widget.ShadowView;

/**
 * 这个就是具体展示的子View，比如Text，Image
 */
public abstract class AbsItemNode<T extends UIModel.Attrs> extends AbsObjectNode<T> {


  public AbsItemNode(@NonNull NodeInfo<T> nodeInfo) {
    super(nodeInfo);
  }

  @Override
  public void loadLayout() {
    LayoutPerformer.setPadding(getItemRealView(), mNodeInfo.layout.padding);
  }

  /**
   * 当以View可能不需要重写这个方法,给一个空实现吧
   */
  @Override
  public void onLayout() {
  }

  @Override
  public void onLayoutParams() {
    super.onLayoutParams();
    // 真实View会添加，需要增加margin
    UIModel.Shadow shadow = mNodeInfo.attrs.shadow;
    if (shadow != null && shadowView != null) {
      // 证明有阴影
      int xOffset = ShadowView.getXPadding(shadow);
      int yOffset = ShadowView.getYPadding(shadow);
      LayoutPerformer.setMatchLayoutParams(getItemRealView());
      LayoutPerformer.setMargins(getItemRealView(), xOffset, xOffset, yOffset, yOffset);
    } else {
      LayoutPerformer.setFixedLayoutParams(getItemRealView(), size.width, size.height);
    }
    // 设置可见性
    setVisibility();
    // 提供一个方法，让外界修改布局参数
    layoutItemParams();
    // 修改margin参数，改变坐标
    LayoutPerformer.requestLayoutByAbsolutePos(getRealView(), absolutePosition, shadow);
  }

  /**
   * 这里子组件可以书写自己的布局参数
   */
  protected void layoutItemParams() {

  }

  @Override
  protected void drawBackground(@NonNull ViewGroup decor) {
    super.drawBackground(decor);
    UIModel.Shadow shadow = mNodeInfo.attrs.shadow;
    if (shadow != null && shadowView != null) {
      // 把正式的View添加到根容器
      LayoutPerformer.addView(shadowDecor, getItemRealView());
    } else {
      ADRenderLogger.d("key = " + mNodeInfo.context.key + " 当前控件没有shadow，不需要在画布中增加shadow根容器");
    }
  }

  @Nullable
  protected abstract View getItemRealView();

  @Nullable
  @Override
  public View getRealView() {
    return shadowDecor != null ? shadowDecor : getItemRealView();
  }

  @Override
  @CallSuper
  public void draw(@NonNull ViewGroup decor) {
    LayoutPerformer.addView(decor, getRealView());
  }
}
