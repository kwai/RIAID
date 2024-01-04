//
//  KCADBTriggerHandler.m
//  KCADBTriggerHandler
//
//  Created by liweipeng on 2021/12/12.
//

#import "RIAIDBrowserTriggerHandler.h"
#import "RIAIDADTriggerModel+Browser.h"
#import "RIAIDBrowserConditionChecker.h"
#import "RIAIDBrowserTimerControllerProtocol.h"
#import "RIAIDBrowserTimerController.h"
#import "RIAIDBrowserVideoTimerController.h"
#import "RIAIDBrowserDeviceMotionHandler.h"

#import "RIAIDLog.h"
#import "NSNumber+RIAIDBToString.h"

@interface RIAIDBrowserTriggerHandler()<RIAIDBrowserTimerDelegate, RIAIDBrowserDeviceMotionHandlerDelegate>

/// Trigger哈希表，以TriggerKey(字符串)->RIAIDADTriggerModel 的形式存储
@property (nonatomic, strong) NSMutableDictionary<NSString*, RIAIDADTriggerModel*> *triggerMap;

/// Timer控制器哈斯表，以TriggerKey(字符串)->RIAIDBrowserTimerController的形式存储
@property (nonatomic, strong) NSMutableDictionary<NSString*, id<RIAIDBrowserTimerControllerProtocol>> *timerMap;

/// 设备能力监听处理类
@property (nonatomic, strong) RIAIDBrowserDeviceMotionHandler *deviceMotionHandler;
@end

@implementation RIAIDBrowserTriggerHandler

- (void)registerTriggers:(NSArray<RIAIDADTriggerModel*>*)triggers {
    for (RIAIDADTriggerModel *trigger in triggers) {
        [self.triggerMap setObject:trigger forKey:@(trigger.key).riaidIntString];
    }
}

#pragma mark - handle Trigger
- (void)handleTrigger:(int32_t)triggerKey {
    RIAIDADTriggerModel *trigger = [self.triggerMap objectForKey:@(triggerKey).riaidIntString];
    
    RIAIDLog(@"执行Trigger key:%d", triggerKey);
    
    switch (trigger.triggerType) {
        case RIAIDADTriggerTypeTimeout:
        case RIAIDADTriggerTypeHeartbeat:
        case RIAIDADTriggerTypeVideoTimeout:
            [self registerTimerTrigger:trigger];
            break;
        case RIAIDADTriggerTypeGeneral:
            [self handleGeneralModel:trigger.general];
            break;
        case RIAIDADTriggerTypeCondition:
            [self handleConditionModel:trigger.condition];
            break;
        case RIAIDADTriggerTypeDeviceMotion:
            [self handleDeviceMotion:trigger.deviceMotion];
            break;
        default:
            break;
    }
}

- (void)handleGeneralModel:(RIAIDADGeneralTriggerModel*)generalModel {
    [self handleActions:generalModel.actionsArray];
}

- (void)handleConditionModel:(RIAIDADConditionTriggerModel*)conditionModel {
    NSArray<RIAIDADActionModel*> *actions = [RIAIDBrowserConditionChecker getActions:conditionModel conditionMap:self.globalVarArea.conditionMap varMap:self.globalVarArea.varMap];
    [self handleActions:actions];
}

- (void)handleActions:(NSArray<RIAIDADActionModel*>*)actions {
    if (_delegate && [_delegate respondsToSelector:@selector(triggerHandler:execute:)]) {
        [_delegate triggerHandler:self execute:actions];
    }
}

#pragma mark - DeviceMotion
- (void)handleDeviceMotion:(RIAIDADDeviceMotionTriggerModel*)deviceMotion {
    [self.deviceMotionHandler addDeviceMotion:deviceMotion];
}

- (void)cancelDeviceMotion:(int32_t)triggerKey {
    [self.deviceMotionHandler cancelDeviceMotion:triggerKey];
}

- (void)deviceMotionHandler:(RIAIDBrowserDeviceMotionHandler *)handler execute:(NSArray<RIAIDADActionModel *> *)actions {
    [self handleActions:actions];
}

#pragma mark - Timer
/// 注册Timer控制器
- (void)registerTimerTrigger:(RIAIDADTriggerModel*)trigger {
    id<RIAIDBrowserTimerControllerProtocol> timer;
    if (trigger.triggerType == RIAIDADTriggerTypeVideoTimeout) {
        RIAIDBrowserVideoTimerController *videoTimer = [[RIAIDBrowserVideoTimerController alloc] initWithTrigger:trigger];
        videoTimer.director = self.director;
        timer = videoTimer;
    } else {
        timer = [[RIAIDBrowserTimerController alloc] initWithTrigger:trigger];
    }
    
    timer.delegate = self;
    [self.timerMap setObject:timer forKey:@(trigger.key).riaidIntString];
    [timer start];
}

/// 取消Timer控制器
- (void)cancelTriggerTimer:(int32_t)triggerKey {
    id<RIAIDBrowserTimerControllerProtocol> timer = [self.timerMap objectForKey:@(triggerKey).riaidIntString];
    [timer cancel];
}

- (void)cancelAllTriggerTimer {
    for (id<RIAIDBrowserTimerControllerProtocol> timer in self.timerMap.allValues) {
        [timer cancel];
    }
}

#pragma mark - RIAIDBrowserTimerDelegate
- (void)timerExecuteActions:(NSArray<RIAIDADActionModel*>*)actions trigger:(RIAIDADTriggerModel*)trigger {
    [self handleActions:actions];
}

- (void)timerDidCancel:(RIAIDADTriggerModel *)trigger {
    [self.timerMap removeObjectForKey:@(trigger.key).riaidIntString];
}


#pragma mark - Lazy init
- (NSMutableDictionary<NSString *,RIAIDADTriggerModel *> *)triggerMap {
    if (!_triggerMap) {
        _triggerMap = [NSMutableDictionary dictionary];
    }
    return _triggerMap;
}

- (NSMutableDictionary<NSString *, id<RIAIDBrowserTimerControllerProtocol>> *)timerMap {
    if (!_timerMap) {
        _timerMap = [NSMutableDictionary dictionary];
    }
    return _timerMap;
}

- (RIAIDBrowserDeviceMotionHandler *)deviceMotionHandler {
    if (!_deviceMotionHandler) {
        _deviceMotionHandler = [RIAIDBrowserDeviceMotionHandler new];
        _deviceMotionHandler.delegate = self;
    }
    return _deviceMotionHandler;
}
@end
