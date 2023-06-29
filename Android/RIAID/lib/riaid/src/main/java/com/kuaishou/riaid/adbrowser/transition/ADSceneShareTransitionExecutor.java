package com.kuaishou.riaid.adbrowser.transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.animator.ADAnimatorListener;
import com.kuaishou.riaid.adbrowser.animator.ADShareViewAnimationBuilder;
import com.kuaishou.riaid.adbrowser.helper.ADBrowserKeyHelper;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.ADSceneShareTransitionModel;
import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.render.config.DSLRenderCreator;
import com.kuaishou.riaid.render.interf.IRealViewWrapper;
import com.kuaishou.riaid.render.logger.RiaidLogger;

/**
 * 场景间共享元素的过渡动画，会将两个场景的render都给到Render模块，然后会Render模块会新的render添加到目标场景中，
 * 并返回一个集合，该集合的元素会有view以及该view的起始态和最终态，{@link ADSceneShareTransitionExecutor}会
 * 拿到这些信息去做相应的动画。
 * 动画完成后需要再通知到Render模块，Render模块会更新其相应的属性值。
 * <p>
 * 如果在构建动画时发生了问题，则降级成最简单的渐隐渐现的动画
 */
public class ADSceneShareTransitionExecutor extends ADBaseTransitionExecutor {
  private static final String TAG = "ADSceneShareTransition";
  @Nullable
  private List<ADSceneShareTransitionModel> mSceneShareTransitionModels;

  public ADSceneShareTransitionExecutor(
      @NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes) {
    super(context, adScenes);
  }

  public void setSceneShareTransitionModel(
      @Nullable List<ADSceneShareTransitionModel> sceneShareTransitionModels) {
    mSceneShareTransitionModels = sceneShareTransitionModels;
  }

  @Override
  public void execute() {
    if (mSceneShareTransitionModels == null) {
      return;
    }
    List<Animator> animators = new ArrayList<>();
    ADBrowserLogger.i(TAG + "mSceneShareTransitionModels:" +
        RiaidLogger.objectToString(mSceneShareTransitionModels));

    for (ADSceneShareTransitionModel sceneShareTransitionModel : mSceneShareTransitionModels) {
      if (sceneShareTransitionModel == null) {
        continue;
      }
      // 这是动画执行的时间
      long duration = sceneShareTransitionModel.duration;
      int startSceneKey = sceneShareTransitionModel.startSceneKey;
      int endSceneKey = sceneShareTransitionModel.endSceneKey;

      if (!ADBrowserKeyHelper.isValidKey(startSceneKey) ||
          !ADBrowserKeyHelper.isValidKey(endSceneKey) ||
          !mADScenes.containsKey(startSceneKey) ||
          !mADScenes.containsKey(endSceneKey)) {
        ADBrowserLogger.e(TAG + " sceneKey无效 sceneShareTransitionModel：" +
            RiaidLogger.objectToString(sceneShareTransitionModel));
        continue;
      }

      ADScene adSceneStart = mADScenes.get(startSceneKey);
      ADScene adSceneEnd = mADScenes.get(endSceneKey);
      if (adSceneStart == null || adSceneEnd == null) {
        continue;
      }
      DSLRenderCreator renderCreatorStart = adSceneStart.getRenderCreator();
      Node renderDataEnd = adSceneEnd.getRenderData();
      if (renderCreatorStart == null || renderDataEnd == null) {
        // 动画降级
        ADBrowserLogger.e(TAG + "renderCreatorStart和renderDataEnd有是空的，降级");
        animators.add(demoteAnimation(adSceneStart, adSceneEnd, duration));
        continue;
      }

      // 代理将要转换的场景中的render
      adSceneEnd.replaceRender(renderCreatorStart, adSceneStart.getSceneRenderView());
      // 显示将要转换的场景
      adSceneEnd.setVisibility(View.VISIBLE);
      // 隐藏要被转换的场景
      adSceneStart.setVisibility(View.INVISIBLE);
      // 拿到两个场景的差值
      List<IRealViewWrapper> realViewWrappers =
          renderCreatorStart.diffRender(mBrowserContext.getContext(), renderDataEnd);
      if (realViewWrappers == null || realViewWrappers.size() == 0) {
        // 如果没有需要直接切换场景，不做元素共享动画了
        ADBrowserLogger.e(TAG + "realViewWrappers是空的，降级");
        animators.add(demoteAnimation(adSceneStart, adSceneEnd, duration));
        continue;
      }
      // 开始构建差值的动画，如果构建过程中出现问题，则降级
      for (IRealViewWrapper realViewWrapper : realViewWrappers) {
        if (realViewWrapper == null || realViewWrapper.getRealView() == null) {
          ADBrowserLogger.e(TAG + "realViewWrapper或getRealView是空的，降级");
          animators.add(demoteAnimation(adSceneStart, adSceneEnd, duration));
          // 直接跳出当前循环
          break;
        }
        realViewWrapper.getRealView().clearAnimation();
        // 开始执行动画 Position,Size,Alpha
        Animator sizeAnimator = ADShareViewAnimationBuilder
            .buildSizeAnimator(realViewWrapper, duration);
        Animator positionAnimator =
            ADShareViewAnimationBuilder.buildPositionAnimator(realViewWrapper, duration);
        Animator alphaAnimator =
            ADShareViewAnimationBuilder.buildAlphaAnimator(realViewWrapper, duration);

        if (sizeAnimator == null || positionAnimator == null || alphaAnimator == null) {
          ADBrowserLogger.e(TAG + "sizeAnimator positionAnimator alphaAnimator 其中有空值，" +
              "构建过渡动画失败，降级");
          animators.add(demoteAnimation(adSceneStart, adSceneEnd, duration));
          // 直接跳出当前循环
          break;
        }
        AnimatorSet shareAnimator = new AnimatorSet();
        shareAnimator.playTogether(sizeAnimator, sizeAnimator, positionAnimator, alphaAnimator);
        shareAnimator.addListener(new ADAnimatorListener() {
          @Override
          public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            renderCreatorStart.updateRenderTree();
          }
        });
        animators.add(shareAnimator);
      }
    }
    playAnimator(animators);
  }

  /**
   * 如果再构建过渡动画的时候出现了异常，则降级处理
   *
   * @param hideADScene 要隐藏的场景
   * @param showADScene 要展示的场景
   * @param duration    动画的执行时间
   * @return 不为空
   */
  @NonNull
  private Animator demoteAnimation(@NonNull ADScene hideADScene, @NonNull ADScene showADScene,
      long duration) {
    AnimatorSet animatorSet = new AnimatorSet();
    // 先将render清空，防止之前的操作有状态残留，
    showADScene.removeRender();
    ObjectAnimator hideSceneAnimator =
        ObjectAnimator.ofFloat(hideADScene.getSceneView(), View.ALPHA, 1, 0);
    hideSceneAnimator.addListener(new ADAnimatorListener() {
      @Override
      public void onAnimationEnd(Animator animation) {
        // 结束之后需要隐藏
        super.onAnimationEnd(animation);
        hideADScene.setVisibility(View.INVISIBLE);
      }
    });
    ObjectAnimator showSceneAnimator =
        ObjectAnimator.ofFloat(showADScene.getSceneView(), View.ALPHA, 0, 1);
    showSceneAnimator.addListener(new ADAnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
        // 开始之前需要展示
        showADScene.setVisibility(View.VISIBLE);
      }
    });
    animatorSet.setDuration(duration);
    animatorSet.playTogether(hideSceneAnimator, showSceneAnimator);
    return animatorSet;
  }
}
