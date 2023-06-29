package com.kuaishou.riaid.render.lottie;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.kuaishou.riaid.proto.nano.LottieAttributes;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * 用于替换Lottie中路径对应的颜色
 */
public class LottieColorDelegate {

  @NonNull
  private final LottieAnimationView mLottieAnimationView;
  private final List<LottieAttributes.ReplaceKeyPathColor> mReplaceKeyPathColors;
  @Nullable
  private final IServiceContainer mServiceContainer;

  public LottieColorDelegate(@NonNull LottieAnimationView lottieAnimationView,
      List<LottieAttributes.ReplaceKeyPathColor> replaceKeyPathColors,
      @Nullable IServiceContainer serviceContainer) {
    this.mLottieAnimationView = lottieAnimationView;
    this.mReplaceKeyPathColors = replaceKeyPathColors;
    this.mServiceContainer = serviceContainer;
  }

  public void replaceColor() {
    if (!ToolHelper.isListValid(mReplaceKeyPathColors)) {
      return;
    }
    mLottieAnimationView.addLottieOnCompositionLoadedListener(
        lottieComposition -> {
          for (LottieAttributes.ReplaceKeyPathColor replaceKeyPathColor : mReplaceKeyPathColors) {
            if (replaceKeyPathColor == null ||
                !ToolHelper.isArrayValid(replaceKeyPathColor.keyPath)) {
              continue;
            }
            int parseColor = ToolHelper.parseColor(mServiceContainer, replaceKeyPathColor.color, 0);
            if (parseColor == 0) {
              continue;
            }
            KeyPath keyPath = new KeyPath(replaceKeyPathColor.keyPath);
            mLottieAnimationView.addValueCallback(keyPath,
                //修改对应keyPath的填充色的属性值
                LottieProperty.COLOR,
                lottieFrameInfo -> parseColor);
          }
        });
  }
}
