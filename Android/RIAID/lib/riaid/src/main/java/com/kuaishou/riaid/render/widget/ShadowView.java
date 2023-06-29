package com.kuaishou.riaid.render.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.model.UIModel;

/**
 * 这个是用来实现自定义View的
 */
public class ShadowView extends View {

  /**
   * 或者shadow的路径
   */
  private final Path mPath = new Path();

  /**
   * 绘制阴影的画笔
   */
  private final Paint mPaint = new Paint();

  /**
   * 强制刷新Shadow，调用requestLayout触发
   */
  private boolean mForceInvalidateShadow = false;

  /**
   * 尺寸改变，也要改变Shadow
   */
  private boolean mInvalidateShadowOnSizeChanged = true;

  /**
   * shadow的属性值
   */
  private UIModel.Shadow mShadow = new UIModel.Shadow();

  public ShadowView(Context context) {
    super(context);
    init();
  }

  public ShadowView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ShadowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  /**
   * 前置初始化，能够重复用的一些属性
   */
  private void init() {
    mPaint.setAntiAlias(true);
    mPaint.setStyle(Paint.Style.FILL);
  }

  @Override
  protected int getSuggestedMinimumWidth() {
    return 0;
  }

  @Override
  protected int getSuggestedMinimumHeight() {
    return 0;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    if (w > 0 && h > 0 &&
        (getBackground() == null || mInvalidateShadowOnSizeChanged || mForceInvalidateShadow)) {
      mForceInvalidateShadow = false;
      setBackgroundCompat(w, h);
    }
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    if (mForceInvalidateShadow) {
      mForceInvalidateShadow = false;
      setBackgroundCompat(right - left, bottom - top);
    }
  }

  public void setInvalidateShadowOnSizeChanged(boolean invalidateShadowOnSizeChanged) {
    mInvalidateShadowOnSizeChanged = invalidateShadowOnSizeChanged;
  }

  private void invalidateShadow() {
    mForceInvalidateShadow = true;
    requestLayout();
    invalidate();
  }

  public void setShadow(@Nullable UIModel.Shadow shadow) {
    if (shadow != null) {
      this.mShadow = shadow;
      int xPadding = getXPadding(shadow);
      int yPadding = getYPadding(shadow);
      setPaddingRelative(xPadding, yPadding, xPadding, yPadding);
      invalidateShadow();
    }
  }

  public static int getXPadding(@NonNull UIModel.Shadow shadow) {
    return (int) (shadow.radius + Math.abs(shadow.offsetX));
  }

  public static int getYPadding(@NonNull UIModel.Shadow shadow) {
    return (int) (shadow.radius + Math.abs(shadow.offsetY));
  }

  @SuppressWarnings("deprecation")
  private void setBackgroundCompat(int w, int h) {
    Bitmap bitmap =
        createShadowBitmap(w, h, mShadow.cornerRadius, mShadow.radius, mShadow.offsetX,
            mShadow.offsetY, mShadow.color, Color.TRANSPARENT);
    BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
      setBackgroundDrawable(drawable);
    } else {
      setBackground(drawable);
    }
  }

  /**
   * 创建Shadow的背景Bitmap
   *
   * @param shadowWidth  阴影的宽度
   * @param shadowHeight 阴影的高度
   * @param cornerRadius 阴影矩形的圆角
   * @param shadowRadius shadow的模糊半径
   * @param dx           阴影X轴偏移量
   * @param dy           阴影Y轴偏移量
   * @param shadowColor  阴影的颜色
   * @param fillColor    填充色，默认都是透明色
   * @return 返回绘制好的Bitmap
   */
  private Bitmap createShadowBitmap(
      int shadowWidth, int shadowHeight, @NonNull UIModel.CornerRadius cornerRadius,
      float shadowRadius, float dx, float dy, int shadowColor, int fillColor) {

    Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ALPHA_8);

    Canvas canvas = new Canvas(output);

    RectF shadowRect = new RectF(shadowRadius, shadowRadius, shadowWidth - shadowRadius,
        shadowHeight - shadowRadius);

    // 重新修改布局范围，阴影看起来会好看很多
    if (dy > 0) {
      shadowRect.top += dy;
      shadowRect.bottom -= dy;
    } else if (dy < 0) {
      shadowRect.top += Math.abs(dy);
      shadowRect.bottom -= Math.abs(dy);
    }
    if (dx > 0) {
      shadowRect.left += dx;
      shadowRect.right -= dx;
    } else if (dx < 0) {
      shadowRect.left += Math.abs(dx);
      shadowRect.right -= Math.abs(dx);
    }
    mPaint.setColor(fillColor);
    if (!isInEditMode()) {
      mPaint.setShadowLayer(shadowRadius, dx, dy, shadowColor);
    }
    mPath.addRoundRect(shadowRect, new float[]{
        cornerRadius.topLeft, cornerRadius.topLeft,
        cornerRadius.topRight, cornerRadius.topRight,
        cornerRadius.bottomLeft, cornerRadius.bottomLeft,
        cornerRadius.bottomRight, cornerRadius.bottomRight,
    }, Path.Direction.CW);
    canvas.drawPath(mPath, mPaint);
    return output;
  }
}
