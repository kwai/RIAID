package com.kuaishou.riaid.adbrowser.trigger;


import java.util.Map;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.action.ADAction;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.adbrowser.transition.ADTransitionExecutor;
import com.kuaishou.riaid.proto.nano.ADGeneralTriggerModel;

/**
 * 触发器，包含了一系列的要触发的{@link ADAction}，职责单一，对外暴露执行和取消接口
 * 每个触发器都有唯一的标识，而多个触发器如果是表示一个行为，则这一组触发器由唯一的unionKey标识
 */
public class ADGeneralTrigger extends ADBaseTrigger<ADGeneralTriggerModel> {

  /**
   * @param context      主要是为了传递给{@link ADTransitionExecutor}
   * @param adScenes     所有的场景，主要是为了找到相应的场景
   * @param triggerModel 触发器的数据模型
   */
  public ADGeneralTrigger(@NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes,
      @NonNull ADGeneralTriggerModel triggerModel) {
    super(context, adScenes, triggerModel);
  }

  @Override
  public boolean execute() {
    ADBrowserLogger.i("触发器将要执行了execute 触发器类型：ADGeneralTrigger 触发器key: " + getTriggerKey());
    executeActions();
    return true;
  }

  @Override
  public void cancel() {
    cancelActions();
  }
}
