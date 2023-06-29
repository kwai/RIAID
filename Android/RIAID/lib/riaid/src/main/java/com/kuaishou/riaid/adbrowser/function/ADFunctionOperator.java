package com.kuaishou.riaid.adbrowser.function;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.proto.nano.ADFunctionModel;

/**
 * 函数集的处理器，会存放所有的{@link ADFunction}，
 * 并可以通过{@code functionKey}来找到相应的函数并执行获取返回值。函数可以用${key}来查找，
 * 然后通过{@link com.kuaishou.riaid.adbrowser.service.BrowserAllDataBindingService}责任链来找到对应的
 * {@link com.kuaishou.riaid.adbrowser.service.FunctionDataBindingService}来解析出来。
 */
public class ADFunctionOperator {

  /**
   * 函数的构建需要context
   */
  @NonNull
  private final ADBrowserContext mBrowserContext;

  /**
   * 函数的构建可能需要场景
   */
  @NonNull
  private final Map<Integer, ADScene> mADScenes;

  /**
   * 所有构建完的函数
   */
  @NonNull
  protected final Map<Integer, ADFunction> mFunctions = new HashMap<>();

  public ADFunctionOperator(@NonNull ADBrowserContext browserContext,
      @NonNull ADFunctionModel[] functionsModel,
      @NonNull Map<Integer, ADScene> adScenes) {
    mBrowserContext = browserContext;
    mADScenes = adScenes;
    build(functionsModel);
  }

  /**
   * 构建{@link #mFunctions}
   *
   * @param functions 构建{@link #mFunctions}需要的数据模型
   */
  private void build(@Nullable ADFunctionModel[] functions) {
    if (functions == null) {
      return;
    }
    for (ADFunctionModel function : functions) {
      if (function.readAttribute != null) {
        mFunctions.put(function.readAttribute.key,
            new ADReadAttributeFunction(mBrowserContext, function.readAttribute, mADScenes));
      }
    }
  }

  /**
   * 根据{@code functionKey}找到相应的函数执行，并返回值。
   *
   * @param functionKey 要执行的函数的key
   * @return 函数执行完成后返回的值，如果没有找到则返回null。
   */
  @Nullable
  public String executeFunctionByKey(int functionKey) {
    if (mFunctions.containsKey(functionKey)) {
      return mFunctions.get(functionKey).execute();
    }
    return null;
  }
}
