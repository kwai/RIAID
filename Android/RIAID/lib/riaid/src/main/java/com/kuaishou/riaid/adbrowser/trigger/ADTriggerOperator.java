package com.kuaishou.riaid.adbrowser.trigger;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.ADDirector;
import com.kuaishou.riaid.adbrowser.helper.ADBrowserKeyHelper;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.ADTriggerModel;
import com.kuaishou.riaid.proto.nano.RiaidModel;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.logger.RiaidLogger;

/**
 * 触发器管理，可以根据key获取一个{@link ADTrigger}
 * 如果是一组{@link ADTrigger}条件一样，但是描述的行为不一致，那
 * 么需要选一个{@link ADTrigger}出来。
 */
public class ADTriggerOperator {
  private static final String TAG = "ADBaseTriggerOperator";
  @NonNull
  private final RiaidModel mAdDSL;
  @NonNull
  private final ADBrowserContext mBrowserContext;
  /**
   * {@link ADTrigger}的集合，
   * 页面离开时需要清空。
   */
  @NonNull
  private final Map<Integer, ADTrigger> mTriggers = new HashMap<>();

  public ADTriggerOperator(@NonNull ADBrowserContext browserContext,
      @NonNull RiaidModel adDSL) {
    mBrowserContext = browserContext;
    mAdDSL = adDSL;
  }

  /**
   * 注册一个{@link ADTrigger}进来，常常是注册的自定义的，如：{@link ADHeartBeatTrigger}
   *
   * @param triggerKey 唯一的标识
   * @param adTrigger  对应的{@link ADTrigger}
   */
  public void putTrigger(int triggerKey, @NonNull ADTrigger adTrigger) {
    mTriggers.put(triggerKey, adTrigger);
  }

  /**
   * @param key 是{@link ADTrigger#getTriggerKey()}
   * @return key对应的触发器
   */
  @Nullable
  public ADTrigger getTrigger(int key) {
    // 先从存放触发器的三个map中尝试找
    return getTriggerFromMaps(key);
  }

  @Nullable
  protected ADTrigger getTriggerFromMaps(int key) {
    if (mTriggers.containsKey(key)) {
      return mTriggers.get(key);
    }
    ADBrowserLogger.e(TAG + "触发器中没有这个key: " + key);
    return null;
  }

  /**
   * 释放所有的{@link ADTrigger}，通常由{@link ADDirector}释放
   */
  public void release() {
    for (Map.Entry<Integer, ADTrigger> trigger : mTriggers.entrySet()) {
      trigger.getValue().cancel();
    }
    mTriggers.clear();
  }

  /**
   * 构建所有的{@link ADTrigger}，通常由{@link ADDirector}调用
   *
   * @param adScenes 构建{@link ADTrigger}需要的参数
   */
  public void buildTrigger(@NonNull Map<Integer, ADScene> adScenes) {
    mTriggers.clear();
    ADBrowserLogger.i("buildActiveTrigger");

    // 构建Trigger
    if (mAdDSL.triggers != null) {
      for (int i = 0; i < mAdDSL.triggers.length; i++) {
        ADTriggerModel triggerModel = mAdDSL.triggers[i];
        if (triggerModel != null) {
          if (!ADBrowserKeyHelper.isValidTriggerByKey(triggerModel)) {
            ADRenderLogger
                .e("Trigger triggerKey为空，不合法" + RiaidLogger.objectToString(triggerModel));
            continue;
          }
          ADTrigger adTrigger =
              ADTriggerFactory.createADTrigger(mBrowserContext, adScenes, triggerModel);
          if (adTrigger != null) {
            mTriggers.put(ADBrowserKeyHelper.getTriggerKey(triggerModel), adTrigger);
          }
        }
      }
    }
  }
}
