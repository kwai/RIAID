package com.kuaishou.riaid.adbrowser.animator;

import android.animation.TypeEvaluator;
import android.graphics.PointF;
import androidx.annotation.NonNull;

/**
 * 三阶贝塞尔曲线估值器
 */
public class BezierThreePointTypeEvaluator implements TypeEvaluator<PointF> {
  /**
   * 控制点一
   */
  @NonNull
  private final PointF pointF1;
  /**
   * 控制点二
   */
  @NonNull
  private final PointF pointF2;
  /**
   * 估值器结果
   */
  @NonNull
  private final PointF point = new PointF();

  public BezierThreePointTypeEvaluator(@NonNull PointF pointF1, @NonNull PointF pointF2) {
    this.pointF1 = pointF1;
    this.pointF2 = pointF2;
  }

  @Override
  public PointF evaluate(float time, PointF startValue,
      PointF endValue) {

    float timeLeft = 1.0f - time;

    point.x = timeLeft * timeLeft * timeLeft * (startValue.x)
        + 3 * timeLeft * timeLeft * time * (pointF1.x)
        + 3 * timeLeft * time * time * (pointF2.x)
        + time * time * time * (endValue.x);

    point.y = timeLeft * timeLeft * timeLeft * (startValue.y)
        + 3 * timeLeft * timeLeft * time * (pointF1.y)
        + 3 * timeLeft * time * time * (pointF2.y)
        + time * time * time * (endValue.y);
    return point;
  }
}
