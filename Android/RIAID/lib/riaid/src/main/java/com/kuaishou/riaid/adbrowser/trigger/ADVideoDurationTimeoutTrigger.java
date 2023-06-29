package com.kuaishou.riaid.adbrowser.trigger;


import java.util.Map;

import android.os.Handler;
import android.util.Pair;
import android.view.View;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowser;
import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.action.ADAction;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.adbrowser.timer.IntervalTimeController;
import com.kuaishou.riaid.adbrowser.transition.ADTransitionExecutor;
import com.kuaishou.riaid.proto.nano.ADVideoDurationTimeoutTriggerModel;
import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.util.AttributeGetter;

/**
 * 视频的定时触发器，该触发器会绑定一个视频且会维持一个计时器。一旦执行，不断的获取视频的当前播放时长，当播放时长满足
 * 了设定的条件，则触发对应的系列{@link ADAction}。
 */
public class ADVideoDurationTimeoutTrigger
    extends ADBaseTrigger<ADVideoDurationTimeoutTriggerModel> {
  private static final String TAG = "ADVideoDurationTimeoutT";
  /**
   * 持有{@link Handler}是发了定时触发{@link ADTrigger}
   * 会在{@link #cancel()} ()}时移除其持有的{@link Runnable}
   */
  private final IntervalTimeController mTimeoutController = new IntervalTimeController();

  private final ADVideoDurationTimeoutTriggerModel mTimerTriggerModel;

  /**
   * @param context      主要是为了传递给{@link ADTransitionExecutor}
   * @param adScenes     所有的场景，主要是为了找到相应的场景
   * @param triggerModel 触发器的数据模型
   */
  public ADVideoDurationTimeoutTrigger(@NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes,
      @NonNull ADVideoDurationTimeoutTriggerModel triggerModel) {
    super(context, adScenes, triggerModel);
    mTimerTriggerModel = triggerModel;
  }

  /**
   * 要检测到视频播放到{@code mTimerTriggerModel.interval}位置，然后执行操作。
   * {@code execute()}一旦执行， 就需要不断的获取视频的播放位置，
   * 这个间隔时间需要 比{@code mTimerTriggerModel.interval}小，为了其准确性，
   * 经间隔设置为mTimerTriggerModel.interval / 10。
   */
  @Override
  public boolean execute() {
    if (mTimerTriggerModel.interval <= 0) {
      return false;
    }
    Pair<ADScene, View> sceneAndViewByKey = ADBrowser
        .findSceneAndViewByKey(mADScenes, mTimerTriggerModel.viewKey);

    if (sceneAndViewByKey == null || sceneAndViewByKey.first.getRenderCreator() == null ||
        sceneAndViewByKey.first.getRenderCreator().rootRender == null) {
      ADBrowserLogger.e(TAG + " 查找view失败，viewKey: " + mTimerTriggerModel.viewKey);

      return false;
    }

    AbsObjectNode<?> rootRender = sceneAndViewByKey.first.getRenderCreator().rootRender;
    // 为了提高精度，设置间隔为目标值对10的除数。
    long timeInterval = mTimerTriggerModel.interval / 10;
    mTimeoutController.start(timeInterval, (count, interval, millis) -> {
      // 每次间隔获取当前的视频时长
      long videoPosition = AttributeGetter.getAttributeVideoPosition(
          mTimerTriggerModel.viewKey, Attributes.ATTRIBUTE_VIDEO_POSITION, rootRender
      );

      // 当视频时长大于等于目标时长，执行声明的行为，同时需要把间隔控制器取消掉，本次任务执行完成。
      if (videoPosition >= mTimerTriggerModel.interval) {
        executeActions();
        mTimeoutController.cancel();
      }
    });
    return true;
  }

  @Override
  public void cancel() {
    cancelActions();
    mTimeoutController.cancel();
  }
}
