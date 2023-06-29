package com.kuaishou.riaid.render.adapter.video;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.adapter.video.base.BaseVideoAdapter;
import com.kuaishou.riaid.render.adapter.video.impl.AutoVideoAdapter;
import com.kuaishou.riaid.render.adapter.video.impl.CenterCropVideoAdapter;
import com.kuaishou.riaid.render.adapter.video.impl.InspireVideoAdapter;
import com.kuaishou.riaid.render.model.UIModel;

/**
 * 这个是适配适配的容器，所有的适配规则都在这里了
 */
public class VideoAdapter {

  private VideoAdapter() {}

  @NonNull
  public static VideoAdapter obtain() {return new VideoAdapter();}

  /**
   * 适配规则的容器集合
   */
  private final List<BaseVideoAdapter> mVideoAdapterList = new ArrayList<>();

  {
    mVideoAdapterList.add(new CenterCropVideoAdapter());
    mVideoAdapterList.add(new AutoVideoAdapter());
    mVideoAdapterList.add(new InspireVideoAdapter());
  }

  @Nullable
  public BaseVideoAdapter.AdapterModel adapterVideo(int videoAdapterType,
      @NonNull UIModel.Size containerSize, @NonNull UIModel.Size videoSize) {
    if (isSizeValid(containerSize) && isSizeValid(videoSize)) {
      // 如果尺寸都吻合的话，开始进行适配，否则也是没有意义的，bro
      for (BaseVideoAdapter videoAdapter : mVideoAdapterList) {
        if (videoAdapter.getVideoAdapterType() == videoAdapterType) {
          return videoAdapter.adapter(containerSize, videoSize);
        }
      }
    }
    return null;
  }

  /**
   * 判断当前尺寸是不是有效
   *
   * @param size 目标尺寸
   * @return 有效就返回true，否则返回false
   */
  private boolean isSizeValid(@NonNull UIModel.Size size) {
    return size.width > 0 && size.height > 0;
  }
}
