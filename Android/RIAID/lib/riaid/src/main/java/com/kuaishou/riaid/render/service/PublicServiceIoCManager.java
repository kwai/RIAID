package com.kuaishou.riaid.render.service;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * 公共服务容器
 */
public class PublicServiceIoCManager {

  @NonNull
  private static final PublicServiceIoCManager mInstance = new PublicServiceIoCManager();

  @NonNull
  public static PublicServiceIoCManager getInstance() {
    return mInstance;
  }

  private final HashMap<Class<?>, ServiceWrap<?>> mServiceContainer = new HashMap<>();

  public <T> void registerService(@NonNull Class<T> clazz, @NonNull T service) {
    mServiceContainer.put(clazz, new ServiceWrap<>(service));
  }

  public <T> void unregisterService(@NonNull Class<T> clazz) {
    mServiceContainer.remove(clazz);
  }

  @Nullable
  public <T> T getService(@NonNull Class<T> clazz) {
    T service = null;
    if (mServiceContainer.containsKey(clazz)) {
      ServiceWrap<?> serviceWrap = mServiceContainer.get(clazz);
      if (serviceWrap == null || serviceWrap.getService() == null) {
        // 没有了就移除吧，没有必要存着啦
        mServiceContainer.remove(clazz);
        return null;
      }
      if (serviceWrap.getService().getClass() == clazz) {
        //noinspection unchecked
        service = (T) serviceWrap.getService();
      }
    }
    return service;
  }

  public void clear() {
    mServiceContainer.clear();
  }

  private static class ServiceWrap<S> {
    S service;

    public ServiceWrap(S service) {
      this.service = service;
    }

    public S getService() {
      return service;
    }
  }

}
