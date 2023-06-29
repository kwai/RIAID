package com.kuaishou.riaid.render.impl.touch;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.impl.touch.gesture.GestureDetector;
import com.kuaishou.riaid.render.impl.touch.gesture.GestureInterceptor;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;

/**
 * 自定义的手势监听,可以构造注入{@link GestureInterceptor}，来拦截手势。
 */
public class CustomGestureImpl extends SimpleOnGestureListener implements
    GestureDetector.IPressListener, GestureDetector.ITouchListener {

  @Nullable
  public GestureDetector.ITouchListener touchListener;

  @Nullable
  public GestureDetector.IHandlerListener handlerListener;

  @Nullable
  public GestureDetector.IPressListener pressListener;

  /**
   * 当前Node的信息
   */
  private UIModel.NodeContext mContext;

  /**
   * 手势拦截器，例如富文本的点击事件需要特殊处理
   */
  @Nullable
  private GestureInterceptor mGestureInterceptor;

  public CustomGestureImpl() {
  }

  public CustomGestureImpl(@Nullable GestureInterceptor gestureInterceptor) {
    mGestureInterceptor = gestureInterceptor;
  }

  /**
   * 这里用来初始化一些必要参数信息
   *
   * @param context node信息
   */
  public void init(@Nullable UIModel.NodeContext context) {
    this.mContext = context;
  }

  @Override
  public boolean onSingleTapConfirmed(MotionEvent e) {
    ADRenderLogger.i("key = " + mContext.key + " onSingleTapConfirmed");
    boolean isIntercept =
        mGestureInterceptor != null && mGestureInterceptor.onSingleTapConfirmed(e);
    // 如果点击没有被拦截，才能继续执行点击监听
    if (!isIntercept && handlerListener != null) {
      handlerListener.onClick();
    }
    return super.onSingleTapConfirmed(e);
  }

  @Override
  public boolean onDoubleTap(MotionEvent e) {
    ADRenderLogger.i("key = " + mContext.key + " onDoubleTap");
    boolean isIntercept =
        mGestureInterceptor != null && mGestureInterceptor.onDoubleTap(e);
    // 如果点击没有被拦截，才能继续执行点击监听
    if (!isIntercept && handlerListener != null) {
      handlerListener.onDoubleClick();
    }
    return super.onDoubleTap(e);
  }

  @Override
  public void onLongPress(MotionEvent e) {
    ADRenderLogger.i("key = " + mContext.key + " onLongPress");
    boolean isIntercept =
        mGestureInterceptor != null && mGestureInterceptor.onLongPress(e);
    // 如果点击没有被拦截，才能继续执行点击监听
    if (!isIntercept && handlerListener != null) {
      handlerListener.onLongPress();
    }
    super.onLongPress(e);
  }

  public void onTouchEvent(MotionEvent event) {
    if (pressListener != null) {
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          pressListener.onPressStart(false);
          break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
          pressListener.onPressEnd(false);
          break;
      }
    }
    if (touchListener != null) {
      // 继续把事件放出去，如果外界需要的话
      touchListener.onTouchEvent(event);
    }
  }

  @Override
  public void onPressStart(boolean fromOutside) {
    ADRenderLogger.i("key = " + mContext.key + " onPressStart");
    if (pressListener != null) {
      pressListener.onPressStart(false);
    }
  }

  @Override
  public void onPressEnd(boolean fromOutside) {
    ADRenderLogger.i("key = " + mContext.key + " onPressEnd");
    if (pressListener != null) {
      pressListener.onPressEnd(false);
    }
  }

}
