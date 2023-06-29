package com.kuaishou.riaid.adbrowser.service;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.helper.ADBrowserStringHelper;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.programming.ADVariableOperator;
import com.kuaishou.riaid.adbrowser.programming.BasicVariableDecorator;
import com.kuaishou.riaid.proto.nano.BasicVariable;
import com.kuaishou.riaid.render.service.base.IDataBindingService;

/**
 * browser内部使用的变量替换服务，先将要替换的数据在全局变量中找，如果有直接返回值，如果没有，返回空
 */
public class VariableDataBindingService implements IDataBindingService {
  private static final String TAG = "VariableDataBindingServ";
  /**
   * 需要通过context找到全局变量
   */
  @NonNull
  private final ADBrowserContext mBrowserContext;

  /**
   * @param browserContext 需要通过context找到变量池
   */
  public VariableDataBindingService(@NonNull ADBrowserContext browserContext) {
    mBrowserContext = browserContext;
  }

  /**
   * 先将dataHolder转换成整形，从变量池中匹配，如果匹配上直接返回，如果匹配不上再
   * 匹配其他的变量
   *
   * @param dataHolder 占位符key，如果是内部变量，则dataHolder应该可以替换为整形。
   * @return 占位符对应的变量值
   */
  @NonNull
  @Override
  public String parseDataHolder(@NonNull String dataHolder) {
    String result = null;
    boolean isInteger = ADBrowserStringHelper.isInteger(dataHolder);
    if (isInteger) {
      ADVariableOperator variableOperator = mBrowserContext.getVariableOperator();
      // 如果是整形，有可能是需要匹配变量池
      try {
        int variableKey = Integer.parseInt(dataHolder);
        BasicVariable variable = variableOperator.findVariableByKey(variableKey);
        if (ADVariableOperator.isValidVariable(variable)) {
          result = new BasicVariableDecorator(variable).stringValue();
        } else {
          ADBrowserLogger.i("变量转换字符串为空，交给其他定义的变量替换");
        }
      } catch (NumberFormatException e) {
        ADBrowserLogger
            .e(TAG + " dataHolder parseInt 解析失败 dataHolder：" + dataHolder);
      }
    }
    if (result == null) {
      result = "";
    }
    return result;
  }

}