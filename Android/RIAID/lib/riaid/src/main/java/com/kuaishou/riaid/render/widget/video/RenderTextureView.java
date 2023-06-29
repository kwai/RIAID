package com.kuaishou.riaid.render.widget.video;

import java.io.File;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Surface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.impl.empty.IEmptySurfaceTextureListener;
import com.kuaishou.riaid.render.impl.media.MediaBufferingUpdateImpl;
import com.kuaishou.riaid.render.impl.media.MediaCompleteImpl;
import com.kuaishou.riaid.render.impl.media.MediaErrorImpl;
import com.kuaishou.riaid.render.impl.media.MediaInfoListenerImpl;
import com.kuaishou.riaid.render.impl.media.MediaPrepareImpl;
import com.kuaishou.riaid.render.impl.media.MediaSizeChangedImpl;
import com.kuaishou.riaid.render.impl.media.service.MediaPlayerServiceImpl;
import com.kuaishou.riaid.render.interf.IDispatchEventService;
import com.kuaishou.riaid.render.interf.IImpressionListener;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.node.item.VideoItemNode;
import com.kuaishou.riaid.render.preload.RIAIDPreloadResourceOperator;
import com.kuaishou.riaid.render.service.base.IMediaPlayerService;

/**
 * Render用来播放视频的View
 */
public class RenderTextureView extends SafeTextureView implements IEmptySurfaceTextureListener {

  public RenderTextureView(Context context) {
    super(context);
  }

  public RenderTextureView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public RenderTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private VideoItemNode.VideoAttrs mVideoAttrs;

  private final MediaPlayerServiceImpl mMediaServiceImpl = new MediaPlayerServiceImpl(this);

  private final MediaErrorImpl mErrorListenerImpl = new MediaErrorImpl();
  private final MediaInfoListenerImpl mInfoImpl = new MediaInfoListenerImpl();
  private final MediaCompleteImpl mCompletionListenerImpl = new MediaCompleteImpl();
  private final MediaBufferingUpdateImpl
      mBufferingUpdateListenerImpl = new MediaBufferingUpdateImpl();
  private final MediaPrepareImpl mPreparedListenerImpl = new MediaPrepareImpl();
  private final MediaSizeChangedImpl mSizeChangedImpl = new MediaSizeChangedImpl();

  /**
   * 加载媒体资源
   *
   * @param mediaPlayer 视频播放的代理实现类
   * @param videoAttrs  视频属性
   */
  public void loadDataSource(@NonNull IMediaPlayerService mediaPlayer,
      @NonNull VideoItemNode.VideoAttrs videoAttrs) {
    if (!TextUtils.isEmpty(videoAttrs.videoUrl) || !TextUtils.isEmpty(videoAttrs.manifest)) {
      // 如果资源Url就是空的，那么也没有必要进行下去了
      this.mVideoAttrs = videoAttrs;
      setSurfaceTextureListener(this);
      mCompletionListenerImpl.setLooping(videoAttrs.autoLoop);
      mPreparedListenerImpl.setAutoPlay(videoAttrs.autoPlay);
      mPreparedListenerImpl.setFirstSeekMesc((int) videoAttrs.autoSeekTime);
      mMediaServiceImpl.setMediaPlayerService(mediaPlayer);
    }
  }


  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    mMediaServiceImpl.release();
  }

  @Override
  public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
    File preloadFile = RIAIDPreloadResourceOperator.getPreloadExistsFile(mVideoAttrs.videoUrl);
    if (preloadFile != null) {
      // 先判断预加载好的资源有没有，如果有的话先用预下载好的资源
      mMediaServiceImpl.setDataSource(preloadFile.getAbsolutePath(), null);
      ADRenderLogger
          .i(RIAIDPreloadResourceOperator.PRELOAD_TAG + "视频文件本地加载：" +
              preloadFile.getAbsolutePath() + " url: " + mVideoAttrs.videoUrl);
    } else {
      ADRenderLogger.i(RIAIDPreloadResourceOperator.PRELOAD_TAG + "视频网络加载 url: " + mVideoAttrs.videoUrl);
      mMediaServiceImpl.setDataSource(mVideoAttrs.videoUrl, mVideoAttrs.manifest);
    }
    mMediaServiceImpl.setOnInfoListener(mInfoImpl);
    mMediaServiceImpl.setOnErrorListener(mErrorListenerImpl);
    mMediaServiceImpl.setOnPreparedListener(mPreparedListenerImpl);
    mMediaServiceImpl.setOnVideoSizeChangedListener(mSizeChangedImpl);
    mMediaServiceImpl.setOnCompletionListener(mCompletionListenerImpl);
    mMediaServiceImpl.setOnBufferingUpdateListener(mBufferingUpdateListenerImpl);
    mMediaServiceImpl.setPlayerEventListener(mInfoImpl);
    mMediaServiceImpl.setSurface(new Surface(surface));
    mMediaServiceImpl.prepareAsync();
  }

  /**
   * 设置媒体关键事件上报监听
   *
   * @param dispatchEventService 回调的listener
   */
  public void setDispatchEventService(@Nullable IDispatchEventService dispatchEventService) {
    mCompletionListenerImpl.setDispatchEventService(dispatchEventService);
    mInfoImpl.setDispatchEventService(dispatchEventService);
    mMediaServiceImpl.setDispatchEventListener(dispatchEventService);
  }

  /**
   * 当前媒体是不是准备好
   *
   * @return true标识尊卑好了，false没有
   */
  public boolean prepared() {
    return mPreparedListenerImpl.prepared();
  }

  /**
   * 当前是不是需要自动播放
   *
   * @param autoPlay true标识需要在资源准备好之后播放，否则不播放
   */
  public void setAutoPlay(boolean autoPlay) {
    mPreparedListenerImpl.setAutoPlay(autoPlay);
  }

  /**
   * 获取播放完成的次数
   *
   * @return 返回播放次数
   */
  public int getLoopingCount() {return mCompletionListenerImpl.getLoopingCount();}

  /**
   * 注册首帧监听
   *
   * @param listener 目标监听器，会在首帧被回调
   */
  public void registerImpressionListener(@NonNull IImpressionListener listener) {
    mInfoImpl.registerImpressionListener(listener);
  }

  public void registerOnPreparedListener(@NonNull IMediaPlayerService.OnPreparedListener listener) {
    mPreparedListenerImpl.registerOnPreparedListener(listener);
  }

  public void registerOnVideoSizeChangedListener(
      @NonNull IMediaPlayerService.OnVideoSizeChangedListener listener) {
    mSizeChangedImpl.registerOnVideoSizeChangedListener(listener);
  }

}
