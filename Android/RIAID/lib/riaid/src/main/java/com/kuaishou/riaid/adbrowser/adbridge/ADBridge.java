package com.kuaishou.riaid.adbrowser.adbridge;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowser;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;

/**
 * AdBrowser的桥接类，AdBrowser的所有行为以及数据传输可以是任意对象，仅仅是内部使用。
 * 行为由{@link ADBridgeHandler}来解析处理。
 * 一个{@link ADBridgeHandler}会绑定一类的对象，这个{@link ADBridgeHandler}仅仅处理对应对象的行为。
 */
public class ADBridge {
  /**
   * 使用{@link CopyOnWriteArrayList}是为了防止{@link java.util.ConcurrentModificationException}
   */
  private final List<ADBridgeHandlerWrap<?>> mHandlerWraps = new CopyOnWriteArrayList<>();

  /**
   * 注册一个{@link ADBridgeHandlerWrap}，用于监听url对应的行为
   * 需要在{@link ADBrowser}初始化后注册
   *
   * @param adBridgeHandlerWrap 不能为空
   */
  @NonNull
  public <T> ADBridgeHandlerWrap<T> register(@NonNull ADBridgeHandlerWrap<T> adBridgeHandlerWrap) {
    if (!mHandlerWraps.contains(adBridgeHandlerWrap)) {
      mHandlerWraps.add(adBridgeHandlerWrap);
    }
    return adBridgeHandlerWrap;
  }

  /**
   * 注销一个{@link ADBridgeHandlerWrap}
   * 需要在{@link ADBrowser}离开或销毁时调用
   *
   * @param adBridgeHandlerWrap 不能为空
   */
  public void unregister(@Nullable ADBridgeHandlerWrap<?> adBridgeHandlerWrap) {
    if (adBridgeHandlerWrap != null) {
      mHandlerWraps.remove(adBridgeHandlerWrap);
    }
  }

  /**
   * 注销所有{@link ADBridgeHandlerWrap}
   * 需要在{@link ADBrowser}离开或销毁时调用
   */
  public void release() {
    mHandlerWraps.clear();
  }

  /**
   * 处理一个对象，遍历所有的{@link ADBridgeHandler}
   *
   * @param tClass 这个对象类型的{@link ADBridgeHandler}
   * @param object {@link ADBridgeHandler}
   * @param <T>    对象的类型
   * @return 是否处理对象成功
   */
  public <T> boolean handle(@NonNull Class<T> tClass, @Nullable T object) {
    if (object == null) {
      ADBrowserLogger.e("处理的ADBridgeHandlerWrap 为空");
      return false;
    }
    boolean handleResult = true;
    for (int i = 0; i < mHandlerWraps.size(); i++) {
      if (mHandlerWraps.get(i).mTClass == tClass) {
        @SuppressWarnings("unchecked")
        // 强转这个类型
        ADBridgeHandlerWrap<T> handlerWrap = (ADBridgeHandlerWrap<T>) mHandlerWraps.get(i);
        handlerWrap.mTADBridgeHandler.canHandle(object);
        handleResult &= handlerWrap.mTADBridgeHandler.handle(object);
      }
    }
    return handleResult;
  }
}