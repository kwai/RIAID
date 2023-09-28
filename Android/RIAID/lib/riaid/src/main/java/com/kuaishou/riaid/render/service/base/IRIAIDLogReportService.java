package com.kuaishou.riaid.render.service.base;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

/**
 * 用于上报RIAID的一些数据指标，例：UI渲染时长
 */
public interface IRIAIDLogReportService {
  /**
   * @param key   自定义event key自定义string即可
   * @param value 数值
   */
  void riaidLogEvent(@NonNull String eventName, @NonNull JsonObject eventParams);
}
