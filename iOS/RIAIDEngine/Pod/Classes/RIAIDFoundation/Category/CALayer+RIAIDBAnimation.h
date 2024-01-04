//
//  CALayer+RIAIDBAnimation.h
//  KCADBrowser
//
//  Created by simon on 2022/1/6.
//

#import <QuartzCore/QuartzCore.h>

NS_ASSUME_NONNULL_BEGIN

//typedef void(^RIAIDBLayerAnimationCompletionBlock)(BOOL);

/// 给 layer 本身添加动画
@interface CALayer (RIAIDBAnimation)<CAAnimationDelegate>

/// 通过原始路径和新路径，对 layer 开启动画
- (void)executeFromValue:(nullable id)fromValue
                 toValue:(nullable id)toValue
                 keyPath:(nullable NSString *)keyPath
                duration:(NSTimeInterval)duration;

@end

NS_ASSUME_NONNULL_END
