package com.kuaishou.riaid.adbrowser.animator;

import android.animation.ValueAnimator;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.proto.nano.ADSceneRelationModel;

/**
 * 场景的间距值变化的动画构建器，用做场景的位移动画。
 */
public class ADMarginValueAnimationBuilder {
  private ADMarginValueAnimationBuilder() {}

  /**
   * 构建{@link ValueAnimator}，用来声明场景的位移动画
   *
   * @param view         要执行动画的视图
   * @param layoutParams 视图的配置参数
   * @param marginType   配置的margin
   *                     类型，例如{@link ADSceneRelationModel#TOP}
   * @param margin       单位px，目标的margin值
   * @param duration     动画执行的时间
   * @return {@link ValueAnimator}
   */
  @Nullable
  public static ValueAnimator build(
      @NonNull View view,
      @NonNull RelativeLayout.LayoutParams layoutParams,
      int marginType, int margin,
      long duration) {
    int marginSource;
    switch (marginType) {
      case ADSceneRelationModel.START:
        marginSource = layoutParams.getMarginStart();
        break;
      case ADSceneRelationModel.TOP:
        marginSource = layoutParams.topMargin;
        break;
      case ADSceneRelationModel.END:
        marginSource = layoutParams.getMarginEnd();
        break;
      case ADSceneRelationModel.BOTTOM:
        marginSource = layoutParams.bottomMargin;
        break;
      default:
        ADBrowserLogger.e("ADMarginValueAnimationBuilder 不支持 marginType: " + marginType);
        return null;
    }
    ValueAnimator ofInt = ValueAnimator.ofInt(marginSource, margin);
    ofInt.setDuration(duration);
    ofInt.addUpdateListener(animation -> {
      Object animatedValue = animation.getAnimatedValue();
      int marginValue = (int) animatedValue;
      switch (marginType) {
        case ADSceneRelationModel.START:
          layoutParams.setMarginStart(marginValue);
          break;
        case ADSceneRelationModel.TOP:
          layoutParams.topMargin = marginValue;
          break;
        case ADSceneRelationModel.END:
          layoutParams.setMarginEnd(marginValue);
          break;
        case ADSceneRelationModel.BOTTOM:
          layoutParams.bottomMargin = marginValue;
          break;
      }
      view.setLayoutParams(layoutParams);
    });
    return ofInt;
  }
}
