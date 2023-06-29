package com.kuaishou.riaid.adbrowser.trigger;

import java.util.Map;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.adbrowser.timer.IntervalTimeController;
import com.kuaishou.riaid.proto.nano.ADHeartBeatTriggerModel;

/**
 * 一个倒计时的触发器，并不是在dsl中定义的触发器，是内部构建出来的
 * 倒计时会定时的触发dsl中定义的触发器
 * 如果一个时间触发器，是和Scene绑定的，那么Scene消失的时候，对应的时间控制器也需要销毁
 */
public class ADHeartBeatTrigger extends ADBaseTrigger<ADHeartBeatTriggerModel> {
  @NonNull
  private final IntervalTimeController mIntervalTimeController = new IntervalTimeController();

  @NonNull
  private final ADHeartBeatTriggerModel mADHeartBeatTriggerModel;

  /**
   * @param context      主要是为了传递给
   *                     {@link com.kuaishou.riaid.adbrowser.transition.ADTransitionExecutor}
   * @param adScenes     所有的场景，主要是为了找到相应的场景
   * @param triggerModel 触发器的数据模型
   */
  public ADHeartBeatTrigger(@NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes,
      @NonNull ADHeartBeatTriggerModel triggerModel) {
    super(context, adScenes, triggerModel);
    mADHeartBeatTriggerModel = triggerModel;
  }

  @Override
  public boolean execute() {
    ADBrowserLogger.i("触发器将要执行了execute 触发器类型：ADHeartBeatTrigger 触发器key: " + getTriggerKey());
    if (mADHeartBeatTriggerModel.count <= 0 || mADHeartBeatTriggerModel.interval <= 0) {
      return false;
    }
    mIntervalTimeController.start(mADHeartBeatTriggerModel.interval, mADHeartBeatTriggerModel.count,
        (count, interval, millis) -> executeActions()
    );
    return true;
  }

  @Override
  public void cancel() {
    cancelActions();
    mIntervalTimeController.cancel();
  }
}
