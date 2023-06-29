package com.kuaishou.riaid.adbrowser.adbridge;


import androidx.annotation.NonNull;

/**
 * {@link ADBridgeHandler}的包装类，用于存其对应的{@link Class}，
 * 不使用泛型获取{@link java.lang.reflect.Type}，主要是为了防止调用方使用匿名内部类导致泛型擦除。
 *
 * @param <T> 要处理的对象类型
 */
public class ADBridgeHandlerWrap<T> {
  @NonNull
  public final Class<T> mTClass;
  public final ADBridgeHandler<T> mTADBridgeHandler;

  public ADBridgeHandlerWrap(@NonNull Class<T> tClass,
      ADBridgeHandler<T> tadBridgeHandler) {
    mTClass = tClass;
    mTADBridgeHandler = tadBridgeHandler;
  }
}
