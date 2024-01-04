//
//  UIView+RIAIDBAnimation.h
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/22.
//

#import <UIKit/UIKit.h>
#import "RIAID.h"

NS_ASSUME_NONNULL_BEGIN

typedef void(^RIAIDBAnimationCompletionBlock)(BOOL);

@interface UIView (RIAIDBAnimation)<CAAnimationDelegate>

/// 给UIView对象本身添加动画
/// @param riaidAnimationModel 广告场景内动画模型
/// @param completion 动画完成回调
- (void)executeRiaid:(RIAIDADAnimationModel*)riaidAnimationModel completion:(RIAIDBAnimationCompletionBlock)completion;

/// 给UIView对象本身添加动画
/// @param animation 动画模型
/// @param completion 动画完成回调
- (void)execute:(CAAnimation*)animation completion:(RIAIDBAnimationCompletionBlock)completion;


- (void)executeFromValue:(nullable id)fromValue
                 toValue:(nullable id)toValue
                duration:(NSTimeInterval)duration
                 keyPath:(nullable NSString *)keyPath
              completion:(nullable RIAIDBAnimationCompletionBlock)completion;

@end

NS_ASSUME_NONNULL_END
