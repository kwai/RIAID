package com.kuaishou.riaid.adbrowser.trigger;


import java.util.Map;

import android.os.Handler;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.action.ADAction;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.adbrowser.timer.TimeOutController;
import com.kuaishou.riaid.adbrowser.transition.ADTransitionExecutor;
import com.kuaishou.riaid.proto.nano.ADTimeoutTriggerModel;

/**
 * 定时触发器，包含了一系列的要触发的{@link ADAction}，在一定的时间，会触发这些{@link ADAction}
 * 如果一个时间触发器，是和Scene绑定的，那么Scene消失的时候，对应的时间控制器也需要销毁
 */
public class ADTimeoutTrigger extends ADBaseTrigger<ADTimeoutTriggerModel> {
  /**
   * 持有{@link Handler}是发了定时触发{@link ADTrigger}
   * 会在{@link #cancel()} ()}时移除其持有的{@link Runnable}
   */
  private final TimeOutController mTimeoutController = new TimeOutController();

  private final ADTimeoutTriggerModel mTimerTriggerModel;

  /**
   * @param context      主要是为了传递给{@link ADTransitionExecutor}
   * @param adScenes     所有的场景，主要是为了找到相应的场景
   * @param triggerModel 触发器的数据模型
   */
  public ADTimeoutTrigger(@NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes,
      @NonNull ADTimeoutTriggerModel triggerModel) {
    super(context, adScenes, triggerModel);
    mTimerTriggerModel = triggerModel;
  }

  @Override
  public boolean execute() {
    ADBrowserLogger.i("触发器将要执行了execute 触发器类型：ADTimeoutTrigger 触发器key: " + getTriggerKey());

    if (mTimerTriggerModel.interval <= 0) {
      return false;
    }
    mTimeoutController.timeoutExecute(this::executeActions, mTimerTriggerModel.interval);
    return true;
  }

  @Override
  public void cancel() {
    cancelActions();
    mTimeoutController.cancel();
  }
}
