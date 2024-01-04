//
//  CALayer+RIAIDBAnimation.m
//  KCADBrowser
//
//  Created by simon on 2022/1/6.
//

#import "CALayer+RIAIDBAnimation.h"

#import <objc/runtime.h>

@implementation CALayer (RIAIDBAnimation)

- (void)executeFromValue:(nullable id)fromValue
                 toValue:(nullable id)toValue
                 keyPath:(nullable NSString *)keyPath
                duration:(NSTimeInterval)duration {
    if (!fromValue
        || !toValue) {
        return;
    }
    CABasicAnimation *layerAnimation = [CABasicAnimation animationWithKeyPath:keyPath];
    layerAnimation.duration = duration;
    layerAnimation.fromValue = fromValue;
    layerAnimation.toValue = toValue;
    if (layerAnimation) {
        if ([toValue isKindOfClass:[UIBezierPath class]]) {
            if ([self isKindOfClass:[CAShapeLayer class]]) {
                CAShapeLayer *shape = (CAShapeLayer *)self;
                shape.path = ((UIBezierPath *)toValue).CGPath;
            } else {
                self.shadowPath = ((UIBezierPath *)toValue).CGPath;
            }
        }
        [self addAnimation:layerAnimation forKey:nil];
    }
}

@end
