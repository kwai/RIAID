//
//  KCADBLayoutExecutor.h
//  KCADBLayoutExecutor
//
//  Created by liweipeng on 2021/12/1.
//

#import <Foundation/Foundation.h>
#import "KCADRender.h"
#import "RIAIDRenderContext.h"
#import "RIAID.h"
#import "RIAIDBSceneController.h"

@class RIAIDBrowserCanvasView;
@protocol RIAIDBrowserDirectorTransitionDelegate;

NS_ASSUME_NONNULL_BEGIN

/// 本类用于操作Browser中的场景（Scene），包括布局、转场、交互
@interface RIAIDBrowserDirector : NSObject

/// 画布对象
@property (nonatomic, strong, readonly) RIAIDBrowserCanvasView *canvas;

/// NSString*为key，RIAIDBSceneController*为value的哈希表
/// 内部存储了所有的RIAIDBSceneController
@property (nonatomic, strong, readonly) NSMutableDictionary<NSString*, RIAIDBSceneController*> *sceneCtrlMap;

/// 动画执行代理
@property (nonatomic, weak) id<RIAIDBrowserDirectorTransitionDelegate> transitionDelegate;

/// 唯一指定的初始化方法
/// @param canvas 画布实例
/// @param context ADRender需要的上下文对象
- (instancetype)initWithCanvas:(RIAIDBrowserCanvasView*)canvas context:(RIAIDRenderContext*)context;

/// 注册RIAIDBSceneController给Director
/// @param sceneCtrl RIAIDBSceneController对象
/// @param sceneKey sceneModel的key
- (void)registerSceneCtrl:(RIAIDBSceneController*)sceneCtrl forSceneKey:(int32_t)sceneKey;


/// 找出ViewKey对应的Render，若Render不存在，返回空
/// @param viewKey Render内部的ViewKey
- (nullable KCADRender*)findRenderByViewKey:(int32_t)viewKey;
@end

NS_ASSUME_NONNULL_END
