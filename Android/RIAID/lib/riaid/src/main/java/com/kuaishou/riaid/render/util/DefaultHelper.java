package com.kuaishou.riaid.render.util;

import android.graphics.Color;

import com.kuaishou.riaid.proto.nano.Gradient;
import com.kuaishou.riaid.proto.nano.ImageAttributes;
import com.kuaishou.riaid.proto.nano.LottieAttributes;
import com.kuaishou.riaid.proto.nano.RIAIDConstants;
import com.kuaishou.riaid.proto.nano.TextAttributes;
import com.kuaishou.riaid.proto.nano.VideoAttributes;

/**
 * 这里是来处理默认值的
 */
public class DefaultHelper {

  public static final int UNSPECIFIED = Integer.MAX_VALUE;
  public static final int DEFAULT_MAX_LINES = Integer.MAX_VALUE;
  public static final int DEFAULT_COLOR =
      ToolHelper.parseColor(null, RIAIDConstants.Render.DEFAULT_COLOR, Color.TRANSPARENT);
  public static final int DEFAULT_FONT_COLOR =
      ToolHelper.parseColor(null, RIAIDConstants.Render.DEFAULT_FONT_COLOR, Color.BLACK);


  public static int defaultTextHorizontalMode(int mode) {
    int result;
    switch (mode) {
      case TextAttributes.Align.HORIZONTAL_END:
      case TextAttributes.Align.HORIZONTAL_CENTER:
      case TextAttributes.Align.HORIZONTAL_START:
        result = mode;
        break;
      default:
        result = RIAIDConstants.Render.DEFAULT_TEXT_HORIZONTAL_MODE;
        break;
    }
    return result;
  }

  public static int defaultTextVerticalMode(int mode) {
    int result;
    switch (mode) {
      case TextAttributes.Align.VERTICAL_TOP:
      case TextAttributes.Align.VERTICAL_CENTER:
      case TextAttributes.Align.VERTICAL_BOTTOM:
        result = mode;
        break;
      default:
        result = RIAIDConstants.Render.DEFAULT_TEXT_VERTICAL_MODE;
        break;
    }
    return result;
  }

  public static int defaultGradientAngle(int angle) {
    int result;
    switch (angle) {
      case Gradient.ANGLE_0:
      case Gradient.ANGLE_45:
      case Gradient.ANGLE_90:
      case Gradient.ANGLE_135:
      case Gradient.ANGLE_180:
      case Gradient.ANGLE_225:
      case Gradient.ANGLE_270:
      case Gradient.ANGLE_315:
        result = angle;
        break;
      default:
        result = RIAIDConstants.Render.DEFAULT_GRADIENT_ANGLE;
        break;
    }
    return result;
  }

  public static int defaultScaleType(int scaleType) {
    int result;
    switch (scaleType) {
      case ImageAttributes.SCALE_TYPE_FIT_XY:
      case ImageAttributes.SCALE_TYPE_FIT_END:
      case ImageAttributes.SCALE_TYPE_FIT_START:
      case ImageAttributes.SCALE_TYPE_FIT_CENTER:
      case ImageAttributes.SCALE_TYPE_CENTER:
      case ImageAttributes.SCALE_TYPE_CENTER_CROP:
        result = scaleType;
        break;
      default:
        result = RIAIDConstants.Render.DEFAULT_IMAGE_SCALE_TYPE;
        break;
    }
    return result;
  }

  public static int defaultLottieRepeatMode(int repeatMode) {
    int result;
    switch (repeatMode) {
      case LottieAttributes.REPEAT_MODE_REVERSE:
      case LottieAttributes.REPEAT_MODE_RESTART:
        result = repeatMode;
        break;
      default:
        result = RIAIDConstants.Render.DEFAULT_LOTTIE_REPEAT_MODE;
        break;
    }
    return result;
  }

  public static int defaultViewAdapterType(int adapterType) {
    int result;
    switch (adapterType) {
      case VideoAttributes.ADAPTER_TYPE_CENTER_CROP:
      case VideoAttributes.ADAPTER_TYPE_AUTO:
      case VideoAttributes.ADAPTER_TYPE_INSPIRE:
        result = adapterType;
        break;
      default:
        result = RIAIDConstants.Render.DEFAULT_VIDEO_ADAPTER_TYPE;
        break;
    }
    return result;
  }

  public static int defaultRichTextAlign(int richAlignMode) {
    int result;
    switch (richAlignMode) {
      case TextAttributes.RichText.RICH_ALIGN_BOTTOM:
      case TextAttributes.RichText.RICH_ALIGN_CENTER:
        result = richAlignMode;
        break;
      default:
        result = TextAttributes.RichText.RICH_ALIGN_CENTER;
        break;
    }
    return result;
  }

}
