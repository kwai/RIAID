package com.kuaishou.riaid.render.impl.media.service;

import android.view.Surface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.interf.IDispatchEventService;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.service.base.IMediaPlayerService;
import com.kuaishou.riaid.render.service.base.IResumeActionService;
import com.kuaishou.riaid.render.widget.video.RenderTextureView;

/**
 * 这个是MediaPlayer的包装类，相对于RenderTextureView，很像一个Presenter
 */
public class MediaPlayerServiceImpl implements IMediaPlayerService {

  @NonNull
  private final RenderTextureView mRenderTextureView;

  @Nullable
  private IMediaPlayerService mMediaPlayerService;

  @Nullable
  private IDispatchEventService mDispatchEventService;

  private boolean mAlreadyPaused = false;

  public MediaPlayerServiceImpl(@NonNull RenderTextureView renderTextureView) {
    this.mRenderTextureView = renderTextureView;
  }

  public void setMediaPlayerService(@Nullable IMediaPlayerService mediaPlayerService) {
    mMediaPlayerService = mediaPlayerService;
  }

  public void setDispatchEventListener(@Nullable IDispatchEventService dispatchEventService) {
    this.mDispatchEventService = dispatchEventService;
  }

  @Nullable
  public IMediaPlayerService getRealMediaPlayerService() {
    return mMediaPlayerService;
  }

  @Override
  public void prepareAsync() {
    try {
      if (mMediaPlayerService != null) {
        mMediaPlayerService.prepareAsync();
      }
    } catch (IllegalStateException e) {
      e.printStackTrace();
      ADRenderLogger.e("media prepareAsync failed", e);
    }
  }

  @Override
  public void start() throws IllegalStateException {
    try {
      mRenderTextureView.setAutoPlay(true);
      if (mRenderTextureView.prepared() && mMediaPlayerService != null &&
          !mMediaPlayerService.isPlaying()) {
        mMediaPlayerService.start();
        // 这个时候的埋点才是有意义的，其他情况，属于异常，和IOS对齐了
        // mAlreadyPaused = false这个证明是首次播放，其他情况都是暂停后播放的
        if (mDispatchEventService != null) {
          mDispatchEventService.dispatchEvent(mAlreadyPaused ?
              IResumeActionService.ACTION_TYPE_VIDEO_RESUME
              : IResumeActionService.ACTION_TYPE_VIDEO_START);
        }
      }
    } catch (IllegalStateException e) {
      e.printStackTrace();
      ADRenderLogger.e("media start failed", e);
    }
  }

  @Override
  public void stop() throws IllegalStateException {
    try {
      if (mRenderTextureView.prepared() && mMediaPlayerService != null) {
        mMediaPlayerService.stop();
      }
    } catch (IllegalStateException e) {
      e.printStackTrace();
      ADRenderLogger.e("media stop failed", e);
    }
  }

  @Override
  public void pause() throws IllegalStateException {
    try {
      // 证明暂停过
      this.mAlreadyPaused = true;
      if (mRenderTextureView.prepared() && mMediaPlayerService != null &&
          mMediaPlayerService.isPlaying()) {
        mMediaPlayerService.pause();
        if (mDispatchEventService != null) {
          mDispatchEventService.dispatchEvent(IResumeActionService.ACTION_TYPE_VIDEO_PAUSE);
        }
      }
    } catch (IllegalStateException e) {
      e.printStackTrace();
      ADRenderLogger.e("media pause failed", e);
    }
  }

  @Override
  public void seekTo(long msec) throws IllegalStateException {
    try {
      if (mRenderTextureView.prepared() && mMediaPlayerService != null) {
        mMediaPlayerService.seekTo(msec);
      }
    } catch (IllegalStateException e) {
      e.printStackTrace();
      ADRenderLogger.e("media seekTo failed", e);
    }
  }

  @Override
  public boolean isPlaying() throws IllegalStateException {
    try {
      if (mMediaPlayerService != null) {
        return mMediaPlayerService.isPlaying();
      }
    } catch (IllegalStateException e) {
      e.printStackTrace();
      ADRenderLogger.e("media isPlaying failed", e);
    }
    return false;
  }

  @Override
  public long getCurrentPosition() {
    try {
      if (mMediaPlayerService != null) {
        return mMediaPlayerService.getCurrentPosition();
      }
    } catch (IllegalStateException e) {
      e.printStackTrace();
      ADRenderLogger.e("media getCurrentPosition failed", e);
    }
    return 0;
  }

  @Override
  public long getDuration() {
    try {
      if (mMediaPlayerService != null) {
        return mMediaPlayerService.getDuration();
      }
    } catch (IllegalStateException e) {
      e.printStackTrace();
      ADRenderLogger.e("media getDuration failed", e);
    }
    return 0;
  }

  @Override
  public void release() {
    if (mMediaPlayerService != null) {
      mMediaPlayerService.release();
    }
  }

  @Override
  public void reset() {
    try {
      if (mMediaPlayerService != null) {
        mMediaPlayerService.reset();
      }
    } catch (IllegalStateException e) {
      e.printStackTrace();
      ADRenderLogger.e("media reset failed", e);
    }
  }

  @Override
  public void setLooping(boolean looping) {
    if (mMediaPlayerService != null) {
      mMediaPlayerService.setLooping(looping);
    }
  }

  @Override
  public int getVideoWidth() {
    try {
      if (mMediaPlayerService != null) {
        return mMediaPlayerService.getVideoWidth();
      }
    } catch (IllegalStateException e) {
      e.printStackTrace();
      ADRenderLogger.e("media getVideoWidth failed", e);
    }
    return 0;

  }

  @Override
  public int getVideoHeight() {
    try {
      if (mMediaPlayerService != null) {
        return mMediaPlayerService.getVideoHeight();
      }
    } catch (IllegalStateException e) {
      e.printStackTrace();
      ADRenderLogger.e("media getVideoHeight failed", e);
    }
    return 0;
  }

  @Override
  public void setVolume(float leftVolume, float rightVolume) {
    try {
      if (mMediaPlayerService != null) {
        mMediaPlayerService.setVolume(leftVolume, rightVolume);
      }
    } catch (IllegalStateException e) {
      e.printStackTrace();
      ADRenderLogger.e("media setVolume failed", e);
    }
  }

  @Override
  public void setSurface(@NonNull Surface surface) {
    try {
      if (mMediaPlayerService != null) {
        mMediaPlayerService.setSurface(surface);
      }
    } catch (IllegalStateException e) {
      e.printStackTrace();
      ADRenderLogger.e("media setSurface failed", e);
    }
  }

  @Override
  public void setDataSource(String url, String manifest) {
    if (mMediaPlayerService != null) {
      mMediaPlayerService.setDataSource(url, manifest);
    }
  }

  @Override
  public void setOnPreparedListener(@NonNull OnPreparedListener listener) {
    if (mMediaPlayerService != null) {
      mMediaPlayerService.setOnPreparedListener(listener);
    }
  }

  @Override
  public void setOnCompletionListener(@NonNull OnCompletionListener listener) {
    if (mMediaPlayerService != null) {
      mMediaPlayerService.setOnCompletionListener(listener);
    }
  }

  @Override
  public void setOnBufferingUpdateListener(@NonNull OnBufferingUpdateListener listener) {
    if (mMediaPlayerService != null) {
      mMediaPlayerService.setOnBufferingUpdateListener(listener);
    }
  }

  @Override
  public void setOnErrorListener(@NonNull OnErrorListener listener) {
    if (mMediaPlayerService != null) {
      mMediaPlayerService.setOnErrorListener(listener);
    }
  }

  @Override
  public void setOnInfoListener(@NonNull OnInfoListener listener) {
    if (mMediaPlayerService != null) {
      mMediaPlayerService.setOnInfoListener(listener);
    }
  }

  @Override
  public void setOnVideoSizeChangedListener(@NonNull OnVideoSizeChangedListener listener) {
    if (mMediaPlayerService != null) {
      mMediaPlayerService.setOnVideoSizeChangedListener(listener);
    }
  }

  @Override
  public void setPlayerEventListener(@NonNull PlayerEventListener playerEventListener) {
    if (mMediaPlayerService != null) {
      mMediaPlayerService.setPlayerEventListener(playerEventListener);
    }
  }
}
