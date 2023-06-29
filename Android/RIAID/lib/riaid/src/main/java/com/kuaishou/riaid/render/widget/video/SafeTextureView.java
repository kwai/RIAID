package com.kuaishou.riaid.render.widget.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Copy自Snack Video，借助巨人的肩膀
 * 对TextureView的onDetachedFromWindow方法做catch处理，避免崩溃.
 */
public class SafeTextureView extends TextureView {
  public SafeTextureView(Context context) {
    super(context);
  }

  public SafeTextureView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SafeTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public SafeTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }


  @Override
  protected void onDetachedFromWindow() {
    try {
      super.onDetachedFromWindow();
    } catch (Throwable e) {
      // 许多机器上会有如下异常
      // java.lang.RuntimeException: Error during detachFromGLContext
      e.printStackTrace();
    }
  }

}
