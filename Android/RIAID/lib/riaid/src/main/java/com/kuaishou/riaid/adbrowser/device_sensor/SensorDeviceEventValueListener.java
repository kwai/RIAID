package com.kuaishou.riaid.adbrowser.device_sensor;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

/**
 * 设备事件返回值的监听。
 */
public interface SensorDeviceEventValueListener {
  /**
   * 系统回调会切换到主线程
   *
   * @param sensorType generic type of this sensor.
   * @param values     参考{@link android.hardware.SensorEvent#values}
   */
  @MainThread
  void OnSensorDeviceEventValueListener(int sensorType, @NonNull float[] values);
}
