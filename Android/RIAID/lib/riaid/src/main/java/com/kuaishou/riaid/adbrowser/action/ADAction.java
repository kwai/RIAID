package com.kuaishou.riaid.adbrowser.action;

/**
 * RIAID里所有的行为接口，可以是执行转场动画，也可以是发送一个埋点，
 * 通常由{@link com.kuaishou.riaid.adbrowser.trigger.ADTrigger}触发。
 * @author sunhongfa
 */
public interface ADAction {
  /**
   * 执行相关的行为，如：执行转场动画
   *
   * @return 执行是否成功
   */
  boolean execute();

  /**
   * 行为可以取消掉，如：取消一个动画行为。
   */
  default void cancel() {}
}
