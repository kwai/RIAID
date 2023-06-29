package com.kuaishou.riaid.adbrowser.timer;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.trigger.ADTrigger;

/**
 * 定时控制器，通常与{@link com.kuaishou.riaid.adbrowser.trigger.ADTimeoutTrigger}绑定使用。
 */
public class TimeOutController implements TimeController {

  /**
   * 持有{@link Handler}是发了定时触发{@link ADTrigger}
   * 会在{@link #cancel()} ()}时移除其持有的{@link Runnable}
   */
  private final Handler mTimeoutHandler = new Handler(Looper.getMainLooper());

  /**
   * @param r           The Runnable that will be executed.
   * @param delayMillis The delay (in milliseconds) until the Runnable
   *                    will be executed.
   */
  public final void timeoutExecute(@NonNull Runnable r, long delayMillis) {
    mTimeoutHandler.postDelayed(r, delayMillis);
  }

  @Override
  public void cancel() {
    mTimeoutHandler.removeCallbacksAndMessages(null);
  }
}
