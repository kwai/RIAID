package com.kuaishou.riaid.adbrowser.device_sensor;


import android.hardware.Sensor;

/**
 * 设备陀螺仪，检测旋转速率
 */
public class SensorGyroscopeMonitor extends BaseSensorMonitor {

  @Override
  public int getSensorType() {
    return Sensor.TYPE_GYROSCOPE;
  }
}
