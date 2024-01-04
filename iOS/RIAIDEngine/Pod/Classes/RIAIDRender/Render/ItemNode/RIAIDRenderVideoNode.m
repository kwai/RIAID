//
//  RIAIDRenderVideoNode.m
//  KCADRender
//
//  Created by simon on 2022/2/18.
//

#import "RIAIDRenderVideoNode.h"

#pragma mark - service
#import "RIAIDRVideoPlayerProtocol.h"
#import "RIAIDRImageLoaderService.h"
#import "RIAIDRDataBindingService.h"
#import "RIAIDRConsumeActionService.h"

#pragma mark - models
#import <RIAID/Riaid.pbobjc.h>

#pragma mark - views
#import "RIAIDRenderTouchImageView.h"
#import "RIAIDRenderTouchView.h"

#pragma mark - utils
#import "KCADRenderKBase.h"
#import "KCADRenderGeometryCalculate.h"
#import "RIAIDLog.h"
#import "KCADRenderLayout.h"

@interface RIAIDRenderVideoNode () <RIAIDRVideoDelegate>

/// 视频对象
@property (nonatomic, strong) id<RIAIDRVideoPlayerProtocol> videoPlayer;
/// 封面图视图
@property (nonatomic, strong) RIAIDRenderTouchImageView *coverImageView;
/// 包装视图，用于包装封面 imageView 以及 playerView。便于进行背景、阴影等的处理
@property (nonatomic, strong) RIAIDRenderTouchView *contentView;
/// 事件消费服务
@property (nonatomic, weak) id<RIAIDRConsumeActionService> consumeActionService;

@end

@implementation RIAIDRenderVideoNode

- (instancetype)initWithRenderInfo:(RIAIDNode *)renderInfo {
    if (self = [super initWithRenderInfo:renderInfo]) {
        _coverImageView = [RIAIDRenderTouchImageView new];
        _coverImageView.layer.masksToBounds = YES;
    }
    return self;
}

- (void)bindData {
    [super bindData];
    RIAIDAttributes *attributes = self.renderInfo.attributes;
    id<RIAIDRDataBindingService> dataBinding = [self.context.serviceContainer
                                                getServiceInstance:@protocol(RIAIDRDataBindingService)];
    [self.videoPlayer setDataSource:[dataBinding parseHolderData:attributes.video.URL]
                           manifest:[dataBinding parseHolderData:attributes.video.manifest]];
    if (attributes.common.hasAlpha) {
        self.videoPlayer.playerView.alpha = attributes.common.alpha.value;        
    }
    if (attributes.video.hasAutoLoop) {
        [self.videoPlayer setLoop:attributes.video.autoLoop.value];
    }
    if (attributes.video.hasAutoPlay) {
        [self.videoPlayer setAutoPlay:attributes.video.autoPlay.value];
    }
    if (attributes.video.hasAutoMute) {
        [self.videoPlayer setVolumeMute:attributes.video.autoMute.value];
    }
    [self.videoPlayer seekToTime:attributes.video.autoSeekTime];
    self.coverImageView.contentMode = [KCADRenderKBase renderVideoScaleType];
    self.videoPlayer.playerView.contentMode = [KCADRenderKBase renderVideoScaleType];
    // 播放器准备播放
    [self.videoPlayer prepareToPlay];
}

- (CGSize)measureWithEstimateWidth:(CGFloat)estimateWidth estimateHeight:(CGFloat)estimateHeight {
    [super measureWithEstimateWidth:estimateWidth estimateHeight:estimateHeight];
    if (nil == self.renderInfo) {
        NSAssert(NO, @"节点对应的数据不应该为空");
        RIAIDLog(@"Video node info should not be empty");
        return CGSizeZero;
    }
    CGSize freeSize = CGSizeMake(estimateWidth, estimateHeight);
    freeSize.width -= (self.renderInfo.layout.padding.start + self.renderInfo.layout.padding.end);
    freeSize.height -= (self.renderInfo.layout.padding.top + self.renderInfo.layout.padding.bottom);
    // 计算最终显示的尺寸
    CGSize renderSize = CGSizeZero;
    renderSize.width = [KCADRenderGeometryCalculate getSizeValueByMode:[KCADRenderKBase renderWidth:self.renderInfo]
                                                           measureVale:self.renderInfo.layout.width
                                                              maxValue:self.renderInfo.layout.maxWidth
                                                         estimateValue:freeSize.width];
    renderSize.height = [KCADRenderGeometryCalculate getSizeValueByMode:[KCADRenderKBase renderHeight:self.renderInfo]
                                                            measureVale:self.renderInfo.layout.height
                                                               maxValue:self.renderInfo.layout.maxHeight
                                                          estimateValue:freeSize.height];
    // 处理封面图的绘制
    if (self.renderInfo.attributes.video.coverURL.length > 0) {
        id<RIAIDRImageLoaderService> imageLoader = [self.context.serviceContainer
                                                    getServiceInstance:@protocol(RIAIDRImageLoaderService)];
        id<RIAIDRDataBindingService> dataBinding = [self.context.serviceContainer
                                                    getServiceInstance:@protocol(RIAIDRDataBindingService)];
        if ([imageLoader respondsToSelector:@selector(loadImageUrlString:completionBlock:)]) {
            NSString *coverUrlString = self.renderInfo.attributes.video.coverURL;
            if ([dataBinding respondsToSelector:@selector(parseHolderData:)]) {
                coverUrlString = [dataBinding parseHolderData:coverUrlString];
            }
            __weak typeof(self) weakSelf = self;
            [imageLoader loadImageUrlString:coverUrlString
                            completionBlock:^(UIImage * _Nullable image,
                                              NSError * _Nullable error,
                                              NSString * _Nullable imageUrlString) {
                __strong typeof(weakSelf) strongSelf = weakSelf;
                if (error) {
                    RIAIDLog(@"Video cover image load error, the url is: %@ \n, errorMessage: %@",
                             imageUrlString, error.description);
                } else {
                    strongSelf.coverImageView.image = image;
                }
            }];
        } else {
            NSAssert(NO, @"该方法需要接口的实现类进行实现");
            RIAIDLog(@"%@ not implement loadImageUrlString:imageView:completionBlock:",
                     NSStringFromClass(imageLoader.class));
        }
    }
    return renderSize;
}

- (void)drawSelfWithDecorView:(UIView *)decorView {
    // 先把封面和视频添加到内容视图上
    if (self.renderInfo.attributes.video.coverURL.length > 0) {
        self.coverImageView.userInteractionEnabled = self.contentView.userInteractionEnabled;
        [KCADRenderLayout parentView:self.contentView
                          addSubView:self.coverImageView];
    }
    [KCADRenderLayout parentView:self.contentView
                      addSubView:self.videoPlayer.playerView];
    [super drawSelfWithDecorView:decorView];
}

- (void)layout {
    CGFloat start = self.renderInfo.layout.padding.start;
    CGFloat top = self.renderInfo.layout.padding.top;
    CGRect contentRect = (CGRect){CGPointMake(start, top), self.size};
    self.coverImageView.frame = contentRect;
    self.videoPlayer.playerView.frame = contentRect;
    [super layout];
}

- (UIView *)getContentView {
    if (!_contentView) {
        _contentView = [RIAIDRenderTouchView new];
        _contentView.actionDelegate = self;
    }
    return _contentView;
}

- (BOOL)dispatchEvent:(NSString *)eventType
              keyList:(NSArray<NSNumber *> *)keyList
           attributes:(RIAIDAttributes *)attributes {
    BOOL result = [super dispatchEvent:eventType
                               keyList:keyList
                            attributes:attributes];
    const  CGFloat kRenderVideoStartPosition = 0;
    if (result) {
        switch ([eventType integerValue]) {
            case RIAIDADVideoActionModel_VideoControlType_VideoReplay: {
                [self.videoPlayer seekToTime:kRenderVideoStartPosition];
                [self.videoPlayer play];
            } break;
            case RIAIDADVideoActionModel_VideoControlType_VideoPositionReset: {
                [self.videoPlayer seekToTime:kRenderVideoStartPosition];
                [self.videoPlayer pause];
            } break;
            case RIAIDADVideoActionModel_VideoControlType_VideoPause: {
                [self.videoPlayer pause];
            } break;
            case RIAIDADVideoActionModel_VideoControlType_VideoPlay: {
                [self.videoPlayer play];
            } break;
            case RIAIDADVideoActionModel_VideoControlType_VideoSoundTurnOn: {
                [self.videoPlayer setVolumeMute:YES];
            } break;
            case RIAIDADVideoActionModel_VideoControlType_VideoSoundTurnOff: {
                [self.videoPlayer setVolumeMute:NO];
            } break;
            default:
                break;
        }
    }
    return result;
}

- (id<RIAIDRVideoStateProtocol>)getVideoStateInfo {
    return self.videoPlayer;
}

#pragma mark - private method
- (id<RIAIDRVideoPlayerProtocol>)videoPlayer {
    if (!_videoPlayer) {
        _videoPlayer = [self.context.serviceContainer getServiceInstance:@protocol(RIAIDRVideoPlayerProtocol)];
        [_videoPlayer addDelegate:self];
    }
    return _videoPlayer;
}

- (id<RIAIDRConsumeActionService>)consumeActionService {
    if (!_consumeActionService) {
        _consumeActionService = [self.context.serviceContainer getServiceInstance:@protocol(RIAIDRConsumeActionService)];
    }
    return _consumeActionService;
}

#pragma mark - video delegate
// 视频处于准备播放阶段
- (void)videoDidPrepare {
    RIAIDLog(@"Render video prepare");
}

// 视频处于预加载阶段
- (void)videoDidPreload {
    RIAIDLog(@"Render video did preload");
}

// 视频开始播放
- (void)videoDidStart {
    RIAIDLog(@"Render video did start");
    if ([self.videoPlayer isFirstPlay]) {
        [self.consumeActionService consumeRenderAction:RIAIDConsumeActionTypeVideoStart
                                             responder:self.renderInfo.videoHandler.start];
    } else {
        [self.consumeActionService consumeRenderAction:RIAIDConsumeActionTypeVideoResume
                                             responder:self.renderInfo.videoHandler.resume];
    }
}

// 视频处于暂停阶段
- (void)videoDidPause {
    RIAIDLog(@"Render video did pause");
    [self.consumeActionService consumeRenderAction:RIAIDConsumeActionTypeVideoPause
                                         responder:self.renderInfo.videoHandler.pause];
}

// 视频准备完毕
- (void)videoDidFinishPreparingWithDuration:(NSTimeInterval)duration {
    RIAIDLog(@"Render video did finish prepare");
}

// 视频首帧渲染完毕
- (void)videoDidRenderFirstVideoFrame {
    RIAIDLog(@"Render video did render first video frame");
    [self.coverImageView removeFromSuperview];
    [self.consumeActionService consumeRenderAction:RIAIDConsumeActionTypeVideoImpress
                                         responder:self.renderInfo.videoHandler.impression];
}

// 播放完成
- (void)videoDidPlayAtEnd {
    RIAIDLog(@"Render video did play at end");
    [self.consumeActionService consumeRenderAction:RIAIDConsumeActionTypeVideoFinish
                                         responder:self.renderInfo.videoHandler.finish];
}

// 视频播放失败
- (void)videoDidPlayError:(NSError *)error {
    RIAIDLog(@"Render video did play error %@", error.description);
}

// 视频下载回调
- (void)videoDidDownLoadSize:(long long)downloadSize totalSize:(long long)totalSize {
    RIAIDLog(@"Render video did download with size: %lld", downloadSize);
}

@end
