package com.kuaishou.riaid.adbrowser.transition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.util.Pair;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowser;
import com.kuaishou.riaid.adbrowser.ADBrowserConstants;
import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.helper.ADBrowserKeyHelper;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.ADRenderContentTransitionModel;
import com.kuaishou.riaid.render.config.DSLRenderCreator;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.logger.RiaidLogger;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;

/**
 * 常用于场景内的动画，用于更改render的属性来改变其内容，如文本信息
 */
public class ADRenderContentTransitionExecutor extends ADBaseTransitionExecutor {
  private static final String TAG = "ADRenderContentTransiti";
  @Nullable
  private List<ADRenderContentTransitionModel> mADRenderContentTransitionModels;

  public ADRenderContentTransitionExecutor(
      @NonNull ADBrowserContext context,
      @NonNull Map<Integer, ADScene> adScenes) {
    super(context, adScenes);
  }

  public void setADRenderContentTransitionModels(
      @Nullable List<ADRenderContentTransitionModel> ADLottieTransitionModels) {
    mADRenderContentTransitionModels = ADLottieTransitionModels;
  }

  @Override
  public void execute() {
    if (mADRenderContentTransitionModels == null) {
      ADRenderLogger
          .e("ADRenderContentTransitionExecutor 执行失败 mADRenderContentTransitionModels 为空");
      return;
    }
    ADBrowserLogger.i("ADRenderContentTransitionExecutor mADRenderContentTransitionModels: " +
        RiaidLogger.objectToString(mADRenderContentTransitionModels));
    for (ADRenderContentTransitionModel renderContentTransitionModel :
        mADRenderContentTransitionModels) {
      if (renderContentTransitionModel == null) {
        ADBrowserLogger.e("ADRenderContentTransitionExecutor renderContentTransitionModel为空");
        continue;
      }
      Pair<ADScene, View> sceneAndViewByKey = ADBrowser
          .findSceneAndViewByKey(mADScenes, renderContentTransitionModel.viewKey);

      if (sceneAndViewByKey == null || sceneAndViewByKey.first.getRenderCreator() == null ||
          sceneAndViewByKey.first.getRenderCreator().rootRender == null) {
        ADBrowserLogger.e(TAG + " 查找view失败，viewKey: " + renderContentTransitionModel.viewKey);
        continue;
      }
      DSLRenderCreator render = sceneAndViewByKey.first.getRenderCreator();
      AbsObjectNode<?> rootRender = render.rootRender;
      if (rootRender != null) {
        rootRender.dispatchEvent(ADBrowserConstants.ATTRIBUTE,
            !ADBrowserKeyHelper.isValidKey(renderContentTransitionModel.viewKey)
                ? new ArrayList<>()
                : Collections.singletonList(renderContentTransitionModel.viewKey),
            renderContentTransitionModel.renderAttributes);
        render.requestLayout();
      }
    }
  }
}
