package com.kuaishou.riaid.render.lottie;

import java.util.List;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.kuaishou.riaid.proto.nano.LottieAttributes;
import com.kuaishou.riaid.render.impl.DefaultAnimatorListener;
import com.kuaishou.riaid.render.impl.empty.IEmptyImageListener;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.preload.RIAIDPreloadResourceOperator;
import com.kuaishou.riaid.render.service.base.ILoadImageService;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * Lottie中对于图片的替换支持，在lottie动画播放过程中，也可以替换图片
 */
public class LottieImageDelegate {

  @NonNull
  private final LottieAnimationView mLottieAnimationView;
  private final ILoadImageService mImageService;
  private final List<LottieAttributes.ReplaceImage> mReplaceImageList;
  /**
   * 替换图片是否支持网络图片。如果是false，仅支持本地图片下载好了的，才能替换，如果是true，则可以在播放期间下载图片并替换
   */
  private final boolean mReplaceImageSupportNet;
  /**
   * 用来给替换的图片计数，完全替换完成，才能开始播放Lottie，不然体验比较差，会在播放过程中替换图片
   */
  private int replaceCount = -1;

  @Nullable
  private OnLottieImageReplaceCallback mOnLottieImageReplaceCallback;

  public LottieImageDelegate(@NonNull LottieAnimationView lottieView,
      @Nullable ILoadImageService service,
      List<LottieAttributes.ReplaceImage> replaceImageList, boolean replaceImageSupportNet) {
    mLottieAnimationView = lottieView;
    mImageService = service;
    mReplaceImageList = replaceImageList;
    mReplaceImageSupportNet = replaceImageSupportNet;
  }

  public void setOnLottieImageReplaceCallback(
      @Nullable OnLottieImageReplaceCallback onLottieImageReplaceCallback) {
    mOnLottieImageReplaceCallback = onLottieImageReplaceCallback;
  }

  /**
   * 使用{@link ILoadImageService}去下载图片，拿到后然后开始替换。
   *
   * @param placeImageId lottie资源中定义好的要替换的图片id
   * @param imageAddress 要替换的图片的url
   */
  private void replaceImage(String placeImageId, String imageAddress) {
    if (TextUtils.isEmpty(placeImageId) || TextUtils.isEmpty(imageAddress)) {
      consumeReplaceCount();
      ADRenderLogger.w("LottieImageDelegate中placeImageId或imageAddress为空");
      return;
    }
    if (mImageService == null) {
      consumeReplaceCount();
      ADRenderLogger.w("LottieImageDelegate中mImageService为空");
      return;
    }
    String realUrl = RIAIDPreloadResourceOperator.getRealUrl(imageAddress);
    mImageService.loadBitmap(realUrl, null, null, new IEmptyImageListener() {
      @Override
      public void onBitmapLoaded(Bitmap bitmap) {
        // 防止数据配错导致异常，这里捕获一下。
        try {
          if (bitmap != null) {
            mLottieAnimationView.updateBitmap(placeImageId, bitmap);
          }
          consumeReplaceCount();
        } catch (Exception e) {
          // 即便是出现异常，这次替换程序也认为是走完了，不能影响逻辑
          consumeReplaceCount();
          ADRenderLogger.e("LottieImageDelegate updateBitmap异常", e);
        }
      }

      @Override
      public void onBitmapFailed(Exception e, @Nullable Drawable errorDrawable) {
        ADRenderLogger.e("LottieImageDelegate onBitmapFailed", e);
        consumeReplaceCount();
      }
    });
  }

  /**
   * 替换所有指定的图片
   */
  public void replaceAllImage() {
    if (!ToolHelper.isListValid(mReplaceImageList)) {
      if (mOnLottieImageReplaceCallback != null) {
        mOnLottieImageReplaceCallback.onLottieImageReplaceFail();
      }
      return;
    }
    boolean isAllLocalExit = true;
    for (LottieAttributes.ReplaceImage replaceImage : mReplaceImageList) {
      // 每遇到一张要替换的图片都+1
      replaceCount++;
      if (replaceImage != null) {
        // 有一个不存在，就不要去做图片替换，因为替换不完整的话，体验会比较差。
        isAllLocalExit &=
            RIAIDPreloadResourceOperator.getPreloadExistsFile(replaceImage.imageAddress) != null;
      }
    }
    if (!isAllLocalExit && !mReplaceImageSupportNet) {
      if (mOnLottieImageReplaceCallback != null) {
        mOnLottieImageReplaceCallback.onLottieImageReplaceFail();
      }
      ADRenderLogger.w(
          RIAIDPreloadResourceOperator.PRELOAD_TAG + " Lottie 替换图片，不支持网络图片的情况下，有部分图片没本地加载出来");
      return;
    }
    // 替换图片需要动画开始后才能替换，不然会替换失败。
    // 日志为：Cannot update bitmap. Most likely the drawable is not added
    // to a View which prevents Lottie from getting a Context.
    mLottieAnimationView.addAnimatorListener(new DefaultAnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
        // 每次监听到动画开始播放，都要先暂停，然后替换图片，图片替换完成再重新播放
        mLottieAnimationView.pauseAnimation();
        for (LottieAttributes.ReplaceImage replaceImage : mReplaceImageList) {
          replaceImage(replaceImage.placeImageId, replaceImage.imageAddress);
        }
      }
    });
  }

  /**
   * 消费替换图片的次数，如果图片全部替换完了，再开始播放
   */
  private void consumeReplaceCount() {
    replaceCount--;
    if (replaceCount <= 0) {
      mLottieAnimationView.resumeAnimation();
      if (mOnLottieImageReplaceCallback != null) {
        mOnLottieImageReplaceCallback.onLottieImageReplaceSuccess();
      }
    }
  }


  /**
   * Lottie图片替换回调
   */
  public interface OnLottieImageReplaceCallback {
    /**
     * 图片替换成功
     */
    void onLottieImageReplaceSuccess();

    /**
     * 图片替换失败
     */
    void onLottieImageReplaceFail();
  }
}
