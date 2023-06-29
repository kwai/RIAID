package com.kuaishou.riaid.render.impl.empty;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.service.base.ILoadImageService;

public interface IEmptyImageListener extends ILoadImageService.IImageListener {

  /**
   * 图片下载成功
   *
   * @param bitmap 返回的bitmap，自己可以加载到图片中
   */
  default void onBitmapLoaded(Bitmap bitmap) {}

  /**
   * 图片下载失败
   *
   * @param e             失败异常对象
   * @param errorDrawable 异常的drawable
   */
  default void onBitmapFailed(Exception e, @Nullable Drawable errorDrawable) {}

  /**
   * 这个时候显示占位图
   *
   * @param placeHolderDrawable 占位图的drawable
   */
  default void onPrepareLoad(@Nullable Drawable placeHolderDrawable) {}

}
