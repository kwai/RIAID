//
//  RIAIDBrowserDirector+Transition.m
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/19.
//

#import "RIAIDBrowserDirector+Transition.h"
#import "RIAIDBrowserDirector+Layout.h"
#import "RIAIDADTransitionModel+Browser.h"

#import "KCADRenderNode.h"
#import "RIAIDDecorViewWidget.h"
#import "RIAIDLog.h"
#import "RIAIDTimeConversion.h"
#import "UIView+RIAIDBAnimation.h"
#import "NSNumber+RIAIDBToString.h"
#import "RIAIDBrowser.h"
#import "KCADRenderTextNode.h"

@implementation RIAIDBrowserDirector (Transition)

- (void)handleTransition:(RIAIDADTransitionModel*)transition {
    switch (transition.transitionType) {
        case RIAIDADTransitionTypeVisibility:
            [self handleVisibility:transition.visibility];
            break;
        case RIAIDADTransitionTypeTemplate:
            [self handleTemplate_p:transition.template_p];
            break;
        case RIAIDADTransitionTypeTranslation:
            [self handleTranslation:transition.translation];
            break;
        case RIAIDADTransitionTypeInSceneAnimation:
            [self handleInSceneAnimation:transition.inSceneAnimation];
            break;
        case RIAIDADTransitionTypeSceneShare:
            [self handleSceneShare:transition.sceneShare];
            break;
        case RIAIDADTransitionTypeLottie:
            [self handleLottie:transition.lottie];
            break;
        case RIAIDADTransitionTypeRenderContent:
            [self handleRenderContent:transition.renderContent];
            break;
        default:
            break;
    }
}

- (void)handleVisibility:(RIAIDADVisibilityTransitionModel*)visibility {
    RIAIDBSceneController *targetSceneCtrl = [self.sceneCtrlMap objectForKey:@(visibility.sceneKey).riaidIntString];
    
    /// 将Scene对应的Render上屏
    /// 仅即将展示的Scene需要上屏：如果是需要被隐藏的Scene，该Scene此刻应该已经在屏幕上
    if (!visibility.hidden) {
        [targetSceneCtrl onScreenIfNeed];
    }
    
    KCADRender *targetRender = targetSceneCtrl.adRender;
    UIView *targetView = targetRender.decorView;
    
    /// 记录初始透明度，动画完成之后恢复
    /// alpha动画仅用于切换可见状态，动画完之后，应当移除
    CGFloat originAlpha = targetView.alpha;
    
    NSTimeInterval duration = [RIAIDTimeConversion millisecondToSecond:visibility.duration];
    CGFloat startAlpha = visibility.startAlpha;
    CGFloat endAlpha = visibility.endAlpha;
    BOOL hidden = visibility.hidden;
    
    __block NSInteger sceneVersion = targetSceneCtrl.currentSceneVersion;
    /// 当duration>0时，才需要做动画，startAlpha和endAlpha才会配置
    if (duration <= 0) {
        [targetSceneCtrl setHidden:hidden isLifeCycle:YES sceneVersion:sceneVersion];
    } else {
        targetView.hidden = NO;
        targetView.alpha = startAlpha;
        [UIView animateWithDuration:duration animations:^{
            targetView.alpha = endAlpha;
        } completion:^(BOOL finished) {
            if (hidden) {
                targetView.alpha = originAlpha;
            }
            [targetSceneCtrl setHidden:hidden isLifeCycle:YES sceneVersion:sceneVersion];
            if ([self.transitionDelegate respondsToSelector:@selector(transitionVisibilityComplete:hidden:)]) {
                [self.transitionDelegate transitionVisibilityComplete:targetRender hidden:visibility.hidden];
            }
        }];
    }
}

- (void)handleTemplate_p:(RIAIDADTemplateTransitionModel*)template_p {
    RIAIDBSceneController *targetSceneCtrl = [self.sceneCtrlMap objectForKey:@(template_p.sceneKey).riaidIntString];
    /// 将Scene对应的Render上屏
    [targetSceneCtrl onScreenIfNeed];
    
    KCADRender *targetRender = targetSceneCtrl.adRender;
    UIView *targetView = targetRender.decorView;
    /// 模板动画时长
    NSTimeInterval duration = [RIAIDTimeConversion millisecondToSecond:template_p.duration];
    CGRect rect = targetView.frame;
    
    __block NSInteger sceneVersion = targetSceneCtrl.currentSceneVersion;
    if (template_p.template_p == RIAIDADTemplateTransitionModel_TemplateType_EnterFromStart) {
        CGRect fromEnterRect = CGRectMake(-rect.size.width, rect.origin.y, rect.size.width, rect.size.height);
        targetView.frame = fromEnterRect;
        targetView.hidden = NO;
        [UIView animateWithDuration:duration animations:^{
            targetView.frame = rect;
        } completion:^(BOOL finished) {
            [targetSceneCtrl setHidden:NO isLifeCycle:YES sceneVersion:sceneVersion];
        }];
    } else if (template_p.template_p == RIAIDADTemplateTransitionModel_TemplateType_ExitFromStart) {
        CGRect targetExitRect = CGRectMake(-rect.size.width, rect.origin.y, rect.size.width, rect.size.height);
        targetView.hidden = NO;
        [UIView animateWithDuration:duration animations:^{
            targetView.frame = targetExitRect;
        } completion:^(BOOL finished) {
            targetView.frame = rect;
            [targetSceneCtrl setHidden:YES isLifeCycle:YES sceneVersion:sceneVersion];
        }];
    }
}

- (void)handleTranslation:(RIAIDADTranslationTransitionModel*)translation {
    for (RIAIDADSceneRelationModel *relationModel in translation.sceneRelationsArray) {
        [self addLayout:relationModel];
    }
    
    NSTimeInterval duration = [RIAIDTimeConversion millisecondToSecond:translation.duration];
    [UIView animateWithDuration:duration animations:^{
        // TODO: 这里本质上重绘了所有的render。这段逻辑需要确认一下，如果不重绘，则可能依赖潜规则。如果重绘scenekey本质上无用
        [self executeAllLayout];
    }];
}

- (void)handleInSceneAnimation:(RIAIDADInSceneAnimationTransitionModel*)inSceneAnimation {
    KCADRender *targetRender = [self findRenderByViewKey:inSceneAnimation.viewKey];
    // 根据 key 找出对应的 Render
    KCADRenderNode *findResultRender = [targetRender findRenderNodeByKey:inSceneAnimation.viewKey];
    RIAIDADAnimationModel *riaidAnimationModel = inSceneAnimation.animation;
    BOOL isAlpha = NO;
    float toValue = 0;
    if (riaidAnimationModel.propertyType == RIAIDADAnimationModel_ViewPropertyType_Alpha) {
        /// 非Hidden动画时，riaidAnimationModel.valuesArray_Count必须为2
        if (riaidAnimationModel.valuesArray_Count != 2) {
            NSAssert(NO, @"Animation valuesArray_Count != 2");
        }
        isAlpha = YES;
        toValue = [riaidAnimationModel.valuesArray valueAtIndex:1];
    }
    // 开始进行动画
    __weak typeof(findResultRender) weakFindResultRender = findResultRender;
    [findResultRender.nodeView.getDecorView executeRiaid:riaidAnimationModel completion:^(BOOL finished) {
        if (isAlpha) {
            [weakFindResultRender updateViewWithAlpha:toValue frame:weakFindResultRender.frame];
        }
    }];
}

- (void)handleSceneShare:(RIAIDADSceneShareTransitionModel*)sceneShare {
    int32_t startSceneKey = sceneShare.startSceneKey;
    int32_t endSceneKey = sceneShare.endSceneKey;
    NSTimeInterval duration = [RIAIDTimeConversion millisecondToSecond:sceneShare.duration];

    RIAIDBSceneController *startSceneCtrl = [self.sceneCtrlMap objectForKey:@(startSceneKey).riaidIntString];
    /// 将Scene对应的Render上屏
    [startSceneCtrl onScreenIfNeed];
    KCADRender *startRender = startSceneCtrl.adRender;
    
    RIAIDBSceneController *endSceneCtrl = [self.sceneCtrlMap objectForKey:@(endSceneKey).riaidIntString];
    KCADRender *endRender = [endSceneCtrl createRender:endSceneCtrl.sceneModel];
    endSceneCtrl.adRender = endRender;
    [endSceneCtrl applyCurrentLayout];
    NSArray<KCADRenderNode *> *nodeList = [startRender diffWithRender:endRender.rootNode.renderInfo
                                                         estimateSize:CGSizeMake(MIN([UIScreen mainScreen].bounds.size.width,
                                                                                     [UIScreen mainScreen].bounds.size.height),
                                                                                 MAX([UIScreen mainScreen].bounds.size.width,
                                                                                     [UIScreen mainScreen].bounds.size.height))];
    
    for (KCADRenderNode *adRenderNode in nodeList) {
        [adRenderNode.nodeView executeShareAnimationWithNewFrame:adRenderNode.newFrame
                                                        newAlpha:adRenderNode.newAlpha
                                                        duration:duration
                                                   completeBlock:^(BOOL isComplete) {

        }];
        // 这个地方是因为富文本使用 YYTextLayout 渲染的，只给 YYLabel 做动画不会让 layout 生效。所以需要单独对 layout 做次刷新动画，代码有点 tricky
        if ([adRenderNode isKindOfClass:[KCADRenderTextNode class]]) {
            KCADRenderTextNode *textNode = (KCADRenderTextNode *)adRenderNode;
            [UIView animateWithDuration:duration
                             animations:^{
                [textNode.richTextWidget reloadTextLayoutWithSize:adRenderNode.newFrame.size];
            }];
        }
    }

    [startRender.decorView executeFromValue:[NSValue valueWithCGRect:(CGRect){CGPointZero, startRender.decorView.frame.size}]
                                    toValue:[NSValue valueWithCGRect:(CGRect){CGPointZero, endRender.decorView.frame.size}]
                                   duration:duration
                                    keyPath:@"bounds"
                                 completion:nil];
    __weak typeof(nodeList) weakNodeList = nodeList;
    [startRender.decorView executeFromValue:[NSValue valueWithCGPoint:startRender.decorView.center]
                                    toValue:[NSValue valueWithCGPoint:endRender.decorView.center]
                                   duration:duration
                                    keyPath:@"position"
                                 completion:^(BOOL isComplete) {
        if (!isComplete) {
            return;
        }
        startRender.decorView.frame = endRender.decorView.frame;
        for (KCADRenderNode *adRenderNode in weakNodeList) {
            adRenderNode.nodeView.alpha = adRenderNode.newAlpha;
            adRenderNode.nodeView.frame = adRenderNode.newFrame;
            [adRenderNode.nodeView refreshAttributes];
            [adRenderNode updateViewWithAlpha:adRenderNode.newAlpha frame:adRenderNode.newFrame];
        }
    }];
    
    /// 将StartScene的Render指向EndScene
    endSceneCtrl.adRender = startRender;
    startSceneCtrl.adRender = nil;
    
    /// 重设Render的Frame
    [endSceneCtrl applyCurrentLayout];
}

- (void)handleLottie:(RIAIDADLottieTransitionModel*)lottie {
    KCADRender *render = [self.sceneCtrlMap objectForKey:@(lottie.sceneKey).riaidIntString].adRender;
    NSMutableArray<NSNumber*> *numArr = [NSMutableArray array];
    [lottie.viewKeysArray enumerateValuesWithBlock:^(int32_t value, NSUInteger idx, BOOL * _Nonnull stop) {
        [numArr addObject:@(value)];
    }];

    __block NSTimeInterval lottieProgress = 0;
    __block NSTimeInterval interval = [RIAIDTimeConversion millisecondToSecond:lottie.interval];
    NSTimeInterval maxProgress = [RIAIDTimeConversion millisecondToSecond:lottie.maxProgress];
    
    // TODO: 把边界情况测一下
    __weak typeof(self) weakSelf = self;
    NSTimer *timer = [NSTimer timerWithTimeInterval:interval repeats:YES block:^(NSTimer * _Nonnull timer) {
        if (weakSelf == nil) {
            [timer invalidate];
        }
        
        if (lottieProgress > maxProgress) {
            [timer invalidate];
        }
        
        RIAIDAttributes *attributes = [RIAIDAttributes new];
        RIAIDLottieAttributes *lottieAttributes = [RIAIDLottieAttributes new];
        RIAIDFloatValue *progress = [RIAIDFloatValue new];
        progress.value = lottieProgress/maxProgress;
        lottieAttributes.progress = progress;
        attributes.lottie = lottieAttributes;
        [render.rootNode dispatchEvent:lottie.lottieType keyList:numArr attributes:attributes];
        
        lottieProgress += interval;
        
        RIAIDLog(@"timer progress:%f", lottieProgress);
    }];
    
    [[NSRunLoop mainRunLoop] addTimer:timer forMode:NSRunLoopCommonModes];
}

- (void)handleRenderContent:(RIAIDADRenderContentTransitionModel*)renderContent {
    KCADRender *render = [self findRenderByViewKey:renderContent.viewKey];
    [render.rootNode dispatchEvent:@"attribute" keyList:@[@(renderContent.viewKey)] attributes:renderContent.renderAttributes];
    [render layoutIfNeeded];
}

#pragma mark - Animation


@end
