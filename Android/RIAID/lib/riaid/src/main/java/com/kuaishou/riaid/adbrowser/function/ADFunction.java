package com.kuaishou.riaid.adbrowser.function;

import androidx.annotation.Nullable;

/**
 * RIAID的函数定义，函数的执行完成需要有个结果，这个结果统一是字符串。
 * 每个函数的定义都有一个唯一的key。
 * @author sunhongfa
 */
public interface ADFunction {
  /**
   * @return 唯一的key
   */
  int getKey();

  /**
   * @return 函数执行后的一个结果，如果没有结果，可以为空
   */
  @Nullable
  String execute();
}
