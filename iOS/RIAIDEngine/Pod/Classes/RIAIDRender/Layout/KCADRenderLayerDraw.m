//
//  KCADRenderLayerDraw.m
//  KCADRender
//
//  Created by simon on 2021/11/30.
//

#import "KCADRenderLayerDraw.h"

#pragma mark - utils
#import "KCADRenderKBase.h"
#import "UIColor+KCADRenderHex.h"
#import "UIView+RIAIDCornerShadow.h"
#import "RIAIDRDataBindingService.h"

@implementation KCADRenderLayerDraw

// 渐变色
+ (CAGradientLayer *)drawGradientLayer:(RIAIDGradient *)gradient frame:(CGRect)frame context:(RIAIDRenderContext *)context {
    CAGradientLayer *gradientLayer = [CAGradientLayer new];
    gradientLayer.frame = frame;
    // 设置渐变色的类型
    switch (gradient.type) {
        case RIAIDGradient_GradientType_GradientTypeLinear:
        default: {
            gradientLayer.type = kCAGradientLayerAxial;
        } break;
    }
    // 设置渐变色开始及结束坐标
    NSValue *startPointValue = [NSValue valueWithCGPoint:CGPointZero];
    NSValue *endPointValue = [NSValue valueWithCGPoint:CGPointZero];
    [KCADRenderKBase getStartPoint:&startPointValue
                          endPoint:&endPointValue
                           byAngle:[KCADRenderKBase renderGradientAngle:gradient]];
    gradientLayer.startPoint = startPointValue.CGPointValue;
    gradientLayer.endPoint = endPointValue.CGPointValue;
    // 设置渐变色的颜色
    NSMutableArray *colorsArray = [NSMutableArray array];
    id<RIAIDRDataBindingService> dataBinding = [context.serviceContainer
                                                getServiceInstance:@protocol(RIAIDRDataBindingService)];
    for (NSString *colorString in gradient.colorsArray) {
        [colorsArray addObject:(id)[UIColor riaid_colorWithHexString:[dataBinding parseHolderData:[KCADRenderKBase renderColorString:colorString]]].CGColor];
    }
    if (colorsArray.count > 0) {
        gradientLayer.colors = colorsArray.copy;
    }
    return gradientLayer;
}

@end
