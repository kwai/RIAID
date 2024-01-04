//
//  KCADRenderVScrollNode.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderVScrollNode.h"

#pragma mark - utils
#import "KCADRenderGeometryCalculate.h"
#import "KCADRenderLayout.h"
#import "KCADRenderKBase.h"

@interface KCADRenderVScrollNode ()

@property (nonatomic, assign) CGSize containerSize;
@property (nonatomic, strong) UIScrollView *scrollView;

@end

@implementation KCADRenderVScrollNode

- (void)bindData {
    [super bindData];
    self.scrollView.showsVerticalScrollIndicator = [KCADRenderKBase renderShowScrollBar:self.renderInfo.attributes];
}

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
            childMaxWidth = 0.f;
    // 这个值会在 for 循环中使用，在这声明是为了避免 for 循环中创建对象带来的性能损耗
    CGFloat hMargin = 0.f;
    // 垂直滚动，无权重，垂直方向都能展开
    for (KCADRenderNode *childNode in self.childSortList) {
        hMargin = childNode.renderInfo.layout.margin.start + childNode.renderInfo.layout.margin.end;
        childNode.size = [childNode measureWithEstimateWidth:(freeSize.width - hMargin)
                                              estimateHeight:CGFLOAT_MAX];
        CGFloat childHeight = childNode.size.height
                              + childNode.renderInfo.layout.margin.top
                              + childNode.renderInfo.layout.margin.bottom;
        childTotalHeight += childHeight;
        childMaxWidth = MAX(childMaxWidth,
                            childNode.size.width
                            + childNode.renderInfo.layout.margin.start
                            + childNode.renderInfo.layout.margin.end);
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
    self.containerSize = CGSizeMake(childMaxWidth, childTotalHeight);
    return renderSize;
}

- (CGSize)getContainerSize {
    return self.containerSize;
}

// 摆放子节点
- (void)layout {
    // 定义边界
    CGFloat left = self.renderInfo.layout.padding.start;
    // 从上向下摆放，该值代表子组件在水平父盒子中的左部分的偏移量
    CGFloat topDelta = self.renderInfo.layout.padding.top;
    for (KCADRenderNode *childNode in self.childList) {
        topDelta += childNode.renderInfo.layout.margin.top;
        CGPoint deltaPoint = CGPointMake(left + childNode.renderInfo.layout.margin.start, topDelta);
        [self.childDeltaMap setObject:[NSValue valueWithCGPoint:deltaPoint]
                               forKey:childNode];
        topDelta += childNode.size.height + childNode.renderInfo.layout.margin.bottom;
        [childNode layout];
    }
    [super layout];
}

- (void)drawSelfWithDecorView:(UIView *)decorView {
    [super drawSelfWithDecorView:decorView];
    self.scrollView.contentSize = self.containerSize;
    for (KCADRenderNode *childNode in self.childList) {
        [childNode beganRendingWithDecorView:self.scrollView];
    }
}

- (UIView *)getContentView {
    if (!_scrollView) {
        _scrollView = [UIScrollView new];
    }
    return _scrollView;
}

@end
