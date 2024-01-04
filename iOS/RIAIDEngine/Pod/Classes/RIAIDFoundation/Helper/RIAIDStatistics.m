//
//  RIAIDStatistics.m
//  KCADRender
//
//  Created by simon on 2022/1/14.
//

#import "RIAIDStatistics.h"

#import "RIAIDCustomEventProtocol.h"

@implementation RIAIDStatistics

+ (void)addEventWithKey:(NSString *)eventKey value:(NSString *)value context:(RIAIDRenderContext *)context {
    id<RIAIDCustomEventProtocol> customEventService = [context.serviceContainer getServiceInstance:@protocol(RIAIDCustomEventProtocol)];
    RIAIDCustomEvent *customEvent = [RIAIDCustomEvent new];
    customEvent.key = eventKey;
    customEvent.value = value;
    [customEventService sendCustomEvent:customEvent];
}

@end
