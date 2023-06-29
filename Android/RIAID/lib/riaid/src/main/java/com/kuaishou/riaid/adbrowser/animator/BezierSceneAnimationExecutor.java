package com.kuaishou.riaid.adbrowser.animator;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.ADBezierTransitionModel;
import com.kuaishou.riaid.proto.nano.Point;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * 场景贝塞尔曲线动画的执行管理
 */
public class BezierSceneAnimationExecutor {
  private static final String TAG = "BezierSceneAnimationExe";
  @NonNull
  private final ADBezierTransitionModel adBezierTransitionModel;
  /**
   * 开始视图，也就是要贝塞尔曲线要执行的视图
   */
  @NonNull
  private final View view;
  /**
   * 动画执行时间
   */
  private final long duration;
  @NonNull
  private final Context context;

  /**
   * 开始视图的窗口位置信息是否获取完成
   */
  private boolean startWindowsReady = false;
  /**
   * 结束视图的窗口位置信息是否获取完成
   */
  private boolean endWindowsReady = false;

  /**
   * 原始开始视图的x坐标
   */
  private float originStartX = -1;
  /**
   * 原始开始视图的y坐标
   */
  private float originStartY = -1;
  /**
   * 原始结束视图的x坐标
   */
  private float originEndX = -1;
  /**
   * 原始结束视图的y坐标
   */
  private float originEndY = -1;
  /**
   * 原始开始视图宽度
   */
  private float originStartWidth = -1;
  /**
   * 原始开始视图高度
   */
  private float originStartHeight = -1;


  /**
   * 原始结束视图宽度
   */
  private float originEndWidth = -1;
  /**
   * 原始结束视图高度
   */
  private float originEndHeight = -1;

  @Nullable
  private ValueAnimator mAnimator = null;

  public BezierSceneAnimationExecutor(@NonNull Context context,
      @NonNull ADBezierTransitionModel adBezierTransitionModel,
      @NonNull View view, long duration) {
    this.context = context;
    this.adBezierTransitionModel = adBezierTransitionModel;
    this.view = view;
    this.duration = duration;
  }

  /**
   * 获取到了开始视图的坐标
   */
  public void startSceneWindowsGet(float x, float y, float width, float height) {
    startWindowsReady = true;
    originStartX = x;
    originStartY = y;
    originStartWidth = width;
    originStartHeight = height;
    tryStartBezier();
  }

  /**
   * 获取到了结束视图的坐标
   */
  public void endSceneWindowsGet(float x, float y, float width, float height) {
    endWindowsReady = true;
    originEndX = x;
    originEndY = y;
    originEndWidth = width;
    originEndHeight = height;
    tryStartBezier();
  }

  /**
   * start和end的窗口位置信息都获取到了，才能开始动画
   */
  private void tryStartBezier() {
    if (mAnimator != null) {
      mAnimator.cancel();
    }
    if (!startWindowsReady || !endWindowsReady) {
      return;
    }
    Point endPoint = adBezierTransitionModel.endPointOffsetTargetScene;
    if (endPoint == null) {
      return;
    }
    TypeEvaluator<PointF> evaluator = null;
    if (adBezierTransitionModel.controlFirstPointOffsetStartScene != null &&
        adBezierTransitionModel.controlSecondPointOffsetTargetScene != null) {
      // 这是构建三阶的贝塞尔曲线
      PointF first = new PointF();
      first.set(originStartX + adBezierTransitionModel.controlFirstPointOffsetStartScene.x,
          originStartY + adBezierTransitionModel.controlFirstPointOffsetStartScene.y);
      // second相对于end，要把endPoint偏移量也算进去
      PointF second =
          new PointF(originEndX + adBezierTransitionModel.controlSecondPointOffsetTargetScene.x +
              adBezierTransitionModel.endPointOffsetTargetScene.x,
              originEndY + adBezierTransitionModel.controlSecondPointOffsetTargetScene.y +
                  adBezierTransitionModel.endPointOffsetTargetScene.y);
      evaluator =
          new BezierThreePointTypeEvaluator(first, second);
    } else if (adBezierTransitionModel.controlFirstPointOffsetStartScene != null) {
      // 只有一个相对于start的控制点，构建二阶
      PointF pointF = new PointF();
      pointF.set(originStartX + adBezierTransitionModel.controlFirstPointOffsetStartScene.x,
          originStartY + adBezierTransitionModel.controlFirstPointOffsetStartScene.y);
      evaluator =
          new BezierSecondPointTypeEvaluator(pointF);
    } else if (adBezierTransitionModel.controlSecondPointOffsetTargetScene != null) {
      // 只有一个相对于end的控制点，构建二阶，second相对于end，要把endPoint偏移量也算进去
      PointF pointF = new PointF();
      pointF.set(originEndX + adBezierTransitionModel.controlSecondPointOffsetTargetScene.x +
              adBezierTransitionModel.endPointOffsetTargetScene.x,
          originEndY + adBezierTransitionModel.controlSecondPointOffsetTargetScene.y +
              adBezierTransitionModel.endPointOffsetTargetScene.y);
      evaluator =
          new BezierSecondPointTypeEvaluator(pointF);
    }

    if (evaluator == null) {
      return;
    }

    // 曲线路径的起始点赋值
    PointF startP = new PointF();
    startP.set(originStartX, originStartY);

    // 曲线路径的结束点赋值
    PointF endP = new PointF();
    // 将偏移量计算进去
    endP.set(originEndX + ToolHelper.dip2px(context, endPoint.x),
        originEndY + ToolHelper.dip2px(context, endPoint.y));
    //启动属性动画
    mAnimator =
        ValueAnimator.ofObject(evaluator, startP, endP);
    mAnimator.setDuration(duration);
    mAnimator.addUpdateListener(animation -> {
      //直接刷新view的位置，这里其实更改的是其Translation
      PointF pointF = (PointF) animation.getAnimatedValue();
      view.setX((int) pointF.x);
      view.setY((int) pointF.y);
    });
    mAnimator.start();
  }

  // 取消动画
  public void cancel() {
    if (mAnimator != null) {
      mAnimator.cancel();
    }
  }
}
