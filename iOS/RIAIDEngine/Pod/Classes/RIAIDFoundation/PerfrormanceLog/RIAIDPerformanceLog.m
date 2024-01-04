//
//  RIAIDPerformanceLog.m
//  KCADRender
//
//  Created by simon on 2022/1/4.
//

#import "RIAIDPerformanceLog.h"

@interface RIAIDPerformanceLog ()

/// 埋点事件字典
@property (nonatomic, strong) NSMutableDictionary *eventDict;

@end

@implementation RIAIDPerformanceLog

+ (instancetype)shareInstance {
    static dispatch_once_t onceToken;
    static id sharedManager = nil;
    dispatch_once(&onceToken, ^{
        sharedManager = [[RIAIDPerformanceLog alloc] init];
    });
    return sharedManager;
}

- (instancetype)init {
    if (self = [super init]) {
        _eventDict = [NSMutableDictionary dictionary];
    }
    return self;
}

- (void)startRecordWithEvent:(NSString *)eventKey {
    if (eventKey.length == 0) {
        NSAssert(eventKey.length > 0, @"Invalid eventKey");
        return;
    }
    CFAbsoluteTime start = CFAbsoluteTimeGetCurrent();
    [self.eventDict setObject:[NSNumber numberWithDouble:start] forKey:eventKey];
}

- (CGFloat)endRecordWithEvent:(NSString *)eventKey {
    if (eventKey.length == 0) {
        NSAssert(eventKey.length > 0, @"Invalid eventKey");
        return 0.f;
    }
    CFAbsoluteTime start = [self.eventDict[eventKey] doubleValue];
    CFAbsoluteTime consume = (CFAbsoluteTimeGetCurrent() - start);
    if (consume >= 0) {
        [self.eventDict removeObjectForKey:eventKey];
        return consume * 1000;
    }
    return 0.f;
}

@end
