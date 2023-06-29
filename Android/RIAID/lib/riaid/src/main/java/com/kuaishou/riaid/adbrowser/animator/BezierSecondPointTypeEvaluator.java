package com.kuaishou.riaid.adbrowser.animator;

import android.animation.TypeEvaluator;
import android.graphics.PointF;
import androidx.annotation.NonNull;

/**
 * 二阶贝塞尔曲线估值器
 */
public class BezierSecondPointTypeEvaluator implements TypeEvaluator<PointF> {
  /**
   * 控制点
   */
  @NonNull
  private final PointF mControl;
  /**
   * 估值器返回值
   */
  @NonNull
  private final PointF mPointF = new PointF();

  public BezierSecondPointTypeEvaluator(@NonNull PointF pointF) {
    this.mControl = pointF;
  }

  @Override
  public PointF evaluate(float time, PointF startValue,
      PointF endValue) {
    mPointF.x = (1 - time) * (1 - time) * startValue.x + 2 * time * (1 - time) * mControl.x +
        time * time * endValue.x;
    mPointF.y = (1 - time) * (1 - time) * startValue.y + 2 * time * (1 - time) * mControl.y +
        time * time * endValue.y;
    return mPointF;
  }
}