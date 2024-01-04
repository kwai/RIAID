//
//  RIAIDBrowserTimerControllerProtocol.h
//  Pods
//
//  Created by liweipeng on 2022/4/8.
//

#ifndef RIAIDBrowserTimerControllerProtocol_h
#define RIAIDBrowserTimerControllerProtocol_h

#import "RIAID.h"

/// Timer-Controller的代理方法
@protocol RIAIDBrowserTimerDelegate <NSObject>

/// Timer-Controller 对应的操作处理
/// @param actions 配置的定时触发操作
/// @param trigger Timer-Trigger模型
- (void)timerExecuteActions:(NSArray<RIAIDADActionModel*>*)actions trigger:(RIAIDADTriggerModel*)trigger;

/// 当Timer控制器被取消时回调
/// @discussion: 外部取消，或者内部操作完成，都会调用。
- (void)timerDidCancel:(RIAIDADTriggerModel*)trigger;
@end

@protocol RIAIDBrowserTimerControllerProtocol <NSObject>

/// 代理对象
@property (nonatomic, weak) id<RIAIDBrowserTimerDelegate> delegate;

/// 唯一初始化方法
/// @param trigger Timer-Trigger模型
- (instancetype)initWithTrigger:(RIAIDADTriggerModel*)trigger;

/// 启动Timer控制器
- (void)start;
/// 取消Timer控制器
- (void)cancel;

@end


#endif /* RIAIDBrowserTimerControllerProtocol_h */
