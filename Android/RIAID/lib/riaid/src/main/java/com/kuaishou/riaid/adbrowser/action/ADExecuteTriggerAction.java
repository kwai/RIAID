package com.kuaishou.riaid.adbrowser.action;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.proto.nano.ADTriggerActionModel;

/**
 * 通过{@link com.kuaishou.riaid.adbrowser.adbridge.ADBridgeHandler}处理的行为
 * 注意：如果要取消这个行为，要将其指向的触发器也要取消掉
 */
public class ADExecuteTriggerAction extends ADBaseAction<ADTriggerActionModel> {

  /**
   * @param browserContext 为了拿到{@link com.kuaishou.riaid.adbrowser.adbridge.ADBridge}去透传行为
   * @param adActionModel  要透传的行为数据模型
   */
  public ADExecuteTriggerAction(
      @NonNull ADBrowserContext browserContext,
      @NonNull ADTriggerActionModel adActionModel) {
    super(browserContext, adActionModel);
  }

  @Override
  public boolean execute() {
    mBrowserContext.getADBridge().handle(ADTriggerActionModel.class, mADActionModel);
    return true;
  }
}