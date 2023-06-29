package com.kuaishou.riaid.adbrowser.timer;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;

/**
 * 运行在主线程，每隔{@link #mInterval}ms去执行，并回调{@link #mOnIntervalListener}，直到执行了{@link #cancel()}为止。
 * 参考系统类{@link android.os.CountDownTimer}的代码而实现的。
 * 注意，使用完需要执行{@link #cancel()}，不然可能会有内存泄漏。
 * 对象创建后，{@link #cancel()}后可以再执行{@link #start}复用。
 */
public class IntervalTimeController implements TimeController {
  private static final String TAG = "IntervalTimeController";
  /**
   * The interval in millis that the user receives callbacks
   */
  private long mInterval;

  /**
   * boolean representing if the timer was cancelled
   */
  private boolean mCancelled = false;
  @Nullable
  private OnIntervalListener mOnIntervalListener;
  // interval开始的时间
  private long mStartTime;
  // 间隔的次数
  private long mIntervalCount;

  // 最大的间隔次数
  private int mMaxIntervalCount;

  public IntervalTimeController() {
  }

  /**
   * Cancel the interval.
   */
  @Override
  public synchronized void cancel() {
    mCancelled = true;
    mHandler.removeMessages(MSG);
  }

  /**
   * @param interval       时间间隔，单位ms
   * @param onTickListener 每次间隔回调监听
   * @return {@link IntervalTimeController}
   */
  public synchronized final IntervalTimeController start(long interval,
      @NonNull OnIntervalListener onTickListener) {
    return start(interval, 0, onTickListener);
  }

  public synchronized final IntervalTimeController start(long interval, int maxCount,
      @NonNull OnIntervalListener onTickListener) {
    mIntervalCount = 0;
    mInterval = interval;
    mMaxIntervalCount = maxCount;
    mCancelled = false;
    mOnIntervalListener = onTickListener;
    mStartTime = SystemClock.elapsedRealtime();

    if (mInterval > 0) {
      mHandler.sendMessage(mHandler.obtainMessage(MSG));
    }
    return this;
  }

  private static final int MSG = 1;


  /**
   * 使用完需要执行{@link #cancel()}，不然可能有内存泄漏
   */
  @SuppressLint("HandlerLeak")
  private final Handler mHandler = new Handler(Looper.getMainLooper()) {

    @Override
    public void handleMessage(@NonNull Message msg) {

      synchronized (IntervalTimeController.this) {
        if (mCancelled) {
          ADBrowserLogger.i(TAG + " 本时间控制器已被取消，handler不需要往下执行interval逻辑");
          return;
        }

        final long millisLeft = SystemClock.elapsedRealtime();

        long lastTickStart = SystemClock.elapsedRealtime();

        mOnIntervalListener.onInterval(mIntervalCount, mInterval, millisLeft - mStartTime);

        // take into account user's onTick taking time to execute
        long lastTickDuration = SystemClock.elapsedRealtime() - lastTickStart;
        long delay;

        if (millisLeft < mInterval) {
          // just delay until done
          delay = millisLeft - lastTickDuration;

          if (delay < 0) {
            delay = 0;
          }
        } else {
          mIntervalCount++;
          delay = mInterval - lastTickDuration;
          // complete, skip to next interval
          while (delay < 0) {
            delay += mInterval;
          }
        }
        // 如果即将超过最大间隔次数，则不在发送
        if (mMaxIntervalCount != 0 && mIntervalCount > mMaxIntervalCount) {
          cancel();
        } else {
          sendMessageDelayed(obtainMessage(MSG), delay);
        }
      }
    }
  };

  /**
   * 与@link IntervalTimeController}结合使用，用于每次间隔执行的回调。
   */
  public interface OnIntervalListener {

    /**
     * Callback fired on regular interval.
     *
     * @param count    间隔执行了几次，从0开始
     * @param interval 时间间隔，单位ms
     * @param millis   The amount of time until canceled.实际时间允许有误差。
     */
    void onInterval(long count, long interval, long millis);
  }
}


