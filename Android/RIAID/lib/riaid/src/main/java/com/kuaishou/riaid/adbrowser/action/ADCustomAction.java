package com.kuaishou.riaid.adbrowser.action;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.proto.nano.ADCustomActionModel;

public class ADCustomAction extends ADBaseAction<ADCustomActionModel> {

  public ADCustomAction(
      @NonNull ADBrowserContext browserContext,
      @NonNull ADCustomActionModel adActionModel) {
    super(browserContext, adActionModel);
  }

  @Override
  public boolean execute() {
    if (mADActionModel.parameters == null) {
      return false;
    }
    mBrowserContext.getADBrowserMetricsEventListener().onCustomEvent(mADActionModel);
    return true;
  }
}
