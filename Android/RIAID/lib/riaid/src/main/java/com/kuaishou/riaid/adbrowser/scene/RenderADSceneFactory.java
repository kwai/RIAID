package com.kuaishou.riaid.adbrowser.scene;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.proto.nano.ADSceneModel;

/**
 * 会提供一个Render渲染的场景
 */
public class RenderADSceneFactory implements ADSceneFactory {
  @NonNull
  @Override
  public ADScene createADScene(@NonNull ADBrowserContext context,
      @NonNull ADSceneModel adSceneModel) {
    return new RenderADScene(context, adSceneModel);
  }
}
