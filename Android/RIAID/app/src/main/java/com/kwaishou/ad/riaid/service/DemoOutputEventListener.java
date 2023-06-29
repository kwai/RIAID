
package com.kwaishou.ad.riaid.service;


import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.event.ADBrowserMetricsEventListener;
import com.kuaishou.riaid.proto.nano.ADConversionActionModel;
import com.kuaishou.riaid.proto.nano.ADCustomActionModel;
import com.kuaishou.riaid.proto.nano.ADTrackActionModel;
import com.kuaishou.riaid.proto.nano.ADUrlActionModel;
import com.kuaishou.riaid.proto.nano.ADVideoActionModel;
import com.kuaishou.riaid.render.logger.RiaidLogger;

/**
 * 用来监听视频事件，通知执行相关视频行为
 */
public class DemoOutputEventListener implements ADBrowserMetricsEventListener {

  private final Context context;

  public DemoOutputEventListener(@NonNull Context context) {
    this.context = context;
  }

  @Override
  public void onVideoEvent(@NonNull ADVideoActionModel action) {
  }

  @Override
  public void onTrackEvent(@NonNull ADTrackActionModel action) {
    Log.i("DEMOonTrackEvent", RiaidLogger.objectToString(action.parameters));
  }

  @Override
  public void onUrlEvent(@NonNull ADUrlActionModel action) {
  }

  @Override
  public void onConversionEvent(@NonNull ADConversionActionModel action) {
  }


  @Override
  public void onCustomEvent(@NonNull ADCustomActionModel action) {
    Log.i("DemoOutputEventListener", RiaidLogger.objectToString(action.parameters));
  }
}
