package com.kuaishou.riaid.adbrowser.timer;


/**
 * 时间控制器的概念。在一次实例化后，只能使用一次，例如与{@link com.kuaishou.riaid.adbrowser.trigger.ADTimeoutTrigger},
 * 是一一绑定的。
 * 使用完成后，在 ADBrowser 中的时间控制器立刻销毁。同时，
 * 时间控制器支持``销毁``操作。可以通过{@link com.kuaishou.riaid.proto.nano.ADCancelTimerActionModel}
 * 来找到销毁一个时间控制器。
 * @author sunhongfa
 */
public interface TimeController {
  /**
   * 时间控制器支持``取消``操作
   */
  void cancel();
}
