package com.kuaishou.riaid.render.service.base;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

/**
 * 用于上报RIAID的一些数据指标，例：UI渲染时长
 * https://docs.corp.kuaishou.com/d/home/fcABAVQfeA8ge5jBA6uGQLOK7#section=vodka.oecpmkoge84l
 */
public interface IRIAIDLogReportService {
  /**
   * @param key   自定义event key自定义string即可
   * @param value 数值
   */
  void riaidLogEvent(@NonNull String eventName, @NonNull JsonObject eventParams);
}
