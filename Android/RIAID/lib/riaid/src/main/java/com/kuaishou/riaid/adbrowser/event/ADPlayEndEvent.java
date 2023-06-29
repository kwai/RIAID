package com.kuaishou.riaid.adbrowser.event;

/**
 * 视频播放结束的事件
 */
public class ADPlayEndEvent implements ADBrowserToggleEvent {
  @Override
  public int getEventType() {
    return ADBrowserEventType.VIDEO_END;
  }
}
