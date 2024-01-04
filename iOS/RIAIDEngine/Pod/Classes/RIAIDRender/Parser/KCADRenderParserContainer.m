//
//  KCADRenderParserContainer.m
//  KCADRender
//
//  Created by simon on 2021/11/12.
//

#import "KCADRenderParserContainer.h"

#pragma mark - models
#import "KCADRenderKBase.h"
#import "RIAID.h"

#pragma mark - parser
#import "KCADRenderParser.h"
#import "KCADRenderBoxParser.h"
#import "KCADRenderAbsoluteBoxParser.h"
#import "KCADRenderButtonBoxParser.h"
#import "KCADRenderHorizontalBoxParser.h"
#import "KCADRenderHScrollBoxParser.h"
#import "KCADRenderSquareBoxParser.h"
#import "KCADRenderVerticalBoxParser.h"
#import "KCADRenderVScrollBoxParser.h"
#import "KCADRenderItemParser.h"
#import "KCADRenderImageItemParser.h"
#import "KCADRenderSpaceItemParser.h"
#import "KCADRenderTextItemParser.h"
#import "RIAIDRenderLottieParser.h"
#import "KCADRenderVideoParser.h"

@interface KCADRenderParserContainer ()<KCADRenderNodeDelegate>

@end

@implementation KCADRenderParserContainer

- (nullable KCADRenderNode *)parseAdRenderInfo:(RIAIDNode *)renderInfo context:(RIAIDRenderContext *)context {
    if (renderInfo
        && renderInfo.classType != RIAIDNode_ClassType_ClassTypeUnknown) {
        return [self _parseAdRenderInfo:renderInfo context:context];
    }
    
    return nil;
    
}

- (NSArray<KCADRenderNode *> *)parseChildNodeInfo:(NSArray<RIAIDNode *> *)childRenderInfoArray
                                          context:(RIAIDRenderContext *)context {
    NSMutableArray<KCADRenderNode *> *childNodeArray = [NSMutableArray array];
    for (RIAIDNode *childNodeModel in childRenderInfoArray) {
        KCADRenderNode *renderNode = [self _parseAdRenderInfo:childNodeModel context:context];
        if (renderNode) {
            [childNodeArray addObject:renderNode];
        }
    }
    return childNodeArray;
}

#pragma mark - KCADRenderNodeDelegate
- (KCADRenderNode *)generateNodeWithRenderInfo:(RIAIDNode *)renderInfo context:(RIAIDRenderContext *)context {
    return [self _parseAdRenderInfo:renderInfo context:context];
}

#pragma mark - private method
- (NSDictionary *)_getParserMap {
    return @{
        @(RIAIDNode_ClassType_ClassTypeLayoutAbsolute): [KCADRenderAbsoluteBoxParser new],
        @(RIAIDNode_ClassType_ClassTypeLayoutButton): [KCADRenderButtonBoxParser new],
        @(RIAIDNode_ClassType_ClassTypeLayoutHorizontal): [KCADRenderHorizontalBoxParser new],
        @(RIAIDNode_ClassType_ClassTypeLayoutHScroll): [KCADRenderHScrollBoxParser new],
        @(RIAIDNode_ClassType_ClassTypeLayoutSquare): [KCADRenderSquareBoxParser new],
        @(RIAIDNode_ClassType_ClassTypeLayoutVertical): [KCADRenderVerticalBoxParser new],
        @(RIAIDNode_ClassType_ClassTypeLayoutVScroll): [KCADRenderVScrollBoxParser new],
        @(RIAIDNode_ClassType_ClassTypeItemImage): [KCADRenderImageItemParser new],
        @(RIAIDNode_ClassType_ClassTypeItemSpace): [KCADRenderSpaceItemParser new],
        @(RIAIDNode_ClassType_ClassTypeItemText): [KCADRenderTextItemParser new],
        @(RIAIDNode_ClassType_ClassTypeItemLottie): [RIAIDRenderLottieParser new],
        @(RIAIDNode_ClassType_ClassTypeItemVideo): [KCADRenderVideoParser new],
    };
}

- (nullable KCADRenderNode *)_parseAdRenderInfo:(RIAIDNode *)renderInfo context:(RIAIDRenderContext *)context {
    KCADRenderNode *renderNode = nil;
    if (renderInfo
        && renderInfo.classType != RIAIDNode_ClassType_ClassTypeUnknown) {
        KCADRenderParser *parser = self._getParserMap[@(renderInfo.classType)];
        renderNode = [parser parseWithRenderInfo:renderInfo
                                    nodeDelegate:self
                                         context:context];
    }
    return renderNode;
}


@end
