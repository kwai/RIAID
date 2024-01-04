//
//  KCADRenderAbsoluteNode.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderAbsoluteNode.h"

#pragma mark - utils
#import "KCADRenderGeometryCalculate.h"
#import "KCADRenderKBase.h"

@implementation KCADRenderAbsoluteNode

- (CGSize)measureWithEstimateWidth:(CGFloat)estimateWidth estimateHeight:(CGFloat)estimateHeight {
    [super measureWithEstimateWidth:estimateWidth estimateHeight:estimateHeight];
    if (nil == self.renderInfo) {
        NSAssert(NO, @"节点对应的数据不应该为空");
        return CGSizeZero;
    }
    // 规定宽高，则需要在规定的宽高范围内计算，若无指定宽高，则可以根据子 View 尺寸进行自适应
    BOOL isWidthFixed = [KCADRenderKBase renderWidth:self.renderInfo] >= 0;
    BOOL isHeightFixed = [KCADRenderKBase renderHeight:self.renderInfo] >= 0;
    CGFloat maxWidth = self.renderInfo.layout.hasMaxWidth ? self.renderInfo.layout.maxWidth.value : estimateWidth;
    CGFloat maxHeight = self.renderInfo.layout.hasMaxHeight ? self.renderInfo.layout.maxHeight.value : estimateHeight;
    if (isWidthFixed) {
        // 标明的是使用区域的约束，谁小就用谁
        maxWidth = MAX(MIN(self.renderInfo.layout.width, maxWidth), 0.f);
    }
    if (isHeightFixed) {
        maxHeight = MAX(MIN(self.renderInfo.layout.height, maxHeight), 0.f);
    }
    // render 的最终尺寸
    CGSize renderSize = CGSizeZero;
    // padding 计算，对于 render 来说，padding 也属于其尺寸，padding 的初始位置差为 view 的真实尺寸
    renderSize.width = self.renderInfo.layout.padding.start + self.renderInfo.layout.padding.end;
    renderSize.height = self.renderInfo.layout.padding.top + self.renderInfo.layout.padding.bottom;
    // 去除真实尺寸的剩余空间
    CGSize freeSize = CGSizeZero;
    freeSize.width = maxWidth - (self.renderInfo.layout.padding.start + self.renderInfo.layout.padding.end);
    freeSize.height = maxHeight - (self.renderInfo.layout.padding.top + self.renderInfo.layout.padding.bottom);
    // 计算子视图的尺寸，绝对布局不需要考虑 weight
    CGFloat hMargin = 0.f,
            vMargin = 0.f,
            childMaxWidth = 0.f,
            childMaxHeight = 0.f;
    for (KCADRenderNode *childNode in self.childSortList) {
        hMargin = childNode.renderInfo.layout.margin.start + childNode.renderInfo.layout.margin.end;
        vMargin = childNode.renderInfo.layout.margin.top + childNode.renderInfo.layout.margin.bottom;
        childNode.size = [childNode measureWithEstimateWidth:freeSize.width - hMargin
                                              estimateHeight:freeSize.height - vMargin];
        childMaxWidth = MAX(childMaxWidth,
                            childNode.size.width + hMargin);
        childMaxHeight = MAX(childMaxHeight,
                             childNode.size.height + vMargin);
    }
    // 计算最终尺寸
    if (isWidthFixed) {
        renderSize.width = maxWidth;
    } else {
        renderSize.width += childMaxWidth;
        renderSize.width = [KCADRenderGeometryCalculate getSizeValueByMode:[KCADRenderKBase renderWidth:self.renderInfo]
                                                               measureVale:renderSize.width
                                                                  maxValue:self.renderInfo.layout.maxWidth
                                                             estimateValue:estimateWidth];
    }
    if (isHeightFixed) {
        renderSize.height = maxHeight;
    } else {
        renderSize.height += childMaxHeight;
        renderSize.height = [KCADRenderGeometryCalculate getSizeValueByMode:[KCADRenderKBase renderHeight:self.renderInfo]
                                                                measureVale:renderSize.height
                                                                   maxValue:self.renderInfo.layout.maxHeight
                                                              estimateValue:estimateHeight];
    }
    return renderSize;
}

// 摆放子节点
- (void)layout {
    // 定义边界
    CGFloat left = self.renderInfo.layout.padding.start,
            top = self.renderInfo.layout.padding.top;
    for (KCADRenderNode *childNode in self.childList) {
        CGPoint deltaPoint = CGPointMake(left + childNode.renderInfo.layout.margin.start,
                                         top + childNode.renderInfo.layout.margin.top);
        [self.childDeltaMap setObject:[NSValue valueWithCGPoint:deltaPoint]
                               forKey:childNode];
        [childNode layout];
    }
    [super layout];
}

@end
