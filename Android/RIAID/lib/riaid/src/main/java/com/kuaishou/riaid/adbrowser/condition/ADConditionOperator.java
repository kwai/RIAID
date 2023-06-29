package com.kuaishou.riaid.adbrowser.condition;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.trigger.ADTrigger;
import com.kuaishou.riaid.proto.nano.ADConditionModel;
import com.kuaishou.riaid.proto.nano.ADLogicUnitModel;
import com.kuaishou.riaid.proto.nano.RIAID;

/**
 * 用处：点击视频结束场景的重播按钮这个事件触发的行为有两个，则会定义两个trigger，这两个trigger有统一的unionKey
 * 那从这两个触发器中选择哪一个，需要一个或多个条件来确定。
 * {@link ADTrigger}的条件选择器
 * 输入一个key，根据当前的条件，能返回一个对应的triggerKey
 * 类内部会维护一个所有的条件的表单，和一个当前的条件。
 * 主要是为{@link ADTrigger}服务的。
 * 例：比如由结束页场景点击重播回到强按钮场景的trigger，需要的条件是点击了卡片的关闭按钮
 */
public class ADConditionOperator {
  /**
   * 当前的条件，key是条件的名字，value是这个条件对应的值
   */
  @NonNull
  private final Map<String, String> mCurrentADConditions = new HashMap<>();

  /**
   * 构建，为其中的条件赋值
   *
   * @param defaultADConditions 默认的条件
   */
  public ADConditionOperator build(@Nullable ADConditionModel[] defaultADConditions) {
    buildDefaultCondition(defaultADConditions);
    return this;
  }

  /**
   * 构建默认的条件
   *
   * @param currentADConditions 原数据模型，构建一个Map利于查找
   */
  private void buildDefaultCondition(@Nullable ADConditionModel[] currentADConditions) {
    mCurrentADConditions.clear();
    if (currentADConditions == null) {
      return;
    }
    for (ADConditionModel currentADCondition : currentADConditions) {
      if (currentADCondition == null) {
        continue;
      }
      mCurrentADConditions
          .put(currentADCondition.conditionName, currentADCondition.conditionValue);
    }
  }

  /**
   * 改变条件对应的值，这些条件会对使用哪个{@link ADTrigger}有影响
   */
  public void alterCondition(@Nullable String conditionName, @Nullable String conditionValue) {
    if (!TextUtils.isEmpty(conditionName) && !TextUtils.isEmpty(conditionValue)) {
      mCurrentADConditions.put(conditionName, conditionValue);
    }
  }

  /**
   * 释放资源，将条件清空，防止状态残留
   */
  public void release() {
    mCurrentADConditions.clear();
  }

  /**
   * 条件匹配
   *
   * @param currentADConditions 作为基准的条件
   * @param operator            与或非
   * @param isMatch             前一个匹配判断
   * @param unit                要判断的逻辑单元
   * @return 是否匹配
   */
  public boolean isMatchSingleUnit(@NonNull Map<String, String> currentADConditions, int operator,
      boolean isMatch,
      @NonNull ADLogicUnitModel unit) {
    ADConditionModel condition = unit.condition;
    if (condition == null) {
      return false;
    }
    isMatch &= currentADConditions.containsKey(condition.conditionName);
    // #1 包含这个条件名
    // #2 条件值与当前的条件匹配
    // adTriggerCondition.adConditions中所有的条件都满足以上，则认为match
    switch (unit.compare) {
      case RIAID.COMPARE_OPERATOR_EQUAL:
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= isEqualsCondition(currentADConditions, condition);
        } else {
          isMatch |= isEqualsCondition(currentADConditions, condition);
        }
        break;
      case RIAID.COMPARE_OPERATOR_NOT_EQUAL:
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= !isEqualsCondition(currentADConditions, condition);
        } else {
          isMatch |= !isEqualsCondition(currentADConditions, condition);
        }
        break;
      case RIAID.COMPARE_OPERATOR_LESS_THAN:
        // 小于
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= isLessThanCondition(currentADConditions, condition, -1);
        } else {
          isMatch |= isLessThanCondition(currentADConditions, condition, -1);
        }
        break;
      case RIAID.COMPARE_OPERATOR_GREATER_THAN:
        // 大于
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= isGreaterThanCondition(currentADConditions, condition, 1);
        } else {
          isMatch |= isGreaterThanCondition(currentADConditions, condition, 1);
        }
        break;
      case RIAID.COMPARE_OPERATOR_LESS_THAN_OR_EQUAL:
        // 小于等于
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= isLessThanCondition(currentADConditions, condition, 0);
        } else {
          isMatch |= isLessThanCondition(currentADConditions, condition, 0);
        }
        break;
      case RIAID.COMPARE_OPERATOR_GREATER_THAN_OR_EQUAL:
        // 大于等于
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= isGreaterThanCondition(currentADConditions, condition, 0);
        } else {
          isMatch |= isGreaterThanCondition(currentADConditions, condition, 0);
        }
        break;
    }
    return isMatch;
  }

  /**
   * @param currentADConditions 基准条件
   * @param condition           判断条件
   * @param datum               判断基准 the value {@code 0} if the argument string is equal to
   *                            this string; a value less than {@code 0} if this string
   *                            is lexicographically less than the string argument; and a
   *                            value greater than {@code 0} if this string is
   *                            lexicographically greater than the string argument.   * @return
   *                            判断条件是否大于或大于等于基准条件
   */
  private boolean isGreaterThanCondition(Map<String, String> currentADConditions,
      ADConditionModel condition, int datum) {
    return condition.conditionValue.compareTo(
        currentADConditions.get(condition.conditionName)) >= datum;
  }

  /**
   * @param currentADConditions 基准条件
   * @param condition           判断条件
   * @param datum               判断基准 the value {@code 0} if the argument string is equal to
   *                            this string; a value less than {@code 0} if this string
   *                            is lexicographically less than the string argument; and a
   *                            value greater than {@code 0} if this string is
   *                            lexicographically greater than the string argument.
   * @return 判断条件是否小于或小于等于基准条件
   */
  private boolean isLessThanCondition(Map<String, String> currentADConditions,
      ADConditionModel condition, int datum) {
    return condition.conditionValue.compareTo(
        currentADConditions.get(condition.conditionName)) <= datum;
  }

  /**
   * @param currentADConditions 基准条件
   * @param condition           判断条件
   * @return 判断条件是否等于基准条件
   */
  private boolean isEqualsCondition(Map<String, String> currentADConditions,
      ADConditionModel condition) {
    return TextUtils.equals(condition.conditionValue,
        currentADConditions.get(condition.conditionName));
  }

  @NonNull
  public Map<String, String> getCurrentADConditions() {
    return mCurrentADConditions;
  }
}
