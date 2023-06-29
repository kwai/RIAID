package com.kuaishou.riaid.adbrowser.trigger;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.action.ADAction;
import com.kuaishou.riaid.adbrowser.action.ADBeepAction;
import com.kuaishou.riaid.adbrowser.action.ADClickableAction;
import com.kuaishou.riaid.adbrowser.action.ADConversionAction;
import com.kuaishou.riaid.adbrowser.action.ADCustomAction;
import com.kuaishou.riaid.adbrowser.action.ADExecuteHandlerAction;
import com.kuaishou.riaid.adbrowser.action.ADExecuteTriggerAction;
import com.kuaishou.riaid.adbrowser.action.ADLottieAction;
import com.kuaishou.riaid.adbrowser.action.ADStepAction;
import com.kuaishou.riaid.adbrowser.action.ADTrackAction;
import com.kuaishou.riaid.adbrowser.action.ADTransitionAction;
import com.kuaishou.riaid.adbrowser.action.ADUrlAction;
import com.kuaishou.riaid.adbrowser.action.ADVariableChangeAction;
import com.kuaishou.riaid.adbrowser.action.ADVibratorAction;
import com.kuaishou.riaid.adbrowser.action.ADVideoAction;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.ADActionModel;
import com.kuaishou.riaid.proto.nano.ADCancelDeviceMotionActionModel;
import com.kuaishou.riaid.proto.nano.ADCancelTimerActionModel;
import com.kuaishou.riaid.proto.nano.ADConditionChangeActionModel;

/**
 * 触发器，包含了一系列的要触发的{@link ADAction}，职责单一，对外暴露执行和取消接口
 * 每个触发器都有唯一的标识，而多个触发器如果是表示一个行为，则这一组触发器由唯一的unionKey标识
 */
public abstract class ADBaseTrigger<T> implements ADTrigger {
  private static final String TAG = "ADBaseTrigger";
  @NonNull
  protected final Map<Integer, ADScene> mADScenes;
  /**
   * 所有的行为集合，这里是个二维数组
   * 一维是各类的行为类型
   * 二维是该行为类型对应的行为集合
   */
  @NonNull
  protected final List<ADAction> mADActions = new ArrayList<>();

  protected final ADTriggerDelegateModel mTriggerModel;

  @NonNull
  protected final ADBrowserContext mBrowserContext;

  /**
   * @param context      主要是为了传递给
   *                     {@link com.kuaishou.riaid.adbrowser.transition.ADTransitionExecutor}
   * @param adScenes     所有的场景，主要是为了找到相应的场景
   * @param triggerModel 触发器的数据模型
   */
  public ADBaseTrigger(@NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes,
      @NonNull T triggerModel) {
    mBrowserContext = context;
    mADScenes = adScenes;
    mTriggerModel = new ADTriggerDelegateModel(triggerModel);
    buildAction(mTriggerModel.getActions());
  }

  /**
   * 构建所有的行为，会先将之前的行为数组清空
   *
   * @param actionModels 行为模型数组，不为空
   */
  protected void buildAction(ADActionModel[] actionModels) {
    cancelActions();
    mADActions.clear();
    if (actionModels == null || actionModels.length <= 0) {
      return;
    }
    for (ADActionModel actionModel : actionModels) {
      if (actionModel == null) {
        continue;
      }
      if (actionModel.transition != null) {
        mADActions
            .add(new ADTransitionAction(mBrowserContext, mADScenes, actionModel.transition));
      } else if (actionModel.track != null) {
        mADActions
            .add(new ADTrackAction(mBrowserContext, actionModel.track));
      } else if (actionModel.video != null) {
        mADActions
            .add(new ADVideoAction(mBrowserContext, mADScenes, actionModel.video));
      } else if (actionModel.url != null) {
        mADActions
            .add(new ADUrlAction(mBrowserContext, actionModel.url));
      } else if (actionModel.conversion != null) {
        // 触发一个转化的行为
        mADActions
            .add(new ADConversionAction(mBrowserContext, actionModel.conversion));
      } else if (actionModel.custom != null) {
        mADActions
            .add(new ADCustomAction(mBrowserContext, actionModel.custom));
      } else if (actionModel.trigger != null) {
        // 触发一个指定的触发器
        mADActions.add(new ADExecuteTriggerAction(mBrowserContext,
            actionModel.trigger));
      } else if (actionModel.cancelTimer != null) {
        // 取消一个定时触发器
        mADActions.add(buildExecuteHandlerAction(ADCancelTimerActionModel.class,
            actionModel.cancelTimer));
      } else if (actionModel.cancelDeviceMotion != null) {
        // 取消一个设备监听触发器
        mADActions.add(buildExecuteHandlerAction(ADCancelDeviceMotionActionModel.class,
            actionModel.cancelDeviceMotion));
      } else if (actionModel.conditionChange != null) {
        // 改变一个条件
        mADActions.add(buildExecuteHandlerAction(ADConditionChangeActionModel.class,
            actionModel.conditionChange));
      } else if (actionModel.variableChange != null) {
        // 改变一个变量
        mADActions.add(new ADVariableChangeAction(mBrowserContext, actionModel.variableChange));
      } else if (actionModel.step != null) {
        // 分步执行行为
        mADActions.add(new ADStepAction(mBrowserContext, actionModel.step));
      } else if (actionModel.beep != null) {
        // 播放提示音效行为
        mADActions.add(new ADBeepAction(mBrowserContext, actionModel.beep));
      } else if (actionModel.vibrator != null) {
        // 震动执行行为
        mADActions.add(new ADVibratorAction(mBrowserContext, actionModel.vibrator));
      } else if (actionModel.lottie != null) {
        // Lottie控制行为
        mADActions.add(new ADLottieAction(mBrowserContext, mADScenes, actionModel.lottie));
      } else if (actionModel.clickable != null) {
        // 点击控制行为
        mADActions.add(new ADClickableAction(mBrowserContext, mADScenes, actionModel.clickable));
      }
    }
  }

  /**
   * 构建直接通过{@link com.kuaishou.riaid.adbrowser.adbridge.ADBridge}执行的{@link ADAction}
   *
   * @return {@link ADAction}
   */
  private <E> ADAction buildExecuteHandlerAction(@NonNull Class<E> tClass, @NonNull E actionModel) {
    return new ADExecuteHandlerAction<>(mBrowserContext, tClass,
        actionModel);
  }

  @Override
  public int getTriggerKey() {
    return mTriggerModel.getKey();
  }

  /**
   * 执行本触发器的所有行为
   */
  protected void executeActions() {
    for (ADAction adAction : mADActions) {
      adAction.execute();
    }
  }

  /**
   * 取消本触发器的所有行为
   */
  protected void cancelActions() {
    for (ADAction adAction : mADActions) {
      adAction.cancel();
    }
  }
}
