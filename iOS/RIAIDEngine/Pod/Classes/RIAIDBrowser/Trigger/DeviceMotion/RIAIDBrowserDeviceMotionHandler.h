//
//  RIAIDBrowserDeviceMotionHandler.h
//  KCADBrowser
//
//  Created by liweipeng on 2022/6/9.
//

#import <Foundation/Foundation.h>
#import "RIAID.h"

NS_ASSUME_NONNULL_BEGIN
@class RIAIDBrowserDeviceMotionHandler;

/// 设备运动代理协议
@protocol RIAIDBrowserDeviceMotionHandlerDelegate <NSObject>
/// 将Action操作代理出去，由外部代理执行
- (void)deviceMotionHandler:(RIAIDBrowserDeviceMotionHandler*)handler execute:(NSArray<RIAIDADActionModel*>*)actions;
@end

@interface RIAIDBrowserDeviceMotionHandler : NSObject

@property (nonatomic, weak) id<RIAIDBrowserDeviceMotionHandlerDelegate> delegate;

/// 添加设备能力监听
/// @param deviceMotion 设备能力Trigger模型
- (void)addDeviceMotion:(RIAIDADDeviceMotionTriggerModel*)deviceMotion;

/// 移除设备能力监听
/// @param key 设备能力Trigger模型的key
- (void)cancelDeviceMotion:(int32_t)key;

@end

NS_ASSUME_NONNULL_END
