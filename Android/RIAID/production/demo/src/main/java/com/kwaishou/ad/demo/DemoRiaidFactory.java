package com.kwaishou.ad.demo;

import com.kuaishou.riaid.proto.ADBrowserLifeCycleModel;
import com.kuaishou.riaid.proto.ADGeneralTriggerModel;
import com.kuaishou.riaid.proto.ADRenderWrapModel;
import com.kuaishou.riaid.proto.ADSceneModel;
import com.kuaishou.riaid.proto.ADSceneRelationModel;
import com.kuaishou.riaid.proto.ADTriggerModel;
import com.kuaishou.riaid.proto.RiaidModel;
import com.kuaishou.riaid.proto.SystemKeyEnum;
import com.kwaishou.riaid.production.factory.ActionFactory;

public class DemoRiaidFactory {
  int scene_01 = 100001;
  int show_scene_01 = 200001;

  public RiaidModel create() {
    return RiaidModel.newBuilder()
        // 添加一个场景
        .addScenes(ADSceneModel.newBuilder()
            .setKey(scene_01)
            .setRender(ADRenderWrapModel.newBuilder()
                .setRenderData(new WhiteWithButton().createNode())
                .build())
            .build())
        // 添加的场景在画布中的位置
        .addDefaultSceneRelations(
            ADSceneRelationModel.newBuilder()
                .setSourceKey(scene_01)
                // 相对于目标的位置
                .setTargetKey(SystemKeyEnum.SystemKeys.SCENE_KEY_CANVAS_VALUE)
                // 这个协议是说明场景与画布左对齐，距离左边是20dp
                .setDistance(20)
                .setSourceEdge(ADSceneRelationModel.SceneEdge.START)
                .setTargetEdge(ADSceneRelationModel.SceneEdge.START)
                .build()
        )
        .addDefaultSceneRelations(
            ADSceneRelationModel.newBuilder()
                .setSourceKey(scene_01)
                // 相对于目标的位置
                .setTargetKey(SystemKeyEnum.SystemKeys.SCENE_KEY_CANVAS_VALUE)
                // 这个协议是说明场景与画布底部对齐，距离底部20dp
                .setDistance(20)
                .setSourceEdge(ADSceneRelationModel.SceneEdge.BOTTOM)
                .setTargetEdge(ADSceneRelationModel.SceneEdge.BOTTOM)
                .build()
        )
        // 这是展示场景一的触发器
        .addTriggers(ADTriggerModel.newBuilder()
            .setGeneral(
                ADGeneralTriggerModel.newBuilder()
                    .setKey(show_scene_01)
                    .addActions(ActionFactory.createVisibility(
                        scene_01, 0, 0, 1, false
                    ))
                    .build()
            )
            .build())
        .setLifeCycle(ADBrowserLifeCycleModel.newBuilder()
            // 当加载的时候，就执行触发器
            .addLoadTriggerKeys(show_scene_01)
            .build())
        .build();
  }
}
