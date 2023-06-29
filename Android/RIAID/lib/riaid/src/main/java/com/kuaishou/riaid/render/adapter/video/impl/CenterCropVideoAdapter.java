package com.kuaishou.riaid.render.adapter.video.impl;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.proto.nano.VideoAttributes;
import com.kuaishou.riaid.render.adapter.video.base.BaseVideoAdapter;
import com.kuaishou.riaid.render.model.UIModel;

/**
 * 最小边界尺寸适配并且居中原则
 * 1.知道视频宽高比，通气宽高比，保证最小边撑满容器，多余部分裁切
 * 2.视频还需要在容器中居中对齐
 * 3.如果视频源的宽高有一方小于容器，就等比例放大，如果都大于容器，直接裁剪
 */
public class CenterCropVideoAdapter extends BaseVideoAdapter {

  @Override
  public int getVideoAdapterType() {
    return VideoAttributes.ADAPTER_TYPE_CENTER_CROP;
  }

  @NonNull
  @Override
  public AdapterModel adapter(@NonNull UIModel.Size containerSize,
      @NonNull UIModel.Size videoSize) {
    UIModel.Point deltaPoint;
    UIModel.Size newVideoSize = new UIModel.Size();
    float widthRatio = videoSize.width * 1.0F / containerSize.width;
    float heightRatio = videoSize.height * 1.0F / containerSize.height;
    // 如果宽度和高度有一边没有超过屏幕，整体来说是需要放大的，然后居中裁剪
    if (widthRatio <= 1.0F || heightRatio <= 1.0F) {
      if (widthRatio < heightRatio) {
        // 证明相对而言，宽度相对来说占比比较小，应该让宽度撑满,高度超出，然后把高度裁切
        newVideoSize.width = containerSize.width;
        newVideoSize.height = (int) (videoSize.height / widthRatio);
        // 计算偏移量
        deltaPoint = new UIModel.Point(0, (containerSize.height - newVideoSize.height) / 2);
      } else {
        // 证明相对而言，高度相对来说占比比较小，应该让高度撑满,高度超出，然后把宽度裁切
        newVideoSize.width = (int) (videoSize.width / heightRatio);
        newVideoSize.height = containerSize.height;
        // 计算偏移量
        deltaPoint = new UIModel.Point((containerSize.width - newVideoSize.width) / 2, 0);
      }
    } else {
      newVideoSize.width = videoSize.width;
      newVideoSize.height = videoSize.height;
      // 宽高都是超出屏幕的，直接裁剪就好，不压缩
      deltaPoint = new UIModel.Point((containerSize.width - newVideoSize.width) / 2,
          (containerSize.height - newVideoSize.height) / 2);
    }
    return new AdapterModel(newVideoSize, deltaPoint);
  }
}
