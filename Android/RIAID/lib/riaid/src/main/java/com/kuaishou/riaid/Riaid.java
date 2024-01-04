package com.kuaishou.riaid;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.ADReadAttributeFunctionModel;

/**
 * 负责整个RIAID的初始化和配置工作
 * @author sunhongfa
 */
public class Riaid {
  private static final Riaid INSTANCE = new Riaid();
  @NonNull
  private Application mApplication;
  @Nullable
  private RiaidConfig mRiaidConfig;

  private Riaid() {}

  public static Riaid getInstance() {
    return INSTANCE;
  }

  /**
   * 整个RIAID的初始化
   */
  public void init(@NonNull Application application) {
    mApplication = application;
  }

  public void setConfig(@NonNull RiaidConfig.Builder builder) {
    mRiaidConfig = builder.build();
  }

  @NonNull
  public Context getApplication() {
    return mApplication;
  }

  public boolean isDebug() {
    if (mRiaidConfig == null) {
      return false;
    }
    return mRiaidConfig.mIsDebug;
  }

  public static class RiaidConfig {
    private boolean mIsDebug;

    public RiaidConfig() {

    }

    public static class Builder {
      private boolean mIsDebug;

      public Builder setDebug(boolean debug) {
        mIsDebug = debug;
        return this;
      }

      public RiaidConfig build() {
        RiaidConfig riaidConfig = new RiaidConfig();
        riaidConfig.mIsDebug = mIsDebug;
        return riaidConfig;
      }
    }
  }
}
