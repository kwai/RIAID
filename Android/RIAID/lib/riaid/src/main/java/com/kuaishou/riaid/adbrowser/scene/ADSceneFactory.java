package com.kuaishou.riaid.adbrowser.scene;

import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.proto.nano.ADSceneModel;

/**
 * 创建场景的工厂接口，会根据{@link ADSceneModel}创建一个{@link ADScene}
 */
public interface ADSceneFactory {
  /**
   * @param adSceneModel 构建一个场景的model
   * @return 提供一个场景，不能为空，场景可以是自定义的，也可以是Render渲染的
   */
  @Nullable
  ADScene createADScene(ADBrowserContext context, ADSceneModel adSceneModel);
}
