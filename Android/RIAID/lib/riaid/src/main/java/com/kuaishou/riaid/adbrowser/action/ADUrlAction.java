package com.kuaishou.riaid.adbrowser.action;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.event.ADBrowserMetricsEventListener;
import com.kuaishou.riaid.proto.nano.ADUrlActionModel;

/**
 * Url行为，通过{@link ADBrowserMetricsEventListener}回调给上层，
 * 由上层处理
 */
public class ADUrlAction extends ADBaseAction<ADUrlActionModel> {

  public ADUrlAction(
      @NonNull ADBrowserContext browserContext,
      @NonNull ADUrlActionModel adActionModel) {
    super(browserContext, adActionModel);
  }

  @Override
  public boolean execute() {
    mBrowserContext.getADBrowserMetricsEventListener().onUrlEvent(mADActionModel);
    return true;
  }
}
