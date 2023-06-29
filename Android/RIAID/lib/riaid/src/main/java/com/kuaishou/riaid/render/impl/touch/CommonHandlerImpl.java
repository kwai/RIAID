package com.kuaishou.riaid.render.impl.touch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.impl.touch.gesture.GestureDetector;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.service.base.IResumeActionService;

public class CommonHandlerImpl implements GestureDetector.IHandlerListener {

  @NonNull
  private final UIModel.Handler mHandlerData;

  @NonNull
  private final UIModel.NodeContext mNodeContext;

  @Nullable
  private final IResumeActionService mResumeActionService;

  public CommonHandlerImpl(@NonNull UIModel.Handler handlerData,
      @NonNull UIModel.NodeContext nodeContext,
      @Nullable IResumeActionService resumeActionService) {
    this.mHandlerData = handlerData;
    this.mNodeContext = nodeContext;
    this.mResumeActionService = resumeActionService;
  }

  @Override
  public void onClick() {
    if (mHandlerData.click != null && mResumeActionService != null) {
      ADRenderLogger.i("key =  " + mNodeContext.key + " invalid action =  onClick");
      mResumeActionService.resumeRenderAction(IResumeActionService.ACTION_TYPE_CLICK,
          mNodeContext, mHandlerData.click);
    }
  }

  @Override
  public void onDoubleClick() {
    if (mHandlerData.doubleClick != null && mResumeActionService != null) {
      ADRenderLogger.i("key  = " + mNodeContext.key + " invalid action =  onDoubleClick");
      mResumeActionService.resumeRenderAction(IResumeActionService.ACTION_TYPE_DOUBLED_CLICK,
          mNodeContext, mHandlerData.doubleClick);
    }

  }

  @Override
  public void onLongPress() {
    if (mHandlerData.longPress != null && mResumeActionService != null) {
      ADRenderLogger.i("key = " + mNodeContext.key + " invalid action =  onLongPress");
      mResumeActionService.resumeRenderAction(IResumeActionService.ACTION_TYPE_LONG_PRESS,
          mNodeContext, mHandlerData.longPress);
    }
  }


}
