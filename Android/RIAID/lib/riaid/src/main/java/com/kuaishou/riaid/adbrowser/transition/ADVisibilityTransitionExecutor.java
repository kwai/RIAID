package com.kuaishou.riaid.adbrowser.transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.animator.ADAnimatorListener;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.ADVisibilityTransitionModel;
import com.kuaishou.riaid.render.logger.RiaidLogger;

/**
 * 展示和隐藏的场景转场行为的执行器
 */
public class ADVisibilityTransitionExecutor extends ADBaseTransitionExecutor {
  private List<ADVisibilityTransitionModel> mVisibilityTransitions;

  public ADVisibilityTransitionExecutor(@NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes) {
    super(context, adScenes);
  }

  public void setVisibilityTransitions(List<ADVisibilityTransitionModel> visibilityTransitions) {
    this.mVisibilityTransitions = visibilityTransitions;
  }

  @Override
  public void execute() {
    if (mVisibilityTransitions == null) {
      return;
    }
    ADBrowserLogger.i("ADVisibilityTransitionExecutor mVisibilityTransitions" +
        RiaidLogger.objectToString(mVisibilityTransitions));
    List<Animator> animators = new ArrayList<>();

    for (ADVisibilityTransitionModel transitionModel : mVisibilityTransitions) {
      if (transitionModel == null) {
        continue;
      }
      if (mADScenes.containsKey(transitionModel.sceneKey)) {
        ADScene adScene = mADScenes.get(transitionModel.sceneKey);
        buildAnimator(animators, transitionModel, adScene);
      } else {
        ADBrowserLogger.w("ADVisibilityTransitionExecutor 无任何可执行的场景");
      }
    }
    playAnimator(animators);
  }

  private void buildAnimator(List<Animator> animators, ADVisibilityTransitionModel transitionModel,
      ADScene adScene) {
    View sceneView = adScene.getSceneView();
    if (transitionModel.duration <= 0) {
      // 如果动画时长 <=0 则直接设置结果值，不需要构建Animator
      if (!transitionModel.hidden) {
        ADBrowserLogger.i("ADVisibilityTransitionExecutor 直接展示场景" + adScene.getSceneKey());
        adScene.setVisibility(View.VISIBLE);
      } else {
        ADBrowserLogger.i("ADVisibilityTransitionExecutor 直接隐藏场景" + adScene.getSceneKey());
        adScene.setVisibility(View.INVISIBLE);
      }
      return;
    }
    ObjectAnimator objectAnimator = ObjectAnimator
        .ofFloat(sceneView, View.ALPHA, transitionModel.startAlpha, transitionModel.endAlpha);
    objectAnimator.setDuration(transitionModel.duration);
    objectAnimator.addListener(new ADAnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
        if (!transitionModel.hidden) {
          adScene.setVisibility(View.VISIBLE);
        }
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        if (transitionModel.hidden) {
          adScene.setVisibility(View.INVISIBLE);
          // 动画执行完成后需要恢复其默认状态
          sceneView.setAlpha(1);
        }
      }
    });
    animators.add(objectAnimator);
  }
}
