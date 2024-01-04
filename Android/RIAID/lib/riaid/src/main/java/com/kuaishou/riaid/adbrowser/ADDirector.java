package com.kuaishou.riaid.adbrowser;

import java.util.LinkedHashMap;
import java.util.Map;

import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.adbridge.ADBridgeHandler;
import com.kuaishou.riaid.adbrowser.adbridge.ADBridgeHandlerWrap;
import com.kuaishou.riaid.adbrowser.canvas.ADCanvas;
import com.kuaishou.riaid.adbrowser.function.ADFunctionOperator;
import com.kuaishou.riaid.adbrowser.helper.ADBrowserKeyHelper;
import com.kuaishou.riaid.adbrowser.lifecycle.ADBrowserLifecycle;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.adbrowser.scene.ADScene;
import com.kuaishou.riaid.adbrowser.scene.ADSceneFactory;
import com.kuaishou.riaid.adbrowser.scene.RenderADSceneFactory;
import com.kuaishou.riaid.adbrowser.transition.RelativeLayoutParamBuilder;
import com.kuaishou.riaid.adbrowser.trigger.ADHeartBeatTrigger;
import com.kuaishou.riaid.adbrowser.trigger.ADTrigger;
import com.kuaishou.riaid.adbrowser.trigger.ADTriggerCore;
import com.kuaishou.riaid.proto.nano.ADCancelDeviceMotionActionModel;
import com.kuaishou.riaid.proto.nano.ADCancelTimerActionModel;
import com.kuaishou.riaid.proto.nano.ADConditionChangeActionModel;
import com.kuaishou.riaid.proto.nano.ADSceneModel;
import com.kuaishou.riaid.proto.nano.ADSceneRelationModel;
import com.kuaishou.riaid.proto.nano.ADTriggerActionModel;
import com.kuaishou.riaid.proto.nano.RIAIDConstants;
import com.kuaishou.riaid.proto.nano.RiaidModel;
import com.kuaishou.riaid.proto.nano.SystemKeyEnum;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.logger.RiaidLogger;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * 通过场景工厂创建并控制场景，会通过多种触发器来管理场景的展示和隐藏，同时也会管理场景的生命周期。
 * {@link ADDirector}是内部使用，通过{@link ADBrowser}调用其相应的函数来触发场景的转场等。
 * 内部会持有一个{@link ADBridgeHandler}，用来处理相关的行为。另外{@link ADBrowser}实现了
 * {@link ADBrowserLifecycle}，与{@link ADBrowser}的生命周期一致。
 *
 * @author sunhongfa
 */
public class ADDirector implements ADBrowserLifecycle {
  private static final String TAG = "ADDirector";
  @NonNull
  private final RiaidModel mRiaidModel;

  /**
   * 所有场景的集合，key是场景的key
   * 期望Scene是按照顺序添加到画布中的，遂使用了LinkedHashMap结构。
   * 页面离开时需要清空。
   */
  @NonNull
  private final Map<Integer, ADScene> mADScenes = new LinkedHashMap<>();

  /**
   * 1.用于透传给Trigger对象
   * 2.用于创建场景
   * 3.用于创建场景的{@link RelativeLayout.LayoutParams}
   */
  @NonNull
  private final ADBrowserContext mBrowserContext;

  /**
   * 自定义的场景工厂类，在{@link ADDirector}初始化时构建自定义场景用
   */
  @Nullable
  private final ADSceneFactory mCustomerSceneFactory;

  /**
   * 用于管理{@link ADTrigger}相关，如：其触发条件，执行或释放，操作行为
   */
  @NonNull
  private final ADTriggerCore mTriggerCore;
  /**
   * 触发器的{@link ADBridgeHandler}
   */
  private ADBridgeHandlerWrap<ADTriggerActionModel> mTriggerHandlerWrap;
  /**
   * 更改条件的{@link ADBridgeHandler}
   */
  private ADBridgeHandlerWrap<ADConditionChangeActionModel> mConditionChangeHandlerWrap;
  /**
   * 取消操作的{@link ADBridgeHandler}
   */
  private ADBridgeHandlerWrap<ADCancelTimerActionModel> mCancelOperationHandlerWrap;
  /**
   * 取消设备监听
   */
  private ADBridgeHandlerWrap<ADCancelDeviceMotionActionModel> mCancelDeviceHandlerWrap;

  /**
   * @param browserContext       拿到Browser的上下文，透传给Trigger、获取其画布添加场景等。
   * @param riaidModel           数据模型，用来构建场景、Render、Trigger等。
   * @param customerSceneFactory 自定义场景的工厂，获取外部自定义的场景
   */
  public ADDirector(
      @NonNull ADBrowserContext browserContext,
      @NonNull RiaidModel riaidModel,
      @Nullable ADSceneFactory customerSceneFactory) {
    this.mBrowserContext = browserContext;
    this.mRiaidModel = riaidModel;
    this.mCustomerSceneFactory = customerSceneFactory;
    this.mBrowserContext.getADCanvas().clear();
    this.mTriggerCore = new ADTriggerCore(mBrowserContext, mRiaidModel);
    init();
  }

  private void init() {
    // 需顺序执行
    long startBuildTime = System.currentTimeMillis();
    // 构建场景
    buildScene();
    // 构建场景位置关系
    buildSceneRelation();
    // 构建触发器
    mTriggerCore.setADScenes(mADScenes);
    mTriggerCore.buildTrigger();
    ADBrowserLogger.i("ADDirector build 耗时：" + (System.currentTimeMillis() - startBuildTime));
    mBrowserContext.getADBrowserMetricsEventListener()
        .onRIAIDLogEvent(RIAIDConstants.Standard.BROW_DIRECT_BUILD_DURATION,
            ToolHelper.createDurationParams(System.currentTimeMillis() - startBuildTime));
    // 初始化context中的函数集处理器
    mBrowserContext.setFunctionOperator(
        new ADFunctionOperator(mBrowserContext, mRiaidModel.functions, mADScenes));
    registerHandlers();
  }

  /**
   * 广告加载，开始展示{@link ADScene}的第一步，需要触发相应生命周期的触发器。
   */
  @Override
  public void onDidLoad() {
    ADBrowserLogger.i(TAG + " 首帧时长 onADEnter广告准备展示时间 ：" + System.currentTimeMillis());
    // 广告加载需要重新构建默认条件和变量
    mBrowserContext.getConditionOperator().build(mRiaidModel.defaultConditions);
    mBrowserContext.getVariableOperator().build(mRiaidModel.defaultVariables);
    // 广告加载代表着展示了，要执行相关生命周期的触发器
    if (mRiaidModel.lifeCycle != null && mRiaidModel.lifeCycle.loadTriggerKeys.length > 0) {
      for (int key : mRiaidModel.lifeCycle.loadTriggerKeys) {
        mTriggerCore.executeTrigger(key);
      }
    }
  }

  /**
   * 需要触发相应生命周期的触发器
   */
  @Override
  public void onDidAppear() {
    if (mRiaidModel.lifeCycle != null && mRiaidModel.lifeCycle.appearTriggerKeys.length > 0) {
      for (int key : mRiaidModel.lifeCycle.appearTriggerKeys) {
        mTriggerCore.executeTrigger(key);
      }
    }
  }

  /**
   * 需要触发相应生命周期的触发器
   */
  @Override
  public void onDidDisappear() {
    if (mRiaidModel.lifeCycle != null && mRiaidModel.lifeCycle.disappearTriggerKeys.length > 0) {
      for (int key : mRiaidModel.lifeCycle.disappearTriggerKeys) {
        mTriggerCore.executeTrigger(key);
      }
    }
  }

  /**
   * 广告卸载，即使可能未完全释放，也需要把{@link ADDirector}相关的资源释放掉。
   */
  @Override
  public void onDidUnload() {
    // 广告卸载代表着不展示了，要执行相关触发器
    if (mRiaidModel.lifeCycle != null && mRiaidModel.lifeCycle.unloadTriggerKeys.length > 0) {
      for (int key : mRiaidModel.lifeCycle.unloadTriggerKeys) {
        mTriggerCore.executeTrigger(key);
      }
    }
    release();
  }

  /**
   * 完全释放掉了。
   */
  @Override
  public void onDestroy() {
    release();
  }

  /**
   * 执行一个触发器
   *
   * @param key 触发器的key
   */
  public void executeTriggerKey(int key) {
    mTriggerCore.executeTrigger(key);
  }

  /**
   * 视频播放完成，需要执行对应的触发器，同时广告播放已经到了最后，则说明定时触发器也不再需要了，需要释放掉。
   */
  void onVideoEnd() {
    mTriggerCore.executeTrigger(SystemKeyEnum.TRIGGER_KEY_AD_VIDEO_END);
  }

  /**
   * 注册{@link ADDirector}需要的{@link ADBridgeHandler}，注意不要重复注册
   * 需要和{@link #unregisterHandlers()}成对调用
   */
  private void registerHandlers() {
    mTriggerHandlerWrap = mBrowserContext.getADBridge().register(
        new ADBridgeHandlerWrap<>(ADTriggerActionModel.class,
            mTriggerHandler));
    mConditionChangeHandlerWrap = mBrowserContext.getADBridge().register(
        new ADBridgeHandlerWrap<>(ADConditionChangeActionModel.class,
            mConditionChangeActionHandler));
    mCancelOperationHandlerWrap = mBrowserContext.getADBridge().register(
        new ADBridgeHandlerWrap<>(ADCancelTimerActionModel.class,
            mCancelOperationHandler));
    mCancelDeviceHandlerWrap = mBrowserContext.getADBridge().register(
        new ADBridgeHandlerWrap<>(ADCancelDeviceMotionActionModel.class,
            mCancelDeviceMotionHandler
        ));
  }

  /**
   * 反注册{@link ADDirector}中的{@link ADBridgeHandler}，
   * {@link #release()}时需要调用
   */
  private void unregisterHandlers() {
    mBrowserContext.getADBridge().unregister(mTriggerHandlerWrap);
    mBrowserContext.getADBridge().unregister(mConditionChangeHandlerWrap);
    mBrowserContext.getADBridge().unregister(mCancelOperationHandlerWrap);
    mBrowserContext.getADBridge().unregister(mCancelDeviceHandlerWrap);
  }

  /**
   * 释放相关的资源，包括触发器的释放，场景的销毁，相关数据的清空以及{@link ADCanvas}的清屏，
   * 清屏主要是防止状态残留，广告进入会重新构建{@link ADScene}
   */
  private void release() {
    // 条件和变量需要释放掉
    mBrowserContext.getConditionOperator().release();
    mBrowserContext.getVariableOperator().release();
    // 注意当广告卸载后，所有的触发器都要释放
    mTriggerCore.release();

    unregisterHandlers();
    // 场景也卸载了，需要执行其对应的生命周期
    for (Map.Entry<Integer, ADScene> scene : mADScenes.entrySet()) {
      scene.getValue().onDidUnload();
    }
    mADScenes.clear();
    // 完全从画布中移出场景，防止状态残留。
    mBrowserContext.getADCanvas().clear();
  }

  /**
   * 触发多个{@link ADTrigger},处理相关的{@link com.kuaishou.riaid.adbrowser.action.ADAction}。
   */
  private final ADBridgeHandler<ADTriggerActionModel> mTriggerHandler =
      new ADBridgeHandler<ADTriggerActionModel>() {
        @Override
        public boolean canHandle(@NonNull ADTriggerActionModel triggerActionModel) {
          return triggerActionModel.triggerKeys != null &&
              triggerActionModel.triggerKeys.length > 0;
        }

        @Override
        public boolean handle(@NonNull ADTriggerActionModel triggerActionModel) {
          for (int triggerKey : triggerActionModel.triggerKeys) {
            mTriggerCore.executeTrigger(triggerKey);
          }
          return true;
        }
      };

  /**
   * 去更改一个条件，用来选择{@link ADTrigger}的条件
   */
  private final ADBridgeHandler<ADConditionChangeActionModel> mConditionChangeActionHandler =
      new ADBridgeHandler<ADConditionChangeActionModel>() {
        @Override
        public boolean canHandle(@NonNull ADConditionChangeActionModel object) {
          return object.condition != null &&
              !TextUtils.isEmpty(object.condition.conditionName) &&
              !TextUtils.isEmpty(object.condition.conditionValue);
        }

        @Override
        public boolean handle(@NonNull ADConditionChangeActionModel object) {
          String conditionName = object.condition.conditionName;
          String conditionValue = object.condition.conditionValue;
          if (!TextUtils.isEmpty(conditionName) && !TextUtils.isEmpty(conditionValue)) {
            mTriggerCore.alterCondition(conditionName, conditionValue);
            return true;
          }
          return false;
        }
      };

  /**
   * 取消一个操作，可以控制某些{@link ADTrigger}不再触发，如：
   * {@link ADHeartBeatTrigger}
   */
  private final ADBridgeHandler<ADCancelTimerActionModel> mCancelOperationHandler =
      new ADBridgeHandler<ADCancelTimerActionModel>() {
        @Override
        public boolean canHandle(@NonNull ADCancelTimerActionModel object) {
          return ADBrowserKeyHelper.isValidKey(object.triggerKey);
        }

        @Override
        public boolean handle(@NonNull ADCancelTimerActionModel object) {
          return mTriggerCore.cancelTrigger(object.triggerKey);
        }
      };

  /**
   * 取消一个设备传感器监听操作，可以控制某些{@link ADTrigger}不再触发
   */
  private final ADBridgeHandler<ADCancelDeviceMotionActionModel> mCancelDeviceMotionHandler =
      new ADBridgeHandler<ADCancelDeviceMotionActionModel>() {
        @Override
        public boolean canHandle(@NonNull ADCancelDeviceMotionActionModel object) {
          return ADBrowserKeyHelper.isValidKey(object.triggerKey);
        }

        @Override
        public boolean handle(@NonNull ADCancelDeviceMotionActionModel object) {
          return mTriggerCore.cancelTrigger(object.triggerKey);
        }
      };

  ///////////////////////////////////////////////////////////////////////////
  // 场景的构建部分
  ///////////////////////////////////////////////////////////////////////////
  private void buildScene() {
    long startBuildTime = System.currentTimeMillis();
    RenderADSceneFactory renderADSceneFactory = new RenderADSceneFactory();
    if (mRiaidModel.scenes == null) {
      return;
    }

    // 根据数据构建出场景集合
    for (int i = 0; i < mRiaidModel.scenes.length; i++) {
      ADSceneModel sceneModel = mRiaidModel.scenes[i];
      if (sceneModel == null) {
        return;
      }
      @Nullable ADScene scene = null;
      if (sceneModel.render == null || sceneModel.render.renderData == null) {
        ADBrowserLogger.i("ADDirector 是需要外接的场景 sceneKey:" + sceneModel.key);
        if (mCustomerSceneFactory != null) {
          scene = mCustomerSceneFactory.createADScene(mBrowserContext, sceneModel);
        } else {
          ADBrowserLogger.e("ADDirector 是需要外接的场景 sceneKey:" + sceneModel.key +
              " 但是没有传入自定义场景工厂");
        }
      } else {
        scene = renderADSceneFactory.createADScene(mBrowserContext, sceneModel);
      }
      if (scene != null) {
        scene.onDidLoad();
        mADScenes.put(sceneModel.key, scene);
      } else {
        ADBrowserLogger.e("ADDirector 场景创建失败 sceneKey:" + sceneModel.key);
      }
    }
    ADBrowserLogger.i(
        "ADDirector buildScene 耗时：" + (System.currentTimeMillis() - startBuildTime));
  }

  /**
   * 根据默认的场景位置关系，来构建各个场景在画布中的位置
   */
  private void buildSceneRelation() {
    long startBuildTime = System.currentTimeMillis();
    if (mRiaidModel.defaultSceneRelations == null) {
      return;
    }
    ADBrowserLogger.i("buildSceneRelation relationModelList" +
        RiaidLogger.objectToString(mRiaidModel.defaultSceneRelations));
    for (int i = 0; i < mRiaidModel.defaultSceneRelations.length; i++) {
      ADSceneRelationModel sceneRelationModel = mRiaidModel.defaultSceneRelations[i];
      if (sceneRelationModel == null) {
        continue;
      }
      // source和target必须都存在，target可以是Canvas
      if (mADScenes.containsKey(sceneRelationModel.sourceKey) &&
          (ADBrowserKeyHelper.isCanvas(sceneRelationModel.targetKey) ||
              mADScenes.containsKey(sceneRelationModel.targetKey))) {
        ADScene adSourceScene = mADScenes.get(sceneRelationModel.sourceKey);
        View sourceSceneView = adSourceScene.getSceneView();
        // 获取targetView的id
        int targetViewId;
        if (ADBrowserKeyHelper.isCanvas(sceneRelationModel.targetKey)) {
          // 说明是target是画布，给其赋值
          targetViewId = SystemKeyEnum.SCENE_KEY_CANVAS;
        } else {
          ADScene adTargetScene = mADScenes.get(sceneRelationModel.targetKey);
          targetViewId = adTargetScene.getViewId();
        }
        RelativeLayout.LayoutParams layoutParams;
        if (sourceSceneView.getLayoutParams() == null) {
          // 创建source的LayoutParams
          layoutParams =
              new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                  RelativeLayout.LayoutParams.WRAP_CONTENT);
          // 默认是居中的
          layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
          layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        } else {
          layoutParams = (RelativeLayout.LayoutParams) sourceSceneView.getLayoutParams();
        }
        // 赋值并设置边距
        RelativeLayoutParamBuilder
            .buildLayoutParam(layoutParams, sceneRelationModel, targetViewId);
        RelativeLayoutParamBuilder
            .buildLayoutMargin(mBrowserContext.getContext(), layoutParams, sceneRelationModel);
        // 将sourceLayoutParam赋值给sourceSceneView
        sourceSceneView.setLayoutParams(layoutParams);
      } else {
        ADBrowserLogger.e("buildSceneRelation sceneRelationModel配置错误 sceneRelationModel：" +
            RiaidLogger.objectToString(sceneRelationModel));
      }
    }

    // 最后将场景顺序添加到画布中
    for (Map.Entry<Integer, ADScene> scenes : mADScenes.entrySet()) {
      View sceneView = scenes.getValue().getSceneView();
      mBrowserContext.getADCanvas().addADView(sceneView);
    }
    ADRenderLogger
        .i("ADDirector buildSceneRelation 耗时：" + (System.currentTimeMillis() - startBuildTime));
  }

}