package com.kuaishou.riaid.render.service;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.interf.IServiceContainer;

/**
 * 这个是存和获取service的抽象包装类
 * 提供容器的注册和注销，以及获取的默认实现,通过弱引用持有，放置泄露
 */
public class ServiceContainer implements IServiceContainer {

  private final HashMap<Class<?>, SoftReference<?>> mServiceContainer = new HashMap<>();

  public <T> void registerService(@NonNull Class<T> clazz, @NonNull T service) {
    mServiceContainer.put(clazz, new SoftReference<>(service));
  }

  @Override
  public <T> void unregisterService(@NonNull Class<T> clazz) {
    mServiceContainer.remove(clazz);
  }

  @Nullable
  public <T> T getService(@NonNull Class<T> clazz) {
    T service = null;
    if (mServiceContainer.containsKey(clazz)) {
      SoftReference<?> serviceReference = mServiceContainer.get(clazz);
      if (serviceReference == null || (service = (T) serviceReference.get()) == null) {
        // 没有了就移除吧，没有必要存着啦
        mServiceContainer.remove(clazz);
      }
    }
    return service;
  }

  @Override
  public void clear() {
    mServiceContainer.clear();
  }

}
