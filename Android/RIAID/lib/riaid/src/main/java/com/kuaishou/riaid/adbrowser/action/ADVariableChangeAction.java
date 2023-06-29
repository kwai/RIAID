package com.kuaishou.riaid.adbrowser.action;


import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.programming.ADVariableOperator;
import com.kuaishou.riaid.proto.nano.ADVariableChangeActionModel;
import com.kuaishou.riaid.render.logger.RiaidLogger;

/**
 * 变量改变的行为，用来找到这个变量并且将其值改变。
 * {@link ADVariableChangeActionModel}会有{@link com.kuaishou.riaid.proto.nano.BasicVariable}
 * 在这个行为中，负责将这个变量从全局变量池中找出来，并对其进行重新赋值。
 */
public class ADVariableChangeAction extends ADBaseAction<ADVariableChangeActionModel> {
  private static final String TAG = "ADVariableChangeAction";

  public ADVariableChangeAction(@NonNull ADBrowserContext browserContext,
      @NonNull ADVariableChangeActionModel adActionModel) {
    super(browserContext, adActionModel);
  }

  @Override
  public boolean execute() {
    ADVariableOperator variableOperator = mBrowserContext.getVariableOperator();
    if (ADVariableOperator.isValidVariable(mADActionModel.variable)) {
      variableOperator.alterCondition(mADActionModel.variable);
      return true;
    } else {
      ADBrowserLogger.e(TAG + "ADVariableChangeActionModel 中的变量不合法 mADActionModel：" +
          RiaidLogger.objectToString(variableOperator));
    }
    return false;
  }
}
