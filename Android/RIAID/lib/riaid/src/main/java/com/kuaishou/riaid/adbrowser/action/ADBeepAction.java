package com.kuaishou.riaid.adbrowser.action;

import android.text.TextUtils;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.service.AudioPlayService;
import com.kuaishou.riaid.adbrowser.service.RIAIDExecutorService;
import com.kuaishou.riaid.proto.nano.ADBeepActionModel;

/**
 * 播放提示音，直接播放即可。
 */
public class ADBeepAction extends ADBaseAction<ADBeepActionModel> {

  public ADBeepAction(
      @NonNull ADBrowserContext browserContext,
      @NonNull ADBeepActionModel adActionModel) {
    super(browserContext, adActionModel);
  }

  @Override
  public boolean execute() {
    if (TextUtils.isEmpty(mADActionModel.url)) {
      return false;
    }
    RIAIDExecutorService.getExecutor()
        .submit(() -> AudioPlayService.getInstance().playAudio(mADActionModel.url));
    return true;
  }

  @Override
  public void cancel() {
    RIAIDExecutorService.getExecutor().submit(() -> AudioPlayService.getInstance().release());
  }
}
