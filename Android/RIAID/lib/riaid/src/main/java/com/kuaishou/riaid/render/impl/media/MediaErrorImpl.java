package com.kuaishou.riaid.render.impl.media;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.render.service.base.IMediaPlayerService;

/**
 * 剥离接口，这个是视频播异常监听的默认实现类
 */
public class MediaErrorImpl implements IMediaPlayerService.OnErrorListener {

  @Override
  public boolean onError(@NonNull IMediaPlayerService mp, int what, int extra) {
    return false;
  }

}
