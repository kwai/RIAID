package com.kuaishou.riaid.render.adapter.video.impl;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.proto.nano.VideoAttributes;
import com.kuaishou.riaid.render.adapter.video.base.BaseVideoAdapter;
import com.kuaishou.riaid.render.model.UIModel;

/**
 * 这个是针对激励视频的适配规则
 * 1. 素材尺寸规则
 *   1.1 如果素材的宽高比 >= 1, 认为该素材就是16：9的
 *   1.2 如果素材的宽高比 < 1, 认为该素材就是9：16的
 * 2. 根据容器的宽高比适配
 *   2.1 素材是宽高比是9：16（竖屏视频）
 *     2.1.1 屏幕宽高比 >=9:16, 视频等比放大，高度撑满屏幕，两侧裁剪
 *     2.1.2 屏幕宽高比 < 9:16，视频等比放大，宽度撑满屏幕，上下裁剪
 *   2.2 素材是宽高比是16：9（横屏视频）
 *     2.2.1 宽度撑满屏幕，上下居中，上下留黑
 */
public class InspireVideoAdapter extends BaseVideoAdapter {

  @Override
  public int getVideoAdapterType() {
    return VideoAttributes.ADAPTER_TYPE_INSPIRE;
  }

  @NonNull
  @Override
  public AdapterModel adapter(@NonNull UIModel.Size containerSize,
      @NonNull UIModel.Size videoSize) {
    UIModel.Point deltaPoint;
    UIModel.Size newVideoSize = new UIModel.Size();
    // 获取容器的宽高比
    float containerRatio = containerSize.width * 1.0F / containerSize.height;
    // 判断素材是不是竖屏的
    boolean isSourceVertical = videoSize.height >= videoSize.width;
    if (isSourceVertical) {
      // 如果素材是竖屏的，就认为该素材的宽高比是9：16的
      if (containerRatio <= (9F / 16F)) {
        // 如果容器宽高比是大于等于9：16的
        newVideoSize.height = containerSize.height;
        newVideoSize.width =
            (int) (videoSize.width * (containerSize.height * 1.0F / videoSize.height));
        deltaPoint = new UIModel.Point((containerSize.width - newVideoSize.width) / 2, 0);
      } else {
        // 如果容器宽高比是小于等于9：16的
        newVideoSize.width = containerSize.width;
        newVideoSize.height =
            (int) (videoSize.height * (containerSize.width * 1.0F / videoSize.width));
        deltaPoint = new UIModel.Point(0, (containerSize.height - newVideoSize.height) / 2);
      }
    } else {
      // 如果素材是横屏的，认为该素材就是16：9的宽高比
      newVideoSize.width = containerSize.width;
      newVideoSize.height =
          (int) (videoSize.height * (containerSize.width * 1.0F / videoSize.width));
      deltaPoint = new UIModel.Point(0, (containerSize.height - newVideoSize.height) / 2);
    }
    return new AdapterModel(newVideoSize, deltaPoint);
  }
}
