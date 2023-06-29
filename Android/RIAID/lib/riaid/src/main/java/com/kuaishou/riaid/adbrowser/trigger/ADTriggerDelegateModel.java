package com.kuaishou.riaid.adbrowser.trigger;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.ADActionModel;
import com.kuaishou.riaid.proto.nano.ADConditionTriggerModel;
import com.kuaishou.riaid.proto.nano.ADDeviceMotionTriggerModel;
import com.kuaishou.riaid.proto.nano.ADGeneralTriggerModel;
import com.kuaishou.riaid.proto.nano.ADHeartBeatTriggerModel;
import com.kuaishou.riaid.proto.nano.ADTimeoutTriggerModel;
import com.kuaishou.riaid.proto.nano.ADVideoDurationTimeoutTriggerModel;


/**
 * 触发器代理模型，用于拿到其公共参数
 */
public class ADTriggerDelegateModel {

  @NonNull
  public final Object mBaseTriggerModel;

  public ADTriggerDelegateModel(@NonNull Object baseTriggerModel) {
    mBaseTriggerModel = baseTriggerModel;
  }

  @Nullable
  public ADActionModel[] getActions() {
    if ((mBaseTriggerModel instanceof ADGeneralTriggerModel)) {
      ADGeneralTriggerModel triggerModel = (ADGeneralTriggerModel) mBaseTriggerModel;
      return triggerModel.actions;
    } else if ((mBaseTriggerModel instanceof ADTimeoutTriggerModel)) {
      ADTimeoutTriggerModel triggerModel = (ADTimeoutTriggerModel) mBaseTriggerModel;
      return triggerModel.actions;
    } else if ((mBaseTriggerModel instanceof ADHeartBeatTriggerModel)) {
      ADHeartBeatTriggerModel triggerModel = (ADHeartBeatTriggerModel) mBaseTriggerModel;
      return triggerModel.actions;
    } else if ((mBaseTriggerModel instanceof ADConditionTriggerModel)) {
      return null;
    } else if ((mBaseTriggerModel instanceof ADDeviceMotionTriggerModel)) {
      return ((ADDeviceMotionTriggerModel) mBaseTriggerModel).actions;
    } else if ((mBaseTriggerModel instanceof ADVideoDurationTimeoutTriggerModel)) {
      return ((ADVideoDurationTimeoutTriggerModel) mBaseTriggerModel).actions;
    }
    return null;
  }

  public int getKey() {
    if ((mBaseTriggerModel instanceof ADGeneralTriggerModel)) {
      ADGeneralTriggerModel triggerModel = (ADGeneralTriggerModel) mBaseTriggerModel;
      return triggerModel.key;
    } else if ((mBaseTriggerModel instanceof ADTimeoutTriggerModel)) {
      ADTimeoutTriggerModel triggerModel = (ADTimeoutTriggerModel) mBaseTriggerModel;
      return triggerModel.key;
    } else if ((mBaseTriggerModel instanceof ADHeartBeatTriggerModel)) {
      ADHeartBeatTriggerModel triggerModel = (ADHeartBeatTriggerModel) mBaseTriggerModel;
      return triggerModel.key;
    } else if ((mBaseTriggerModel instanceof ADConditionTriggerModel)) {
      ADConditionTriggerModel triggerModel = (ADConditionTriggerModel) mBaseTriggerModel;
      return triggerModel.key;
    } else if ((mBaseTriggerModel instanceof ADDeviceMotionTriggerModel)) {
      ADDeviceMotionTriggerModel triggerModel = (ADDeviceMotionTriggerModel) mBaseTriggerModel;
      return triggerModel.key;
    }
    return 0;
  }
}
