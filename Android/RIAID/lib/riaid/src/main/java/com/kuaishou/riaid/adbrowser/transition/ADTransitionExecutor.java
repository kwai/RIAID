package com.kuaishou.riaid.adbrowser.transition;

import com.kuaishou.riaid.adbrowser.trigger.ADTrigger;

/**
 * 场景Transition的执行器的抽象，一般在{@link ADTrigger}中创建并执行。
 * 对外暴露执行和释放接口
 * @author sunhongfa
 */
public interface ADTransitionExecutor {

  /**
   * 执行transition，通常是{@link ADTrigger}调用
   */
  void execute();

  /**
   * 取消transition，通常是{@link ADTrigger}在释放资源时调用
   */
  default void cancel() {
  }
}