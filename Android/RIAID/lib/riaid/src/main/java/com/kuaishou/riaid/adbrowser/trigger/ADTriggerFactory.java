package com.kuaishou.riaid.adbrowser.trigger;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.ADTriggerModel;
import com.kuaishou.riaid.render.logger.RiaidLogger;

/**
 * 根据不同的数据模型，构建出不同的触发器，目前支持触发器类型：
 * {@link ADTimeoutTrigger}
 * {@link ADHeartBeatTrigger}
 * {@link ADGeneralTrigger}
 * {@link ADConditionTrigger}
 * {@link ADVideoDurationTimeoutTrigger}
 */
public class ADTriggerFactory {
  private ADTriggerFactory() {}

  @Nullable
  public static ADTrigger createADTrigger(@NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes,
      @NonNull ADTriggerModel triggerModel) {
    if (triggerModel.general != null) {
      // 普通触发器
      return new ADGeneralTrigger(context, adScenes, triggerModel.general);
    } else if (triggerModel.timeout != null) {
      // 定时触发器
      return new ADTimeoutTrigger(context, adScenes, triggerModel.timeout);
    } else if (triggerModel.heartbeat != null) {
      // 心跳触发器
      return new ADHeartBeatTrigger(context, adScenes, triggerModel.heartbeat);
    } else if (triggerModel.condition != null) {
      // 条件触发器
      return new ADConditionTrigger(context, adScenes, triggerModel.condition);
    } else if (triggerModel.videoDuration != null) {
      // 视频时长定时触发器
      return new ADVideoDurationTimeoutTrigger(context, adScenes, triggerModel.videoDuration);
    }else if (triggerModel.deviceMotion != null) {
      // 设备运动传感触发器
      return new ADDeviceMotionTrigger(context, adScenes, triggerModel.deviceMotion);
    } else {
      ADBrowserLogger
          .e("ADTriggerFactory 创建触发器时，没有可创建的触发器 triggerModel:" +
              RiaidLogger.objectToString(triggerModel));
    }
    return null;
  }
}
