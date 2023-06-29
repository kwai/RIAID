package com.kuaishou.riaid.adbrowser.service;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.function.ADFunctionOperator;
import com.kuaishou.riaid.adbrowser.helper.ADBrowserStringHelper;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.render.service.base.IDataBindingService;

/**
 * browser内部使用的函数返回值的替换服务，先将要替换的数据在全局变量中找，如果有直接返回值，如果没有，返回空
 */
public class FunctionDataBindingService implements IDataBindingService {
  private static final String TAG = "FunctionDataBindingServ";
  /**
   * 需要通过context找到全局变量
   */
  @NonNull
  private final ADBrowserContext mBrowserContext;

  /**
   * @param browserContext 需要通过context找到变量池
   */
  public FunctionDataBindingService(@NonNull ADBrowserContext browserContext) {
    mBrowserContext = browserContext;
  }

  /**
   * 先将dataHolder转换成整形，从函数池中匹配，如果匹配上直接返回，如果匹配不上再
   * 匹配其他的函数
   *
   * @param dataHolder 占位符key，如果是函数的key，则dataHolder应该可以替换为整形。
   * @return 占位符对应的变量值
   */
  @NonNull
  @Override
  public String parseDataHolder(@NonNull String dataHolder) {
    String result = null;
    boolean isInteger = ADBrowserStringHelper.isInteger(dataHolder);
    if (isInteger) {
      ADFunctionOperator functionOperator = mBrowserContext.getFunctionOperator();
      if (functionOperator != null) {
        // 如果是整形，有可能是需要匹配函数池
        try {
          int functionKey = Integer.parseInt(dataHolder);
          result = functionOperator.executeFunctionByKey(functionKey);
        } catch (NumberFormatException e) {
          ADBrowserLogger
              .e(TAG + " dataHolder parseInt 解析失败 dataHolder：" + dataHolder);
        }
      } else {
        ADBrowserLogger.i(TAG + "解析失败，functionOperator 为空");
      }
    }
    if (result == null) {
      result = "";
    }
    return result;
  }

}