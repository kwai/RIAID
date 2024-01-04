//
//  UIView+RIAIDCornerShadow.m
//  KCADRender
//
//  Created by simon on 2021/12/26.
//

#import "UIView+RIAIDCornerShadow.h"

#import <objc/runtime.h>

RIAIDCornerRadii RIAIDCornerRadiiMake(CGFloat topLeft, CGFloat topRight, CGFloat bottomLeft, CGFloat bottomRight) {
    RIAIDCornerRadii cornerRadii;
    cornerRadii.topLeft = topLeft;
    cornerRadii.topRight = topRight;
    cornerRadii.bottomLeft = bottomLeft;
    cornerRadii.bottomRight = bottomRight;
    return cornerRadii;
}

@implementation UIView (RIAIDCornerShadow)

// 圆角
- (void)addCornersWithCornerRadii:(RIAIDCornerRadii)cornerRadii {
    UIBezierPath *path = [UIView createPathWithRoundedRectWithBounds:(CGRect){CGPointZero, self.layerDrawSize}
                                                         cornerRadii:cornerRadii];
    CAShapeLayer *maskLayer = [CAShapeLayer layer];
    maskLayer.path = path.CGPath;
    self.maskLayer = maskLayer;
    self.layer.mask = maskLayer;
}

// 实线边框
- (void)addBorderColor:(UIColor *)borderColor
           borderWidth:(CGFloat)borderWidth
           cornerRadii:(RIAIDCornerRadii)cornerRadii {
    [self.borderLayer removeFromSuperlayer];
    UIView *targetView = self;
    UIBezierPath *path = [UIView createPathWithRoundedRectWithBounds:(CGRect){CGPointZero, self.layerDrawSize}
                                                         cornerRadii:cornerRadii];
    // 绘画边框
    if (borderWidth > 0) {
        CAShapeLayer *borderLayer = [[CAShapeLayer alloc] init];
        borderLayer.frame = targetView.bounds;
        borderLayer.strokeColor = (borderColor ?: UIColor.blackColor).CGColor;
        borderLayer.lineWidth = borderWidth;
        borderLayer.fillColor = [UIColor clearColor].CGColor;
        borderLayer.path = path.CGPath;
        self.borderLayer = borderLayer;
        [targetView.layer insertSublayer:self.borderLayer atIndex:0];
    }
}

// 虚线边框
- (void)addDashBorderColor:(UIColor *)borderColor
               borderWidth:(CGFloat)borderWidth
                   dashGap:(CGFloat)dashGap
                 dashWidth:(CGFloat)dashWidth
               cornerRadii:(RIAIDCornerRadii)cornerRadii {
    [self.borderLayer removeFromSuperlayer];
    UIView *targetView = self;
    UIBezierPath *path = [UIView createPathWithRoundedRectWithBounds:(CGRect){CGPointZero, self.layerDrawSize}
                                                         cornerRadii:cornerRadii];
    // 绘画边框
    if (borderWidth > 0) {
        CAShapeLayer *borderLayer = [[CAShapeLayer alloc] init];
        borderLayer.frame = targetView.bounds;
        borderLayer.strokeColor = (borderColor ?: UIColor.blackColor).CGColor;
        borderLayer.lineWidth = borderWidth;
        borderLayer.lineDashPattern = @[@(dashWidth), @(dashGap)];
        borderLayer.fillColor = [UIColor clearColor].CGColor;
        borderLayer.path = path.CGPath;
        self.borderLayer = borderLayer;
        [targetView.layer insertSublayer:self.borderLayer atIndex:0];
    }
}

// 阴影
- (void)addShadowLayerWithShadowRadius:(CGFloat)radius
                      shadowLayerBlock:(nullable void (^)(CALayer * shadowLayer))shadowLayerBlock {
    UIView *targetView = self;
    UIBezierPath *path = [UIView createPathWithRoundedRectWithBounds:(CGRect){CGPointZero, self.layerDrawSize}
                                                         cornerRadii:RIAIDCornerRadiiMake(radius, radius, radius, radius)];
    if (!targetView.superview
        || !shadowLayerBlock) {
        NSAssert(targetView.superview, @"shadow must have super view");
        return;
    }
    [self.shadowBackgroundView removeFromSuperview];
    UIView *shadowView = [[UIView alloc] initWithFrame:targetView.frame];
    shadowView.backgroundColor = nil;
    [targetView.superview insertSubview:shadowView belowSubview:targetView];
    self.shadowBackgroundView = shadowView;
    shadowLayerBlock(shadowView.layer);
    shadowView.layer.shadowPath = path.CGPath;
}

// 根据目标尺寸和圆角，创建贝塞尔绘制路径
+ (UIBezierPath *)createCornersPathWithSize:(CGSize)size cornerRadii:(RIAIDCornerRadii)cornerRadii {
    UIBezierPath *path = [UIView createPathWithRoundedRectWithBounds:(CGRect){CGPointZero, size}
                                                         cornerRadii:cornerRadii];
    return path;
}

#pragma mark - private method
// 绘制圆角线条
+ (UIBezierPath *)createPathWithRoundedRectWithBounds:(CGRect)bounds cornerRadii:(RIAIDCornerRadii)cornerRadii {
    CGFloat minX = CGRectGetMinX(bounds);
    CGFloat minY = CGRectGetMinY(bounds);
    CGFloat maxX = CGRectGetMaxX(bounds);
    CGFloat maxY = CGRectGetMaxY(bounds);
    // 左上圆心
    CGFloat topLeftCenterX = minX + cornerRadii.topLeft;
    CGFloat topLeftCenterY = minY + cornerRadii.topLeft;
    // 右上圆心
    CGFloat topRightCenterX = maxX - cornerRadii.topRight;
    CGFloat topRightCenterY = minY + cornerRadii.topRight;
    // 左下圆心
    CGFloat bottomLeftCenterX = minX + cornerRadii.bottomLeft;
    CGFloat bottomLeftCenterY = maxY - cornerRadii.bottomLeft;
    // 右下圆心
    CGFloat bottomRightCenterX = maxX -  cornerRadii.bottomRight;
    CGFloat bottomRightCenterY = maxY - cornerRadii.bottomRight;
    
    UIBezierPath *path = [UIBezierPath bezierPath];
    // 左上
    [path addArcWithCenter:CGPointMake(topLeftCenterX, topLeftCenterY) radius:cornerRadii.topLeft
                startAngle:-M_PI
                  endAngle:-0.5 * M_PI
                 clockwise:YES];
    // 右上
    [path addArcWithCenter:CGPointMake(topRightCenterX, topRightCenterY)
                    radius:cornerRadii.topRight
                startAngle:-0.5 * M_PI
                  endAngle:0
                 clockwise:YES];
    // 右下
    [path addArcWithCenter:CGPointMake(bottomRightCenterX, bottomRightCenterY)
                    radius:cornerRadii.bottomRight
                startAngle:0
                  endAngle:0.5 * M_PI
                 clockwise:YES];
    // 左下
    [path addArcWithCenter:CGPointMake(bottomLeftCenterX, bottomLeftCenterY)
                    radius:cornerRadii.bottomLeft
                startAngle:0.5 * M_PI
                  endAngle:1 * M_PI
                 clockwise:YES];
    [path closePath];
    return path;

}

#pragma mark - setter && getter
static const char *kRIAIDLayerDrawSize = "kRIAIDLayerDrawSize";
- (void)setLayerDrawSize:(CGSize)layerDrawSize {
    objc_setAssociatedObject(self, kRIAIDLayerDrawSize,
                             [NSValue valueWithCGSize:layerDrawSize], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (CGSize)layerDrawSize {
    return ((NSValue *)objc_getAssociatedObject(self, kRIAIDLayerDrawSize)).CGSizeValue;
}

static const char *kRIAIDBorderLayer = "kRIAIDBorderLayer";
- (void)setBorderLayer:(CAShapeLayer * _Nonnull)borderLayer {
    objc_setAssociatedObject(self, kRIAIDBorderLayer,
                             borderLayer, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (CAShapeLayer *)borderLayer {
    return objc_getAssociatedObject(self, kRIAIDBorderLayer);
}

static const char *kRIAIDMaskLayer = "kRIAIDMaskLayer";
- (void)setMaskLayer:(CAShapeLayer * _Nonnull)maskLayer {
    objc_setAssociatedObject(self, kRIAIDMaskLayer,
                             maskLayer, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (CAShapeLayer *)maskLayer {
    return objc_getAssociatedObject(self, kRIAIDMaskLayer);
}

static const char *kRIAIDShadowBackgroundView = "kRIAIDShadowBackgroundView";
- (void)setShadowBackgroundView:(UIView * _Nonnull)shadowBackgroundView {
    objc_setAssociatedObject(self, kRIAIDShadowBackgroundView,
                             shadowBackgroundView, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (UIView *)shadowBackgroundView {
    return objc_getAssociatedObject(self, kRIAIDShadowBackgroundView);
}

@end
