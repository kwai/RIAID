package com.kuaishou.riaid.adbrowser.transition;

import java.util.List;
import java.util.Map;

import android.animation.Animator;
import android.animation.AnimatorSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.adbrowser.trigger.ADTrigger;
import com.kuaishou.riaid.render.logger.RiaidLogger;

/**
 * 场景Transition的执行器的抽象，一般在{@link ADTrigger}中创建并执行。
 * 对外暴露执行和释放接口
 */
public abstract class ADBaseTransitionExecutor implements ADTransitionExecutor {
  private static final String TAG = "ADBaseTransition";
  @NonNull
  protected final Map<Integer, ADScene> mADScenes;
  @NonNull
  protected final ADBrowserContext mBrowserContext;

  /**
   * 需要执行的动画的集合
   */
  @Nullable
  protected AnimatorSet mAnimatorSet;

  public ADBaseTransitionExecutor(@NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes) {
    mBrowserContext = context;
    mADScenes = adScenes;
  }

  /**
   * 播放动画集合
   * 每次需要重新创建AnimatorSet
   * 重复播放可能导致：java.lang.IllegalStateException: Circular dependencies cannot exist in AnimatorSet
   *
   * @param animators 新创建动画的集合，需要播放的动画
   */
  protected void playAnimator(List<Animator> animators) {
    // 集合里没有动画就不用播放了
    if (animators.size() <= 0) {
      return;
    }

    // 先把上一次的取消掉
    if (mAnimatorSet != null) {
      mAnimatorSet.cancel();
    }
    // 重新创建AnimatorSet
    mAnimatorSet = new AnimatorSet();
    mAnimatorSet.playTogether(animators);
    try {
      mAnimatorSet.start();
    } catch (IllegalStateException e) {
      RiaidLogger.e(TAG, "playAnimator: ", e);
    }
  }

  /**
   * 取消transition，通常是{@link ADTrigger}在释放资源时调用
   */
  @Override
  public void cancel() {
    if (mAnimatorSet != null) {
      mAnimatorSet.cancel();
    }
  }
}