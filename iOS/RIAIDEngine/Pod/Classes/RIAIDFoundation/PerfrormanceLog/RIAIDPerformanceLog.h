//
//  RIAIDPerformanceLog.h
//  KCADRender
//
//  Created by simon on 2022/1/4.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// RIAID 性能打点工具
@interface RIAIDPerformanceLog : NSObject

/// 开始打点
/// @param eventKey 传入对应的指标 key
- (void)startRecordWithEvent:(NSString *)eventKey;

/// 结束打点
/// @param eventKey 传入对应的指标 key
- (CGFloat)endRecordWithEvent:(NSString *)eventKey;

@end

NS_ASSUME_NONNULL_END
