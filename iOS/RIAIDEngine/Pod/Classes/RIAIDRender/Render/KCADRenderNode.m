//
//  KCADRenderNode.m
//  KCADRender
//
//  Created by simon on 2021/11/11.
//

#import "KCADRenderNode.h"

#pragma mark - view
#import "RIAIDRenderTouchView.h"
#import "KCADRenderBoxNode.h"
#import "KCADRenderImageNode.h"
#import "KCADRenderButtonNode.h"
#import "KCADRenderTextNode.h"
#import "KCADRenderBoxNode.h"
#import "KCADRenderVScrollNode.h"

#pragma mark - utils
#import "KCADRenderGeometryCalculate.h"
#import "KCADRenderKBase.h"
#import "KCADRenderLayout.h"
#import "KCADRenderLayerDraw.h"
#import "UIView+DrawBackgroundAttributes.h"
#import "RIAIDRHighlightAttributesGenerator.h"

@interface KCADRenderNode ()

@property (nonatomic, assign, readwrite) BOOL isRoot;
@property (nonatomic, strong, readwrite) RIAIDDecorViewWidget *nodeView;

@end

@implementation KCADRenderNode

@synthesize nodeView = _nodeView;
@synthesize size = _size;
@synthesize frame = _frame;
@synthesize alpha = _alpha;
@synthesize newFrame = _newFrame;
@synthesize newAlpha = _newAlpha;

// 需要父类重写
- (instancetype)initWithRenderInfo:(RIAIDNode *)renderInfo {
    if (self = [super init]) {
        _childDeltaMap = [NSMapTable weakToStrongObjectsMapTable];
        _nodeView = [[RIAIDDecorViewWidget alloc] initWithIsBox:[self isKindOfClass:[KCADRenderBoxNode class]]
                                                     attributes:renderInfo
                                                       realView:[self getContentView]
                                                 actionDelegate:self];
    }
    return self;
}

#pragma mark - optional abstract api
// 需要子类进行重写，子类重写需要调用 super
- (void)bindData {
    if (self.renderInfo.attributes.button.highlightStateListArray.count > 0) {
        [self deliverHighlightAttributes:self.renderInfo.attributes.button.highlightStateListArray];
    }
    if ([self isKindOfClass:[KCADRenderVScrollNode class]]
        || ((self.renderInfo.hasHandler
             || self.highlightAttributes != nil)
             && ([self isKindOfClass:[KCADRenderTextNode class]]
                 || [self isKindOfClass:[KCADRenderImageNode class]]
                 || [self isKindOfClass:[KCADRenderButtonNode class]]
                 || [self isKindOfClass:[KCADRenderButtonNode class]]))) {
        self.nodeView.userInteractionEnabled = YES;
    } else {
        self.nodeView.userInteractionEnabled = NO;
    }
}

// 需要子类进行重写，需要调用 super
- (CGSize)measureWithEstimateWidth:(CGFloat)estimateWidth estimateHeight:(CGFloat)estimateHeight {
    self.isRoot = [self.nodeView isContainer];
    return CGSizeZero;
}

- (void)layout {
    self.frame = [KCADRenderGeometryCalculate transformPosition:self
                                                           size:self.size];
    self.nodeView.frame = self.frame;
}

- (void)beganRendingWithDecorView:(UIView *)decorView {
    [self drawBackGroundWithDecorView:decorView];
    [self drawSelfWithDecorView:decorView];
    [self drawForegroundWithDecorView:decorView];
    // 挂载 view key，供 UI 自动化测试使用
    [self.nodeView configViewKey:self.renderInfo.key];
}

- (void)drawBackGroundWithDecorView:(UIView *)decorView {
    // 设置背景
    [self.nodeView setupBackground];
    // 设置隐藏
    if (self.renderInfo.attributes.common.hasHidden) {
        BOOL hidden = self.renderInfo.attributes.common.hidden.value;
        [self.nodeView setViewHidden:hidden];
        for (KCADRenderNode *childNode in self.childList) {
            [childNode.nodeView setViewHidden:hidden];
        }
    }
}

- (void)drawSelfWithDecorView:(UIView *)decorView {
    [KCADRenderLayout addTargetView:self.nodeView
                        toDecorView:decorView
                  withAbsoluteFrame:self.frame];
    [self.nodeView setupShadow];
}

- (void)drawForegroundWithDecorView:(UIView *)decorView {}

/// 事件分发
- (BOOL)dispatchEvent:(NSString *)eventType
              keyList:(NSArray<NSNumber *> *)keyList
           attributes:(RIAIDAttributes *)attributes {
    if ([keyList containsObject:@(self.renderInfo.key)]) {
        return YES;
    }
    return NO;
}

#pragma mark - RIAIDWrapperViewInterface && RIAIDNodeDiffViewInfoInterface
- (void)setNewFrame:(CGRect)newFrame {
    _newFrame = newFrame;
}

- (void)setContext:(RIAIDRenderContext *)context {
    _context = context;
    self.nodeView.renderContext = context;
}

- (void)updateViewWithAlpha:(CGFloat)alpha frame:(CGRect)frame {
    self.renderInfo.attributes.common.alpha.value = alpha;
}

- (CGFloat)alpha {
    return self.renderInfo.attributes.common.hasAlpha ? self.renderInfo.attributes.common.alpha.value : 1;
}

- (UIView *)getRealView {
    return self.nodeView.getDecorView;
}

#pragma mark - private method
- (UIView *)getContentView { return nil; }

- (CGPoint)getChileNodeDeltaPoint:(KCADRenderNode *)childNode {
    NSValue *pointValue = [self.childDeltaMap objectForKey:childNode];
    return pointValue ? pointValue.CGPointValue : CGPointZero;
}

- (void)deliverHighlightAttributes:(NSArray<RIAIDButtonAttributes_HighlightState *> *)highlightStateArray {
    for (RIAIDButtonAttributes_HighlightState *highlightState in highlightStateArray) {
        if (highlightState.key == self.renderInfo.key) {
            RIAIDAttributes *highlightAttributes = [RIAIDRHighlightAttributesGenerator getNewAttributesWithOrigin:self.renderInfo.attributes
                                                                                                    diffAttributes:highlightState.attributes];
            self.highlightAttributes = highlightAttributes;
            break;
        }
    }
    for (KCADRenderNode *childNode in self.childList) {
        [childNode deliverHighlightAttributes:highlightStateArray];
    }
}

- (void)onPressStart:(BOOL)bySelf {}

- (void)onPressEnd:(BOOL)bySelf {}

@end
