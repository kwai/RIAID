package com.kuaishou.riaid.adbrowser.trigger;


import com.kuaishou.riaid.proto.nano.ADTriggerModel;
import com.kuaishou.riaid.proto.nano.SystemKeyEnum;

/**
 * 触发器接口，可用于场景转场、场景内动画等，数据模型见
 * {@link ADTriggerModel}，通常是由
 * {@link ADTriggerModel}来对应一个
 * {@link ADTrigger}。
 * 在本系统中，也可以自定义的触发器，暂无。
 * 注意：触发器也可以触发触发器
 */
public interface ADTrigger {
  /**
   * 执行相关的{@link com.kuaishou.riaid.adbrowser.action.ADAction}，在满足触发条件时调用
   */
  boolean execute();

  /**
   * 用于释放相关资源，如：{@link com.kuaishou.riaid.adbrowser.action.ADTransitionAction}
   */
  default void cancel() {}

  /**
   * 唯一标识，规则：注意系统保留的key定义都是小于0的，而外部使用时定义的key需是大于零的。
   * 自定义的触发器，认为是系统的触发器，key需要是小于零的。
   *
   * @return 该触发器的key
   */
  default int getTriggerKey() {return SystemKeyEnum.INVALID_KEY;}
}