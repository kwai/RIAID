//
//  RIAIDBrowserTimer.m
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/18.
//

#import "RIAIDBrowserTimerController.h"
#import "RIAIDADTriggerModel+Browser.h"

#import "RIAIDTimeConversion.h"
#import "RIAIDBrowserWeakProxy.h"
#import "RIAIDLog.h"

@interface RIAIDBrowserTimerController()
/// 定时器Trigger对象
@property (nonatomic, strong) RIAIDADTriggerModel *trigger;
/// 已经执行次数
@property (nonatomic, assign) NSUInteger executedCount;
/// NSTimer
@property (nonatomic, strong) NSTimer *nsTimer;
@end


@implementation RIAIDBrowserTimerController
@synthesize delegate;

- (instancetype)initWithTrigger:(RIAIDADTriggerModel*)trigger {
    self = [self init];
    if (self) {
        _trigger = trigger;
    }
    return self;
}

- (void)createNSTimer {
    /// 借助proxy来防止循环引用
    RIAIDBrowserWeakProxy *proxy = [[RIAIDBrowserWeakProxy alloc] initWithTarget:self];
    
    if ([self isTimerTrigger]) {
        NSTimeInterval interval = [self timerInterval];
        BOOL repeat = [self isRepeatTimer];
        NSDate *fireDate = [self fireDate];
        _nsTimer = [[NSTimer alloc] initWithFireDate:fireDate interval:interval target:proxy selector:@selector(timerAction) userInfo:nil repeats:repeat];
        [[NSRunLoop mainRunLoop] addTimer:_nsTimer forMode:NSRunLoopCommonModes];
    }
}

- (void)timerAction {
    /// 定时操作调用
    NSArray<RIAIDADActionModel*> *actions = [self triggerActions];
    if (self.delegate && [self.delegate respondsToSelector:@selector(timerExecuteActions:trigger:)]) {
        [self.delegate timerExecuteActions:actions trigger:self.trigger];
    }
    
    self.executedCount += 1;
    [self checkExecutedCount];
}

- (void)checkExecutedCount {
    /// 检查操作执行次数，当执行次数已经足够，取消本控制器
    if (self.executedCount >= [self triggerExecuteCount]) {
        [self onTimerCancel];
    }
}

- (void)start {
    [self createNSTimer];
}

- (void)cancel {
    [self onTimerCancel];
}

/// 取消定时器
- (void)onTimerCancel {
    [self.nsTimer invalidate];

    if (self.delegate && [self.delegate respondsToSelector:@selector(timerDidCancel:)]) {
        [self.delegate timerDidCancel:self.trigger];
    }
}

- (void)dealloc {
    RIAIDLog(@"Timer控制器销毁，TriggerKey: %d", self.trigger.key);
}

#pragma mark - Trigger Info
- (BOOL)isTimerTrigger {
    return self.trigger.triggerType == RIAIDADTriggerTypeTimeout || self.trigger.triggerType == RIAIDADTriggerTypeHeartbeat;
}

- (NSDate*)fireDate {
    if (self.trigger.triggerType == RIAIDADTriggerTypeTimeout) {
        NSTimeInterval interval = [RIAIDTimeConversion millisecondToSecond:self.trigger.timeout.interval];
        return [NSDate dateWithTimeIntervalSinceNow:interval];
    }
    
    return [NSDate date];
}

- (NSTimeInterval)timerInterval {
    if (self.trigger.triggerType == RIAIDADTriggerTypeHeartbeat) {
        // trigger中配置单位是毫秒，这里需要转换
        NSTimeInterval interval = [RIAIDTimeConversion millisecondToSecond:self.trigger.heartbeat.interval];
        return interval;
    }
    return 0;
}

- (BOOL)isRepeatTimer {
    if (self.trigger.triggerType == RIAIDADTriggerTypeHeartbeat) {
        return YES;
    }
    return NO;
}

- (NSUInteger)triggerExecuteCount {
    if (self.trigger.triggerType == RIAIDADTriggerTypeHeartbeat) {
        return self.trigger.heartbeat.count;
    }
    return 1;
}

- (NSArray<RIAIDADActionModel*>*)triggerActions {
    if (self.trigger.triggerType == RIAIDADTriggerTypeTimeout) {
        return self.trigger.timeout.actionsArray;
    } else if (self.trigger.triggerType == RIAIDADTriggerTypeHeartbeat) {
        return self.trigger.heartbeat.actionsArray;
    }
    return @[];
}

@end
