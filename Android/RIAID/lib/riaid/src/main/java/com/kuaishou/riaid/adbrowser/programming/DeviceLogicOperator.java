package com.kuaishou.riaid.adbrowser.programming;

import static com.kuaishou.riaid.proto.nano.RIAID.DEVICE_AXIS_TYPE_X;
import static com.kuaishou.riaid.proto.nano.RIAID.DEVICE_AXIS_TYPE_Y;
import static com.kuaishou.riaid.proto.nano.RIAID.DEVICE_AXIS_TYPE_Z;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.proto.nano.DeviceAxisConditionModel;
import com.kuaishou.riaid.proto.nano.DeviceAxisUnitModel;
import com.kuaishou.riaid.proto.nano.RIAID;

/**
 * 用于设备逻辑判断器
 */
public class DeviceLogicOperator {

  /**
   * @param values    目标值
   * @param condition 基准值和一些条件
   * @return 目标值和基准值在一定条件下是否满足
   */
  public static boolean groupMatch(@NonNull float[] values,
      @NonNull DeviceAxisConditionModel condition) {
    float value_x = values[0];
    float value_y = values[1];
    float value_z = values[2];
    int operator = condition.operator;
    // 如果是或逻辑，起始的isMatch应该是false，如果是与和非逻辑，起始的isMatch应该是true
    boolean isMatch = operator != RIAID.LOGIC_OPERATOR_OR;
    DeviceAxisUnitModel[] units = condition.units;
    for (DeviceAxisUnitModel unit : units) {
      if (unit.type == DEVICE_AXIS_TYPE_X) {
        isMatch = isMatch(isMatch, unit.threshold, value_x, operator, unit.compare);
      } else if (unit.type == DEVICE_AXIS_TYPE_Y) {
        isMatch = isMatch(isMatch, unit.threshold, value_y, operator, unit.compare);
      } else if (unit.type == DEVICE_AXIS_TYPE_Z) {
        isMatch = isMatch(isMatch, unit.threshold, value_z, operator, unit.compare);
      }
    }
    // 如果operator是非的话，需要取反
    if (operator == RIAID.LOGIC_OPERATOR_NOT) {
      isMatch = !isMatch;
    }
    return isMatch;
  }

  private static boolean isMatch(boolean isMatch, float threshold, float value, int operator,
      int compare) {
    switch (compare) {
      case RIAID.COMPARE_OPERATOR_EQUAL:
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= threshold == value;
        } else {
          isMatch |= threshold == value;
        }
        break;
      case RIAID.COMPARE_OPERATOR_NOT_EQUAL:
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= threshold != value;
        } else {
          isMatch |= threshold != value;
        }
        break;
      case RIAID.COMPARE_OPERATOR_LESS_THAN:
        // 小于
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= value < threshold;
        } else {
          isMatch |= value < threshold;
        }
        break;
      case RIAID.COMPARE_OPERATOR_GREATER_THAN:
        // 大于
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= value > threshold;
        } else {
          isMatch |= value > threshold;
        }
        break;
      case RIAID.COMPARE_OPERATOR_LESS_THAN_OR_EQUAL:
        // 小于等于
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= value <= threshold;
        } else {
          isMatch |= value <= threshold;
        }
        break;
      case RIAID.COMPARE_OPERATOR_GREATER_THAN_OR_EQUAL:
        // 大于等于
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= value >= threshold;
        } else {
          isMatch |= value >= threshold;
        }
        break;
    }
    return isMatch;
  }
}
