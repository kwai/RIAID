package com.kuaishou.riaid.adbrowser.action;

import java.util.Map;

import android.text.TextUtils;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.event.ADBrowserMetricsEventListener;
import com.kuaishou.riaid.proto.nano.ADTrackActionModel;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * 埋点行为，通过{@link ADBrowserMetricsEventListener}回调给上层，
 * 由上层处理。
 */
public class ADTrackAction extends ADBaseAction<ADTrackActionModel> {

  public ADTrackAction(
      @NonNull ADBrowserContext browserContext,
      @NonNull ADTrackActionModel adActionModel) {
    super(browserContext, adActionModel);
  }

  @Override
  public boolean execute() {
    if (mADActionModel.parameters == null) {
      return false;
    }
    for (Map.Entry<String, String> entry : mADActionModel.parameters.entrySet()) {
      // 埋点数据需要先经过数据绑定服务，在这一层做一次替换
      String value = ToolHelper.resolveValue(mBrowserContext.getBindingService(), entry.getValue());
      if (!TextUtils.isEmpty(value)) {
        // 如果不为空则认为替换成功，需要重新给value赋值
        entry.setValue(value);
      }
    }
    // 将埋点数据通过回调透传到上层
    mBrowserContext.getADBrowserMetricsEventListener().onTrackEvent(mADActionModel);
    return true;
  }
}
