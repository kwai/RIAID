package com.kuaishou.riaid.render.impl.media;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.interf.IDispatchEventService;
import com.kuaishou.riaid.render.service.base.IMediaPlayerService;
import com.kuaishou.riaid.render.service.base.IResumeActionService;

/**
 * 剥离接口，这个是视频播结束监听的默认实现类
 */
public class MediaCompleteImpl implements IMediaPlayerService.OnCompletionListener {

  @Nullable
  private IDispatchEventService mDispatchEventService;

  /**
   * 是否循环播放
   */
  private boolean mLooping = false;

  /**
   * 循环播放次数
   */
  private int mLoopingCount = 0;

  public void setDispatchEventService(@Nullable IDispatchEventService dispatchEventService) {
    this.mDispatchEventService = dispatchEventService;
  }

  public void setLooping(boolean looping) {
    this.mLooping = looping;
  }

  @Override
  public void onCompletion(@NonNull IMediaPlayerService mp) {
    mLoopingCount++;
    if (mDispatchEventService != null) {
      mDispatchEventService.dispatchEvent(IResumeActionService.ACTION_TYPE_VIDEO_FINISH);
    }
    // 让视频回到其实位置，并且继续开始播放
    if (mLooping) {
      mp.seekTo(0);
      mp.start();
    }
  }

  /**
   * 获取视频播放循环次数
   *
   * @return 返回播放次数
   */
  public int getLoopingCount() {
    return mLoopingCount;
  }
}
