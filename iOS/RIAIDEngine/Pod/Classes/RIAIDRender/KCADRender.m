//
//  KCADRender.m
//  KCADRender
//
//  Created by simon on 2021/11/9.
//

#import "KCADRender.h"

#pragma mark - parser
#import "KCADRenderParserContainer.h"

#pragma mark - render node && render model
#import "KCADRenderNode.h"
#import "KCADRenderItemNode.h"
#import "KCADRenderButtonNode.h"
#import "RIAIDRenderTouchView.h"

#pragma mark - utils
#import "RIAIDRenderArrayIterator.h"
#import "RIAIDPerformanceLog.h"
#import "RIAIDStatistics.h"

@interface KCADRender ()

@property (nonatomic, strong, readwrite) RIAIDRenderTouchView *decorView;
@property (nonatomic, copy) RIAIDNode *renderInfo;
@property (nonatomic, strong) RIAIDRenderContext *context;
@property (nonatomic, strong, readwrite) KCADRenderNode *rootNode;
@property (nonatomic, strong) RIAIDPerformanceLog *performanceLog;
@property (nonatomic, assign) CGSize estimateSize;

@end

@implementation KCADRender

+ (instancetype)createRenderWithView:(UIView *)injectView {
    KCADRender *render = [[KCADRender alloc] initWithRenderInfo:nil context:nil];
    render.decorView = injectView;
    return render;
}

- (instancetype)initWithRenderInfo:(RIAIDNode *)renderInfo context:(nullable RIAIDRenderContext *)context {
    if (self = [super init]) {
        _renderInfo = [renderInfo copy];
        _context = context;
        _decorView = [RIAIDRenderTouchView new];
        _performanceLog = [RIAIDPerformanceLog new];
    }
    return self;
}

+ (CGSize)sizeWithRenderInfo:(RIAIDNode *)renderInfo
               estimateWidth:(CGFloat)estimateWidth
              estimateHeight:(CGFloat)estimateHeight
                     context:(nullable RIAIDRenderContext *)context {
    KCADRender *render = [[KCADRender alloc] initWithRenderInfo:renderInfo context:context];
    KCADRenderNode *rootNode = [render parseNodeInfo:renderInfo];
    [render loadAttributesWithRootNode:rootNode];
    [render measureWithEstimateSize:CGSizeMake(estimateWidth, estimateHeight) rootNode:rootNode];
    [render layoutWithRenderNode:rootNode];
    return rootNode.size;
}

// 开始渲染
- (void)renderWithEstimateWidth:(CGFloat)estimateWidth estimateHeight:(CGFloat)estimateHeight {
    KCADRenderNode *rootNode = [self parseNodeInfo:self.renderInfo];
    if (nil == rootNode) {
        return;
    }
    self.estimateSize = CGSizeMake(estimateWidth, estimateHeight);
    if (self.decorView.subviews) {
        for (UIView *view in self.decorView.subviews) {
            [view removeFromSuperview];
        }
    }
    [self loadAttributesWithRootNode:rootNode];
    [self measureWithEstimateSize:CGSizeMake(estimateWidth, estimateHeight) rootNode:rootNode];
    [self layoutWithRenderNode:rootNode];
    [self drawWithRootNode:rootNode decorView:self.decorView];
    // 外部创建的 render，没有 node 信息，不可以重置其尺寸
    self.decorView.frame = (CGRect){CGPointZero, rootNode.size};
    self.rootNode = rootNode;
}

// 当 Render 的数据发生变化时，更新 Render 的内部布局
- (void)layoutIfNeeded {
    [self measureWithEstimateSize:self.estimateSize
                         rootNode:self.rootNode];
    [self layoutWithRenderNode:self.rootNode];
    [self drawWithRootNode:self.rootNode decorView:self.decorView];
}

// 根据新的预估尺寸，更改布局
- (void)reRenderWithEstimateSize:(CGSize)estimateSize {
    self.estimateSize = estimateSize;
    [self layoutIfNeeded];
    if (nil != self.rootNode) {
        // 外部创建的 render，没有 node 信息，不可以重置其尺寸
        self.decorView.frame = (CGRect){CGPointZero, self.rootNode.size};
    }
}

- (RIAIDDecorViewWidget *)findViewByKey:(NSInteger)renderKey {
    KCADRenderNode *targetRender  = [self findRenderNodeByKey:renderKey];
    return targetRender.nodeView;
}

- (KCADRenderNode *)findRenderNodeByKey:(NSInteger)renderKey {
    KCADRenderNode *node = self.rootNode;
    NSMutableArray <KCADRenderNode *> *nodeArray = [NSMutableArray arrayWithArray:node.childList];
    if ([node isKindOfClass:[KCADRenderButtonNode class]]) {
        [nodeArray addObject:((KCADRenderButtonNode *)node).contentRender];
    }
    RIAIDRenderArrayIterator *arrayIterator = [[RIAIDRenderArrayIterator alloc] initWithArray:nodeArray.copy
                                                                                      context:self.context];
    while (node) {
        if (renderKey == node.renderInfo.key) {
            return node;
        }
        KCADRenderNode *tempNode = arrayIterator.next;
        if (nil == tempNode
            || [tempNode isKindOfClass:[KCADRenderNode class]]) {
            node = tempNode;
        }
    }
    return nil;
}

- (NSArray<KCADRenderNode *> *)diffWithRender:(RIAIDNode *)newNodeInfo estimateSize:(CGSize)estimateSize {
    // 通过 nodeInfo 创建一个新的 RenderNode，并计算出来每个节点的尺寸和位置
    KCADRenderNode *newNode = [self parseNodeInfo:newNodeInfo];
    [self loadAttributesWithRootNode:newNode];
    [self measureWithEstimateSize:estimateSize rootNode:newNode];
    [self layoutWithRenderNode:newNode];
    if (!newNode) {
        return nil;
    }
    
    NSMutableArray<KCADRenderNode *> *diffNodeArray = [NSMutableArray array];
    RIAIDRenderArrayIterator *newNodeIterator = [[RIAIDRenderArrayIterator alloc] initWithArray:newNode.childList
                                                                                        context:self.context];
    KCADRenderNode *rootNode = self.rootNode;
    RIAIDRenderArrayIterator *rootNodeIterator = [[RIAIDRenderArrayIterator alloc] initWithArray:rootNode.childList
                                                                                         context:self.context];
    // 遍历新旧节点，将所有 key > 0 的视图放入两个字典中
    NSMutableDictionary *rootNodeMap = [NSMutableDictionary dictionary];
    NSMutableDictionary *newNodeMap = [NSMutableDictionary dictionary];
    while (rootNode) {
        if (rootNode.renderInfo.key > 0) {
            [rootNodeMap setValue:rootNode forKey:[NSString stringWithFormat:@"%d", rootNode.renderInfo.key]];
        }
        KCADRenderNode *tempNode = rootNodeIterator.next;
        if (nil == tempNode
            || [tempNode isKindOfClass:[KCADRenderNode class]]) {
            rootNode = tempNode;
        }
    }
    while (newNode) {
        if (newNode.renderInfo.key > 0) {
            [newNodeMap setValue:newNode forKey:[NSString stringWithFormat:@"%d", newNode.renderInfo.key]];
        }
        KCADRenderNode *tempNode = newNodeIterator.next;
        if (nil == tempNode
            || [tempNode isKindOfClass:[KCADRenderNode class]]) {
            newNode = tempNode;
        }
    }
    for (NSString *keyString in rootNodeMap.allKeys) {
        KCADRenderNode *rootRenderNode = rootNodeMap[keyString];
        KCADRenderNode *newRenderNode = newNodeMap[keyString];
        if (newRenderNode) {
            // 判断两个节点是否存在真实视图，若一个存在，一个不存在，则树也是不相等的
            BOOL rootNodeViewNotNull = [rootRenderNode.nodeView getDecorView];
            BOOL newNodeViewNotNull = [newRenderNode.nodeView getDecorView];
            if (rootNodeViewNotNull != newNodeViewNotNull) {
                return nil;
            }
            BOOL changed = [self haveChangeRender:rootRenderNode newNode:newRenderNode];
            if (changed) {
                rootRenderNode.newAlpha = newRenderNode.alpha;
                rootRenderNode.newFrame = newRenderNode.frame;
                [diffNodeArray addObject:rootRenderNode];
            }
        }
    }
    
    return diffNodeArray.copy;
}

#pragma mark - private method
// 根据 NodeInfo 解析出根节点
- (KCADRenderNode *)parseNodeInfo:(RIAIDNode *)nodeInfo {
    [self.performanceLog startRecordWithEvent:RIAIDStandardRenderBuildDuration];
    KCADRenderParserContainer *parserContainer = [KCADRenderParserContainer new];
    KCADRenderNode *rootNode = [parserContainer parseAdRenderInfo:nodeInfo context:self.context];
    CGFloat parserTime = [self.performanceLog endRecordWithEvent:RIAIDStandardRenderBuildDuration];
    [RIAIDStatistics addEventWithKey:RIAIDStandardRenderBuildDuration
                               value:[NSString stringWithFormat:@"%.f", parserTime]
                             context:self.context];
    return rootNode;
}

// 挂载 rootNode 的属性
- (void)loadAttributesWithRootNode:(KCADRenderNode *)rootNode {
    [self.performanceLog startRecordWithEvent:RIAIDStandardRenderLoadAttributesLayoutDuration];
    [rootNode bindData];
    CGFloat bindDataTime = [self.performanceLog endRecordWithEvent:RIAIDStandardRenderLoadAttributesLayoutDuration];
    [RIAIDStatistics addEventWithKey:RIAIDStandardRenderLoadAttributesLayoutDuration
                               value:[NSString stringWithFormat:@"%.f", bindDataTime]
                             context:self.context];
}

// 计算 rootNode 的尺寸
- (CGSize)measureWithEstimateSize:(CGSize)estimateSize rootNode:(KCADRenderNode *)rootNode {
    [self.performanceLog startRecordWithEvent:RIAIDStandardRenderMeasureDuration];
    CGFloat width = rootNode.renderInfo.layout.width > 0
                    ? MIN(rootNode.renderInfo.layout.width, estimateSize.width)
                    : estimateSize.width;
    CGFloat height = rootNode.renderInfo.layout.height > 0
                     ? MIN(rootNode.renderInfo.layout.height, estimateSize.height)
                     : estimateSize.height;
    rootNode.size = [rootNode measureWithEstimateWidth:width estimateHeight:height];
    CGFloat measureTime = [self.performanceLog endRecordWithEvent:RIAIDStandardRenderMeasureDuration];
    [RIAIDStatistics addEventWithKey:RIAIDStandardRenderMeasureDuration
                               value:[NSString stringWithFormat:@"%.f", measureTime]
                             context:self.context];
    return rootNode.size;
}

// 对 rootNode 进行排列，进行子节点的布局
- (CGSize)layoutWithRenderNode:(KCADRenderNode *)rootNode {
    [self.performanceLog startRecordWithEvent:RIAIDStandardRenderLayoutDuration];
    [rootNode layout];
    CGFloat layoutTime = [self.performanceLog endRecordWithEvent:RIAIDStandardRenderLayoutDuration];
    [RIAIDStatistics addEventWithKey:RIAIDStandardRenderLayoutDuration
                               value:[NSString stringWithFormat:@"%.f", layoutTime]
                             context:self.context];
    return rootNode.size;
}

// 绘制制定的 rootNode
- (void)drawWithRootNode:(KCADRenderNode *)rootNode decorView:(UIView *)decorView {
    [self.performanceLog startRecordWithEvent:RIAIDStandardRenderDrawDuration];
    [rootNode beganRendingWithDecorView:decorView];
    CGFloat drawTime = [self.performanceLog endRecordWithEvent:RIAIDStandardRenderDrawDuration];
    [RIAIDStatistics addEventWithKey:RIAIDStandardRenderDrawDuration
                               value:[NSString stringWithFormat:@"%.f", drawTime]
                             context:self.context];
}

- (BOOL)haveChangeRender:(KCADRenderNode *)rootNode
                 newNode:(KCADRenderNode *)newNode {
    BOOL alphaChanged = rootNode.alpha != newNode.alpha;
    BOOL positionChanged = !CGRectEqualToRect(rootNode.frame, newNode.frame);
    BOOL sizeChanged = !CGSizeEqualToSize(rootNode.size, newNode.size);
    return alphaChanged || positionChanged || sizeChanged;
}

@end
