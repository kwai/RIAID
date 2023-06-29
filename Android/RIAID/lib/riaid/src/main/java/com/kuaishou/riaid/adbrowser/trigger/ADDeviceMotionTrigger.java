package com.kuaishou.riaid.adbrowser.trigger;

import static com.kuaishou.riaid.proto.nano.ADDeviceMotionTriggerModel.DEVICE_MOTION_TYPE_ROTATIONRATE;
import static com.kuaishou.riaid.proto.nano.ADDeviceMotionTriggerModel.DEVICE_MOTION_TYPE_USERACCELERATION;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.device_sensor.BaseSensorMonitor;
import com.kuaishou.riaid.adbrowser.device_sensor.SensorGyroscopeMonitor;
import com.kuaishou.riaid.adbrowser.device_sensor.SensorLinearAccelerationMonitor;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.programming.DeviceLogicOperator;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.adbrowser.transition.ADTransitionExecutor;
import com.kuaishou.riaid.proto.nano.ADDeviceMotionTriggerModel;

/**
 * 设备运动传感的触发器，一旦开始在本引擎的生命周期内一直监听，取消的话得在额外配置取消的行为。
 */
public class ADDeviceMotionTrigger extends ADBaseTrigger<ADDeviceMotionTriggerModel> {
  @NonNull
  private final ADDeviceMotionTriggerModel mDeviceMotionTrigger;

  @Nullable
  private BaseSensorMonitor mSensorMonitor;

  /**
   * @param context      主要是为了传递给
   *                     {@link ADTransitionExecutor}
   * @param adScenes     所有的场景，主要是为了找到相应的场景
   * @param triggerModel 触发器的数据模型
   */
  public ADDeviceMotionTrigger(
      @NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes,
      @NonNull ADDeviceMotionTriggerModel triggerModel) {
    super(context, adScenes, triggerModel);
    mDeviceMotionTrigger = triggerModel;
    switch (mDeviceMotionTrigger.motionType) {
      case DEVICE_MOTION_TYPE_USERACCELERATION:
        mSensorMonitor = new SensorLinearAccelerationMonitor();
        break;
      case DEVICE_MOTION_TYPE_ROTATIONRATE:
        mSensorMonitor = new SensorGyroscopeMonitor();
        break;
      default:
        ADBrowserLogger
            .w("不支持的设备类型 mDeviceMotionTrigger.motionType：" + mDeviceMotionTrigger.motionType);
        break;
    }
  }

  @Override
  public boolean execute() {
    if (mSensorMonitor == null) {
      ADBrowserLogger.w("execute mSensorMonitor为空");
      return false;
    }
    if (mDeviceMotionTrigger.condition == null) {
      ADBrowserLogger.w("execute condition为空");
      return false;
    }
    // 防止多次启动，先把上一次的监听取消掉
    mSensorMonitor.release();
    mSensorMonitor.startMonitor(mBrowserContext.getContext(),
        (sensorType, values) -> {
          if (values.length < 3) {
            ADBrowserLogger.w("startMonitor 返回的值长度小于3");
            return;
          }
          boolean isMatch = DeviceLogicOperator.groupMatch(values, mDeviceMotionTrigger.condition);
          if (isMatch) {
            executeActions();
          }
        });
    return true;
  }

  @Override
  public void cancel() {
    if (mSensorMonitor != null) {
      mSensorMonitor.release();
      ADBrowserLogger.i("mSensorMonitor 设备监听已取消, 触发器的key: "+getTriggerKey());

    }
  }
}
