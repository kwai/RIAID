package com.kuaishou.riaid.render.adapter.video.base;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.render.model.UIModel;

/**
 * 视频适配的基类
 */
public abstract class BaseVideoAdapter {

  /**
   * 获取适配的类型
   *
   * @return 返回适配类型的枚举
   */
  public abstract int getVideoAdapterType();

  /**
   * 根据容器尺寸，和视频真实的尺寸，计算出适配之后的适配尺寸和位置
   *
   * @param containerSize 容器的尺寸
   * @param videoSize     视频的原始尺寸
   * @return 返回适配的结果
   */
  @NonNull
  public abstract AdapterModel adapter(@NonNull UIModel.Size containerSize,
      @NonNull UIModel.Size videoSize);


  /**
   * 这个是适配的返回值
   */
  public static final class AdapterModel {
    @NonNull
    public final UIModel.Size newVideoSize;
    @NonNull
    public final UIModel.Point deltaPoint;

    public AdapterModel(@NonNull UIModel.Size newVideoSize,
        @NonNull UIModel.Point deltaPoint) {
      this.newVideoSize = newVideoSize;
      this.deltaPoint = deltaPoint;
    }
  }
}
