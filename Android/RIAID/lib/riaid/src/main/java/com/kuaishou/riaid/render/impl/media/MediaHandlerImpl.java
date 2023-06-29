package com.kuaishou.riaid.render.impl.media;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.interf.IDispatchEventService;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.service.base.IResumeActionService;

/**
 * 这个是Video的Handler的实现类
 */
public class MediaHandlerImpl implements IDispatchEventService {

  @Nullable
  private final UIModel.VideoHandler mVideoHandler;

  @NonNull
  private final UIModel.NodeContext mNodeContext;

  @Nullable
  private final IResumeActionService mResumeActionService;

  public MediaHandlerImpl(@Nullable UIModel.VideoHandler videoHandler,
      @NonNull UIModel.NodeContext nodeContext,
      @Nullable IResumeActionService resumeActionService) {
    this.mNodeContext = nodeContext;
    this.mVideoHandler = videoHandler;
    this.mResumeActionService = resumeActionService;
  }

  /**
   * 触发对应的事件
   *
   * @param eventType 当事件的类型，比如首帧，播放等，参考{@link IResumeActionService}的常量
   */
  @Override
  public void dispatchEvent(int eventType) {
    if (mResumeActionService == null || mVideoHandler == null) {
      return;
    }
    switch (eventType) {
      case IResumeActionService.ACTION_TYPE_VIDEO_IMPRESSION:
        resumeRenderAction(eventType, mVideoHandler.impression);
        break;
      case IResumeActionService.ACTION_TYPE_VIDEO_FINISH:
        resumeRenderAction(eventType, mVideoHandler.finish);
        break;
      case IResumeActionService.ACTION_TYPE_VIDEO_PAUSE:
        resumeRenderAction(eventType, mVideoHandler.pause);
        break;
      case IResumeActionService.ACTION_TYPE_VIDEO_START:
        resumeRenderAction(eventType, mVideoHandler.start);
        break;
      case IResumeActionService.ACTION_TYPE_VIDEO_RESUME:
        resumeRenderAction(eventType, mVideoHandler.resume);
        break;
      default:
        ADRenderLogger.e("MediaHandlerImpl 无法识别的type类型 " + eventType);
        break;
    }
  }

  /**
   * 具体的事件透传，把事件传递给Browser
   *
   * @param eventType 事件类型，参考{@link IResumeActionService}的常量
   * @param responder 里面是triggerKey的结合，触发具体的动作，比如埋点
   */
  private void resumeRenderAction(int eventType, @Nullable UIModel.Responder responder) {
    if (mResumeActionService != null && responder != null) {
      mResumeActionService.resumeRenderAction(eventType, mNodeContext, responder);
    }
  }
}
