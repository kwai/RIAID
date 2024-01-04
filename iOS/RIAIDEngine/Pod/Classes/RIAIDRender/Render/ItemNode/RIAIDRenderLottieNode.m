//
//  RIAIDRenderLottieNode.m
//  KCADRender
//
//  Created by simon on 2021/12/20.
//

#import "RIAIDRenderLottieNode.h"

#pragma mark - service
#import "RIAIDRServiceContainer.h"
#import "RIAIDRDataBindingService.h"

#pragma mark - views
#import "RIAIDRenderTouchView.h"

#pragma mark - utils
#import "KCADRenderGeometryCalculate.h"
#import "KCADRenderKBase.h"
#import "RIAIDLottieZipDownloader.h"
#import "UIColor+KCADRenderHex.h"
#import "RIAIDRHighlightAttributesGenerator.h"
#import "RIAIDLottieInterface.h"
#import "KCADRenderLayout.h"

@interface RIAIDRenderLottieNode ()

@property (nonatomic, strong) id<RIAIDLottieViewInterface> lottieView;

/// 占位 view，用于处理 zip 类型的资源
@property (nonatomic, strong) RIAIDRenderTouchView *placeholderView;

@end

@implementation RIAIDRenderLottieNode

- (void)bindData {
    [super bindData];
    [self refreshUI];
}

- (void)refreshUI {
    id<RIAIDRDataBindingService> dataBinding = [self.context.serviceContainer getServiceInstance:@protocol(RIAIDRDataBindingService)];
    NSString *lottieUrlString = [dataBinding parseHolderData:self.renderInfo.attributes.lottie.URL];
    BOOL isLocalResource = [[NSBundle mainBundle] pathForResource:lottieUrlString ofType:@"json"].length > 0;
    if (isLocalResource) {
        id<RIAIDLottieViewProviderInterface> provider = [self.context.serviceContainer getServiceInstance:@protocol(RIAIDLottieViewProviderInterface)];
        self.lottieView = [provider viewWithLottieName:lottieUrlString];
        [self setupLottie];
    } else {
        __weak typeof(self)weakSelf = self;
        if ([self isZipUrl:lottieUrlString]) {
            // 异步下载，先放置一个用于占位的 view
            [self.nodeView setupRealView:self.placeholderView];
            [RIAIDLottieZipDownloader unZipWithLottieUrlString:lottieUrlString
                                                       success:^(NSString * _Nullable jsonName, NSError * _Nullable error) {
                __strong typeof(self)strongSelf = weakSelf;
                if (!error) {
                    id<RIAIDLottieViewProviderInterface> provider = [self.context.serviceContainer getServiceInstance:@protocol(RIAIDLottieViewProviderInterface)];
                    strongSelf.lottieView = [provider viewWithFilePath:jsonName];
                    [strongSelf setupLottie];
                    [strongSelf refreshLottie];
                }
            }];
        } else {
            NSURL *url = [NSURL URLWithString:lottieUrlString ?: @""];
            id<RIAIDLottieViewProviderInterface> provider = [self.context.serviceContainer getServiceInstance:@protocol(RIAIDLottieViewProviderInterface)];
            self.lottieView = [provider viewWithURL:url complete:^(NSError *error) {
                if (!error) {
                    __strong typeof(self)strongSelf = weakSelf;
                    if (strongSelf.renderInfo.attributes.lottie.autoPlay.value) {
                        [strongSelf.lottieView play];
                    }
                    [strongSelf refreshLottie];
                }
            }];

            [self setupLottie];
        }
    }
    
}

- (CGSize)measureWithEstimateWidth:(CGFloat)estimateWidth estimateHeight:(CGFloat)estimateHeight {
    [super measureWithEstimateWidth:estimateWidth estimateHeight:estimateHeight];
    if (nil == self.renderInfo) {
        NSAssert(NO, @"节点对应的数据不应该为空");
        return CGSizeZero;
    }
    CGSize renderSize = CGSizeZero;
    renderSize.width = [KCADRenderGeometryCalculate getSizeValueByMode:[KCADRenderKBase renderWidth:self.renderInfo]
                                                           measureVale:self.renderInfo.layout.width
                                                              maxValue:self.renderInfo.layout.maxWidth
                                                         estimateValue:estimateWidth];
    renderSize.height = [KCADRenderGeometryCalculate getSizeValueByMode:[KCADRenderKBase renderHeight:self.renderInfo]
                                                            measureVale:self.renderInfo.layout.height
                                                               maxValue:self.renderInfo.layout.maxHeight
                                                          estimateValue:estimateHeight];
    return renderSize;
}

- (BOOL)dispatchEvent:(NSString *)eventType
              keyList:(NSArray<NSNumber *> *)keyList
           attributes:(RIAIDAttributes *)attributes {
    if ([super dispatchEvent:eventType keyList:keyList attributes:attributes]) {
        if (attributes.hasLottie
            && attributes.lottie.hasProgress) {
            self.renderInfo.attributes = [RIAIDRHighlightAttributesGenerator getNewAttributesWithOrigin:self.renderInfo.attributes
                                                                                         diffAttributes:attributes];
            [self.lottieView setProgress:self.renderInfo.attributes.lottie.progress.value];
            return YES;
        }
    }
    return NO;
}

- (UIView *)getContentView {
    UIView *contentView = [self.lottieView getLottieView];
    if (!contentView
        && !self.placeholderView) {
        self.placeholderView = [RIAIDRenderTouchView new];
        self.placeholderView.backgroundColor = UIColor.clearColor;
        contentView = self.placeholderView;
    }
    return contentView;
}

#pragma mark - private method
- (BOOL)isZipUrl:(NSString *)urlString {
    return [urlString.pathExtension isEqualToString:@"zip"];
}

- (void)setupLottie {
    self.lottieView.progress = self.renderInfo.attributes.lottie.progress ? self.renderInfo.attributes.lottie.progress.value : 0.f;
    self.lottieView.animationSpeed = [KCADRenderKBase renderLottieSpeed:self.renderInfo.attributes.lottie.speed.value];
    if (self.renderInfo.attributes.lottie.repeat.value) {
        RIAIDLottieViewLoopMode loopMode = RIAIDLottieViewLoopModeLoop;
        if (![KCADRenderKBase renderLottieRepeatMode:self.renderInfo.attributes.lottie.repeatMode]) {
            loopMode = RIAIDLottieViewLoopModeAutoReverse;
        }
        self.lottieView.loopMode = loopMode;
    } else {
        self.lottieView.loopMode = RIAIDLottieViewLoopModePlayOnce;
    }
    NSMutableDictionary *replaceTextDictionary = [NSMutableDictionary dictionary];
    for (RIAIDLottieAttributes_ReplaceText *replaceText in self.renderInfo.attributes.lottie.replaceTextListArray) {
        if (replaceText.placeHolder.length > 0) {
            [replaceTextDictionary setValue:replaceText.realText ?: @""
                                     forKey:replaceText.placeHolder];
        }
    }
    if (replaceTextDictionary.allKeys.count > 0) {
        [self.lottieView setDynamicTextWithTextDictionary:replaceTextDictionary];
    }
    // 是否需要自动播放
    if (self.renderInfo.attributes.lottie.autoPlay.value) {
        [self.lottieView play];
    }
    if (self.placeholderView
        && self.placeholderView.superview) {
        // 更换 view，先迁移子视图，再换父视图
        UIView *superView = self.placeholderView.superview;
        [self.lottieView getLottieView].frame = self.placeholderView.frame;
        for (UIView *subView in self.placeholderView.subviews) {
            [KCADRenderLayout parentView:[self.lottieView getLottieView]
                              addSubView:subView];
        }
        [superView insertSubview:[self.lottieView getLottieView] aboveSubview:self.placeholderView];
        [self.placeholderView removeFromSuperview];
    }
    [self.nodeView setupRealView:[self.lottieView getLottieView]];
}

- (void)refreshLottie {
    // 动态修改色值
    for (RIAIDLottieAttributes_ReplaceKeyPathColor *keyPathColor in self.renderInfo.attributes.lottie.replaceKeyPathColorListArray) {
        id<RIAIDRDataBindingService> dataBinding = [self.context.serviceContainer getServiceInstance:@protocol(RIAIDRDataBindingService)];
        for (NSString *keyPathString in keyPathColor.keyPathArray) {
            [self.lottieView setColorValue:[UIColor riaid_getColorType:RIAIDColorTypeR fromHexString:[dataBinding parseHolderData:keyPathColor.color]]
                                    colorG:[UIColor riaid_getColorType:RIAIDColorTypeG fromHexString:[dataBinding parseHolderData:keyPathColor.color]]
                                    colorB:[UIColor riaid_getColorType:RIAIDColorTypeB fromHexString:[dataBinding parseHolderData:keyPathColor.color]]
                                    colorA:[UIColor riaid_getColorType:RIAIDColorTypeA fromHexString:[dataBinding parseHolderData:keyPathColor.color]]
                             keyPathString:[dataBinding parseHolderData:keyPathString]];
        }
    }
}

@end
