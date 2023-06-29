package com.kuaishou.riaid.render.lottie;

import android.graphics.Typeface;

import com.airbnb.lottie.FontAssetDelegate;

public class LottieFontAssetDelegate extends FontAssetDelegate {

  @Override
  public Typeface fetchFont(String fontFamily) {
    return Typeface.DEFAULT;
  }

  @Override
  public String getFontPath(String fontFamily) {
    return super.getFontPath(fontFamily);
  }
}
