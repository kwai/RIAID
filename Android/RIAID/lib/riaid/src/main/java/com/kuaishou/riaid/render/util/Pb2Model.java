package com.kuaishou.riaid.render.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieDrawable;
import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.proto.nano.ButtonAttributes;
import com.kuaishou.riaid.proto.nano.CommonAttributes;
import com.kuaishou.riaid.proto.nano.CornerRadius;
import com.kuaishou.riaid.proto.nano.Gradient;
import com.kuaishou.riaid.proto.nano.Handler;
import com.kuaishou.riaid.proto.nano.ImageAttributes;
import com.kuaishou.riaid.proto.nano.Layout;
import com.kuaishou.riaid.proto.nano.LottieAttributes;
import com.kuaishou.riaid.proto.nano.LottieHandler;
import com.kuaishou.riaid.proto.nano.RIAIDConstants;
import com.kuaishou.riaid.proto.nano.Responder;
import com.kuaishou.riaid.proto.nano.ScrollAttributes;
import com.kuaishou.riaid.proto.nano.TextAttributes;
import com.kuaishou.riaid.proto.nano.VideoAttributes;
import com.kuaishou.riaid.proto.nano.VideoHandler;
import com.kuaishou.riaid.render.config.DSLRenderCore;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.item.ImageItemNode;
import com.kuaishou.riaid.render.node.item.LottieItemNode;
import com.kuaishou.riaid.render.node.item.TextItemNode;
import com.kuaishou.riaid.render.node.item.VideoItemNode;
import com.kuaishou.riaid.render.node.layout.ButtonLayoutNode;
import com.kuaishou.riaid.render.service.base.IDataBindingService;

/**
 * 这里负责PB的model到渲染Model的装换
 */
public class Pb2Model {

  /**
   * 解析layout布局属性
   *
   * @param context  context
   * @param pbLayout layout的布局属性pb对象
   * @return 返回渲染UI的Layout-Model对象
   */
  @NonNull
  public static UIModel.Layout transformLayout(@NonNull Context context,
      @Nullable Layout pbLayout) {
    UIModel.Layout layout = new UIModel.Layout();
    if (pbLayout != null) {
      copyLayout(context, layout, pbLayout);
    }
    return layout;
  }

  /**
   * 解析handler字段
   *
   * @param handlerPb pb的handler属性对象
   * @return 返回装换好的UI的handler对象，如果actionPb不为空的话
   */
  @Nullable
  public static UIModel.Handler transformHandler(
      @Nullable Handler handlerPb) {
    if (handlerPb != null) {
      UIModel.Handler handler = new UIModel.Handler();
      handler.click = transformResponder(handlerPb.click);
      handler.longPress = transformResponder(handlerPb.longPress);
      handler.doubleClick = transformResponder(handlerPb.doubleClick);
      return handler;
    }
    return null;
  }

  /**
   * 解析video_handler字段
   *
   * @param handlerPb pb的video_handler属性对象
   * @return 返回装换好的UI的video_handler对象，如果actionPb不为空的话
   */
  @Nullable
  public static UIModel.VideoHandler transformVideoHandler(
      @Nullable VideoHandler handlerPb) {
    if (handlerPb != null) {
      UIModel.VideoHandler handler = new UIModel.VideoHandler();
      handler.impression = transformResponder(handlerPb.impression);
      handler.finish = transformResponder(handlerPb.finish);
      handler.pause = transformResponder(handlerPb.pause);
      handler.start = transformResponder(handlerPb.start);
      handler.resume = transformResponder(handlerPb.resume);
      return handler;
    }
    return null;
  }


  /**
   * 解析lottie_handler字段
   *
   * @param handlerPb pb的lottie_handler属性对象
   * @return 返回装换好的UI的lottie_handler对象，如果actionPb不为空的话
   */
  @Nullable
  public static UIModel.LottieHandler transformLottieHandler(
      @Nullable LottieHandler handlerPb) {
    if (handlerPb != null) {
      UIModel.LottieHandler handler = new UIModel.LottieHandler();
      handler.start = transformResponder(handlerPb.start);
      handler.end = transformResponder(handlerPb.end);
      handler.replaceImageSuccess = transformResponder(handlerPb.replaceImageSuccess);
      handler.replaceImageFail = transformResponder(handlerPb.replaceImageFalse);
      return handler;
    }
    return null;
  }

  /**
   * responder
   */
  @Nullable
  private static UIModel.Responder transformResponder(
      @Nullable Responder responderPb) {
    if (responderPb != null) {
      int[] triggers = responderPb.triggerKeys;
      if (triggers != null && triggers.length > 0) {
        UIModel.Responder responder = new UIModel.Responder();
        responder.triggers = triggers;
        return responder;
      }
    }
    return null;
  }

  /**
   * 拷贝layout属性
   *
   * @param context  context
   * @param layout   需要被赋值的目标属性layout对象
   * @param layoutPb pb的layout属性对象
   */
  private static void copyLayout(@NonNull Context context, @NonNull UIModel.Layout layout,
      @NonNull Layout layoutPb) {
    layout.weight = layoutPb.weight;
    layout.priority = layoutPb.priority;
    copyEdge(context, layout.margin, layoutPb.margin);
    copyEdge(context, layout.padding, layoutPb.padding);
    layout.width = LayoutPerformer.getSize(context, layoutPb.width);
    layout.height = LayoutPerformer.getSize(context, layoutPb.height);
    layout.maxWidth = layoutPb.maxWidth == null ? DefaultHelper.UNSPECIFIED
        : LayoutPerformer.getSize(context, layoutPb.maxWidth.value);
    layout.maxHeight = layoutPb.maxHeight == null ? DefaultHelper.UNSPECIFIED
        : LayoutPerformer.getSize(context, layoutPb.maxHeight.value);
  }

  /**
   * 解析Edge
   *
   * @param context   context
   * @param edgeAttrs 需要被复制的目标属性对象
   * @param edgePb    pb对象属性对象
   */
  private static void copyEdge(@NonNull Context context, @NonNull UIModel.Edge edgeAttrs,
      @Nullable Layout.Edge edgePb) {
    if (edgePb != null) {
      edgeAttrs.top = ToolHelper.dip2px(context, edgePb.top);
      edgeAttrs.bottom = ToolHelper.dip2px(context, edgePb.bottom);
      edgeAttrs.start = ToolHelper.dip2px(context, edgePb.start);
      edgeAttrs.end = ToolHelper.dip2px(context, edgePb.end);
    }
  }

  /**
   * 解析转换成渲染用的image属性对象
   *
   * @param context          context
   * @param serviceContainer 这个提供数据绑定解析的service实例，可能为空
   * @param imageAttrs       渲染用的image属性对象
   * @param imagePb          pb的图片属性对象
   */
  public static void transformImageAttrs(@NonNull Context context,
      @Nullable IServiceContainer serviceContainer, @NonNull ImageItemNode.ImageAttrs imageAttrs,
      @NonNull ImageAttributes imagePb) {
    if (serviceContainer == null) {
      return;
    }
    IDataBindingService service = serviceContainer.getService(IDataBindingService.class);
    boolean isRTL = ToolHelper.isRtlByLocale();
    String url = ToolHelper.resolveValue(service, imagePb.url);
    String rtlUrl = ToolHelper.resolveValue(service, imagePb.rtlUrl);
    String highlightUrl = ToolHelper.resolveValue(service, imagePb.highlightUrl);
    String rtlHighlightUrl = ToolHelper.resolveValue(service, imagePb.rtlHighlightUrl);

    imageAttrs.scaleType = imageScaleType(imagePb.scaleType);
    imageAttrs.imageUrl = !TextUtils.isEmpty(rtlUrl) && isRTL ? rtlUrl : url;
    imageAttrs.colorFilter = ToolHelper.parseColor(serviceContainer, imagePb.colorFilter, 0);
    imageAttrs.highlightUrl =
        !TextUtils.isEmpty(rtlHighlightUrl) && isRTL ? rtlHighlightUrl : highlightUrl;
  }

  /**
   * 解析转换成渲染用的lottie属性对象
   *
   * @param context     context
   * @param lottieAttrs 渲染用的lottie属性对象
   * @param lottiePb    源Pb属性对象
   */
  public static void transformLottieAttrs(@NonNull Context context,
      @Nullable IDataBindingService service, @NonNull LottieItemNode.LottieAttrs lottieAttrs,
      @NonNull LottieAttributes lottiePb) {
    lottieAttrs.speed =
        lottiePb.speed != null ? lottiePb.speed.value : RIAIDConstants.Render.DEFAULT_LOTTIE_SPEED;
    lottieAttrs.progress = lottiePb.progress != null ? lottiePb.progress.value : 0.0F;
    lottieAttrs.autoPlay = lottiePb.autoPlay != null && lottiePb.autoPlay.value;
    lottieAttrs.repeat = lottiePb.repeat != null && lottiePb.repeat.value;
    lottieAttrs.repeatMode = repeatMode(lottiePb.repeatMode);
    lottieAttrs.url = ToolHelper.resolveValue(service, lottiePb.url);
    lottieAttrs.replaceTextList = transformReplaceTextList(lottiePb.replaceTextList);
    lottieAttrs.replaceImageList = transformReplaceImageList(lottiePb.replaceImageList);
    lottieAttrs.replaceKeyPathColorList =
        transformReplaceColorList(lottiePb.replaceKeyPathColorList);
    lottieAttrs.scaleType = lottieScaleType(lottiePb.scaleType);
    lottieAttrs.replaceImageSupportNet = lottiePb.replaceImageSupportNet;
  }

  /**
   * 解析转换成渲染用的video属性对象
   *
   * @param context    context
   * @param videoAttrs 渲染用的video属性对象
   * @param videoPb    源Pb属性对象
   */
  public static void transformVideoAttrs(@NonNull Context context,
      @Nullable IDataBindingService service, @NonNull VideoItemNode.VideoAttrs videoAttrs,
      @NonNull VideoAttributes videoPb) {
    videoAttrs.opaque = videoPb.opaque != null && videoPb.opaque.value;
    videoAttrs.autoMute = videoPb.autoMute != null && videoPb.autoMute.value;
    videoAttrs.autoLoop = videoPb.autoLoop != null && videoPb.autoLoop.value;
    videoAttrs.autoPlay = videoPb.autoPlay != null && videoPb.autoPlay.value;
    videoAttrs.autoSeekTime = videoPb.autoSeekTime;
    videoAttrs.coverUrl = ToolHelper.resolveValue(service, videoPb.coverUrl);
    videoAttrs.videoUrl = ToolHelper.resolveValue(service, videoPb.url);
    videoAttrs.manifest = ToolHelper.resolveValue(service, videoPb.manifest);
    videoAttrs.adapterType = DefaultHelper.defaultViewAdapterType(videoPb.adapterType);
  }

  /**
   * 解析lottie需要替换的文本
   *
   * @param replaceTextList 原始数据源，替换文本对象集合
   * @return 返回处理好的数据源集合
   */
  @Nullable
  private static List<LottieAttributes.ReplaceText> transformReplaceTextList(
      LottieAttributes.ReplaceText[] replaceTextList) {
    if (ToolHelper.isArrayValid(replaceTextList)) {
      List<LottieAttributes.ReplaceText> resultList = new ArrayList<>();
      for (LottieAttributes.ReplaceText replaceText : replaceTextList) {
        // 做一次校验吧，只有数据都合法才会考虑替换
        if (!TextUtils.isEmpty(replaceText.realText) &&
            !TextUtils.isEmpty(replaceText.placeHolder)) {
          resultList.add(replaceText);
        }
      }
      return resultList;
    }
    return null;
  }


  /**
   * 解析lottie需要替换的图片
   *
   * @param replaceImages 原始数据源，替换图片对象集合
   * @return 返回处理好的数据源集合
   */
  @Nullable
  private static List<LottieAttributes.ReplaceImage> transformReplaceImageList(
      LottieAttributes.ReplaceImage[] replaceImages) {
    if (ToolHelper.isArrayValid(replaceImages)) {
      List<LottieAttributes.ReplaceImage> resultList = new ArrayList<>();
      for (LottieAttributes.ReplaceImage replaceImage : replaceImages) {
        // 做一次校验吧，只有数据都合法才会考虑替换
        if (!TextUtils.isEmpty(replaceImage.imageAddress) &&
            !TextUtils.isEmpty(replaceImage.placeImageId)) {
          resultList.add(replaceImage);
        }
      }
      return resultList;
    }
    return null;
  }


  /**
   * 解析lottie需要替换的颜色
   *
   * @param replaceKeyPathColors 原始数据源，替换颜色对象集合
   * @return 返回处理好的数据源集合
   */
  @Nullable
  private static List<LottieAttributes.ReplaceKeyPathColor> transformReplaceColorList(
      LottieAttributes.ReplaceKeyPathColor[] replaceKeyPathColors) {
    if (ToolHelper.isArrayValid(replaceKeyPathColors)) {
      List<LottieAttributes.ReplaceKeyPathColor> resultList = new ArrayList<>();
      for (LottieAttributes.ReplaceKeyPathColor replaceKeyPathColor : replaceKeyPathColors) {
        // 做一次校验吧，只有数据都合法才会考虑替换
        if (ToolHelper.isArrayValid(replaceKeyPathColor.keyPath) &&
            !TextUtils.isEmpty(replaceKeyPathColor.color)) {
          resultList.add(replaceKeyPathColor);
        }
      }
      return resultList;
    }
    return null;
  }

  /**
   * repeat-mode
   */
  private static int repeatMode(int repeatMode) {
    int newRepeatMode = DefaultHelper.defaultLottieRepeatMode(repeatMode);
    return newRepeatMode == LottieAttributes.REPEAT_MODE_REVERSE ?
        LottieDrawable.REVERSE : LottieDrawable.RESTART;
  }

  /**
   * 解析转换成渲染用的text属性对象
   *
   * @param context          context
   * @param serviceContainer 这个是外界提供的service能力的包装类
   * @param textAttrs        渲染用的text属性对象
   * @param textPb           源Pb属性对象
   */
  public static void transformTextAttrs(@NonNull Context context,
      @NonNull IServiceContainer serviceContainer, @NonNull UIModel.Size decorSize,
      @NonNull TextItemNode.TextAttrs textAttrs,
      @NonNull TextAttributes textPb) {
    textAttrs.size =
        textPb.fontSize != null ? textPb.fontSize.value : RIAIDConstants.Render.DEFAULT_FONT_SIZE;
    textAttrs.maxLines =
        textPb.maxLines != null ? textPb.maxLines.value : DefaultHelper.DEFAULT_MAX_LINES;
    textAttrs.isBold = textPb.bold != null && textPb.bold.value;
    textAttrs.isTilt = textPb.tilt != null && textPb.tilt.value;
    textAttrs.fontName = textPb.fontName;
    textAttrs.alignMode = align(textPb.align);
    textAttrs.lineMode = lineMode(textPb.lineMode);
    Integer highlightColor = ToolHelper.parseColor(textPb.highlightColor);
    textAttrs.color =
        ToolHelper.parseColor(serviceContainer, textPb.fontColor, DefaultHelper.DEFAULT_FONT_COLOR);
    if (highlightColor != null) {
      textAttrs.highlightColor = highlightColor;
    }
    textAttrs.ellipsizeMode = textEllipsizeMode(textPb.ellipsize);
    IDataBindingService service = serviceContainer.getService(IDataBindingService.class);
    textAttrs.text = ToolHelper.resolveValue(service, textPb.text);
    if (textPb.lineSpace != null) {
      textAttrs.lineSpaceExtra = ToolHelper.dip2px(context, textPb.lineSpace.value);
    }
    if (textPb.lineHeight != null) {
      textAttrs.lineHeight = ToolHelper.dip2px(context, textPb.lineHeight.value);
    }
    // 解析富文本
    textAttrs.richTextList =
        richText(context, serviceContainer, decorSize, Arrays.asList(textPb.richList));
  }

  /**
   * 解析转换成渲染用的button属性对象
   * 为了实现接口的复用，都转成Json吧
   *
   * @param context          context
   * @param decorSize        这个是外界约束的render的大小，也可以理解是画布的大小
   * @param serviceContainer 外界提供的service能力
   * @param buttonAttrs      渲染用的button属性对象
   * @param buttonPb         源Pb属性对象
   * @param nodeCacheMap     这个map是用来映射key和node的
   */
  public static void transformButtonAttrs(@NonNull Context context,
      @NonNull UIModel.Size decorSize,
      @NonNull IServiceContainer serviceContainer,
      @NonNull ButtonLayoutNode.ButtonAttrs buttonAttrs,
      @NonNull ButtonAttributes buttonPb,
      @NonNull Map<Integer, AbsObjectNode<?>> nodeCacheMap) {
    buttonAttrs.contentNode = DSLRenderCore.createInstance()
        .parsePbSourceData(context, serviceContainer, decorSize, buttonPb.content, nodeCacheMap);
    buttonAttrs.pressStateList = Arrays.asList(buttonPb.highlightStateList);
  }

  /**
   * 解析转换成渲染用的scroll属性对象
   *
   * @param context     context
   * @param scrollAttrs 渲染用的scroll属性对象
   * @param scrollPb    源Pb属性对象
   */
  public static void transformScrollAttrs(@NonNull Context context,
      @NonNull UIModel.ScrollAttrs scrollAttrs, @NonNull ScrollAttributes scrollPb) {
    scrollAttrs.showScrollBar = scrollPb.showScrollbar != null && scrollPb.showScrollbar.value;
  }

  /**
   * 解析align
   */
  public static int align(@Nullable TextAttributes.Align align) {
    if (align != null) {
      int result;
      int hMode = DefaultHelper.defaultTextHorizontalMode(align.horizontal);
      int vMode = DefaultHelper.defaultTextVerticalMode(align.vertical);
      switch (hMode) {
        case TextAttributes.Align.HORIZONTAL_END:
          result = Gravity.END;
          break;
        case TextAttributes.Align.HORIZONTAL_CENTER:
          result = Gravity.CENTER_HORIZONTAL;
          break;
        default:
          result = Gravity.START;
          break;
      }
      switch (vMode) {
        case TextAttributes.Align.VERTICAL_BOTTOM:
          result |= Gravity.BOTTOM;
          break;
        case TextAttributes.Align.VERTICAL_CENTER:
          result |= Gravity.CENTER_VERTICAL;
          break;
        default:
          result |= Gravity.TOP;
          break;
      }
      return result;
    }
    return Gravity.START | Gravity.TOP;
  }

  /**
   * 解析ellipsize
   */
  @Nullable
  private static TextUtils.TruncateAt textEllipsizeMode(int ellipsizeMode) {
    TextUtils.TruncateAt result;
    switch (ellipsizeMode) {
      case TextAttributes.ELLIPSIZE_START:
        result = TextUtils.TruncateAt.START;
        break;
      case TextAttributes.ELLIPSIZE_MIDDLE:
        result = TextUtils.TruncateAt.MIDDLE;
        break;
      case TextAttributes.ELLIPSIZE_END:
        result = TextUtils.TruncateAt.END;
        break;
      default:
        // 默认正常截断即可
        result = null;
        break;
    }
    return result;
  }

  /**
   * 解析lineMode
   */
  public static int lineMode(int lineMode) {
    int result;
    switch (lineMode) {
      case TextAttributes.LINE_MODE_UNDERLINE:
        result = Paint.UNDERLINE_TEXT_FLAG;
        break;
      case TextAttributes.LINE_MODE_STRIKE_THRU:
        result = Paint.STRIKE_THRU_TEXT_FLAG;
        break;
      default:
        result = Paint.ANTI_ALIAS_FLAG;
        break;
    }
    return result;
  }

  /**
   * 解析rich
   */
  @Nullable
  private static List<UIModel.RichText> richText(@NonNull Context context,
      @NonNull IServiceContainer serviceContainer, @NonNull UIModel.Size decorSize,
      @Nullable List<TextAttributes.RichText> richPbList) {
    if (ToolHelper.isListValid(richPbList)) {
      List<UIModel.RichText> resultList = new ArrayList<>();
      UIModel.RichText richText;
      IDataBindingService service = serviceContainer.getService(IDataBindingService.class);
      for (TextAttributes.RichText richPb : richPbList) {
        boolean isMatchValid = !TextUtils.isEmpty(richPb.placeHolder);
        boolean isContentValid = richPb.content != null;
        if (isMatchValid && isContentValid) {
          // 这个时候才有富文本的可能
          richText = new UIModel.RichText();
          richText.richContent = richPb.content;
          richText.placeHolder =
              service == null ? richPb.placeHolder
                  : ToolHelper.resolveValue(service, richPb.placeHolder);
          richText.handler = richPb.handler != null ? transformHandler(richPb.handler) : null;
          richText.richAlignMode = DefaultHelper.defaultRichTextAlign(richPb.richAlign);
          resultList.add(richText);
        }
      }
      return resultList;
    }
    return null;
  }


  /**
   * image scale-type
   */
  @NonNull
  private static ImageView.ScaleType imageScaleType(int scaleType) {
    ImageView.ScaleType result;
    int newScaleType = DefaultHelper.defaultScaleType(scaleType);
    switch (newScaleType) {
      case ImageAttributes.SCALE_TYPE_FIT_XY:
        result = ImageView.ScaleType.FIT_XY;
        break;
      case ImageAttributes.SCALE_TYPE_FIT_END:
        result = ImageView.ScaleType.FIT_END;
        break;
      case ImageAttributes.SCALE_TYPE_FIT_START:
        result = ImageView.ScaleType.FIT_START;
        break;
      case ImageAttributes.SCALE_TYPE_FIT_CENTER:
        result = ImageView.ScaleType.FIT_CENTER;
        break;
      case ImageAttributes.SCALE_TYPE_CENTER:
        result = ImageView.ScaleType.CENTER;
        break;
      default:
        result = ImageView.ScaleType.CENTER_CROP;
        break;
    }
    return result;
  }


  /**
   * lottie scale-type
   */
  @NonNull
  private static ImageView.ScaleType lottieScaleType(int scaleType) {
    ImageView.ScaleType result;
    int newScaleType = DefaultHelper.defaultScaleType(scaleType);
    switch (newScaleType) {
      case LottieAttributes.SCALE_TYPE_FIT_XY:
        result = ImageView.ScaleType.FIT_XY;
        break;
      case LottieAttributes.SCALE_TYPE_FIT_END:
        result = ImageView.ScaleType.FIT_END;
        break;
      case LottieAttributes.SCALE_TYPE_FIT_START:
        result = ImageView.ScaleType.FIT_START;
        break;
      case LottieAttributes.SCALE_TYPE_FIT_CENTER:
        result = ImageView.ScaleType.FIT_CENTER;
        break;
      case LottieAttributes.SCALE_TYPE_CENTER:
        result = ImageView.ScaleType.CENTER;
        break;
      default:
        result = ImageView.ScaleType.CENTER_CROP;
        break;
    }
    return result;
  }

  /**
   * 拷贝common相同属性
   *
   * @param serviceContainer
   * @param context          context
   * @param attrs            目标属性对象，被赋值的ui对象
   * @param attrsPb          源属性对象，pb的model对象
   */
  public static void copyAttrs(IServiceContainer serviceContainer,
      @NonNull Context context, @NonNull UIModel.Attrs attrs,
      @NonNull CommonAttributes attrsPb) {
    attrs.alpha = attrsPb.alpha != null ? attrsPb.alpha.value : RIAIDConstants.Render.DEFAULT_ALPHA;
    if (attrsPb.shadow != null) {
      attrs.shadow = createShadow(serviceContainer, context, attrsPb);
    }
    if (attrsPb.hidden != null) {
      attrs.hidden = attrsPb.hidden.value;
    }
    attrs.backgroundDrawable = createDrawable(serviceContainer, context, attrsPb);
    if (attrsPb.cornerRadius != null) {
      attrs.cornerRadius = new UIModel.CornerRadius();
      copyCorner(serviceContainer, context, attrs.cornerRadius, attrsPb.cornerRadius);
    }
  }

  /**
   * 创建drawable
   */
  @Nullable
  private static Drawable createDrawable(
      IServiceContainer serviceContainer, @NonNull Context context,
      @Nullable CommonAttributes attrsPb) {
    // shapeType必须是有效的，不然不好操作呀，兄弟
    if (attrsPb != null && attrsPb.shapeType != CommonAttributes.SHAPE_TYPE_UNKNOWN) {
      GradientDrawable drawable = new GradientDrawable();
      // 目前只支持一种
      drawable.setShape(shapeDrawableType(attrsPb.shapeType));
      // 设置color
      if (attrsPb.cornerRadius != null) {
        CornerRadius radiusPb = attrsPb.cornerRadius;
        UIModel.CornerRadius radius = new UIModel.CornerRadius();
        copyCorner(serviceContainer, context, radius, radiusPb);
        drawable.setCornerRadii(new float[]{
            radius.topLeft, radius.topLeft, radius.topRight, radius.topRight,
            radius.bottomRight, radius.bottomRight, radius.bottomLeft, radius.bottomLeft,
        });
      }
      // 设置solid
      if (!TextUtils.isEmpty(attrsPb.backgroundColor)) {
        drawable
            .setColor(ToolHelper.parseColor(serviceContainer, attrsPb.backgroundColor,
                DefaultHelper.DEFAULT_COLOR));
      }
      // 设置stroke
      if (attrsPb.stroke != null) {
        drawable.setStroke(
            ToolHelper.dip2px(context, attrsPb.stroke.width),
            ToolHelper.parseColor(serviceContainer, attrsPb.stroke.color,
                DefaultHelper.DEFAULT_COLOR),
            ToolHelper.dip2px(context, attrsPb.stroke.dashWidth),
            ToolHelper.dip2px(context, attrsPb.stroke.dashGap));
      }
      if (attrsPb.gradient != null && attrsPb.gradient.type != Gradient.GRADIENT_TYPE_UNKNOWN) {
        Gradient gradient = attrsPb.gradient;
        // 目前也是支持一种
        drawable.setGradientType(shapeDrawableGradientType(gradient.type));
        if (ToolHelper.isArrayValid(gradient.colors)) {
          int[] colors = new int[gradient.colors.length];
          for (int i = 0; i < gradient.colors.length; i++) {
            colors[i] = ToolHelper.parseColor(serviceContainer, gradient.colors[i],
                DefaultHelper.DEFAULT_COLOR);
          }
          drawable.setColors(colors);
        }
        drawable.setOrientation(shapeDrawableOrientationType(gradient.angle));
      }
      return drawable;
    }
    return null;
  }

  /**
   * 获取具体的shape-type
   */
  private static int shapeDrawableType(int type) {
    return GradientDrawable.RECTANGLE;
  }

  /**
   * 获取GradientType
   */
  private static int shapeDrawableGradientType(int type) {
    return GradientDrawable.LINEAR_GRADIENT;
  }

  /**
   * 获取渐变方向
   */
  @NonNull
  private static GradientDrawable.Orientation shapeDrawableOrientationType(int type) {
    GradientDrawable.Orientation resultType;
    int newType = DefaultHelper.defaultGradientAngle(type);
    switch (newType) {
      case Gradient.ANGLE_45:
        resultType = GradientDrawable.Orientation.BL_TR;
        break;
      case Gradient.ANGLE_90:
        resultType = GradientDrawable.Orientation.BOTTOM_TOP;
        break;
      case Gradient.ANGLE_135:
        resultType = GradientDrawable.Orientation.BR_TL;
        break;
      case Gradient.ANGLE_270:
        resultType = GradientDrawable.Orientation.TOP_BOTTOM;
        break;
      case Gradient.ANGLE_180:
        resultType = GradientDrawable.Orientation.RIGHT_LEFT;
        break;
      case Gradient.ANGLE_225:
        resultType = GradientDrawable.Orientation.TR_BL;
        break;
      case Gradient.ANGLE_315:
        resultType = GradientDrawable.Orientation.TL_BR;
        break;
      default:
        resultType = GradientDrawable.Orientation.LEFT_RIGHT;
        break;
    }
    return resultType;
  }

  /**
   * 创建shadow
   */
  @Nullable
  public static UIModel.Shadow createShadow(
      IServiceContainer serviceContainer, @NonNull Context context,
      @NonNull CommonAttributes attrsPb) {
    if (attrsPb.shadow != null) {
      UIModel.Shadow shadow = new UIModel.Shadow();
      shadow.color = ToolHelper.parseColor(serviceContainer, attrsPb.shadow.color,
          DefaultHelper.DEFAULT_COLOR);
      shadow.offsetX = ToolHelper.dip2px(context, attrsPb.shadow.offsetX);
      shadow.offsetY = ToolHelper.dip2px(context, attrsPb.shadow.offsetY);
      shadow.radius = ToolHelper.dip2px(context, attrsPb.shadow.radius);
      // 圆角数据同样需要解析保存
      if (attrsPb.cornerRadius != null) {
        copyCorner(serviceContainer, context, shadow.cornerRadius, attrsPb.cornerRadius);
      }
      return shadow;
    }
    return null;
  }

  /**
   * 解析圆角
   */
  private static void copyCorner(
      IServiceContainer serviceContainer, @NonNull Context context,
      @NonNull UIModel.CornerRadius radius, @Nullable CornerRadius radiusPb) {
    if (radiusPb != null) {
      boolean isRTL = ToolHelper.isRtlByLocale();
      int topEnd = ToolHelper.dip2px(context, radiusPb.topEnd);
      int topStart = ToolHelper.dip2px(context, radiusPb.topStart);
      int bottomEnd = ToolHelper.dip2px(context, radiusPb.bottomEnd);
      int bottomStart = ToolHelper.dip2px(context, radiusPb.bottomStart);
      radius.topRight = isRTL ? topStart : topEnd;
      radius.topLeft = isRTL ? topEnd : topStart;
      radius.bottomRight = isRTL ? bottomStart : bottomEnd;
      radius.bottomLeft = isRTL ? bottomEnd : bottomStart;
    }
  }


  ////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * press-lottie
   */
  @Nullable
  public static LottieItemNode.LottieAttrs pressLottieAttrs(@NonNull Context context,
      @NonNull IServiceContainer serviceContainer,
      @NonNull LottieItemNode.LottieAttrs lottieAttrs, @Nullable Attributes attributes) {
    if (attributes != null && (attributes.common != null || attributes.lottie != null)) {
      LottieItemNode.LottieAttrs copyAttrs = new LottieItemNode.LottieAttrs();
      pressAttrs(serviceContainer, context, lottieAttrs, copyAttrs, attributes);
      LottieAttributes lottiePb = attributes.lottie;
      boolean lottieValid = attributes.lottie != null;
      IDataBindingService service = serviceContainer.getService(IDataBindingService.class);

      if (lottieValid && !TextUtils.isEmpty(lottiePb.url)) {
        copyAttrs.url =
            service != null ? ToolHelper.resolveValue(service, lottiePb.url) : lottiePb.url;
      } else {
        copyAttrs.url = lottieAttrs.url;
      }
      if (lottieValid && lottiePb.speed != null) {
        copyAttrs.speed = lottiePb.speed.value;
      } else {
        copyAttrs.speed = lottieAttrs.speed;
      }
      if (lottieValid && lottiePb.progress != null) {
        copyAttrs.progress = lottiePb.progress.value;
      } else {
        copyAttrs.progress = lottieAttrs.progress;
      }
      if (lottieValid && lottiePb.repeat != null) {
        copyAttrs.repeat = lottiePb.repeat.value;
      } else {
        copyAttrs.repeat = lottieAttrs.repeat;
      }
      if (lottieValid && lottiePb.repeatMode != LottieAttributes.REPEAT_MODE_UNKNOWN) {
        copyAttrs.repeatMode = repeatMode(lottiePb.repeatMode);
      } else {
        copyAttrs.repeatMode = lottieAttrs.repeatMode;
      }
      if (lottieValid && lottiePb.autoPlay != null) {
        copyAttrs.autoPlay = lottiePb.autoPlay.value;
      } else {
        copyAttrs.autoPlay = lottieAttrs.autoPlay;
      }
      if (lottieValid && ToolHelper.isArrayValid(lottiePb.replaceTextList)) {
        copyAttrs.replaceTextList = transformReplaceTextList(lottiePb.replaceTextList);
      } else {
        copyAttrs.replaceTextList = lottieAttrs.replaceTextList;
      }
      if (lottieValid && ToolHelper.isArrayValid(lottiePb.replaceImageList)) {
        copyAttrs.replaceImageList = transformReplaceImageList(lottiePb.replaceImageList);
      } else {
        copyAttrs.replaceImageList = lottieAttrs.replaceImageList;
      }

      if (lottieValid && ToolHelper.isArrayValid(lottiePb.replaceKeyPathColorList)) {
        copyAttrs.replaceKeyPathColorList =
            transformReplaceColorList(lottiePb.replaceKeyPathColorList);
      } else {
        copyAttrs.replaceKeyPathColorList = lottieAttrs.replaceKeyPathColorList;
      }

      if (lottieValid && lottiePb.scaleType != LottieAttributes.SCALE_TYPE_UNKNOWN) {
        copyAttrs.scaleType = imageScaleType(lottiePb.scaleType);
      } else {
        copyAttrs.scaleType = lottieAttrs.scaleType;
      }
      if (lottieValid) {
        copyAttrs.replaceImageSupportNet = lottiePb.replaceImageSupportNet;
      } else {
        copyAttrs.replaceImageSupportNet = lottieAttrs.replaceImageSupportNet;
      }
      return copyAttrs;
    }
    return null;
  }

  /**
   * press-text
   */
  @Nullable
  public static TextItemNode.TextAttrs pressTextAttrs(@NonNull Context context,
      @NonNull IServiceContainer serviceContainer,
      @NonNull UIModel.Size decorSize,
      @NonNull TextItemNode.TextAttrs textAttrs, @Nullable Attributes attributes) {
    if (attributes != null && (attributes.common != null || attributes.text != null)) {
      TextItemNode.TextAttrs copyAttrs = new TextItemNode.TextAttrs();
      pressAttrs(serviceContainer, context, textAttrs, copyAttrs, attributes);
      TextAttributes textPb = attributes.text;
      boolean textValid = attributes.text != null;
      if (textValid && !TextUtils.isEmpty(textPb.text)) {
        String text = attributes.text.text;
        IDataBindingService service = serviceContainer.getService(IDataBindingService.class);
        copyAttrs.text = service != null ? ToolHelper.resolveValue(service, text) : text;
      } else {
        copyAttrs.text = textAttrs.text;
      }
      if (textValid && textPb.fontSize != null) {
        copyAttrs.size = textPb.fontSize.value;
      } else {
        copyAttrs.size = textAttrs.size;
      }
      if (textValid && !TextUtils.isEmpty(textPb.fontColor)) {
        copyAttrs.color =
            ToolHelper.parseColor(serviceContainer, textPb.fontColor, textAttrs.color);
      } else {
        copyAttrs.color = textAttrs.color;
      }
      if (textValid && textPb.maxLines != null) {
        copyAttrs.maxLines = textPb.maxLines.value;
      } else {
        copyAttrs.maxLines = textAttrs.maxLines;
      }
      if (textValid && textPb.align != null) {
        copyAttrs.alignMode = align(textPb.align);
      } else {
        copyAttrs.alignMode = textAttrs.alignMode;
      }
      if (textValid && textPb.bold != null) {
        copyAttrs.isBold = textPb.bold.value;
      } else {
        copyAttrs.isBold = textAttrs.isBold;
      }
      if (textValid && !TextUtils.isEmpty(textPb.fontName)) {
        copyAttrs.fontName = textPb.fontName;
      } else {
        copyAttrs.fontName = textAttrs.fontName;
      }
      if (textValid && textPb.lineSpace != null) {
        copyAttrs.lineSpaceExtra = ToolHelper.dip2px(context, textPb.lineSpace.value);
      } else {
        copyAttrs.lineSpaceExtra = textAttrs.lineSpaceExtra;
      }
      if (textValid && textPb.lineHeight != null) {
        copyAttrs.lineHeight = ToolHelper.dip2px(context, textPb.lineHeight.value);
      } else {
        copyAttrs.lineHeight = textAttrs.lineHeight;
      }
      if (textValid && !TextUtils.isEmpty(textPb.highlightColor)) {
        copyAttrs.highlightColor =
            ToolHelper.parseColor(serviceContainer, textPb.highlightColor,
                textAttrs.highlightColor);
      } else {
        copyAttrs.highlightColor = textAttrs.highlightColor;
      }
      if (textValid && textPb.ellipsize != TextAttributes.ELLIPSIZE_UNKNOWN) {
        copyAttrs.ellipsizeMode = textEllipsizeMode(textPb.ellipsize);
      } else {
        copyAttrs.ellipsizeMode = textAttrs.ellipsizeMode;
      }
      if (textValid && ToolHelper.isArrayValid(textPb.richList)) {
        copyAttrs.richTextList =
            richText(context, serviceContainer, decorSize, Arrays.asList(textPb.richList));
      } else {
        copyAttrs.richTextList = textAttrs.richTextList;
      }
      if (textValid && textPb.tilt != null) {
        copyAttrs.isTilt = textPb.tilt.value;
      } else {
        copyAttrs.isTilt = textAttrs.isTilt;
      }
      if (textValid && textPb.lineMode != TextAttributes.LINE_MODE_UNKNOWN) {
        copyAttrs.lineMode = lineMode(textPb.lineMode);
      } else {
        copyAttrs.lineMode = textAttrs.lineMode;
      }
      return copyAttrs;
    }
    return null;
  }

  /**
   * press-image
   */
  @Nullable
  public static ImageItemNode.ImageAttrs pressImageAttrs(@NonNull Context context,
      @NonNull IServiceContainer serviceContainer,
      @NonNull ImageItemNode.ImageAttrs imageAttrs, @Nullable Attributes attributes) {
    if (attributes != null && (attributes.common != null || attributes.image != null)) {
      ImageItemNode.ImageAttrs copyAttrs = new ImageItemNode.ImageAttrs();
      pressAttrs(serviceContainer, context, imageAttrs, copyAttrs, attributes);
      ImageAttributes imagePb = attributes.image;
      boolean imageValid = attributes.image != null;
      IDataBindingService service = serviceContainer.getService(IDataBindingService.class);
      if (imageValid && !TextUtils.isEmpty(imagePb.url)) {
        copyAttrs.imageUrl =
            service != null ? ToolHelper.resolveValue(service, imagePb.url) : imagePb.url;
      } else {
        copyAttrs.imageUrl = imageAttrs.imageUrl;
      }
      if (imageValid && !TextUtils.isEmpty(imagePb.highlightUrl)) {
        copyAttrs.highlightUrl = service != null
            ? ToolHelper.resolveValue(service, imagePb.highlightUrl) : imagePb.highlightUrl;
      } else {
        copyAttrs.highlightUrl = imageAttrs.highlightUrl;
      }
      if (imageValid && imagePb.scaleType != ImageAttributes.SCALE_TYPE_UNKNOWN) {
        copyAttrs.scaleType = imageScaleType(imagePb.scaleType);
      } else {
        copyAttrs.scaleType = imageAttrs.scaleType;
      }
      if (imageValid && !TextUtils.isEmpty(imagePb.colorFilter)) {
        copyAttrs.colorFilter = ToolHelper.parseColor(serviceContainer, imagePb.colorFilter, 0);
      } else {
        copyAttrs.colorFilter = imageAttrs.colorFilter;
      }
      return copyAttrs;
    }
    return null;
  }

  /**
   * press-item
   *
   * @param serviceContainer
   * @param context          context
   * @param source           render正在展示的数据源
   * @param copy             这个是press之后要展示的数据源
   * @param attributes       这个是外界触发press，传递过来的数据源
   */
  public static void pressAttrs(@Nullable IServiceContainer serviceContainer,
      @NonNull Context context, @NonNull UIModel.Attrs source,
      @NonNull UIModel.Attrs copy, @Nullable Attributes attributes) {
    boolean commonValid = attributes != null && attributes.common != null;
    if (commonValid && attributes.common.alpha != null) {
      copy.alpha = attributes.common.alpha.value;
    } else {
      copy.alpha = source.alpha;
    }
    Drawable drawable =
        commonValid ? createDrawable(serviceContainer, context, attributes.common) : null;
    UIModel.Shadow shadow =
        commonValid ? createShadow(serviceContainer, context, attributes.common) : null;
    CornerRadius cornerRadius = commonValid ? attributes.common.cornerRadius : null;
    copy.backgroundDrawable = drawable == null ? source.backgroundDrawable : drawable;
    copy.shadow = shadow == null ? source.shadow : shadow;
    copy.cornerRadius = new UIModel.CornerRadius();
    if (cornerRadius == null) {
      copy.cornerRadius = source.cornerRadius;
    } else {
      copyCorner(serviceContainer, context, copy.cornerRadius, cornerRadius);
    }
  }
}
