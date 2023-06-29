package com.kuaishou.riaid.adbrowser.service;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.render.service.base.IDataBindingService;
import com.kuaishou.riaid.render.service.base.ILoadImageService;
import com.kuaishou.riaid.render.service.base.IMediaPlayerService;
import com.kuaishou.riaid.render.service.base.IRIAIDLogReportService;
import com.kuaishou.riaid.render.service.base.IResumeActionService;
import com.kuaishou.riaid.render.service.base.render.IRenderService;

/**
 * 这个类是Render需要的service的默认实现
 */
public class RenderService implements IRenderService {

  @NonNull
  private final IResumeActionService mResumeActionService;
  @NonNull
  private final IDataBindingService mDataBindingService;
  @NonNull
  private final ILoadImageService mLoadImageService;
  @NonNull
  private final IRIAIDLogReportService mRIAIDLogReportService;
  @NonNull
  private final IMediaPlayerService mMediaPlayerService;

  public RenderService(
      @NonNull IDataBindingService dataBindingService,
      @NonNull ILoadImageService loadImageService,
      @NonNull IResumeActionService renderService,
      @NonNull IRIAIDLogReportService reportService,
      @NonNull IMediaPlayerService mediaPlayerService) {
    mResumeActionService = renderService;
    mDataBindingService = dataBindingService;
    mLoadImageService = loadImageService;
    mRIAIDLogReportService = reportService;
    mMediaPlayerService = mediaPlayerService;
  }

  @NonNull
  @Override
  public IResumeActionService getResumeActionService() {
    return mResumeActionService;
  }

  @NonNull
  @Override
  public IDataBindingService getDataBindingService() {
    return mDataBindingService;
  }

  @NonNull
  @Override
  public ILoadImageService getLoadImageService() {
    return mLoadImageService;
  }

  @NonNull
  @Override
  public IRIAIDLogReportService getRIAIDLogReportService() {
    return mRIAIDLogReportService;
  }

  @NonNull
  @Override
  public IMediaPlayerService getMediaService() {
    return mMediaPlayerService;
  }
}
