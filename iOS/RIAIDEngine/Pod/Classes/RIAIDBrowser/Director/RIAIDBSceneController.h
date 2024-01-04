//
//  RIAIDBSceneController.h
//  KCADBrowser
//
//  Created by liweipeng on 2022/1/12.
//

#import <Foundation/Foundation.h>
#import "RIAID.h"
#import "KCADRender.h"
#import "RIAIDBExternalRenderProvider.h"

NS_ASSUME_NONNULL_BEGIN
@class RIAIDBSceneController;
@protocol RIAIDBSceneControllerDelegate <NSObject>
/// 处理Trigger
- (void)sceneController:(RIAIDBSceneController*)sceneController handleTrigger:(int32_t)triggerKey;
@end

/// 场景（Scene）的控制类
/// 内部包含布局、生命周期等逻辑
@interface RIAIDBSceneController : NSObject

/// 唯一指定初始化方法
/// @param sceneModel 场景数据模型
/// @param externalRenderProvider 定制Render的提供类
/// @param canvas 画布
/// @param context adrender需要的上下文
- (instancetype)initWithScene:(RIAIDADSceneModel*)sceneModel
       externalRenderProvider:(nullable id<RIAIDBExternalRenderProvider>)externalRenderProvider
                       canvas:(UIView*)canvas
                      context:(nullable RIAIDRenderContext *)context;

/// 弱持有代理
@property (nonatomic, weak) id<RIAIDBSceneControllerDelegate> delegate;

/// 场景数据模型
@property (nonatomic, copy, readonly) RIAIDADSceneModel *sceneModel;

/// 当前场景版本，从 0 开始计算
/// @discussion 场景对 render 会有很多异步操作，比如动画完成的操作，该字段用来记录每次操作的场景版本，异步操作可使用当前版本和上次场景版的比较
/// 判断当前操作是否有效
@property (nonatomic, assign, readonly) NSInteger currentSceneVersion;

/// 设置Scene的展示与隐藏，当isLifeCycle为YES时，会触发Scene的生命周期相关行为
/// @param hidden scene是否隐藏
/// @param isLifeCycle 是否需要触发Scene的LifeCycle
/// @param version 触发隐藏时场景的版本，用于判断触发时的场景版本和当前版本是否一致
/// @discussion: 若isLifeCycle为YES，触发Scene的LifeCycle同时，会触发对应配置的Triggers
- (void)setHidden:(BOOL)hidden isLifeCycle:(BOOL)isLifeCycle sceneVersion:(NSInteger)version;

/// 重置场景
/// @discussion 当场景需要重置时，调用该方法，此方法会使场景版本号加一
- (void)resetScene;

#pragma mark - Render

/// ADRender模型
@property (nonatomic, strong, nullable) KCADRender *adRender;

/// 将Render上屏。例：做 展示/隐藏 动画之前调用，因为做动画之前，需要保证Render的View在屏幕上。
- (void)onScreenIfNeed;

/// 当画布Size发生变化时，调用此方法重绘Render
/// @param canvasSize 画布Size
- (void)resizeRender:(CGSize)canvasSize;

/// 根据SceneModel创建Render。创建过程会借用SceneController内部持有的画布大小、外部Render创建服务、以及Render需要的上下文Context。
/// @param sceneModel 场景数据模型
- (KCADRender*)createRender:(RIAIDADSceneModel*)sceneModel;

#pragma mark - 布局
/// 将约束模型（ADSceneRelationModel）场景（Scene）绑定
/// 每个UIView拥有两个ADSceneRelationModel，分别代表X方向和Y方向的约束
/// x方向约束
@property (nonatomic, strong) RIAIDADSceneRelationModel *xRelation;
/// y方向约束
@property (nonatomic, strong) RIAIDADSceneRelationModel *yRelation;

/// 当前约束条件下, Scene对应的目标origin
/// 在真正setFrame之前，用于暂存origin。
/// 设置为NSNumber是为了方便判空，用户查看该Scene的目标origin是否已经计算出来
/// @discussion: originValue不等于已经给View设置的frame.origin
/// X方向Origin
@property (nonatomic, strong, nullable) NSNumber *originXValue;
/// Y方向Origin
@property (nonatomic, strong, nullable) NSNumber *originYValue;

/// 应用当前布局
- (void)applyCurrentLayout;

/// 返回预期占用尺寸。若有Render，根据Render返回；若无Render，根据SceneModel计算。
- (CGSize)expectSize;
@end

NS_ASSUME_NONNULL_END
