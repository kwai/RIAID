package com.kuaishou.riaid.adbrowser.service;

import android.text.TextUtils;
import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.kuaishou.riaid.adbrowser.event.ADBrowserMetricsEventListener;
import com.kuaishou.riaid.render.service.base.IRIAIDLogReportService;

public class RIAIDLogReportService implements IRIAIDLogReportService {
  @NonNull
  private final ADBrowserMetricsEventListener mEventListener;
  /**
   * 模板的名字
   */
  private final String mRiaidKey;

  public RIAIDLogReportService(@NonNull ADBrowserMetricsEventListener eventListener,
      String riaidKey) {
    mEventListener = eventListener;
    mRiaidKey = riaidKey;
  }

  @Override
  public void riaidLogEvent(@NonNull String eventName, @NonNull JsonObject eventParams) {
    if (!TextUtils.isEmpty(mRiaidKey)) {
      eventParams.addProperty("template_name", mRiaidKey);
    }
    mEventListener.onRIAIDLogEvent(eventName, eventParams);
  }
}
