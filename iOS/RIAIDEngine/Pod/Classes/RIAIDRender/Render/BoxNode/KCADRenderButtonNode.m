//
//  KCADRenderButtonNode.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderButtonNode.h"

#pragma mark - view
#import "RIAIDRenderTouchView.h"

#pragma mark - service
#import "RIAIDRConsumeActionService.h"

#pragma mark - parser
#import "KCADRenderParserContainer.h"

#pragma mark - utils
#import "KCADRenderGeometryCalculate.h"
#import "KCADRenderLayout.h"
#import "KCADRenderKBase.h"

@interface KCADRenderButtonNode () <RIAIDTouchViewDelegate>

@property (nonatomic, strong) RIAIDRenderTouchView *contentView;

@end

@implementation KCADRenderButtonNode

- (void)bindData {
    [super bindData];
    self.contentRender = [[KCADRenderParserContainer new] parseAdRenderInfo:self.renderInfo.attributes.button.content
                                                                    context:self.context];
    [self.contentRender bindData];
    if (self.renderInfo.attributes.button.highlightStateListArray.count > 0) {
        [self.contentRender deliverHighlightAttributes:self.renderInfo.attributes.button.highlightStateListArray];
    }
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
    CGSize renderSize = CGSizeZero;
    self.contentRender.size = [self.contentRender measureWithEstimateWidth:maxWidth
                                                            estimateHeight:maxHeight];
    renderSize.width = self.contentRender.size.width > 0 ? self.contentRender.size.width : maxWidth;
    renderSize.height = self.contentRender.size.height > 0 ? self.contentRender.size.height : maxHeight;
    return renderSize;
}

- (BOOL)dispatchEvent:(NSString *)eventType
              keyList:(NSArray<NSNumber *> *)keyList
           attributes:(RIAIDAttributes *)attributes {
    BOOL result = [super dispatchEvent:eventType keyList:keyList attributes:attributes];
    result |= [self.contentRender dispatchEvent:eventType
                                        keyList:keyList
                                     attributes:attributes];
    return result;
    
}

// 摆放子节点
- (void)layout {
    [self.contentRender layout];
    [super layout];
}

- (void)drawSelfWithDecorView:(UIView *)decorView {
    [super drawSelfWithDecorView:decorView];
    [self.contentRender beganRendingWithDecorView:self.contentView];
    for (KCADRenderNode *childNode in self.childList) {
        [childNode beganRendingWithDecorView:self.contentView];
    }
}

- (void)drawForegroundWithDecorView:(UIView *)decorView {
    [super drawForegroundWithDecorView:decorView];
    [self.nodeView setupForeground];
}

- (UIView *)getContentView {
    if (!_contentView) {
        _contentView = [RIAIDRenderTouchView new];
        _contentView.userInteractionEnabled = YES;
        _contentView.actionDelegate = self;
    }
    return _contentView;
}

#pragma mark - action delegate
- (void)onSingleTapAction {
    if (!self.renderInfo.handler.hasClick) {
        return;
    }
    RIAIDResponder *responder = self.renderInfo.handler.click;
    NSObject<RIAIDRConsumeActionService> *consumeActionService = [self.context.serviceContainer
                                                                  getServiceInstance:@protocol(RIAIDRConsumeActionService)];
    if ([consumeActionService respondsToSelector:@selector(consumeRenderAction:responder:)]) {
        [consumeActionService consumeRenderAction:RIAIDConsumeActionTypeClick
                                        responder:responder];
    }
}

- (void)onDoubleTapAction {
    if (!self.renderInfo.handler.hasDoubleClick) {
        return;
    }
    RIAIDResponder *responder = self.renderInfo.handler.doubleClick;
    NSObject<RIAIDRConsumeActionService> *consumeActionService = [self.context.serviceContainer
                                                                  getServiceInstance:@protocol(RIAIDRConsumeActionService)];
    if ([consumeActionService respondsToSelector:@selector(consumeRenderAction:responder:)]) {
        [consumeActionService consumeRenderAction:RIAIDConsumeActionTypeDoubleClick
                                        responder:responder];
    }
}

- (void)onLongPressAction {
    if (!self.renderInfo.handler.hasLongPress) {
        return;
    }
    RIAIDResponder *responder = self.renderInfo.handler.longPress;
    NSObject<RIAIDRConsumeActionService> *consumeActionService = [self.context.serviceContainer
                                                                  getServiceInstance:@protocol(RIAIDRConsumeActionService)];
    if ([consumeActionService respondsToSelector:@selector(consumeRenderAction:responder:)]) {
        [consumeActionService consumeRenderAction:RIAIDConsumeActionTypeLongPress
                                        responder:responder];
    }
}

- (void)onPressStart:(BOOL)bySelf {
    [super onPressStart:NO];
    [self.contentRender onPressStart:NO];
}

- (void)onPressEnd:(BOOL)bySelf {
    [super onPressEnd:NO];
    [self.contentRender onPressEnd:NO];
}


@end
