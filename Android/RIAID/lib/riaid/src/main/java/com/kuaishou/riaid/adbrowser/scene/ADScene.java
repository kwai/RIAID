package com.kuaishou.riaid.adbrowser.scene;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADDirector;
import com.kuaishou.riaid.adbrowser.lifecycle.ADSceneLifecycle;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.render.config.DSLRenderCreator;
import com.kuaishou.riaid.render.interf.IRealViewWrapper;

/**
 * 广告场景接口，一个卡片或者一个样式称为一个场景。
 * 可以支持自定义场景。
 * @author sunhongfa
 */
public interface ADScene extends ADSceneLifecycle {
  /**
   * 唯一标识，规则：注意系统保留的key定义都是小于0的，而外部使用时定义的key需是大于零的。
   */
  int getSceneKey();

  /**
   * @return 场景的信息
   */
  default String getSceneDebugInfo() {
    return String.valueOf(getSceneKey());
  }

  /**
   * @return 广告场景的对应视图
   */
  @NonNull
  View getSceneView();


  /**
   * @return 真正渲染的视图
   */
  @Nullable
  View getSceneRenderView();

  /**
   * @return 场景经过测量后的宽度
   */
  int getSceneMeasureWidth();

  /**
   * @return 场景视图对应的id
   */
  int getViewId();

  /**
   * 场景可见性设置
   *
   * @param visible {@link View#VISIBLE}，{@link View#INVISIBLE}
   */
  void setVisibility(int visible);

  /**
   * @return 场景是否正在展示
   */
  boolean isShown();

  /**
   * @param viewKey view的唯一标识
   * @return viewKey 对应的view
   */
  @Nullable
  View findViewByKey(int viewKey);

  /**
   * @param viewKey view的唯一标识
   * @return viewKey 对应的{@link IRealViewWrapper}
   */
  @Nullable
  IRealViewWrapper findViewWrapperByKey(int viewKey);

  /**
   * 场景不一定是由Render渲染的，还有可能是自定义的场景，所以可能为空
   *
   * @return 渲染该场景的render
   */
  @Nullable
  default DSLRenderCreator getRenderCreator() {
    return null;
  }

  /**
   * @return 当前场景渲染需要的数据模型
   */
  @Nullable
  default Node getRenderData() {
    return null;
  }

  /**
   * 替换render，不需要场景内自己创建Render，直接替换掉
   * 注意：要在{@link #setVisibility(int)}前调用才能生效
   *
   * @param renderCreator {@link DSLRenderCreator}
   * @param view          已经渲染好的视图
   */
  default void replaceRender(@NonNull DSLRenderCreator renderCreator, View view) {}

  /**
   * 移除当前场景内的render
   * 会在{@link #setVisibility(int)}时重新渲染render
   */
  default void removeRender() {}

  /**
   * 初始化Scene时回调
   */
  @Override
  @CallSuper
  default void onDidLoad() {
    ADBrowserLogger.i(getSceneDebugInfo() + "ADScene.ADLifecycle onDidLoad");
  }

  /**
   * 将要展示Scene中视图时回调，在view可见之前
   */
  @Override
  @CallSuper
  default void onWillAppear() {
    ADBrowserLogger.i(getSceneDebugInfo() + "ADScene.ADLifecycle onWillAppear");
  }

  /**
   * 监听场景的窗口信息，待场景绘制完成后回调回来
   *
   * @param sceneWindowCallback 场景绘制完成后的回调
   */
  void listenSceneWindowInfo(@NonNull OnSceneWindowCallback sceneWindowCallback);

  /**
   * Scene中视图展示后时回调，在view可见之后
   */
  @Override
  @CallSuper
  default void onDidAppear() {
    ADBrowserLogger.i(getSceneDebugInfo() + "ADScene.ADLifecycle onDidAppear");
  }

  /**
   * 将要隐藏Scene视图时回调，在view不可见之前
   */
  @Override
  @CallSuper
  default void onWillDisappear() {
    ADBrowserLogger.i(getSceneDebugInfo() + "ADScene.ADLifecycle onWillDisappear");
  }

  /**
   * Scene中视图隐藏后时回调，在view不可见之后
   */
  @Override
  @CallSuper
  default void onDidDisappear() {
    ADBrowserLogger.i(getSceneDebugInfo() + "ADScene.ADLifecycle onDidDisappear");
  }

  /**
   * Scene销毁时回调，通常是{@link ADDirector}调用的
   */
  @Override
  @CallSuper
  default void onDidUnload() {
    ADBrowserLogger.i(getSceneDebugInfo() + "ADScene.ADLifecycle onDidUnload");
  }

  /**
   * 设置场景可见性监听
   *
   * @param onSceneShowListener {@link OnSceneVisibilityListener}
   */
  void setOnSceneVisibilityListener(
      @Nullable OnSceneVisibilityListener onSceneShowListener);

  /**
   * 场景展示监听回调
   */
  interface OnSceneVisibilityListener {
    /**
     * 场景展示了
     *
     * @param isFirstDisplay 是否为第一次展示
     */
    default void onSceneShow(boolean isFirstDisplay) {}

    /**
     * 场景隐藏了
     */
    default void onSceneHide() {}
  }

  /**
   * 场景在画布中的相关窗口信息回调
   */
  interface OnSceneWindowCallback {
    /**
     * @param x      The visual x position of this view, in pixels.
     * @param y      The visual y position of this view, in pixels.
     * @param width  场景的宽
     * @param height 场景的高
     */
    void onSceneWindowCallback(float x, float y, int width, int height);
  }
}
