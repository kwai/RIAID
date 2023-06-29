package com.kuaishou.riaid.adbrowser.event;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.proto.nano.ADConversionActionModel;
import com.kuaishou.riaid.proto.nano.ADCustomActionModel;
import com.kuaishou.riaid.proto.nano.ADTrackActionModel;
import com.kuaishou.riaid.proto.nano.ADUrlActionModel;
import com.kuaishou.riaid.proto.nano.ADVideoActionModel;

/**
 * 对外输出的一些事件监听，如埋点上报。这个监听是由外层实现的，
 * 添加到{@link com.kuaishou.riaid.adbrowser.ADBrowser}的
 * {@link ADBrowserContext}中。
 * 监听通常由{@link com.kuaishou.riaid.adbrowser.action.ADAction}回调。
 */
public interface ADBrowserMetricsEventListener {
  /**
   * 通常用{@link com.kuaishou.riaid.adbrowser.action.ADTrackAction}回调
   *
   * @param action 埋点的行为数据模型，是在dsl中定义的，上层可以拿到，去做埋点上报
   */
  default void onTrackEvent(@NonNull ADTrackActionModel action) {}

  /**
   * @param action 视频行为数据模型，是在dsl中定义的，上层可以拿到，去做视频的控制
   */
  default void onVideoEvent(@NonNull ADVideoActionModel action) {}

  /**
   * @param action url行为数据模型，上是在dsl中定义的，层可以拿到，可以跳转落地页，也可以走deeplink
   */
  default void onUrlEvent(@NonNull ADUrlActionModel action) {}

  /**
   * @param action 转化的行为数据模型，上是在dsl中定义的，层可以拿到，可以跳转落地页，也可以走deeplink
   */
  default void onConversionEvent(@NonNull ADConversionActionModel action) {}

  /**
   * @param action 自定义的数据模型，透传给上层使用
   */
  default void onCustomEvent(@NonNull ADCustomActionModel action) {}

  /**
   * RIAID内部的数据指标
   */
  default void onRIAIDLogEvent(@Nullable String eventName, @Nullable JsonObject eventParams) {}

}
