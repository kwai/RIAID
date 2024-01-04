//
//  RIAIDBSceneController.m
//  KCADBrowser
//
//  Created by liweipeng on 2022/1/12.
//

#import "RIAIDBSceneController.h"
#import "RIAIDLog.h"

@interface RIAIDBSceneController()

/// 场景数据模型
@property (nonatomic, copy, readwrite) RIAIDADSceneModel *sceneModel;
/// 外部自定义Render的提供类
@property (nonatomic, strong) id<RIAIDBExternalRenderProvider> externalRenderProvider;
/// adrender需要的上下文
@property (nonatomic, strong) RIAIDRenderContext *context;
/// 画布
@property (nonatomic, strong) UIView *canvas;
/// 场景版本
@property (nonatomic, assign, readwrite) NSInteger currentSceneVersion;

@end

@implementation RIAIDBSceneController

- (instancetype)initWithScene:(RIAIDADSceneModel*)sceneModel
       externalRenderProvider:(nullable id<RIAIDBExternalRenderProvider>)externalRenderProvider
                       canvas:(UIView*)canvas
                      context:(nullable RIAIDRenderContext *)context {
    self = [self init];
    if (self) {
        _currentSceneVersion = 0;
        /// 避免内部操作数据，对原始数据产生影响
        _sceneModel = [sceneModel copy];
        _externalRenderProvider = externalRenderProvider;
        _context = context;
        _canvas = canvas;
    }
    return self;
}

- (void)setHidden:(BOOL)hidden isLifeCycle:(BOOL)isLifeCycle sceneVersion:(NSInteger)version {
    // 记录的上次版本若和当前版本一致，则说明场景已经更新，则不需要进行接下来的操作
    if (version != self.currentSceneVersion) {
        return;
    }
    [self.adRender.decorView setHidden:hidden];
    
    /// 是否需要触发生命周期
    if (isLifeCycle) {
        RIAIDADSceneLifeCycleModel *lifeCycleModel = self.sceneModel.lifeCycle;
        
        /// 通过代理，处理生命周期对应的Triggers
        if (hidden) {
            RIAIDLog(@"Scene生命周期-disappear key:%d", self.sceneModel.key);
            
            /// RenderView从Canvas移除
            if (self.adRender && self.adRender.decorView.superview) {
                [self.adRender.decorView removeFromSuperview];
            }
            
            
            [lifeCycleModel.disappearTriggerKeysArray enumerateValuesWithBlock:^(int32_t value, NSUInteger idx, BOOL * _Nonnull stop) {
                if (self.delegate && [self.delegate respondsToSelector:@selector(sceneController:handleTrigger:)]) {
                    [self.delegate sceneController:self handleTrigger:value];
                }
            }];
            
        } else {
            RIAIDLog(@"Scene生命周期-appear key:%d", self.sceneModel.key);
            [self onScreenIfNeed];
            
            [lifeCycleModel.appearTriggerKeysArray enumerateValuesWithBlock:^(int32_t value, NSUInteger idx, BOOL * _Nonnull stop) {
                if (self.delegate && [self.delegate respondsToSelector:@selector(sceneController:handleTrigger:)]) {
                    [self.delegate sceneController:self handleTrigger:value];
                }
            }];
        }
    }
}

- (void)onScreenIfNeed {
    /// RenderView添加到Canvas
    if (!self.adRender) {
        self.adRender = [self createRender:self.sceneModel];
        [self applyCurrentLayout];
    }
    /// Render存在 && Render对应的View没有SuperView && 画布存在
    if (self.adRender && (!self.adRender.decorView.superview) && self.canvas) {
        [self.canvas addSubview:self.adRender.decorView];
    }
}

- (void)resetScene {
    self.currentSceneVersion ++;
}

#pragma mark - Render
- (KCADRender*)createRender:(RIAIDADSceneModel*)sceneModel {
    KCADRender *adRender = nil;
    /// 尝试使用外部自定义场景
    adRender = [self.externalRenderProvider renderWithScene:sceneModel];
    if (!adRender) {
        /// 若外部不支持自定义本场景，使用KCADRender进行初始化
        adRender = [[KCADRender alloc] initWithRenderInfo:sceneModel.render.renderData context:self.context];
    }
    [adRender renderWithEstimateWidth:self.canvas.bounds.size.width estimateHeight:self.canvas.bounds.size.height];
    
    return adRender;
}

- (void)resizeRender:(CGSize)canvasSize {
    if (_adRender) {
        [_adRender reRenderWithEstimateSize:canvasSize];
    }
}

#pragma mark - 布局
- (void)applyCurrentLayout {
    if (_adRender) {
        CGSize size = _adRender.decorView.bounds.size;
        _adRender.decorView.frame = CGRectMake(self.originXValue.floatValue, self.originYValue.floatValue, size.width, size.height);
    }
}

- (CGSize)expectSize {
    if (_adRender) {
        return _adRender.decorView.bounds.size;
    }
    
    RIAIDNode *renderInfo = self.sceneModel.render.renderData;
    return [KCADRender sizeWithRenderInfo:renderInfo estimateWidth:self.canvas.bounds.size.width estimateHeight:self.canvas.bounds.size.height context:self.context];
}
@end
