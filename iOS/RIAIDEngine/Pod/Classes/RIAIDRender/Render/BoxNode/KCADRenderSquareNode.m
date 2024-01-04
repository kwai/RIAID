//
//  KCADRenderSquareNode.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderSquareNode.h"

#pragma mark - parser
#import "KCADRenderParserContainer.h"

#pragma mark - utils
#import "KCADRenderGeometryCalculate.h"
#import "KCADRenderKBase.h"

@implementation KCADRenderSquareNode

- (CGSize)measureWithEstimateWidth:(CGFloat)estimateWidth estimateHeight:(CGFloat)estimateHeight {
    [super measureWithEstimateWidth:estimateWidth estimateHeight:estimateHeight];
    if (self.childList.count > 1) {
        NSAssert(NO, @"只允许有一个子节点");
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
    CGFloat squareSide = MAX(MIN(maxWidth, maxHeight), 0);
    CGSize renderSize = CGSizeMake(squareSide, squareSide);
    // 去除 padding 后的剩余空间
    CGSize freeSize = CGSizeZero;
    freeSize.width = squareSide - (self.renderInfo.layout.padding.start + self.renderInfo.layout.padding.end);
    freeSize.height = squareSide - (self.renderInfo.layout.padding.top + self.renderInfo.layout.padding.bottom);
    // 由于只允许一个子节点，故只进行一次的循环计算
    for (KCADRenderNode *childNode in self.childSortList) {
        CGFloat hMargin = 0.f, vMargin = 0.f;
        hMargin = childNode.renderInfo.layout.margin.start + childNode.renderInfo.layout.margin.end;
        vMargin = childNode.renderInfo.layout.margin.top + childNode.renderInfo.layout.margin.bottom;
        childNode.renderInfo.layout.width = RIAIDRenderMatchParent;
        childNode.renderInfo.layout.height = RIAIDRenderMatchParent;
        childNode.size = [childNode measureWithEstimateWidth:freeSize.width - hMargin
                                              estimateHeight:freeSize.height - vMargin];
        break;
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
