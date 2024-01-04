package com.kuaishou.riaid.adbrowser.lifecycle;

/**
 * 定义的{@link com.kuaishou.riaid.adbrowser.scene.ADScene}的生命周期。
 * @author sunhongfa
 */
public interface ADSceneLifecycle {

  /**
   * {@link com.kuaishou.riaid.adbrowser.scene.ADScene}初始化时调用
   */
  void onDidLoad();

  /**
   * 将要展示{@link com.kuaishou.riaid.adbrowser.scene.ADScene}中视图时回调，在view可见之前
   */
  void onWillAppear();

  /**
   * {@link com.kuaishou.riaid.adbrowser.scene.ADScene}中视图展示后时回调，在view可见之后
   */
  void onDidAppear();

  /**
   * 将要隐藏{@link com.kuaishou.riaid.adbrowser.scene.ADScene}视图时回调，在view不可见之前
   */
  void onWillDisappear();

  /**
   * {@link com.kuaishou.riaid.adbrowser.scene.ADScene}中视图隐藏后时回调，在view不可见之后
   */
  void onDidDisappear();

  /**
   * {@link com.kuaishou.riaid.adbrowser.scene.ADScene}销毁时回调
   */
  void onDidUnload();
}