//
//  KCADBTriggerHandler.h
//  KCADBTriggerHandler
//
//  Created by liweipeng on 2021/12/12.
//

#import <Foundation/Foundation.h>
#import "RIAID.h"

#import "RIAIDBConsumeActionService.h"
#import "RIAIDBrowserDirector.h"
#import "RIAIDBGlobalVarArea.h"

NS_ASSUME_NONNULL_BEGIN
@class RIAIDADActionModel;
@class RIAIDBrowserTriggerHandler;
/// Trigger代理协议
@protocol RIAIDBrowserTriggerHandlerDelegate <NSObject>
/// 将Action操作代理出去，由外部代理执行
- (void)triggerHandler:(RIAIDBrowserTriggerHandler*)handler execute:(NSArray<RIAIDADActionModel*>*)actions;
@end

/// Trigger处理类
@interface RIAIDBrowserTriggerHandler : NSObject<RIAIDHandleTriggerProtocol>

/// 当前Browser所持有的director
@property (nonatomic, strong) RIAIDBrowserDirector *director;

/// 当前Browser所持有的GlobalVarArea
@property (nonatomic, strong) RIAIDBGlobalVarArea *globalVarArea;

/// 当前Browser所持有的DeviceMotionHandler

/// Trigger代理实例
@property (nonatomic, weak) id<RIAIDBrowserTriggerHandlerDelegate> delegate;

/// 注册所有Triggers
/// @param triggers RIAIDADTriggerModel数组
- (void)registerTriggers:(NSArray<RIAIDADTriggerModel*>*)triggers;

/// 处理Trigger
/// @param triggerKey Trigger对应的key
- (void)handleTrigger:(int32_t)triggerKey;

/// 取消Timer触发器对应的Timer控制器
/// @param triggerKey Timer触发器对应的key
- (void)cancelTriggerTimer:(int32_t)triggerKey;

/// 移除设备能力监听
/// @param key 设备能力Trigger模型的key
- (void)cancelDeviceMotion:(int32_t)triggerKey;

/// 取消所有已经发出的定时任务
- (void)cancelAllTriggerTimer;
@end

NS_ASSUME_NONNULL_END
