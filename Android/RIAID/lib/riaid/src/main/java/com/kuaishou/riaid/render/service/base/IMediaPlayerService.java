package com.kuaishou.riaid.render.service.base;

import android.view.Surface;
import androidx.annotation.NonNull;

/**
 * 把视频控件的具体实现抛到外界
 * 参考android media标准接口定义
 * <p>
 * 这里支持单个播放器，没有支持多播放器，所以如果以后要支持多个，需要创建不同的实例，需要特别关注
 * {@link com.kuaishou.riaid.render.node.item.VideoItemNode}
 */
public interface IMediaPlayerService {

  /**
   * 异步准备
   */
  void prepareAsync() throws IllegalStateException;

  /**
   * 开始播放
   */
  void start() throws IllegalStateException;

  /**
   * 停止视频
   */
  void stop() throws IllegalStateException;

  /**
   * 暂停视频
   */
  void pause() throws IllegalStateException;

  /**
   * 指定视频播放位置
   */
  void seekTo(long msec) throws IllegalStateException;

  /**
   * 是否正在播放视频
   */
  boolean isPlaying() throws IllegalStateException;

  /**
   * 当前视频播放位置
   */
  long getCurrentPosition();

  /**
   * 视频文件的总时长
   */
  long getDuration();

  /**
   * 释放视频资源
   */
  void release();

  /**
   * 重置视频
   */
  void reset();

  /**
   * 设置视频循环播放
   */
  void setLooping(boolean looping);

  /**
   * 获取视频的宽度
   */
  int getVideoWidth();

  /**
   * 设置视频的高度
   */
  int getVideoHeight();

  /**
   * 设置音量
   */
  void setVolume(float leftVolume, float rightVolume);

  /**
   * 设置Surface，给mediaPlayer暴露Surface
   */
  void setSurface(@NonNull Surface surface) throws IllegalStateException;

  /**
   * 设置视频链接
   */
  void setDataSource(String url, String manifest);

  /**
   * 设置视频资源准备就绪的监听
   */
  void setOnPreparedListener(@NonNull OnPreparedListener listener);

  /**
   * 设置视频播放结束的监听
   */
  void setOnCompletionListener(@NonNull OnCompletionListener listener);

  /**
   * 设置视频播放buffer的监听
   */
  void setOnBufferingUpdateListener(@NonNull OnBufferingUpdateListener listener);

  /**
   * 设置视频播放异常的的监听
   */
  void setOnErrorListener(@NonNull OnErrorListener listener);

  /**
   * 注册信息更新的回调
   */
  void setOnInfoListener(@NonNull OnInfoListener listener);

  /**
   * 设置尺寸变换的监听
   *
   * @param listener 尺寸变化的监听
   */
  void setOnVideoSizeChangedListener(@NonNull OnVideoSizeChangedListener listener);

  /**
   * 播放事件的回调，目前只需要首帧回调
   *
   * @param playerEventListener 播放事件的监听
   */
  void setPlayerEventListener(@NonNull PlayerEventListener playerEventListener);


  /////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////

  interface OnPreparedListener {
    void onPrepared(@NonNull IMediaPlayerService mp);
  }

  interface OnCompletionListener {
    void onCompletion(@NonNull IMediaPlayerService mp);
  }

  interface OnBufferingUpdateListener {
    void onBufferingUpdate(@NonNull IMediaPlayerService mp, int percent);
  }

  interface OnErrorListener {
    boolean onError(@NonNull IMediaPlayerService mp, int what, int extra);
  }

  interface OnInfoListener {
    boolean onInfo(@NonNull IMediaPlayerService mp, int what, int extra);
  }

  interface OnVideoSizeChangedListener {
    void onVideoSizeChanged(IMediaPlayerService mp, int width, int height);
  }


  /**
   * Interface definition for player event.
   * <p>
   * 对照VodPlayerEventListener创建，用于video state event事件上报时一些数据的填充和时机的对其
   */
  interface PlayerEventListener {

    /**
     * 首帧回调
     */
    default void onFirstFrameRenderStarted() {}

  }

}
