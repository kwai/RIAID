package com.kuaishou.riaid.adbrowser.service;

import java.io.File;
import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.preload.RIAIDPreloadResourceOperator;

/**
 * 音频播放服务，目前只是负责简单的音效播放。目前业务上只需要同时播放一个音频，所以采用单例。
 */
public class AudioPlayService {
  private static final AudioPlayService instance = new AudioPlayService();
  private MediaPlayer mMediaPlayer;

  public static AudioPlayService getInstance() {
    return instance;
  }

  /**
   * 要求是在子线程播放音频
   *
   * @param path 可以是本地文件，也可以是网络url。目前本地没有音频资源，只需要支持网络url即可。
   *             注意，系统的{@link MediaPlayer 不支持https}
   */
  @WorkerThread
  public synchronized void playAudio(@NonNull String path) {
    if (mMediaPlayer == null) {
      mMediaPlayer = new MediaPlayer();
    }
    mMediaPlayer.reset();
    mMediaPlayer.setLooping(false);
    try {
      File file = RIAIDPreloadResourceOperator.getPreloadExistsFile(path);

      if (file != null) {
        String url = path;
        path = file.getAbsolutePath();
        ADRenderLogger.i(
            RIAIDPreloadResourceOperator.PRELOAD_TAG + "音频 压缩文件本地加载：" + file.getAbsolutePath() +
                " url: " + url);
      } else {
        ADRenderLogger.i(RIAIDPreloadResourceOperator.PRELOAD_TAG + "音频网络加载 url: " + path);
      }
      mMediaPlayer.setDataSource(path);
      mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      // 音量调到最大
      mMediaPlayer.setVolume(1, 1);
      mMediaPlayer.prepare();
    } catch (IOException ioException) {
      ADBrowserLogger.e("音频播放失败", ioException);
    }
    mMediaPlayer.start();
  }


  /**
   * 释放播放器资源
   */
  public synchronized void release() {
    if (mMediaPlayer != null) {
      mMediaPlayer.stop();
      mMediaPlayer.release();
    }
    mMediaPlayer = null;
  }
}
