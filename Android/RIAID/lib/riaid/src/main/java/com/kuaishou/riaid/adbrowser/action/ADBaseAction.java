package com.kuaishou.riaid.adbrowser.action;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;

/**
 * 抽象的行为类，将共有的部分抽取出来，成员变量包含了行为数据模型的赋值和{@link ADBrowserContext}
 */
public abstract class ADBaseAction<T> implements ADAction {
  @NonNull
  protected final ADBrowserContext mBrowserContext;
  @NonNull
  protected final T mADActionModel;

  public ADBaseAction(
      @NonNull ADBrowserContext browserContext,
      @NonNull T adActionModel) {
    mBrowserContext = browserContext;
    mADActionModel = adActionModel;
  }
}
