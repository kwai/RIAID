package com.kuaishou.riaid.adbrowser.device_sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 传感器监听，不需要监听后一定要调用{@link #release()}
 */
public abstract class BaseSensorMonitor {

  @Nullable
  protected SensorManager mSensorManager;

  @Nullable
  protected SensorDeviceEventValueListener mValueListener;

  /**
   * 开始监听设备的运动传感器，这里即便是切到子线程执行也是起不到优化的效果，因为系统提供的回调会切换到主线程。
   *
   * @param context  需要通过context获取SensorManager
   * @param listener 监听的回调
   */
  public void startMonitor(@NonNull Context context,
      @Nullable SensorDeviceEventValueListener listener) {
    Log.i("startMonitor",
        "thread" + Thread.currentThread().getName());
    if (mSensorManager != null) {
      mSensorManager.unregisterListener(mSensorEventListener);
    }
    mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    mValueListener = listener;
    if (mSensorManager != null) {
      // 注册陀螺仪监听
      mSensorManager.registerListener(mSensorEventListener,
          mSensorManager.getDefaultSensor(getSensorType()),
          SensorManager.SENSOR_DELAY_NORMAL);
    }
  }

  /**
   * @return 设备监听的运动传感器类型
   */
  public abstract int getSensorType();

  /**
   * 如果是TYPE_LINEAR_ACCELERATION，单位：米/秒²
   * SensorEvent.values[0]	沿 x 轴的加速力（不包括重力）。
   * SensorEvent.values[1]	沿 y 轴的加速力（不包括重力）。
   * SensorEvent.values[2]	沿 z 轴的加速力（不包括重力）。
   * <p>
   * 如果是TYPE_GYROSCOPE，单位：弧度/秒
   * SensorEvent.values[0]	绕 x 轴的旋转速率。
   * SensorEvent.values[1]	绕 y 轴的旋转速率。
   * SensorEvent.values[2]	绕 z 轴的旋转速率。
   * <p>
   * 示例：调节扭一扭的灵敏度
   * if ((Math.abs(values[0]) > 3 || Math.abs(values[1]) > 3 ||
   * Math.abs(values[2]) > 3)) {
   * }
   * <p>
   * 示例：调节摇一摇的灵敏度
   * if ((Math.abs(values[0]) > 10 || Math.abs(values[1]) > 10 ||
   * Math.abs(values[2]) > 10)) {
   * }
   */
  protected final SensorEventListener mSensorEventListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      int sensorType = event.sensor.getType();
      float[] values = event.values;
      if (sensorType == getSensorType() && values != null) {
        if (mValueListener != null) {
          mValueListener.OnSensorDeviceEventValueListener(sensorType, values);
        }
      }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
  };

  public void release() {
    if (mSensorManager != null) {
      mSensorManager.unregisterListener(mSensorEventListener);
    }
    mValueListener = null;
  }
}
