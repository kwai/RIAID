package com.kuaishou.riaid.adbrowser.transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.animator.ADMarginValueAnimationBuilder;
import com.kuaishou.riaid.adbrowser.helper.ADBrowserKeyHelper;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.ADSceneRelationModel;
import com.kuaishou.riaid.proto.nano.ADTranslationTransitionModel;
import com.kuaishou.riaid.proto.nano.SystemKeyEnum;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.logger.RiaidLogger;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * 场景位移动画的执行器
 */
public class ADTranslationTransitionExecutor extends ADBaseTransitionExecutor {
  @Nullable
  private List<ADTranslationTransitionModel> mTransitionModels;

  public ADTranslationTransitionExecutor(@NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes) {
    super(context, adScenes);
  }

  public void setTranslations(@Nullable List<ADTranslationTransitionModel> transitionModels) {
    this.mTransitionModels = transitionModels;
  }

  @Override
  public void execute() {
    if (mTransitionModels == null) {
      return;
    }
    List<Animator> animators = new ArrayList<>();

    for (ADTranslationTransitionModel transitionModel : mTransitionModels) {
      if (transitionModel == null) {
        continue;
      }
      ADRenderLogger
          .i("ADTranslationTransitionExecutor transitionModel:" +
              RiaidLogger.objectToString(transitionModel));
      if (!mADScenes.containsKey(transitionModel.sceneKey)) {
        continue;
      }
      ADSceneRelationModel[] sceneRelations = transitionModel.sceneRelations;
      if (sceneRelations == null) {
        continue;
      }
      for (ADSceneRelationModel sceneRelationModel : sceneRelations) {
        if (sceneRelationModel == null) {
          continue;
        }
        // #1 不包含source场景
        // #2 不包含target场景且target不等于Canvas
        if (!mADScenes.containsKey(sceneRelationModel.sourceKey) ||
            (!mADScenes.containsKey(sceneRelationModel.targetKey) &&
                !(ADBrowserKeyHelper.isCanvas(sceneRelationModel.targetKey)))) {
          ADRenderLogger
              .e("ADTranslationTransitionExecutor sceneRelationModel不合法 sceneRelationModel：" +
                  RiaidLogger.objectToString(sceneRelationModel));
          continue;
        }
        ADScene adSourceScene = mADScenes.get(sceneRelationModel.sourceKey);
        buildAnimator(animators, transitionModel, sceneRelationModel, adSourceScene);
      }
    }
    playAnimator(animators);
  }

  private void buildAnimator(List<Animator> animators,
      ADTranslationTransitionModel transitionModel,
      ADSceneRelationModel sceneRelationModel, ADScene adSourceScene) {
    View sourceSceneView = adSourceScene.getSceneView();

    // 获取targetView的id
    int targetViewId;
    if (ADBrowserKeyHelper.isCanvas(sceneRelationModel.targetKey)) {
      // 说明是target是画布
      targetViewId = SystemKeyEnum.SCENE_KEY_CANVAS;
    } else {
      ADScene adTargetScene = mADScenes.get(sceneRelationModel.targetKey);
      targetViewId = adTargetScene.getViewId();
    }
    if (!(sourceSceneView.getLayoutParams() instanceof RelativeLayout.LayoutParams)) {
      ADRenderLogger
          .e("ADTranslationTransitionExecutor sourceSceneView.getLayoutParams()不合法 " +
              "sceneKey:" +
              adSourceScene.getSceneKey());
      return;
    }
    RelativeLayout.LayoutParams layoutParams =
        (RelativeLayout.LayoutParams) sourceSceneView.getLayoutParams();
    RelativeLayoutParamBuilder
        .buildLayoutParam(layoutParams, sceneRelationModel, targetViewId);
    // 这里需要将位移量置位空，因为像这种直接更改LayoutParam的位移动画，需要无视原来的其他属性进行的动画。
    sourceSceneView.setTranslationX(0);
    sourceSceneView.setTranslationY(0);
    if (transitionModel.duration > 0) {
      ADBrowserLogger.i("ADTranslationTransitionExecutor 开始构建位移动画 sceneKey:" +
          adSourceScene.getSceneKey());
      ValueAnimator animator = ADMarginValueAnimationBuilder
          .build(sourceSceneView, layoutParams, sceneRelationModel.sourceEdge,
              ToolHelper.dip2px(mBrowserContext.getContext(), sceneRelationModel.distance),
              transitionModel.duration);
      if (animator != null) {
        animators.add(animator);
      }
    } else {
      ADRenderLogger
          .i("ADTranslationTransitionExecutor 不需要动画 直接改变位置关系，sceneKey:"
              + adSourceScene.getSceneKey());
      RelativeLayoutParamBuilder
          .buildLayoutMargin(mBrowserContext.getContext(), layoutParams, sceneRelationModel);
      sourceSceneView.setLayoutParams(layoutParams);
    }
  }
}
