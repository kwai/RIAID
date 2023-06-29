package com.kuaishou.riaid.render.lottie;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.TextDelegate;

public class LottieTextDelegate extends TextDelegate {

  public LottieTextDelegate(LottieAnimationView animationView) {
    super(animationView);
  }

  public LottieTextDelegate(LottieDrawable drawable) {
    super(drawable);
  }

  @Override
  public void setText(String input, String output) {
    super.setText(input, output);
  }
}
