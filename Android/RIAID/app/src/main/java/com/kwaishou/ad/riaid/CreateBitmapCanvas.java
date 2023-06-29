package com.kwaishou.ad.riaid;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.kuaishou.riaid.adbrowser.canvas.ADCanvas;
import com.kwaishou.riaid_adapter.utils.DisplayUtil;

/**
 * 广告浏览器的画布，画布内有安全区的限定
 */
public class CreateBitmapCanvas extends FrameLayout implements ADCanvas {
  private static final String TAG = "CreateBitmapCanvas";

  /**
   * 广告展示的安全区
   */
  private RelativeLayout mSafeArea;

  private final int mWidth = DisplayUtil.dip2px(getContext(), 300);
  private final int mHeight = DisplayUtil.dip2px(getContext(), 300);

  public CreateBitmapCanvas(@NonNull Context context) {
    this(context, null);
  }

  public CreateBitmapCanvas(@NonNull Context context,
      @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CreateBitmapCanvas(@NonNull Context context, @Nullable AttributeSet attrs,
      int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public CreateBitmapCanvas(@NonNull Context context, @Nullable AttributeSet attrs,
      int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context);
  }

  private void init(Context context) {
    mSafeArea = new RelativeLayout(context);
    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mWidth, mHeight);
    // 居中
    layoutParams.gravity = Gravity.CENTER;
    mSafeArea.setLayoutParams(layoutParams);
    mSafeArea.setBackgroundColor(Color.parseColor("#4D4CAF50"));
    addView(mSafeArea);
  }

  @Override
  public int getCanvasWidth() {
    return mWidth;
  }

  /**
   * 底部导航栏的部分，是无法绘制广告的，不能计算进去
   *
   * @return 画布的高度
   */
  @Override
  public int getCanvasHeight() {
    return mHeight;
  }

  @NonNull
  @Override
  public RelativeLayout getCanvas() {
    return mSafeArea;
  }

}
