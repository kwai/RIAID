package com.kuaishou.riaid.adbrowser.animator;

import android.animation.Animator;
import androidx.annotation.CallSuper;

/**
 * 默认的动画监听，仅用于简化实现代码
 */
public class ADAnimatorListener implements Animator.AnimatorListener {

  @Override
  public void onAnimationStart(Animator animation) {

  }

  @Override
  @CallSuper
  public void onAnimationEnd(Animator animation) {
    if (animation != null) {
      animation.removeAllListeners();
    }
  }

  @Override
  public void onAnimationCancel(Animator animation) {

  }

  @Override
  public void onAnimationRepeat(Animator animation) {

  }
}
