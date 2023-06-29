package com.kuaishou.riaid.adbrowser.programming;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.condition.ADConditionOperator;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.proto.nano.ADConditionLogicModel;
import com.kuaishou.riaid.proto.nano.ADConditionModel;
import com.kuaishou.riaid.proto.nano.ADLogicUnitModel;
import com.kuaishou.riaid.proto.nano.BasicVariable;
import com.kuaishou.riaid.proto.nano.RIAID;

/**
 * 逻辑判断操作，用于判断一组逻辑单元是否满足当前条件
 */
public class LogicOperator {
  private static final String TAG = "LogicOperator";

  /**
   * @param conditionOperator   条件判断器
   * @param variableOperator    变量判断器
   * @param currentADConditions 判断基准，当前条件
   * @param currentADVariables  判断基准，当前变量
   * @param logic               逻辑单元集
   * @return 逻辑单元集与当前条件与变量的逻辑关系成立，为返回true。如果数据本身有误则返回null
   */
  @Nullable
  public Boolean isMatchGroupUnit(
      @NonNull ADConditionOperator conditionOperator,
      @NonNull ADVariableOperator variableOperator,
      @NonNull Map<String, String> currentADConditions,
      @NonNull Map<Integer, BasicVariable> currentADVariables,
      @NonNull ADConditionLogicModel logic) {
    if (logic.actions == null || logic.units == null) {
      ADBrowserLogger.w(TAG + " 逻辑单元组或执行Action为空");
      return null;
    }
    int operator = logic.operator;
    // 如果是或逻辑，起始的isMatch应该是false，如果是与和非逻辑，起始的isMatch应该是true
    boolean isMatch = operator != RIAID.LOGIC_OPERATOR_OR;
    // 遍历所有的单元，准备开始判断条件是否匹配
    for (ADLogicUnitModel unit : logic.units) {
      ADConditionModel condition = unit.condition;
      BasicVariable variable = unit.variable;
      if (condition == null && variable == null) {
        ADBrowserLogger.w(TAG + " 条件和变量都为空");
        continue;
      }
      if (condition != null && variable != null) {
        ADBrowserLogger.w(TAG + " 条件和变量都不为空，两者只能有一个不为空");
        continue;
      }
      // 给isMatch重新赋值，下次循环继续判断
      if (condition != null) {
        // 条件判断
        isMatch =
            conditionOperator.isMatchSingleUnit(currentADConditions, operator, isMatch, unit);
      }
      if (variable != null) {
        // 变量判断
        isMatch = variableOperator
            .isMatchSingleUnit(currentADVariables, operator, isMatch, unit);
      }
    }
    // 如果operator是非的话，需要取反
    if (operator == RIAID.LOGIC_OPERATOR_NOT) {
      isMatch = !isMatch;
    }
    return isMatch;
  }
}
