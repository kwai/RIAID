package com.kuaishou.riaid.adbrowser.function;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;

public abstract class ADBaseAttributeFunction<T> implements ADFunction {
  @NonNull
  protected final ADBrowserContext mBrowserContext;
  @NonNull
  protected final T mFunctionModel;

  public ADBaseAttributeFunction(@NonNull ADBrowserContext browserContext,
      @NonNull T functionModel) {
    mBrowserContext = browserContext;
    mFunctionModel = functionModel;
  }
}
