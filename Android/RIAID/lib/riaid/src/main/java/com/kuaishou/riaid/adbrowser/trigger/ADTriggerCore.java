package com.kuaishou.riaid.adbrowser.trigger;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.ADDirector;
import com.kuaishou.riaid.adbrowser.condition.ADConditionOperator;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.RiaidModel;

/**
 * 负责所有触发器的管理的枢纽，构建了{@link ADTriggerOperator}，进行统一管理
 * 对外暴露接口，可以执行和释放{@link ADTrigger}，并负责管理{@link ADConditionOperator}
 */
public class ADTriggerCore {
  private static final String TAG = "ADTriggerOperator";
  /**
   * 保存了当前的数据模型，构建其一些对象可能需要
   */
  @SuppressWarnings("SpellCheckingInspection")
  @NonNull
  private final RiaidModel mRiaidModel;

  /**
   * 所有的触发器管理
   */
  @NonNull
  private final ADTriggerOperator mADBaseTriggerOperator;
  @NonNull
  private final ADBrowserContext mBrowserContext;

  /**
   * 构建{@link ADTrigger}时有用，key是场景的key。
   */
  @Nullable
  private Map<Integer, ADScene> mADScenes;

  /**
   * @param browserContext 主要是为了构建{@link ADTrigger}
   * @param riaidModel     其数据模型用于创建相应的对象
   */
  public ADTriggerCore(@NonNull ADBrowserContext browserContext,
      @NonNull RiaidModel riaidModel) {
    this.mBrowserContext = browserContext;
    this.mRiaidModel = riaidModel;
    this.mADBaseTriggerOperator = new ADTriggerOperator(browserContext, riaidModel);

  }

  /**
   * 改变条件对应的值，这些条件会对使用哪个{@link ADTrigger}有影响
   */
  public void alterCondition(@Nullable String conditionName, @Nullable String conditionValue) {
    mBrowserContext.getConditionOperator().alterCondition(conditionName, conditionValue);
  }

  /**
   * @param ADScenes 为了构建{@link ADTrigger}
   */
  public void setADScenes(@Nullable Map<Integer, ADScene> ADScenes) {
    mADScenes = ADScenes;
  }

  /**
   * @param key 对应了{@link ADTrigger#getTriggerKey()}
   * @return key对应的触发器
   */
  @Nullable
  public ADTrigger getTrigger(int key) {
    return mADBaseTriggerOperator.getTrigger(key);
  }

  /**
   * @param key 对应了{@link ADTrigger#getTriggerKey()}
   * @return key对应的触发器是否执行成功
   */
  public boolean executeTrigger(int key) {
    ADTrigger trigger = getTrigger(key);
    if (trigger != null) {
      return trigger.execute();
    }
    return false;
  }

  /**
   * 取消一个触发器
   *
   * @param key 对应了{@link ADTrigger#getTriggerKey()}
   */
  public boolean cancelTrigger(int key) {
    ADTrigger trigger = getTrigger(key);
    if (trigger != null) {
      trigger.cancel();
      return true;
    }
    return false;
  }

  /**
   * 构建所有的{@link ADTrigger}，通常由{@link ADDirector}调用
   */
  public void buildTrigger() {
    long startBuildTime = System.currentTimeMillis();
    if (mADScenes != null) {
      mADBaseTriggerOperator.buildTrigger(mADScenes);
    }
    ADBrowserLogger.i(TAG + "buildTrigger 耗时：" + (System.currentTimeMillis() - startBuildTime));
  }


  /**
   * 释放所有的{@link ADTrigger}，通常由{@link ADDirector}释放
   */
  public void release() {
    mADBaseTriggerOperator.release();
  }
}
