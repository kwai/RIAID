package com.kuaishou.riaid.render.interf;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.model.UIModel;

/**
 * 定义一个接口，获取真实的View，以及获取View相关的信息
 */
public interface IRealViewWrapper {

  /**
   * 获取正式的渲染View
   */
  @Nullable
  View getRealView();

  /**
   * @return 支持手势的view，如单击、长按等
   */
  @Nullable
  default View getGestureView() {
    return null;
  }

  /**
   * 获取当前展示View的信息
   */
  @NonNull
  IViewInfo getCurrentViewInfo();

  /**
   * 获取复用动画，接下来需要展示的信息
   */
  @Nullable
  IViewInfo getShowingViewInfo();

  /**
   * 更新当前节点信息
   */
  void updateViewInfo(@Nullable IViewInfo showingViewInfo);

  /**
   * 这个定义的是View的信息
   */
  interface IViewInfo {

    /**
     * 获取View的尺寸大小
     *
     * @return 返回当前View的尺寸
     */
    @NonNull
    UIModel.Size getRealViewSize();

    /**
     * 获取当前View的透明度
     *
     * @return 返回当前View的透明度
     */
    float getRealViewAlpha();

    /**
     * 返回当前View的绝对坐标，相对于顶层画布
     *
     * @return 返回绝对坐标
     */
    @NonNull
    Rect getRealViewPosition();

  }

}
