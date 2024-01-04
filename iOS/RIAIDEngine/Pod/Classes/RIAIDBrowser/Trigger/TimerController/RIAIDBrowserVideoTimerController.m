//
//  RIAIDBrowserVideoTimerController.m
//  KCADBrowser
//
//  Created by liweipeng on 2022/4/8.
//

#import "RIAIDBrowserVideoTimerController.h"
#import "RIAIDADTriggerModel+Browser.h"
#import "RIAIDBrowserDirector.h"

#import "RIAIDTimeConversion.h"
#import "RIAIDBrowserWeakProxy.h"
#import "RIAIDLog.h"
#import "RIAIDRenderVideoNode.h"
#import "RIAIDRVideoStateProtocol.h"
#import "RIAIDRServiceContainer.h"

@interface RIAIDBrowserVideoTimerController()
/// 定时器Trigger对象
@property (nonatomic, strong) RIAIDADTriggerModel *trigger;
/// 已经执行次数
@property (nonatomic, assign) NSUInteger executedCount;
/// NSTimer
@property (nonatomic, strong) NSTimer *nsTimer;
@end

@implementation RIAIDBrowserVideoTimerController
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
    
    if ([self isVideoTimerTrigger]) {
        NSTimeInterval interval = [self timerInterval];
        BOOL repeat = [self isRepeatTimer];
        NSDate *fireDate = [self fireDate];
        _nsTimer = [[NSTimer alloc] initWithFireDate:fireDate interval:interval target:proxy selector:@selector(timerAction) userInfo:nil repeats:repeat];
        [[NSRunLoop mainRunLoop] addTimer:_nsTimer forMode:NSRunLoopCommonModes];
    }
}

- (void)timerAction {
    /// trigger中配置单位是毫秒，这里需要转换为秒
    NSTimeInterval interval = [RIAIDTimeConversion millisecondToSecond:self.trigger.videoDuration.interval];
    
    int32_t viewKey = self.trigger.videoDuration.viewKey;
    KCADRender *targetRender = [self.director findRenderByViewKey:viewKey];
    KCADRenderNode *findResultRender = [targetRender findRenderNodeByKey:viewKey];
    
    if ([findResultRender isKindOfClass:[RIAIDRenderVideoNode class]]) {
        id<RIAIDRVideoStateProtocol> videoStateInfo = [(RIAIDRenderVideoNode *)findResultRender getVideoStateInfo];
        /// 以秒为单位的播放时间
        if ([videoStateInfo respondsToSelector:@selector(getCurrentPosition)]) {
            NSTimeInterval playTime = [videoStateInfo getCurrentPosition];
            if (playTime >= interval) {
                [self executeTriggerActions];
            }
        }
    }
}

- (void)executeTriggerActions {
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
- (BOOL)isVideoTimerTrigger {
    return self.trigger.triggerType == RIAIDADTriggerTypeVideoTimeout;
}

- (NSDate*)fireDate {
    return [NSDate date];
}

- (NSTimeInterval)timerInterval {
    // trigger中配置单位是毫秒，这里需要转换
    NSTimeInterval interval = [RIAIDTimeConversion millisecondToSecond:self.trigger.videoDuration.interval];
    
    // 次数除以10得出的是去播放器取播放时间的间隔，和Android实现对齐
    return interval/10.f;
}

- (BOOL)isRepeatTimer {
    return YES;
}

- (NSUInteger)triggerExecuteCount {
    return 1;
}

- (NSArray<RIAIDADActionModel*>*)triggerActions {
    return self.trigger.videoDuration.actionsArray;
}

@end
