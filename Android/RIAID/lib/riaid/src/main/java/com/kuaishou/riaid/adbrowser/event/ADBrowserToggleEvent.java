package com.kuaishou.riaid.adbrowser.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.IntDef;

import com.kuaishou.riaid.adbrowser.ADBrowser;


/**
 * 输入通知到{@link ADBrowser}的一些事件，如：视频开始、视频结束
 * 这些事件会触发一些内置的key对应的{@link com.kuaishou.riaid.adbrowser.trigger.ADTrigger}
 * @author sunhongfa
 */
public interface ADBrowserToggleEvent {

  /**
   * @return 事件的类型，{@link ADBrowserEventType}
   */
  @ADBrowserEventType
  int getEventType();

  @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
  @IntDef({ADBrowserEventType.VIDEO_START, ADBrowserEventType.VIDEO_END})
  @Retention(RetentionPolicy.SOURCE)
  @interface ADBrowserEventType {
    /**
     * 视频播放开始
     */
    int VIDEO_START = 1;
    /**
     * 视频播放失败
     */
    int VIDEO_END = 2;
  }
}
