package com.kuaishou.riaid.render.service.base.render;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.render.service.base.IDataBindingService;
import com.kuaishou.riaid.render.service.base.ILoadImageService;
import com.kuaishou.riaid.render.service.base.IMediaPlayerService;
import com.kuaishou.riaid.render.service.base.IRIAIDLogReportService;
import com.kuaishou.riaid.render.service.base.IResumeActionService;

/**
 * 这些是render需要的service
 */
public interface IRenderService {

  /**
   * 获取消费事件的service实例
   *
   * @return 返回一个非空的IResumeActionService实现类实例
   */
  @NonNull
  IResumeActionService getResumeActionService();

  /**
   * 获取绑定数据解析占位符的service实例
   *
   * @return 返回一个非空的IDataBindingService实现类实例
   */
  @NonNull
  IDataBindingService getDataBindingService();

  /**
   * 获取加载图片的service实例
   *
   * @return 返回一个非空的ILoadImageService实现类实例
   */
  @NonNull
  ILoadImageService getLoadImageService();

  /**
   * @return 返回一个非空的IRIAIDLogReportService实现类实例
   */
  @NonNull
  IRIAIDLogReportService getRIAIDLogReportService();

  /**
   * @return 返回一个非空的IMediaService的具体实现类
   */
  @NonNull
  IMediaPlayerService getMediaService();

}
