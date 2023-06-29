package com.kuaishou.riaid.render.impl.touch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.impl.touch.gesture.GestureDetector;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;

/**
 * 按压态的通用逻辑
 */
public class CommonPressImpl implements GestureDetector.IPressListener {

  @Nullable
  private final GestureDetector.IPressListener mPressListener;

  @NonNull
  private final UIModel.NodeContext mContext;

  public CommonPressImpl(@NonNull UIModel.NodeContext context,
      @Nullable GestureDetector.IPressListener pressListener) {
    this.mContext = context;
    this.mPressListener = pressListener;
  }

  @Override
  public void onPressStart(boolean fromOutside) {
    if (mPressListener != null) {
      ADRenderLogger.i("key = " + mContext.key + " invalid onPressStart");
      mPressListener.onPressStart(fromOutside);
    }
  }

  @Override
  public void onPressEnd(boolean fromOutside) {
    if (mPressListener != null) {
      ADRenderLogger.i("key = " + mContext.key + " invalid onPressEnd");
      mPressListener.onPressEnd(fromOutside);
    }
  }
}
