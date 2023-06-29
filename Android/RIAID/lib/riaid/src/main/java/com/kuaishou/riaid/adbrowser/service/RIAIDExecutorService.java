package com.kuaishou.riaid.adbrowser.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Looper;

/**
 * RIAID内部的线程池管理
 */
public class RIAIDExecutorService {
  private static final int CORE_SIZE = 4;
  private static final ExecutorService executorService = createFixedThreadPool(CORE_SIZE);

  // 普通线程池任务，耗时任务丢到这里
  public static ExecutorService getExecutor() {
    return executorService;
  }

  /**
   * 判断当前是不是主线程
   *
   * @return
   */
  public static boolean isMainThread() {
    return Looper.myLooper() == Looper.getMainLooper();
  }

  public static ExecutorService createFixedThreadPool(int nThreads) {
    try {
      ThreadPoolExecutor executor = new ThreadPoolExecutor(nThreads, nThreads,
          5L, TimeUnit.MINUTES,
          new LinkedBlockingQueue<Runnable>());
      executor.allowCoreThreadTimeOut(true);
      return executor;
    } catch (Throwable throwable) {
      return Executors.newFixedThreadPool(CORE_SIZE);
    }
  }
}
