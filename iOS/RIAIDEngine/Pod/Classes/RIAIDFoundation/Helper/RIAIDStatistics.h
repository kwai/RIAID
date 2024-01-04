//
//  RIAIDStatistics.h
//  KCADRender
//
//  Created by simon on 2022/1/14.
//

#import <Foundation/Foundation.h>

#import "RIAIDRenderContext.h"

NS_ASSUME_NONNULL_BEGIN

/// RIAID 性能指标上报
@interface RIAIDStatistics : NSObject

+ (void)addEventWithKey:(NSString *)eventKey value:(NSString *)value context:(RIAIDRenderContext *)context;

@end

NS_ASSUME_NONNULL_END
