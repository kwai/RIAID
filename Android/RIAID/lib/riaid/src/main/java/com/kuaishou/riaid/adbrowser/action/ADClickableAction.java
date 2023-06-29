package com.kuaishou.riaid.adbrowser.action;

import java.util.Map;

import android.util.Pair;
import android.view.View;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowser;
import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.helper.ADBrowserKeyHelper;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.ADClickableActionModel;
import com.kuaishou.riaid.render.interf.IRealViewWrapper;

/**
 * 控制视图是否可点击的行为，即使是设置点击的handler，如果clickable为false，也不支持点击。
 */
public class ADClickableAction extends ADBaseAction<ADClickableActionModel> {
  private static final String TAG = "ADClickableAction";
  /**
   * 需要场景检索到指定的控件
   */
  @NonNull
  private final Map<Integer, ADScene> mADScenes;

  public ADClickableAction(
      @NonNull ADBrowserContext browserContext,
      @NonNull Map<Integer, ADScene> adScenes,
      @NonNull ADClickableActionModel adActionModel) {
    super(browserContext, adActionModel);
    mADScenes = adScenes;
  }

  @Override
  public boolean execute() {
    if (!ADBrowserKeyHelper.isValidKey(mADActionModel.viewKey)) {
      ADBrowserLogger.w(TAG + " viewKey不合法:" + mADActionModel.viewKey);
      return false;
    }
    Pair<ADScene, IRealViewWrapper> sceneAndViewByKey = ADBrowser
        .findSceneAndViewWrapperByKey(mADScenes, mADActionModel.viewKey);
    if (sceneAndViewByKey == null || sceneAndViewByKey.second == null) {
      ADBrowserLogger.w(TAG + " 没有找到的对应的view:" + mADActionModel.viewKey);
      return false;
    }
    View gestureView = sceneAndViewByKey.second.getGestureView();
    if (gestureView == null) {
      ADBrowserLogger.w(TAG + " 没有找到的对应的可点击的view:" + mADActionModel.viewKey);
      return false;
    }
    ADBrowserLogger.i(TAG + " setClickable:" + mADActionModel.clickable + " viewKey: " +
        mADActionModel.viewKey);
    // 设置是否可点击
    gestureView.setClickable(mADActionModel.clickable);
    return true;
  }
}
