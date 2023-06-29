package com.kuaishou.riaid.adbrowser.transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.animator.ADAnimatorListener;
import com.kuaishou.riaid.adbrowser.helper.ADBrowserKeyHelper;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.ADTemplateTransitionModel;
import com.kuaishou.riaid.render.logger.RiaidLogger;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * 固定模板的场景转场行为的执行器
 * 目前支持的模板是左进右出
 */
public class ADTemplateTransitionExecutor extends ADBaseTransitionExecutor {
  @Nullable
  private List<ADTemplateTransitionModel> mADTemplateTransitionModels;

  public ADTemplateTransitionExecutor(@NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes) {
    super(context, adScenes);
  }

  public void setTemplateTransitionModels(
      @Nullable List<ADTemplateTransitionModel> templateTransitionModels) {
    this.mADTemplateTransitionModels = templateTransitionModels;
  }

  @Override
  public void execute() {
    if (mADTemplateTransitionModels == null) {
      return;
    }
    ADBrowserLogger.i("ADTemplateTransitionExecutor mVisibilityTransitions" +
        RiaidLogger.objectToString(mADTemplateTransitionModels));
    List<Animator> animators = new ArrayList<>();

    for (ADTemplateTransitionModel transitionModel : mADTemplateTransitionModels) {
      if (transitionModel == null) {
        ADBrowserLogger.e("ADTemplateTransitionExecutor ADTemplateTransitionModel不合法");
        continue;
      }
      if (!ADBrowserKeyHelper.isValidKey(transitionModel.sceneKey)) {
        continue;
      }
      if (mADScenes.containsKey(transitionModel.sceneKey)) {
        ADScene adScene = mADScenes.get(transitionModel.sceneKey);
        buildAnimator(animators, transitionModel, adScene);
      } else {
        ADBrowserLogger.w("ADTemplateTransitionExecutor 无任何可执行的场景");
      }
    }
    playAnimator(animators);
  }

  private void buildAnimator(@NonNull List<Animator> animators,
      @NonNull ADTemplateTransitionModel transitionModel,
      @NonNull ADScene adScene) {

    if (ADTemplateTransitionModel.ENTER_FROM_START
        == transitionModel.template ||
        ADTemplateTransitionModel.EXIT_FROM_START ==
            transitionModel.template) {
      // 左进右出模板，适配了RTL
      boolean rtl = ToolHelper.isRtlByLocale();
      View sceneView = adScene.getSceneView();
      ViewGroup.LayoutParams layoutParams = sceneView.getLayoutParams();
      // 画布之外的TransitionX，0是指当前view的起始位置
      int outTransitionX = 0;
      if (layoutParams instanceof RelativeLayout.LayoutParams) {
        outTransitionX += ((RelativeLayout.LayoutParams) layoutParams).getMarginStart();
      }
      outTransitionX += adScene.getSceneMeasureWidth();
      float[] values = new float[2];
      if (rtl) {
        // 如果是RTL，需要加上Canvas的宽度
        outTransitionX += mBrowserContext.getADCanvas().getCanvasWidth();
      } else {
        outTransitionX = -outTransitionX;
      }
      if (ADTemplateTransitionModel.ENTER_FROM_START ==
          transitionModel.template) {
        values[0] = outTransitionX;
        values[1] = 0;
      } else {
        values[0] = 0;
        values[1] = outTransitionX;
      }
      ObjectAnimator objectAnimator =
          ObjectAnimator.ofFloat(sceneView, View.TRANSLATION_X, values);
      objectAnimator.setDuration(transitionModel.duration);
      objectAnimator.addListener(new ADAnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
          if (ADTemplateTransitionModel.ENTER_FROM_START ==
              transitionModel.template) {
            adScene.setVisibility(View.VISIBLE);
          }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
          super.onAnimationEnd(animation);
          if (ADTemplateTransitionModel.EXIT_FROM_START ==
              transitionModel.template) {
            adScene.setVisibility(View.INVISIBLE);
            // 动画执行完成后需要恢复其初始状态
            sceneView.setTranslationX(0);
          }
        }
      });
      animators.add(objectAnimator);
    } else {
      ADBrowserLogger.e("ADTemplateTransitionExecutor 暂不支持的模板类型 transitionModel.template：" +
          transitionModel.template);
    }
  }
}
