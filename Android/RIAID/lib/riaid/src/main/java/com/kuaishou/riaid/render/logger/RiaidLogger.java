package com.kuaishou.riaid.render.logger;


import android.util.Log;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.util.GsonSerializer;

/**
 * <p>
 * attention attention
 * 级别>=i的都会写入到本地，注意一下
 */
public class RiaidLogger {

  public static void e(String TAG, String message) {
    Log.e(TAG, message);
  }

  public static void e(String TAG, String message, Throwable tr) {
    Log.e(TAG, message, tr);
  }

  public static void w(String TAG, String message) {
    Log.w(TAG, message);
  }

  public static void i(String TAG, String message) {
    Log.i(TAG, message);
  }

  public static void d(String TAG, String message) {
    Log.d(TAG, message);
  }

  public static void v(String TAG, String message) {
    Log.v(TAG, message);
  }

  public static String objectToString(@Nullable Object object) {
    if (object == null) {
      return "null";
    }
    return GsonSerializer.parseObject2JsonString(object, "");
  }
}