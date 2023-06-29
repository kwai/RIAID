package com.kuaishou.riaid.render.impl.media;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.render.service.base.IMediaPlayerService;

/**
 * 这个是用来监听视频尺寸变化的
 */
public class MediaSizeChangedImpl implements IMediaPlayerService.OnVideoSizeChangedListener {

  private final List<IMediaPlayerService.OnVideoSizeChangedListener> mListeners = new ArrayList<>();

  public void registerOnVideoSizeChangedListener(
      @NonNull IMediaPlayerService.OnVideoSizeChangedListener listener) {
    if (!mListeners.contains(listener)) {
      mListeners.add(listener);
    }
  }

  @Override
  public void onVideoSizeChanged(IMediaPlayerService mp, int width, int height) {
    for (IMediaPlayerService.OnVideoSizeChangedListener listener : mListeners) {
      listener.onVideoSizeChanged(mp, width, height);
    }
  }

}
