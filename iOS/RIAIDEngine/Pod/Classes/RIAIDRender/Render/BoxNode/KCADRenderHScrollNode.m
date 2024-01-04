//
//  KCADRenderHScrollNode.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderHScrollNode.h"

#pragma mark - utils
#import "KCADRenderGeometryCalculate.h"
#import "KCADRenderLayout.h"
#import "KCADRenderKBase.h"

@interface KCADRenderHScrollNode ()

/// 内容尺寸
@property (nonatomic, assign) CGSize containerSize;
@property (nonatomic, strong) UIScrollView *scrollView;

@end

@implementation KCADRenderHScrollNode

- (void)bindData {
    [super bindData];
    self.scrollView.showsHorizontalScrollIndicator = [KCADRenderKBase renderShowScrollBar:self.renderInfo.attributes];
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
    CGFloat childTotalWidth = 0.f,
            childMaxHeight = 0.f;
    // 这个值会在 for 循环中使用，在这声明是为了避免 for 循环中创建对象带来的性能损耗
    CGFloat vMargin = 0.f;
    // 滚动视图不需要权重，水平方向全都能展开，垂直方向需要有最高的宽度
    for (KCADRenderNode *childNode in self.childSortList) {
        vMargin = childNode.renderInfo.layout.margin.top + childNode.renderInfo.layout.margin.bottom;
        childNode.size = [childNode measureWithEstimateWidth:CGFLOAT_MAX
                                              estimateHeight:(freeSize.height - vMargin)];
        CGFloat childWidth = childNode.size.width
                             + childNode.renderInfo.layout.margin.start
                             + childNode.renderInfo.layout.margin.end;
        childTotalWidth += childWidth;
        childMaxHeight = MAX(childMaxHeight,
                             childNode.size.height
                             + childNode.renderInfo.layout.margin.top
                             + childNode.renderInfo.layout.margin.bottom);
    }
    
    // 计算最终尺寸
    if (isWidthFixed) {
        renderSize.width = maxWidth;
    } else {
        renderSize.width += childTotalWidth;
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
    self.containerSize = CGSizeMake(childTotalWidth, childMaxHeight);
    return renderSize;
}

- (CGSize)getContainerSize {
    return self.containerSize;
}

// 摆放子节点
- (void)layout {
    CGFloat top = self.renderInfo.layout.padding.top;
    // 从左向右摆放，该值代表子组件在水平父盒子中的左部分的偏移量
    CGFloat leftDelta = self.renderInfo.layout.padding.start;
    for (KCADRenderNode *childNode in self.childList) {
        leftDelta += childNode.renderInfo.layout.margin.start;
        CGPoint deltaPoint = CGPointMake(leftDelta, top + childNode.renderInfo.layout.margin.top);
        [self.childDeltaMap setObject:[NSValue valueWithCGPoint:deltaPoint]
                               forKey:childNode];
        leftDelta += (childNode.size.width + childNode.renderInfo.layout.margin.end);
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
