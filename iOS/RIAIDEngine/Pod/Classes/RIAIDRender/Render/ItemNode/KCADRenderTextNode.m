//
//  KCADRenderTextNode.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderTextNode.h"

#pragma mark - service
#import "RIAIDRConsumeActionService.h"
#import "RIAIDRDataBindingService.h"

#pragma mark - widget
#import "UIView+DrawBackgroundAttributes.h"
#import "KCADRenderKBase.h"
#import "RIAIDRenderTouchView.h"

#pragma mark - utils
#import "RIAIDRHighlightAttributesGenerator.h"
#import "KCADRenderLayout.h"

@interface KCADRenderTextNode ()<KCADRenderRichLabelDelegate, RIAIDTouchViewDelegate, RIAIDRRichLabelActionDelegate>

@property (nonatomic, strong, readwrite) KCADRenderRichTextWidget *richTextWidget;
@property (nonatomic, strong) RIAIDRenderTouchView *textDecorView;

@end

@implementation KCADRenderTextNode

- (void)bindData {
    [super bindData];
}

- (CGSize)measureWithEstimateWidth:(CGFloat)estimateWidth estimateHeight:(CGFloat)estimateHeight {
    [super measureWithEstimateWidth:estimateWidth estimateHeight:estimateHeight];
    if (nil == self.renderInfo) {
        NSAssert(NO, @"节点对应的数据不应该为空");
        return CGSizeZero;
    }
    // render 的最终尺寸
    CGSize renderSize = CGSizeZero;
    // padding 计算，对于 render 来说，padding 也属于其尺寸，padding 的初始位置差为 view 的真实尺寸
    renderSize.width = self.renderInfo.layout.padding.start + self.renderInfo.layout.padding.end;
    renderSize.height = self.renderInfo.layout.padding.top + self.renderInfo.layout.padding.bottom;
    CGFloat textEstimateWidth = estimateWidth - self.renderInfo.layout.padding.start - self.renderInfo.layout.padding.end;
    CGFloat textEstimateHeight = estimateHeight - self.renderInfo.layout.padding.top - self.renderInfo.layout.padding.bottom;
    id<RIAIDRDataBindingService> dataBinding = [self.context.serviceContainer
                                                getServiceInstance:@protocol(RIAIDRDataBindingService)];
    // layoutIfNeed 时，会重复调用计算方法，这里先移除下，避免出现重复添加
    [self.richTextWidget.richLabel removeFromSuperview];
    // 富文本的创建现在是整个计算过程中最耗时的操作
    self.richTextWidget = [[KCADRenderRichTextWidget alloc] initWithString:[dataBinding parseHolderData:self.renderInfo.attributes.text.text]
                                                              estimateSize:CGSizeMake(textEstimateWidth, textEstimateHeight)
                                                          renderLayoutSize:CGSizeMake([KCADRenderKBase renderWidth:self.renderInfo],
                                                                                      [KCADRenderKBase renderHeight:self.renderInfo])
                                                                     model:self.renderInfo.attributes.text
                                                                  delegate:self
                                                                   context:self.context];
    self.richTextWidget.actionDelegate = self;
    renderSize.width += self.richTextWidget.size.width;
    renderSize.height += self.richTextWidget.size.height;
    return renderSize;
}

- (void)drawSelfWithDecorView:(UIView *)decorView {
    // 先将富文本塞到包裹视图中，再进行 draw
    self.richTextWidget.richLabel.userInteractionEnabled = self.textDecorView.userInteractionEnabled;
    [KCADRenderLayout parentView:self.textDecorView addSubView:self.richTextWidget.richLabel];
    [super drawSelfWithDecorView:decorView];
    self.richTextWidget.richLabel.frame = (CGRect){CGPointMake(self.renderInfo.layout.padding.start,
                                                               self.renderInfo.layout.padding.top),
                                                   self.richTextWidget.size};
}

- (BOOL)dispatchEvent:(NSString *)eventType
              keyList:(NSArray<NSNumber *> *)keyList
           attributes:(RIAIDAttributes *)attributes {
    if ([super dispatchEvent:eventType keyList:keyList attributes:attributes]) {
        if (attributes.hasText) {
            self.renderInfo.attributes = [RIAIDRHighlightAttributesGenerator getNewAttributesWithOrigin:self.renderInfo.attributes
                                                                                         diffAttributes:attributes];
            [self refreshUIWithAttributes:self.renderInfo.attributes];
            return YES;
        }
    }
    return NO;
}

- (UIView *)getContentView {
    if (!_textDecorView) {
        _textDecorView = [RIAIDRenderTouchView new];
        _textDecorView.actionDelegate = self;
    }
    return _textDecorView;
}

- (void)refreshUIWithAttributes:(RIAIDAttributes *)attributes {
    if (!attributes) {
        return;
    }
    [self.textDecorView drawWithCommonAttributes:attributes.common
                                            size:self.size
                                         context:self.context];
    id<RIAIDRDataBindingService> dataBinding = [self.context.serviceContainer
                                                getServiceInstance:@protocol(RIAIDRDataBindingService)];
    [self.richTextWidget reloadWithWithText:attributes
                               estimateSize:self.size
                           renderLayoutSize:CGSizeMake([KCADRenderKBase renderWidth:self.renderInfo],
                                                       [KCADRenderKBase renderHeight:self.renderInfo])];
    // 重新对富文本进行此尺寸赋值
    // 解决 YYLabel 自适应布局下，重新赋值后渲染结果不对的问题
    [self layout];
}

#pragma mark - KCADRenderRichLabelDelegate
- (UIView *)renderViewWithInfo:(id)renderInfo estimateSize:(CGSize)estimateSize {
    UIView *renderView;
    if ([renderInfo isKindOfClass:[RIAIDNode class]]) {
        if ([self.delegate respondsToSelector:@selector(generateNodeWithRenderInfo:context:)]) {
            KCADRenderNode *richNode = [self.delegate generateNodeWithRenderInfo:(RIAIDNode *)renderInfo
                                                                         context:self.context];
            renderView = [RIAIDRenderTouchView new];
            [richNode bindData];
            CGFloat width = richNode.renderInfo.layout.width > 0 ?
                            MIN(richNode.renderInfo.layout.width, estimateSize.width) :
                            estimateSize.width;
            CGFloat height = richNode.renderInfo.layout.height > 0 ?
                             MIN(richNode.renderInfo.layout.height, estimateSize.height) :
                             estimateSize.height;
            richNode.size = [richNode measureWithEstimateWidth:width estimateHeight:height];
            [richNode layout];
            [richNode beganRendingWithDecorView:renderView];
            renderView.frame = (CGRect){CGPointZero, richNode.size};
        }
    }
    return renderView;
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
    if (self.renderInfo.attributes.button.highlightStateListArray.count == 0
        && bySelf
        && self.renderInfo.attributes.text.highlightColor.length == 0) {
        return;
    }
    if (bySelf) {
        [self.richTextWidget reloadTextColor:[KCADRenderKBase
                                              renderTextColorString:self.renderInfo.attributes.text.highlightColor]];
    } else {
        [self refreshUIWithAttributes:self.highlightAttributes];
    }
}

- (void)onPressEnd:(BOOL)bySelf {
    if (self.renderInfo.attributes.button.highlightStateListArray.count == 0
        && bySelf
        && self.renderInfo.attributes.text.highlightColor.length == 0) {
        return;
    }
    [self refreshUIWithAttributes:self.renderInfo.attributes];
}

- (BOOL)haveHandlerWithRichText:(NSAttributedString *)richText textRange:(NSRange)range {
    return !![self.richTextWidget getHandlerWithText:richText range:range];
}

#pragma mark - RIAIDRRichLabelActionDelegate
- (void)tapAction:(RIAIDHandler *)handler {
    RIAIDResponder *responder = handler.click;
    NSObject<RIAIDRConsumeActionService> *consumeActionService = [self.context.serviceContainer
                                                                  getServiceInstance:@protocol(RIAIDRConsumeActionService)];
    if ([consumeActionService respondsToSelector:@selector(consumeRenderAction:responder:)]) {
        [consumeActionService consumeRenderAction:RIAIDConsumeActionTypeClick
                                        responder:responder];
    }
}

- (void)longPressAction:(RIAIDHandler *)handler {
    RIAIDResponder *responder = handler.longPress;
    NSObject<RIAIDRConsumeActionService> *consumeActionService = [self.context.serviceContainer
                                                                  getServiceInstance:@protocol(RIAIDRConsumeActionService)];
    if ([consumeActionService respondsToSelector:@selector(consumeRenderAction:responder:)]) {
        [consumeActionService consumeRenderAction:RIAIDConsumeActionTypeClick
                                        responder:responder];
    }
}

@end
