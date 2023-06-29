package com.kuaishou.riaid.adbrowser.action;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.event.ADBrowserMetricsEventListener;
import com.kuaishou.riaid.proto.nano.ADConversionActionModel;

/**
 * 转化行为，通过{@link ADBrowserMetricsEventListener}回调给上层，
 * 由上层处理
 */
public class ADConversionAction extends ADBaseAction<ADConversionActionModel> {

  public ADConversionAction(
      @NonNull ADBrowserContext browserContext,
      @NonNull ADConversionActionModel adActionModel) {
    super(browserContext, adActionModel);
  }

  @Override
  public boolean execute() {
    mBrowserContext.getADBrowserMetricsEventListener().onConversionEvent(mADActionModel);
    return true;
  }
}
