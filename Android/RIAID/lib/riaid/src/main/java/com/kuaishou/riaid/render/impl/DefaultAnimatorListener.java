package com.kuaishou.riaid.render.impl;

import android.animation.Animator;

/**
 * 默认实现的动画监听，避免空实现
 */
public interface DefaultAnimatorListener extends Animator.AnimatorListener {

  default void onAnimationStart(Animator animation) {
  }

  default void onAnimationEnd(Animator animation) {
  }

  default void onAnimationCancel(Animator animation) {
  }

  default void onAnimationRepeat(Animator animation) {
  }
}
