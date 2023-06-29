package com.kuaishou.riaid.adbrowser.lifecycle;

import com.kuaishou.riaid.adbrowser.ADBrowser;

/**
 * 定义的{@link ADBrowser}的生命周期，由自己来回调调用的。
 */
public interface ADBrowserLifecycle {

  /**
   * {@link ADBrowser}广告加载时回调，例如广告滑入时会调用，一般我们会在广告进入时，去触发广告场景开始展示。
   */
  void onDidLoad();

  /**
   * RIAID所在的页面推入或从后台切换到前台。对齐Android的onResume和iOS的onDidAppear以及从后台切换到前台
   */
  void onDidAppear();

  /**
   * RIAID所在的页面压栈或从前台切换到后台。对齐Android的onPause和iOS的onDidDisappear以及从前台切换到后台
   */
  void onDidDisappear();

  /**
   * {@link ADBrowser}广告卸载时回调，不一定会销毁，例如广告滑出时调用
   */
  void onDidUnload();

  /**
   * {@link ADBrowser}销毁时调用
   */
  void onDestroy();
}