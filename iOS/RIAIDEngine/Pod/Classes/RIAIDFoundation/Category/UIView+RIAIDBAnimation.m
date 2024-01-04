//
//  UIView+RIAIDBAnimation.m
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/22.
//

#import "UIView+RIAIDBAnimation.h"
#import "RIAIDTimeConversion.h"
#import "RIAIDBObjectContainer.h"
#import <objc/runtime.h>
#pragma mark - view
#import "RIAIDRenderTouchView.h"
#import "RIAIDRenderTouchImageView.h"


#define KCompletionContainerKey @"KCompletionContainerKey"

/// 该分类 RIAID 相关的动画代理被其他其他分类劫持，此次针对 RIAIDRenderTouchImageView 做处理
@interface RIAIDRenderTouchImageView (RIAIDBAnimation)<CAAnimationDelegate>
@end
@implementation RIAIDRenderTouchImageView (RIAIDBAnimation)
- (void)animationDidStop:(CAAnimation *)anim finished:(BOOL)flag {
    RIAIDBObjectContainer *container = [anim valueForKey:KCompletionContainerKey];
    RIAIDBAnimationCompletionBlock block = container.cpObject;
    
    if (block) {
        block(flag);
    }
}
@end

/// 该分类 RIAID 相关的动画代理被其他其他分类劫持，此次针对 RIAIDRenderTouchView 做处理
@interface RIAIDRenderTouchView (RIAIDBAnimation)<CAAnimationDelegate>
@end
@implementation RIAIDRenderTouchView (RIAIDBAnimation)
- (void)animationDidStop:(CAAnimation *)anim finished:(BOOL)flag {
    RIAIDBObjectContainer *container = [anim valueForKey:KCompletionContainerKey];
    RIAIDBAnimationCompletionBlock block = container.cpObject;
    
    if (block) {
        block(flag);
    }
}
@end

@implementation UIView (RIAIDBAnimation)

- (void)executeRiaid:(RIAIDADAnimationModel*)riaidAnimationModel completion:(RIAIDBAnimationCompletionBlock)completion {
    CABasicAnimation *caAnimation = [self caAnimation:riaidAnimationModel];
    [self execute:caAnimation completion:completion];
}


- (void)executeFromValue:(nullable id)fromValue
                 toValue:(nullable id)toValue
                duration:(NSTimeInterval)duration
                 keyPath:(nullable NSString *)keyPath
              completion:(nullable RIAIDBAnimationCompletionBlock)completion {
    if (!fromValue
        || !toValue) {
        return;
    }
    CABasicAnimation *basicAnimation = [CABasicAnimation animationWithKeyPath:keyPath];
    basicAnimation.fromValue = fromValue;
    basicAnimation.toValue = toValue;
    basicAnimation.duration = duration;
    [self execute:basicAnimation completion:completion];
}

- (void)execute:(CAAnimation*)animation completion:(RIAIDBAnimationCompletionBlock)completion {
    if (animation) {
        if (completion) {
            RIAIDBObjectContainer *container = [[RIAIDBObjectContainer alloc] initWithCopyObject:completion];
            [animation setValue:container forKey:KCompletionContainerKey];
        }
        
        animation.delegate = self;
        [self.layer addAnimation:animation forKey:nil];
    }
}

/// 将riaidAnimationModel转换为CABasicAnimation对象
- (CABasicAnimation*)caAnimation:(RIAIDADAnimationModel*)riaidAnimationModel {
    CABasicAnimation *animaiton;
    
    switch (riaidAnimationModel.propertyType) {
        case RIAIDADAnimationModel_ViewPropertyType_Alpha:
            animaiton = [CABasicAnimation animationWithKeyPath:@"opacity"];
            break;
        case RIAIDADAnimationModel_ViewPropertyType_Scale:
            animaiton = [CABasicAnimation animationWithKeyPath:@"transform.scale"];
            break;
        case RIAIDADAnimationModel_ViewPropertyType_Rotation:
            animaiton = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
            break;
        case RIAIDADAnimationModel_ViewPropertyType_Hidden:
            animaiton = [CABasicAnimation animationWithKeyPath:@"hidden"];
            break;
        case RIAIDADAnimationModel_ViewPropertyType_Width:
            animaiton = [CABasicAnimation animationWithKeyPath:@"bounds.size.width"];
            break;
        case RIAIDADAnimationModel_ViewPropertyType_Height:
            animaiton = [CABasicAnimation animationWithKeyPath:@"bounds.size.height"];
            break;
        default:
            break;
    }
    
    animaiton.fillMode = kCAFillModeForwards;
    animaiton.duration = [RIAIDTimeConversion millisecondToSecond:riaidAnimationModel.duration];
    animaiton.repeatCount = riaidAnimationModel.repeatCount == -1 ? HUGE_VALF : riaidAnimationModel.repeatCount;
    // TODO: 确认内存问题
    animaiton.removedOnCompletion = NO;
    
    if (riaidAnimationModel.propertyType == RIAIDADAnimationModel_ViewPropertyType_Hidden) {
        /// Hidden动画时，riaidAnimationModel.valuesArray_Count必须为1
        if (riaidAnimationModel.valuesArray_Count != 1) {
            NSAssert(NO, @"Hidden Animation valuesArray_Count != 1");
            return nil;
        }
        float toValue = [riaidAnimationModel.valuesArray valueAtIndex:0];
        animaiton.toValue = [NSNumber numberWithFloat:toValue];

    } else {
        /// 非Hidden动画时，riaidAnimationModel.valuesArray_Count必须为2
        if (riaidAnimationModel.valuesArray_Count != 2) {
            NSAssert(NO, @"Animation valuesArray_Count != 2");
            return nil;
        }
        float fromValue = [riaidAnimationModel.valuesArray valueAtIndex:0];
        float toValue = [riaidAnimationModel.valuesArray valueAtIndex:1];
        animaiton.fromValue = [NSNumber numberWithFloat:fromValue];
        animaiton.toValue = [NSNumber numberWithFloat:toValue];
    }
    
    return animaiton;
}

#pragma mark - CAAnimationDelegate
- (void)animationDidStop:(CAAnimation *)anim finished:(BOOL)flag {
    RIAIDBObjectContainer *container = [anim valueForKey:KCompletionContainerKey];
    RIAIDBAnimationCompletionBlock block = container.cpObject;
    
    if (block) {
        block(flag);
    }
}
@end
