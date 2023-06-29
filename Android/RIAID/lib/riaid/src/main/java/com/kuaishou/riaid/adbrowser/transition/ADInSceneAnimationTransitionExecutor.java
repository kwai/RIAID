package com.kuaishou.riaid.adbrowser.transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.util.Pair;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowser;
import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.animator.ADViewPropertyAnimationBuilder;
import com.kuaishou.riaid.adbrowser.helper.ADBrowserKeyHelper;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.ADInSceneAnimationTransitionModel;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.logger.RiaidLogger;

/**
 * 场景内动画的执行器
 */
public class ADInSceneAnimationTransitionExecutor extends ADBaseTransitionExecutor {
  private static final String TAG = "ADInSceneAnimationTrans";
  @Nullable
  private List<ADInSceneAnimationTransitionModel> mAnimationTransitions;

  public ADInSceneAnimationTransitionExecutor(@NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes) {
    super(context, adScenes);
  }

  public void setInSceneAnimationModels(
      @Nullable List<ADInSceneAnimationTransitionModel> sceneAnimationTransitionModels) {
    this.mAnimationTransitions = sceneAnimationTransitionModels;
  }

  @Override
  public void execute() {
    if (mAnimationTransitions == null) {
      return;
    }
    ADBrowserLogger.i("ADInSceneAnimationTransitionExecutor mTransitions" +
        RiaidLogger.objectToString(mAnimationTransitions));
    List<Animator> animators = new ArrayList<>();
    for (ADInSceneAnimationTransitionModel transitionModel : mAnimationTransitions) {
      if (transitionModel == null ||
          transitionModel.animation == null ||
          !ADBrowserKeyHelper.isValidKey(transitionModel.viewKey)) {
        ADBrowserLogger.e("ADInSceneAnimationTransitionExecutor 动画配置不合法");
        continue;
      }
      Pair<ADScene, View> sceneAndViewByKey = ADBrowser
          .findSceneAndViewByKey(mADScenes, transitionModel.viewKey);

      if (sceneAndViewByKey == null || sceneAndViewByKey.first.getRenderCreator() == null ||
          sceneAndViewByKey.first.getRenderCreator().rootRender == null) {
        ADBrowserLogger.e(TAG + " 查找view失败，viewKey: " + transitionModel.viewKey);
        continue;
      }
      View targetView = sceneAndViewByKey.second;
      if (targetView == null) {
        ADRenderLogger
            .e("ADInSceneAnimationTransitionExecutor 没有找到该view :" + transitionModel.viewKey);
        continue;
      }
      AnimatorSet animator =
          ADViewPropertyAnimationBuilder
              .build(transitionModel.viewKey, targetView,
                  sceneAndViewByKey.first.getRenderCreator(),
                  transitionModel.animation);
      if (animator != null) {
        animators.add(animator);
      }
    }
    playAnimator(animators);
  }
}
