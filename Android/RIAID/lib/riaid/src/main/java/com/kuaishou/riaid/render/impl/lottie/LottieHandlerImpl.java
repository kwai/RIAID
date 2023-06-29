package com.kuaishou.riaid.render.impl.lottie;

import android.animation.Animator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.impl.DefaultAnimatorListener;
import com.kuaishou.riaid.render.interf.IDispatchEventService;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.lottie.LottieImageDelegate;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.service.base.IResumeActionService;

/**
 * 这个是Lottie的Handler的实现类
 */
public class LottieHandlerImpl implements IDispatchEventService, DefaultAnimatorListener,
    LottieImageDelegate.OnLottieImageReplaceCallback {

  @Nullable
  private final UIModel.LottieHandler mLottieHandler;

  @NonNull
  private final UIModel.NodeContext mNodeContext;

  @Nullable
  private final IResumeActionService mResumeActionService;

  public LottieHandlerImpl(@Nullable UIModel.LottieHandler videoHandler,
      @NonNull UIModel.NodeContext nodeContext,
      @Nullable IResumeActionService resumeActionService) {
    this.mNodeContext = nodeContext;
    this.mLottieHandler = videoHandler;
    this.mResumeActionService = resumeActionService;
  }

  /**
   * 触发对应的事件
   *
   * @param eventType 当事件的类型，比如动画开始和结束等，参考{@link IResumeActionService}的常量
   */
  @Override
  public void dispatchEvent(int eventType) {
    if (mResumeActionService == null || mLottieHandler == null) {
      return;
    }
    switch (eventType) {
      case IResumeActionService.ACTION_TYPE_LOTTIE_START:
        resumeRenderAction(eventType, mLottieHandler.start);
        break;
      case IResumeActionService.ACTION_TYPE_LOTTIE_END:
        resumeRenderAction(eventType, mLottieHandler.end);
        break;
      case IResumeActionService.ACTION_TYPE_LOTTIE_REPLACE_IMAGE_SUCCESS:
        resumeRenderAction(eventType, mLottieHandler.replaceImageSuccess);
        break;
      case IResumeActionService.ACTION_TYPE_LOTTIE_REPLACE_IMAGE_FAIL:
        resumeRenderAction(eventType, mLottieHandler.replaceImageFail);
        break;
      default:
        ADRenderLogger.e("LottieHandlerImpl 无法识别的type类型 " + eventType);
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

  @Override
  public void onAnimationStart(Animator animation) {
    dispatchEvent(IResumeActionService.ACTION_TYPE_LOTTIE_START);
  }

  @Override
  public void onAnimationEnd(Animator animation) {
    dispatchEvent(IResumeActionService.ACTION_TYPE_LOTTIE_END);
  }

  @Override
  public void onLottieImageReplaceSuccess() {
    dispatchEvent(IResumeActionService.ACTION_TYPE_LOTTIE_REPLACE_IMAGE_SUCCESS);
  }

  @Override
  public void onLottieImageReplaceFail() {
    dispatchEvent(IResumeActionService.ACTION_TYPE_LOTTIE_REPLACE_IMAGE_FAIL);
  }
}
