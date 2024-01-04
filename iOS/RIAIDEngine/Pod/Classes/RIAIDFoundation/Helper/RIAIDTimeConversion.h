//
//  TimeConversion.h
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/20.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// 时间单位转换工具类
@interface RIAIDTimeConversion : NSObject

/// 将秒转换为毫秒
/// @param second 以秒为单位的时间
+ (NSTimeInterval)millisecondToSecond:(int64_t)second;

@end

NS_ASSUME_NONNULL_END
