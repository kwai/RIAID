package com.kuaishou.riaid.render.interf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 这个是注册以及获取service的接口
 * service是render需要依赖的外界能力，比如加载图片，数据绑定解析占位符等
 */
public interface IServiceContainer {

  /**
   * 注册service，提供能力
   *
   * @param clazz   目标service的接口的class
   * @param service 目标service的具体实例
   * @param <T>     这个是接口的泛型，让接口是实例实现约束，不许是同一个接口类型
   */
  <T> void registerService(@NonNull Class<T> clazz, @NonNull T service);

  /**
   * 注销指定类型的service的实例
   *
   * @param clazz service实例的在map对应的key，也是实现的接口的class
   * @param <T>   实现的接口的class
   */
  <T> void unregisterService(@NonNull Class<T> clazz);

  /**
   * 通过接口的class，在map中获取service
   *
   * @param clazz service实现的接口的class
   * @param <T>   service实现的接口的类型
   * @return 返回获取到的接口的实例，如果存在的话
   */
  @Nullable
  <T> T getService(@NonNull Class<T> clazz);

  /**
   * 释放所有的service
   */
  void clear();

}
