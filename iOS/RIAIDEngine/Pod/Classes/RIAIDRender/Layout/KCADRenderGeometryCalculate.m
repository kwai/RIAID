//
//  KCADRenderLayoutMeasure.m
//  KCADRender
//
//  Created by simon on 2021/11/16.
//

#import "KCADRenderGeometryCalculate.h"

#import "KCADRenderNode.h"
#import "KCADRenderKBase.h"

@implementation KCADRenderGeometryCalculate

+ (CGFloat)getSizeValueByMode:(CGFloat)mode
                  measureVale:(CGFloat)measureVale
                     maxValue:(RIAIDFloatValue *)maxValue
                estimateValue:(CGFloat)estimateValue {
    CGFloat resultValue = 0.f;
    CGFloat max = estimateValue;
    if (maxValue != nil
        && maxValue.value > 0) {
        max = MIN(maxValue.value, estimateValue);
    }
    if (mode == RIAIDRenderMatchParent) {
        resultValue = max;
    } else if (mode == RIAIDRenderWrapContent) {
        resultValue = MIN(measureVale, max);
    } else if (mode >= 0) {
        resultValue = MIN(mode, max);
    }
    return MAX(resultValue, 0.f);
}

+ (CGRect)transformPosition:(KCADRenderNode *)render size:(CGSize)size {
    KCADRenderNode *parentNode;
    CGPoint deltaPoint = CGPointZero;
    while (render.parentNode) {
        parentNode = render.parentNode;
        CGPoint childDeltaPoint = [parentNode getChileNodeDeltaPoint:render];
        deltaPoint.x += childDeltaPoint.x;
        deltaPoint.y += childDeltaPoint.y;
        // 顶层节点，不需要将其子节点继续拍平到上层节点上
        if (parentNode.isRoot) {
            break;
        }
        render = parentNode;
    }
    CGRect absoluteRect = CGRectZero;
    absoluteRect.origin = deltaPoint;
    absoluteRect.size = size;
    
    return absoluteRect;
}

@end
