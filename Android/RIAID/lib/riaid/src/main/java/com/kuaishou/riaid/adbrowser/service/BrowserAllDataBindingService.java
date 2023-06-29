package com.kuaishou.riaid.adbrowser.service;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.render.service.base.IDataBindingService;

/**
 * browser的数据替换服务，内部会持有{@link #mBindingServices}，用于存放所有数据绑定的服务。
 * 会找出一个能解析出数据的服务去解析并返回值。
 */
public class BrowserAllDataBindingService implements IDataBindingService {

  /**
   * 所有数据绑定的服务，构建一个责任链，从第一个开始解析，如果解析结果为空，
   * 则转到下一个服务，直到解析不为空或最后一个服务为止。
   */
  @NonNull
  private final List<IDataBindingService> mBindingServices = new ArrayList<>();

  /**
   * @param browserContext 需要通过context找到变量池
   * @param service        上层注入的{@link IDataBindingService}
   */
  public BrowserAllDataBindingService(@NonNull ADBrowserContext browserContext,
      @NonNull IDataBindingService service) {
    mBindingServices.add(new VariableDataBindingService(browserContext));
    mBindingServices.add(new FunctionDataBindingService(browserContext));
    mBindingServices.add(service);
  }

  /**
   * 遍历所有的数据服务，找出一个能解析出数据的服务去解析并返回值，
   * 如果没有则返回为空字符串。
   *
   * @param dataHolder 占位符key
   * @return 占位符对应的变量值
   */
  @NonNull
  @Override
  public String parseDataHolder(@NonNull String dataHolder) {
    String result = "";
    for (IDataBindingService bindingService : mBindingServices) {
      result = bindingService.parseDataHolder(dataHolder);
      if (!TextUtils.isEmpty(result)) {
        return result;
      }
    }
    return result;
  }
}