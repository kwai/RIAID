package com.kuaishou.riaid.adbrowser.programming;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.BasicVariable;
import com.kuaishou.riaid.proto.nano.BasicVariableValue;

/**
 * {@link BasicVariable}的装饰器，负责给其扩展能力
 */
public class BasicVariableDecorator {
  public BasicVariableDecorator(@NonNull BasicVariable basicVariable) {
    mVariable = basicVariable;
  }

  @NonNull
  private final BasicVariable mVariable;

  /**
   * 将变量中的值转换为字符串
   */
  @Nullable
  public String stringValue() {
    if (!ADVariableOperator.isValidVariable(mVariable)) {
      return null;
    }
    switch (mVariable.value.type) {
      case BasicVariableValue.STRING:
        return mVariable.value.s;
      case BasicVariableValue.BOOL:
        return String.valueOf(mVariable.value.b);
      case BasicVariableValue.INTEGER:
        return String.valueOf(mVariable.value.i);
      case BasicVariableValue.DOUBLE:
        return String.valueOf(mVariable.value.d);
    }
    return null;
  }
}
