package com.kuaishou.riaid.adbrowser.trigger;


import java.util.Map;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.action.ADAction;
import com.kuaishou.riaid.adbrowser.condition.ADConditionOperator;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.programming.ADVariableOperator;
import com.kuaishou.riaid.adbrowser.programming.LogicOperator;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.adbrowser.transition.ADTransitionExecutor;
import com.kuaishou.riaid.proto.nano.ADConditionLogicModel;
import com.kuaishou.riaid.proto.nano.ADConditionTriggerModel;
import com.kuaishou.riaid.proto.nano.BasicVariable;

/**
 * 条件触发器，包含了一系列的要触发的{@link ADAction}，满足一定条件才能触发系列{@link ADAction}
 */
public class ADConditionTrigger extends ADBaseTrigger<ADConditionTriggerModel> {
  @NonNull
  private final ADConditionTriggerModel mConditionTriggerModel;

  /**
   * @param context      主要是为了传递给{@link ADTransitionExecutor}
   * @param adScenes     所有的场景，主要是为了找到相应的场景
   * @param triggerModel 触发器的数据模型
   */
  public ADConditionTrigger(@NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes,
      @NonNull ADConditionTriggerModel triggerModel) {
    super(context, adScenes, triggerModel);
    mConditionTriggerModel = triggerModel;
  }

  @Override
  public boolean execute() {
    ADBrowserLogger.i("触发器将要执行了execute 触发器类型：ADConditionTrigger 触发器key: " + getTriggerKey());
    if (mConditionTriggerModel.logics == null) {
      return false;
    }
    ADConditionOperator conditionOperator = mBrowserContext.getConditionOperator();
    ADVariableOperator variableOperator = mBrowserContext.getVariableOperator();

    Map<String, String> currentADConditions =
        conditionOperator.getCurrentADConditions();
    Map<Integer, BasicVariable> currentADVariables = variableOperator.getCurrentADVariables();
    // 同一时间，数组内应当有且仅有一个ADConditionLogicModel是符合执行条件的
    for (ADConditionLogicModel logic : mConditionTriggerModel.logics) {
      Boolean isMatch = new LogicOperator().isMatchGroupUnit(
          conditionOperator,
          variableOperator,
          currentADConditions,
          currentADVariables,
          logic);
      if (isMatch == null) {
        continue;
      }

      // 如果所有的条件都匹配，那么开始构建行为并执行
      if (isMatch) {
        buildAction(logic.actions);
        executeActions();
        return true;
      }
    }
    return false;
  }


  @Override
  public void cancel() {
    cancelActions();
  }
}
