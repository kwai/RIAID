package com.kuaishou.riaid.adbrowser.action;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;

/**
 * 通过{@link com.kuaishou.riaid.adbrowser.adbridge.ADBridgeHandler}处理的行为
 * 仅仅是透传的能力
 *
 * @param <T> 要处理的数据模型
 */
public class ADExecuteHandlerAction<T> extends ADBaseAction<T> {
  @NonNull
  private final Class<T> mTClass;

  /**
   * @param browserContext 为了拿到{@link com.kuaishou.riaid.adbrowser.adbridge.ADBridge}去透传行为
   * @param tClass         透传行为需要该行为的类型
   * @param adActionModel  要透传的行为数据模型
   */
  public ADExecuteHandlerAction(
      @NonNull ADBrowserContext browserContext,
      @NonNull Class<T> tClass,
      @NonNull T adActionModel) {
    super(browserContext, adActionModel);
    mTClass = tClass;
  }

  @Override
  public boolean execute() {
    mBrowserContext.getADBridge().handle(mTClass, mADActionModel);
    return true;
  }
}
