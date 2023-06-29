package com.kuaishou.riaid.adbrowser.logger;

import com.kuaishou.riaid.render.logger.RiaidLogger;

/**
 * 用于打印日志的类
 */
public class ADBrowserLogger {
  public static final String TAG = "ADBrowserLogger";

  public static void e(String message) {
    RiaidLogger.e(TAG, message);
  }

  public static void e(String message, Throwable tr) {
    RiaidLogger.e(TAG, message, tr);
  }

  public static void w(String message) {
    RiaidLogger.w(TAG, message);
  }

  public static void i(String message) {
    RiaidLogger.i(TAG, message);
  }
}