//
//  KCADRenderImageNode.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderImageNode.h"

#pragma mark - view
#import "RIAIDRenderTouchImageView.h"

#pragma mark - service
#import "RIAIDRConsumeActionService.h"
#import "RIAIDRImageLoaderService.h"
#import "RIAIDRDataBindingService.h"

#pragma mark - utils
#import "KCADRenderGeometryCalculate.h"
#import "UIView+DrawBackgroundAttributes.h"
#import "KCADRenderKBase.h"
#import "RIAIDRHighlightAttributesGenerator.h"

@interface KCADRenderImageNode ()<RIAIDTouchViewDelegate>

@property (nonatomic, strong) RIAIDRenderTouchImageView *imageView;

@end

@implementation KCADRenderImageNode

- (void)bindData {
    [super bindData];
    [self loadImageUrlString:self.renderInfo.attributes.image.URL];
    self.imageView.contentMode = [self getContentModeWithScaleType:self.renderInfo.attributes.image.scaleType];
}

- (CGSize)measureWithEstimateWidth:(CGFloat)estimateWidth estimateHeight:(CGFloat)estimateHeight {
    [super measureWithEstimateWidth:estimateWidth estimateHeight:estimateHeight];
    if (nil == self.renderInfo) {
        NSAssert(NO, @"节点对应的数据不应该为空");
        return CGSizeZero;
    }
    CGSize freeSize = CGSizeMake(estimateWidth, estimateHeight);
    freeSize.width -= (self.renderInfo.layout.padding.start + self.renderInfo.layout.padding.end);
    freeSize.height -= (self.renderInfo.layout.padding.top + self.renderInfo.layout.padding.bottom);
    
    CGSize renderSize = CGSizeZero;
    renderSize.width = [KCADRenderGeometryCalculate getSizeValueByMode:[KCADRenderKBase renderWidth:self.renderInfo]
                                                           measureVale:self.renderInfo.layout.width
                                                              maxValue:self.renderInfo.layout.maxWidth
                                                         estimateValue:freeSize.width];
    renderSize.height = [KCADRenderGeometryCalculate getSizeValueByMode:[KCADRenderKBase renderHeight:self.renderInfo]
                                                            measureVale:self.renderInfo.layout.height
                                                               maxValue:self.renderInfo.layout.maxHeight
                                                          estimateValue:freeSize.height];
    return renderSize;
}

- (BOOL)dispatchEvent:(NSString *)eventType
              keyList:(NSArray<NSNumber *> *)keyList
           attributes:(RIAIDAttributes *)attributes {
    if ([super dispatchEvent:eventType keyList:keyList attributes:attributes]) {
        if (attributes.hasImage) {
            self.renderInfo.attributes = [RIAIDRHighlightAttributesGenerator getNewAttributesWithOrigin:self.renderInfo.attributes
                                                                                         diffAttributes:attributes];
            [self refreshUIWithAttributes:self.renderInfo.attributes];
            return YES;
        }
    }
    return NO;
}

- (UIView *)getContentView {
    if (!_imageView) {
        _imageView = [RIAIDRenderTouchImageView new];
        _imageView.actionDelegate = self;
    }
    return _imageView;
}

- (void)refreshUIWithAttributes:(RIAIDAttributes *)attributes {
    if (attributes.hasImage) {
        [self loadImageUrlString:attributes.image.URL];
    }
    self.imageView.contentMode = [self getContentModeWithScaleType:self.renderInfo.attributes.image.scaleType];
    if (attributes.hasCommon) {
        [self.imageView drawWithCommonAttributes:attributes.common size:self.size context:self.context];        
    }
}

- (void)loadImageUrlString:(NSString *)urlString {
    if (urlString.length == 0) {
        return;
    }
    id<RIAIDRImageLoaderService> imageLoader = [self.context.serviceContainer
                                                getServiceInstance:@protocol(RIAIDRImageLoaderService)];
    id<RIAIDRDataBindingService> dataBinding = [self.context.serviceContainer
                                                getServiceInstance:@protocol(RIAIDRDataBindingService)];
    [imageLoader loadImageUrlString:[dataBinding parseHolderData:urlString]
                          imageView:self.imageView];
}

- (UIViewContentMode)getContentModeWithScaleType:(RIAIDImageAttributes_ScaleType)scaleType {
    UIViewContentMode contentMode;
    switch ([KCADRenderKBase renderImageScaleType:scaleType]) {
        case RIAIDImageAttributes_ScaleType_ScaleTypeFitXy: {
            contentMode = UIViewContentModeScaleToFill;
        } break;
        case RIAIDImageAttributes_ScaleType_ScaleTypeFitEnd: {
            contentMode = UIViewContentModeBottomRight;
        } break;
        case RIAIDImageAttributes_ScaleType_ScaleTypeFitStart: {
            contentMode = UIViewContentModeTopLeft;
        } break;
        case RIAIDImageAttributes_ScaleType_ScaleTypeFitCenter: {
            contentMode = UIViewContentModeScaleAspectFit;
        } break;
        case RIAIDImageAttributes_ScaleType_ScaleTypeCenter: {
            contentMode = UIViewContentModeCenter;
        } break;
        case RIAIDImageAttributes_ScaleType_ScaleTypeCenterCrop: {
            contentMode = UIViewContentModeScaleAspectFill;
        } break;
        default: {
            contentMode = UIViewContentModeScaleToFill;
        } break;
    }
    return contentMode;
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
        && self.renderInfo.attributes.image.highlightURL.length == 0) {
        return;
    }
    if (bySelf) {
        [self loadImageUrlString:self.renderInfo.attributes.image.highlightURL];
    } else {
        [self refreshUIWithAttributes:self.highlightAttributes];
    }
}

- (void)onPressEnd:(BOOL)bySelf {
    if (self.renderInfo.attributes.button.highlightStateListArray.count == 0
        && bySelf
        && self.renderInfo.attributes.image.highlightURL.length == 0) {
        return;
    }
    [self refreshUIWithAttributes:self.renderInfo.attributes];
}

@end
