package com.kuaishou.riaid.render.impl.touch.gesture;


import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.impl.touch.CustomGestureImpl;
import com.kuaishou.riaid.render.model.UIModel;

/**
 * 这个类是用来响应click，double-click，以及longPress封装类
 */
public class GestureDetector {

  @NonNull
  private final UIModel.NodeContext mContext;

  private android.view.GestureDetector mGestureDetector;

  @Nullable
  private ITouchListener mTouchListener;

  @Nullable
  private IHandlerListener mHandlerListener;

  @Nullable
  private IPressListener mPressListener;

  public void setTouchListener(@Nullable ITouchListener touchListener) {
    mTouchListener = touchListener;
  }

  public void setHandlerListener(@Nullable IHandlerListener handlerListener) {
    mHandlerListener = handlerListener;
  }

  public void setPressListener(@Nullable IPressListener pressListener) {
    mPressListener = pressListener;
  }

  public GestureDetector(@NonNull UIModel.NodeContext context) {
    this.mContext = context;
  }

  public void initGestureDetector(@NonNull View targetView) {
    CustomGestureImpl customGestureDelegate = new CustomGestureImpl();
    handlerGesture(targetView, customGestureDelegate);
  }


  public void initGestureDetector(@NonNull View targetView,
      @NonNull GestureInterceptor gestureInterceptor) {
    CustomGestureImpl customGestureDelegate = new CustomGestureImpl(gestureInterceptor);
    handlerGesture(targetView, customGestureDelegate);
  }

  @SuppressLint("ClickableViewAccessibility")
  private void handlerGesture(@NonNull View targetView, CustomGestureImpl customGestureDelegate) {
    customGestureDelegate.init(mContext);
    customGestureDelegate.touchListener = mTouchListener;
    customGestureDelegate.pressListener = mPressListener;
    customGestureDelegate.handlerListener = mHandlerListener;
    mGestureDetector =
        new android.view.GestureDetector(mContext.realContext, customGestureDelegate) {
          @Override
          public boolean onTouchEvent(MotionEvent ev) {
            customGestureDelegate.onTouchEvent(ev);
            return super.onTouchEvent(ev);
          }
        };
    targetView.setOnTouchListener((v, event) -> {
      mGestureDetector.onTouchEvent(event);
      return true;
    });
  }

  /**
   * 这个是动作接口封装
   */
  public interface IHandlerListener {

    /**
     * 单击
     */
    void onClick();

    /**
     * 双击
     */
    void onDoubleClick();

    /**
     * 长按
     */
    void onLongPress();

  }

  /**
   * 留给外界自定义的口子
   */
  public interface ITouchListener {

    /**
     * 把具体的触摸事件传递出去，如果有人愿意实现的话
     *
     * @param ev 具体的事件
     */
    void onTouchEvent(MotionEvent ev);

  }

  /**
   * 按压回调的接口
   */
  public interface IPressListener {

    /**
     * 触摸按压开始
     *
     * @param fromOutside 是不是外界触发的，举个栗子：如果是view自身设置的监听，触摸到该view上，
     *                    这个时候值就是false，如果当前组件处于一个盒子中（目前仅有button），按压盒子，
     *                    盒子组件整体要变成按压态，那么这个值就是true
     */
    default void onPressStart(boolean fromOutside) {}

    /**
     * 触摸按压结束，松手
     *
     * @param fromOutside 是不是外界触发的，举个栗子：如果是view自身设置的监听，触摸到该view上，
     *                    这个时候值就是false，如果当前组件处于一个盒子中（目前仅有button），按压盒子，
     *                    盒子组件整体要变成按压态，那么这个值就是true
     */
    default void onPressEnd(boolean fromOutside) {}

  }
}
