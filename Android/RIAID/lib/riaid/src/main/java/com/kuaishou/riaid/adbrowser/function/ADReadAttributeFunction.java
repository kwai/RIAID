package com.kuaishou.riaid.adbrowser.function;


import java.util.Map;

import android.util.Pair;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowser;
import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.ADReadAttributeFunctionModel;
import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.util.AttributeGetter;

/**
 * 读取Render某个视图的属性的函数，{@link #execute()}会返回定义的属性对应的值。
 */
public class ADReadAttributeFunction extends ADBaseAttributeFunction<ADReadAttributeFunctionModel> {
  private static final String TAG = "ADReadAttributeFunction";
  @NonNull
  private final Map<Integer, ADScene> mADScenes;

  public ADReadAttributeFunction(@NonNull ADBrowserContext browserContext,
      @NonNull ADReadAttributeFunctionModel functionModel,
      @NonNull Map<Integer, ADScene> adScenes) {
    super(browserContext, functionModel);
    mADScenes = adScenes;
  }

  @Override
  @Nullable
  public String execute() {
    Pair<ADScene, View> sceneAndViewByKey = ADBrowser
        .findSceneAndViewByKey(mADScenes, mFunctionModel.viewKey);

    if (sceneAndViewByKey == null || sceneAndViewByKey.first.getRenderCreator() == null ||
        sceneAndViewByKey.first.getRenderCreator().rootRender == null) {
      ADBrowserLogger.e(TAG + " 查找view失败，viewKey: " + mFunctionModel.viewKey);
      return null;
    }
    AbsObjectNode<?> rootRender = sceneAndViewByKey.first.getRenderCreator().rootRender;

    switch (mFunctionModel.attributeType) {
      case Attributes.ATTRIBUTE_VIDEO_POSITION:
        long position =
            AttributeGetter.getAttributeVideoPosition(mFunctionModel.viewKey,
                mFunctionModel.attributeType, rootRender);
        return String.valueOf(position);
      case Attributes.ATTRIBUTE_VIDEO_TOTAL_DURATION:
        long totalDuration =
            AttributeGetter.getAttributeVideoTotalDuration(mFunctionModel.viewKey,
                mFunctionModel.attributeType, rootRender);
        return String.valueOf(totalDuration);
    }
    return null;
  }

  @Override
  public int getKey() {
    return mFunctionModel.key;
  }
}
