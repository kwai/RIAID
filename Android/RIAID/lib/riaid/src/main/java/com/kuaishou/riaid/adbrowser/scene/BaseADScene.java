package com.kuaishou.riaid.adbrowser.scene;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.adbrowser.helper.ADBrowserKeyHelper;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.proto.nano.ADSceneModel;
import com.kuaishou.riaid.proto.nano.ADTriggerActionModel;
import com.kuaishou.riaid.render.interf.IRealViewWrapper;

/**
 * 基础的的广告场景
 * 内部会有一个容器，用来填充渲染的视图
 */
public abstract class BaseADScene implements ADScene {
  private static final String TAG = "BaseADScene";
  protected final Context mContext;
  /**
   * Scene视图的容器
   */
  @NonNull
  protected final FrameLayout mSceneContainer;
  /**
   * 渲染的视图，可以是render渲染，也可以是自定义创建
   */
  @Nullable
  protected View mSceneRenderView;

  @NonNull
  protected final ADBrowserContext mBrowserContext;
  @NonNull
  protected final ADSceneModel mADSceneModel;
  /**
   * 场景视图的id
   */
  protected int mViewId = View.NO_ID;

  @Nullable
  protected OnSceneVisibilityListener mOnSceneShowListener;

  // 记录上次的显示状态，防止重复上报曝光埋点
  protected int lastVisibility = -1;

  protected boolean isFirstDisplay = true;

  public BaseADScene(@NonNull ADBrowserContext context, @NonNull ADSceneModel adSceneModel) {
    mContext = context.getContext();
    mBrowserContext = context;
    mADSceneModel = adSceneModel;
    mSceneContainer = new FrameLayout(mContext);
    // 默认是隐藏，只有触发器触发显示才会上屏
    mSceneContainer.setVisibility(View.INVISIBLE);
  }

  @Override
  public void setOnSceneVisibilityListener(
      @Nullable OnSceneVisibilityListener onSceneShowListener) {
    mOnSceneShowListener = onSceneShowListener;
  }

  @Override
  public int getSceneKey() {
    return mADSceneModel.key;
  }

  @Override
  public String getSceneDebugInfo() {
    return mADSceneModel.debugInfo;
  }

  @Override
  @NonNull
  public View getSceneView() {
    return mSceneContainer;
  }

  @Override
  @Nullable
  public View getSceneRenderView() {
    return mSceneRenderView;
  }

  @Override
  public void setVisibility(int visibility) {
    if (lastVisibility == visibility) {
      return;
    }
    if (visibility == View.VISIBLE) {
      onWillAppear();
    } else {
      onWillDisappear();
    }
    mSceneContainer.setVisibility(visibility);
    if (visibility == View.VISIBLE) {
      onDidAppear();
    } else {
      onDidDisappear();
    }
    if (visibility == View.VISIBLE) {
      long startVisibilityTime = System.currentTimeMillis();
      ADBrowserLogger.i(TAG + getSceneKey() + " 首帧时长，要展示，展示前 ：" + startVisibilityTime);
      requestAddRenderView();
      ADBrowserLogger.i(TAG + getSceneKey() + " 首帧时长，要展示，展示后 ：" +
          (System.currentTimeMillis() - startVisibilityTime));
      if (mOnSceneShowListener != null && lastVisibility != visibility) {
        // 回调场景已展示
        mOnSceneShowListener.onSceneShow(isFirstDisplay);
      }
      isFirstDisplay = false;
    } else {
      if (mOnSceneShowListener != null) {
        // 回调场景已隐藏
        mOnSceneShowListener.onSceneHide();
      }
    }
    lastVisibility = visibility;
  }

  @Override
  public void listenSceneWindowInfo(@NonNull OnSceneWindowCallback sceneWindowCallback) {
    requestAddRenderView();
    mSceneContainer.post(
        () -> {
          ADBrowserLogger.i(
              "listenSceneWindowInfo sceneKey" + getSceneKey() +
                  " mSceneContainer.getY()" + mSceneContainer.getY() +
                  " mSceneContainer.getX(): " + mSceneContainer.getX() +
                  " mSceneContainer.getWidth(): " + mSceneContainer.getWidth() +
                  " mSceneContainer.getHeight():" + mSceneContainer.getHeight());
          sceneWindowCallback.onSceneWindowCallback(
              mSceneContainer.getX(),
              mSceneContainer.getY(),
              mSceneContainer.getWidth(),
              mSceneContainer.getHeight());
        });
  }

  @Nullable
  @Override
  public View findViewByKey(int viewKey) {
    return null;
  }

  @Nullable
  @Override
  public IRealViewWrapper findViewWrapperByKey(int viewKey) {
    return null;
  }

  /**
   * 直接添加渲染的视图，但不改变整个场景的可见性
   */
  private void requestAddRenderView() {
    // 如果是可见，需要创建视图并添加到场景中
    tryCreateRenderView();
    // 如果没有添加渲染的视图才会添加
    if (mSceneContainer.getChildCount() <= 0 && mSceneRenderView != null) {
      // 如果要添加的渲染的视图，已经有父布局了，需要移出出去。
      if (mSceneRenderView.getParent() != null &&
          mSceneRenderView.getParent() instanceof ViewGroup) {
        ((ViewGroup) mSceneRenderView.getParent()).removeView(mSceneRenderView);
      }
      mSceneContainer.addView(mSceneRenderView);
    }
  }

  /**
   * 尝试创建一个渲染的view，如果{@link #mSceneRenderView}已经存在了就不需要创建了
   */
  protected void tryCreateRenderView() {
    if (mSceneRenderView != null) {
      return;
    }
    mSceneRenderView = createRenderView();
  }

  @Override
  public void onDidAppear() {
    ADScene.super.onDidAppear();
    if (mADSceneModel.lifeCycle != null && mADSceneModel.lifeCycle.appearTriggerKeys != null &&
        mADSceneModel.lifeCycle.appearTriggerKeys.length > 0) {
      mBrowserContext.getADBridge().handle(ADTriggerActionModel.class,
          ADBrowserKeyHelper.buildTriggerActionModel(mADSceneModel.lifeCycle.appearTriggerKeys));
    }
  }

  @Override
  public void onDidDisappear() {
    ADScene.super.onDidDisappear();
    if (mADSceneModel.lifeCycle != null && mADSceneModel.lifeCycle.disappearTriggerKeys != null &&
        mADSceneModel.lifeCycle.disappearTriggerKeys.length > 0) {
      mBrowserContext.getADBridge().handle(ADTriggerActionModel.class,
          ADBrowserKeyHelper.buildTriggerActionModel(mADSceneModel.lifeCycle.disappearTriggerKeys));
    }
  }

  /**
   * @return 创建一个渲染的视图
   */
  @Nullable
  protected abstract View createRenderView();

  @Override
  public boolean isShown() {
    return mSceneContainer.isShown();
  }

  /**
   * 获取id主要是摆放场景的位置关系
   *
   * @return 当前场景渲染的视图的id
   */
  @Override
  public int getViewId() {
    if (mViewId == View.NO_ID) {
      mViewId = View.generateViewId();
      mSceneContainer.setId(mViewId);
    }
    return mSceneContainer.getId();
  }
}
