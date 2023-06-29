package com.kwaishou.ad.riaid;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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
public class ADBrowserCanvas extends FrameLayout implements ADCanvas {
  private static final String TAG = "ADBrowserCanvas";

  /**
   * 广告展示的安全区
   */
  private RelativeLayout mSafeArea;

  public ADBrowserCanvas(@NonNull Context context) {
    this(context, null);
  }

  public ADBrowserCanvas(@NonNull Context context,
      @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ADBrowserCanvas(@NonNull Context context, @Nullable AttributeSet attrs,
      int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public ADBrowserCanvas(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context);
  }

  private void init(Context context) {
    LayoutInflater.from(context).inflate(R.layout.riaid_canvas_layout, this);
    mSafeArea = findViewById(R.id.riaid_canvas_safe_area);
  }

  @Override
  public int getCanvasWidth() {
    return DisplayUtil.getScreenRealWidth(getContext());
  }

  /**
   * 底部导航栏的部分，是无法绘制广告的，不能计算进去
   *
   * @return 画布的高度
   */
  @Override
  public int getCanvasHeight() {
    return DisplayUtil.getScreenRealHeight(getContext());
  }

  @NonNull
  @Override
  public RelativeLayout getCanvas() {
    return mSafeArea;
  }

}
