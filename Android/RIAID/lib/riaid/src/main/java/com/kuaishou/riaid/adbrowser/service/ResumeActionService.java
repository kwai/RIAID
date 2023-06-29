package com.kuaishou.riaid.adbrowser.service;


import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.adbridge.ADBridge;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.proto.nano.ADTriggerActionModel;
import com.kuaishou.riaid.render.logger.RiaidLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.service.base.IResumeActionService;

/**
 * 消费Render传过来的响应时间，如点击、双击、长按。
 */
public class ResumeActionService implements IResumeActionService {
  @NonNull
  private final ADBridge mADBridge;

  public ResumeActionService(@NonNull ADBridge ADBridge) {
    mADBridge = ADBridge;
  }

  @Override
  public void resumeRenderAction(int actionType, @NonNull UIModel.NodeContext context,
      @NonNull UIModel.Responder action) {
    ADBrowserLogger.i("ResumeActionService resumeRenderEvent schemes:" +
        RiaidLogger.objectToString(action.triggers));
    if (action.triggers == null) {
      return;
    }
    ADTriggerActionModel adTriggerActionModel = new ADTriggerActionModel();
    adTriggerActionModel.triggerKeys = action.triggers;
    mADBridge.handle(ADTriggerActionModel.class, adTriggerActionModel);
  }
}
