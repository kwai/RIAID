//
//  KCADBrowser.h
//  KCADBrowser
//
//  Created by liweipeng on 2021/11/29.
//

#import <Foundation/Foundation.h>
#import "RIAID.h"
#import "RIAIDBrowserCanvasView.h"
#import "RIAIDBToggleEvent.h"
#import "RIAIDBExternalService.h"
#import "RIAIDBExternalRenderProvider.h"

NS_ASSUME_NONNULL_BEGIN
@class RIAIDBrowser;

#pragma mark - RIAIDBrowserDelegate
/// RIAIDBrowser的代理接口
@protocol RIAIDBrowserDelegate <NSObject>
@optional
/// 埋点代理方法
/// @param browser 对应的browser实例
/// @param parameters 埋点参数
- (void)browser:(RIAIDBrowser*)browser handleTrack:(NSDictionary<NSString*, NSString*>*)parameters;


/// 播放器控制代理方法
/// @param browser 对应的browser实例
/// @param videoControlType 播放器操作枚举类型
- (void)browser:(RIAIDBrowser*)browser handleVideoAction:(RIAIDADVideoActionModel_VideoControlType)videoControlType;


/// URL跳转代理方法
/// @param browser 对应的browser实例
/// @param urlModel URL跳转参数模型
- (void)browser:(RIAIDBrowser*)browser handleUrl:(RIAIDADUrlActionModel*)urlModel;


/// 转化跳转代理方法
/// @param browser 对应的browser实例
/// @param conversionModel 转化跳转参数模型
- (void)browser:(RIAIDBrowser*)browser handlerConversionAction:(RIAIDADConversionActionModel*)conversionModel;

/// 自定义操作代理方法
/// @param browser 对应的browser实例
/// @param customModel 自定义操作参数模型
- (void)browser:(RIAIDBrowser*)browser handleCustomAction:(RIAIDADCustomActionModel*)customModel;
@end

#pragma mark - RIAIDBrowserDirectorTransitionDelegate
/// 动画专场事件回调
@protocol RIAIDBrowserDirectorTransitionDelegate <NSObject>

/// 动画完成回调
/// @param targetRender 执行动画的 Render
- (void)transitionVisibilityComplete:(KCADRender *)targetRender hidden:(BOOL)hidden;

@end


/// 广告浏览器，采用BS（Browser-Server）架构，解决广告样式可配置以及服务端控制等问题
@interface RIAIDBrowser : NSObject

/// 初始化方法，通过一个ADDirectorModel模型，创建实例
/// @param model ADDirectorModel实例
/// @param externalRenderProvider 定制Render的提供类
/// @param externalServiceList 定制服务列表
- (instancetype)initWithDSLModel:(RIAIDRiaidModel*)model
          externalRenderProvider:(nullable id<RIAIDBExternalRenderProvider>)externalRenderProvider
             externalServiceList:(nullable NSArray<id<RIAIDBExternalServiceProtocol>>*)externalServiceList;

/// 画布实例，是所有页面展示的载体
/// 通过initWithDSLModel创建并获得一个KCADBrowser实例时，内部会生成一个canvas。
@property (nonatomic, strong, readonly) RIAIDBrowserCanvasView *canvas;

/// 实现了RIAIDBrowserDelegate协议的代理类
@property (nonatomic, weak) id<RIAIDBrowserDelegate> delegate;

/// 注册转场动画的监听回调
/// @param transitionDelegate 需要监听专场动画的代理
- (void)registerTransitionDelegate:(id<RIAIDBrowserDirectorTransitionDelegate>)transitionDelegate;

/// 处理外部传入事件
- (void)handleToggleEvent:(RIAIDBToggleEventType)eventType;

#pragma mark - LifeCycle
/// 所在的页面推入或从后台切换到后台，调用此方法
- (void)browserDisappear;

/// 所在的页面推入或从后台切换到前台，调用此方法
- (void)browserAppear;

/// 加载广告，例如广告滑入时
- (void)browserLoad;

/// 卸载广告，例如广告滑出时
- (void)browserUnLoad;
@end

NS_ASSUME_NONNULL_END
