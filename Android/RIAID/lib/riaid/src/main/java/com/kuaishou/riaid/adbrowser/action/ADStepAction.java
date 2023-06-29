package com.kuaishou.riaid.adbrowser.action;


import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.programming.ADVariableOperator;
import com.kuaishou.riaid.proto.nano.ADStepActionModel;
import com.kuaishou.riaid.proto.nano.ADTriggerActionModel;
import com.kuaishou.riaid.proto.nano.BasicVariable;
import com.kuaishou.riaid.proto.nano.BasicVariableValue;
import com.kuaishou.riaid.render.logger.RiaidLogger;

/**
 * 分步执行的行为，每次执行需要取出指定的全局变量，并且对其进行操作，++step。
 * 另外，进行++step会受到{@link ADStepActionModel#max}和{@link ADStepActionModel#min}的限制
 * 执行完变量操作后，如果{@link ADStepActionModel}还定义了触发器，则去触发。
 */
public class ADStepAction extends ADBaseAction<ADStepActionModel> {
  private static final String TAG = "ADStepAction";

  public ADStepAction(@NonNull ADBrowserContext browserContext,
      @NonNull ADStepActionModel adActionModel) {
    super(browserContext, adActionModel);
  }

  @Override
  public boolean execute() {
    ADVariableOperator variableOperator = mBrowserContext.getVariableOperator();
    // 先找到这个要操作的变量
    BasicVariable variableByKey = variableOperator.findVariableByKey(mADActionModel.variableKey);
    if (!ADVariableOperator.isValidVariable(variableByKey)) {
      ADBrowserLogger.e(TAG + "ADStepActionModel不合法 mADActionModel:" +
          RiaidLogger.objectToString(mADActionModel));
      return false;
    }
    // 仅支持整形的操作
    if (variableByKey.value.type != BasicVariableValue.INTEGER) {
      ADBrowserLogger.e(TAG + "ADStepActionModel提供了不是INTEGER类型的变量 mADActionModel: " +
          RiaidLogger.objectToString(mADActionModel));
      return false;
    }

    long value = variableByKey.value.i;
    value += mADActionModel.step;
    if (value >= mADActionModel.min && value <= mADActionModel.max) {
      // 只要在规定范围内才能赋值
      variableByKey.value.i = value;
    }

    // 最后执行其他的触发器
    if (mADActionModel.triggerKeys != null && mADActionModel.triggerKeys.length > 0) {
      ADTriggerActionModel adTriggerActionModel = new ADTriggerActionModel();
      adTriggerActionModel.triggerKeys = mADActionModel.triggerKeys;
      mBrowserContext.getADBridge().handle(ADTriggerActionModel.class, adTriggerActionModel);
    }
    return true;
  }
}
