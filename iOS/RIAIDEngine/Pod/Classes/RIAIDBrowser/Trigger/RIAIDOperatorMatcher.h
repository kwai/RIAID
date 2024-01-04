//
//  RIAIDOperatorTool.h
//  KCADBrowser
//
//  Created by liweipeng on 2022/6/9.
//

#import <Foundation/Foundation.h>
#import "RIAID.h"

NS_ASSUME_NONNULL_BEGIN

/// 计算工具类
@interface RIAIDOperatorMatcher : NSObject

/// 将RIAIDCompareOperator与NSComparisonResult做结果匹配
/// @param comparision NSComparisonResult参数
/// @param operator RIAIDCompareOperator参数
/// @return 是否匹配
+ (BOOL)isMatch:(NSComparisonResult)comparision operator:(RIAIDCompareOperator)operator;

@end

NS_ASSUME_NONNULL_END
