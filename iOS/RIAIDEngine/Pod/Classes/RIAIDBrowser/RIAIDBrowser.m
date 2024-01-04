//
//  KCADBrowser.m
//  KCADBrowser
//
//  Created by liweipeng on 2021/11/29.
//

#import "RIAIDBrowser.h"
#import "KCADRender.h"
#import "RIAIDRenderContext.h"
#import "RIAIDRenderVideoNode.h"

#import "RIAIDStatistics.h"
#import "RIAIDLog.h"

#import "RIAIDBrowserDirector+Layout.h"
#import "RIAIDBrowserDirector+Transition.h"
#import "RIAIDBrowserCanvasView.h"
#import "RIAIDBrowserTriggerHandler.h"
#import "RIAIDBrowserFunctionHandler.h"
#import "RIAIDADActionModel+Browser.h"
#import "RIAIDBSceneController.h"
#import "RIAIDBServiceContainer.h"
#import "RIAIDBDataBindingService.h"
#import "RIAIDBConsumeActionService.h"
#import "RIAIDBGlobalVarArea.h"

@interface RIAIDBrowser()<RIAIDBrowserTriggerHandlerDelegate, RIAIDBSceneControllerDelegate>

/// RIAIDRiaidModel实例
@property (nonatomic, strong) RIAIDRiaidModel *dslModel;

/// 画布实例，内部赋予读取权限，对外只读。
@property (nonatomic, strong, readwrite) RIAIDBrowserCanvasView *canvas;

/// 广告“导演”，负责场景布局、转场、交互。
@property (nonatomic, strong) RIAIDBrowserDirector *director;

/// 全局变量区，存储所有条件（Condition）及变量（Variables）
@property (nonatomic, strong) RIAIDBGlobalVarArea *globalVarArea;

/// Trigger处理器
@property (nonatomic, strong) RIAIDBrowserTriggerHandler *triggerHandler;

/// Function处理器
@property (nonatomic, strong) RIAIDBrowserFunctionHandler *functionHandler;

/// ADRender需要的上下文
@property (nonatomic, strong) RIAIDRenderContext *context;

/// 管理各种服务的容器，主要服务于RIAIDRenderContext
@property (nonatomic, strong) RIAIDBServiceContainer *serviceContainer;

/// 外部自定义的提供类
@property (nonatomic, strong) id<RIAIDBExternalRenderProvider> externalRenderProvider;

/// 外部自定义服务列表
@property (nonatomic, copy) NSArray<id<RIAIDBExternalServiceProtocol>> *externalServiceList;
@end

@implementation RIAIDBrowser
@synthesize canvas = _canvas;

- (instancetype)initWithDSLModel:(RIAIDRiaidModel*)model
          externalRenderProvider:(nullable id<RIAIDBExternalRenderProvider>)externalRenderProvider
             externalServiceList:(nullable NSArray<id<RIAIDBExternalServiceProtocol>>*)externalServiceList {
    self = [self init];
    if (self) {
        _dslModel = model;
        _externalRenderProvider = externalRenderProvider;
        _externalServiceList = [externalServiceList copy];
        /// 配置Browser
        [self config];
    }
    return self;
}

- (void)registerTransitionDelegate:(id<RIAIDBrowserDirectorTransitionDelegate>)transitionDelegate {
    self.director.transitionDelegate = transitionDelegate;
}

- (void)config {
    CFAbsoluteTime beginTime = CFAbsoluteTimeGetCurrent();
    /// 注册当前所有Scene
    [self registerScenes];
    /// 通过DSL数据中的SceneRelations数组，对已有的Render(UIView)添加初始布局约束
    [self addSceneRelations];
    /// 注册Triggers
    [self.triggerHandler registerTriggers:self.dslModel.triggersArray];
    /// 注册conditions
    [self.globalVarArea registerConditions:self.dslModel.defaultConditionsArray];
    /// 注册变量
    [self.globalVarArea registerVariables:self.dslModel.defaultVariablesArray];
    /// 注册functions
    [self.functionHandler registerFunctions:self.dslModel.functionsArray];
    self.context.serviceContainer = self.serviceContainer;
    
    CFAbsoluteTime endTime = CFAbsoluteTimeGetCurrent();
    
    double duration = endTime - beginTime;
    
    RIAIDLog(@"browser 初始化耗时：%f", duration);
    [RIAIDStatistics addEventWithKey:RIAIDStandardBrowDirectBuildDuration
                               value:[NSString stringWithFormat:@"%f", duration]
                             context:self.context];
}

- (void)registerScenes {
    for (RIAIDADSceneModel *sceneModel in _dslModel.scenesArray) {
        /// 将SceneCtrl注册到Director中
        RIAIDBSceneController *sceneCtrl = [[RIAIDBSceneController alloc] initWithScene:sceneModel externalRenderProvider:self.externalRenderProvider canvas:self.canvas context:self.context];
        sceneCtrl.delegate = self;
        [self.director registerSceneCtrl:sceneCtrl forSceneKey:sceneModel.key];
    }
}

- (void)addSceneRelations {
    for (RIAIDADSceneRelationModel *sceneRelationModel in _dslModel.defaultSceneRelationsArray) {
        /// 将初始化约束，添加到Director中
        [self.director addLayout:sceneRelationModel];
    }
}

- (void)handleTrigger:(int32_t)triggerKey {
    // 之前这里是异步处理的（dispatch_async(dispatch_get_main_queue()），触发 unloadTrigger 的时候会有时序问题，先去掉
    [self.triggerHandler handleTrigger:triggerKey];
}

#pragma mark - LifeCycle
- (void)browserDisappear {
    /// 触发所有BrowserLifeCycle.disappearTriggerKeysArray中配置的Triggers
    [self.dslModel.lifeCycle.disappearTriggerKeysArray enumerateValuesWithBlock:^(int32_t value, NSUInteger idx, BOOL * _Nonnull stop) {
        [self handleTrigger:value];
    }];
}

- (void)browserAppear {
    /// 触发所有BrowserLifeCycle.appearTriggerKeysArray中配置的Triggers
    [self.dslModel.lifeCycle.appearTriggerKeysArray enumerateValuesWithBlock:^(int32_t value, NSUInteger idx, BOOL * _Nonnull stop) {
        [self handleTrigger:value]; 
    }];
}

- (void)browserLoad {
    [self.dslModel.lifeCycle.loadTriggerKeysArray enumerateValuesWithBlock:^(int32_t value, NSUInteger idx, BOOL * _Nonnull stop) {
        [self handleTrigger:value];
    }];
}

- (void)browserUnLoad {
    [self.dslModel.lifeCycle.unloadTriggerKeysArray enumerateValuesWithBlock:^(int32_t value, NSUInteger idx, BOOL * _Nonnull stop) {
        [self handleTrigger:value];
    }];
    
    [self reset];
}

#pragma mark - RIAIDBSceneControllerDelegate
/// 所有Scene生命周期中注册的操作，会代理到这里由Browser处理
- (void)sceneController:(RIAIDBSceneController *)sceneController handleTrigger:(int32_t)triggerKey {
    [self handleTrigger:triggerKey];
}

#pragma mark - RIAIDBrowserTriggerHandlerDelegate
/// triggerHandler的代理方法，由Browser处理Actions
- (void)triggerHandler:(RIAIDBrowserTriggerHandler *)handler execute:(NSArray<RIAIDADActionModel *> *)actions {
    for (RIAIDADActionModel *action in actions) {
        [self handleAction:action];
    }
}

#pragma mark - Handle Toggle Event
- (void)handleToggleEvent:(RIAIDBToggleEventType)eventType {
    switch (eventType) {
        case RIAIDBToggleEventTypeAdVideoEnd:
        {
            [self handleAdVideoEndEvent];
        }
            break;
        default:
            break;
    }
}

/// 广告视频播放结束
- (void)handleAdVideoEndEvent {
    [self handleTrigger:RIAIDSystemKeyEnum_SystemKeys_TriggerKeyAdVideoEnd];
}

#pragma mark - Handle Action
- (void)handleAction:(RIAIDADActionModel*)actionModel {
    switch (actionModel.actionType) {
        case RIAIDADActionTypeTransition: {
            /// 转场操作
            for (RIAIDADTransitionModel *transitionModel in actionModel.transition.transitionsArray) {
                [self.director handleTransition:transitionModel];
            }
        } break;
        case RIAIDADActionTypeTrack: {
            /// 埋点
            NSMutableDictionary *newParameters = [NSMutableDictionary dictionary];
            id<RIAIDRDataBindingService> dataBinding = [self.context.serviceContainer
                                                        getServiceInstance:@protocol(RIAIDRDataBindingService)];
            for (NSString *key in actionModel.track.parameters.allKeys) {
                NSString *value = [dataBinding parseHolderData:actionModel.track.parameters[key]];
                [newParameters setObject:(value ?: @"") forKey:key];
            }
            if (self.delegate && [self.delegate respondsToSelector:@selector(browser:handleTrack:)]) {
                [self.delegate browser:self handleTrack:newParameters];
            }
        } break;
        case RIAIDADActionTypeVideo: {
            /// 播放器操作
            
            /// 是否是外部播放器
            BOOL isExternalVideo = actionModel.video.viewKey == 0;
            
            if (isExternalVideo) {
                /// 外部播放器
                if (self.delegate && [self.delegate respondsToSelector:@selector(browser:handleVideoAction:)]) {
                    [self.delegate browser:self handleVideoAction:actionModel.video.type];
                }
            } else {
                /// 内部播放器
                int32_t viewKey = actionModel.video.viewKey;
                KCADRender *targetRender = [self.director findRenderByViewKey:viewKey];
                KCADRenderNode *findResultRender = [targetRender findRenderNodeByKey:viewKey];
                
                if ([findResultRender isKindOfClass:[RIAIDRenderVideoNode class]]) {
                    [findResultRender dispatchEvent:[@(actionModel.video.type) stringValue] keyList:@[@(viewKey)] attributes:nil];
                }
            }
        } break;
        case RIAIDADActionTypeURL: {
            /// 跳转URL
            if (self.delegate && [self.delegate respondsToSelector:@selector(browser:handleUrl:)]) {
                [self.delegate browser:self handleUrl:actionModel.URL];
            }
        } break;
        case RIAIDADActionTypeConditionChange: {
            /// 条件改变
            [self.globalVarArea updateCondition:actionModel.conditionChange.condition];
        } break;
        case RIAIDADActionTypeVariableChange: {
            /// 变量改变
            [self.globalVarArea updateVariable:actionModel.variableChange.variable];
        } break;
        case RIAIDADActionTypeCancelTimer:{
            /// 取消已经注册的Timer控制器
            [self.triggerHandler cancelTriggerTimer:actionModel.cancelTimer.triggerKey];
        } break;
        case RIAIDADActionTypeCustom: {
            /// 自定义操作
            if (self.delegate && [self.delegate respondsToSelector:@selector(browser:handleCustomAction:)]) {
                [self.delegate browser:self handleCustomAction:actionModel.custom];
            }
        } break;
        case RIAIDADActionTypeTrigger: {
            /// 触发器Action
            [actionModel.trigger.triggerKeysArray enumerateValuesWithBlock:^(int32_t value, NSUInteger idx, BOOL * _Nonnull stop) {
                [self handleTrigger:value];
            }];
        } break;
        case RIAIDADActionTypeConversion: {
            /// 转化操作
            if (self.delegate && [self.delegate respondsToSelector:@selector(browser:handlerConversionAction:)]) {
                [self.delegate browser:self handlerConversionAction:actionModel.conversion];
            }
        } break;
        case RIAIDADActionTypeStep: {
            /// 分步执行Action
            /// 处理变量
            RIAIDADStepActionModel *step = actionModel.step;
            RIAIDBasicVariable *var = [self.globalVarArea.varMap objectForKey:@(step.variableKey).stringValue];
            if (var.value.type == RIAIDBasicVariableValue_Type_Integer) {
                int64_t targetValue = (int64_t)(var.value.i + step.step);
                if (targetValue >= step.min && targetValue <= step.max) {
                    var.value.i = targetValue;
                    /// 更新到全局变量
                    [self.globalVarArea updateVariable:var];
                }
            }
            
            /// 触发Trigger
            [step.triggerKeysArray enumerateValuesWithBlock:^(int32_t value, NSUInteger idx, BOOL * _Nonnull stop) {
                [self handleTrigger:value];
            }];
        } break;
        case RIAIDADActionTypeVibrator: {
            /// 设备震动Action
            UIImpactFeedbackGenerator *generator = [[UIImpactFeedbackGenerator alloc] initWithStyle:UIImpactFeedbackStyleMedium];
            [generator impactOccurred];
        } break;
        case RIAIDADActionTypeCancelDeviceMotion: {
            /// 取消设备运动Trigger监听
            RIAIDADCancelDeviceMotionActionModel *cancelDeviceMotion = actionModel.cancelDeviceMotion;
            [self.triggerHandler cancelDeviceMotion:cancelDeviceMotion.triggerKey];
        } break;
        default:
            break;
    }
}

#pragma mark - Lazy init
- (RIAIDBrowserCanvasView *)canvas {
    if (!_canvas) {
        _canvas = [RIAIDBrowserCanvasView new];
    }
    return _canvas;
}

- (RIAIDBrowserDirector *)director {
    if (!_director) {
        _director = [[RIAIDBrowserDirector alloc] initWithCanvas:self.canvas context:self.context];
    }
    return _director;
}

- (RIAIDBGlobalVarArea *)globalVarArea {
    if (!_globalVarArea) {
        _globalVarArea = [RIAIDBGlobalVarArea new];
    }
    return _globalVarArea;
}

- (RIAIDBrowserTriggerHandler *)triggerHandler {
    if (!_triggerHandler) {
        _triggerHandler = [RIAIDBrowserTriggerHandler new];
        _triggerHandler.delegate = self;
        _triggerHandler.director = self.director;
        _triggerHandler.globalVarArea = self.globalVarArea;
    }
    return _triggerHandler;
}

- (RIAIDBrowserFunctionHandler *)functionHandler {
    if (!_functionHandler) {
        _functionHandler = [RIAIDBrowserFunctionHandler new];
        _functionHandler.director = self.director;
    }
    return _functionHandler;
}

- (RIAIDRenderContext *)context {
    if (!_context) {
        _context = [RIAIDRenderContext new];
    }
    return _context;
}

- (RIAIDBServiceContainer *)serviceContainer {
    if (!_serviceContainer) {
        _serviceContainer = [RIAIDBServiceContainer new];
        
        /// 数据绑定Service
        RIAIDBDataBindingService *bDBBindingService = [RIAIDBDataBindingService new];
        bDBBindingService.functionHandler = self.functionHandler;
        bDBBindingService.globalVarArea = self.globalVarArea;
        [_serviceContainer registerService:NSProtocolFromString(@"RIAIDRDataBindingService") serviceInstance:bDBBindingService];
        
        /// 消费操作Service
        RIAIDBConsumeActionService *bActionService = [RIAIDBConsumeActionService new];
        bActionService.triggerHandler = self.triggerHandler;
        [_serviceContainer registerService:NSProtocolFromString(@"RIAIDRConsumeActionService") serviceInstance:bActionService];
        
        /// 外部自定义Service
        [self.externalServiceList enumerateObjectsUsingBlock:^(id<RIAIDBExternalServiceProtocol>  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            [_serviceContainer registerExternalService:[obj serviceProtocol] serviceInstance:[obj serviceInstance]];
        }];
    }
    
    return _serviceContainer;
}

#pragma mark - Reset
- (void)reset {
    /// 移除所有场景
    for (RIAIDBSceneController *sceneCtrl in [self.director.sceneCtrlMap allValues]) {
        [sceneCtrl resetScene];
        [sceneCtrl.adRender.decorView removeFromSuperview];
    }
    /// 取消已经发出的定时任务
    [self.triggerHandler cancelAllTriggerTimer];
    /// 清空
    self.director = nil;
    self.triggerHandler = nil;
    self.functionHandler = nil;
    self.serviceContainer = nil;
    /// 重新配置
    [self config];
}
@end
