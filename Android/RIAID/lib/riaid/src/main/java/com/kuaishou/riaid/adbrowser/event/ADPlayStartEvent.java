package com.kuaishou.riaid.adbrowser.event;

/**
 * 视频播放开始的事件
 */
public class ADPlayStartEvent implements ADBrowserToggleEvent {
  @Override
  public int getEventType() {
    return ADBrowserEventType.VIDEO_START;
  }
}
