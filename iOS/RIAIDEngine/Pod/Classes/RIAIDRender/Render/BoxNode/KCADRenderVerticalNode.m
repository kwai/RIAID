//
//  KCADRenderVerticalNode.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderVerticalNode.h"

#pragma mark - utils
#import "KCADRenderGeometryCalculate.h"
#import "KCADRenderKBase.h"

@implementation KCADRenderVerticalNode

// 垂直盒子的尺寸计算
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
    
    //MARK: 子节点计算，反向撑开父节点
    CGFloat childTotalHeight = 0.f,
            childMaxWidth = 0.f,
            childTotalMargin = 0.f;
    NSInteger totalWeight = 0;
    // 这两个值会在 for 循环中使用，在这声明是为了避免 for 循环中创建对象带来的性能损耗
    CGFloat hMargin = 0.f, vMargin = 0.f;
    // 首次遍历子节点，测量无权重节点
    for (KCADRenderNode *childNode in self.childSortList) {
        if (childNode.renderInfo.layout.weight > 0) {
            totalWeight += childNode.renderInfo.layout.weight;
            // 有权重的节点，后续需要根据垂直父节点提供的剩余空间进行垂直填充
            childNode.renderInfo.layout.height = RIAIDRenderMatchParent;
            childTotalMargin += (childNode.renderInfo.layout.margin.top + childNode.renderInfo.layout.margin.bottom);
            continue;
        }
        hMargin = childNode.renderInfo.layout.margin.start + childNode.renderInfo.layout.margin.end;
        vMargin = childNode.renderInfo.layout.margin.top + childNode.renderInfo.layout.margin.bottom;
        childNode.size = [childNode measureWithEstimateWidth:(freeSize.width - hMargin)
                                              estimateHeight:(freeSize.height - vMargin)];
        CGFloat childHeight = childNode.size.height
                              + childNode.renderInfo.layout.margin.top
                              + childNode.renderInfo.layout.margin.bottom;
        freeSize.height -= childHeight;
        childTotalHeight += childHeight;
        childMaxWidth = MAX(childMaxWidth,
                            childNode.size.width
                            + childNode.renderInfo.layout.margin.start
                            + childNode.renderInfo.layout.margin.end);
    }
    // 若有权重，则进行再次遍历，测量有权重的节点
    if (totalWeight > 0) {
        freeSize.height -= childTotalMargin;
        for (KCADRenderNode *childNode in self.childSortList) {
            if (childNode.renderInfo.layout.weight > 0) {
                hMargin = childNode.renderInfo.layout.margin.start + childNode.renderInfo.layout.margin.end;
                childNode.size = [childNode measureWithEstimateWidth:freeSize.width - hMargin
                                                      estimateHeight:freeSize.height * ((CGFloat)childNode.renderInfo.layout.weight / (CGFloat)totalWeight)];
                CGFloat childHeight = childNode.size.height;
                freeSize.height -= childHeight;
                vMargin = childNode.renderInfo.layout.margin.top + childNode.renderInfo.layout.margin.bottom;
                childTotalHeight += (childHeight + vMargin);
                childMaxWidth = MAX(childMaxWidth,
                                    childNode.size.width
                                    + childNode.renderInfo.layout.margin.start
                                    + childNode.renderInfo.layout.margin.end);
                totalWeight -= childNode.renderInfo.layout.weight;
            }
        }
    }
    
    //MARK: 根据子视图反馈的尺寸信息，重新计算尺寸
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
        renderSize.height += childTotalHeight;
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
    CGFloat left = self.renderInfo.layout.padding.start;
    // 从上到下摆放，该值代表子组件在水平父盒子中的居上的偏移量
    CGFloat topBoundary = self.renderInfo.layout.padding.top;
    for (KCADRenderNode *childNode in self.childList) {
        topBoundary += childNode.renderInfo.layout.margin.top;
        CGPoint deltaPoint = CGPointMake(left + childNode.renderInfo.layout.margin.start, topBoundary);
        [self.childDeltaMap setObject:[NSValue valueWithCGPoint:deltaPoint]
                               forKey:childNode];
        topBoundary += (childNode.size.height + childNode.renderInfo.layout.margin.bottom);
        [childNode layout];
    }
    [super layout];
}

@end
