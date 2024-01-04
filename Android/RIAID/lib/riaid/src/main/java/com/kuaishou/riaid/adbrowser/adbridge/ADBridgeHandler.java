package com.kuaishou.riaid.adbrowser.adbridge;

import androidx.annotation.NonNull;


/**
 * 注册到{@link ADBridge}中的handler，用于处理对应的行为。
 * @author sunhongfa
 */
public interface ADBridgeHandler<T> {

  /**
   * @return 能否处理该数据
   */
  boolean canHandle(@NonNull T object);

  /**
   * @param object 不能为空
   * @return 是否被正确的处理
   */
  boolean handle(@NonNull T object);
}
