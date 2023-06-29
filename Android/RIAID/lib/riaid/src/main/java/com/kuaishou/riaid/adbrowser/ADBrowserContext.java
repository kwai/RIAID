package com.kuaishou.riaid.adbrowser;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import com.kuaishou.riaid.adbrowser.adbridge.ADBridge;
import com.kuaishou.riaid.adbrowser.canvas.ADCanvas;
import com.kuaishou.riaid.adbrowser.condition.ADConditionOperator;
import com.kuaishou.riaid.adbrowser.event.ADBrowserMetricsEventListener;
import com.kuaishou.riaid.adbrowser.function.ADFunctionOperator;
import com.kuaishou.riaid.adbrowser.programming.ADVariableOperator;
import com.kuaishou.riaid.adbrowser.service.ADBrowserService;
import com.kuaishou.riaid.adbrowser.service.BrowserAllDataBindingService;
import com.kuaishou.riaid.adbrowser.service.RIAIDLogReportService;
import com.kuaishou.riaid.adbrowser.service.RenderService;
import com.kuaishou.riaid.adbrowser.service.ResumeActionService;
import com.kuaishou.riaid.proto.nano.ADConversionActionModel;
import com.kuaishou.riaid.proto.nano.ADCustomActionModel;
import com.kuaishou.riaid.proto.nano.ADTrackActionModel;
import com.kuaishou.riaid.proto.nano.ADUrlActionModel;
import com.kuaishou.riaid.proto.nano.ADVideoActionModel;
import com.kuaishou.riaid.proto.nano.RiaidModel;
import com.kuaishou.riaid.render.service.base.render.IRenderService;

/**
 * 在{@link ADBrowser}初始化时创建，持有了{@link Context}、{@link ADBridge}等，
 * 存放了ADBrowser生命周期内创建的对象可能需要的数据，并对外提供get方法。
 */
public class ADBrowserContext {
  /**
   * 用于构建视图和场景或px-dp互转
   */
  @NonNull
  private final Context mContext;
  /**
   * 为了获取其宽高，或者是添加移除场景
   */
  @NonNull
  private final ADCanvas mADCanvas;
  /**
   * 为了透传给其他类，如{@link ResumeActionService}，可以发送点击事件
   */
  @NonNull
  private final ADBridge mADBridge;
  /**
   * Render需要的服务容器，内部持有加载图片服务
   */
  @NonNull
  private final IRenderService mRenderService;
  /**
   * 内部可能使用的服务接口，如是否为debug
   */
  @NonNull
  private final ADBrowserService mBrowserService;

  private final ADBrowserMetricsEventListener mADBrowserMetricsEventListener =
      new ADBrowserMetricsEventListenerImpl();

  /**
   * 用于场景A的操作会影响场景B的行为，这都是一些条件，根据条件会获取到对应的trigger
   * 例：点击了卡片场景的叉号，在结束页场景点击重播不能再回到卡片页，需要再展示其他的场景
   */
  @NonNull
  private final ADConditionOperator mConditionOperator;

  /**
   * 变量可用于在用户操作过程中，其中的一些值随之发生了变化。
   * 例：倒计时，实时改变一个变量从10到1。
   */
  @NonNull
  private final ADVariableOperator mVariableOperator;

  /**
   * browser的数据替换服务
   */
  @NonNull
  private final BrowserAllDataBindingService mBindingService;

  /**
   * RIAID内部的函数操作，这些函数操作会返回值，可以做变量替换。
   */
  @Nullable
  private ADFunctionOperator mFunctionOperator;

  /**
   * 使用CopyOnWriteArrayList，防止{@link java.util.ConcurrentModificationException}问题
   */
  @NonNull
  private final List<ADBrowserMetricsEventListener> mADBrowserMetricsEventListeners =
      new CopyOnWriteArrayList<>();

  public ADBrowserContext(
      @NonNull Context context,
      @NonNull ADCanvas adCanvas,
      @NonNull ADBridge adBridge,
      @NonNull RiaidModel riaidModel,
      @NonNull ADBrowserService service) {
    mContext = context;
    mADCanvas = adCanvas;
    mADBridge = adBridge;
    mBrowserService = service;
    mConditionOperator = new ADConditionOperator().build(riaidModel.defaultConditions);
    mVariableOperator = new ADVariableOperator().build(riaidModel.defaultVariables);
    mBindingService =
        new BrowserAllDataBindingService(this, service.getDataBindingService());
    mRenderService =
        new RenderService(mBindingService, service.getLoadImageService(),
            new ResumeActionService(adBridge),
            new RIAIDLogReportService(this.mADBrowserMetricsEventListener, riaidModel.key),
            service.getMediaPlayerService(context));
  }

  @NonNull
  public BrowserAllDataBindingService getBindingService() {
    return mBindingService;
  }

  public void setFunctionOperator(
      @Nullable ADFunctionOperator functionOperator) {
    mFunctionOperator = functionOperator;
  }

  public void release() {
    mADBrowserMetricsEventListeners.clear();
  }

  /**
   * 上层添加一个监听{@link ADBrowserMetricsEventListener}，用来监听对外输出的事件，上层自己消费
   *
   * @param adBrowserMetricsEventListener 不能为空
   */
  public void addBrowserOutEventListener(
      @NonNull ADBrowserMetricsEventListener adBrowserMetricsEventListener) {
    mADBrowserMetricsEventListeners.add(adBrowserMetricsEventListener);
  }

  public void removeBrowserOutEventListener(
      @NonNull ADBrowserMetricsEventListener adBrowserMetricsEventListener) {
    mADBrowserMetricsEventListeners.remove(adBrowserMetricsEventListener);
  }

  @NonNull
  public Context getContext() {
    return mContext;
  }

  @NonNull
  public ADCanvas getADCanvas() {
    return mADCanvas;
  }

  @NonNull
  public ADBridge getADBridge() {
    return mADBridge;
  }

  @NonNull
  public ADBrowserService getBrowserService() {
    return mBrowserService;
  }

  /**
   * @return 这是服务容器，存放了一些服务，如图片加载服务
   */
  @NonNull
  public IRenderService getRenderService() {
    return mRenderService;
  }

  /**
   * @return {@link ADBrowserMetricsEventListener}，拿到外界添加的监听，可以通知一些事件，通常是
   * {@link com.kuaishou.riaid.adbrowser.action.ADAction}调用
   */
  @NonNull
  public ADBrowserMetricsEventListener getADBrowserMetricsEventListener() {
    return mADBrowserMetricsEventListener;
  }

  @NonNull
  public ADConditionOperator getConditionOperator() {
    return mConditionOperator;
  }

  @NonNull
  public ADVariableOperator getVariableOperator() {
    return mVariableOperator;
  }

  @Nullable
  public ADFunctionOperator getFunctionOperator() {
    return mFunctionOperator;
  }

  private class ADBrowserMetricsEventListenerImpl implements ADBrowserMetricsEventListener {
    @Override
    public void onTrackEvent(@NonNull ADTrackActionModel action) {
      for (ADBrowserMetricsEventListener adBrowserMetricsEventListener :
          mADBrowserMetricsEventListeners) {
        if (adBrowserMetricsEventListener != null) {
          adBrowserMetricsEventListener.onTrackEvent(action);
        }
      }
    }

    @Override
    public void onCustomEvent(@NonNull ADCustomActionModel action) {
      for (ADBrowserMetricsEventListener adBrowserMetricsEventListener :
          mADBrowserMetricsEventListeners) {
        if (adBrowserMetricsEventListener != null) {
          adBrowserMetricsEventListener.onCustomEvent(action);
        }
      }
    }

    @Override
    public void onVideoEvent(@NonNull ADVideoActionModel action) {
      for (ADBrowserMetricsEventListener adBrowserMetricsEventListener :
          mADBrowserMetricsEventListeners) {
        if (adBrowserMetricsEventListener != null) {
          adBrowserMetricsEventListener.onVideoEvent(action);
        }
      }
    }

    @Override
    public void onUrlEvent(@NonNull ADUrlActionModel action) {
      for (ADBrowserMetricsEventListener adBrowserMetricsEventListener :
          mADBrowserMetricsEventListeners) {
        if (adBrowserMetricsEventListener != null) {
          adBrowserMetricsEventListener.onUrlEvent(action);
        }
      }
    }

    @Override
    public void onConversionEvent(@NonNull ADConversionActionModel action) {
      for (ADBrowserMetricsEventListener adBrowserMetricsEventListener :
          mADBrowserMetricsEventListeners) {
        if (adBrowserMetricsEventListener != null) {
          adBrowserMetricsEventListener.onConversionEvent(action);
        }
      }
    }

    @Override
    public void onRIAIDLogEvent(@Nullable String eventName, @Nullable JsonObject eventParams) {
      for (ADBrowserMetricsEventListener adBrowserMetricsEventListener :
          mADBrowserMetricsEventListeners) {
        if (adBrowserMetricsEventListener != null) {
          adBrowserMetricsEventListener.onRIAIDLogEvent(eventName, eventParams);
        }
      }
    }
  }
}
