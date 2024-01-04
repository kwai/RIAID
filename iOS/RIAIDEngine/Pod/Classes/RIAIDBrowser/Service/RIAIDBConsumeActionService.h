//
//  RIAIDBConsumeActionService.h
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/22.
//

#import <Foundation/Foundation.h>
#import "RIAIDRConsumeActionService.h"
#import "RIAID.h"

NS_ASSUME_NONNULL_BEGIN

/// 处理Trigger协议
@protocol RIAIDHandleTriggerProtocol <NSObject>
-(void)handleTrigger:(int32_t)triggerKey;
@end

/// 消费行动协议实现类，提供给ADRender处理相关操作
@interface RIAIDBConsumeActionService : NSObject<RIAIDRConsumeActionService>

/// Trigger处理器
@property (nonatomic, weak) id<RIAIDHandleTriggerProtocol> triggerHandler;
@end

NS_ASSUME_NONNULL_END
