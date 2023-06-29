package com.kuaishou.riaid.render.impl.media;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.render.service.base.IMediaPlayerService;

/**
 * 剥离接口，这个是视频播准备监听的默认实现类
 */
public class MediaPrepareImpl implements IMediaPlayerService.OnPreparedListener {

  private boolean mPrepared = false;

  /**
   * 视频加载好之后，是否需要立马播放
   */
  private boolean mAutoPlay = false;

  /**
   * 这个是服务端下发的字段，视频刚开始就要从这个位置播放
   */
  private int mFirstSeekMesc = 0;

  private final List<IMediaPlayerService.OnPreparedListener> mPreparedList = new ArrayList<>();

  public void registerOnPreparedListener(@NonNull IMediaPlayerService.OnPreparedListener listener) {
    if (!mPreparedList.contains(listener)) {
      mPreparedList.add(listener);
    }
  }

  public boolean prepared() {
    return mPrepared;
  }

  public void setAutoPlay(boolean autoPlay) {
    this.mAutoPlay = autoPlay;
  }

  public void setFirstSeekMesc(int firstSeekMesc) {
    this.mFirstSeekMesc = firstSeekMesc;
  }

  @Override
  public void onPrepared(@NonNull IMediaPlayerService mp) {
    mPrepared = true;
    // 通知外界
    for (IMediaPlayerService.OnPreparedListener listener : mPreparedList) {
      listener.onPrepared(mp);
    }
    if (mAutoPlay) {
      mp.start();
      if (mFirstSeekMesc > 0) {
        mp.seekTo(mFirstSeekMesc);
        mFirstSeekMesc = 0; // 重置，不需要再次seek了，这个是第一次传递来的，执行一次就好
      }
    }
  }
}
