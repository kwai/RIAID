package com.kuaishou.riaid.render.constants;

/**
 * 这个是Browser下发的给Render的事件类型
 */
public class DispatchEventType {

  /**
   * 暂停视频播放
   */
  public static final String VIDEO_PLAY = "VIDEO_PLAY";

  /**
   * 让视频播放（当前视频可能是一开始的start也可能是pause之后的恢复播放resume，但是Browser不关心，它关心的
   * 就是让视频播放，bro，不管不管人家就要)
   */
  public static final String VIDEO_PAUSE = "VIDEO_PAUSE";

  /**
   * 关闭声音
   */
  public static final String VIDEO_SOUND_TURN_ON = "VIDEO_SOUND_TURN_ON";

  /**
   * 开启声音
   */
  public static final String VIDEO_SOUND_TURN_OFF = "VIDEO_SOUND_TURN_OFF";

  /**
   * 视频播放位置到零
   */
  public static final String VIDEO_RESET = "VIDEO_RESET";

  /**
   * 视频从头开始播放
   */
  public static final String VIDEO_REPLAY = "VIDEO_REPLAY";

  /**
   * 刷新所有的属性
   */
  public static final String REFRESH_ALL_ATTRIBUTES = "REFRESH_ALL_ATTRIBUTES";

  /**
   * Lottie动画播放
   */
  public static final String LOTTIE_PLAY = "LOTTIE_PLAY";
  /**
   * Lottie动画重新播放
   */
  public static final String LOTTIE_REPLAY = "LOTTIE_REPLAY";
  /**
   * Lottie动画回到首帧
   */
  public static final String LOTTIE_RESET = "LOTTIE_RESET";
  /**
   * Lottie动画暂停
   */
  public static final String LOTTIE_PAUSE = "LOTTIE_PAUSE";

}
