package com.kuaishou.riaid.render.impl.touch.gesture;

import android.view.MotionEvent;

/**
 * 针对 {@link android.view.GestureDetector}的拦截器，
 * 主要是拦截{@link com.kuaishou.riaid.render.impl.touch.CustomGestureImpl}中定义的点击、双击行为等。
 */
public interface GestureInterceptor {

  /**
   * 单击，如果拦截成功，则返回true
   */
  default boolean onSingleTapConfirmed(MotionEvent e) {
    return false;
  }

  /**
   * 双击，如果拦截成功，则返回true
   */
  default boolean onDoubleTap(MotionEvent e) {
    return false;
  }

  /**
   * 长按，如果拦截成功，则返回true
   */
  default boolean onLongPress(MotionEvent e) {
    return false;
  }
}
