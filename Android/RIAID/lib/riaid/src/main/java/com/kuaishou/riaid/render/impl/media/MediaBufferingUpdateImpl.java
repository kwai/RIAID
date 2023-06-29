package com.kuaishou.riaid.render.impl.media;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.render.service.base.IMediaPlayerService;

/**
 * 剥离接口，这个是视频播放Buffer的默认实现类
 */
public class MediaBufferingUpdateImpl implements IMediaPlayerService.OnBufferingUpdateListener {

  @Override
  public void onBufferingUpdate(@NonNull IMediaPlayerService mp, int percent) {

  }
}
