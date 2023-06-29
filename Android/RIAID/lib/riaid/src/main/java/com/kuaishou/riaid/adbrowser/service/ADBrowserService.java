package com.kuaishou.riaid.adbrowser.service;

import android.content.Context;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.render.service.base.IDataBindingService;
import com.kuaishou.riaid.render.service.base.ILoadImageService;
import com.kuaishou.riaid.render.service.base.IMediaPlayerService;

/**
 * Browser服务的接口约束，要求提供两个非空的Service，是ADRender需要的。
 */
public interface ADBrowserService {

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
   * @return 返回一个非空的视频播放服务
   */
  @NonNull
  IMediaPlayerService getMediaPlayerService(@NonNull Context context);


  /**
   * 用来区分debug还是release，debug下通常用于mock数据
   *
   * @return 是否为debug环境
   */
  default boolean isDebug() {
    return false;
  }
}
