package com.kuaishou.riaid.adbrowser.helper;

import android.view.View;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.ADTriggerActionModel;
import com.kuaishou.riaid.proto.nano.ADTriggerModel;
import com.kuaishou.riaid.proto.nano.SystemKeyEnum;


/**
 * 系统中对于key的一些处理和判断，比如是不是有效的key等。
 */
public class ADBrowserKeyHelper {
  /**
   * @param key 要判断的key值
   * @return 是否为画布的key
   */
  public static boolean isCanvas(int key) {
    return key == SystemKeyEnum.SCENE_KEY_CANVAS;
  }

  /**
   * @param key 要判断的key值
   * @return 这个key是不是有效的
   */
  public static boolean isValidKey(int key) {
    return key != SystemKeyEnum.INVALID_KEY;
  }

  /**
   * @param triggerModel 要判断的数据模型
   * @return 根据key来判断这个触发器的数据模型是不是有效的
   */
  public static boolean isValidTriggerByKey(@Nullable ADTriggerModel triggerModel) {
    if (triggerModel == null) {
      return false;
    }
    return isValidKey(getTriggerKey(triggerModel));
  }

  /**
   * {@link ADTriggerModel}可能持有多种触发器，拿到其中触发器的key
   *
   * @param triggerModel 触发器包裹类
   * @return 对应的key
   */
  public static int getTriggerKey(@Nullable ADTriggerModel triggerModel) {
    if (triggerModel == null) {
      return SystemKeyEnum.INVALID_KEY;
    }
    if (triggerModel.condition != null) {
      return triggerModel.condition.key;
    } else if (triggerModel.general != null) {
      return triggerModel.general.key;
    } else if (triggerModel.heartbeat != null) {
      return triggerModel.heartbeat.key;
    } else if (triggerModel.timeout != null) {
      return triggerModel.timeout.key;
    } else if (triggerModel.videoDuration != null) {
      return triggerModel.videoDuration.key;
    } else if (triggerModel.deviceMotion != null) {
      return triggerModel.deviceMotion.key;
    } else {
      return SystemKeyEnum.INVALID_KEY;
    }
  }

  /**
   * 根据triggerKey数组构建一个 {@link ADTriggerActionModel}，
   * 通常是通过{@link com.kuaishou.riaid.adbrowser.adbridge.ADBridge}去触发这些
   * {@link com.kuaishou.riaid.adbrowser.trigger.ADTrigger}
   *
   * @param triggerKeys key数组
   * @return {@link ADTriggerActionModel}
   */
  @Nullable
  public static ADTriggerActionModel buildTriggerActionModel(@Nullable int[] triggerKeys) {
    if (triggerKeys == null || triggerKeys.length <= 0) {
      return null;
    }
    ADTriggerActionModel adTriggerActionModel = new ADTriggerActionModel();
    adTriggerActionModel.triggerKeys = triggerKeys;
    return adTriggerActionModel;
  }

  /**
   * @param key 要判断的key值
   * @return 这个key是否是指的当前展示的所有场景
   */
  public static boolean isCurrentScenes(int key) {
    return false;
  }

  /**
   * 要获取一个随机的key，但是不能与已有的重复
   * 使用{@link View#generateViewId()}的能力
   *
   * @return 唯一的key
   */
  public static int generateKey() {
    int key = -View.generateViewId();
    for (int systemKey : SYSTEM_KEYS) {
      if (key == systemKey) {
        key = -View.generateViewId();
      }
    }
    // 再次生成的key就不可能是系统的key了
    return -View.generateViewId();
  }


  /**
   * 所有的系统key，记录下来，在生成key的时候需要过滤掉这些
   */
  private static final int[] SYSTEM_KEYS = new int[]
      {SystemKeyEnum.SCENE_KEY_CANVAS, SystemKeyEnum.TRIGGER_KEY_AD_VIDEO_END,
          SystemKeyEnum.INVALID_KEY};
}
