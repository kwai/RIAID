package com.kuaishou.riaid.render.util;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.item.VideoItemNode;
import com.kuaishou.riaid.render.service.base.IMediaPlayerService;
import com.kuaishou.riaid.render.service.inner.IFindNodeByKeyService;

/**
 * 获取控件的属性
 */
public class AttributeGetter {

  /**
   * 获取指定key的VideoNode控件的当前视频播放位置
   *
   * @param key           组件的唯一标识
   * @param attributeType 属性枚举标识
   * @return 返回当前播放时长
   */
  public static long getAttributeVideoPosition(int key, int attributeType,
      @NonNull AbsObjectNode<?> rootNode) {
    IFindNodeByKeyService service =
        rootNode.mNodeInfo.serviceContainer.getService(IFindNodeByKeyService.class);
    if (attributeType == Attributes.ATTRIBUTE_VIDEO_POSITION && service != null) {
      AbsObjectNode<?> resultNode = service.findNodeByKey(key);
      if (resultNode instanceof VideoItemNode) {
        VideoItemNode videoNode = (VideoItemNode) resultNode;
        IMediaPlayerService mediaPlayerService =
            videoNode.mNodeInfo.serviceContainer.getService(IMediaPlayerService.class);
        if (mediaPlayerService != null) {
          return mediaPlayerService.getCurrentPosition();
        }
      } else {
        ADRenderLogger.e("key = " + key + " key doesn't match the node type");
      }
    }
    return 0;
  }

  /**
   * 获取指定key的VideoNode控件的当前视频播放位置
   *
   * @param key           组件的唯一标识
   * @param attributeType 属性枚举标识
   * @return 返回当前播放时长
   */
  public static long getAttributeVideoTotalDuration(int key, int attributeType,
      @NonNull AbsObjectNode<?> rootNode) {
    IFindNodeByKeyService service =
        rootNode.mNodeInfo.serviceContainer.getService(IFindNodeByKeyService.class);
    if (attributeType == Attributes.ATTRIBUTE_VIDEO_TOTAL_DURATION && service != null) {
      AbsObjectNode<?> resultNode = service.findNodeByKey(key);
      if (resultNode instanceof VideoItemNode) {
        VideoItemNode videoNode = (VideoItemNode) resultNode;
        return videoNode.getVideoTotalPlayDuration();
      } else {
        ADRenderLogger.e("key doesn't match the node type");
      }
    }
    return 0;
  }

}
