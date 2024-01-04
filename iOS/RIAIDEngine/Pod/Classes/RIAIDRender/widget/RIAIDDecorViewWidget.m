//
//  RIAIDDecorViewWidget.m
//  KCADRender
//
//  Created by simon on 2021/12/28.
//

#import "RIAIDDecorViewWidget.h"

#pragma mark - view
#import "RIAIDRenderTouchView.h"

#pragma mark - utils
#import "UIView+DrawBackgroundAttributes.h"
#import "KCADRenderKBase.h"
#import <YYText/YYText.h>
#import "UIView+RIAIDBAnimation.h"
#import "CALayer+RIAIDBAnimation.h"
#import "UIColor+KCADRenderHex.h"

@interface RIAIDDecorViewWidget ()

@property (nonatomic, assign) BOOL isBox;
@property (nonatomic, assign) BOOL haveBackground;
@property (nonatomic, strong) RIAIDNode *nodeInfo;

/// 阴影包裹视图
@property (nonatomic, strong) RIAIDRenderTouchView *shadowDecorView;
@property (nonatomic, strong, readwrite) UIView *realView;
@property (nonatomic, strong) RIAIDRenderTouchView *foregroundView;
@property (nonatomic, strong) RIAIDRenderTouchView *backgroundView;

@end

@implementation RIAIDDecorViewWidget

- (instancetype)initWithIsBox:(BOOL)isBox
                   attributes:(RIAIDNode *)nodeInfo
                     realView:(UIView *)realView
               actionDelegate:(id<RIAIDTouchViewDelegate>)actionDelegate {
    if (self = [super init]) {
        _isBox = isBox;
        _nodeInfo = nodeInfo;
        _realView = realView;
        _actionDelegate = actionDelegate;
        [self setupUI];
    }
    return self;
}

/// 根据属性初始化所需要的 UI
- (void)setupUI {
    if (self.nodeInfo.attributes.common.hasShadow) {
        _shadowDecorView = [RIAIDRenderTouchView new];
        _shadowDecorView.backgroundColor = UIColor.clearColor;
    }
    if (self.nodeInfo.classType == RIAIDNode_ClassType_ClassTypeLayoutButton) {
        _foregroundView = [RIAIDRenderTouchView new];
        _foregroundView.backgroundColor = UIColor.clearColor;
        _foregroundView.actionDelegate = self.actionDelegate;
    }
    if (_isBox
        && [KCADRenderKBase hasBackground:self.nodeInfo.attributes]) {
        _haveBackground = YES;
        _backgroundView = [RIAIDRenderTouchView new];
        _backgroundView.actionDelegate = self.actionDelegate;
    }
}

- (void)setUserInteractionEnabled:(BOOL)userInteractionEnabled {
    _userInteractionEnabled = userInteractionEnabled;
    self.shadowDecorView.userInteractionEnabled = userInteractionEnabled;
    self.foregroundView.userInteractionEnabled = userInteractionEnabled;
    self.backgroundView.userInteractionEnabled = userInteractionEnabled;
    self.realView.userInteractionEnabled = userInteractionEnabled;
}

- (UIView *)getDecorView {
    if (self.nodeInfo.attributes.common.hasShadow) {
        // 有阴影的时候，父视图为阴影包裹
        return self.shadowDecorView;
    } else {
        if (self.isBox) {
            // 盒子节点有背景时，背景为父视图，若无父视图，尝试返回 realView
            return self.backgroundView ?: self.realView;
        } else {
            // 非盒子节点，真实视图为父视图
            return self.realView;
        }
    }
    return nil;
}

- (UIView *)getRenderView {
    UIView *renderView = self.realView;
    if (self.realView.subviews.count > 0) {
        renderView = self.realView.subviews.firstObject;
    }
    return renderView;
}

- (UIView *)getForegroundView {
    return self.foregroundView;
}

- (BOOL)isButton {
    return self.nodeInfo.classType == RIAIDNode_ClassType_ClassTypeLayoutButton;
}

- (void)configViewKey:(NSInteger)viewKey {
    UIView *renderView = self.realView;
    if (nil == renderView) {
        return;
    }
    NSMutableArray *viewArray = [NSMutableArray arrayWithArray:renderView.subviews];
    [viewArray addObject:renderView];
    for (UIView *subView in viewArray) {
        if (subView.userInteractionEnabled) {
            subView.accessibilityIdentifier = [NSString stringWithFormat:@"%ld", viewKey];
        }
    }
}

- (void)setupRealView:(UIView *)realView {
    self.realView = realView;
}

- (void)setupForeground {
    UIView *decorView = [self getDecorView];
    [self parentView:decorView addSubView:self.foregroundView];
    [decorView bringSubviewToFront:self.foregroundView];
}

- (void)setupBackground {
    if ([KCADRenderKBase hasBackground:self.nodeInfo.attributes]) {
        // 先判断是否有背景视图，没有背景视图，尝试用真实视图渲染
        if (self.haveBackground) {
            [self.backgroundView drawWithCommonAttributes:self.nodeInfo.attributes.common
                                                     size:self.frame.size
                                                  context:self.renderContext];
            if (self.realView
                && self.backgroundView.subviews.count == 0) {
                [self parentView:self.backgroundView addSubView:self.realView];
            }
        } else {
            [self.realView drawWithCommonAttributes:self.nodeInfo.attributes.common
                                               size:self.frame.size
                                            context:self.renderContext];
        }
    }
}

- (void)setupShadow {
    if (self.haveBackground) {
        if (self.nodeInfo.attributes.common.hasShadow) {
            [self parentView:self.shadowDecorView addSubView:self.backgroundView];
            [self.backgroundView drawShadow:self.nodeInfo.attributes.common.shadow context:self.renderContext];
        }
    } else {
        if (self.nodeInfo.attributes.common.hasShadow) {
            [self parentView:self.shadowDecorView addSubView:self.realView];
            [self.realView drawShadow:self.nodeInfo.attributes.common.shadow context:self.renderContext];
        }
    }
}

- (void)refreshAttributes {
    [self setupBackground];
    [self setupShadow];
    [self setupForeground];
}

- (void)setViewHidden:(BOOL)hidden {
    self.shadowDecorView.hidden = hidden;
    self.backgroundView.hidden = hidden;
    self.realView.hidden = hidden;
    self.foregroundView.hidden = hidden;
}

- (BOOL)isContainer {
    BOOL haveShadow = self.nodeInfo.attributes.common.hasShadow;
    // 有背景且没有真正的视图时，需要一个单独的背景视图
    if ([self.realView isKindOfClass:[UIScrollView class]]
        || haveShadow
        || self.haveBackground) {
        return YES;
    }
    return NO;
}

- (void)executeShareAnimationWithNewFrame:(CGRect)newFrame
                                 newAlpha:(CGFloat)newAlpha
                                 duration:(NSTimeInterval)duration
                            completeBlock:(RIAIDRShareAnimationBlock)animationBlock {
    // 背景视图做动画，先判断是否有阴影，有阴影时，需要将新 frame 和 padding 做一次计算
    BOOL haveShadow = self.nodeInfo.attributes.common.hasShadow;
    CGRect backgroundFrame = (CGRect){CGPointZero, newFrame.size};
    // 真正渲染的视图做动画
    if (self.realView) {
        if (!haveShadow && !self.haveBackground) {
            backgroundFrame = newFrame;
        }
        for (UIView *subView in self.realView.subviews) {
            if ([subView isKindOfClass:[YYLabel class]]) {
                [self _executeShareAnimationWithView:subView
                                            NewFrame:(CGRect){CGPointZero, newFrame.size}
                                            newAlpha:newAlpha
                                            duration:duration
                                       completeBlock:animationBlock];
            }
        }
        [self _executeShareAnimationWithView:self.realView
                                    NewFrame:backgroundFrame
                                    newAlpha:newAlpha
                                    duration:duration
                               completeBlock:animationBlock];
    }
    // 背景视图做动画
    if (self.backgroundView) {
        if (!haveShadow) {
            backgroundFrame = newFrame;
        }
        [self _executeShareAnimationWithView:self.backgroundView
                                    NewFrame:backgroundFrame
                                    newAlpha:newAlpha
                                    duration:duration
                               completeBlock:animationBlock];
    }
    // 给阴影视图添加动画
    if (self.shadowDecorView) {
        [self _executeShareAnimationWithView:_shadowDecorView
                                    NewFrame:newFrame
                                    newAlpha:newAlpha
                                    duration:duration
                               completeBlock:animationBlock];
    }
    // 给前景视图添加动画
    if (self.foregroundView) {
        [self _executeShareAnimationWithView:_foregroundView
                                    NewFrame:(CGRect){CGPointZero, newFrame.size}
                                    newAlpha:newAlpha
                                    duration:duration
                               completeBlock:animationBlock];
    }
}

#pragma mark - private method
- (void)_executeShareAnimationWithView:(UIView *)targetView
                              NewFrame:(CGRect)newFrame
                              newAlpha:(CGFloat)newAlpha
                              duration:(NSTimeInterval)duration
                         completeBlock:(RIAIDRShareAnimationBlock)animationBlock {
    [targetView executeFromValue:[NSValue valueWithCGRect:(CGRect){CGPointZero, targetView.frame.size}]
                         toValue:[NSValue valueWithCGRect:(CGRect){CGPointZero, newFrame.size}]
                        duration:duration
                         keyPath:@"bounds"
                      completion:nil];
    [targetView executeFromValue:[NSNumber numberWithFloat:targetView.alpha]
                         toValue:[NSNumber numberWithFloat:newAlpha]
                        duration:duration
                         keyPath:@"opacity"
                      completion:nil];
    CGPoint newCenter = CGPointMake(newFrame.origin.x + newFrame.size.width * 0.5,
                                    newFrame.origin.y + newFrame.size.height * 0.5);
    [targetView executeFromValue:[NSValue valueWithCGPoint:targetView.center]
                         toValue:[NSValue valueWithCGPoint:newCenter]
                        duration:duration
                         keyPath:@"position"
                      completion:nil];
    // 重新给 frame 付一次值
    targetView.frame = newFrame;
    // 修改 path
    UIBezierPath *newPath = [self getBackgroundNewPathWithSize:newFrame.size];
    if (newPath && targetView.maskLayer) {
        UIBezierPath *maskPath = newPath.copy;
        [targetView.maskLayer executeFromValue:[UIBezierPath bezierPathWithCGPath:targetView.maskLayer.path]
                                       toValue:maskPath
                                       keyPath:@"path"
                                      duration:duration];
        targetView.maskLayer.path = maskPath.CGPath;
    }
    if (newPath && targetView.borderLayer) {
        UIBezierPath *borderPath = newPath.copy;
        [targetView.borderLayer executeFromValue:[UIBezierPath bezierPathWithCGPath:targetView.borderLayer.path]
                                         toValue:borderPath
                                         keyPath:@"path"
                                        duration:duration];
        targetView.borderLayer.path = borderPath.CGPath;
    }
}

- (UIBezierPath *)getBackgroundNewPathWithSize:(CGSize)size {
    return [UIView getNewPathWithCommonAttributes:self.nodeInfo.attributes.common
                                             size:size];
}

- (UIBezierPath *)getShadowPathWithSize:(CGSize)size {
    return [UIView getNewShadowPathWithCommonAttributes:self.nodeInfo.attributes.common.shadow
                                                   size:size];
}

- (void)parentView:(UIView *)parentView addSubView:(UIView *)subView {
    BOOL isAdded = [parentView.subviews containsObject:subView];
    if (!isAdded) {
        [parentView addSubview:subView];
    }
}

#pragma mark - setter && getter
- (void)setFrame:(CGRect)frame {
    _frame = frame;
    BOOL haveShadow = self.nodeInfo.attributes.common.hasShadow;
    CGRect bounds = (CGRect){CGPointZero, frame.size};
    if (haveShadow) {
        // 有阴影包裹时，阴影包裹为父视图，需要使用 frame 赋值
        self.shadowDecorView.frame = frame;
        self.realView.frame = bounds;
        self.backgroundView.frame = bounds;
        self.foregroundView.frame = bounds;
    } else {
        // 无阴影包裹时，背景或真实渲染 View 为父视图，需要使用 frame 赋值。这里不区分背景和真实 view，是因为两者不能并存
        self.backgroundView.frame = frame;
        self.realView.frame = self.haveBackground ? bounds : frame;
        self.foregroundView.frame = bounds;
    }
    // 处理 YYLabel 被包裹的情况
    for (UIView *subView in self.realView.subviews) {
        if ([subView isKindOfClass:[YYLabel class]]) {
            subView.frame = bounds;
        }
    }
    // 非盒子且有 padding 时，真实视图的 view 需要根据 padding 再计算一次最终 frame
    if (!self.isBox
        && self.nodeInfo.layout.hasPadding) {
        // realView 还有子视图时，对子视图进行 frame 的赋值处理
        if (self.realView.subviews.count > 0) {
            for (UIView *subView in self.realView.subviews) {
                if ([subView isKindOfClass:[YYLabel class]]) {
                    subView.frame = CGRectMake(subView.frame.origin.x + self.nodeInfo.layout.padding.start,
                                               subView.frame.origin.y + self.nodeInfo.layout.padding.top,
                                               subView.frame.size.width - (self.nodeInfo.layout.padding.start
                                                                           + self.nodeInfo.layout.padding.end),
                                               subView.frame.size.height - (self.nodeInfo.layout.padding.top
                                                                            + self.nodeInfo.layout.padding.bottom));
                }
            }
        } else {
            self.realView.frame = (CGRect){
                CGPointMake(self.realView.frame.origin.x + self.nodeInfo.layout.padding.start,
                            self.realView.frame.origin.y),
                self.realView.frame.size};
        }
    }
}

- (void)setAlpha:(CGFloat)alpha {
    UIView *view = self.realView ?: self.backgroundView;
    view.alpha = alpha;
}

- (void)setActionDelegate:(id<RIAIDTouchViewDelegate>)actionDelegate {
    self.backgroundView.actionDelegate = actionDelegate;
    self.foregroundView.actionDelegate = actionDelegate;
}

@end
