package com.kuaishou.riaid.render.node.item;

import java.util.List;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.render.adapter.video.VideoAdapter;
import com.kuaishou.riaid.render.adapter.video.base.BaseVideoAdapter;
import com.kuaishou.riaid.render.constants.DispatchEventType;
import com.kuaishou.riaid.render.impl.empty.IEmptyImageListener;
import com.kuaishou.riaid.render.impl.media.MediaHandlerImpl;
import com.kuaishou.riaid.render.impl.media.service.MediaPlayerServiceImpl;
import com.kuaishou.riaid.render.impl.touch.CommonHandlerImpl;
import com.kuaishou.riaid.render.impl.touch.gesture.GestureDetector;
import com.kuaishou.riaid.render.interf.IImpressionListener;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.item.base.AbsItemNode;
import com.kuaishou.riaid.render.preload.RIAIDPreloadResourceOperator;
import com.kuaishou.riaid.render.service.base.ILoadImageService;
import com.kuaishou.riaid.render.service.base.IMediaPlayerService;
import com.kuaishou.riaid.render.service.base.IResumeActionService;
import com.kuaishou.riaid.render.util.LayoutPerformer;
import com.kuaishou.riaid.render.widget.CornerImageView;
import com.kuaishou.riaid.render.widget.video.RenderTextureView;

/**
 * 用来播放视频的控件
 */
public class VideoItemNode extends AbsItemNode<VideoItemNode.VideoAttrs>
    implements IImpressionListener, IMediaPlayerService.OnVideoSizeChangedListener {

  @NonNull
  private final FrameLayout mVideoContainer =
      new FrameLayout(mNodeInfo.context.realContext);

  @NonNull
  private final CornerImageView mCornerImageView =
      new CornerImageView(mNodeInfo.context.realContext);

  @NonNull
  private final RenderTextureView mTextTureView =
      new RenderTextureView(mNodeInfo.context.realContext);

  @NonNull
  private final MediaPlayerServiceImpl mMediaPlayerService =
      new MediaPlayerServiceImpl(mTextTureView);

  @NonNull
  private final MediaHandlerImpl mMediaHandlerImpl =
      new MediaHandlerImpl(mNodeInfo.videoHandler, mNodeInfo.context,
          getService(IResumeActionService.class));

  public VideoItemNode(@NonNull NodeInfo<VideoAttrs> nodeInfo) {
    super(nodeInfo);
    mMediaPlayerService.setMediaPlayerService(getService(IMediaPlayerService.class));
    mMediaPlayerService.setDispatchEventListener(mMediaHandlerImpl);
  }

  @Override
  public void loadAttributes() {
    VideoAttrs attrs = mNodeInfo.attrs;
    // 设置播放器是是否为透明控件，默认是透明，也就是opaque为false。
    mTextTureView.setOpaque(attrs.opaque);
    setVolumeMute(attrs.autoMute);
    mTextTureView.setAlpha(attrs.alpha);
    // 这是Responder的回调响应
    mTextTureView.setDispatchEventService(mMediaHandlerImpl);
    // 注册首帧回调
    mTextTureView.registerImpressionListener(this);
    // 注册视频准备好的回调
    mTextTureView.registerOnVideoSizeChangedListener(this);
    // 开始加载
    mTextTureView.loadDataSource(mMediaPlayerService, mNodeInfo.attrs);
    // 设置背景
    if (attrs.backgroundDrawable != null) {
      mVideoContainer.setBackground(attrs.backgroundDrawable);
    }
    // 设置图片适配模式
    mCornerImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    ILoadImageService loadImageService = getService(ILoadImageService.class);
    IResumeActionService resumeActionService = getService(IResumeActionService.class);
    String realUrl = RIAIDPreloadResourceOperator.getRealUrl(attrs.coverUrl);
    if (loadImageService != null && !TextUtils.isEmpty(realUrl)) {
      loadImageService.loadImage(realUrl, mCornerImageView);
    }
    if (mNodeInfo.handler != null && resumeActionService != null) {
      // 需要绑定触摸事件
      GestureDetector detector = new GestureDetector(mNodeInfo.context);
      detector.setHandlerListener(
          new CommonHandlerImpl(mNodeInfo.handler, mNodeInfo.context, resumeActionService));
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

  @Override
  protected void layoutItemParams() {
    if (!TextUtils.isEmpty(mNodeInfo.attrs.coverUrl)) {
      // 父容器已经在基类设置尺寸了，这里就不用设置了
      // 把TextureView的布局属性设置为充满
      LayoutPerformer.setMatchLayoutParams(mTextTureView);
      // 把图片的布局属性设置为充满
      LayoutPerformer.setMatchLayoutParams(mCornerImageView);
    } else {
      // 图片没有添加到容器中，也就没有必要写撑满属性了，直接定死宽高
      LayoutPerformer.setFixedLayoutParams(mCornerImageView, size.width, size.height);
    }
    // 适配图片
    adapterCoverImageUrl();
  }

  @Override
  public boolean dispatchEvent(@NonNull String eventType, @Nullable List<Integer> keyList,
      @Nullable Attributes attributes) {
    if (canMatchRenderKey(keyList)) {
      switch (eventType) {
        case DispatchEventType.VIDEO_PAUSE:
          mMediaPlayerService.pause();
          break;
        case DispatchEventType.VIDEO_PLAY:
          mMediaPlayerService.start();
          break;
        case DispatchEventType.VIDEO_SOUND_TURN_OFF:
          setVolumeMute(true);
          break;
        case DispatchEventType.VIDEO_SOUND_TURN_ON:
          setVolumeMute(false);
          break;
        case DispatchEventType.VIDEO_RESET:
          mMediaPlayerService.seekTo(0);
          mMediaPlayerService.pause();
          break;
        case DispatchEventType.VIDEO_REPLAY:
          mMediaPlayerService.seekTo(0);
          mMediaPlayerService.start();
          break;
        default:
          ADRenderLogger.e("VideoItemNode 无法识别的type类型 " + eventType);
          break;
      }
      return true;
    }
    return super.dispatchEvent(eventType, keyList, attributes);
  }

  /**
   * 设置当前播放器是否静音
   *
   * @param mute true标识静音false不静音
   */
  private void setVolumeMute(boolean mute) {
    float volume = mute ? 0.0F : 1.0F;
    mMediaPlayerService.setVolume(volume, volume);
  }

  /**
   * 获取视频播放的总时长，计时累加
   *
   * @return 返回播放总时长，单位毫秒
   */
  public long getVideoTotalPlayDuration() {
    long currentPosition = mMediaPlayerService.getCurrentPosition();
    long timesDuration = mTextTureView.getLoopingCount() * mMediaPlayerService.getDuration();
    return currentPosition + timesDuration;
  }

  @NonNull
  @Override
  protected View getItemRealView() {
    return mVideoContainer;
  }

  @Override
  public void draw(@NonNull ViewGroup decor) {
    // 准备好容器
    if (!TextUtils.isEmpty(mNodeInfo.attrs.coverUrl)) {
      // 添加视频显示的view
      LayoutPerformer.addView(mVideoContainer, mTextTureView);
      // 添加图片显示的view
      LayoutPerformer.addView(mVideoContainer, mCornerImageView);
    } else {
      // 只添加一个即可
      LayoutPerformer.addView(mVideoContainer, mTextTureView);
    }
    super.draw(decor);
  }

  @Override
  public void onImpression() {
    // 把图片移除
    if (!TextUtils.isEmpty(mNodeInfo.attrs.coverUrl)) {
      // 证明图片添加进去了
      mCornerImageView.setVisibility(View.GONE);
    }
  }

  @Override
  public void onVideoSizeChanged(IMediaPlayerService mp, int width, int height) {
    UIModel.Size videoSize = new UIModel.Size();
    videoSize.width = mp.getVideoWidth();
    videoSize.height = mp.getVideoHeight();
    adapterSize(mNodeInfo.attrs.adapterType, mTextTureView, size, videoSize);
  }

  /**
   * 适配图片
   */
  private void adapterCoverImageUrl() {
    String realUrl = RIAIDPreloadResourceOperator.getRealUrl(mNodeInfo.attrs.coverUrl);
    ILoadImageService loadImageService = getService(ILoadImageService.class);
    if (loadImageService != null && !TextUtils.isEmpty(realUrl)) {
      loadImageService.loadBitmap(realUrl, null, null, new IEmptyImageListener() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap) {
          if (bitmap != null) {
            UIModel.Size bitmapSize = new UIModel.Size();
            bitmapSize.width = bitmap.getWidth();
            bitmapSize.height = bitmap.getHeight();
            adapterSize(mNodeInfo.attrs.adapterType, mCornerImageView,
                VideoItemNode.this.size, bitmapSize);
          }
        }
      });
    }
  }

  /**
   * 适配View
   *
   * @param videoAdapterType 适配类型
   * @param targetView       需要适配的目标View
   * @param containerSize    容器的尺寸
   * @param realSize         目标实际尺寸
   */
  private void adapterSize(int videoAdapterType, @NonNull View targetView,
      @NonNull UIModel.Size containerSize, @NonNull UIModel.Size realSize) {
    // 容器剩余的空间需要减去padding
    UIModel.Edge containerPadding = mNodeInfo.layout.padding;
    containerSize.width -= (containerPadding.start + containerPadding.end);
    containerSize.height -= (containerPadding.top + containerPadding.bottom);
    // 然后做一个边界保护
    containerSize.width = Math.max(containerSize.width, 0);
    containerSize.height = Math.max(containerSize.height, 0);
    // 开始计算
    BaseVideoAdapter.AdapterModel adapterModel =
        VideoAdapter.obtain().adapterVideo(videoAdapterType, containerSize, realSize);
    if (adapterModel != null) {
      LayoutPerformer.changeVideoSizeAndPosition(targetView, adapterModel);
    }
  }

  public static final class VideoAttrs extends UIModel.Attrs {
    public boolean autoMute;
    public boolean autoLoop;
    public long autoSeekTime;
    public boolean autoPlay;
    public String videoUrl;
    public String manifest;
    public String coverUrl;
    public int adapterType;
    // 默认透明
    public boolean opaque;
  }
}
