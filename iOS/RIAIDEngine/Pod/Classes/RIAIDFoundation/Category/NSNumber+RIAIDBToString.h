//
//  NSNumber+RIAIDBToString.h
//  RIAIDBToString
//
//  Created by liweipeng on 2021/12/16.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSNumber (RIAIDBToString)

/// 将NSNumer取int值转换为NSString
/// 实现: [NSString stringWithFormat:@"%d", self.intValue];
- (NSString*)riaidIntString;

@end

NS_ASSUME_NONNULL_END
