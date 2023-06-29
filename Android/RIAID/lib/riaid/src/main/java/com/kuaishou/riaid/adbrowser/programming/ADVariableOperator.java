package com.kuaishou.riaid.adbrowser.programming;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.helper.ADBrowserKeyHelper;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.trigger.ADTrigger;
import com.kuaishou.riaid.proto.nano.ADLogicUnitModel;
import com.kuaishou.riaid.proto.nano.BasicVariable;
import com.kuaishou.riaid.proto.nano.BasicVariableValue;
import com.kuaishou.riaid.proto.nano.RIAID;
import com.kuaishou.riaid.render.logger.RiaidLogger;

/**
 * 负责riaid中的全局变量的管理，变量可用于在用户操作过程中，其中的一些值随之发生了变化。
 * 例：倒计时，实时改变一个变量从10到1。
 */
public class ADVariableOperator {
  /**
   * 当前所有的变量，key是变量的地址，value是这个变量
   */
  @NonNull
  private final Map<Integer, BasicVariable> mCurrentADVariables = new HashMap<>();

  /**
   * 构建变量管理类，将所有的变量都存储
   *
   * @param defaultADVariables 默认的变量
   */
  public ADVariableOperator build(@Nullable BasicVariable[] defaultADVariables) {
    buildDefaultCondition(defaultADVariables);
    return this;
  }

  /**
   * 构建默认的条件
   *
   * @param currentADVariables 原数据模型，构建一个Map利于查找
   */
  private void buildDefaultCondition(@Nullable BasicVariable[] currentADVariables) {
    mCurrentADVariables.clear();
    if (currentADVariables == null) {
      return;
    }
    for (BasicVariable current : currentADVariables) {
      if (!isValidVariable(current)) {
        continue;
      }
      putCopyValue(current);
    }
  }

  /**
   * 改变条件对应的值，这些条件会对使用哪个{@link ADTrigger}有影响
   */
  public void alterCondition(@Nullable BasicVariable variable) {
    if (isValidVariable(variable)) {
      putCopyValue(variable);
    }
  }

  /**
   * 为了防止变量在某些操作改变其原来的值，需要将其复制一份，再put到Map中
   *
   * @param variable 需要塞进Map的变量
   */
  private void putCopyValue(@Nullable BasicVariable variable) {
    @Nullable
    BasicVariable variableCopy = null;
    if (isValidVariable(variable)) {
      switch (variable.value.type) {
        case BasicVariableValue.STRING:
          variableCopy = createStringVariable(variable.key, variable.value.s);
          break;
        case BasicVariableValue.INTEGER:
          variableCopy = createLongVariable(variable.key, variable.value.i);
          break;
        case BasicVariableValue.BOOL:
          variableCopy = createBoolVariable(variable.key, variable.value.b);
          break;
        case BasicVariableValue.DOUBLE:
          variableCopy = createDoubleVariable(variable.key, variable.value.d);
          break;
        default:
          ADBrowserLogger.w("putCopyValue 不支持的变量类型：" + variable.value.type);
          break;
      }
    }
    if (variableCopy != null) {
      mCurrentADVariables.put(variableCopy.key, variableCopy);
    }
  }

  /**
   * 创建一个字符串类型的变量
   *
   * @param key   变量的地址，要求大于零，如果等于零，即便是创建了也是非法的
   * @param value 变量的值
   * @return 一个不为空的变量
   */
  @NonNull
  public BasicVariable createStringVariable(int key, String value) {
    BasicVariable basicVariable = new BasicVariable();
    basicVariable.key = key;
    basicVariable.value = new BasicVariableValue.Value();
    basicVariable.value.type = BasicVariableValue.STRING;
    basicVariable.value.s = value;
    return basicVariable;
  }

  /**
   * 创建一个长整形类型的变量
   *
   * @param key   变量的地址，要求大于零，如果等于零，即便是创建了也是非法的
   * @param value 变量的值
   * @return 一个不为空的变量
   */
  public BasicVariable createLongVariable(int key, long value) {
    BasicVariable basicVariable = new BasicVariable();
    basicVariable.key = key;
    basicVariable.value = new BasicVariableValue.Value();
    basicVariable.value.type = BasicVariableValue.INTEGER;
    basicVariable.value.i = value;
    return basicVariable;
  }

  /**
   * 创建一个Double类型的变量
   *
   * @param key   变量的地址，要求大于零，如果等于零，即便是创建了也是非法的
   * @param value 变量的值
   * @return 一个不为空的变量
   */
  public BasicVariable createDoubleVariable(int key, double value) {
    BasicVariable basicVariable = new BasicVariable();
    basicVariable.key = key;
    basicVariable.value = new BasicVariableValue.Value();
    basicVariable.value.type = BasicVariableValue.DOUBLE;
    basicVariable.value.d = value;
    return basicVariable;
  }

  /**
   * 创建一个布尔类型的变量
   *
   * @param key   变量的地址，要求大于零，如果等于零，即便是创建了也是非法的
   * @param value 变量的值
   * @return 一个不为空的变量
   */
  public BasicVariable createBoolVariable(int key, boolean value) {
    BasicVariable basicVariable = new BasicVariable();
    basicVariable.key = key;
    basicVariable.value = new BasicVariableValue.Value();
    basicVariable.value.type = BasicVariableValue.BOOL;
    basicVariable.value.b = value;
    return basicVariable;
  }

  /**
   * 条件匹配
   *
   * @param baseVariables 作为基准的条件
   * @param operator      与或非
   * @param isMatch       前一个匹配判断
   * @param unit          要判断的逻辑单元
   * @return 是否匹配
   */
  public boolean isMatchSingleUnit(@NonNull Map<Integer, BasicVariable> baseVariables, int operator,
      boolean isMatch, @NonNull ADLogicUnitModel unit) {
    BasicVariable variable = unit.variable;
    if (!isValidVariable(variable)) {
      return false;
    }
    if (!baseVariables.containsKey(variable.key)) {
      return false;
    }
    BasicVariable baseVariable = baseVariables.get(variable.key);
    isMatch &= baseVariables.containsKey(variable.key);
    // #1 包含这个变量地址
    // #2 变量值与当前的变量匹配
    // adTriggerCondition.adConditions中所有的条件都满足以上，则认为match
    switch (unit.compare) {
      case RIAID.COMPARE_OPERATOR_EQUAL:
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= isEquals(baseVariable, variable);
        } else {
          isMatch |= isEquals(baseVariable, variable);
        }
        break;
      case RIAID.COMPARE_OPERATOR_NOT_EQUAL:
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= !isEquals(baseVariable, variable);
        } else {
          isMatch |= !isEquals(baseVariable, variable);
        }
        break;
      case RIAID.COMPARE_OPERATOR_LESS_THAN:
        // 小于
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= isLessThan(baseVariable, variable, -1);
        } else {
          isMatch |= isLessThan(baseVariable, variable, -1);
        }
        break;
      case RIAID.COMPARE_OPERATOR_GREATER_THAN:
        // 大于
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= isGreaterThan(baseVariable, variable, 1);
        } else {
          isMatch |= isGreaterThan(baseVariable, variable, 1);
        }
        break;
      case RIAID.COMPARE_OPERATOR_LESS_THAN_OR_EQUAL:
        // 小于等于
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= isLessThan(baseVariable, variable, 0);
        } else {
          isMatch |= isLessThan(baseVariable, variable, 0);
        }
        break;
      case RIAID.COMPARE_OPERATOR_GREATER_THAN_OR_EQUAL:
        // 大于等于
        if (operator == RIAID.LOGIC_OPERATOR_AND || operator == RIAID.LOGIC_OPERATOR_NOT) {
          isMatch &= isGreaterThan(baseVariable, variable, 0);
        } else {
          isMatch |= isGreaterThan(baseVariable, variable, 0);
        }
        break;
    }
    return isMatch;
  }

  /**
   * @param baseVariable 基准变量
   * @param variable     判断变量
   * @param datum        判断基准 如果值是{@code 0}，那么是大于等于，如果值是{@code 1}，那么是大于
   */
  private boolean isGreaterThan(@NonNull BasicVariable baseVariable,
      @NonNull BasicVariable variable, int datum) {
    // 想拿到基准条件组中对应的变量
    if (baseVariable.value == null) {
      return false;
    }
    switch (variable.value.type) {
      case BasicVariableValue.STRING:
        return variable.value.s != null &&
            baseVariable.value.s != null &&
            (variable.value.s.compareTo(baseVariable.value.s) >= datum);
      case BasicVariableValue.BOOL:
        ADBrowserLogger.e("bool 类型无法做大于或大于等于的比较，variable: "
            + RiaidLogger.objectToString(variable) + " datum:" + datum);
        return false;
      case BasicVariableValue.INTEGER:
        return variable.value.i - baseVariable.value.i >= datum;
      case BasicVariableValue.DOUBLE:
        return variable.value.d - baseVariable.value.d >= datum;
    }
    return false;
  }

  /**
   * @param baseVariable 基准变量
   * @param variable     判断变量
   * @param datum        判断基准 如果值是{@code 0}，那么是小于等于，如果值是{@code -1}，那么是小于
   */
  private boolean isLessThan(@NonNull BasicVariable baseVariable,
      @NonNull BasicVariable variable, int datum) {
    // 想拿到基准条件组中对应的变量
    if (baseVariable.value == null) {
      return false;
    }
    switch (variable.value.type) {
      case BasicVariableValue.STRING:
        return variable.value.s != null &&
            baseVariable.value.s != null &&
            (variable.value.s.compareTo(baseVariable.value.s) <= datum);
      case BasicVariableValue.BOOL:
        ADBrowserLogger.e("bool 类型无法做小于或小于等于的比较，variable: "
            + RiaidLogger.objectToString(variable) + " datum:" + datum);
        return false;
      case BasicVariableValue.INTEGER:
        return variable.value.i - baseVariable.value.i <= datum;
      case BasicVariableValue.DOUBLE:
        return variable.value.d - baseVariable.value.d <= datum;
    }
    return false;
  }

  /**
   * @param baseVariable 基准变量
   * @param variable     判断变量
   */
  private boolean isEquals(@NonNull BasicVariable baseVariable,
      @NonNull BasicVariable variable) {
    // 想拿到基准条件组中对应的变量
    if (baseVariable.value == null) {
      return false;
    }
    switch (variable.value.type) {
      case BasicVariableValue.STRING:
        return TextUtils.equals(variable.value.s, baseVariable.value.s);
      case BasicVariableValue.BOOL:
        return variable.value.b == baseVariable.value.b;
      case BasicVariableValue.INTEGER:
        return variable.value.i == baseVariable.value.i;
      case BasicVariableValue.DOUBLE:
        return variable.value.d == baseVariable.value.d;
    }
    return false;
  }

  @NonNull
  public Map<Integer, BasicVariable> getCurrentADVariables() {
    return mCurrentADVariables;
  }

  /**
   * 从当前的变量池子中，找到一个变量
   *
   * @param key 要寻找的变量的地址
   * @return 地址对应的变量，结果可能为空
   */
  @Nullable
  public BasicVariable findVariableByKey(int key) {
    if (!mCurrentADVariables.containsKey(key)) {
      return null;
    }
    return mCurrentADVariables.get(key);
  }

  /**
   * 变量不为空且值不为空且地址合法才是一个合法的变量
   *
   * @param variable 要被校验的变量，可以为空
   * @return 输入的变量是否合法
   */
  public static boolean isValidVariable(@Nullable BasicVariable variable) {
    return variable != null && variable.value != null &&
        ADBrowserKeyHelper.isValidKey(variable.key);
  }

  /**
   * 释放资源，将变量清空，防止状态残留
   */
  public void release() {
    mCurrentADVariables.clear();
  }

}
