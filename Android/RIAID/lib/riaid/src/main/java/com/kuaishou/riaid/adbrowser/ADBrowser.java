package com.kuaishou.riaid.adbrowser;


import java.util.Map;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.adbridge.ADBridge;
import com.kuaishou.riaid.adbrowser.canvas.ADCanvas;
import com.kuaishou.riaid.adbrowser.event.ADBrowserMetricsEventListener;
import com.kuaishou.riaid.adbrowser.event.ADBrowserToggleEvent;
import com.kuaishou.riaid.adbrowser.lifecycle.ADBrowserLifecycle;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.adbrowser.scene.ADSceneFactory;
import com.kuaishou.riaid.adbrowser.service.ADBrowserService;
import com.kuaishou.riaid.adbrowser.trigger.ADTrigger;
import com.kuaishou.riaid.proto.nano.RiaidModel;
import com.kuaishou.riaid.render.interf.IRealViewWrapper;

/**
 * 广告浏览器，采用BS（Browser-Server）架构，解决广告样式可配置以及服务端控制等问题。
 * 外层需要主动去调用广告也有的行为函数，如 {@link #onDestroy()} ()}、{@link #toggleEvent(ADBrowserToggleEvent)} ()}
 * ()}等。
 * 类似于打开一个浏览器，输入一个网址，通过触发这些行为函数，可以打开页面，离开页面。{@link ADBrowser}内部能处理
 * 所有交互和渲染。
 * 有一些{@link ADBrowser}不能处理的，比如上报埋点，控制视频的播放，
 * 需要通过{@link #addBrowserMetricsEventListener(ADBrowserMetricsEventListener)} 来添加监听，外部来处理。
 * 注意：释放一定要调用{@link #onDestroy()} ()}
 */
public class ADBrowser
    implements ADBrowserLifecycle {
  private static final String TAG = "ADBrowser";
  /**
   * 持有{@link ADDirector}是为了调用其内部函数，如{@link ADDirector#onVideoEnd()}去触发相应的
   * {@link ADTrigger}
   */
  @NonNull
  private ADDirector mADDirector;

  /**
   * {@link ADBrowser}会维护一个画布，主要是{@link ADDirector}去使用，添加或移除
   * {@link ADScene}，并放置
   * {@link ADScene}的位置。
   */
  @SuppressWarnings("FieldCanBeLocal")
  @NonNull
  private final ADCanvas mADCanvas;

  /**
   * 整个{@link ADBrowser}只有这一个{@link ADBridge}的对象，会存放到{@link ADBrowserContext}中，
   * 层层传递给使用的类或方法。
   */
  @NonNull
  private final ADBridge mADBridge = new ADBridge();

  /**
   * 整个{@link ADBrowser}会持有一个{@link ADBrowserContext}
   */
  @NonNull
  private final ADBrowserContext mBrowserContext;
  @NonNull
  private final RiaidModel mRiaidModel;
  @Nullable
  private final ADSceneFactory mCustomADSceneFactory;

  /**
   * @param context {@link Context}，用于创建视图
   * @param asDSL   输入的数据模型，用于构建{@link ADDirector}
   * @param service 需要传入的服务对象，图片加载和数据绑定
   */
  public ADBrowser(
      @NonNull Context context,
      @NonNull RiaidModel asDSL,
      @NonNull ADCanvas adCanvas,
      @NonNull ADBrowserService service) {
    this(context, asDSL, adCanvas, service, null);
  }

  /**
   * @param context              {@link Context}，用于创建视图
   * @param riaidModel           输入的数据模型，用于构建{@link ADDirector}
   * @param service              需要传入的服务对象，图片加载和数据绑定
   * @param customADSceneFactory 自定义场景工厂类，可用来生产自定义的场景，{@link ADDirector} 会将其组装起来。
   */
  public ADBrowser(
      @NonNull Context context,
      @NonNull RiaidModel riaidModel,
      @NonNull ADCanvas adCanvas,
      @NonNull ADBrowserService service,
      @Nullable ADSceneFactory customADSceneFactory) {
    this.mADCanvas = adCanvas;
    this.mRiaidModel = riaidModel;
    this.mCustomADSceneFactory = customADSceneFactory;
    this.mBrowserContext =
        new ADBrowserContext(context, mADCanvas, mADBridge, mRiaidModel, service);
    this.mADDirector = new ADDirector(mBrowserContext, mRiaidModel, mCustomADSceneFactory);
    ADBrowserLogger.i(TAG + "RIAID_MODEL_NAME riaidModel.key:" + riaidModel.key);
  }

  @Override
  public void onDidLoad() {
    ADBrowserLogger.i(TAG + " onDidLoad");
    mADDirector.onDidLoad();
  }

  @Override
  public void onDidAppear() {
    ADBrowserLogger.i(TAG + " onDidAppear");
    mADDirector.onDidAppear();
  }

  @Override
  public void onDidDisappear() {
    ADBrowserLogger.i(TAG + " onDidDisappear");
    mADDirector.onDidDisappear();
  }

  /**
   * 广告离开，但未完全释放{@link ADBrowser}，仅仅需要释放相关资源并重新构建{@link ADDirector}。
   * 之所以要重新构建{@link ADDirector}，是因为可能会滑回来，所以离开后需要重新构建（不会渲染），放到内存缓存中，
   * 提高用户体验。
   */
  @Override
  public void onDidUnload() {
    ADBrowserLogger.i(TAG + " onDidUnload");
    mADBridge.release();
    mADDirector.onDidUnload();
    // 重新创建ADDirector
    mADDirector = new ADDirector(mBrowserContext, mRiaidModel, mCustomADSceneFactory);
  }

  /**
   * 主动释放资源，画布销毁的时候可触发，防止内存泄漏，上层必须调用
   */
  @Override
  public void onDestroy() {
    ADBrowserLogger.i(TAG + " onDestroy");
    mBrowserContext.release();
    mADBridge.release();
    mADDirector.onDestroy();
  }

  /**
   * 用于上层执行的一些协定好的触发器，比如上层接口请求成功后需要执行RIAID内部的行为。
   *
   * @param triggerKey 触发器的key
   */
  public void executeTrigger(int triggerKey) {
    mADDirector.executeTriggerKey(triggerKey);
  }

  /**
   * 上层输入的一些事件，如视频播放的一些关键事件，类似首帧播放、播放结束、暂停、重新开始播放等等。
   * 这些事件需要{@link ADBrowser}处理
   *
   * @param event {@link ADBrowserToggleEvent} 定义的一些事件，不能为空
   */
  public void toggleEvent(@NonNull ADBrowserToggleEvent event) {
    switch (event.getEventType()) {
      case ADBrowserToggleEvent.ADBrowserEventType.VIDEO_START:
        ADBrowserLogger.i("onVideoStart");
        break;
      case ADBrowserToggleEvent.ADBrowserEventType.VIDEO_END:
        ADBrowserLogger.i("onVideoEnd");
        mADDirector.onVideoEnd();
        break;
      default:
        ADBrowserLogger.e("toggleEvent ADBrowser不支持的事件 event:" + event.getEventType());
        break;
    }
  }

  /**
   * 上层添加一个监听{@link ADBrowserMetricsEventListener}，用来监听对外输出的事件，外界自己消费，
   * 可以添加多个监听。
   * 添加的监听，在执行了{@link #onDestroy()}后会全部释放。
   *
   * @param adBrowserMetricsEventListener 事件监听
   */
  public void addBrowserMetricsEventListener(
      @Nullable ADBrowserMetricsEventListener adBrowserMetricsEventListener) {
    if (adBrowserMetricsEventListener != null) {
      mBrowserContext.addBrowserOutEventListener(adBrowserMetricsEventListener);
    }
  }

  /**
   * 上层移除一个监听{@link ADBrowserMetricsEventListener}
   *
   * @param adBrowserMetricsEventListener 事件监听
   */
  public void removeBrowserMetricsEventListener(
      @Nullable ADBrowserMetricsEventListener adBrowserMetricsEventListener) {
    if (adBrowserMetricsEventListener != null) {
      mBrowserContext.removeBrowserOutEventListener(adBrowserMetricsEventListener);
    }
  }

  /**
   * 根据{@code viewKey}来找到一个view，如果{@code sceneKey}是有效的，需要直接从这个场景中找到视图
   * 如果{@code sceneKey}是无效的，需要从{@code scenes}中遍历找到对应的视图
   *
   * @param scenes  从这些场景中找
   * @param viewKey 要找到的view的唯一标识
   * @return 如果找到了，则返回一个不为空的{@link Pair}，其{@link Pair#first}为对应的场景，
   * {@link Pair#second}为对应的view， 如果找不到则返回一个空的{@link Pair}。
   * 注意：在RIAID整个模型里，所有的viewKey都是不重复且唯一的，但是如果是共享元素的场景，两个场景中的viewKey是一样的，
   * 但这也不影响view的查找，因为在共享元素的两个场景中的视图渲染器是一个，而真正的检索行为就是视图渲染器来执行的。
   */
  @Nullable
  public static Pair<ADScene, View> findSceneAndViewByKey(@NonNull Map<Integer, ADScene> scenes,
      int viewKey) {
    for (Map.Entry<Integer, ADScene> entry : scenes.entrySet()) {
      ADScene scene = entry.getValue();
      if (scene == null || scene.getRenderCreator() == null) {
        // 如果场景不存在，或者是其渲染引擎未初始化，则不需要检索
        continue;
      }
      View view = scene.findViewByKey(viewKey);
      if (view != null) {
        return new Pair<>(scene, view);
      }
    }
    return null;
  }


  /**
   * 根据{@code viewKey}来找到一个view，如果{@code sceneKey}是有效的，需要直接从这个场景中找到视图
   * 如果{@code sceneKey}是无效的，需要从{@code scenes}中遍历找到对应的视图
   *
   * @param scenes  从这些场景中找
   * @param viewKey 要找到的view的唯一标识
   * @return 如果找到了，则返回一个不为空的{@link Pair}，其{@link Pair#first}为对应的场景，
   * {@link Pair#second}为对应的view， 如果找不到则返回一个空的{@link Pair}。
   * 注意：在RIAID整个模型里，所有的viewKey都是不重复且唯一的，但是如果是共享元素的场景，两个场景中的viewKey是一样的，
   * 但这也不影响view的查找，因为在共享元素的两个场景中的视图渲染器是一个，而真正的检索行为就是视图渲染器来执行的。
   */
  @Nullable
  public static Pair<ADScene, IRealViewWrapper> findSceneAndViewWrapperByKey(
      @NonNull Map<Integer, ADScene> scenes,
      int viewKey) {
    for (Map.Entry<Integer, ADScene> entry : scenes.entrySet()) {
      ADScene scene = entry.getValue();
      if (scene == null || scene.getRenderCreator() == null) {
        // 如果场景不存在，或者是其渲染引擎未初始化，则不需要检索
        continue;
      }
      IRealViewWrapper viewWrapper = scene.findViewWrapperByKey(viewKey);
      if (viewWrapper != null) {
        return new Pair<>(scene, viewWrapper);
      }
    }
    return null;
  }
}
