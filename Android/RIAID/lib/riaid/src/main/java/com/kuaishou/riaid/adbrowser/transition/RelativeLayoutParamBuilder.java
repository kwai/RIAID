package com.kuaishou.riaid.adbrowser.transition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.proto.nano.ADSceneRelationModel;
import com.kuaishou.riaid.proto.nano.SystemKeyEnum;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * {@link RelativeLayout.LayoutParams}的构建器
 * 会根据数据模型去构建其约束关系和外间距
 */
public class RelativeLayoutParamBuilder {

  private RelativeLayoutParamBuilder() {}

  /**
   * 给到一个{@link RelativeLayout.LayoutParams}，根据数据模型，添加相应的约束关系
   *
   * @param layoutParams       场景的布局参数，{@link RelativeLayout.LayoutParams}
   * @param sceneRelationModel 关系的数据模型
   * @param targetViewId       相对对象的id，如果为{@link SystemKeyEnum#SCENE_KEY_CANVAS}，则认为是相对于父布局
   * @return {@link RelativeLayout.LayoutParams}
   */
  @SuppressLint("SwitchIntDef")
  @SuppressWarnings("UnusedReturnValue")
  public static RelativeLayout.LayoutParams buildLayoutParam(
      @NonNull RelativeLayout.LayoutParams layoutParams,
      @NonNull ADSceneRelationModel sceneRelationModel,
      int targetViewId) {
    switch (sceneRelationModel.sourceEdge) {
      case ADSceneRelationModel.START:
        contactSourceStartRelation(layoutParams, sceneRelationModel, targetViewId);
        break;
      case ADSceneRelationModel.END:
        contractSourceEndRelation(layoutParams, sceneRelationModel, targetViewId);
        break;
      case ADSceneRelationModel.TOP:
        contractSourceTopRelation(layoutParams, sceneRelationModel, targetViewId);
        break;
      case ADSceneRelationModel.BOTTOM:
        contractSourceBottomRelation(layoutParams, sceneRelationModel, targetViewId);
        break;
      case ADSceneRelationModel.SCENE_EDGE_NONE:
        break;
      default:
        ADRenderLogger
            .e("RelativeLayoutParamBuilder buildLayoutParam 提供了不支持的边 sourceEdge：" +
                sceneRelationModel.sourceEdge);
        break;
    }
    return layoutParams;
  }

  /**
   * 约束start边的关系
   */
  @SuppressLint("SwitchIntDef")
  private static void contactSourceStartRelation(@NonNull RelativeLayout.LayoutParams layoutParams,
      @NonNull ADSceneRelationModel sceneRelationModel, int targetViewId) {
    removeAllHorizontalRelation(layoutParams);
    switch (sceneRelationModel.targetEdge) {
      case ADSceneRelationModel.START:
        addParamRule(layoutParams, targetViewId, RelativeLayout.ALIGN_PARENT_START,
            RelativeLayout.ALIGN_START);
        break;
      case ADSceneRelationModel.END:
        if (targetViewId == SystemKeyEnum.SCENE_KEY_CANVAS) {
          ADBrowserLogger.e("Relation 发生了冲突，source的start要求与canvas的end齐平，不支持，已兼容start-start");
        }
        addParamRule(layoutParams, targetViewId, RelativeLayout.ALIGN_PARENT_START,
            RelativeLayout.END_OF);
        break;
      default:
        ADRenderLogger
            .e("Relation 发生了冲突，source的start匹配了target非横向的边 targetEdge:" +
                sceneRelationModel.targetEdge);
        break;
    }
  }


  /**
   * 约束end边的关系
   */
  @SuppressLint("SwitchIntDef")
  private static void contractSourceEndRelation(@NonNull RelativeLayout.LayoutParams layoutParams,
      @NonNull ADSceneRelationModel sceneRelationModel, int targetViewId) {
    removeAllHorizontalRelation(layoutParams);
    switch (sceneRelationModel.targetEdge) {
      case ADSceneRelationModel.START:
        if (targetViewId == SystemKeyEnum.SCENE_KEY_CANVAS) {
          ADBrowserLogger.e("Relation 发生了冲突，source的end要求与canvas的start齐平，不支持，已兼容end-end");
        }
        addParamRule(layoutParams, targetViewId, RelativeLayout.ALIGN_PARENT_END,
            RelativeLayout.START_OF);
        break;
      case ADSceneRelationModel.END:
        addParamRule(layoutParams, targetViewId, RelativeLayout.ALIGN_PARENT_END,
            RelativeLayout.ALIGN_END);
        break;
      default:
        ADRenderLogger
            .e("Relation 发生了冲突，source的end匹配了target非横向的边 targetEdge:" +
                sceneRelationModel.targetEdge);
        break;
    }
  }


  /**
   * 约束top边的关系
   */
  @SuppressLint("SwitchIntDef")
  private static void contractSourceTopRelation(@NonNull RelativeLayout.LayoutParams layoutParams,
      @NonNull ADSceneRelationModel sceneRelationModel, int targetViewId) {
    removeAllVerticalRelation(layoutParams);
    switch (sceneRelationModel.targetEdge) {
      case ADSceneRelationModel.TOP:
        addParamRule(layoutParams, targetViewId, RelativeLayout.ALIGN_PARENT_TOP,
            RelativeLayout.ALIGN_TOP);
        break;
      case ADSceneRelationModel.BOTTOM:
        if (targetViewId == SystemKeyEnum.SCENE_KEY_CANVAS) {
          ADBrowserLogger.e("Relation 发生了冲突，source的top要求与canvas的bottom齐平，不支持，已兼容top-top");
        }
        addParamRule(layoutParams, targetViewId, RelativeLayout.ALIGN_PARENT_TOP,
            RelativeLayout.BELOW);
        break;
      default:
        ADRenderLogger
            .e("Relation 发生了冲突，source的top匹配了target非纵向的边 targetEdge:" +
                sceneRelationModel.targetEdge);
        break;
    }
  }


  /**
   * 约束bottom边的关系
   */
  @SuppressLint("SwitchIntDef")
  private static void contractSourceBottomRelation(
      @NonNull RelativeLayout.LayoutParams layoutParams,
      @NonNull ADSceneRelationModel sceneRelationModel, int targetViewId) {
    removeAllVerticalRelation(layoutParams);
    switch (sceneRelationModel.targetEdge) {
      case ADSceneRelationModel.TOP:
        if (targetViewId == SystemKeyEnum.SCENE_KEY_CANVAS) {
          ADBrowserLogger.e("Relation 发生了冲突，source的bottom要求与canvas的top齐平，不支持，已兼容bottom-bottom");
        }
        addParamRule(layoutParams, targetViewId, RelativeLayout.ALIGN_PARENT_BOTTOM,
            RelativeLayout.ABOVE);
        break;
      case ADSceneRelationModel.BOTTOM:
        addParamRule(layoutParams, targetViewId, RelativeLayout.ALIGN_PARENT_BOTTOM,
            RelativeLayout.ALIGN_BOTTOM);
        break;
      default:
        ADRenderLogger
            .e("Relation 发生了冲突，source的bottom匹配了target非纵向的边 targetEdge:" +
                sceneRelationModel.targetEdge);
        break;
    }
  }


  /**
   * 添加约束规则
   *
   * @param layoutParams 要约束view的布局参数
   * @param targetViewId 目标view的id
   * @param verbCanvas   a layout verb, such as {@link RelativeLayout#ALIGN_RIGHT}
   * @param verbTarget   a layout verb, such as {@link RelativeLayout#ALIGN_RIGHT}
   *                     注意，这里会有兼容逻辑，targetViewId是Canvas时，如果source的边和canvas的边不是一个，则会兼容成一个
   *                     例：sourceEdge = start targetEdge = end target = Canvas，则认为不合法。
   */
  private static void addParamRule(
      @NonNull RelativeLayout.LayoutParams layoutParams, int targetViewId,
      int verbCanvas, int verbTarget) {
    if (targetViewId == SystemKeyEnum.SCENE_KEY_CANVAS) {
      layoutParams.addRule(verbCanvas, RelativeLayout.TRUE);
    } else {
      layoutParams.addRule(verbTarget, targetViewId);
    }
  }

  /**
   * 给到一个{@link RelativeLayout.LayoutParams}，根据数据模型添加外间距
   *
   * @param context            {@link Context}
   * @param layoutParams       需要添加外间距的{@link RelativeLayout.LayoutParams}
   * @param sceneRelationModel 添加外间距的数据模型
   * @return 添加了外间距的LayoutParams
   */
  @SuppressLint("SwitchIntDef")
  @SuppressWarnings("UnusedReturnValue")
  public static RelativeLayout.LayoutParams buildLayoutMargin(@NonNull Context context,
      @NonNull RelativeLayout.LayoutParams layoutParams,
      @NonNull ADSceneRelationModel sceneRelationModel) {
    switch (sceneRelationModel.sourceEdge) {
      case ADSceneRelationModel.START:
        layoutParams
            .setMarginStart(ToolHelper.dip2px(context, sceneRelationModel.distance));
        break;
      case ADSceneRelationModel.END:
        layoutParams
            .setMarginEnd(ToolHelper.dip2px(context, sceneRelationModel.distance));
        break;
      case ADSceneRelationModel.TOP:
        layoutParams
            .topMargin = ToolHelper.dip2px(context, sceneRelationModel.distance);
        break;
      case ADSceneRelationModel.BOTTOM:
        layoutParams
            .bottomMargin = ToolHelper.dip2px(context, sceneRelationModel.distance);
        break;
      case ADSceneRelationModel.SCENE_EDGE_NONE:
        break;
      default:
        ADRenderLogger
            .e("RelativeLayoutParamBuilder buildLayoutMargin 提供了不支持的边 sourceEdge：" +
                sceneRelationModel.sourceEdge);
        break;
    }
    return layoutParams;
  }

  /**
   * 移除该布局配置的横向的所有约束
   *
   * @param params {@link RelativeLayout.LayoutParams}
   */
  public static void removeAllHorizontalRelation(@NonNull RelativeLayout.LayoutParams params) {
    params.removeRule(RelativeLayout.ALIGN_RIGHT);
    params.removeRule(RelativeLayout.ALIGN_END);
    params.removeRule(RelativeLayout.ALIGN_PARENT_START);
    params.removeRule(RelativeLayout.ALIGN_PARENT_END);
    params.removeRule(RelativeLayout.START_OF);
    params.removeRule(RelativeLayout.END_OF);
    params.removeRule(RelativeLayout.CENTER_HORIZONTAL);
    params.removeRule(RelativeLayout.CENTER_IN_PARENT);
  }

  /**
   * 移除该布局配置的纵向的所有约束
   *
   * @param params {@link RelativeLayout.LayoutParams}
   */
  public static void removeAllVerticalRelation(@NonNull RelativeLayout.LayoutParams params) {
    params.removeRule(RelativeLayout.ALIGN_TOP);
    params.removeRule(RelativeLayout.ALIGN_BOTTOM);
    params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
    params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    params.removeRule(RelativeLayout.ABOVE);
    params.removeRule(RelativeLayout.BELOW);
    params.removeRule(RelativeLayout.CENTER_VERTICAL);
    params.removeRule(RelativeLayout.CENTER_IN_PARENT);
  }
}
