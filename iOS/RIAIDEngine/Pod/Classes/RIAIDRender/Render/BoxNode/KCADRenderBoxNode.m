//
//  KCADRenderBoxNode.m
//  KCADRender
//
//  Created by simon on 2021/11/11.
//

#import "KCADRenderBoxNode.h"

#import "UIView+DrawBackgroundAttributes.h"

@implementation KCADRenderBoxNode

@synthesize childList = _childList;
@synthesize childSortList = _childSortList;

- (instancetype)initWithRenderInfo:(RIAIDNode *)renderInfo {
    if (self = [super initWithRenderInfo:renderInfo]) {
        _childList = [NSMutableArray array];
    }
    return self;
}

- (void)addNode:(KCADRenderNode *)renderNode {
    if (renderNode) {
        renderNode.parentNode = self;
        [self _addAndSortWithNode:renderNode];
    }
}

- (void)addNodeArray:(NSArray<KCADRenderNode *> *)renderNodeArray {
    if (renderNodeArray
        && renderNodeArray.count > 0) {
        for (KCADRenderNode *node in renderNodeArray) {
            [self addNode:node];
        }
    }
}

- (BOOL)dispatchEvent:(NSString *)eventType
              keyList:(NSArray<NSNumber *> *)keyList
           attributes:(RIAIDAttributes *)attributes {
    BOOL result = [super dispatchEvent:eventType keyList:keyList attributes:attributes];
    for (KCADRenderNode *childNode in self.childList) {
        result |= [childNode dispatchEvent:eventType
                                   keyList:keyList
                                attributes:attributes];
    }
    return result;
}

#pragma mark - override
- (void)bindData {
    [super bindData];
    for (KCADRenderNode *node in self.childList) {
        [node bindData];
    }
}

- (void)drawSelfWithDecorView:(UIView *)decorView {
    [super drawSelfWithDecorView:decorView];
    for (KCADRenderNode *node in self.childList) {
        [node beganRendingWithDecorView:self.nodeView.getDecorView ?: decorView];
    }
}

- (void)onPressStart:(BOOL)bySelf {
    if (self.renderInfo.attributes.button.highlightStateListArray.count == 0
        && bySelf) {
        return;
    }
    [self refreshPressUI:self.highlightAttributes];
    for (KCADRenderNode *childNode in self.childList) {
        [childNode onPressStart:bySelf];
    }
}

- (void)onPressEnd:(BOOL)bySelf {
    if (self.renderInfo.attributes.button.highlightStateListArray.count == 0
        && bySelf) {
        return;
    }
    [self refreshPressUI:self.renderInfo.attributes];
    for (KCADRenderNode *childNode in self.childList) {
        [childNode onPressEnd:bySelf];
    }
}

- (void)refreshPressUI:(RIAIDAttributes *)attributes {
    if (attributes) {
        [self.nodeView setupBackground];
    }
}

#pragma mark - private method
// 根据节点优先级重排子节点数组的顺序，优先级高的节点放在前面
- (void)_addAndSortWithNode:(KCADRenderNode *)renderNode {
    [self.childList addObject:renderNode];
    NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"renderInfo.layout.priority"
                                                                     ascending:NO];
    self.childSortList = [[self.childList sortedArrayUsingDescriptors:[NSArray arrayWithObject:sortDescriptor]]
                          mutableCopy];
}

@end
