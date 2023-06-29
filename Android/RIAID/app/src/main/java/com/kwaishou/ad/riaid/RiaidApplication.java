package com.kwaishou.ad.riaid;

import android.app.Application;

import com.kuaishou.riaid.Riaid;

public class RiaidApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    Riaid.getInstance().init(this);
  }
}
