package com.kuaishou.riaid.render.node.item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.zip.ZipInputStream;

import android.animation.ValueAnimator;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieCompositionFactory;
import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.proto.nano.LottieAttributes;
import com.kuaishou.riaid.render.constants.DispatchEventType;
import com.kuaishou.riaid.render.impl.lottie.LottieHandlerImpl;
import com.kuaishou.riaid.render.impl.touch.CommonHandlerImpl;
import com.kuaishou.riaid.render.impl.touch.gesture.GestureDetector;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.lottie.LottieColorDelegate;
import com.kuaishou.riaid.render.lottie.LottieFontAssetDelegate;
import com.kuaishou.riaid.render.lottie.LottieImageDelegate;
import com.kuaishou.riaid.render.lottie.LottieTextDelegate;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.item.base.AbsItemNode;
import com.kuaishou.riaid.render.preload.RIAIDPreloadResourceOperator;
import com.kuaishou.riaid.render.service.base.ILoadImageService;
import com.kuaishou.riaid.render.service.base.IResumeActionService;
import com.kuaishou.riaid.render.util.LayoutPerformer;
import com.kuaishou.riaid.render.util.ToolHelper;
import com.kuaishou.riaid.render.util.ZipHelper;

/**
 * 这个是处理lottie动画的组件
 */
public class LottieItemNode extends AbsItemNode<LottieItemNode.LottieAttrs> {
  @NonNull
  private final LottieHandlerImpl mHandlerImpl =
      new LottieHandlerImpl(mNodeInfo.lottieHandler, mNodeInfo.context,
          getService(IResumeActionService.class));

  @NonNull
  private final LottieAnimationView mLottieView =
      new LottieAnimationView(mNodeInfo.context.realContext);

  public LottieItemNode(@NonNull NodeInfo<LottieAttrs> nodeInfo) {
    super(nodeInfo);
  }

  @Nullable
  @Override
  public View getGestureView() {
    return mLottieView;
  }

  @Override
  public void loadAttributes() {
    // attrs都是在数据转换层进行了修改，保证不能为空
    refreshUI(mNodeInfo.attrs);
    boolean handlerValid = mNodeInfo.handler != null;
    if (handlerValid) {
      GestureDetector wrapper = new GestureDetector(mNodeInfo.context);
      // 绑定单击，双击，长按
      IResumeActionService service = getService(IResumeActionService.class);
      wrapper.setHandlerListener(
          new CommonHandlerImpl(mNodeInfo.handler, mNodeInfo.context, service));
      wrapper.initGestureDetector(mLottieView);
    }
  }

  /**
   * 刷新UI
   *
   * @param attrs lottie的属性对象
   */
  private void refreshUI(@NonNull LottieAttrs attrs) {
    mLottieView.setScaleType(attrs.scaleType);
    mLottieView.setMinProgress(0.0F);
    mLottieView.setMaxProgress(1.0F);
    mLottieView.setSpeed(attrs.speed);
    mLottieView.setProgress(attrs.progress);
    mLottieView.setRepeatMode(attrs.repeatMode);
    mLottieView.setFontAssetDelegate(new LottieFontAssetDelegate());
    mLottieView.setRepeatCount(attrs.repeat ? ValueAnimator.INFINITE : 0);
    LottieTextDelegate lottieTextDelegate = new LottieTextDelegate(mLottieView);

    mLottieView.setTextDelegate(lottieTextDelegate);
    mLottieView.removeAllAnimatorListeners();
    mLottieView.addAnimatorListener(mHandlerImpl);
    List<LottieAttributes.ReplaceText> replaceTextList = mNodeInfo.attrs.replaceTextList;
    List<LottieAttributes.ReplaceImage> replaceImageList = mNodeInfo.attrs.replaceImageList;
    if (ToolHelper.isListValid(replaceTextList)) {
      for (LottieAttributes.ReplaceText replaceText : replaceTextList) {
        lottieTextDelegate.setText(replaceText.placeHolder, replaceText.realText);
      }
    }

    LottieImageDelegate lottieImageDelegate = new LottieImageDelegate(mLottieView, getService(
        ILoadImageService.class), replaceImageList, mNodeInfo.attrs.replaceImageSupportNet);
    new LottieColorDelegate(mLottieView, mNodeInfo.attrs.replaceKeyPathColorList,
        mNodeInfo.serviceContainer).replaceColor();
    lottieImageDelegate.setOnLottieImageReplaceCallback(mHandlerImpl);
    lottieImageDelegate.replaceAllImage();
    File lottieFile = RIAIDPreloadResourceOperator.getPreloadExistsFile(attrs.url);
    if (ZipHelper.isArchiveFile(lottieFile)) {
      // 如果是压缩文件，直接走压缩文件的本地加载
      ADRenderLogger.i(RIAIDPreloadResourceOperator.PRELOAD_TAG + "Lottie 压缩文件本地加载：" +
          lottieFile.getAbsolutePath() + " url: " + attrs.url);
      try {
        LottieCompositionFactory
            .fromZipStream(new ZipInputStream(new FileInputStream(lottieFile.getAbsolutePath())),
                ToolHelper.toMd5Key(attrs.url)
            )
            .addListener(result -> {
              mLottieView.setComposition(result);
              if (attrs.autoPlay) {
                mLottieView.playAnimation();
              }
              if (TextUtils.equals(dispatchEventType, DispatchEventType.LOTTIE_PLAY)) {
                mLottieView.playAnimation();
              }
              if (TextUtils.equals(dispatchEventType, DispatchEventType.LOTTIE_REPLAY)) {
                mLottieView.setProgress(0);
                mLottieView.playAnimation();
              }
            });
      } catch (FileNotFoundException e) {
        ADRenderLogger.e("Lottie zip资源加载失败", e);
        playWithUrl(attrs);
      }
    } else {
      ADRenderLogger.i(RIAIDPreloadResourceOperator.PRELOAD_TAG + "Lottie 网络加载 url: " + attrs.url);
      playWithUrl(attrs);
    }
    if (attrs.backgroundDrawable != null) {
      mLottieView.setBackground(attrs.backgroundDrawable);
    }
  }

  private void playWithUrl(@NonNull LottieAttrs attrs) {
    if (!TextUtils.isEmpty(attrs.url)) {
      mLottieView.setAnimationFromUrl(attrs.url);
      if (attrs.autoPlay) {
        mLottieView.playAnimation();
      }
    }
  }

  @Override
  public void onMeasure(int widthSpec, int heightSpec) {
    size.width = LayoutPerformer
        .getSideValueByMode(mNodeInfo.layout.width, mNodeInfo.layout.width, widthSpec);
    size.height = LayoutPerformer
        .getSideValueByMode(mNodeInfo.layout.height, mNodeInfo.layout.height, heightSpec);
    // 再通过最大尺寸来约束一下
    size.width = LayoutPerformer.getSizeByMax(size.width, mNodeInfo.layout.maxWidth);
    size.height = LayoutPerformer.getSizeByMax(size.height, mNodeInfo.layout.maxHeight);
  }

  /**
   * 记录下分发下来的事件类型，因为本地加载Lottie资源是异步的，如果没加载完成，是播放不了的，所以待加载完成再执行播放。
   */
  private String dispatchEventType = "";

  public boolean dispatchEvent(@NonNull String eventType, @Nullable List<Integer> keyList,
      @Nullable Attributes attributes) {
    if (canMatchRenderKey(keyList)) {
      dispatchEventType = eventType;
      if (TextUtils.equals(eventType, DispatchEventType.LOTTIE_PLAY)) {
        mLottieView.playAnimation();
        return true;
      } else if (TextUtils.equals(eventType, DispatchEventType.LOTTIE_REPLAY)) {
        mLottieView.setProgress(0);
        mLottieView.playAnimation();
        return true;
      } else if (TextUtils.equals(eventType, DispatchEventType.LOTTIE_RESET)) {
        mLottieView.setProgress(0);
        return true;
      } else if (TextUtils.equals(eventType, DispatchEventType.LOTTIE_PAUSE)) {
        mLottieView.pauseAnimation();
        return true;
      } else if (attributes != null && attributes.lottie != null &&
          attributes.lottie.progress != null) {
        mNodeInfo.attrs.progress = attributes.lottie.progress.value;
        mLottieView.setProgress(mNodeInfo.attrs.progress);
        return true;
      }
    }
    return false;
  }

  @NonNull
  @Override
  protected View getItemRealView() {
    return mLottieView;
  }

  public static final class LottieAttrs extends UIModel.Attrs {
    public String url;
    public float speed;
    public boolean repeat;
    public int repeatMode;
    public float progress;
    public boolean autoPlay;
    public List<LottieAttributes.ReplaceText> replaceTextList;
    public List<LottieAttributes.ReplaceImage> replaceImageList;
    public List<LottieAttributes.ReplaceKeyPathColor> replaceKeyPathColorList;
    public ImageView.ScaleType scaleType;
    // 替换图片是否支持图片。如果是false，仅支持本地图片下载好了的，才能替换，如果是true，则可以在播放期间下载图片并替换
    public boolean replaceImageSupportNet;
  }
}
