package com.kuaishou.riaid.adbrowser.action;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.util.Pair;
import android.view.View;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowser;
import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.event.ADBrowserMetricsEventListener;
import com.kuaishou.riaid.adbrowser.helper.ADBrowserKeyHelper;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.ADVideoActionModel;
import com.kuaishou.riaid.render.constants.DispatchEventType;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;

/**
 * 视频控制行为，控制RIAID内部的视频控件或通过{@link ADBrowserMetricsEventListener}回调给上层，
 * 由上层处理。
 */
public class ADVideoAction extends ADBaseAction<ADVideoActionModel> {
  private static final String TAG = "ADVideoAction";
  /**
   * 需要场景检索到指定的视频控件
   */
  @NonNull
  private final Map<Integer, ADScene> mADScenes;

  public ADVideoAction(
      @NonNull ADBrowserContext browserContext,
      @NonNull Map<Integer, ADScene> adScenes,
      @NonNull ADVideoActionModel adActionModel) {
    super(browserContext, adActionModel);
    mADScenes = adScenes;
  }

  @Override
  public boolean execute() {
    // 如果场景和视图的key是有效的，则是RIAID内部自己的播放器，这些视频操作通过Render控制。
    // 如果key是无效的，则是上层的播放器，通过事件通知上层，由上层控制。
    if (ADBrowserKeyHelper.isValidKey(mADActionModel.viewKey)) {

      Pair<ADScene, View> sceneAndViewByKey = ADBrowser
          .findSceneAndViewByKey(mADScenes, mADActionModel.viewKey);

      if (sceneAndViewByKey == null || sceneAndViewByKey.first.getRenderCreator() == null ||
          sceneAndViewByKey.first.getRenderCreator().rootRender == null) {
        ADBrowserLogger.e(TAG + " 查找view失败，viewKey: " + mADActionModel.viewKey);
        return false;
      }

      AbsObjectNode<?> rootRender = sceneAndViewByKey.first.getRenderCreator().rootRender;

      List<Integer> views = Collections.singletonList(mADActionModel.viewKey);
      switch (mADActionModel.type) {
        case ADVideoActionModel.VIDEO_PLAY:
          rootRender.dispatchEvent(DispatchEventType.VIDEO_PLAY, views, null);
          break;
        case ADVideoActionModel.VIDEO_PAUSE:
          rootRender.dispatchEvent(DispatchEventType.VIDEO_PAUSE, views, null);
          break;
        case ADVideoActionModel.VIDEO_SOUND_TURN_OFF:
          rootRender.dispatchEvent(DispatchEventType.VIDEO_SOUND_TURN_OFF, views, null);
          break;
        case ADVideoActionModel.VIDEO_SOUND_TURN_ON:
          rootRender.dispatchEvent(DispatchEventType.VIDEO_SOUND_TURN_ON, views, null);
          break;
        case ADVideoActionModel.VIDEO_REPLAY:
          rootRender.dispatchEvent(DispatchEventType.VIDEO_REPLAY, views, null);
          break;
        case ADVideoActionModel.VIDEO_POSITION_RESET:
          rootRender.dispatchEvent(DispatchEventType.VIDEO_RESET, views, null);
          break;
        default:
          ADBrowserLogger.w(TAG + " 不支持的视频控制 type: " + mADActionModel.type);
          return false;
      }
    } else {
      mBrowserContext.getADBrowserMetricsEventListener().onVideoEvent(mADActionModel);
    }
    return true;
  }
}
