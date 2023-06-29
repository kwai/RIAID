package com.kuaishou.riaid.adbrowser.transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.animator.BezierSceneAnimationExecutor;
import com.kuaishou.riaid.adbrowser.helper.ADBrowserKeyHelper;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.ADBezierTransitionModel;
import com.kuaishou.riaid.render.logger.RiaidLogger;

/**
 * 贝塞尔曲线的场景位移动画，需要定义要执行的场景和曲线终点的场景，待场景的位置获取到后再构建动画。
 * 因为动画回调后创建的，所以动画的开始和取消需要单独的{@link #mBezierAnimationBuilders}。
 */
public class ADBezierTransitionExecutor extends ADBaseTransitionExecutor {
  private static final String TAG = "ADBezierTransitionModel";
  @Nullable
  private List<ADBezierTransitionModel> mADBezierTransitionModels;

  /**
   * 所有贝塞尔曲线执行器集合，需要在释放资源的时候遍历去取消动画
   */
  private final List<BezierSceneAnimationExecutor> mBezierAnimationBuilders = new ArrayList<>();

  public ADBezierTransitionExecutor(
      @NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes) {
    super(context, adScenes);
  }

  public void setSceneShareTransitionModel(
      @Nullable List<ADBezierTransitionModel> sceneShareTransitionModels) {
    mADBezierTransitionModels = sceneShareTransitionModels;
  }

  @Override
  public void execute() {
    if (mADBezierTransitionModels == null) {
      return;
    }
    for (ADBezierTransitionModel adBezierTransitionModel : mADBezierTransitionModels) {
      if (adBezierTransitionModel == null) {
        continue;
      }
      long duration = adBezierTransitionModel.duration;
      int startSceneKey = adBezierTransitionModel.startSceneKey;
      int endSceneKey = adBezierTransitionModel.targetSceneKey;
      if (!ADBrowserKeyHelper.isValidKey(startSceneKey) ||
          !ADBrowserKeyHelper.isValidKey(endSceneKey) ||
          !mADScenes.containsKey(startSceneKey) ||
          !mADScenes.containsKey(endSceneKey)) {
        ADBrowserLogger.e(TAG + " sceneKey无效 mADBezierTransitionModels：" +
            RiaidLogger.objectToString(mADBezierTransitionModels));
        continue;
      }
      ADScene adSceneStart = mADScenes.get(startSceneKey);
      ADScene adSceneEnd = mADScenes.get(endSceneKey);
      if (adSceneStart == null || adSceneEnd == null) {
        ADBrowserLogger.e(TAG + " 场景不存在 mADBezierTransitionModels：" +
            RiaidLogger.objectToString(mADBezierTransitionModels));
        continue;
      }

      // 构建场景的贝塞尔曲线的执行器
      BezierSceneAnimationExecutor bezierAnimationBuilder =
          new BezierSceneAnimationExecutor(mBrowserContext.getContext(), adBezierTransitionModel,
              adSceneStart.getSceneView(),
              duration);
      mBezierAnimationBuilders.add(bezierAnimationBuilder);
      // 先监听两个场景在画布中的位置
      adSceneStart.listenSceneWindowInfo(bezierAnimationBuilder::startSceneWindowsGet);

      adSceneEnd.listenSceneWindowInfo(bezierAnimationBuilder::endSceneWindowsGet);
    }
  }

  @Override
  public void cancel() {
    for (BezierSceneAnimationExecutor bezierAnimationBuilder : mBezierAnimationBuilders) {
      bezierAnimationBuilder.cancel();
    }
  }
}
