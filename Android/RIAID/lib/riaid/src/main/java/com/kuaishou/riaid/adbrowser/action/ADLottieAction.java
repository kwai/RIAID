package com.kuaishou.riaid.adbrowser.action;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.util.Pair;
import android.view.View;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowser;
import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.helper.ADBrowserKeyHelper;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.ADLottieActionModel;
import com.kuaishou.riaid.render.constants.DispatchEventType;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;

/**
 * Lottie控制行为，控制RIAID内部的Lottie控件
 */
public class ADLottieAction extends ADBaseAction<ADLottieActionModel> {
  private static final String TAG = "ADVideoAction";
  /**
   * 需要场景检索到指定的视频控件
   */
  @NonNull
  private final Map<Integer, ADScene> mADScenes;

  public ADLottieAction(
      @NonNull ADBrowserContext browserContext,
      @NonNull Map<Integer, ADScene> adScenes,
      @NonNull ADLottieActionModel adActionModel) {
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
        case ADLottieActionModel.LOTTIE_PLAY:
          rootRender.dispatchEvent(DispatchEventType.LOTTIE_PLAY, views, null);
          break;
        case ADLottieActionModel.LOTTIE_REPLAY:
          rootRender.dispatchEvent(DispatchEventType.LOTTIE_REPLAY, views, null);
          break;
        case ADLottieActionModel.LOTTIE_PAUSE:
          rootRender.dispatchEvent(DispatchEventType.LOTTIE_PAUSE, views, null);
          break;
        case ADLottieActionModel.LOTTIE_POSITION_RESET:
          rootRender.dispatchEvent(DispatchEventType.LOTTIE_RESET, views, null);
          break;
        default:
          ADBrowserLogger.w(TAG + " 不支持的Lottie控制 type: " + mADActionModel.type);
          return false;
      }
    }
    return true;
  }
}
