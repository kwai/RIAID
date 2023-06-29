package com.kuaishou.riaid.adbrowser.action;

import android.app.Service;
import android.os.Vibrator;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.proto.nano.ADVibratorActionModel;

/**
 * 设备震动一下
 */
public class ADVibratorAction extends ADBaseAction<ADVibratorActionModel> {

  /**
   * 经验值，震动的时长。
   */
  private static final long VIBRATE_DURATION = 100;

  public ADVibratorAction(
      @NonNull ADBrowserContext browserContext,
      @NonNull ADVibratorActionModel adActionModel) {
    super(browserContext, adActionModel);
  }

  @Override
  public boolean execute() {
    try {
      Vibrator vibrator =
          (Vibrator) mBrowserContext.getContext().getSystemService(Service.VIBRATOR_SERVICE);
      if (vibrator != null && vibrator.hasVibrator()) {
        vibrator.vibrate(VIBRATE_DURATION);
      }
      return true;
    } catch (Exception e) {
      ADBrowserLogger.e("ADVibratorAction vibrate err", e);
      return false;
    }
  }

  @Override
  public void cancel() {
  }
}
