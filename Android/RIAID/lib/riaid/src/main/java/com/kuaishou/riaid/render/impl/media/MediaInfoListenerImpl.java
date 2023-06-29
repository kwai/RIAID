package com.kuaishou.riaid.render.impl.media;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.interf.IDispatchEventService;
import com.kuaishou.riaid.render.interf.IImpressionListener;
import com.kuaishou.riaid.render.service.base.IMediaPlayerService;
import com.kuaishou.riaid.render.service.base.IResumeActionService;

/**
 * 关键事件监听类，目前只需要关注首帧
 */
public class MediaInfoListenerImpl implements IMediaPlayerService.OnInfoListener,
    IMediaPlayerService.PlayerEventListener {

  @Nullable
  private IDispatchEventService mDispatchEventService;

  private final List<IImpressionListener> mImpressionList = new ArrayList<>();

  public void setDispatchEventService(@Nullable IDispatchEventService dispatchEventService) {
    this.mDispatchEventService = dispatchEventService;
  }

  public void registerImpressionListener(@NonNull IImpressionListener listener) {
    if (!mImpressionList.contains(listener)) {
      mImpressionList.add(listener);
    }
  }

  /**
   * 这里暂时用不到，不过这属于播放器都有这个信息回调，先保留。
   */
  @Override
  public boolean onInfo(@NonNull IMediaPlayerService mp, int what, int extra) {
    return false;
  }

  /**
   * 首帧回调回来需要通知
   */
  @Override
  public void onFirstFrameRenderStarted() {
    if (mDispatchEventService != null) {
      mDispatchEventService.dispatchEvent(IResumeActionService.ACTION_TYPE_VIDEO_IMPRESSION);
      for (IImpressionListener impressionListener : mImpressionList) {
        impressionListener.onImpression();
      }
    }
  }
}
