package com.kuaishou.riaid.adbrowser.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.adbrowser.transition.ADBezierTransitionExecutor;
import com.kuaishou.riaid.adbrowser.transition.ADInSceneAnimationTransitionExecutor;
import com.kuaishou.riaid.adbrowser.transition.ADLottieTransitionExecutor;
import com.kuaishou.riaid.adbrowser.transition.ADRenderContentTransitionExecutor;
import com.kuaishou.riaid.adbrowser.transition.ADSceneShareTransitionExecutor;
import com.kuaishou.riaid.adbrowser.transition.ADTemplateTransitionExecutor;
import com.kuaishou.riaid.adbrowser.transition.ADTransitionExecutor;
import com.kuaishou.riaid.adbrowser.transition.ADTranslationTransitionExecutor;
import com.kuaishou.riaid.adbrowser.transition.ADVisibilityTransitionExecutor;
import com.kuaishou.riaid.proto.nano.ADBezierTransitionModel;
import com.kuaishou.riaid.proto.nano.ADInSceneAnimationTransitionModel;
import com.kuaishou.riaid.proto.nano.ADLottieTransitionModel;
import com.kuaishou.riaid.proto.nano.ADRenderContentTransitionModel;
import com.kuaishou.riaid.proto.nano.ADSceneShareTransitionModel;
import com.kuaishou.riaid.proto.nano.ADTemplateTransitionModel;
import com.kuaishou.riaid.proto.nano.ADTransitionActionModel;
import com.kuaishou.riaid.proto.nano.ADTransitionModel;
import com.kuaishou.riaid.proto.nano.ADTranslationTransitionModel;
import com.kuaishou.riaid.proto.nano.ADVisibilityTransitionModel;

/**
 * 触发器触发的Transition行为，去执行相应行为，会在初始化时创建好多种类型的, {@link ADTransitionExecutor}
 * 例如：{@link ADVisibilityTransitionExecutor}、{@link ADTemplateTransitionExecutor}
 * 和{@link ADTranslationTransitionExecutor}。
 */
public class ADTransitionAction extends ADBaseAction<ADTransitionActionModel> {

  @NonNull
  private final Map<Integer, ADScene> mADScenes;
  @NonNull
  private final List<ADTransitionExecutor> mTransitionExecutors = new ArrayList<>();

  public ADTransitionAction(
      @NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes,
      @NonNull ADTransitionActionModel adActionModel) {
    super(context, adActionModel);
    mADScenes = adScenes;
    init(context, adScenes);
  }

  private void init(@NonNull ADBrowserContext context, @NonNull Map<Integer, ADScene> adScenes) {
    if (mADActionModel.transitions == null ||
        mADActionModel.transitions.length <= 0) {
      return;
    }

    List<ADVisibilityTransitionModel> adVisibilityTransitionModels = new ArrayList<>();
    List<ADTranslationTransitionModel> adTranslationTransitionModels = new ArrayList<>();
    List<ADTemplateTransitionModel> adTemplateTransitionModels = new ArrayList<>();
    List<ADInSceneAnimationTransitionModel> adInSceneAnimationTransitionModels = new ArrayList<>();
    List<ADLottieTransitionModel> adLottieTransitionModels = new ArrayList<>();
    List<ADSceneShareTransitionModel> adSceneShareTransitionModels = new ArrayList<>();
    List<ADRenderContentTransitionModel> adRenderContentTransitionModels = new ArrayList<>();
    List<ADBezierTransitionModel> adBezierTransitionModels = new ArrayList<>();

    // 先把所有的Transition取出来，添加到对应的类集合中
    for (ADTransitionModel transition : mADActionModel.transitions) {
      if (transition == null) {
        continue;
      }
      if (transition.visibility != null) {
        adVisibilityTransitionModels.add(transition.visibility);
      } else if (transition.translation != null) {
        adTranslationTransitionModels.add(transition.translation);
      } else if (transition.template != null) {
        adTemplateTransitionModels.add(transition.template);
      } else if (transition.inSceneAnimation != null) {
        adInSceneAnimationTransitionModels.add(transition.inSceneAnimation);
      } else if (transition.lottie != null) {
        adLottieTransitionModels.add(transition.lottie);
      } else if (transition.sceneShare != null) {
        adSceneShareTransitionModels.add(transition.sceneShare);
      } else if (transition.renderContent != null) {
        adRenderContentTransitionModels.add(transition.renderContent);
      } else if (transition.bezier != null) {
        adBezierTransitionModels.add(transition.bezier);
      }
    }
    // 顺序构建这些行为
    buildVisibilityTransitions(adScenes, adVisibilityTransitionModels);
    buildTranslationTransitions(adTranslationTransitionModels);
    buildTemplateTransitions(adTemplateTransitionModels);
    buildInSceneAnimationTransitions(context, adScenes, adInSceneAnimationTransitionModels);
    buildLottieTransitions(context, adScenes, adLottieTransitionModels);
    buildSceneShareTransitions(context, adScenes, adSceneShareTransitionModels);
    buildRenderContentTransitions(context, adScenes, adRenderContentTransitionModels);
    buildBezierTransitions(adScenes, adBezierTransitionModels);
  }

  private void buildLottieTransitions(@NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes,
      @NonNull List<ADLottieTransitionModel> transitionModels) {
    if (transitionModels.size() > 0) {
      ADLottieTransitionExecutor lottieTransitionExecutor =
          new ADLottieTransitionExecutor(context, adScenes);
      lottieTransitionExecutor
          .setADLottieTransitionModels(transitionModels);
      mTransitionExecutors.add(lottieTransitionExecutor);
    }
  }

  private void buildRenderContentTransitions(@NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes,
      @NonNull List<ADRenderContentTransitionModel> transitionModels) {
    if (transitionModels.size() > 0) {
      ADRenderContentTransitionExecutor renderContentTransitionExecutor =
          new ADRenderContentTransitionExecutor(context, adScenes);
      renderContentTransitionExecutor
          .setADRenderContentTransitionModels(
              transitionModels);
      mTransitionExecutors.add(renderContentTransitionExecutor);
    }
  }

  private void buildSceneShareTransitions(@NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes,
      @NonNull List<ADSceneShareTransitionModel> transitionModels) {
    if (transitionModels.size() > 0) {
      ADSceneShareTransitionExecutor sceneShareTransitionExecutor =
          new ADSceneShareTransitionExecutor(context, adScenes);
      sceneShareTransitionExecutor
          .setSceneShareTransitionModel(transitionModels);
      mTransitionExecutors.add(sceneShareTransitionExecutor);
    }
  }

  private void buildInSceneAnimationTransitions(@NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes,
      @NonNull List<ADInSceneAnimationTransitionModel> transitionModels) {
    if (transitionModels.size() > 0) {
      ADInSceneAnimationTransitionExecutor animationTransitionExecutor =
          new ADInSceneAnimationTransitionExecutor(context, adScenes);
      animationTransitionExecutor
          .setInSceneAnimationModels(transitionModels);
      mTransitionExecutors.add(animationTransitionExecutor);
    }
  }

  private void buildTemplateTransitions(
      @NonNull List<ADTemplateTransitionModel> transitionModels) {
    if (transitionModels.size() > 0) {
      // 创建模板转场执行器
      ADTemplateTransitionExecutor adTemplateTransitionExecutor =
          new ADTemplateTransitionExecutor(mBrowserContext, mADScenes);
      adTemplateTransitionExecutor
          .setTemplateTransitionModels(transitionModels);
      mTransitionExecutors.add(adTemplateTransitionExecutor);
    }
  }

  private void buildTranslationTransitions(
      @NonNull List<ADTranslationTransitionModel> transitionModels) {
    if (transitionModels.size() > 0) {
      // 创建位移转场执行器
      ADTranslationTransitionExecutor adTranslationTransitionExecutor =
          new ADTranslationTransitionExecutor(mBrowserContext, mADScenes);
      adTranslationTransitionExecutor
          .setTranslations(transitionModels);
      mTransitionExecutors.add(adTranslationTransitionExecutor);
    }
  }

  private void buildVisibilityTransitions(@NonNull Map<Integer, ADScene> adScenes,
      @NonNull List<ADVisibilityTransitionModel> transitionModels) {
    if (transitionModels.size() > 0) {
      // 创建可见转场执行器
      ADVisibilityTransitionExecutor adVisibilityTransitionExecutor =
          new ADVisibilityTransitionExecutor(mBrowserContext, adScenes);
      adVisibilityTransitionExecutor
          .setVisibilityTransitions(transitionModels);
      mTransitionExecutors.add(adVisibilityTransitionExecutor);
    }
  }

  private void buildBezierTransitions(@NonNull Map<Integer, ADScene> adScenes,
      @NonNull List<ADBezierTransitionModel> transitionModels) {
    if (transitionModels.size() > 0) {
      // 创建可见转场执行器
      ADBezierTransitionExecutor bezierTransition =
          new ADBezierTransitionExecutor(mBrowserContext, adScenes);
      bezierTransition
          .setSceneShareTransitionModel(transitionModels);
      mTransitionExecutors.add(bezierTransition);
    }
  }

  @Override
  public boolean execute() {
    for (int i = 0; i < mTransitionExecutors.size(); i++) {
      mTransitionExecutors.get(i).execute();
    }
    return true;
  }

  @Override
  public void cancel() {
    for (int i = 0; i < mTransitionExecutors.size(); i++) {
      mTransitionExecutors.get(i).cancel();
    }
  }
}
