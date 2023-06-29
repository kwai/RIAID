package com.kuaishou.riaid.adbrowser.transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.ADLottieTransitionModel;
import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.proto.nano.FloatValue;
import com.kuaishou.riaid.proto.nano.LottieAttributes;
import com.kuaishou.riaid.render.config.DSLRenderCreator;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.logger.RiaidLogger;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;

/**
 * Lottie动画的执行器，常用于场景内的动画
 */
public class ADLottieTransitionExecutor extends ADBaseTransitionExecutor {
  @Nullable
  private List<ADLottieTransitionModel> mADLottieTransitionModels;

  /**
   * 触发的定时器集合
   */
  @NonNull
  private final List<CountDownTimer> mDownTimerList = new ArrayList<>();

  public ADLottieTransitionExecutor(
      @NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes) {
    super(context, adScenes);
  }

  public void setADLottieTransitionModels(
      @Nullable List<ADLottieTransitionModel> ADLottieTransitionModels) {
    mADLottieTransitionModels = ADLottieTransitionModels;
  }

  @Override
  public void execute() {
    if (mADLottieTransitionModels == null) {
      ADBrowserLogger.e("ADLottieTransitionExecutor 执行失败 mADLottieTransitionModels 为空");
      return;
    }
    ADBrowserLogger.i("ADLottieTransitionExecutor mADLottieTransitionModels: " +
        RiaidLogger.objectToString(mADLottieTransitionModels));
    for (ADLottieTransitionModel adLottieTransitionModel : mADLottieTransitionModels) {
      if (adLottieTransitionModel == null) {
        ADBrowserLogger.e("ADLottieTransitionExecutor adLottieTransitionModel为空");
        continue;
      }
      if (mADScenes.containsKey(adLottieTransitionModel.sceneKey)) {
        ADScene adScene = mADScenes.get(adLottieTransitionModel.sceneKey);
        DSLRenderCreator render = adScene.getRenderCreator();
        if (render == null) {
          ADRenderLogger
              .e("ADLottieTransitionExecutor 场景内的Render为空 key: " + adScene.getSceneKey());
          continue;
        }
        AbsObjectNode<?> rootRender = render.rootRender;

        if (rootRender != null) {
          List<Integer> viewKeys = new ArrayList<>();
          if (adLottieTransitionModel.viewKeys != null) {
            for (int viewKey : adLottieTransitionModel.viewKeys) {
              viewKeys.add(viewKey);
            }
          }
          // 需要创建Lottie的属性
          Attributes attributes = new Attributes();
          attributes.lottie = new LottieAttributes();
          FloatValue floatValue = new FloatValue();
          // 创建一个定时器，不断的去更新其进度
          CountDownTimer countDownTimer = new CountDownTimer(adLottieTransitionModel.maxProgress,
              adLottieTransitionModel.interval) {
            @Override
            public void onTick(long millisUntilFinished) {

              // 拿到当前的进度
              floatValue.value =
                  (adLottieTransitionModel.maxProgress - millisUntilFinished) * 1.0f /
                      adLottieTransitionModel.maxProgress * 1.0f;
              attributes.lottie.progress = floatValue;

              // 分发更新进度
              rootRender.dispatchEvent(adLottieTransitionModel.lottieType, viewKeys, attributes);
            }

            @Override
            public void onFinish() {
              floatValue.value = 1.0f;
              attributes.lottie.progress = floatValue;
              rootRender.dispatchEvent(adLottieTransitionModel.lottieType, viewKeys, attributes);

            }
          }.start();
          mDownTimerList.add(countDownTimer);
        }
      }
    }
  }

  @Override
  public void cancel() {
    for (CountDownTimer countDownTimer : mDownTimerList) {
      countDownTimer.cancel();
    }
  }
}
