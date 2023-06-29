package com.kuaishou.riaid.adbrowser.device_sensor;


import android.hardware.Sensor;

/**
 * 检测设备加速度，不包含重力
 */
public class SensorLinearAccelerationMonitor extends BaseSensorMonitor {

  @Override
  public int getSensorType() {
    return Sensor.TYPE_LINEAR_ACCELERATION;
  }
}
