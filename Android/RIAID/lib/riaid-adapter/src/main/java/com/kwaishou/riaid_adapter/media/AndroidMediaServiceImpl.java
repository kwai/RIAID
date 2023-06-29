package com.kwaishou.riaid_adapter.media;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.render.service.base.IMediaPlayerService;

/**
 * 系统播放器默认实现
 */
public class AndroidMediaServiceImpl implements IMediaPlayerService {
  private final MediaPlayer mMediaPlayer;
  private String mResourceUrl;
  private boolean isPrepared;
  private OnPreparedListener mOnPreparedListener;
  private boolean isLooping;
  private OnCompletionListener mOnCompletionListener;
  private OnBufferingUpdateListener mOnBufferingUpdateListener;
  private OnVideoSizeChangedListener mOnVideoSizeChangedListener;
  private OnErrorListener mOnErrorListener;
  private OnInfoListener mOnInfoListener;
  private Surface mSurface;
  private PlayerEventListener mPlayerEventListener;
  // 默认实现MediaPlayer监听首帧有点问题，所以这里加了个标志
  private boolean isFirstFrame = false;

  public AndroidMediaServiceImpl() {
    mMediaPlayer = new MediaPlayer();
  }

  @Override
  public void setSurface(@NonNull Surface surface) {
    mSurface = surface;
  }

  @Override
  public void setDataSource(String url, String manifest) {
    mResourceUrl = url;
  }

  @Override
  public void prepareAsync() throws IllegalStateException {
    try {
      try {
        mMediaPlayer.setDataSource(mResourceUrl);
      } catch (Exception e) {
        Log.e("AndroidMediaServiceImpl", e.getMessage());
      }
      mMediaPlayer.setSurface(mSurface);
      mMediaPlayer.setLooping(isLooping);
      mMediaPlayer.setOnErrorListener((mp, what, extra) -> {
        if (mOnErrorListener != null) {
          return mOnErrorListener.onError(AndroidMediaServiceImpl.this, what, extra);
        }
        return false;
      });
      mMediaPlayer.setOnCompletionListener(mp -> {
        if (mOnCompletionListener != null) {
          mOnCompletionListener.onCompletion(AndroidMediaServiceImpl.this);
        }
      });
      mMediaPlayer.setOnPreparedListener(mp -> {
        isPrepared = true;
        if (mOnPreparedListener != null) {
          mOnPreparedListener.onPrepared(AndroidMediaServiceImpl.this);
        }
      });
      mMediaPlayer.setOnVideoSizeChangedListener((mp, width, height) -> {
        if (mOnVideoSizeChangedListener != null) {
          mOnVideoSizeChangedListener
              .onVideoSizeChanged(AndroidMediaServiceImpl.this, width, height);
        }
      });
      mMediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
        if (mOnBufferingUpdateListener != null) {
          mOnBufferingUpdateListener.onBufferingUpdate(AndroidMediaServiceImpl.this, percent);
        }
      });
      mMediaPlayer.setOnInfoListener((mp, what, extra) -> {
        if (mOnInfoListener != null) {
          mOnInfoListener.onInfo(AndroidMediaServiceImpl.this, what, extra);
        }
        if (extra == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START ||
            what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
          if (mPlayerEventListener != null && !isFirstFrame) {
            mPlayerEventListener.onFirstFrameRenderStarted();
            isFirstFrame = true;
          }
        }
        return false;
      });
      mMediaPlayer.prepareAsync();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void start() throws IllegalStateException {
    if (isPrepared) {
      mMediaPlayer.start();
    }
  }

  @Override
  public void stop() throws IllegalStateException {
    if (isPrepared) {
      mMediaPlayer.stop();
    }
  }

  @Override
  public void pause() throws IllegalStateException {
    if (isPrepared) {
      mMediaPlayer.pause();
    }
  }

  @Override
  public void seekTo(long msec) throws IllegalStateException {
    if (isPrepared) {
      mMediaPlayer.seekTo((int) msec);
    }
  }


  @Override
  public int getVideoWidth() {
    return mMediaPlayer.getVideoWidth();
  }

  @Override
  public int getVideoHeight() {
    return mMediaPlayer.getVideoHeight();
  }

  @Override
  public boolean isPlaying() {
    return mMediaPlayer.isPlaying();
  }

  @Override
  public long getCurrentPosition() {
    return mMediaPlayer.getCurrentPosition();
  }

  @Override
  public long getDuration() {
    return mMediaPlayer.getDuration();
  }

  @Override
  public void release() {
    mMediaPlayer.release();
  }

  @Override
  public void reset() {
    mMediaPlayer.reset();
  }

  @Override
  public void setVolume(float leftVolume, float rightVolume) {
    mMediaPlayer.setVolume(leftVolume, rightVolume);
  }

  @Override
  public void setOnPreparedListener(@NonNull OnPreparedListener listener) {
    mOnPreparedListener = listener;
  }

  @Override
  public void setOnCompletionListener(@NonNull OnCompletionListener listener) {
    mOnCompletionListener = listener;
  }

  @Override
  public void setOnBufferingUpdateListener(@NonNull OnBufferingUpdateListener listener) {
    mOnBufferingUpdateListener = listener;
  }

  @Override
  public void setOnVideoSizeChangedListener(@NonNull OnVideoSizeChangedListener listener) {
    mOnVideoSizeChangedListener = listener;
  }

  @Override
  public void setOnErrorListener(@NonNull OnErrorListener listener) {
    mOnErrorListener = listener;
  }

  @Override
  public void setOnInfoListener(@NonNull OnInfoListener listener) {
    mOnInfoListener = listener;
  }

  @Override
  public void setLooping(boolean looping) {
    isLooping = looping;
  }


  @Override
  public void setPlayerEventListener(@NonNull PlayerEventListener playerEventListener) {
    mPlayerEventListener = playerEventListener;
  }

}
