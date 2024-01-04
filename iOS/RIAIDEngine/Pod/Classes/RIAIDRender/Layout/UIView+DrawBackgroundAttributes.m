//
//  UIView+DrawBackgroundAttributes.m
//  KCADRender
//
//  Created by simon on 2021/12/12.
//

#import "UIView+DrawBackgroundAttributes.h"

#pragma mark - utils
#import "KCADRenderKBase.h"
#import "UIColor+KCADRenderHex.h"
#import "KCADRenderLayerDraw.h"
#import <objc/runtime.h>
#import "RIAIDRDataBindingService.h"

@implementation UIView (DrawBackgroundAttributes)

+ (UIBezierPath *)getNewPathWithCommonAttributes:(RIAIDCommonAttributes *)commonAttributes size:(CGSize)size {
    // 添加背景圆角
    RIAIDCornerRadii cornerRadii = RIAIDCornerRadiiMake(0, 0, 0, 0);
    if ([commonAttributes hasCornerRadius]) {
        RIAIDCornerRadius *cornerRadius = commonAttributes.cornerRadius;
        CGFloat maxRadius = MAX(MIN(size.width/2.f, size.height/2.f), 0);
        cornerRadii = RIAIDCornerRadiiMake(cornerRadius.topStart > maxRadius ? maxRadius : cornerRadius.topStart,
                                           cornerRadius.topEnd > maxRadius ? maxRadius : cornerRadius.topEnd,
                                           cornerRadius.bottomStart > maxRadius ? maxRadius : cornerRadius.bottomStart,
                                           cornerRadius.bottomEnd > maxRadius ? maxRadius : cornerRadius.bottomEnd);
        return [UIView createCornersPathWithSize:size cornerRadii:cornerRadii];
    }
    // 添加边框线
    if ([commonAttributes hasStroke]) {
        return [UIView createCornersPathWithSize:size cornerRadii:cornerRadii];
    }
    return nil;
}

+ (UIBezierPath *)getNewShadowPathWithCommonAttributes:(RIAIDShadow *)shadow size:(CGSize)size {
    if (!shadow) {
        return nil;
    }
    RIAIDCornerRadii cornerRadii = RIAIDCornerRadiiMake(shadow.radius, shadow.radius, shadow.radius, shadow.radius);
    return [UIView createCornersPathWithSize:size cornerRadii:cornerRadii];
}

// 绘画背景
- (void)drawWithCommonAttributes:(RIAIDCommonAttributes *)commonAttributes
                            size:(CGSize)size
                         context:(RIAIDRenderContext *)context {
    // 添加阴影的尺寸
    self.layerDrawSize = size;
    [self _drawBackgroundWithCommonAttributes:commonAttributes size:size context:context];
}

- (void)drawShadow:(RIAIDShadow *)shadow context:(RIAIDRenderContext *)context {
    id<RIAIDRDataBindingService> dataBinding = [context.serviceContainer
                                                getServiceInstance:@protocol(RIAIDRDataBindingService)];
    [self addShadowLayerWithShadowRadius:shadow.radius shadowLayerBlock:^(CALayer * _Nonnull shadowLayer) {
        shadowLayer.shadowColor = [UIColor riaid_colorWithHexString:[dataBinding parseHolderData:[KCADRenderKBase renderColorString:shadow.color]]].CGColor;
        shadowLayer.shadowOpacity = [UIColor riaid_getAlphaValueWithHexString:[dataBinding parseHolderData:[KCADRenderKBase renderColorString:shadow.color]]];
        shadowLayer.shadowRadius = shadow.radius;
        shadowLayer.shadowOffset = CGSizeMake(shadow.offsetX, shadow.offsetY);
    }];
}

#pragma mark - private method
- (void)_drawBackgroundWithCommonAttributes:(RIAIDCommonAttributes *)commonAttributes
                                       size:(CGSize)size
                                    context:(RIAIDRenderContext *)context {
    id<RIAIDRDataBindingService> dataBinding = [context.serviceContainer
                                                getServiceInstance:@protocol(RIAIDRDataBindingService)];
    // 添加背景色
    if (commonAttributes.backgroundColor.length > 0) {
        self.backgroundColor = [UIColor riaid_colorWithHexString:[dataBinding parseHolderData:commonAttributes.backgroundColor]];
    } else {
        self.backgroundColor = [UIColor riaid_colorWithHexString:[KCADRenderKBase renderColorString:nil]];
    }
    // 添加渐变图层
    [self.gradientLayer removeFromSuperlayer];
    if ([commonAttributes hasGradient]) {
        self.gradientLayer = [KCADRenderLayerDraw drawGradientLayer:commonAttributes.gradient
                                                              frame:(CGRect){CGPointZero, size}
                                                            context:context];
        [self.layer addSublayer:self.gradientLayer];
    }
    // 添加背景圆角
    RIAIDCornerRadii cornerRadii = RIAIDCornerRadiiMake(0, 0, 0, 0);
    if ([commonAttributes hasCornerRadius]) {
        RIAIDCornerRadius *cornerRadius = commonAttributes.cornerRadius;
        CGFloat maxRadius = MAX(MIN(size.width/2.f, size.height/2.f), 0);
        cornerRadii = RIAIDCornerRadiiMake(cornerRadius.topStart > maxRadius ? maxRadius : cornerRadius.topStart,
                                           cornerRadius.topEnd > maxRadius ? maxRadius : cornerRadius.topEnd,
                                           cornerRadius.bottomStart > maxRadius ? maxRadius : cornerRadius.bottomStart,
                                           cornerRadius.bottomEnd > maxRadius ? maxRadius : cornerRadius.bottomEnd);
    }
    [self addCornersWithCornerRadii:cornerRadii];
    // 添加边框线
    if ([commonAttributes hasStroke]) {
        RIAIDStroke *stroke = commonAttributes.stroke;
        if (stroke.dashWidth > 0) {
            // 虚线
            [self addDashBorderColor:[UIColor riaid_colorWithHexString:[dataBinding parseHolderData:[KCADRenderKBase renderColorString:stroke.color]]]
                         borderWidth:stroke.width
                             dashGap:stroke.dashGap
                           dashWidth:stroke.dashWidth
                         cornerRadii:cornerRadii];
        } else {
            // 实线
            [self addBorderColor:[UIColor riaid_colorWithHexString:[dataBinding parseHolderData:[KCADRenderKBase renderColorString:stroke.color]]]
                     borderWidth:stroke.width
                     cornerRadii:cornerRadii];
        }
    } else {
        // 实线
        [self addBorderColor:nil
                 borderWidth:0
                 cornerRadii:cornerRadii];
    }
    // 透明度
    if ([commonAttributes hasAlpha]) {
        self.alpha = commonAttributes.alpha.value;
    } else {
        self.alpha = 1.0f;
    }
}

#pragma mark - setter && getter
static const char *kRIAIDGradientLayer = "kRIAIDGradientLayer";
- (void)setGradientLayer:(CAGradientLayer * _Nonnull)gradientLayer {
    objc_setAssociatedObject(self, kRIAIDGradientLayer,
                             gradientLayer, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (CAGradientLayer *)gradientLayer {
    return objc_getAssociatedObject(self, kRIAIDGradientLayer);
}


@end
