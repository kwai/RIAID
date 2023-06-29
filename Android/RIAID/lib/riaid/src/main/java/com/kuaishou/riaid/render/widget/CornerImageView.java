package com.kuaishou.riaid.render.widget;

import java.util.Arrays;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.kuaishou.riaid.render.model.UIModel;

/**
 * 带圆角的ImageView
 */
public class CornerImageView extends AppCompatImageView {

  public CornerImageView(Context context) {
    super(context);
    init();
  }

  public CornerImageView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public CornerImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private final RectF roundRect = new RectF();
  private final Paint maskPaint = new Paint();
  private final Paint zonePaint = new Paint();

  private final Path path = new Path();
  private final float[] radiusArray = new float[]{0, 0, 0, 0, 0, 0, 0, 0};

  /**
   * 初始化一些属性
   */
  private void init() {
    zonePaint.setAntiAlias(true);
    maskPaint.setAntiAlias(true);
    maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    roundRect.set(0, 0, getWidth(), getHeight());
  }

  @Override
  public void draw(Canvas canvas) {
    path.reset();
    path.addRoundRect(roundRect, radiusArray, Path.Direction.CW);
    canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
    canvas.drawPath(path, zonePaint);
    canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
    super.draw(canvas);
    canvas.restore();
  }

  public void setRoundRadius(int roundRadius) {
    Arrays.fill(radiusArray, roundRadius);
    invalidate();
  }

  public void setRoundRadius(@NonNull UIModel.CornerRadius radius) {
    radiusArray[0] = radius.topLeft;
    radiusArray[1] = radius.topLeft;
    radiusArray[2] = radius.topRight;
    radiusArray[3] = radius.topRight;
    radiusArray[4] = radius.bottomLeft;
    radiusArray[5] = radius.bottomLeft;
    radiusArray[6] = radius.bottomRight;
    radiusArray[7] = radius.bottomRight;
    invalidate();
  }

}
