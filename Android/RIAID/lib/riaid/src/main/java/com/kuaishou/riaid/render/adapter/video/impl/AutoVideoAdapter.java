package com.kuaishou.riaid.render.adapter.video.impl;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.proto.nano.VideoAttributes;
import com.kuaishou.riaid.render.adapter.video.base.BaseVideoAdapter;
import com.kuaishou.riaid.render.model.UIModel;

/**
 * 这个是默认的适配规则，距离的适配逻辑取决于端上的逻辑，服务端不关心
 */
public class AutoVideoAdapter extends BaseVideoAdapter {

  @Override
  public int getVideoAdapterType() {
    return VideoAttributes.ADAPTER_TYPE_AUTO;
  }

  @NonNull
  @Override
  public AdapterModel adapter(@NonNull UIModel.Size containerSize,
      @NonNull UIModel.Size videoSize) {
    /**
     * 暂时auto就是center_crop，后面可以修改
     */
    return new CenterCropVideoAdapter().adapter(containerSize, videoSize);
  }
}
