//
//  RIAIDBrowserDirector+Transition.h
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/19.
//

#import "RIAIDBrowserDirector.h"

NS_ASSUME_NONNULL_BEGIN
/// 处理Transition相关界面操作
@interface RIAIDBrowserDirector (Transition)

/// 处理RIAIDADTransitionModel
/// @param transition 转场信息实例
- (void)handleTransition:(RIAIDADTransitionModel*)transition;

@end

NS_ASSUME_NONNULL_END
