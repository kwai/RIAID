package com.kuaishou.riaid.render.logger;


/**
 * <p>
 * attention attention
 * 级别>=i的都会写入到本地，注意一下
 */
public class ADRenderLogger {
  public static final String TAG = "ADRenderLogger";

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

  public static void d(String message) {
    RiaidLogger.d(TAG, message);
  }

  public static void v(String message) {
    RiaidLogger.v(TAG, message);
  }
}