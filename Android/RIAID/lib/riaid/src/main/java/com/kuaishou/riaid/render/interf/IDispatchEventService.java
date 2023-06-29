package com.kuaishou.riaid.render.interf;

/**
 * 这个是事件分发的通用接口
 */
public interface IDispatchEventService {

  /**
   * 分发指定类型的事件
   *
   * @param eventType 事件类型
   */
  void dispatchEvent(int eventType);

}
