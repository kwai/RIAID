//
//  KCADRenderBoxParser.m
//  KCADRender
//
//  Created by simon on 2021/11/10.
//

#import "KCADRenderBoxParser.h"

#import "KCADRenderParserContainer.h"
#import "KCADRenderBoxNode.h"

@implementation KCADRenderBoxParser

- (KCADRenderNode *)parseWithRenderInfo:(RIAIDNode *)renderInfo
                           nodeDelegate:(id<KCADRenderNodeDelegate>)delegate
                                context:(RIAIDRenderContext *)context {
    KCADRenderNode *renderNode = [super parseWithRenderInfo:renderInfo nodeDelegate:delegate context:context];
    KCADRenderBoxNode *boxNode;
    if ([renderNode isKindOfClass:[KCADRenderBoxNode class]]) {
        boxNode = (KCADRenderBoxNode *)renderNode;
        if (renderInfo.childrenArray_Count > 0) {
            NSArray *childNodeArray = [KCADRenderParserContainer.new parseChildNodeInfo:renderInfo.childrenArray
                                                                                context:context];
            if (childNodeArray.count > 0) {
                [boxNode addNodeArray:childNodeArray];
            }
        }
    }
    return boxNode;
}

@end
