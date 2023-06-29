package com.kuaishou.riaid.adbrowser.canvas;


import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowser;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;


/**
 * 广告画布，在广告渲染的时候，一般宿主会给一定的空间。把这个可以在其上叠加广告的空间称之为 ADCanvas。
 * 例如在 Feed 流广告中，把可以用来渲染广告的区域都称为 ADCanvas，这个画布仅仅提供给外层一个View，
 * 添加到自己的视图树中，添加到画布中的视图是{@link ADBrowser}内部自己调用的。
 */
public interface ADCanvas extends ViewParent {
  /**
   * 添加到 {@link com.kuaishou.riaid.adbrowser.ADDirector}
   * 中的{@link com.kuaishou.riaid.adbrowser.scene.ADScene}要求是
   * {@link RelativeLayout}，内部的约束关系，依赖于{@link RelativeLayout.LayoutParams}
   *
   * @return 相对布局
   */
  @NonNull
  RelativeLayout getCanvas();

  /**
   * @param view 要添加到画布中的视图
   */
  default void addADView(@NonNull View view) {
    if (view.getParent() == null) {
      getCanvas().addView(view);
    } else {
      ADBrowserLogger.e("FeedADCanvas 要添加的View.getParent不为空");
    }
  }

  /**
   * 常用于对Render的大小限制，或场景进出动画的赋值
   *
   * @return 画布宽度的尺寸
   */
  int getCanvasWidth();

  /**
   * 常用于对Render的大小限制，或场景进出动画的赋值
   *
   * @return 画布高度的尺寸
   */
  int getCanvasHeight();

  /**
   * @param view 要在画布中移除的视图
   */
  default void removeADView(@NonNull View view) {
    getCanvas().removeView(view);
  }

  /**
   * 移除画布所有视图
   */
  default void clear() {
    getCanvas().removeAllViews();
  }
}
