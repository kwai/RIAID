package com.kuaishou.riaid.adbrowser.animator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.render.interf.IRealViewWrapper;
import com.kuaishou.riaid.render.model.UIModel;

/**
 * 共享动画的{@link Animator}构建，如果在构建过程中有问题，则返回空。
 * <p>
 * 考虑：如果起始宽度且高度是零，是不是只需要将宽度且高度改为最终值，仅仅执行alpha的动画
 */
public class ADShareViewAnimationBuilder {

  private static final String ERROR_MSG_EMPTY =
      "ADShareViewAnimationBuilder view为空或showingViewInfo为空";

  /**
   * 负责宽高的动画
   *
   * @param viewWrapper 持有需要执行动画的数据
   * @param duration    动画执行时长
   * @return 可能为空
   */
  @Nullable
  public static Animator buildSizeAnimator(@NonNull IRealViewWrapper viewWrapper,
      long duration) {
    AnimatorSet animatorSet = new AnimatorSet();
    View view = viewWrapper.getRealView();
    IRealViewWrapper.IViewInfo currentViewInfo = viewWrapper.getCurrentViewInfo();
    IRealViewWrapper.IViewInfo showingViewInfo = viewWrapper.getShowingViewInfo();
    if (view == null || showingViewInfo == null) {
      ADBrowserLogger.e(ERROR_MSG_EMPTY);
      return null;
    }
    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

    UIModel.Size realViewSize = currentViewInfo.getRealViewSize();
    UIModel.Size showViewSize = showingViewInfo.getRealViewSize();
    float[] floatsWidth = {realViewSize.width, showViewSize.width};
    ValueAnimator animatorWidth = ValueAnimator.ofFloat(floatsWidth);
    // 添加更新的监听，更改其值
    animatorWidth.addUpdateListener(animation -> {
      layoutParams.width = (int) (float) animation.getAnimatedValue();
      view.setLayoutParams(layoutParams);
    });
    float[] floatsHeight = {realViewSize.height, showViewSize.height};
    ValueAnimator animatorHeight = ValueAnimator.ofFloat(floatsHeight);
    // 添加更新的监听，更改其值
    animatorHeight.addUpdateListener(animation -> {
      layoutParams.height = (int) (float) animation.getAnimatedValue();
      view.setLayoutParams(layoutParams);
    });
    animatorSet.playTogether(animatorWidth, animatorHeight);
    animatorSet.setDuration(duration);
    return animatorSet;
  }

  /**
   * 负责位置的动画
   *
   * @param viewWrapper 持有需要执行动画的数据
   * @param duration    动画执行时长
   * @return 可能为空
   */
  @Nullable
  public static Animator buildPositionAnimator(@NonNull IRealViewWrapper viewWrapper,
      long duration) {
    AnimatorSet animatorSet = new AnimatorSet();
    View view = viewWrapper.getRealView();
    IRealViewWrapper.IViewInfo currentViewInfo = viewWrapper.getCurrentViewInfo();
    IRealViewWrapper.IViewInfo showingViewInfo = viewWrapper.getShowingViewInfo();
    if (view == null || showingViewInfo == null) {
      ADBrowserLogger.e(ERROR_MSG_EMPTY);
      return null;
    }

    if (!(view.getLayoutParams() instanceof FrameLayout.LayoutParams)) {
      ADBrowserLogger.e("ADShareViewAnimationBuilder view的LayoutParams不是FrameLayout.LayoutParams");
      return null;
    }
    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();

    Rect realViewPosition = currentViewInfo.getRealViewPosition();
    Rect showViewPosition = showingViewInfo.getRealViewPosition();
    float[] floatsLeft = {realViewPosition.left, showViewPosition.left};
    ValueAnimator animatorStartPosition = ValueAnimator.ofFloat(floatsLeft);
    // 添加更新的监听，更改其值
    animatorStartPosition.addUpdateListener(
        animation -> {
          int animatedValue = (int) (float) animation.getAnimatedValue();
          layoutParams.setMarginStart(animatedValue);
          view.setLayoutParams(layoutParams);
        });
    float[] floatsHeight = {realViewPosition.top, showViewPosition.top};
    ValueAnimator animatorTopPosition = ValueAnimator.ofFloat(floatsHeight);
    // 添加更新的监听，更改其值
    animatorTopPosition.addUpdateListener(animation -> {
      layoutParams.topMargin = (int) (float) animation.getAnimatedValue();
      view.setLayoutParams(layoutParams);
    });
    animatorSet.playTogether(animatorStartPosition, animatorTopPosition);
    animatorSet.setDuration(duration);
    return animatorSet;
  }


  /**
   * 负责透明度的动画
   *
   * @param viewWrapper 持有需要执行动画的数据
   * @param duration    动画执行时长
   * @return 可能为空
   */
  @Nullable
  public static Animator buildAlphaAnimator(@NonNull IRealViewWrapper viewWrapper,
      long duration) {
    View view = viewWrapper.getRealView();
    IRealViewWrapper.IViewInfo currentViewInfo = viewWrapper.getCurrentViewInfo();
    IRealViewWrapper.IViewInfo showingViewInfo = viewWrapper.getShowingViewInfo();
    if (view == null || showingViewInfo == null) {
      ADBrowserLogger.e(ERROR_MSG_EMPTY);
      return null;
    }
    float realViewAlpha = currentViewInfo.getRealViewAlpha();
    float showViewAlpha = showingViewInfo.getRealViewAlpha();
    float[] floatsAlpha = {realViewAlpha, showViewAlpha};

    ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, floatsAlpha);
    animator.setDuration(duration);
    return animator;
  }
}
