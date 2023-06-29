package com.kuaishou.riaid.render.service.base;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 这个是加载图片的service，使用render的宿主负责实现
 */
public interface ILoadImageService {


  /**
   * 预加载图片放到缓存，并不需要展示出来
   *
   * @param url      图片的链接，这个链接可能是网络图片，也有可能是本地图片
   * @param listener 加载是否成功的回调
   */
  void preload(@NonNull String url, @Nullable ILoadListener listener);

  /**
   * 加载图片
   *
   * @param url      图片的链接，这个链接可能是网络图片，也有可能是本地图片
   * @param view     加载图片的视图View，也就是ImageView
   * @param listener 加载是否成功的回调
   */
  void loadImage(@NonNull String url, @NonNull ImageView view, @Nullable ILoadListener listener);

  /**
   * 加载图片
   *
   * @param url  图片的链接，这个链接可能是网络图片，也有可能是本地图片
   * @param view 加载图片的视图View，也就是ImageView
   */
  void loadImage(@NonNull String url, @NonNull ImageView view);

  /**
   * 加载图片
   *
   * @param url    图片的链接，这个链接可能是网络图片，也有可能是本地图片
   * @param view   加载图片的视图View，也就是ImageView
   * @param width  传入的固定的宽度
   * @param height 传入的固定的高度
   */
  void loadImage(@NonNull String url, @NonNull ImageView view, int width, int height);


  /**
   * 这个是查看图片生命周期的
   *
   * @param url           图片的连接
   * @param placeHolder   加载中的占位图
   * @param listener      加载回调监听
   * @param errorDrawable 加载失败的占位图
   */
  void loadBitmap(@NonNull String url, @Nullable Drawable placeHolder,
      @Nullable Drawable errorDrawable, @Nullable IImageListener listener);

  /**
   * 这个是加载图片回调的接口
   */
  interface ILoadListener {

    /**
     * 图片加载成功
     */
    void onSuccess();

    /**
     * 图片加载失败
     *
     * @param e 失败异常对象
     */
    void onFailure(@Nullable Exception e);

  }

  /**
   * 图片下载过程的监听
   */
  interface IImageListener {

    /**
     * 图片下载成功
     *
     * @param bitmap 返回的bitmap，自己可以加载到图片中
     */
    void onBitmapLoaded(Bitmap bitmap);

    /**
     * 图片下载失败
     *
     * @param e             失败异常对象
     * @param errorDrawable 异常的drawable
     */
    void onBitmapFailed(Exception e, @Nullable Drawable errorDrawable);

    /**
     * 这个时候显示占位图
     *
     * @param placeHolderDrawable 占位图的drawable
     */
    void onPrepareLoad(@Nullable Drawable placeHolderDrawable);
  }
}
